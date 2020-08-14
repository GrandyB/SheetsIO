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
	private static final String COMMENT = " Set apiKey below as the key from https://console.developers.google.com/ - e.g. apikey=123abc;";
	public static final String FILE_NAME = "application.properties";
	public static final String API_KEY = "apiKey";

	private final Properties props = new Properties();
	private ApiKeyStatus apiKeyStatus = ApiKeyStatus.MISSING;

	public PropertiesHolder() {
		reload();
	}

	public void reload() {
		// Private constructor to restrict new instances
		LOGGER.debug("Reading all properties from the file");
		try {
			FileInputStream fis = new FileInputStream(FILE_NAME);
			props.load(fis);
			fis.close();

			String apiKey = props.getProperty(API_KEY);
			LOGGER.debug("{}: {}", API_KEY, apiKey);
			apiKeyStatus = apiKey == null || apiKey.isEmpty() ? ApiKeyStatus.INCOMPLETE : ApiKeyStatus.LOADED;
			return;
		} catch (Exception e) {
			LOGGER.debug(e);
		}

		LOGGER.debug("Write empty properties");
		props.setProperty("apiKey", "");
		try {
			flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		apiKeyStatus = ApiKeyStatus.INCOMPLETE;
	}

	public boolean isLoaded() {
		return ApiKeyStatus.LOADED.equals(apiKeyStatus);
	}

	public ApiKeyStatus getStatus() {
		return this.apiKeyStatus;
	}

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

	// Bill Pugh Solution for singleton pattern
	private static class LazyHolder {
		private static final PropertiesHolder INSTANCE = new PropertiesHolder();
	}

	public static PropertiesHolder get() {
		return LazyHolder.INSTANCE;
	}
}
