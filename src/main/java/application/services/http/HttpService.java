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
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import application.models.CellWrapper;
import application.models.FileExtension;
import application.models.FileExtension.FileExtensionType;
import application.models.PropertiesHolder;
import application.services.SheetCache;
import application.services.ThreadCollector;
import application.services.http.ConnectionRequest.ConnectionRequestType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A singleton http server that handles/controls a local webserver.
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

	public void start(SheetCache sheetCache) throws IOException {
		if (server != null) {
			server.stop(0);
		}
		this.sheetCache = sheetCache;
		int port = Integer.parseInt(PropertiesHolder.get().getProperty(PropertiesHolder.HTTP_PORT));
		socket = new InetSocketAddress("localhost", port);
		server = HttpServer.create(socket, 0);

		server.createContext("/", this);

		server.setExecutor(EXECUTOR);
		server.start();
		LOGGER.info("Web server started on port {}", port);
	}

	public void stop() {
		EXECUTOR.shutdownNow();
		server.stop(0);
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String requestParamValue = httpExchange.getRequestURI().getPath();
		String reqMethod = httpExchange.getRequestMethod();
		LOGGER.info("REQUEST: {} - {}", requestParamValue, reqMethod);

		Optional<ConnectionRequest> request = ConnectionRequest.from(httpExchange.getRequestURI());
		if (request.isPresent()) {
			// Request format is valid; e.g. '/project/asset' or '/project/asset.png'
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

	private void handleHeadRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		if (ConnectionRequestType.HTML.equals(req.getType())) {
			// HttpBinding potential = HttpBinding.from(httpExchange.getLocalAddress(),
			// req);

			// Look up value from SheetCache for the value of the cell
			CellWrapper cell = getCell(req);
			String cellValue = sheetCache.get(cell);

			// Compare hash to that of the request's etag
			if (Integer.toString(cellValue.hashCode()) != httpExchange.getRequestHeaders().getFirst("etag")) {
				httpExchange.getResponseHeaders().add("etag", Integer.toString(cellValue.hashCode()));
				httpExchange.getResponseHeaders().add("content-type", "text/html");
			}
			httpExchange.sendResponseHeaders(200, -1);
		} else {
			LOGGER.debug("Received an unexpected HEAD request: {}", req);
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
		default:
			throw new IllegalArgumentException(
					"Unable to handle GET request with " + ConnectionRequestType.class.getName() + " " + req.getType());
		}
	}

	/** GET request - serving new things, in this case it's files on our system. */
	private void handleFileGetRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		File file = new File(System.getProperty("user.dir") + "/files" + req.getFullRequest());
		httpExchange.sendResponseHeaders(200, file.length());

		CellWrapper cell = getCell(req);
		FileExtension ext = cell.getFileExtension();
		httpExchange.getResponseHeaders().add("content-type", ext.getContentType());

		// TODO: Serve local files rather than remote ones
		LOGGER.warn("Attempted to get a file, shouldn't need to yet: {}", req);

		OutputStream outputStream = httpExchange.getResponseBody();
		Files.copy(file.toPath(), outputStream);
		outputStream.close();
	}

	/**
	 * GET request - non-file, so assume it's serving a cell value in its
	 * appropriate form.
	 */
	private void handleHtmlGetRequest(ConnectionRequest req, HttpExchange httpExchange) throws IOException {
		OutputStream outputStream = httpExchange.getResponseBody();
		LOGGER.info("GET html -> {}", req);

		HtmlResponseBuilder templater = new HtmlResponseBuilder().empty();

		// Look up value from SheetCache for the value of the cell
		CellWrapper cell = getCell(req);
		// Cell could be null if we haven't hit 'update now' for the first time
		if (cell != null) {
			String cellValue = sheetCache.get(cell);

			// TODO: Use downloaded version of file rather than passing in remote url?
			switch (cell.getFileExtension().getType()) {
			case IMAGE:
				templater = templater.buildImgTemplate(cellValue);
				break;
			case TEXT:
				templater = templater.buildDivTemplate(cellValue);
				break;
			case VIDEO:
				templater = templater.buildVideoTemplate(cellValue, cell.getFileExtension().getContentType());
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

			// httpExchange.getResponseHeaders().add("content-type",
			// cell.getFileExtension().getContentType());
			httpExchange.getResponseHeaders().add("etag", Integer.toString(cellValue.hashCode()));
		}
		String builtHtmlResponse = templater.build();
		httpExchange.sendResponseHeaders(200, builtHtmlResponse.length());
		outputStream.write(builtHtmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
	}

	private CellWrapper getCell(ConnectionRequest req) {
		Optional<CellWrapper> cell = sheetCache.findByName(req.getAsset());
		if (cell.isPresent()) {
			return cell.get();
		} else {
			LOGGER.warn("Attempted to get {} but found nothing in the {}", req, SheetCache.class.getSimpleName());
			return null;
		}
	}
}
