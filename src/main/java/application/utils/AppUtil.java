/**
 * AppUtil.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility methods to perform common actions.
 * 
 * TODO: Move some of these into FileIO
 *
 * @author Mark "Grandy" Bishop
 */
public class AppUtil {
	private static final Logger LOGGER = LogManager.getLogger(AppUtil.class);

	/** @return the string but stripped of the apiKey, for safety. */
	public static String sanitiseApiKey(String currentApiKey, String str) {
		if (str == null) {
			return "";
		}

		if (currentApiKey == null || currentApiKey.trim().isEmpty()) {
			return str;
		}

		return str.replace(currentApiKey, "YOUR_UNSANITISED_API_KEY_HERE");
	}

	/**
	 * @return a safe, escaped {@link URI} for use in {@link FileIO} when
	 *         downloading files.
	 */
	public static URI encodeForUrl(String url)
			throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
		String encodedUrl = url;

		if (!url.matches("((http://)|(https://)|(file://)).*")) {
			throw new MalformedURLException(String.format("Attempted to get an image from url: '%s'.\n"
					+ "The URL was invalid - requires either http://, https:// or file:// prefix", url));
		}

		return new URI(encodeUrlContent(encodedUrl));
	}

	/** @return a safe, escaped version of the url for use in queries. */
	public static String encodeUrlContent(String url) {
		String encodedUrl = url;
		encodedUrl = replaceCharInUrl("\\", "/", encodedUrl, url);
		encodedUrl = replaceCharInUrl(" ", "%20", encodedUrl, url);
		encodedUrl = replaceCharInUrl("-", "%2D", encodedUrl, url);
		encodedUrl = replaceCharInUrl(".", "%2E", encodedUrl, url);
		return encodedUrl;
	}

	private static String replaceCharInUrl(String c, String replacement, String encodedUrl, String url) {
		String newUrl = encodedUrl;
		if (url.contains(c)) {
			newUrl = encodedUrl.replace(c, replacement);
			LOGGER.trace("URL contains \"{}\"; auto-replace with '{}': '{}' -> '{}'", c, replacement, url, encodedUrl);
		}
		return newUrl;
	}

	public static StringBuilder getMessageFromStream(InputStream stream) throws IOException {
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		return sb;
	}
}
