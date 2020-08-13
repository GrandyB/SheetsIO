/**
 * TimerRunnable.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.IExceptionHandler;
import application.panels.TimerPanel;
import application.services.FileIO;
import application.services.FileUpdater;
import lombok.Setter;

/**
 * A {@link Runnable} for updating the timer file, controlled by
 * {@link TimerPanel}.
 *
 * @author Mark "Grandy" Bishop
 */
public class TimerRunnable extends IntervalRunnable {

	private static final Logger LOGGER = LogManager.getLogger(TimerRunnable.class);

	private FileIO fileIO = new FileIO();
	@Setter
	private TimerPanel timer;

	public TimerRunnable(IExceptionHandler exceptionHandler) {
		super(exceptionHandler, 1000L);
		pause();
	}

	@Override
	protected void perform() throws Exception {
		LOGGER.trace("Perform timer tick");
		timer.decreaseTick();
		updateFile(false);
	}

	/**
	 * Updates the file. Can function as a reset if pausing the thread.
	 * 
	 * @param pause
	 *            if true, pause the thread
	 * @throws IOException
	 *             if writing the current time to file fails
	 */
	public void updateFile(boolean pause) throws IOException {
		if (pause) {
			pause();
		}
		writeFile(timer.getDisplay());
	}

	private void writeFile(String display) throws IOException {
		fileIO.writeTextFile(FileUpdater.FOLDER_PREFIX + File.separator + "timer.txt", display);
	}

	@Override
	public synchronized void doStop() {
		try {
			writeFile("");
		} catch (IOException e) {
			LOGGER.error("Exception while wiping the timer values during a thread stop: {}", e);
		}
		super.doStop();
	}
}
