/**
 * FileExtensionType.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.util.Arrays;
import java.util.List;

import application.exceptions.IllegalFileExtensionException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Type of extension, based on preset lists of acceptable extensions per type.
 *
 * @author Mark "Grandy" Bishop
 */
public class FileExtension {
	public static final List<String> IMAGE_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg", "gif", "bmp");
	public static final List<String> TEXT_EXTENSIONS = Arrays.asList("txt", "json", "html");

	@Getter
	private String extension;
	@Getter
	private FileExtensionType type;

	public FileExtension(String extension) throws IllegalFileExtensionException {
		this.extension = extension;
		this.type = FileExtensionType.from(extension);
	}

	public static FileExtension defaultType() throws IllegalFileExtensionException {
		return new FileExtension("txt");
	}

	@AllArgsConstructor
	public enum FileExtensionType {
		TEXT(TEXT_EXTENSIONS), IMAGE(IMAGE_EXTENSIONS);

		@Getter
		private List<String> extensions;

		static FileExtensionType from(String extension) throws IllegalFileExtensionException {
			for (FileExtensionType type : FileExtensionType.values()) {
				if (type.getExtensions().contains(extension)) {
					return type;
				}
			}
			throw new IllegalFileExtensionException(
					"Unable to find " + FileExtensionType.class.getSimpleName() + " for extension: " + extension);
		}
	}
}
