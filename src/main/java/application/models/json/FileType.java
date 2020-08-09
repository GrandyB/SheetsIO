/**
 * FileType.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Model for config json file mapping (id to extension).
 *
 * @author Mark "Grandy" Bishop
 */
@AllArgsConstructor
public final class FileType {
	@Getter
	@NotBlank(message = "Mapping \"id\" must have a value")
	private String id;

	@Getter
	@NotBlank(message = "Mapping \"extension\" must have a value")
	private String extension;
}
