/**
 * HttpServer.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package application.services.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import application.models.CellWrapper;
import application.models.FileExtension.FileExtensionType;
import application.models.PropertiesHolder;
import application.services.SheetCache;
import application.services.http.ConnectionRequest.ConnectionRequestType;
import application.threads.ThreadCollector;
import application.threads.UpdateRunnable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A singleton http server that handles/controls a local webserver.
 * 
 * For simplicity, we want to follow similar URL behaviour to local folder
 * behaviour - for instance, we want 'caster1Name.txt' (that gets saved in
 * '/files/project/caster1Name.txt') to be accessed from
 * 'http://server:port/project/caster1Name'.
 * 
 * To achieve realtime updating, we first construct a {@link ConnectionRequest}
 * to figure out what we're after, use the {@link SheetCache} to get the current
 * value, and serve a templated version of the value using
 * {@link HtmlResponseBuilder}. This template includes livejs, which is the
 * method by which we achieve the realtime updating.
 * 
 * Livejs works by sending HEAD requests to the server every second, comparing
 * 'etag' (basically a version number) of the existing page to the etag from the
 * server response. If they differ, livejs then sends a GET request and reloads
 * the page. For our use case, we use the .hashCode() of the value String to
 * determine whether the cache's value has changed.
 *
 * @author Mark "Grandy" Bishop
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpService implements HttpHandler {
	private static final Logger LOGGER = LogManager.getLogger(HttpService.class);

	private HttpServer server;
	private InetSocketAddress socket;

	private SheetCache sheetCache;

	private static HttpService INSTANCE;
	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

	/** @return the singleton instance of {@link HttpService}. */
	public static HttpService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HttpService();
			ThreadCollector.setHttpService(INSTANCE);
		}
		return INSTANCE;
	}

	/**
	 * Begin the {@link HttpService} and begin listening on the port from
	 * application.properties.
	 */
	public void start(SheetCache sheetCache) throws IOException {
		if (server != null) {
			LOGGER.info("Web server stopped");
			server.stop(0);
		}
		this.sheetCache = sheetCache;
		int port = Integer.parseInt(PropertiesHolder.get().getProperty(PropertiesHolder.HTTP_PORT));
		socket = new InetSocketAddress("localhost", port);
		server = HttpServer.create(socket, 0);

		server.createContext("/", this);

		server.setExecutor(EXECUTOR);
		server.start();
		LOGGER.info("Web server began listening on port {}", port);
	}

	/**
	 * Forcibly stop the {@link HttpService} AND thread pool; should only be called
	 * on application shutdown.
	 */
	public void stop() {
		EXECUTOR.shutdownNow();
		server.stop(0);
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String requestParamValue = httpExchange.getRequestURI().getPath();
		String reqMethod = httpExchange.getRequestMethod();
		LOGGER.trace("REQUEST: {} - {}", requestParamValue, reqMethod);

		Optional<ConnectionRequest> request = ConnectionRequest.from(httpExchange.getRequestURI());
		if (request.isPresent()) {
			// Request format is valid; e.g. '/project/asset' or '/project/asset.png'

			if (ConnectionRequestType.UPDATE.equals(request.get().getType())) {
				// We don't care whether it's a GET or HEAD or PUSH
				handleUpdateRequest(request.get());
				return;
			}

			switch (reqMethod) {
			case "GET":
				// Respond with initial page body
				handleGetRequest(request.get(), httpExchange);
				break;
			case "HEAD":
				// Respond to a request for updates from livejs
				handleHeadRequest(request.get(), httpExchange);
				break;
			default:
				// Ignore
			}
		}
	}

	/**
	 * HEAD requests are sent in from livejs on the webpages served up every second.
	 */
	private void handleHeadRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		LOGGER.trace("HEAD -> {}", req);
		if (ConnectionRequestType.HTML.equals(req.getType())) {
			// Look up value from SheetCache for the value of the cell
			CellWrapper cell = getCell(req);
			String cellValue = sheetCache.get(cell);

			/*
			 * Send an etag with the current value; livejs will compare it to its existing
			 * value and perform a GET request if it differs to what it already has.
			 */
			httpExchange.getResponseHeaders().add("etag", Integer.toString(cellValue.hashCode()));
			httpExchange.getResponseHeaders().add("content-type", "text/html");
			httpExchange.sendResponseHeaders(200, -1);
		} else {
			LOGGER.debug("Received an unexpected non-HTML HEAD request: {}", req);
		}
	}

	private void handleGetRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		switch (req.getType()) {
		case FILE:
			handleFileGetRequest(req, httpExchange);
			break;
		case HTML:
			handleHtmlGetRequest(req, httpExchange);
			break;
		case FAVICON:
			// Just ignore it...
			break;
		default:
			throw new IllegalArgumentException(
					"Unable to handle GET request with " + ConnectionRequestType.class.getName() + " " + req.getType());
		}
	}

	/**
	 * GET request - serving new things, in this case it's files on our system.
	 * 
	 * TODO: This should not be hit right now, unless people manually try to access
	 * e.g. "file.png" rather than "file". In the future, we may want to replace
	 * most remote assets (images, videos etc) with local references, so we download
	 * and serve locally - but not necessary for the moment.
	 */
	private void handleFileGetRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		LOGGER.info("GET file -> {}", req);
		LOGGER.debug(
				"Attempted to get a file, this is unusual as webpages are only currently serving remote files (asides from file:// sources for images/videos): {}",
				req);

		String path = System.getProperty("user.dir") + "/files" + req.getFullRequest();
		File file = new File(path);
		httpExchange.sendResponseHeaders(200, file.length());
		httpExchange.getResponseHeaders().add("content-type",
				Files.probeContentType(Paths.get(file.getAbsolutePath())));

		OutputStream outputStream = httpExchange.getResponseBody();
		Files.copy(file.toPath(), outputStream);
		outputStream.close();
	}

	/**
	 * GET request - non-file, so assume it's serving a cell value from the cache in
	 * its appropriate form.
	 */
	private void handleHtmlGetRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		LOGGER.info("GET html -> {}", req);
		OutputStream outputStream = httpExchange.getResponseBody();

		HtmlResponseBuilder templater = new HtmlResponseBuilder().empty();

		// Look up value from SheetCache for the value of the cell
		CellWrapper cell = getCell(req);
		// Cell could be null if we haven't hit 'update now' for the first time
		if (cell != null) {
			String cellValue = sheetCache.get(cell);
			LOGGER.debug("Cell value is '{}' with file extension '{}'", cellValue, cell.getFileExtension());

			// TODO: Use downloaded version of file rather than passing in remote url?
			switch (cell.getFileExtension().getType()) {
			case IMAGE:
				templater = templater.buildImgTemplate(cell, cellValue);
				break;
			case TEXT:
				templater = templater.buildDivTemplate(cellValue);
				break;
			case VIDEO:
				templater = templater.buildVideoTemplate(cell, cellValue);
				break;
			case HTTP:
				templater = templater.buildIframeTemplate(cellValue);
				break;
			default:
				throw new IllegalArgumentException("Unable to handle " + FileExtensionType.class.getName() + " "
						+ cell.getFileExtension().getType());
			}

			if (req.hasParam("noscale")) {
				templater = templater.scale(false);
			}
			if (req.hasParam("loop")) {
				templater = templater.loop(true);
			}
		}

		String builtHtmlResponse = templater.build();
		LOGGER.debug("Responding with html:\n{}", builtHtmlResponse);
		httpExchange.sendResponseHeaders(200, builtHtmlResponse.length());
		outputStream.write(builtHtmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * Handle (any kind of) request (GET/PUSH for example) to the /update route;
	 * should force the sheets update loop to run.
	 */
	public void handleUpdateRequest(ConnectionRequest request) {
		Optional<UpdateRunnable> updateLoop = ThreadCollector.getUpdateLoop();
		if (updateLoop.isPresent()) {
			LOGGER.debug("Requested an update call from {}", HttpService.class.getName());
			updateLoop.get().runOnce();
		} else {
			LOGGER.debug(
					"Attempted to perform an update using {}'s /update call, but there was no updateLoop available",
					HttpService.class.getName());
		}
	}

	/**
	 * @return {@link CellWrapper} from the cache, using the details from the
	 *         url/request.
	 */
	private CellWrapper getCell(ConnectionRequest req) {
		Optional<CellWrapper> cell = sheetCache.findByName(req.getAsset());
		if (cell.isPresent()) {
			return cell.get();
		} else if (req.isValid()) {
			// Warn if we were trying to retrieve something valid
			LOGGER.error("Attempted to get {} but found nothing in the {}", req, SheetCache.class.getSimpleName());
		}
		return null;
	}
}
