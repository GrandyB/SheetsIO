/**
 * ApplicationProperties.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.configuration;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;

/**
 * Configuration class that loads the application.properties file, with
 * information such as the apiKey and other core values.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ApplicationProperties extends AbstractReloadableProperties {
	// Keys
	private static final String API_KEY = "apiKey";
	private static final String UPDATE_INTERVAL = "update.interval";
	private static final String LAST_CONFIG = "last.config";
	private static final String TEST_WORKBOOKID = "apiKey.test.workbookId";
	private static final String TEST_SPREADSHEETID = "apiKey.test.spreadsheetId";
	private static final String HTTP_PORT = "http.port";

	// Sample values
	private static final String SAMPLE_API_TEST_SPREADSHEET_ID = "1z2BtJTik73zIUvKi0y9RZbImDyWp_RiQikaEeFBF5E8";
	private static final String SAMPLE_API_TEST_WORKBOOK_ID = "Test";
	private static final String DEFAULT_PORT = "8001";

	@PostConstruct
	public void init() {
		// This runs after {@link AbstractReloadableProperties} is done
		if (!isLoaded()) {
			// Create a default file
			PropertiesConfiguration newConfig = new PropertiesConfiguration();
			newConfig.setAutoSave(true);
			newConfig.setProperty(API_KEY, "");
			newConfig.setProperty(UPDATE_INTERVAL, "2000");
			newConfig.setProperty(TEST_WORKBOOKID, SAMPLE_API_TEST_WORKBOOK_ID);
			newConfig.setProperty(TEST_SPREADSHEETID, SAMPLE_API_TEST_SPREADSHEET_ID);
			newConfig.setProperty(HTTP_PORT, DEFAULT_PORT);
			newConfig.setProperty(LAST_CONFIG, "");

			newConfig.setFileName(FILE_PATH);
			setConfiguration(newConfig);
			save();
		}
	}

	/** @return the current apiKey. */
	public String getApiKey() {
		return getProperty(API_KEY);
	}

	/** Set the apiKey. */
	public void setApiKey(String apiKey) {
		setProperty(API_KEY, apiKey);
	}

	/** @return the {@link Long} update interval, in ms. */
	public long getUpdateInterval() {
		return Long.parseLong(getProperty(UPDATE_INTERVAL));
	}

	/** @return the path to the last used configuration file. */
	public String getLastConfigLocation() {
		return getProperty(LAST_CONFIG);
	}

	/** Set the last known config path. */
	public void setLastConfigLocation(String location) {
		setProperty(LAST_CONFIG, location);
	}

	/** @return the test workbook ID. */
	public String getTestWorkbookID() {
		return getProperty(TEST_WORKBOOKID);
	}

	/** @return the test workbook ID. */
	public String getTestSpreadsheetID() {
		return getProperty(TEST_SPREADSHEETID);
	}
}
