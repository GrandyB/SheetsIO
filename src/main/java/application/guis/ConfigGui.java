/**
 * ConfigGui.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.models.PropertiesHolder;
import application.panels.ConfigPanel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

/**
 * Gui for the Config chooser and other related UI elements surrounding it.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ConfigGui extends BaseGui<ConfigPanel, ConfigPanel.Gui, VBox> implements ConfigPanel.Gui {

	private final FileChooser configChooser = new FileChooser();
	private final Button chooserButton = new Button("Browse");
	private final Hyperlink reloadConfigLink = new Hyperlink("Reload config");
	private final Text chosenConfigName = new Text();
	private final CheckBox autoUpdateCheck = new CheckBox("Auto update");
	private final Button updateNowButton = new Button("Update now");

	@Autowired
	public ConfigGui(ConfigPanel panel) {
		super(panel);
	}

	@Override
	public void setUp() {
		// File "suggestion" -> json config files
		configChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

		chooserButton.setOnAction((a) -> {
			disableThenReenable(chooserButton);
			getPanel().handleConfigSelection(configChooser.showOpenDialog(getPanel().getApp().getPrimaryStage()));
		});

		reloadConfigLink.setOnAction(ev -> {
			disableThenReenable(reloadConfigLink);
			getPanel().handleReloadLinkClick();
		});

		autoUpdateCheck.setSelected(false);
		autoUpdateCheck.setOnAction(ev -> {
			getPanel().handleAutoUpdateCheck(autoUpdateCheck.isSelected());
		});

		updateNowButton.setDisable(false);
		updateNowButton.setOnAction(ev -> {
			disableThenReenable(updateNowButton);
			getPanel().handleUpdateNowPress();
		});
	}

	@Override
	public void doLayout() {
		getRoot().setSpacing(PropertiesHolder.LAYOUT_SPACING);

		// Config + reload link
		Text configText = new Text("Config file");
		configText.getStyleClass().add("bold-text");

		chosenConfigName.getStyleClass().add("config-name-label");
		HBox configNameLayout = new HBox(chosenConfigName);
		configNameLayout.getStyleClass().add("config-name-layout");
		configNameLayout.setPrefWidth(180);
		configNameLayout.setPrefHeight(20);

		HBox configButtons = new HBox(chooserButton, reloadConfigLink);
		reloadConfigLink.setVisible(false);
		reloadConfigLink.getStyleClass().add("config-reload-link");
		chooserButton.getStyleClass().add("choose-config-button");

		VBox configLayout = new VBox(configText, configNameLayout, configButtons);
		configLayout.setSpacing(PropertiesHolder.INTERNAL_SPACING);
		getLayout().add(configLayout);

		// Update now + autoupdate checkbox
		Text updateMethodText = new Text("Update method");
		updateMethodText.getStyleClass().add("bold-text");

		HBox updateBox = new HBox(updateNowButton, autoUpdateCheck);
		updateBox.setSpacing(5);
		updateBox.setAlignment(Pos.CENTER_LEFT);
		updateBox.getStyleClass().add("update-box-layout");
		updateNowButton.getStyleClass().add("update-now-button");
		autoUpdateCheck.getStyleClass().add("auto-update-checkbox");

		VBox updateLayout = new VBox(updateMethodText, updateBox);
		updateLayout.setSpacing(PropertiesHolder.INTERNAL_SPACING);
		getLayout().add(updateLayout);
	}

	@Override
	public void setConfigChooserDirectory(File file) {
		configChooser.setInitialDirectory(file);
	}

	@Override
	public void setConfigLabel(String label) {
		chosenConfigName.setText(label);
	}

	@Override
	public void setReloadConfigLinkVisible(boolean visible) {
		reloadConfigLink.setVisible(visible);
	}

	@Override
	public void setAutoUpdateCheckState(boolean checked) {
		autoUpdateCheck.setSelected(checked);
	}

	@Override
	public void setUpdateNowButtonEnabled(boolean enabled) {
		updateNowButton.setDisable(!enabled);
	}

}
