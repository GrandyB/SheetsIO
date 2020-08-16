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
import org.greenrobot.eventbus.EventBus;

import application.guis.MainGui;
import application.models.PropertiesHolder;
import application.services.ThreadCollector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

	@Getter
	private EventBus eventBus = new EventBus();

	@Override
	public void start(Stage stage) throws IOException {
		this.primaryStage = stage;
		primaryStage.setTitle("SheetsIO");

		MainGui mainGui = new MainGui(this);

		Scene mainScene = new Scene(mainGui, PropertiesHolder.SCENE_WIDTH, PropertiesHolder.SCENE_HEIGHT);
		mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	@Override
	public void openBrowser(String url) {
		getHostServices().showDocument(url);
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
