/**
 * ConfigPanel.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.panels;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.data.ConfigurationFileRepository;
import application.services.UpdateService;

/**
 * Logic base for the config section.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ConfigPanel extends BasePanel<ConfigPanel.Gui> {
	private static final Logger LOGGER = LogManager.getLogger(ConfigPanel.class);

	@Autowired
	private ConfigurationFileRepository configurationFileRepository;
	@Autowired
	private UpdateService updateService;

	public interface Gui extends BasePanel.Gui {
		/**
		 * Update the directory shown to the user on opening the file chooser.
		 */
		void setConfigChooserDirectory(File file);

		/** Set the 'currently loaded config' label. */
		void setConfigLabel(String label);

		/** Set whether the 'reload' link is visible. */
		void setReloadConfigLinkVisible(boolean visible);

		/** Select or deselect the 'autoupdate' checkbox. */
		void setAutoUpdateCheckState(boolean checked);

		/** Set whether the 'update now' button is enabled. */
		void setUpdateNowButtonEnabled(boolean enabled);
	}

	@Override
	public void initialise() {
		// Load the previous config if there is one
		String previousConfigPath = getAppProps().getLastConfigLocation();
		if (previousConfigPath != null && !previousConfigPath.isEmpty()) {
			handleConfigSelection(new File(previousConfigPath));
		}
	}

	/**
	 * Handle the selection of a config file from the chooser.
	 * 
	 * @throws Exception
	 *             if the saving of the 'last selected config' fails.
	 */
	public void handleConfigSelection(File file) {
		try {
			configurationFileRepository.loadConfiguration(file);
		} catch (Exception e) {
			getExceptionHandler().handle(e);
			return;
		}

		getGui().setConfigChooserDirectory(file.getAbsoluteFile().getParentFile());
		getGui().setConfigLabel(file.getName());
		getGui().setReloadConfigLinkVisible(true);

		// Set 'last config' option in application.properties
		getAppProps().setLastConfigLocation(file.getAbsolutePath());
	}

	/** Handle a click of the 'reload' config button in the UI. */
	public void handleReloadLinkClick() {
		configurationFileRepository.reload();
	}

	/** Handle a toggle in the auto update checkbox. */
	public void handleAutoUpdateCheck(boolean selected) {
		getTransientProperties().setAutoUpdate(selected);
		// TODO: Event?
	}

	/** Handle a press of the 'Update Now' button. */
	public void handleUpdateNowPress() {
		try {
			updateService.update();
		} catch (Exception e) {
			getExceptionHandler().handle(e);
		}
	}
}
