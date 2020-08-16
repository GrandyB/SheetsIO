/**
 * Config.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Bean equivalent of expected json config file.
 *
 * @author Mark "Grandy" Bishop
 */
@ToString
@AllArgsConstructor
public class Config {
	@Getter
	@NotBlank(message = "\"projectName\" must be provided and non-blank")
	private final String projectName;

	@Getter
	@NotBlank(message = "\"spreadsheetId\" must be provided and non-blank")
	private final String spreadsheetId;

	@Getter
	@NotBlank(message = "\"worksheetName\" must be provided and non-blank")
	private final String worksheetName;

	@Valid
	@NotEmpty(message = "\"cells\" array must be provided and have values")
	private final Cell[] cells;

	/**
	 * @return the {@link Cell}[] array muted into an {@link ArrayList} of
	 *         {@link Cell}, for convenience.
	 */
	public List<Cell> getCells() {
		List<Cell> cells = new ArrayList<>();
		cells.addAll(Arrays.asList(this.cells));
		return cells;
	}
}
