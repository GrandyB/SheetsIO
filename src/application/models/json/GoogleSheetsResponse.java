/**
 * GoogleSheetsReponse.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.models.json;

import java.util.HashMap;
import java.util.Map;

import applications.models.CellData;

/**
 * Bean represenatation of json object received back from the google
 * spreadsheets api v4.
 *
 * @author Mark "Grandy" Bishop
 */
public class GoogleSheetsResponse {
	@SuppressWarnings("unused")
	private String range;
	@SuppressWarnings("unused")
	private String majorDimension;
	private String[][] values;

	/**
	 * @return a {@link HashMap} of {@link CellData} (coordinate + file) to
	 *         {@link String} 'actual' values from the spreadsheet.
	 */
	public Map<CellData, String> getMutatedRowColumnData() {
		Map<CellData, String> vals = new HashMap<>();

		// Stored as an array of columns, containing an array (row values)
		// Contains empty arrays where a column has no data in its rows
		// Contains empty inner array values where there's empty cells within the column

		for (int col = 0; col < values.length; col++) {
			// Loop through columns, A to ZZ (or however many)
			for (int row = 0; row < values[col].length; row++) {
				// Loop through the rows within the column
				vals.put(new CellData(col, row), values[col][row]);
			}
		}

		return vals;
	}
}
