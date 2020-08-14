/**
 * MainView.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import application.IApplicationOps;
import application.models.ApiKeyStatus;
import application.models.PropertiesHolder;
import application.panels.MainPanel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * GUI for main bulk of the app, comprised of a {@link ConfigGui} and a
 * {@link TimerGui}.
 *
 * @author Mark "Grandy" Bishop
 */
public class MainGui extends BaseGui<MainPanel, MainPanel.Gui, VBox> implements MainPanel.Gui {

	public MainGui(IApplicationOps app) {
		super(app, new MainPanel(), new VBox(3));
		getPanel().initialise();
	}

	@Override
	protected void doLayout() {
		getRoot().getStyleClass().add("root");
		getRoot().setSpacing(10);

		ApiKeyStatus status = PropertiesHolder.get().getStatus();

		Text apiKeyText = new Text("API status");
		apiKeyText.getStyleClass().add("bold-text");
		HBox apiKeyTextAndIndicator = new HBox(apiKeyText, ApiKeyStatus.getCircle(status));
		apiKeyTextAndIndicator.setAlignment(Pos.CENTER_LEFT);
		apiKeyTextAndIndicator.setSpacing(5);
		getLayout().add(apiKeyTextAndIndicator);

		switch (status) {
		case LOADED:
			createAndAdd();
			break;
		case INCOMPLETE:
		case MISSING:
			Label apiKeyLabel = new Label("Enter API key:");
			TextField apiKeyInput = new TextField();
			VBox vb = new VBox(apiKeyLabel, apiKeyInput);
			vb.setSpacing(5);
			HBox buttonLayout = new HBox();
			getLayout().add(vb);
			break;
		default:
			throw new IllegalArgumentException(
					String.format("Unable to handle %s '%s'", ApiKeyStatus.class.getSimpleName(), status));

		}
	}

	private void createAndAdd() {
		ConfigGui configGui = new ConfigGui(getApp());
		getRoot().getChildren().add(configGui);

		TimerGui timer = new TimerGui(getApp());
		getRoot().getChildren().add(timer);
	}

}
