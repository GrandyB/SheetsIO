package application.services.http;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

public class HttpServiceTest {

	@Test
	public void test_faviconRequestsCompleteWithoutContent() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", HttpService.getInstance());
		server.start();
		try {
			HttpClient client = HttpClient.newHttpClient();
			String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
			for (int i = 0; i < 6; i++) {
				String path = i == 0 ? "/favicon.ico" : "/favicon.ico?source=" + i;
				HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
						.timeout(Duration.ofSeconds(5)).build();
				HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
				Assertions.assertEquals(204, response.statusCode());
				Assertions.assertEquals(0, response.body().length);
			}
		} finally {
			server.stop(0);
		}
	}
}
