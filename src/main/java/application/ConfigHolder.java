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
package application;

import java.io.File;
import java.io.IOException;
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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import application.exceptions.IllegalFileExtensionException;
import application.exceptions.JsonValidationException;
import application.models.CellWrapper;
import application.models.json.Cell;
import application.models.json.Config;
import lombok.Getter;

/**
 * Wrapper around {@link Config}, accessing its data and reading in data.
 *
 * @author Mark "Grandy" Bishop
 */
public class ConfigHolder {
	private static final Logger LOGGER = LogManager.getLogger(ConfigHolder.class);

	/**
	 * "This version of the Google Sheets API has a limit of 500 requests per 100
	 * seconds per project, and 100 requests per 100 seconds per user. Limits for
	 * reads and writes are tracked separately. There is no daily usage limit."
	 * 
	 * https://developers.google.com/sheets/api/limits
	 */
	public static final long UPDATE_INTERVAL = 1000L;

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

	public String getProjectName() {
		assert config != null : "No config available";
		return config.getProjectName();
	}

	public String getApiKey() {
		assert config != null : "No config available";
		return config.getApiKey();
	}

	public String getSpreadsheetId() {
		assert config != null : "No config available";
		return config.getSpreadsheetId();
	}

	public String getWorksheetName() {
		assert config != null : "No config available";
		return config.getWorksheetName();
	}

	public List<CellWrapper> getCells() throws IllegalFileExtensionException {
		assert config != null : "No config loaded";
		return this.cellWrappers;
	}

	public boolean isLoaded() {
		return lastFile != null;
	}

	public void setAutoUpdate(boolean update) {
		LOGGER.debug("Autoupdate set to {}", update);
		this.autoUpdate = update;
	}

	/**
	 * Reloads the most recently successful config file.
	 * 
	 * @throws JsonValidationException
	 *             if validation of the incoming config goes awry.
	 * @throws IllegalFileExtensionException
	 */
	public void reload()
			throws JsonSyntaxException, IOException, JsonValidationException, IllegalFileExtensionException {
		assert lastFile != null : "There is no existing config file loaded";
		LOGGER.debug("Reloading.");
		loadFile(lastFile);
	}

	/**
	 * Loads the given {@link File} into java beans, which are then accessible from
	 * this class.
	 * 
	 * @throws JsonValidationException
	 *             if validation of the incoming config goes awry.
	 * @throws IllegalFileExtensionException
	 */
	public void loadFile(File file)
			throws IOException, JsonSyntaxException, JsonValidationException, IllegalFileExtensionException {
		String jsonStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		JsonObject root = JsonParser.parseString(jsonStr).getAsJsonObject();
		LOGGER.debug("Loaded file: {}", root.toString());

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
			cellWrappers.add(new CellWrapper(cell));
		}
	}

	/** @return the update interval for querying the sheet. */
	public long getUpdateInterval() {
		return UPDATE_INTERVAL;
	}
}
