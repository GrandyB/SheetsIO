/**
 * CellMapping.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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

import applications.models.CellData;

/**
 * Bean equivalent of the [ { "cell": "C4", "file": "file.txt" } ] individual
 * objects.
 *
 * @author Mark "Grandy" Bishop
 */
public final class CellMapping {
	/** Native/mostly useless direct mappings, for validation purposes. */
	@NotBlank(message = "Mapping \"cell\" must have a value")
	private String cell;
	@NotBlank(message = "Mapping \"file\" must have a value")
	private String file;

	/**
	 * @return {@link CellData}, built from this {@link CellMapping} for
	 *         convenience.
	 */
	public CellData getMutatedData() {
		return new CellData(cell, file);
	}
}
