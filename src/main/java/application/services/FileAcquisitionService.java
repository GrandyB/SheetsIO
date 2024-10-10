/**
 * FileAcquisitionService.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import application.exceptions.UnableToLoadRemoteURLException;
import application.utils.AppUtil;

/**
 * Service responsible for downloading files from remote URLs or loading from
 * local drives.
 *
 * @author Mark "Grandy" Bishop
 */
@Service
public class FileAcquisitionService {
	private static final Logger LOGGER = LogManager.getLogger(FileAcquisitionService.class);

	/**
	 * @return {@link InputStream} for the file at the given URL.
	 * @throws {@link
	 *             MalformedURLException} if the URL is invalid.
	 * @throws {@link
	 *             IOException} if the InputStream couldn't be created.
	 * @throws {@link
	 *             UnableToLoadRemoteURLException} for access-specific errors.
	 */
	public InputStream downloadRemoteFile(String url)
			throws MalformedURLException, IOException, UnableToLoadRemoteURLException {
		LOGGER.debug("Treating '{}' as a remote url", url);
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		// Provide a User-Agent, without it, many sites block incoming requests
		// with 403
		conn.addRequestProperty("User-Agent", "SheetsIO");
		conn.setInstanceFollowRedirects(true);
		InputStream is;
		int responseCode = conn.getResponseCode();
		if (responseCode >= 200 && responseCode < 300) {
			is = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Print the response content
			System.out.println("Response Content: " + response.toString());
		} else if (conn.getResponseCode() >= 300 && responseCode < 400) {
			if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				String newUrl = conn.getHeaderField("Location");
				return downloadRemoteFile(newUrl);
			} else {
				return conn.getInputStream();
			}
		} else if (responseCode >= 500) {
			// Don't break the flow of the application, log it
			return conn.getInputStream();
		} else {
			StringBuilder sb = AppUtil.getMessageFromStream(conn.getErrorStream());
			// Bit hacky but works
			if (sb.toString().contains("1010")) {
				sb.append(
						" - The owner of this website has prevented access to this file based on your browser's signature");
			}
			throw new UnableToLoadRemoteURLException(sb.toString());
		}
		return is;
	}

	/**
	 * @param uri
	 *            The {@link URI} for local file://url we wish to acquire
	 * @return {@link InputStream} for the file at the given URL.
	 * @throws FileNotFoundException
	 *             If the local file was unable to be loaded
	 * @throws IllegalStateException
	 *             If the given URI isn't for a local file
	 * @see {@link AppUtil#encodeForUrl(String)};
	 */
	public InputStream acquireLocalFile(URI uri) throws FileNotFoundException {
		LOGGER.debug("Treating {} as a local file url", uri);
		if (uri.getScheme().equals("file")) {
			throw new IllegalStateException("Expected local file: " + uri);
		}
		return new FileInputStream(new File(uri.getAuthority() + uri.getPath()));
	}
}
