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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import application.models.CellWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bean equivalent of expected json config file.
 *
 * @author Mark "Grandy" Bishop
 */
@AllArgsConstructor
public final class Config {
	@Getter
	@NotBlank(message = "Please provide a non-blank \"projectName\"")
	private final String projectName;

	@Getter
	@NotBlank(message = "Please provide a non-blank \"apiKey\"")
	private final String apiKey;

	@Getter
	@NotBlank(message = "Please provide a non-blank \"spreadsheetId\"")
	private final String spreadsheetId;

	@Getter
	@NotBlank(message = "Please provide a non-blank \"worksheetName\"")
	private final String worksheetName;

	@NotEmpty(message = "\"cells\" must be provided and have values")
	private final Cell[] cells;

	@NotEmpty(message = "\"fileTypes\" must be provided and have values")
	private final FileType[] fileTypes;

	/**
	 * @return the {@link Cell}[] array muted into an {@link ArrayList} of
	 *         {@link CellWrapper}, for convenience.
	 */
	public List<CellWrapper> getMutatedMappings() {
		List<CellWrapper> mappings = new ArrayList<>();
		Arrays.asList(cells).forEach(cell -> {
			mappings.add(cell.getMutatedData());
		});
		return mappings;
	}

	/**
	 * @return the {@link FileType}[] array muted into an {@link ArrayList} of
	 *         {@link FileType}, for convenience.
	 */
	public List<FileType> getFileTypes() {
		List<FileType> types = new ArrayList<>();
		types.addAll(Arrays.asList(fileTypes));
		return types;
	}

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
