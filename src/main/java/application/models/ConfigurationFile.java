/**
 * Config.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020. 
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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.exceptions.IllegalFileExtensionException;
import application.exceptions.JsonValidationException;
import application.models.json.Cell;
import application.models.json.Config;
import lombok.Getter;

/**
 * Wrapper around {@link Config}, accessing its data and reading in data.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ConfigurationFile {
	private static final Logger LOGGER = LogManager.getLogger(ConfigurationFile.class);

	/** Cached version of most recent config {@link File}. */
	private File lastFile;

	@Getter
	private boolean autoUpdate = false;

	/** The latest loaded config. */
	private Config config;

	/**
	 * CellWrappers, made from Cells, used by the rest of the app, wiped/repopulated
	 * on config load.
	 */
	private List<CellWrapper> cellWrappers = new ArrayList<>();

	public synchronized String getProjectName() {
		assert config != null : "No config available";
		return config.getProjectName();
	}

	public synchronized String getSpreadsheetId() {
		assert config != null : "No config available";
		return config.getSpreadsheetId();
	}

	public synchronized String getWorksheetName() {
		assert config != null : "No config available";
		return config.getWorksheetName();
	}

	public synchronized List<CellWrapper> getCells() throws IllegalFileExtensionException {
		assert config != null : "No config loaded";
		return cellWrappers;
	}

	public synchronized boolean isLoaded() {
		return lastFile != null;
	}

	public synchronized void setAutoUpdate(boolean update) {
		LOGGER.debug("Autoupdate set to {}", update);
		this.autoUpdate = update;
	}

	/**
	 * Reloads the most recently successful config file.
	 * 
	 * @throws Exception
	 *             any exception from config loading.
	 */
	public synchronized void reload() throws Exception {
		assert lastFile != null : "There is no existing config file loaded";
		LOGGER.debug("Reloading.");
		loadFile(lastFile);
	}

	/**
	 * Loads the given {@link File} into java beans, which are then accessible from
	 * this class.
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

		this.lastFile = file;
		this.config = conf;

		cellWrappers.clear();
		for (Cell cell : config.getCells()) {
			if (cell != null) {
				cellWrappers.add(new CellWrapper(cell));
			} else {
				LOGGER.debug(
						"Detected empty/null entry in the 'cells' array; Check that your 'cells' array in config does not have any double commas ,, or a comma after the last element of the array.");
			}
		}
	}
}
