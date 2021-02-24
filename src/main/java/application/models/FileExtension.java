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

import application.exceptions.IllegalFileExtensionException;
import lombok.Getter;
import lombok.ToString;

/**
 * Type of extension, based on preset lists of acceptable extensions per type.
 *
 * @author Mark "Grandy" Bishop
 */
@ToString
public enum FileExtension {
	TXT(FileExtensionType.TEXT, "text/plain"),
	PNG(FileExtensionType.IMAGE, "image/png"),
	JPG(FileExtensionType.IMAGE, "image/jpeg"),
	JPEG(FileExtensionType.IMAGE, "image/jpeg"),
	GIF(FileExtensionType.IMAGE, "image/gif"),
	BMP(FileExtensionType.IMAGE, "image/bmp"),
	WEBM(FileExtensionType.VIDEO, "video/webm"),
	MP4(FileExtensionType.VIDEO, "video/mp4"),
	HTML(FileExtensionType.HTTP, "text/html");

	@Getter
	private String extension;
	@Getter
	private String contentType;
	@Getter
	private FileExtensionType type;

	private FileExtension(FileExtensionType type, String contentType) {
		this.extension = this.name().toLowerCase();
		;
		this.type = type;
		this.contentType = contentType;
	}

	public static FileExtension fromRaw(String rawExtension) throws IllegalFileExtensionException {
		if (rawExtension == null) {
			return defaultType();
		} else {
			for (FileExtension ext : values()) {
				if (ext.getExtension().equals(rawExtension)) {
					return ext;
				}
			}
			throw new IllegalFileExtensionException(
					"Unable to find " + FileExtension.class.getSimpleName() + " for extension: " + rawExtension);
		}
	}

	public static FileExtension defaultType() throws IllegalFileExtensionException {
		return FileExtension.TXT;
	}

	/**
	 * @return whether or not this type is for a file. Used for working out if we
	 *         should be creating initial files/considering this cell for file
	 *         updating.
	 */
	public boolean isForFile() {
		return !type.equals(FileExtensionType.HTTP);
	}

	public enum FileExtensionType {
		TEXT, IMAGE, VIDEO, HTTP
	}
}
