/**
 * FileUpdateRepository.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import application.models.CellWrapper;
import application.models.FileExtension;
import application.panels.ConfigPanel;

/**
 * Repository responsible for reading and writing output files.
 *
 * @author Mark "Grandy" Bishop
 */
@Repository
public class FileUpdateRepository {
	private Logger LOGGER = LogManager.getLogger(FileUpdateRepository.class);
	public static final String FOLDER_PREFIX = "files";

	/**
	 * Saves a value to the file path.
	 * 
	 * @throws IOException
	 *             if there's an error during writing to file
	 */
	public void writeTextFile(String filePath, String newValue) throws IOException {
		FileWriterWithEncoding myWriter = new FileWriterWithEncoding(filePath, "UTF-8");
		myWriter.write(newValue);
		myWriter.close();
	}

	/**
	 * Write an image to a destination.
	 *
	 * @param inputStream
	 *            an {@link InputStream} containing the file to be written
	 * @param destinationPath
	 *            The complete path we wish to save the image to
	 * @param extension
	 *            The file extension for the image (e.g. "png")
	 * @throws Exception
	 */
	public void writeImage(InputStream inputStream, String destinationPath, String extension) throws Exception {
		Instant entireStart = Instant.now();

		File outputFile = new File(destinationPath);

		File tempFile = writeImageToTempFolder(inputStream, extension);
		moveTempFileToOutput(tempFile, outputFile);

		LOGGER.debug("Image stored to '{}' [{}ms]", tempFile.getPath(),
				Duration.between(entireStart, Instant.now()).toMillis());
	}

	private File writeImageToTempFolder(InputStream inputStream, String extension) throws IOException {
		File tempFile = new File(ConfigPanel.TEMP_FOLDER + "/" + getRandomString(8) + "." + extension);

		Instant readStart = Instant.now();
		BufferedImage image = ImageIO.read(inputStream);
		LOGGER.debug("Read [{}ms]", Duration.between(readStart, Instant.now()).toMillis());
		if (image == null) {
			throw new IOException("Unable to read image");
		}

		OutputStream outputStream = new FileOutputStream(tempFile);
		Instant writeStart = Instant.now();
		ImageIO.write(image, extension, outputStream);
		LOGGER.debug("Write [{}ms]", Duration.between(writeStart, Instant.now()).toMillis());
		outputStream.close();
		inputStream.close();

		return tempFile;
	}

	/** Move a file in the /temp folder into its end resting point. */
	private void moveTempFileToOutput(File tempFile, File outputFile) throws IOException {
		Instant moveStart = Instant.now();
		Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		LOGGER.debug("Image renamed/moved to '{}' [{}ms]\n----------", outputFile.getAbsolutePath(),
				Duration.between(moveStart, Instant.now()).toMillis());
	}

	/**
	 * Writes a transparent image to the given file path.
	 * 
	 * @param destFilePath
	 *            file path to write to
	 * @param ext
	 *            the image extension (e.g. ".png")
	 */
	public void saveTransparentImage(String destFilePath, String ext) throws Exception {
		writeImage(createEmptyImage(ext), destFilePath, ext);
	}

	private ByteArrayInputStream createEmptyImage(String extension) throws Exception {
		// Create 50x50 so it's moveable in OBS more easily
		BufferedImage result = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		int color = (0 << 24) | (0 << 16) | (0 << 8) | 0; // ARGB format
		for (int x = 0; x < 50; x++) {
			for (int y = 0; y < 50; y++) {
				result.setRGB(x, y, color);
			}
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(result, extension, os);
		return new ByteArrayInputStream(os.toByteArray());
	}

	/**
	 * Creates a folder from a path.
	 * 
	 * @param path
	 *            Where the folder should be
	 * @return File the directory that was created
	 * @throws IOException
	 *             should there be an issue creating the folder
	 */
	public File createFolder(String folderPath) throws IOException {
		File folder = new File(folderPath);
		folder.mkdirs();
		if (folder.exists()) {
			LOGGER.debug("Folder prepped: " + folderPath);
			return folder;
		} else {
			throw new IOException("Unable to create folder: " + folderPath);
		}
	}

	/**
	 * Delete all the files within the given folder name in /files/.
	 * 
	 * @param folderName
	 *            Name of the folder
	 * @throws IOException
	 *             if deletion fails or folder acquisition fails
	 */
	public void cleanExistingFolderIfExists(String folderName) throws IOException {
		String folderPath = createFolderPath(folderName);
		File folder = new File(folderPath);
		if (folder.exists()) {
			deleteFilesRecursive(folder);
		}
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

	/**
	 * Recursively delete the give File (folder) and everything within it (all the
	 * way down).
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

	private String getRandomString(int len) {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < len) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}
}
