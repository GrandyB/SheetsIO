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

import application.models.PropertiesHolder;

/**
 * 
 *
 * @author Mark "Grandy" Bishop
 */
public class AppUtil {

	/** @return the string but stripped of the apiKey, for safety. */
	public static String sanitiseApiKey(String str) {
		if (str == null) {
			return "";
		}

		return str.replace(PropertiesHolder.get().getProperty(PropertiesHolder.API_KEY),
				"YOUR_UNSANITISED_API_KEY_HERE");
	}

	/**
	 * The formation of a spreadsheet URL, for use in String.format();
	 * 
	 * @return the format for the URL, needing spreadsheetId, worksheetName, and
	 *         apiKey.
	 */
	public static String getSpreadsheetUrlFormat() {
		return "https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s?key=%s&majorDimension=COLUMNS&valueRenderOption=FORMATTED_VALUE";
	}
}
