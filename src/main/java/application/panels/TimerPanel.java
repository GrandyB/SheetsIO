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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.events.TimerUpdateEvent;
import application.models.TimerDuration;
import application.services.TimerService;
import javafx.application.Platform;

/**
 * Timer panel, for all logic surrounding the timer.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class TimerPanel extends BasePanel<TimerPanel.Gui> {
	private static final Logger LOGGER = LogManager.getLogger(TimerPanel.class);

	@Autowired
	private TimerService timerService;

	public interface Gui extends BasePanel.Gui {

		/** Update the current live timer text preview. */
		void updatePreview(String display);

		/** Set the play/pause button text. */
		void setPlayPauseButtonText(String text);

		int getHours();

		int getMinutes();

		int getSeconds();
	}

	@Override
	public void initialise() {
		super.initialise();
		getGui().updatePreview(timerService.getDisplay());
	}

	/** Reset the timer. */
	public void reset() {
		LOGGER.debug("Resetting timer");
		timerService.reset();
		getGui().setPlayPauseButtonText("Start");
	}

	/**
	 * When the update button is clicked, the values from the three spinners is
	 * sent here; need to update our {@link TimerDuration}, the underlying file
	 * and preview.
	 */
	public void handleUpdateButtonClick() {
		timerService.setTimeAndFormat(getGui().getHours(), getGui().getMinutes(), getGui().getSeconds());
		timerService.updateOnce();
	}

	@Subscribe
	public void handleTimerUpdateEvent(TimerUpdateEvent event) {
		Platform.runLater(() -> {
			getGui().updatePreview(event.getTime().getDisplay());
			if (!event.isRunning()) {
				getGui().setPlayPauseButtonText("Start");
			}
		});
	}

	/**
	 * Handle a play/pause button press; using the current values of the 3 input
	 * fields.
	 */
	public void handlePlayPauseButtonPress() {
		if (timerService.isRunning()) {
			timerService.setRunning(false);
			getGui().setPlayPauseButtonText("Resume");
		} else {
			timerService.setTimeAndFormat(getGui().getHours(), getGui().getMinutes(), getGui().getSeconds());
			timerService.setRunning(true);
			getGui().setPlayPauseButtonText("Pause");
		}
	}
}
