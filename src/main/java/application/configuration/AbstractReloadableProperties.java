/**
 * ApplicationConfiguration.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * Configuration class that loads the application.properties file.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public abstract class AbstractReloadableProperties {
	private static final Logger LOGGER = LogManager.getLogger(AbstractReloadableProperties.class);
	protected static final String FILE_PATH = "application.properties";
	@Setter(AccessLevel.PROTECTED)
	private PropertiesConfiguration configuration;

	@PostConstruct
	private void init() {
		try {
			LOGGER.debug("Loading the properties file: " + FILE_PATH);
			configuration = new PropertiesConfiguration(FILE_PATH);
		} catch (ConfigurationException e) {
			LOGGER.error("Unable to load {} file", FILE_PATH, e);
		}
	}

	/** Enable automatic reloading of the properties file every X ms). */
	public void enableAutomaticReload(int delayInMs) {
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(delayInMs);
		configuration.setReloadingStrategy(fileChangedReloadingStrategy);
	}

	/** Disable automatic reload. */
	public void disableAutomaticReload() {
		configuration.setReloadingStrategy(new InvariantReloadingStrategy());
	}

	/** Forcibly reload the configuration file. */
	public void reload() {
		try {
			configuration.refresh();
		} catch (ConfigurationException e) {
			LOGGER.error("Unable to refresh {} file", FILE_PATH, e);
		}
	}

	/** @return whether there are any properties loaded. */
	public boolean isLoaded() {
		return !configuration.isEmpty();
	}

	/** @return the property, or null. */
	protected String getProperty(String key) {
		return (String) configuration.getProperty(key);
	}

	/** Set a property and save the config. */
	protected void setProperty(String key, Object value) {
		configuration.setProperty(key, value);
	}

	/** Save the configuration. */
	public void save() {
		try {
			configuration.save();
		} catch (ConfigurationException e) {
			LOGGER.error("Unable to save {} file", FILE_PATH, e);
		}
	}
}
