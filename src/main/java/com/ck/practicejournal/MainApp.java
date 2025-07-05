package com.ck.practicejournal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Label label = new Label("Willkommen zum Gitarren-Tagebuch!");
		StackPane root = new StackPane(label);

		Scene scene = new Scene(root, 600, 400);

		primaryStage.setTitle("Gitarren Übungstagebuch");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
