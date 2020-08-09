/**
 * FileIO.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
 */
package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encapsulate all file/folder-related operations.
 *
 * @author Mark "Grandy" Bishop
 */
public class FileIO {
	private static final Logger LOGGER = LogManager.getLogger(FileIO.class);

	/**
	 * Creates a folder from a path.
	 * 
	 * @param path
	 *            Where the folder should be
	 * @return File the directory that was created
	 * @throws IOException
	 *             should there be an issue creating the folder
	 */
	public File createFolder(String path) throws IOException {
		File folder = new File(path);
		folder.mkdirs();
		if (folder.exists()) {
			LOGGER.debug("Folder prepped: " + path);
			return folder;
		} else {
			throw new IOException("Unable to create folder: " + path);
		}
	}

	/**
	 * Saves a value to the file path.
	 * 
	 * @throws IOException
	 *             if there's an error during writing to file
	 */
	public void writeFile(String filePath, String newValue) throws IOException {
		FileWriter myWriter = new FileWriter(filePath);
		myWriter.write(newValue);
		myWriter.close();
	}

	/**
	 * Recursively delete the give File (folder) and everything within it.
	 * 
	 * @throws IOException
	 *             should deletion fail
	 */
	public void deleteFiles(File dirForDelete) throws IOException {
		if (!deleteFilesRecursive(dirForDelete)) {
			throw new IOException("Unable to delete files for folder: " + dirForDelete.getAbsolutePath());
		}
	}

	private boolean deleteFilesRecursive(File dirForDelete) {
		if (dirForDelete == null) {
			return false;
		}
		File[] allContents = dirForDelete.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteFilesRecursive(file);
			}
		}
		LOGGER.debug("Deleting {}", dirForDelete.getAbsolutePath());
		return dirForDelete.delete();
	}
}
