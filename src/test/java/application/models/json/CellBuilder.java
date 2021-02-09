/**
 * CellBuilder.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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

/**
 * Builder for a {@link Cell}.
 *
 * @author Mark "Grandy" Bishop
 */
public class CellBuilder {
	private String name;
	private String cell;
	private String fileExtension;
	private String pad;

	public Cell build() {
		return new Cell(name, cell, fileExtension, pad);
	}

	public CellBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public CellBuilder withCell(String cell) {
		this.cell = cell;
		return this;
	}

	public CellBuilder withFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
		return this;
	}

	public CellBuilder withPad(String pad) {
		this.pad = pad;
		return this;
	}
}
