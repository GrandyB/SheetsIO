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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.guis.ConfigGui;
import application.guis.TimerGui;
import application.services.ThreadCollector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * Application entry point, creates the overall window.
 *
 * @author Mark "Grandy" Bishop
 */
public class Main extends Application implements IApplicationOps {
	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	@Getter
	private Stage primaryStage;

	@Override
	public void start(Stage stage) throws IOException {
		this.primaryStage = stage;
		primaryStage.setTitle("SheetsIO");

		Pane root = doLayout();
		Scene scene = new Scene(root, 190, 270);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Pane doLayout() {
		VBox root = new VBox(3);
		root.getStyleClass().add("root");
		root.setSpacing(10);

		ConfigGui configGui = new ConfigGui(this);
		root.getChildren().add(configGui);

		TimerGui timer = new TimerGui(this);
		root.getChildren().add(timer);
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

	@Override
	public void stop() {
		LOGGER.info("Stage is closing");
		// Shut down the threads
		ThreadCollector.stopAllThreads();
	}
}
