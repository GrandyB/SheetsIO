/**
 * FileUpdater.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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
package application;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.models.CellData;

/**
 * For all file-related operations, taking {@link CellData} and a String value,
 * and physically updating the files on disk.
 *
 * @author Mark "Grandy" Bishop
 */
public class FileUpdater {
	private static final Logger LOGGER = LogManager.getLogger(FileUpdater.class);

	public static final String FOLDER_PREFIX = "files";

	private FileIO fileIO;
	private String folderName;
	private File folder;

	/**
	 * Prime the FileUpdater with the folder we'll be using; create empty initial
	 * files.
	 * 
	 * @throws IOException
	 *             if folder cannot be made.
	 */
	public void setup(String projectName, ConfigHolder config) throws IOException {
		assert projectName != null : "projectName cannot be null";
		assert config != null : "config cannot be null";

		this.folderName = projectName;
		writeFolders();
		createInitialFiles(config);
	}

	/** Update all the files from the Map with their new values. */
	public void updateFiles(Map<CellData, String> updatedCells) throws IOException {
		for (Entry<CellData, String> entry : updatedCells.entrySet()) {
			String fileName = entry.getKey().getFileName();
			String newValue = entry.getValue();

			getFileIO().writeFile(createFilePath(this.folderName, fileName), newValue);
		}
	}

	/** Create folder for project if it doesn't exist. */
	private void writeFolders() throws IOException {
		String folderPath = createFolderPath(this.folderName);
		this.folder = getFileIO().createFolder(folderPath);
	}

	/**
	 * Creates empty files for all the cells we're interested in. Must be run after
	 * {@link #writeFolders()}
	 */
	private void createInitialFiles(ConfigHolder config) throws IOException {
		for (CellData data : config.getCells()) {
			getFileIO().writeFile(data.getFileName(), "");
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
		getFileIO().deleteFiles(this.folder);
	}

	/** @return the file path, using prefix and folder name. */
	protected String createFilePath(String folderName, String fileName) {
		return FOLDER_PREFIX + File.separator + folderName + File.separator + fileName;
	}

	/** @return the folder path, using prefix and separator. */
	protected String createFolderPath(String folderName) {
		return FOLDER_PREFIX + File.separator + folderName;
	}

	/**
	 * @return {@link FileIO} the instantiated FileIO. Protected and separate as to
	 *         more easily extend and test.
	 */
	protected FileIO getFileIO() {
		if (this.fileIO == null) {
			this.fileIO = new FileIO();
		}
		return this.fileIO;
	}
}
