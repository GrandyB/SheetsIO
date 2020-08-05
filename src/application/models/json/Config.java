/**
 * Config.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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

import applications.models.CellData;
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

	@NotEmpty(message = "\"cells\" must be provided have values")
	private final CellMapping[] cells;

	/**
	 * @return the {@link CellMapping}[] array muted into an {@link ArrayList} of
	 *         {@link CellData}, for convenience.
	 */
	public List<CellData> getMutatedMappings() {
		List<CellData> mappings = new ArrayList<>();
		Arrays.asList(cells).forEach(cell -> {
			mappings.add(cell.getMutatedData());
		});
		return mappings;
	}
}
