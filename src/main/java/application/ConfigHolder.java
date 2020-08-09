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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import application.models.CellWrapper;
import application.models.JsonValidationException;
import application.models.json.Cell;
import application.models.json.Config;
import application.models.json.FileType;
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

	private Map<Cell, FileType> fileTypePerCell = new HashMap<>();
	private Map<String, FileType> fileTypeById = new HashMap<>();

	/** Cached version of most recent config {@link File}. */
	private File lastFile;

	@Getter
	private boolean autoUpdate = false;

	/** The latest loaded config. */
	private Config config;

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

	public List<CellWrapper> getCells() {
		assert config != null : "No config available";
		return config.getMutatedMappings();
	}

	public List<FileType> getFileTypes() {
		assert config != null : "No config available";
		return config.getFileTypes();
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
	 */
	public void reload() throws JsonSyntaxException, IOException, JsonValidationException {
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
	 */
	public void loadFile(File file) throws IOException, JsonSyntaxException, JsonValidationException {
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

		// Populate "fileType" mappings
		config.getFileTypes().forEach(f -> {
			fileTypeById.put(f.getId(), f);
		});
		// Populate our map of cells -> fileTypes
		config.getCells().forEach(c -> {
			fileTypePerCell.put(c, fileTypeById.get(c.getFileType()));
		});
	}

	/** @return the update interval for querying the sheet. */
	public long getUpdateInterval() {
		return UPDATE_INTERVAL;
	}

	/**
	 * @return the String extension from the config for the particular cell & file.
	 */
	public String getExtension(Cell cell) {
		FileType type = this.fileTypePerCell.get(cell);
		assert type != null : "Could not find the FileType for given cell";
		return type.getExtension();
	}

	/**
	 * @return the {@link FileType} from a given id/name, as defined in "fileTypes"
	 *         in config.
	 */
	public FileType getFileTypeById(String id) {
		FileType type = this.fileTypeById.get(id);
		assert type != null : "Could not find the FileType for given id";
		return type;
	}
}
