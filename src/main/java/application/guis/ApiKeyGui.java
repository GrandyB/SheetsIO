/**
 * ApiKeyGui.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.models.ApiKeyStatus;
import application.models.PropertiesHolder;
import application.panels.ApiKeyPanel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * UI for the apiKey input and validation section.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ApiKeyGui extends BaseGui<ApiKeyPanel, ApiKeyPanel.Gui, VBox> implements ApiKeyPanel.Gui {
	private static final String HELP_LINK = "https://github.com/GrandyB/SheetsIO#apikey";

	private HBox textAndIndicatorLayout;
	private Circle statusCircle;
	private TextField apiKeyInput = new TextField();
	private Hyperlink helpLink = new Hyperlink("Help");

	@Autowired
	public ApiKeyGui(ApiKeyPanel panel) {
		super(panel);
	}

	@Override
	protected void doLayout() {
		ApiKeyStatus status = PropertiesHolder.get().getStatus();
		helpLink.setOnAction(a -> getPanel().openBrowser(HELP_LINK));

		Text apiKeyText = new Text("API key");
		apiKeyText.getStyleClass().add("bold-text");
		statusCircle = ApiKeyStatus.getIndicatorCircle(status);
		textAndIndicatorLayout = new HBox(apiKeyText, statusCircle, helpLink);
		textAndIndicatorLayout.setAlignment(Pos.CENTER_LEFT);
		textAndIndicatorLayout.setSpacing(5);
		getLayout().add(textAndIndicatorLayout);

		addApiKeyInput();

		Button setApiKeyButton = new Button("Save");
		setApiKeyButton.setOnAction(e -> getPanel().handleSetApiKeyPress(apiKeyInput.getText()));
		getLayout().add(setApiKeyButton);
	}

	private void addApiKeyInput() {
		apiKeyInput.setManaged(false);
		apiKeyInput.setVisible(false);

		// Actual password field
		final PasswordField passwordField = new PasswordField();

		ToggleButton checkBox = new ToggleButton("Show");

		apiKeyInput.managedProperty().bind(checkBox.selectedProperty());
		apiKeyInput.visibleProperty().bind(checkBox.selectedProperty());

		passwordField.managedProperty().bind(checkBox.selectedProperty().not());
		passwordField.visibleProperty().bind(checkBox.selectedProperty().not());

		// Bind the textField and passwordField text values bidirectionally.
		apiKeyInput.textProperty().bindBidirectional(passwordField.textProperty());
		HBox hbox = new HBox(apiKeyInput, passwordField, checkBox);
		hbox.setSpacing(PropertiesHolder.INTERNAL_SPACING);
		getLayout().add(hbox);
	}

	@Override
	public void setCircle(ApiKeyStatus status) {
		textAndIndicatorLayout.getChildren().remove(statusCircle);
		statusCircle = ApiKeyStatus.getIndicatorCircle(status);
		textAndIndicatorLayout.getChildren().add(1, statusCircle);
	}

	@Override
	public void setApiKeyField(String value) {
		apiKeyInput.setText(value);
	}

	@Override
	public void showHelpLink(boolean show) {
		helpLink.setVisible(show);
	}

}
