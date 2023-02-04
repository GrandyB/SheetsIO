/**
 * GoogleSheetsRepository.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Repository;

import com.google.gson.GsonBuilder;

import application.configuration.ApplicationProperties;
import application.exceptions.GoogleSheetsException;
import application.models.json.GoogleSheetsResponse;
import application.utils.AppUtil;

/**
 * Responsible for API calls to Google Sheets, retrieving data.
 *
 * @author Mark "Grandy" Bishop
 */
@Repository
public class GoogleSheetsRepository extends AbstractRepository {

	/** the format for the URL, needing spreadsheetId, worksheetName, and apiKey. */
	public static final String SPREADSHEET_URL_FORMAT = "https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s?key=%s&majorDimension=COLUMNS&valueRenderOption=FORMATTED_VALUE";

	/**
	 * Create a connection to the Google Sheets v4 API using the given {@link URL}.
	 * 
	 * @return a {@link GoogleSheetsResponse} representation of the Google Sheet
	 *         data.
	 * @throws IOException
	 *             if the connection to Google Sheets or converting to
	 *             {@link GoogleSheetsResponse} fails
	 * @throws GoogleSheetsException
	 */
	public GoogleSheetsResponse getGoogleSheetsData(String url) throws IOException, GoogleSheetsException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
			return new GsonBuilder().create().fromJson(new InputStreamReader(conn.getInputStream()),
					GoogleSheetsResponse.class);
		} else {
			StringBuilder sb = AppUtil.getMessageFromStream(conn.getErrorStream());
			throw GoogleSheetsException.fromJsonString(url, sb.toString());
		}
	}

	/**
	 * Attempt a connection to the test spreadsheet specified in the
	 * {@link ApplicationProperties}.
	 * 
	 * @return true if it connects, false if not
	 */
	public boolean testConnection() {
		// Get the "sample"/test URL and try out a connection
		String url = String.format(SPREADSHEET_URL_FORMAT, getAppProps().getTestSpreadsheetID(),
				getAppProps().getTestWorkbookID(), getAppProps().getApiKey());
		try {
			getGoogleSheetsData(url);
			return true;
		} catch (GoogleSheetsException | IOException e) {
			return false;
		}
	}
}
