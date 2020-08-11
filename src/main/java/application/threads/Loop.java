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

import application.IExceptionHandler;
import lombok.RequiredArgsConstructor;

/**
 * 
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public abstract class Loop implements Runnable {
	private boolean doStop = false;

	private final IExceptionHandler exceptionHandler;

	@Override
	public void run() {
		while (keepRunning()) {
			try {
				perform();
			} catch (Exception e) {
				exceptionHandler.handleException(e);
			}
		}
	}

	protected abstract void perform() throws Exception;

	/** Completely halt the thread; should only be used when exiting the app. */
	public synchronized void doStop() {
		this.doStop = true;
	}

	private synchronized boolean keepRunning() {
		return this.doStop == false;
	}
}
