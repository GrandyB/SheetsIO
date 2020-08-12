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

import application.ConfigHolder;
import application.FileIO;
import application.ThreadCollector;
import application.exceptions.IllegalFileExtensionException;
import application.threads.UpdateRunnable;

/**
 * 
 *
 * @author Mark "Grandy" Bishop
 */
public class ConfigPanel extends BasePanel<ConfigPanel.Gui> {

	private final UpdateRunnable updateRunnable = ThreadCollector.registerThread(new UpdateRunnable(this));

	public interface Gui extends BasePanel.Gui {
		void setConfigChooserDirectory(File file);

		void setConfigLabel(String label);

		void setReloadConfigLinkVisible(boolean visible);

		void setAutoUpdateCheckState(boolean checked);

		void setUpdateNowButtonEnabled(boolean enabled);
	}

	public void initialise() {
		// Create /logs folder
		FileIO fileIO = new FileIO();
		try {
			fileIO.createFolder("logs");
		} catch (IOException e) {
			handleException(e);
		}

		// Begin the update thread
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
