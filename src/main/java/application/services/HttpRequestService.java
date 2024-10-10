package application.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import application.exceptions.UnableToLoadRemoteURLException;
import application.utils.AppUtil;

@Component
public class HttpRequestService extends AbstractService {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestService.class);

	/**
	 * Acquire the result of a GET request to the given URL.
	 * 
	 * @param url
	 *            where to request
	 * @return Optional<InputStream> from a successful request; empty (and
	 *         logging) otherwise
	 * @throws IOException
	 *             if opening the request fails
	 */
	public Optional<InputStream> get(String url) throws IOException {
		return get(url, 1);
	}

	private Optional<InputStream> get(String url, int attemptCount) throws IOException {
		if (attemptCount >= 5) {
			// Maximum # of redirects
			return Optional.empty();
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		// Provide a User-Agent, without it, many sites block incoming requests
		// with 403
		conn.addRequestProperty("User-Agent", "SheetsIO");
		conn.setInstanceFollowRedirects(true);
		InputStream is;
		int responseCode = conn.getResponseCode();
		if (responseCode >= 200 && responseCode < 300) {
			// Successful response
			return Optional.of(conn.getInputStream());
		} else if (conn.getResponseCode() >= 300 && responseCode < 400) {
			if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				String newUrl = conn.getHeaderField("Location");
				LOGGER.debug("Attempt to GET '{}', received {} redirect to '{}', moving from attempt {} to attempt {}",
						url, responseCode, newUrl, attemptCount, (attemptCount + 1));
				return get(newUrl, attemptCount++);
			} else {
				return Optional.empty();
			}
		} else if (responseCode >= 500) {
			// Don't break the flow of the application, log it
			return Optional.empty();
		} else {
			StringBuilder sb = AppUtil.getMessageFromStream(conn.getErrorStream());
			// Bit hacky but works
			if (sb.toString().contains("1010")) {
				sb.append(
						" - The owner of this website has prevented access to this file based on your browser's signature");
			}
			throw new UnableToLoadRemoteURLException(sb.toString());
		}
		return Optional.empty();
	}

	private void debugLog(HttpURLConnection conn) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		LOGGER.debug(response.toString());
	}
}
