/**
 * TimerPanel.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.ThreadCollector;
import application.models.TimerDuration;
import application.threads.TimerRunnable;

/**
 * Timer panel, for all logic surrounding the timer.
 *
 * @author Mark "Grandy" Bishop
 */
public class TimerPanel extends BasePanel<TimerPanel.Gui> {
	private static final Logger LOGGER = LogManager.getLogger(TimerPanel.class);

	private TimerRunnable thread = ThreadCollector.registerThread(new TimerRunnable(this));
	private TimerDuration time;

	private boolean initialState = true;
	private boolean isCurrentlyRunning = false;

	public interface Gui extends BasePanel.Gui {

		/** Update the current live timer text preview. */
		void updatePreview(String display);

		/** Set the play/pause button text. */
		void setPlayPauseButtonText(String text);
	}

	public void setUp() {
		this.time = new TimerDuration();
		thread.setTimer(this);

		getGui().updatePreview(getDisplay());
		new Thread(thread).start();
	}

	/**
	 * Perform a countdown tick, decreasing the time by 1 second.
	 * 
	 * @return true if the timer was indeed decreased (false if timer is at 0)
	 */
	public boolean decreaseTick() {
		boolean didDecrease = this.time.decrease();
		getGui().updatePreview(time.getDisplay());
		return didDecrease;
	}

	/** @return the human-readable format of the remaining time. */
	public String getDisplay() {
		return time.getDisplay();
	}

	/** Reset the timer. */
	public void reset() {
		LOGGER.debug("Resetting timer");
		time.update(0);
		try {
			this.thread.resetTimer();
		} catch (IOException e) {
			handleException(e);
		}
		this.initialState = true;
		this.isCurrentlyRunning = false;
		getGui().setPlayPauseButtonText("Start");

		getGui().updatePreview(time.getDisplay());
	}

	/**
	 * 
	 */
	public void handleUpdateButtonClick(int hours, int minutes, int seconds) {
		time.setTimeAndFormat(hours, minutes, seconds);
		getGui().updatePreview(time.getDisplay());
	}

	public void handlePlayPauseButtonPress(int hours, int minutes, int seconds) {
		if (initialState) {
			// First time click, setup the time
			time.setTimeAndFormat(hours, minutes, seconds);
			initialState = false;
		}
		if (isCurrentlyRunning) {
			// We're currently running, so pause
			thread.setPaused(true);
			this.isCurrentlyRunning = false;
			getGui().setPlayPauseButtonText("Resume");
		} else {
			// We're not currently running/initial, so start it
			thread.setPaused(false);
			this.isCurrentlyRunning = true;
			getGui().setPlayPauseButtonText("Pause");
		}
	}
}
