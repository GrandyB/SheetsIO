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
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.AppUtil;
import application.exceptions.IllegalFileExtensionException;
import application.models.ConfigHolder;
import application.models.PropertiesHolder;
import application.services.FileIO;
import application.services.FileUpdater;
import application.threads.ThreadCollector;
import application.threads.UpdateRunnable;

/**
 * Logic base for the config section.
 *
 * @author Mark "Grandy" Bishop
 */
public class ConfigPanel extends BasePanel<ConfigPanel.Gui> {
	private static final Logger LOGGER = LogManager.getLogger(ConfigPanel.class);

	public static final String LOGS_FOLDER = "logs";
	public static final String TEMP_FOLDER = "temp";

	private UpdateRunnable updateRunnable;
	private ConfigHolder configHolder;
	private FileIO fileIO;

	/** Primary constructor. */
	public ConfigPanel() {
		super();
		this.configHolder = ConfigHolder.get();
		this.fileIO = new FileIO();

		// Create/begin the update thread
		if (updateRunnable == null) {
			// Ensure only ever have one
			updateRunnable = ThreadCollector.registerUpdateLoop(new UpdateRunnable(this));
		}
		new Thread(this.updateRunnable).start();
	}

	/** Dependency injection, for use in tests. */
	public ConfigPanel(ConfigHolder configHolder, FileIO fileIO, UpdateRunnable updateRunnable, AppUtil appUtil,
			PropertiesHolder props) {
		super(appUtil, props);
		this.configHolder = configHolder;
		this.fileIO = fileIO;
		this.updateRunnable = updateRunnable;
	}

	public interface Gui extends BasePanel.Gui {
		/** Update the directory shown to the user on opening the file chooser. */
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
		super.initialise();

		// Create the initial folders
		try {
			fileIO.createFolder(LOGS_FOLDER);
			fileIO.createFolder(FileUpdater.FOLDER_PREFIX);
			fileIO.createFolder(ConfigPanel.TEMP_FOLDER);
		} catch (IOException e) {
			handleException(e);
		}

		// Load the previous config if there is one
		String previousConfigPath = getProps().getProperty(PropertiesHolder.LAST_CONFIG);
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
		if (file == null) {
			return;
		}

		try {
			this.configHolder.loadFile(file);
			this.updateRunnable.updateConfig(true);
		} catch (Exception e) {
			handleException(e);
			return;
		}

		getGui().setConfigChooserDirectory(file.getAbsoluteFile().getParentFile());
		getGui().setConfigLabel(file.getName());
		getGui().setReloadConfigLinkVisible(true);
		getGui().setAutoUpdateCheckState(this.configHolder.isAutoUpdate());

		// Set 'last config' option in application.properties
		getProps().setProperty(PropertiesHolder.LAST_CONFIG, file.getAbsolutePath());
		try {
			getProps().flush();
		} catch (IOException e) {
			LOGGER.error("Unable to set the property '{}' to '{}': {}", PropertiesHolder.LAST_CONFIG,
					file.getAbsolutePath(), e);
		}
	}

	/** Handle a click of the 'reload' config button in the UI. */
	public void handleReloadLinkClick() {
		if (this.configHolder.isLoaded()) {
			try {
				/*
				 * Reload backing config file, set it onto the thread, clearing and re-adding
				 * files into the relevant folder (empty).
				 */
				this.configHolder.reload();
				this.updateRunnable.updateConfig(true);
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	/** Handle a toggle in the auto update checkbox. */
	public void handleAutoUpdateCheck(boolean selected) {
		this.configHolder.setAutoUpdate(selected);
		getGui().setUpdateNowButtonEnabled(!selected);
		try {
			if (this.configHolder.isLoaded()) {
				this.updateRunnable.updateConfig(false);
			}
		} catch (IOException | IllegalFileExtensionException e) {
			handleException(e);
		}
	}

	/** Handle a press of the 'Update Now' button. */
	public void handleUpdateNowPress() {
		this.updateRunnable.runOnce();
	}
}
