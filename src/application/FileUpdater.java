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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import application.models.CellData;

/**
 * For all file-related operations, taking {@link CellData} and a String value,
 * and physically updating the files on disk.
 *
 * @author Mark "Grandy" Bishop
 */
public class FileUpdater {
	private static final String PREFIX = "files";

	private String folderName;
	private File folder;

	/**
	 * Prime the FileUpdater with the folder we'll be using.
	 * 
	 * @throws IOException
	 *             if folder cannot be made.
	 */
	public void setup(String projectName, ConfigHolder config) throws IOException {
		this.folderName = projectName;
		writeFolders();
		createInitialFiles(config);
	}

	public void updateFiles(Map<CellData, String> updatedCells) throws IOException {
		for (Entry<CellData, String> entry : updatedCells.entrySet()) {
			String fileName = entry.getKey().getFileName();
			String newValue = entry.getValue();

			writeFile(fileName, newValue);
		}
	}

	private void writeFile(String fileName, String newValue) throws IOException {
		FileWriter myWriter = new FileWriter(PREFIX + File.separator + this.folderName + File.separator + fileName);
		myWriter.write(newValue);
		myWriter.close();
	}

	private void writeFolders() throws IOException {
		// Create folder if it doesn't exist
		String path = PREFIX + File.separator + this.folderName;
		this.folder = new File(path);
		this.folder.mkdirs();
		if (this.folder.exists()) {
			System.out.println("Folder prepped: " + path);
		} else {
			throw new IOException("Unable to create folder for text file output: " + path);
		}
	}

	/**
	 * Creates empty files for all the cells we're interested in. Must be run after
	 * {@link #writeFolders()}
	 */
	private void createInitialFiles(ConfigHolder config) throws IOException {
		for (CellData data : config.getCells()) {
			writeFile(data.getFileName(), "");
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
			System.out.println("No files to clean up; no project folder existing");
			return;
		}
		System.out.println(String.format("Cleaning project folder '%s'", this.folder.getAbsolutePath()));
		if (!deleteFiles(this.folder)) {
			throw new IOException("Unable to delete project files for folder: " + this.folder.getAbsolutePath());
		}
	}

	private boolean deleteFiles(File dirForDelete) {
		if (dirForDelete == null) {
			return false;
		}
		File[] allContents = dirForDelete.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteFiles(file);
			}
		}
		System.out.println("\tDeleting " + dirForDelete.getAbsolutePath());
		return dirForDelete.delete();
	}
}
