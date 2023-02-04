/**
 * UpdateRunnable.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.threads;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.IExceptionHandler;
import application.Main;
import application.exceptions.IllegalFileExtensionException;
import application.models.ConfigHolder;
import application.models.PropertiesHolder;
import application.services.UpdateService;

/**
 * Meat of the thread running the update loop. Runs continuously based on the
 * updateInterval, but can be triggered "manually" (on next cycle) or
 * "automatically" (every cycle), as per the controls in {@link Main}.
 *
 * @author Mark "Grandy" Bishop
 */
public class UpdateRunnable extends IntervalRunnable {
	private static final Logger LOGGER = LogManager.getLogger(UpdateRunnable.class);

	private UpdateService updater;
	private boolean runOnce = false;

	public UpdateRunnable(IExceptionHandler handler) {
		super(handler, PropertiesHolder.get().getUpdateInterval());
	}

	@Override
	public synchronized void perform() throws Exception {
		LOGGER.trace("Updater: {}\n" + "Auto update: {}\n" + "RunOnce: {}", this.updater,
				ConfigHolder.get().isAutoUpdate(), this.runOnce);
		if (this.updater != null && (ConfigHolder.get().isAutoUpdate() || this.runOnce)) {
			this.runOnce = false;
			updater.update();
		}
	}

	public synchronized void updateConfig(boolean fromScratch) throws IOException, IllegalFileExtensionException {
		this.unpause();

		if (this.updater == null) {
			this.updater = new UpdateService();
		}
		this.updater.setConfig(fromScratch);
	}

	/** Perform a single update, on next update iteration. */
	public synchronized void runOnce() {
		this.unpause();
		LOGGER.debug("Updating on next tick...");
		this.runOnce = true;
	}
}
