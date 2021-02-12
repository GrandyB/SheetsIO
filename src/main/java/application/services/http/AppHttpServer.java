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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpServer;

/**
 * AppHttpServer that handles/controls a local webserver.
 *
 * @author Mark "Grandy" Bishop
 */
public class AppHttpServer {
	private static final Logger LOGGER = LogManager.getLogger(AppHttpServer.class);

	private HttpServer server;
	private InetSocketAddress socket;

	public void start() throws IOException {
		socket = new InetSocketAddress("localhost", 8001);
		server = HttpServer.create(socket, 0);

		server.createContext("/", new MyHttpHandler());

		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		server.setExecutor(threadPoolExecutor);
		server.start();
		LOGGER.info("Server started on port 8001");
	}
}
