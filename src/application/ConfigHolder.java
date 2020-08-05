/**
 * Config.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020. 
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
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import application.models.json.Config;
import applications.models.CellData;
import applications.models.JsonValidationException;
import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper around {@link Config}, accessing its data and reading in data.
 *
 * @author Mark "Grandy" Bishop
 */
public class ConfigHolder {
	/**
	 * "This version of the Google Sheets API has a limit of 500 requests per 100
	 * seconds per project, and 100 requests per 100 seconds per user. Limits for
	 * reads and writes are tracked separately. There is no daily usage limit."
	 * 
	 * https://developers.google.com/sheets/api/limits
	 */
	private static final long UPDATE_INTERVAL = 1000L;

	/** Cached version of most recent config {@link File}. */
	private File lastFile;

	@Getter
	@Setter
	private boolean autoUpdate = true;

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

	public List<CellData> getCells() {
		assert config != null : "No config available";
		return config.getMutatedMappings();
	}

	/**
	 * Reloads the most recently successful config file.
	 * 
	 * @throws JsonValidationException
	 *             if validation of the incoming config goes awry.
	 */
	public void reload() throws JsonSyntaxException, IOException, JsonValidationException {
		assert lastFile != null : "There is no existing config file loaded";
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
		String jsonStr = fileToString(file);
		JsonObject root = JsonParser.parseString(jsonStr).getAsJsonObject();
		System.out.println("Loaded file: " + root.toString());

		// Load json into java beans
		Config conf = new GsonBuilder().create().fromJson(jsonStr, Config.class);
		System.out.println(conf);

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Config>> violations = validator.validate(conf);

		if (violations.isEmpty()) {
			this.lastFile = file;
			this.config = conf;
		} else {
			throw new JsonValidationException(violations);
		}
	}

	private String fileToString(File file) throws IOException {
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	/** @return the update interval for querying the sheet. */
	public long getUpdateInterval() {
		return UPDATE_INTERVAL;
	}
}
