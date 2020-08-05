/**
 * CellData.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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
package applications.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Class for converting and storing an excel cell reference in terms of rows and
 * columns.
 * 
 * e.g. A1 = 0,0 / B3 = 1,2 / AA45 = 26,44 etc
 *
 * @author Mark "Grandy" Bishop
 */
@EqualsAndHashCode(of = { "col", "row" })
public final class CellData {

	/** 0-indexed column number. */
	@Getter
	private final int col;

	/** 0-indexed row number. */
	@Getter
	private final int row;

	/** The coord itself. Only used for debug purposes. */
	@Getter
	private final String coordString;

	@Getter
	private final String fileName;

	public CellData(String coord, String file) {
		this.coordString = coord;
		this.col = toColumnNumber(coord);
		this.row = toRowNumber(coord);
		this.fileName = file;
	}

	/**
	 * Constructor, only used when we aren't converting from an excel-style coord.
	 */
	public CellData(int col, int row) {
		this.col = col;
		this.row = row;
		this.coordString = "N/A";
		this.fileName = "N/A";
	}

	private static int toRowNumber(String coord) {
		return Integer.parseInt(coord.replaceAll("\\D+", "")) - 1;
	}

	// https://codereview.stackexchange.com/questions/44545/excel-column-string-to-row-number-and-vice-versa
	private static int toColumnNumber(String excelValue) {
		String justStr = excelValue.replaceAll("[^a-zA-Z]", "");
		int number = 0;
		for (int i = 0; i < justStr.length(); i++) {
			number = number * 26 + (justStr.charAt(i) - ('A' - 1));
		}
		return number - 1;
	}

	@Override
	public String toString() {
		return "CellData [col=" + col + ", row=" + row + ", coordString=" + coordString + ", fileName=" + fileName
				+ "]";
	}
}
