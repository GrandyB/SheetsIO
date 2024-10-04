/**
 * ConfigurationFileRepository.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.exceptions.JsonValidationException;
import application.models.CellWrapper;
import application.models.ConfigurationFile;
import application.models.SheetCache;
import application.models.json.Cell;
import application.models.json.Config;

/**
 * Repository responsible for accessing json configuration files.
 *
 * @author Mark "Grandy" Bishop
 */
@Repository
public class ConfigurationFileRepository extends AbstractRepository {
	private static final Logger LOGGER = LogManager.getLogger(ConfigurationFileRepository.class);

	@Autowired
	private ConfigurationFile configurationFile;
	@Autowired
	private SheetCache sheetCache;
	@Autowired
	private FileUpdateRepository fileUpdateRepository;

	/** Load the configuration file. */
	public void loadConfiguration(File file) throws Exception {
		if (file == null) {
			LOGGER.debug("Attempted to load a null configuration file");
			return;
		}

		loadFile(file);
	}

	/**
	 * Loads the given {@link File} into java beans, which are then accessible
	 * from this class.
	 * 
	 * @throws Exception
	 *             any exception from config loading.
	 */
	public synchronized void loadFile(File file) throws Exception {
		String jsonStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		JsonObject root = JsonParser.parseString(jsonStr).getAsJsonObject();
		LOGGER.debug("Config file has been loaded.");
		LOGGER.trace(root.toString());

		// Load json into java beans
		Config conf = new GsonBuilder().create().fromJson(jsonStr, Config.class);
		LOGGER.debug(conf);

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Config>> violations = validator.validate(conf);

		if (!violations.isEmpty()) {
			throw new JsonValidationException(violations);
		}

		configurationFile.setConfig(conf);
		configurationFile.setLastFile(file);

		configurationFile.getCellWrappers().clear();
		for (Cell cell : conf.getCells()) {
			if (cell != null) {
				configurationFile.getCellWrappers().add(new CellWrapper(cell));
			} else {
				LOGGER.debug(
						"Detected empty/null entry in the 'cells' array; Check that your 'cells' array in config does not have any double commas ,, or a comma after the last element of the array.");
			}
		}
		sheetCache.setup(configurationFile.getCellWrappers());

		fileUpdateRepository.cleanExistingFolderIfExists(configurationFile.getProjectName());
		fileUpdateRepository.createFilesFolder(configurationFile.getProjectName());
		createInitialFiles(configurationFile);
	}

	private void createInitialFiles(ConfigurationFile configurationFile) throws Exception {
		for (CellWrapper cellWrapper : configurationFile.getCells()) {
			String destFilePath = fileUpdateRepository.createFilePath(configurationFile.getProjectName(), cellWrapper);
			switch (cellWrapper.getFileExtension()) {
			case TXT:
				fileUpdateRepository.writeTextFile(destFilePath, "");
				break;
			case BMP:
			case GIF:
			case JPEG:
			case JPG:
			case PNG:
				fileUpdateRepository.saveTransparentImage(destFilePath, cellWrapper.getFileExtension().getExtension());
				break;
			case MP4:
			case WEBM:
			default:
				// TODO: Should we be providing empty files for video types?
				LOGGER.info("Not creating default/empty file for {} {} ", cellWrapper.getFileExtension(), cellWrapper);
			}
		}
	}

	/**
	 * Reloads the existing config file, if there is one. If there isn't,
	 * nothing happens.
	 * 
	 * @throws Exception
	 *             any exception from config loading.
	 */
	public synchronized void reload() {
		File lastFile = configurationFile.getLastFile();
		if (lastFile != null) {
			LOGGER.debug("Reloading.");
			try {
				loadFile(lastFile);
			} catch (Exception e) {
				getExceptionHandler().handle(e);
			}
		} else {
			LOGGER.warn("There is no existing config file loaded");
		}
	}
}
