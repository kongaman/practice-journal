package com.ck.practicejournal.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.ck.practicejournal.dao.GoalDao;
import com.ck.practicejournal.dao.PracticeDao;
import com.ck.practicejournal.model.Goal;
import com.ck.practicejournal.model.PracticeEntry;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

	@FXML private VBox goalsContainer;
	@FXML private DatePicker datePicker;
	@FXML private TableView<PracticeEntry> entriesTable;
	@FXML private TextField durationField;
	@FXML private ComboBox<String> focusCombo;

	private final PracticeDao journalDao = new PracticeDao();
	private final GoalDao goalDao = new GoalDao();
	private final ObservableList<PracticeEntry> entries = FXCollections.observableArrayList();

	@FXML
	public void initialize() {

		focusCombo.getItems().addAll("Akkordwechsel", "Soli", "Rhythmik", "Skalen");

		LocalDate today = LocalDate.now();
		datePicker.setValue(today);

		loadAndDisplayGoals(today);

		loadEntriesForDate(today);

		configureTableColumns();

		datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
			loadEntriesForDate(newDate);
			loadAndDisplayGoals(newDate);
		});
	}

	private void loadAndDisplayGoals(LocalDate date) {
		goalsContainer.getChildren().clear();

		try {
			List<Goal> activeGoals = goalDao.getActiveGoals(date);
			for (Goal goal : activeGoals) {
				goalsContainer.getChildren().add(createGoalDisplay(goal));
			}
		} catch (SQLException e) {
			showError("Fehler beim Laden der Ziele: " + e.getMessage());
		}
	}

	private Node createGoalDisplay(Goal goal) {
		HBox goalBox = new HBox(10);
		goalBox.setPadding(new Insets(5));

		ProgressBar progressBar = new ProgressBar(
				goal.getCurrentValue() / goal.getTargetValue()
				);
		progressBar.setPrefWidth(200);

		Label label = new Label(String.format("%s: %.0f/%.0f",
				goal.getDescription(),
				goal.getCurrentValue(),
				goal.getTargetValue()
				));

		goalBox.getChildren().addAll(progressBar, label);
		return goalBox;
	}

	private void loadEntriesForDate(LocalDate date) {
		try {
			entries.setAll(journalDao.getEntriesByDate(date));
		} catch (SQLException e) {
			showError("Fehler beim Laden der Einträge: " + e.getMessage());
		}
	}

	private void configureTableColumns() {
		TableColumn<PracticeEntry, String> focusCol = new TableColumn<>("Fokus");
		focusCol.setCellValueFactory(cellData ->
		new SimpleStringProperty(cellData.getValue().getFocusArea()));

		TableColumn<PracticeEntry, String> exerciseCol = new TableColumn<>("Übung");
		exerciseCol.setCellValueFactory(cellData ->
		new SimpleStringProperty(cellData.getValue().getExercise()));

		TableColumn<PracticeEntry, Number> durationCol = new TableColumn<>("Dauer (min)");
		durationCol.setCellValueFactory(cellData ->
		new SimpleIntegerProperty(cellData.getValue().getDurationMinutes()));

		entriesTable.getColumns().addAll(focusCol, exerciseCol, durationCol);
		entriesTable.setItems(entries);
	}

	@FXML
	private void handlePreviousDay() {
		LocalDate current = datePicker.getValue();
		datePicker.setValue(current.minusDays(1));
	}

	@FXML
	private void handleNextDay() {
		LocalDate current = datePicker.getValue();
		datePicker.setValue(current.plusDays(1));
	}

	@FXML
	private void handleToday() {
		datePicker.setValue(LocalDate.now());
	}

	@FXML
	private void handleSaveEntry() {
		try {
			int duration = Integer.parseInt(durationField.getText());
			String focus = focusCombo.getValue();

			PracticeEntry entry = new PracticeEntry(
					LocalDate.now(),
					duration,
					focus,
					"Übung", // Temporär
					0,       // Tempo
					0.0,     // Fehlerquote
					""       // Notizen
					);

			journalDao.addEntry(entry);

			durationField.clear();
			focusCombo.getSelectionModel().clearSelection();

		} catch (NumberFormatException e) {
			showError("Ungültige Eingabe. Bitte eine Zahl für die Dauer eingeben");
		} catch (SQLException e) {
			showError("Datenbankfehler. Eintrag konnte nicht gespeichert werden");
		}
	}

	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR, message);
		alert.showAndWait();
	}
}