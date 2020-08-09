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
package application;

import java.io.IOException;

import application.models.IExceptionHandler;
import lombok.RequiredArgsConstructor;

/**
 * Meat of the thread running the update loop. Runs continuously based on the
 * updateInterval, but can be triggered "manually" (on next cycle) or
 * "automatically" (every cycle), as per the controls in {@link Main}.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public class UpdateRunnable implements Runnable {
	/** Set a default of 1s update, until we have loaded a config. */
	private long updateInterval = ConfigHolder.UPDATE_INTERVAL;
	private boolean autoUpdate = true;

	private UpdateController updater;
	private boolean doStop = false;
	private boolean runOnce = false;

	private final IExceptionHandler exceptionHandler;

	@Override
	public void run() {
		while (keepRunning()) {
			try {
				if (this.updater != null && (this.autoUpdate || this.runOnce)) {
					updater.update();
					this.runOnce = false;
				}

				Thread.sleep(updateInterval);
			} catch (Exception e) {
				exceptionHandler.handleException(e);
			}
		}
	}

	public synchronized void updateConfig(ConfigHolder config, boolean fromScratch) throws IOException {
		this.updateInterval = config.getUpdateInterval();
		this.autoUpdate = config.isAutoUpdate();

		if (this.updater == null) {
			this.updater = new UpdateController();
		}
		this.updater.setConfig(config, fromScratch);
	}

	/** Perform a single update, on next update iteration. */
	public synchronized void runOnce() {
		this.runOnce = true;
	}

	/** Completely halt the thread; should only be used when exiting the app. */
	public synchronized void doStop() {
		this.doStop = true;
	}

	private synchronized boolean keepRunning() {
		return this.doStop == false;
	}
}
