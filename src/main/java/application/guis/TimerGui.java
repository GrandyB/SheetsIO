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
package application.guis;

import application.Main;
import application.panels.TimerPanel;
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
public class TimerGui extends BaseGui<TimerPanel, TimerPanel.Gui> implements TimerPanel.Gui {

	private Spinner<Integer> hours;
	private Spinner<Integer> minutes;
	private Spinner<Integer> seconds;

	private Button startPauseButton = new Button("Start");
	private Text preview = new Text();

	public TimerGui(Main app) {
		super(app, new TimerPanel(), new VBox(3));

		getPanel().setUp();

		Text timerText = new Text("Timer");
		timerText.getStyleClass().add("timer-label");
		getLayout().add(timerText);

		getLayout().add(preview);

		/* SPINNERS */
		hours = createSpinner();
		minutes = createSpinner();
		seconds = createSpinner();

		HBox spinners = new HBox();
		spinners.getChildren().addAll(hours, minutes, seconds);
		getLayout().add(spinners);

		/* BUTTONS */
		startPauseButton.setOnAction(a -> {
			getPanel().handlePlayPauseButtonPress(hours.getValueFactory().getValue(),
					minutes.getValueFactory().getValue(), seconds.getValueFactory().getValue());
		});

		Button resetButton = new Button("Reset");
		resetButton.setOnAction(ev -> {
			getPanel().reset();
		});

		Button updateButton = new Button("Update");
		updateButton.setOnAction(ev -> {
			getPanel().handleUpdateButtonClick(hours.getValueFactory().getValue(), minutes.getValueFactory().getValue(),
					seconds.getValueFactory().getValue());
		});

		HBox buttons = new HBox(startPauseButton, updateButton, resetButton);
		getLayout().add(buttons);
	}

	/**
	 * Spinners are the devil; but thankfully we can kinda bend them to our will. By
	 * default spinners don't set their internal value when changing their value
	 * directly (outside of the buttons). However, with some {@link TextFormatter}
	 * wizardry, they do! ...don't ask.
	 * 
	 * @return Spinner<Integer> a properly instantiated {@link Spinner}.
	 */
	private Spinner<Integer> createSpinner() {
		Spinner<Integer> spinner = new Spinner<>();
		SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
		StringConverter<Integer> converter = new StringIntegerConverter();
		TextFormatter<Integer> formatter = new TextFormatter<>(factory.getConverter(), factory.getValue());
		spinner.getEditor().setTextFormatter(formatter);
		factory.valueProperty().bindBidirectional(formatter.valueProperty());

		spinner.setValueFactory(factory);
		spinner.getValueFactory().setConverter(converter);
		spinner.setEditable(true);
		spinner.setPrefSize(50, 20);

		return spinner;
	}

	@Override
	public void updatePreview(String display) {
		preview.setText(display);
	}

	@Override
	public void setPlayPauseButtonText(String text) {
		startPauseButton.setText(text);
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
