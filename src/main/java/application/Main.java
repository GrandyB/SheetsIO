/**
 * Main.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonSyntaxException;

import application.exceptions.IllegalFileExtensionException;
import application.exceptions.JsonValidationException;
import application.threads.UpdateRunnable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
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
 * @author Mark "Grandy" Bishop
 */
public class Main extends Application implements IExceptionHandler {
	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	private static final long DISABLE_CONTROL_TIME = 1000L;
	private static final int MAX_EXCEPTION_STACK_LINES = 10;

	private final FileChooser configChooser = new FileChooser();
	private final Button chooserButton = new Button("Select config");
	private final Hyperlink reloadConfigLink = new Hyperlink("Reload");
	private final Text chosenConfigName = new Text();
	private final CheckBox autoUpdateCheck = new CheckBox("Auto update");
	private final Button updateNowButton = new Button("Update now");

	private final ConfigHolder config = new ConfigHolder();
	private final UpdateRunnable runnable = new UpdateRunnable(this);
	private Stage primaryStage;

	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		primaryStage.setTitle("SheetsIO");

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
		FileIO fileIO = new FileIO();
		try {
			fileIO.createFolder("logs");
		} catch (IOException e) {
			handleException(e);
		}

		configChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
		chooserButton.setOnAction(ev -> {
			try {
				disableThenReenable(chooserButton);
				chooseFile();
			} catch (JsonSyntaxException | IOException | JsonValidationException | IllegalFileExtensionException e) {
				handleException(e);
			}
		});

		reloadConfigLink.setOnAction(ev -> {
			if (config.isLoaded()) {
				try {
					/*
					 * Reload backing config file, set it onto the thread, clearing and re-adding
					 * files into the relevant folder (empty).
					 */
					disableThenReenable(reloadConfigLink);
					config.reload();
					runnable.updateConfig(config, true);
				} catch (Exception e) {
					handleException(e);
				}
			}
		});

		autoUpdateCheck.setSelected(false);
		autoUpdateCheck.setOnAction(ev -> {
			config.setAutoUpdate(autoUpdateCheck.isSelected());
			updateNowButton.setDisable(!updateNowButton.isDisabled());
			try {
				if (config.isLoaded()) {
					runnable.updateConfig(config, false);
				}
			} catch (IOException | IllegalFileExtensionException e) {
				handleException(e);
			}
		});

		updateNowButton.setDisable(false);
		updateNowButton.setOnAction(ev -> {
			disableThenReenable(updateNowButton);
			runnable.runOnce();
		});
	}

	private Pane doLayout() {
		VBox root = new VBox();
		root.getStyleClass().add("root");
		root.setSpacing(3);

		Text configText = new Text("Config file");
		configText.getStyleClass().add("config-file-label");
		reloadConfigLink.setVisible(false);
		reloadConfigLink.getStyleClass().add("config-reload-link");
		HBox configLabelLayout = new HBox(configText, reloadConfigLink);
		root.getChildren().add(configLabelLayout);

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
		LOGGER.info("____________________");
		LOGGER.info("Starting up...");
		LOGGER.info("     _____");
		LOGGER.info("   .'     `.");
		LOGGER.info("  /  .-=-.  \\  \\  __");
		LOGGER.info("  | (  C\\ \\  \\_.'')");
		LOGGER.info(" _\\  `--' |,'   _/");
		LOGGER.info("/__`.____.'__.-'");
		LOGGER.info("____________________");

		launch(args);
	}

	private void chooseFile()
			throws JsonSyntaxException, IOException, JsonValidationException, IllegalFileExtensionException {
		File file = configChooser.showOpenDialog(primaryStage);
		if (file == null) {
			return;
		}

		// Remember the directory you used
		configChooser.setInitialDirectory(file.getParentFile());

		// Update config
		config.loadFile(file);
		runnable.updateConfig(config, true);

		chosenConfigName.setText(file.getName());
		reloadConfigLink.setVisible(true);
		autoUpdateCheck.setSelected(config.isAutoUpdate());
	}

	private void disableThenReenable(Control ctrl) {
		new Thread(() -> {
			try {
				ctrl.setDisable(true);
				Thread.sleep(DISABLE_CONTROL_TIME);
			} catch (InterruptedException e) {
				handleException(e);
			} finally {
				ctrl.setDisable(false);
			}
		}).start();
	}

	@Override
	public void handleException(Exception e) {
		StringBuilder error = new StringBuilder();
		if (e instanceof JsonValidationException) {
			error.append("Error while attempting to load config values into the application.\n");
			JsonValidationException jsonEx = (JsonValidationException) e;
			jsonEx.getViolations().forEach(v -> {
				error.append(v.getMessage());
				error.append('\n');
			});
		} else if (e instanceof JsonSyntaxException) {
			error.append("Your json is malformed and needs correcting!\n");
			error.append(e.getMessage());
			error.append(
					"\n\nCheck the line/column numbers in the error above for hints on where your json is failing.\nIf that doesn't help, consider running your config through a validation service such as https://jsonlint.com/ - removing your apiKey first of course!\n");
		} else {
			StackTraceElement[] stack = e.getStackTrace();
			// If stack smaller than preset length, use that; otherwise limit to defined max
			for (int i = 0; i < (stack.length > MAX_EXCEPTION_STACK_LINES ? MAX_EXCEPTION_STACK_LINES
					: stack.length); i++) {
				error.append(stack[i].toString());
				error.append('\n');
			}
			error.append("...\n");
		}

		error.append(
				"\nIf unable to fix locally, please raise an issue with today's log file (in /logs) and any details on how to reproduce at https://github.com/GrandyB/SheetsIO/issues");

		// Remove all instances of the user's API key
		String sanitisedMessage = ConfigHolder.sanitiseApiKey(config, e.getLocalizedMessage());
		LOGGER.error(sanitisedMessage);
		String errorMessage = ConfigHolder.sanitiseApiKey(config, error.toString());
		LOGGER.error(errorMessage);

		/*
		 * Exceptions can be thrown within our {@link UpdateRunnable} thread and beyond,
		 * which is separate to the JavaFX application thread; Platform.runLater allows
		 * the code to be ran on the proper thread.
		 */
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("SheetsIO error");
			alert.setHeaderText(sanitisedMessage);
			alert.setContentText(errorMessage);
			alert.showAndWait();
		});
	}

	@Override
	public void stop() {
		LOGGER.info("Stage is closing");
		// Shut down the thread
		runnable.doStop();
	}
}
