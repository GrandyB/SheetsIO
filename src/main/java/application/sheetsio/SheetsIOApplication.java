package application.sheetsio;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@SpringBootApplication
public class SheetsIOApplication extends javafx.application.Application {

	@Override
	public void init() throws Exception {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(SheetsIOApplication.class);
		builder.application().setWebApplicationType(WebApplicationType.NONE);
		builder.build();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("SheetsIO");
		StackPane root = new StackPane();
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		root.getChildren().add(btn);
		Scene mainScene = new Scene(root, 800, 600);
		// mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setResizable(false);
		// primaryStage.getIcons().add(new
		// Image(getClass().getResourceAsStream("icon.png")));
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

}
