/**
 * CellUpdate.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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

import application.models.FileExtension.FileExtensionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Small model for an update to a value within a Google Sheet.
 *
 * @author Mark "Grandy" Bishop
 */
@AllArgsConstructor
public final class CellUpdate {
	@Getter
	private CellWrapper cellWrapper;
	@Getter
	private VersionedString newValue;

	/** @return whether this update is a file. */
	public boolean isForFile() {
		return getCellWrapper().getFileExtension().getType().equals(FileExtensionType.HTTP);
	}
}
