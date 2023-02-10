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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import application.exceptions.IllegalFileExtensionException;
import application.models.json.Config;
import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper around {@link Config}, accessing its data and reading in data.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ConfigurationFile {
	private static final Logger LOGGER = LogManager.getLogger(ConfigurationFile.class);

	/** Cached version of most recent config {@link File}. */
	@Getter
	@Setter
	private File lastFile;

	/** The latest loaded config. */
	@Setter
	private Config config;

	/**
	 * CellWrappers, made from Cells, used by the rest of the app, wiped/repopulated
	 * on config load.
	 */
	@Getter
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
}
