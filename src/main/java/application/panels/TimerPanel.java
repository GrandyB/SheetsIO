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

import application.models.TimerDuration;
import application.threads.ThreadCollector;
import application.threads.TimerRunnable;

/**
 * Timer panel, for all logic surrounding the timer.
 *
 * @author Mark "Grandy" Bishop
 */
public class TimerPanel extends BasePanel<TimerPanel.Gui> {
	private static final Logger LOGGER = LogManager.getLogger(TimerPanel.class);

	private TimerRunnable timerRunnable;
	private TimerDuration time;

	private boolean initialState = true;
	private boolean isCurrentlyRunning = false;

	public interface Gui extends BasePanel.Gui {

		/** Update the current live timer text preview. */
		void updatePreview(String display);

		/** Set the play/pause button text. */
		void setPlayPauseButtonText(String text);
	}

	public TimerPanel() {
		super();
		this.time = new TimerDuration();
	}

	/** Dependency injection, for use in tests. */
	public TimerPanel(TimerDuration time, TimerRunnable timerRunnable) {
		this.time = time;
		this.timerRunnable = timerRunnable;
	}

	@Override
	public void initialise() {
		super.initialise();
		getGui().updatePreview(getDisplay());

		if (this.timerRunnable == null) {
			// Ensure there is only ever one
			this.timerRunnable = ThreadCollector.registerRunnable(new TimerRunnable(this));
		}
		this.timerRunnable.setTimer(this);
		new Thread(timerRunnable).start();
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
		int currentTime = time.getTotalSeconds();
		time.update(0);
		try {
			this.timerRunnable.updateFile(true);

			this.initialState = true;
			this.isCurrentlyRunning = false;
			getGui().setPlayPauseButtonText("Start");
		} catch (IOException e) {
			handleException(e);
			time.update(currentTime);
		}

		getGui().updatePreview(time.getDisplay());
	}

	/**
	 * When the update button is clicked, the values from the three spinners is sent
	 * here; need to update our {@link TimerDuration}, the underlying file and
	 * preview.
	 */
	public void handleUpdateButtonClick(int hours, int minutes, int seconds) {
		time.setTimeAndFormat(hours, minutes, seconds);
		try {
			this.timerRunnable.updateFile(false);
		} catch (IOException e) {
			handleException(e);
		}
		getGui().updatePreview(time.getDisplay());
	}

	/**
	 * Handle a play/pause button press; using the current values of the 3 input
	 * fields.
	 */
	public void handlePlayPauseButtonPress(int hours, int minutes, int seconds) {
		if (initialState) {
			// First time click, setup the time
			time.setTimeAndFormat(hours, minutes, seconds);
			initialState = false;
			LOGGER.debug("Initialising timer.\n{}", time);
		}
		if (isCurrentlyRunning) {
			// We're currently running, so pause
			timerRunnable.pause();
			this.isCurrentlyRunning = false;
			getGui().setPlayPauseButtonText("Resume");
		} else {
			// We're not currently running/initial, so start it
			timerRunnable.unpause();
			this.isCurrentlyRunning = true;
			getGui().setPlayPauseButtonText("Pause");
		}
	}
}
