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

import application.exceptions.IllegalFileExtensionException;
import application.models.ConfigHolder;
import application.services.FileIO;
import application.services.ThreadCollector;
import application.threads.UpdateRunnable;

/**
 * Logic base for the config section.
 *
 * @author Mark "Grandy" Bishop
 */
public class ConfigPanel extends BasePanel<ConfigPanel.Gui> {

	private UpdateRunnable updateRunnable;

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
	public void preInitialise() {
		// Create /logs folder
		FileIO fileIO = new FileIO();
		try {
			fileIO.createFolder("logs");
		} catch (IOException e) {
			handleException(e);
		}

		// Create/begin the update thread
		if (updateRunnable == null) {
			// Ensure only ever have one
			updateRunnable = ThreadCollector.registerThread(new UpdateRunnable(this));
		}
		new Thread(updateRunnable).start();
	}

	/** Handle the selection of a config file from the chooser. */
	public void handleConfigSelection(File file) {
		if (file == null) {
			return;
		}
		getGui().setConfigChooserDirectory(file.getParentFile());

		try {
			ConfigHolder.loadFile(file);
			updateRunnable.updateConfig(true);
		} catch (Exception e) {
			handleException(e);
		}

		getGui().setConfigLabel(file.getName());
		getGui().setReloadConfigLinkVisible(true);
		getGui().setAutoUpdateCheckState(ConfigHolder.isAutoUpdate());
	}

	/** Handle a click of the 'reload' config button in the UI. */
	public void handleReloadLinkClick() {
		if (ConfigHolder.isLoaded()) {
			try {
				/*
				 * Reload backing config file, set it onto the thread, clearing and re-adding
				 * files into the relevant folder (empty).
				 */
				ConfigHolder.reload();
				updateRunnable.updateConfig(true);
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	/** Handle a toggle in the auto update checkbox. */
	public void handleAutoUpdateCheck(boolean selected) {
		ConfigHolder.setAutoUpdate(selected);
		getGui().setUpdateNowButtonEnabled(!selected);
		try {
			if (ConfigHolder.isLoaded()) {
				updateRunnable.updateConfig(false);
			}
		} catch (IOException | IllegalFileExtensionException e) {
			handleException(e);
		}
	}

	/** Handle a press of the 'Update Now' button. */
	public void handleUpdateNowPress() {
		updateRunnable.runOnce();
	}
}
