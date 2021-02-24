/**
 * FileIO.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
 */
package application.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.AppUtil;
import application.models.FileExtension;
import application.models.FileExtension.FileExtensionType;
import application.panels.ConfigPanel;

/**
 * Encapsulate all file/folder-related operations. Not final as needs to be
 * mocked.
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
	public void downloadAndConvertImage(String url, String destinationPath, String extension) throws Exception {
		Instant entireStart = Instant.now();
		File outputFile = new File(destinationPath);
		File tempFile = new File(ConfigPanel.TEMP_FOLDER + "/" + getRandomString(8) + "." + extension);

		URI uri = encodeUri(url);
		InputStream is;
		if (uri.getScheme().equals("file")) {
			LOGGER.debug("Treating {} as a local image url", uri);
			is = new FileInputStream(new File(uri.getAuthority() + uri.getPath()));
		} else {
			LOGGER.debug("Treating {} as a remote image url", uri);
			is = uri.toURL().openStream();
		}
		Instant readStart = Instant.now();
		BufferedImage image = ImageIO.read(is);
		LOGGER.debug("Read [{}ms]", Duration.between(readStart, Instant.now()).toMillis());
		if (image == null) {
			throw new IOException("Unable to read image from URL " + url + " - ensure it is of a supported type: "
					+ Arrays.asList(FileExtension.values()).stream() //
							.filter(f -> FileExtensionType.IMAGE.equals(f.getType())) //
							.map(f -> f.getExtension()) //
							.collect(Collectors.toList()));
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
	 * @param extension
	 * @throws IOException
	 *             Should reading from the URL or writing/converting go awry
	 */
	public void downloadAndSaveFile(String url, String destinationPath, String extension) throws Exception {
		Instant start = Instant.now();
		File tempFile = new File(ConfigPanel.TEMP_FOLDER + "/" + getRandomString(8) + "." + extension);

		URI uri = encodeUri(url);
		InputStream is;
		if (uri.getScheme().equals("file")) {
			LOGGER.debug("Treating {} as a local file", uri);
			is = new FileInputStream(new File(uri.getAuthority() + uri.getPath()));
		} else {
			LOGGER.debug("Treating {} as a remote url", uri);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

			if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
				is = conn.getInputStream();
			} else {
				StringBuilder sb = AppUtil.getErrorFromStream(conn.getErrorStream());
				// Bit hacky but works
				if (sb.toString().contains("1010")) {
					sb.append(
							" - The owner of this website has prevented access to this file based on your browser's signature");
				}
				throw new IOException(sb.toString());
			}
		}

		Instant copyStart = Instant.now();
		FileUtils.copyToFile(is, tempFile);
		is.close();
		LOGGER.debug("Copy [{}ms]", Duration.between(copyStart, Instant.now()).toMillis());

		File outputFile = new File(destinationPath);
		Instant moveStart = Instant.now();
		Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		LOGGER.debug("File '{}' renamed/moved to '{}' [{}ms]", url, destinationPath,
				Duration.between(moveStart, Instant.now()).toMillis());

		LOGGER.debug("Full process [{}ms]", Duration.between(start, Instant.now()).toMillis());
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

	private URI encodeUri(String url) throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
		String encodedUrl = url;
		if (url.contains("\\")) {
			encodedUrl = encodedUrl.replace('\\', '/');
			LOGGER.debug("URL contains '\\'; auto-converting to '/': '{}' -> '{}'", url, encodedUrl);
		}
		if (!url.matches("((http://)|(https://)|(file://)).*")) {
			throw new MalformedURLException("URL requires either http://, https:// or file:// schema: '" + url + "'");
		}
		if (url.contains(" ")) {
			encodedUrl = encodedUrl.replace(" ", "%20");
			LOGGER.debug("URL contains space(s); auto-replace with '%20': '{}' -> '{}'", url, encodedUrl);
		}
		return new URI(encodedUrl);
	}
}
