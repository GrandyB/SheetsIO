/**
 * FileIO.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
 */
package application.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.models.FileExtension;
import application.panels.ConfigPanel;

/**
 * Encapsulate all file/folder-related operations.
 *
 * @author Mark "Grandy" Bishop
 */
public class FileIO {
	private static final Logger LOGGER = LogManager.getLogger(FileIO.class);
	/** Max time (ms) allowed to connect to a site to download its file. */
	private static final int CONNECTION_TIMEOUT = 2000;
	/** Max time (ms) allowed to read/download the file. */
	private static final int READ_TIMEOUT = 20000;

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
	 * Saves a value to the file path.
	 * 
	 * @throws IOException
	 *             if there's an error during writing to file
	 */
	public void writeTextFile(String filePath, String newValue) throws IOException {
		FileWriter myWriter = new FileWriter(filePath);
		myWriter.write(newValue);
		myWriter.close();
	}

	/**
	 * Looks up URL for an image, takes its data, converts it to a file of given
	 * type.
	 * 
	 * @param url
	 *            A full URL, e.g. https://i.imgur.com/jcYxcS4.png
	 * @param destinationPath
	 *            The file path including extension
	 * @param extension
	 *            The extension/type to convert to
	 * @throws IOException
	 *             Should reading from the URL or writing/converting go awry
	 */
	public void downloadAndConvertImage(String url, String destinationPath, String extension) throws IOException {
		Instant entireStart = Instant.now();
		File outputFile = new File(destinationPath);
		File tempFile = new File(ConfigPanel.TEMP_FOLDER + "/" + getRandomString(8) + "." + extension);

		InputStream is = new URL(url).openStream();
		Instant readStart = Instant.now();
		BufferedImage image = ImageIO.read(is);
		LOGGER.debug("Read [{}ms]", Duration.between(readStart, Instant.now()).toMillis());
		if (image == null) {
			throw new IOException("Unable to read image from URL " + url + " - ensure it is of a supported type: "
					+ Arrays.toString(FileExtension.IMAGE_EXTENSIONS.toArray()));
		}
		OutputStream os = new FileOutputStream(tempFile);
		Instant writeStart = Instant.now();
		ImageIO.write(image, extension, os);
		LOGGER.debug("Write [{}ms]", Duration.between(writeStart, Instant.now()).toMillis());
		os.close();
		is.close();
		LOGGER.debug("Image '{}' downloaded and stored to '{}' [{}ms]", url, tempFile.getPath(),
				Duration.between(entireStart, Instant.now()).toMillis());

		Instant moveStart = Instant.now();
		Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		LOGGER.debug("Image '{}' renamed/moved to '{}' [{}ms]", url, destinationPath,
				Duration.between(moveStart, Instant.now()).toMillis());
	}

	/**
	 * Downloads file from the url and saves as destinationPath.
	 * 
	 * @param url
	 *            A full URL, e.g.
	 *            http://dl5.webmfiles.org/big-buck-bunny_trailer.webm
	 * @param destinationPath
	 *            The file path including extension
	 * @throws IOException
	 *             Should reading from the URL or writing/converting go awry
	 */
	public void downloadAndSaveFile(String url, String destinationPath) throws IOException {
		Instant start = Instant.now();
		File outputFile = new File(destinationPath);
		FileUtils.copyURLToFile(new URL(url), outputFile, CONNECTION_TIMEOUT, READ_TIMEOUT);
		Instant end = Instant.now();

		LOGGER.debug("File '{}' downloaded and stored to '{}' [{}ms] (max allowed DL time: {}ms)", url, destinationPath,
				Duration.between(start, end).toMillis(), READ_TIMEOUT);
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
