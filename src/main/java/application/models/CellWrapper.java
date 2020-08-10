/**
 * CellData.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.models;

import application.exceptions.IllegalFileExtensionException;
import application.models.json.Cell;
import application.models.json.ICell;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Class for converting and storing an excel cell reference in terms of rows and
 * columns.
 * 
 * e.g. A1 = 0,0 / B3 = 1,2 / AA45 = 26,44 etc
 *
 * @author Mark "Grandy" Bishop
 */
@ToString
@EqualsAndHashCode(of = { "col", "row" })
@RequiredArgsConstructor
public final class CellWrapper implements ICell {

	/** 0-indexed column number. */
	@Getter
	private final int col;

	/** 0-indexed row number. */
	@Getter
	private final int row;

	/** The coord reference itself, e.g. 'A4'. Only used for debug purposes. */
	@Getter
	private final String coordString;

	@Getter
	private FileExtension fileExtension;

	private final Cell cell;

	public CellWrapper(Cell cell) throws IllegalFileExtensionException {
		this.cell = cell;
		this.coordString = cell.getCell();
		this.col = toColumnNumber(this.coordString);
		this.row = toRowNumber(this.coordString);

		// If an extension is not provided, use default
		if (cell.getFileExtension() == null) {
			fileExtension = FileExtension.defaultType();
		} else {
			fileExtension = new FileExtension(cell.getFileExtension());
		}
	}

	/**
	 * Create a CellWrapper just from the Google-provided row/col values; we don't
	 * need to know an alphabetical reference as the system only cares about row/col
	 * in the end.
	 * 
	 * @param col
	 *            The column id (zero-indexed)
	 * @param row
	 *            The row id (zero-indexed)
	 * @return a {@link CellWrapper}
	 */
	public static CellWrapper fromGoogleCoord(int col, int row) {
		return new CellWrapper(col, row, "N/A", null);
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
	/* @see application.models.json.ICell#getName() */
	public String getName() {
		return cell.getName();
	}

	@Override
	/* @see application.models.json.ICell#getCell() */
	public String getCell() {
		return cell.getCell();
	}
}
