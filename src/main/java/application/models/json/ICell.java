/**
 * ICell.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
 * Interface for common data actions for a {@link Cell}.
 *
 * @author Mark "Grandy" Bishop
 */
public interface ICell {
	/**
	 * @return the identifying 'name' of the cell, eventually used as the file name.
	 */
	String getName();

	/** @return Cell reference on the Google Sheet, e.g. "A4". */
	String getCell();

	/** @return the 'fileExtension' (if provided, default 'txt'). */
	String getFileExtension();
}
