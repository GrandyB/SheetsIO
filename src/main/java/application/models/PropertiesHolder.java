/**
 * ApiHolder.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton holder of the apiKey/any other application config. Ties in with
 * Java's .properties file.
 *
 * @author Mark "Grandy" Bishop
 */
public class PropertiesHolder {
	private static final Logger LOGGER = LogManager.getLogger(PropertiesHolder.class);
	private static final PropertiesHolder INSTANCE = new PropertiesHolder();

	// Sample
	private static final String SAMPLE_API_TEST_SPREADSHEET_ID = "1z2BtJTik73zIUvKi0y9RZbImDyWp_RiQikaEeFBF5E8";
	private static final String SAMPLE_API_TEST_WORKBOOK_ID = "Test";

	public static final String COMMENT = " Set 'apiKey' below as the key from https://console.developers.google.com/ - e.g. apikey=123abc;";
	public static final String FILE_NAME = "application.properties";
	public static final String API_KEY = "apiKey";
	public static final String API_KEY_TEST_SPREADSHEET_ID = "apiKey.test.spreadsheetId";
	public static final String API_KEY_TEST_WORKBOOK_ID = "apiKey.test.workbookId";
	public static final String LAST_CONFIG = "last.config";

	public static final int SCENE_WIDTH = 210;
	public static final int SCENE_HEIGHT = 370;
	public static final int LAYOUT_SPACING = 10;
	public static final int AVAILABLE_WIDTH = SCENE_WIDTH - (2 * LAYOUT_SPACING);
	public static final int INTERNAL_SPACING = 4;

	/**
	 * "This version of the Google Sheets API has a limit of 500 requests per 100
	 * seconds per project, and 100 requests per 100 seconds per user. Limits for
	 * reads and writes are tracked separately. There is no daily usage limit."
	 * 
	 * https://developers.google.com/sheets/api/limits
	 */
	public static final long UPDATE_INTERVAL = 2000L;

	private final Properties props = new Properties();
	private ApiKeyStatus apiKeyStatus = ApiKeyStatus.MISSING;

	public PropertiesHolder() {
		load();
	}

	public void load() {
		// Private constructor to restrict new instances
		LOGGER.debug("Reading all properties from the file");
		try {
			FileInputStream fis = new FileInputStream(FILE_NAME);
			props.load(fis);
			fis.close();

			String apiKey = props.getProperty(API_KEY);
			LOGGER.trace("{}: {}", API_KEY, apiKey);
			apiKeyStatus = apiKey == null || apiKey.isEmpty() ? ApiKeyStatus.INCOMPLETE : ApiKeyStatus.LOADED;
			return;
		} catch (Exception e) {
			LOGGER.debug("Reading input file failed; assuming no file exists", e);
		}

		LOGGER.debug("Write empty properties");
		props.setProperty("apiKey", "");
		props.setProperty(API_KEY_TEST_SPREADSHEET_ID, SAMPLE_API_TEST_SPREADSHEET_ID);
		props.setProperty(API_KEY_TEST_WORKBOOK_ID, SAMPLE_API_TEST_WORKBOOK_ID);
		props.setProperty(LAST_CONFIG, "");
		try {
			flush();
		} catch (Exception e) {
			LOGGER.debug("Saving properties file failed", e);
		}
		apiKeyStatus = ApiKeyStatus.INCOMPLETE;
	}

	public boolean isLoaded() {
		return ApiKeyStatus.LOADED.equals(apiKeyStatus);
	}

	public ApiKeyStatus getStatus() {
		return this.apiKeyStatus;
	}

	/** @return String property if exists, or null. */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public Set<String> getAllPropertyNames() {
		return props.stringPropertyNames();
	}

	public boolean containsKey(String key) {
		return props.containsKey(key);
	}

	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public void flush() throws FileNotFoundException, IOException {
		try (final OutputStream outputstream = new FileOutputStream(FILE_NAME);) {
			props.store(outputstream, COMMENT);
			outputstream.close();
		}
	}

	public static PropertiesHolder get() {
		return PropertiesHolder.INSTANCE;
	}
}
