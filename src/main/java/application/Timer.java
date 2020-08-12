/**
 * TimerLayout.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.models.TimerDuration;
import application.threads.TimerRunnable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Layout containing timer-related functions.
 *
 * @author Mark "Grandy" Bishop
 */
public class Timer extends VBox {
	private static final Logger LOGGER = LogManager.getLogger(Timer.class);

	private TimerRunnable thread;
	private TimerDuration time;

	private Spinner<Integer> hours;
	private Spinner<Integer> minutes;
	private Spinner<Integer> seconds;
	private StartPauseButton startPauseButton = new StartPauseButton();

	private Text preview = new Text();

	public void setup(TimerRunnable thread, IExceptionHandler handler) throws IOException {
		this.thread = thread;
		thread.setTimer(this);
		this.time = new TimerDuration();

		Text timerText = new Text("Timer");
		timerText.getStyleClass().add("timer-label");
		getChildren().add(timerText);

		getChildren().add(preview);

		hours = createSpinner();
		minutes = createSpinner();
		seconds = createSpinner();

		HBox spinners = new HBox();
		spinners.getChildren().addAll(hours, minutes, seconds);
		getChildren().add(spinners);

		Button resetButton = new Button("Reset");
		resetButton.setOnAction((ev) -> {
			try {
				this.reset();
			} catch (IOException e) {
				handler.handleException(e);
			}
		});
		Button updateButton = new Button("Update");
		updateButton.setOnAction((ev) -> {
			time.setTimeAndFormat(hours.getValueFactory().getValue(), minutes.getValueFactory().getValue(),
					seconds.getValueFactory().getValue());
			updatePreview();
		});
		HBox buttons = new HBox(startPauseButton, updateButton, resetButton);
		getChildren().add(buttons);

		this.reset();
	}

	/**
	 * @return
	 */
	private Spinner<Integer> createSpinner() {
		Spinner<Integer> spinner = new Spinner<>();
		SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
		StringConverter<Integer> converter = new StringIntegerConverter();
		// hook in a formatter with the same properties as the factory
		TextFormatter<Integer> formatter = new TextFormatter<>(factory.getConverter(), factory.getValue());
		spinner.getEditor().setTextFormatter(formatter);
		// bidi-bind the values
		factory.valueProperty().bindBidirectional(formatter.valueProperty());

		spinner.setValueFactory(factory);
		spinner.getValueFactory().setConverter(converter);
		spinner.setEditable(true);
		return spinner;
	}

	public void reset() throws IOException {
		LOGGER.debug("Resetting timer");
		time.update(0);
		updatePreview();
		startPauseButton.reset();
		this.thread.resetTimer();

	}

	private void updatePreview() {
		preview.setText(this.time.getDisplay());
	}

	public boolean decreaseTick() {
		boolean didDecrease = this.time.decrease();
		preview.setText(time.getDisplay());
		return didDecrease;
	}

	/** @return Get the display for the current {@link TimerDuration}. */
	public String getDisplay() {
		return this.time.getDisplay();
	}

	private class StartPauseButton extends Button {
		boolean initial = true;
		boolean running;

		public StartPauseButton() {
			this.setText("Start");

			this.setOnAction((e) -> {
				if (initial) {
					// First time click, setup the time
					time.setTimeAndFormat(hours.getValueFactory().getValue(), minutes.getValueFactory().getValue(),
							seconds.getValueFactory().getValue());
					initial = false;
				}
				if (running) {
					// We're currently running, so pause
					thread.setPaused(true);
					this.running = false;
					setText("Resume");
				} else {
					// We're not currently running/initial, so start it
					thread.setPaused(false);
					this.running = true;
					setText("Pause");
				}
			});
		}

		/** Reset button state, act as if first time again. */
		public void reset() {
			this.running = false;
			this.initial = true;
			this.setText("Start");
		}
	}

	private class StringIntegerConverter extends StringConverter<Integer> {
		@Override
		public String toString(Integer object) {
			return object.toString();
		}

		@Override
		public Integer fromString(String string) {
			return Integer.parseInt(string.replaceAll("[^\\d]", ""));
		}
	}
}
