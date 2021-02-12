/**
 * MyHttpHandler.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Responds to requests to view anything within the mini httpserver.
 * 
 * TODO: Currently just sends a different url each time livejs sends a HEAD
 * request for an update, which is every second.
 *
 * @author Mark "Grandy" Bishop
 */
public class MyHttpHandler implements HttpHandler {
	private boolean toggle;
	private String[] items = { "https://obs.ninja",
			"https://docs.oracle.com/en/java/javase/11/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpExchange.html#sendResponseHeaders(int,long)" };
	private static int i;

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String requestParamValue = null;
		String reqMethod = httpExchange.getRequestMethod();
		switch (reqMethod) {
		case "GET":
			// Respond with initial page body
			handleResponse(httpExchange, requestParamValue);
		case "HEAD":
			// Respond to a request for updates from livejs
			handleHeadRequest(httpExchange);
		default:
			// Ignore
		}

	}

	private void handleHeadRequest(HttpExchange httpExchange) throws IOException {
		httpExchange.getResponseHeaders().add("etag", Integer.toString(i++));
		httpExchange.getResponseHeaders().add("content-type", "text/html");
		httpExchange.sendResponseHeaders(200, -1);
	}

	// TODO: send actual item
	private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
		OutputStream outputStream = httpExchange.getResponseBody();

		String htmlResponse = "<html>" //
				+ "	<head>" //
				+ "		<script type=\"text/javascript\" src=\"http://livejs.com/live.js\"></script>" //
				+ "		<style>body { margin: 0; padding: 0; } </style>" //
				+ "	</head>" //
				+ "	<body>" //
				+ "		<iframe src=\"" + items[toggle ? 1 : 0] + "\" width=\"100%\" height=\"100%\" frameborder=\"0\">" //
				+ "		</iframe>" //
				+ "	</body>" //
				+ "</html>";
		toggle = !toggle;

		httpExchange.sendResponseHeaders(200, htmlResponse.length());
		outputStream.write(htmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
	}
}