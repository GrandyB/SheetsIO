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

import application.models.CellWrapper;
import lombok.Getter;

/**
 * Bean equivalent of the [ { "cell": "C4", "file": "file.txt" } ] individual
 * objects.
 *
 * @author Mark "Grandy" Bishop
 */
public final class Cell {
	@Getter
	@NotBlank(message = "Mapping \"name\" must have a value")
	/** Name to give the cell, eventually used in the file name. */
	private String name;

	@Getter
	@NotBlank(message = "Mapping \"cell\" must have a value")
	/** Cell reference on the Google Sheet, e.g. "A4". */
	private String cell;

	@Getter
	@NotBlank(message = "Mapping \"fileType\" must have a value")
	/** Eventually to be converted to a {@link FileType}. */
	private String fileType;

	/**
	 * Constructor.
	 *
	 * @param name
	 *            The identifier of the cell
	 * @param cell
	 *            The physical coordinate
	 * @param fileType
	 *            The raw version of the fileType
	 */
	public Cell(String name, String cell, String fileType) {
		this.name = name;
		this.cell = cell;
		this.fileType = fileType;
	}

	/**
	 * @return {@link CellWrapper}, built from this {@link Cell} for convenience.
	 */
	public CellWrapper getMutatedData() {
		return new CellWrapper(this);
	}

}
