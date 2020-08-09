/**
 * CellMapping.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import javax.validation.constraints.NotBlank;

import lombok.Getter;

/**
 * Bean equivalent of the [ { "cell": "C4", "file": "file.txt" } ] individual
 * objects.
 *
 * @author Mark "Grandy" Bishop
 */
public final class Cell {
	private static final String DEFAULT_EXTENSION = "txt";
	@Getter
	@NotBlank(message = "Cells must have a \"name\" to be identified by")
	/** Name to give the cell, eventually used in the file name. */
	private String name;

	@Getter
	@NotBlank(message = "Cells must have a \"cell\" reference")
	/** Cell reference on the Google Sheet, e.g. "A4". */
	private String cell;

	@NotBlank(message = "Cells must have a \"fileExtension\" defined")
	/** Eventually to be converted to a {@link FileType}. */
	private String fileExtension;

	/**
	 * Constructor.
	 *
	 * @param name
	 *            The identifier of the cell
	 * @param cell
	 *            The physical coordinate
	 * @param filExtension
	 *            The extension to save the file with
	 */
	public Cell(String name, String cell, String fileExtension) {
		this.name = name;
		this.cell = cell;
		this.fileExtension = fileExtension;
	}

	/**
	 * Constructor, no file extension given, use default.
	 *
	 * @param name
	 *            The identifier of the cell
	 * @param cell
	 *            The physical coordinate
	 */
	public Cell(String name, String cell) {
		this.name = name;
		this.cell = cell;
		this.fileExtension = DEFAULT_EXTENSION;
	}

	/** @return the fileExtension, or the DEFAULT (txt) if null. */
	public String getFileExtension() {
		return this.fileExtension != null ? this.fileExtension : DEFAULT_EXTENSION;
	}
}
