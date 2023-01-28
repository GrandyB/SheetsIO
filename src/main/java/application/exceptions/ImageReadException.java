/**
 * UnableToReadURLException.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.exceptions;

import java.util.Arrays;
import java.util.stream.Collectors;

import application.models.FileExtension;
import application.models.FileExtension.FileExtensionType;

/**
 * Checked exception when ImageIO fails to read an image.
 *
 * @author Mark "Grandy" Bishop
 */
public class ImageReadException extends Exception {
	private static final long serialVersionUID = 1L;

	public ImageReadException(String message) {
		super(wrapMessage(message));
	}

	public ImageReadException(String message, Exception e) {
		super(wrapMessage(message), e);
	}

	private static String wrapMessage(String message) {
		return String.format("%s\nEnsure it is of a valid type: [%s]", message,
				String.join(", ", Arrays.asList(FileExtension.values()).stream() //
						.filter(f -> FileExtensionType.IMAGE.equals(f.getType())) //
						.map(f -> f.getExtension()) //
						.collect(Collectors.toList())));
	}
}
