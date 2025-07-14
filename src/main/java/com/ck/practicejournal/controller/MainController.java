package com.ck.practicejournal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ck.practicejournal.model.FocusArea;
import com.ck.practicejournal.model.Goal;
import com.ck.practicejournal.model.PracticeEntry;
import com.ck.practicejournal.service.FocusService;
import com.ck.practicejournal.service.GoalService;
import com.ck.practicejournal.service.PracticeService;
import com.ck.practicejournal.util.DataChangeListener;
import com.ck.practicejournal.view.FocusAreaCellFactory;
import com.ck.practicejournal.view.MainViewInitializer;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements DataChangeListener {

	@FXML
	private Button manageGoalsButton;
	@FXML
	private VBox goalsContainer;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TableView<PracticeEntry> entriesTable;
	@FXML
	private TextField durationField;
	@FXML
	private TextField exerciseField;
	@FXML
	private TextField tempoField;
	@FXML
	private TextField errorField;
	@FXML
	private TextArea notesField;
	@FXML
	private ListView<FocusArea> focusList;

	private final ObservableList<PracticeEntry> entries = FXCollections.observableArrayList();
	private final Map<FocusArea, BooleanProperty> focusSelectionMap = new HashMap<>();

	private final PracticeService practiceService = new PracticeService();
	private final GoalService goalService = new GoalService();
	private final FocusService focusService = new FocusService();
	private final MainViewInitializer viewInitializer = new MainViewInitializer();

	@FXML
	public void initialize() {
		viewInitializer.configureTableColumns(entriesTable);
		focusList.setCellFactory(new FocusAreaCellFactory(focusSelectionMap));

		manageGoalsButton.setOnAction(e -> showGoalsManager());

		LocalDate today = LocalDate.now();
		datePicker.setValue(today);
		datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
			loadEntriesForDate(newDate);
			loadAndDisplayGoals(newDate);
		});

		practiceService.addDataChangeListener(this::refreshData);

		loadAndDisplayGoals(today);
		loadEntriesForDate(today);
		loadFocusAreas();
	}

	private void loadFocusAreas() {
		Platform.runLater(() -> {
			try {
				List<FocusArea> areas = focusService.getAllFocusAreas();
				focusList.getItems().setAll(areas);
				focusSelectionMap.clear();
				for (FocusArea area : areas) {
					focusSelectionMap.put(area, new SimpleBooleanProperty(false));
				}
			} catch (SQLException e) {
				showError("Error loading focuses: " + e.getMessage());
			}
		});
	}

	private void loadEntriesForDate(LocalDate date) {
		try {
			entries.setAll(practiceService.getEntriesForDate(date));
			entriesTable.setItems(entries);
		} catch (SQLException e) {
			showError("Error loading Practice Entries: " + e.getMessage());
		}
	}

	private void loadAndDisplayGoals(LocalDate date) {
		goalsContainer.getChildren().clear();
		try {
			List<Goal> activeGoals = goalService.getActiveGoals(date);
			for (Goal goal : activeGoals) {
				goalsContainer.getChildren().add(createGoalDisplay(goal));
			}
		} catch (SQLException e) {
			showError("Error loading Goals: " + e.getMessage());
		}
	}

	private Node createGoalDisplay(Goal goal) {
		HBox goalBox = new HBox(10);
		goalBox.setPadding(new Insets(5));
		ProgressBar progressBar = new ProgressBar(goal.getCurrentValue() / goal.getTargetValue());
		progressBar.setPrefWidth(200);
		Label label = new Label(String.format("%s: %.0f/%.0f", goal.getDescription(), goal.getCurrentValue(), goal.getTargetValue()));
		goalBox.getChildren().addAll(progressBar, label);
		return goalBox;
	}

	@FXML
	private void showGoalsManager() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ck/practicejournal/goals-view.fxml"));
			Parent root = loader.load();

			GoalOverviewController controller = loader.getController();
			controller.setGoalDao(goalService.getGoalDao());

			Stage stage = new Stage();
			stage.setTitle("Manage Goals");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			loadAndDisplayGoals(datePicker.getValue());
		} catch (IOException e) {
			showError("Error opening Goals-Management: " + e.getMessage());
		}
	}

	@FXML
	private void showFocusManager() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ck/practicejournal/focus-view.fxml"));
			Parent root = loader.load();

			FocusManagerController controller = loader.getController();
			controller.setFocusDao(focusService.getFocusDao());

			Stage stage = new Stage();
			stage.setTitle("Manage Focuses");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			loadFocusAreas();
		} catch (IOException e) {
			showError("Error opening Focus-Management: " + e.getMessage());
		}
	}

	@FXML
	private void handleSaveEntry() {
		try {
			int duration = Integer.parseInt(durationField.getText());
			String exercise = exerciseField.getText();
			int tempo = Integer.parseInt(tempoField.getText());
			int errors = Integer.parseInt(errorField.getText());
			String notes = notesField.getText();
			LocalDate date = datePicker.getValue();

			Set<Integer> seenIds = new HashSet<>();
			List<FocusArea> selectedAreas = focusList.getItems().stream().filter(area -> focusSelectionMap.get(area).get())
					.filter(area -> seenIds.add(area.getId())).toList();

			if (selectedAreas.isEmpty()) {
				showError("Please chose at least one focus.");
				return;
			}

			PracticeEntry entry = new PracticeEntry(date, duration, exercise, tempo, errors, notes);
			entry.getFocusAreas().addAll(selectedAreas);

			practiceService.addEntry(entry);
			resetForm();

		} catch (NumberFormatException e) {
			showError("Please enter numbers for Duration, Tempo and Error Rate.");
		} catch (SQLException e) {
			showError("Save Error: " + e.getMessage());
		}
	}

	private void resetForm() {
		for (BooleanProperty prop : focusSelectionMap.values()) {
			prop.set(false);
		}
		focusList.refresh();
		durationField.clear();
		exerciseField.clear();
		tempoField.clear();
		errorField.clear();
		notesField.clear();
	}

	@FXML
	private void handlePreviousDay() {
		datePicker.setValue(datePicker.getValue().minusDays(1));
	}

	@FXML
	private void handleNextDay() {
		datePicker.setValue(datePicker.getValue().plusDays(1));
	}

	@FXML
	private void handleToday() {
		datePicker.setValue(LocalDate.now());
	}

	private void showError(String message) {
		new Alert(Alert.AlertType.ERROR, message).showAndWait();
	}

	@Override
	public void onDataChanged() {
		Platform.runLater(this::refreshData);
	}

	private void refreshData() {
		loadEntriesForDate(datePicker.getValue());
	}

	public void cleanup() {
		// Optional — falls PracticeService später explizites Entfernen von Listenern
		// anbietet
	}
}
