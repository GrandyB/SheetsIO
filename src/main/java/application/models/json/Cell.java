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
import lombok.RequiredArgsConstructor;

/**
 * Bean equivalent of the [ { "cell": "C4", "file": "file.txt" } ] individual
 * objects.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public final class Cell implements ICell {
	@Getter
	@NotBlank(message = "Cells must have a \"name\" to be identified by")
	/** Name to give the cell, eventually used in the file name. */
	private final String name;

	@Getter
	@NotBlank(message = "Cells must have a \"cell\" reference")
	private final String cell;

	@Getter
	@NotBlank(message = "Cells must have a \"fileExtension\" defined")
	/** Eventually to be converted to a {@link FileType}. */
	private final String fileExtension;

	@Getter
	/**
	 * If txt type, optionally provide a 'pad' to say how many spaces should be
	 * appended onto the end.
	 */
	private String pad;
}
