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
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.GsonBuilder;

import application.exceptions.GoogleSheetsException;
import application.models.PropertiesHolder;
import application.models.json.GoogleSheetsResponse;

/**
 * Utility methods to perform common actions.
 *
 * @author Mark "Grandy" Bishop
 */
public class AppUtil {
	private static final AppUtil INSTANCE = new AppUtil();

	/** the format for the URL, needing spreadsheetId, worksheetName, and apiKey. */
	public static final String SPREADSHEET_URL_FORMAT = "https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s?key=%s&majorDimension=COLUMNS&valueRenderOption=FORMATTED_VALUE";

	/** @return the singleton instance of AppUtil. */
	public static AppUtil get() {
		return INSTANCE;
	}

	/** @return the string but stripped of the apiKey, for safety. */
	public String sanitiseApiKey(String str) {
		if (str == null) {
			return "";
		}

		String currentApiKey = PropertiesHolder.get().getProperty(PropertiesHolder.API_KEY);
		if (currentApiKey == null || currentApiKey.trim().isEmpty()) {
			return str;
		}

		return str.replace(currentApiKey, "YOUR_UNSANITISED_API_KEY_HERE");
	}

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
	public GoogleSheetsResponse getGoogleSheetsData(URL url) throws IOException, GoogleSheetsException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		InputStreamReader isr;
		if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
			return new GsonBuilder().create().fromJson(new InputStreamReader(conn.getInputStream()),
					GoogleSheetsResponse.class);
		} else {
			isr = new InputStreamReader(conn.getErrorStream());
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			throw GoogleSheetsException.fromJsonString(sb.toString());
		}
	}
}
