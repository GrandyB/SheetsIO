/**
 * FileUpdater.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.services;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.exceptions.IllegalFileExtensionException;
import application.models.CellWrapper;
import application.models.ConfigHolder;
import application.models.FileExtension;
import application.models.FileExtension.FileExtensionType;
import lombok.RequiredArgsConstructor;

/**
 * For all file-related operations, taking {@link CellWrapper} and a String
 * value, and physically updating the files on disk.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public class FileUpdater {
	private static final Logger LOGGER = LogManager.getLogger(FileUpdater.class);

	public static final String FOLDER_PREFIX = "files";

	private final FileIO fileIO;
	private File folder;

	/**
	 * Prime the FileUpdater with the folder we'll be using; create empty initial
	 * files.
	 * 
	 * @throws IOException
	 *             if folder cannot be made.
	 * @throws IllegalFileExtensionException
	 */
	public void setup() throws IOException, IllegalFileExtensionException {
		assert ConfigHolder.get().getProjectName() != null : "projectName cannot be null";

		cleanExistingFolderIfExists();
		writeFolders();
		createInitialFiles();
	}

	/** Update all the files from the Map with their new values. */
	public void updateFiles(Map<CellWrapper, String> updatedCells) throws IOException {
		for (Entry<CellWrapper, String> entry : updatedCells.entrySet()) {
			CellWrapper cellWrapper = entry.getKey();
			String newValue = entry.getValue();

			String destFilePath = createFilePath(ConfigHolder.get().getProjectName(), cellWrapper);
			FileExtension ext = cellWrapper.getFileExtension();
			switch (ext.getType()) {
			case TEXT:
				/** Add padding if applicable (@see {@link CellWrapper#getPadding}). */
				fileIO.writeTextFile(destFilePath, newValue + cellWrapper.getPadding());
				break;
			case IMAGE:
				fileIO.downloadAndConvertImage(newValue, destFilePath, ext.getExtension());
				break;
			case FILE:
				fileIO.downloadAndSaveFile(newValue, destFilePath, ext.getExtension());
				break;
			default:
				throw new IllegalStateException(
						"Unable to handle " + FileExtensionType.class.getSimpleName() + ": " + ext.getType());
			}
		}
	}

	/** Create folder for project if it doesn't exist. */
	private void writeFolders() throws IOException {
		String folderPath = createFolderPath(ConfigHolder.get().getProjectName());
		this.folder = fileIO.createFolder(folderPath);
	}

	/**
	 * Creates empty files for all the cells we're interested in. Must be run after
	 * {@link #writeFolders()}
	 * 
	 * @throws IllegalFileExtensionException
	 *             if
	 */
	private void createInitialFiles() throws IOException, IllegalFileExtensionException {
		for (CellWrapper cellWrapper : ConfigHolder.get().getCells()) {
			fileIO.writeTextFile(createFilePath(ConfigHolder.get().getProjectName(), cellWrapper), "");
		}
	}

	private void cleanExistingFolderIfExists() throws IOException {
		String folderPath = createFolderPath(ConfigHolder.get().getProjectName());
		File folder = new File(folderPath);
		if (folder.exists()) {
			this.folder = folder;
			this.cleanUp();
		}
	}

	/**
	 * Perform a general cleanup of the files dir; if you change configs, it'll
	 * remove the files from the existing config.
	 * 
	 * @throws IOException
	 *             if it cannot delete the folder/files
	 */
	public void cleanUp() throws IOException {
		if (this.folder == null) {
			LOGGER.debug("No files to clean up; no project folder existing");
			return;
		}
		LOGGER.debug("Cleaning project folder '{}'", this.folder.getAbsolutePath());
		fileIO.deleteFiles(this.folder);
	}

	/**
	 * @param folderName
	 *            the name of the project/folder
	 * @param cell
	 *            the {@link CellWrapper}, for its name and {@link FileExtension}
	 * @return the file path, using prefix and folder name.
	 */
	protected String createFilePath(String folderName, CellWrapper cell) {
		return FOLDER_PREFIX + File.separator + folderName + File.separator + cell.getName() + "."
				+ cell.getFileExtension().getExtension();
	}

	/**
	 * @param folderName
	 *            the name of the project/folder
	 * @return the folder path, using prefix and separator.
	 */
	protected String createFolderPath(String folderName) {
		return FOLDER_PREFIX + File.separator + folderName;
	}
}
