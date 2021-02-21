/**
 * Loop.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.IExceptionHandler;
import lombok.RequiredArgsConstructor;

/**
 * Base class for interval/tick-based {@link Runnable}s in the application; can
 * be paused and resumed at will, and handles exceptions off to a provided
 * {@link IExceptionHandler} - usually the panel it is being controlled from.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public abstract class IntervalRunnable implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(IntervalRunnable.class);
	private boolean doStop = false;

	private final IExceptionHandler exceptionHandler;
	private final long interval;

	private boolean paused;

	@Override
	public void run() {
		while (keepRunning()) {
			try {
				if (!paused) {
					perform();
				}
				Thread.sleep(interval);
			} catch (Exception e) {
				exceptionHandler.handleException(e);
				paused = true;
			}
		}
	}

	protected abstract void perform() throws Exception;

	/** Pause the thread. */
	public synchronized void pause() {
		LOGGER.debug("Pausing.");
		this.paused = true;
	}

	/* Unpause the thread. */
	public synchronized void unpause() {
		LOGGER.debug("Resuming.");
		this.paused = false;
	}

	/** Completely halt the thread; should only be used when exiting the app. */
	public synchronized void doStop() {
		this.doStop = true;
	}

	private synchronized boolean keepRunning() {
		return this.doStop == false;
	}
}
