/**
 * Main.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import application.models.JsonValidationException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Application entry point, creates the single-page GUI, and is an
 * {@link IExceptionHandler} - exceptions should make their way here and then be
 * handled/shown to the user where appropriate; we also use threads, so we use
 * this to allow threads to pass back exception info to be handled nicely.
 * 
 * <pre>
 * 		TODO: Separate out the updating from the setting up of the config (e.g. no deletes when toggling autoupdate)
 * 		TODO: Logging of errors
 * 		TODO: 'Clean all files' button (w/confirmation dialog)
 * 		TODO: Update CellDataTest
 * </pre>
 *
 * @author Mark "Grandy" Bishop
 */
public class Main extends Application implements IExceptionHandler {
	private static final long DISABLE_CONTROL_TIME = 1000L;

	private final FileChooser configChooser = new FileChooser();
	private final Button chooserButton = new Button("Select config");
	private final Text chosenConfigName = new Text();
	private final CheckBox autoUpdateCheck = new CheckBox("Auto update");
	private final Button updateNowButton = new Button("Update now");

	private final ConfigHolder config = new ConfigHolder();
	private final UpdateRunnable runnable = new UpdateRunnable(this);
	private Stage primaryStage;

	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		primaryStage.setTitle("SheeTXT");

		doInit();

		Pane root = doLayout();
		Scene scene = new Scene(root, 300, 120);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Begin update loop
		new Thread(runnable).start();

	}

	private void doInit() {
		configChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
		chooserButton.setOnAction(ev -> {
			try {
				chooseFile();
			} catch (JsonSyntaxException | IOException | JsonValidationException e) {
				handleException(e);
			}
			disableThenReenable(chooserButton);
		});

		autoUpdateCheck.setSelected(true);
		autoUpdateCheck.setOnAction(ev -> {
			config.setAutoUpdate(autoUpdateCheck.isSelected());
			updateNowButton.setDisable(!updateNowButton.isDisabled());
			try {
				if (config.isLoaded()) {
					runnable.updateConfig(config);
				}
			} catch (IOException e) {
				handleException(e);
			}
		});

		updateNowButton.setDisable(true);
		updateNowButton.setOnAction(ev -> {
			runnable.runOnce();
			disableThenReenable(updateNowButton);
		});
	}

	private Pane doLayout() {
		VBox root = new VBox();
		root.getStyleClass().add("root");
		root.setSpacing(3);

		Text configText = new Text("Config file");
		configText.getStyleClass().add("config-file-label");
		root.getChildren().add(configText);

		HBox configBox = new HBox(chooserButton, chosenConfigName);
		configBox.setSpacing(5);
		configBox.setAlignment(Pos.CENTER_LEFT);
		configBox.getStyleClass().add("config-box-layout");
		chosenConfigName.getStyleClass().add("config-name-label");
		chooserButton.getStyleClass().add("choose-config-button");
		root.getChildren().add(configBox);

		Text updateMethodText = new Text("Update method");
		updateMethodText.getStyleClass().add("update-method-label");
		root.getChildren().add(updateMethodText);

		HBox updateBox = new HBox(updateNowButton, autoUpdateCheck);
		updateBox.setSpacing(5);
		updateBox.setAlignment(Pos.CENTER_LEFT);
		updateBox.getStyleClass().add("update-box-layout");
		updateNowButton.getStyleClass().add("update-now-button");
		autoUpdateCheck.getStyleClass().add("auto-update-checkbox");
		root.getChildren().add(updateBox);

		return root;
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void chooseFile() throws JsonSyntaxException, IOException, JsonValidationException {
		File file = configChooser.showOpenDialog(primaryStage);
		if (file == null) {
			return;
		}

		// Remember the directory you used
		configChooser.setInitialDirectory(file.getParentFile());

		// Update labels and config
		chosenConfigName.setText(file.getName());
		config.loadFile(file);
		runnable.updateConfig(config);
	}

	private void disableThenReenable(Control ctrl) {
		new Thread(() -> {
			try {
				ctrl.setDisable(true);
				Thread.sleep(DISABLE_CONTROL_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				ctrl.setDisable(false);
			}
		}).start();
	}

	@Override
	public void handleException(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void stop() {
		System.out.println("Stage is closing");
		// Shut down the thread
		runnable.doStop();
	}
}
