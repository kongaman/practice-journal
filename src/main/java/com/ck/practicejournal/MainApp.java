package com.ck.practicejournal;

import java.io.IOException;

import com.ck.practicejournal.util.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		DatabaseManager.initializeDatabase();

		FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("main-view.fxml"));

		Scene scene = new Scene(fxmlLoader.load(), 600, 400);

		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		stage.setTitle("Practice Journal");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}

}
