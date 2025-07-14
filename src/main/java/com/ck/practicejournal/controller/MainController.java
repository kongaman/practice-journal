package com.ck.practicejournal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ck.practicejournal.dao.FocusDao;
import com.ck.practicejournal.dao.GoalDao;
import com.ck.practicejournal.dao.PracticeDao;
import com.ck.practicejournal.model.FocusArea;
import com.ck.practicejournal.model.Goal;
import com.ck.practicejournal.model.PracticeEntry;
import com.ck.practicejournal.util.DataChangeListener;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
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

	private final ObservableList<FocusArea> availableFocusAreas = FXCollections.observableArrayList();
	private final ObservableList<FocusArea> selectedFocusAreas = FXCollections.observableArrayList();
	private final ObservableList<PracticeEntry> entries = FXCollections.observableArrayList();
	private final Map<FocusArea, BooleanProperty> focusSelectionMap = new HashMap<>();
	private final PracticeDao journalDao = new PracticeDao();
	private final GoalDao goalDao = new GoalDao();
	private final FocusDao focusDao = new FocusDao();

	@FXML
	public void initialize() {

		manageGoalsButton.setOnAction(e -> showGoalsManager());
		LocalDate today = LocalDate.now();
		datePicker.setValue(today);
		datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
			loadEntriesForDate(newDate);
			loadAndDisplayGoals(newDate);
		});
		journalDao.addDataChangeListener(this);
		loadAndDisplayGoals(today);
		loadEntriesForDate(today);
		configureTableColumns();
		refreshData();
		focusList.setCellFactory(lv -> new ListCell<FocusArea>() {
			private final CheckBox checkBox = new CheckBox();
			private BooleanProperty currentSelectedProperty = null;

			{
				// Listener für CheckBox-Änderungen
				checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
					FocusArea item = getItem();
					if (item != null && currentSelectedProperty != null) {
						currentSelectedProperty.set(newVal);
					}
				});
			}

			@Override
			protected void updateItem(FocusArea item, boolean empty) {
				super.updateItem(item, empty);

				// Alte Bindung entfernen
				if (currentSelectedProperty != null) {
					checkBox.selectedProperty().unbindBidirectional(currentSelectedProperty);
					currentSelectedProperty = null;
				}

				if (empty || item == null) {
					setGraphic(null);
				} else {
					// Neue Bindung erstellen
					currentSelectedProperty = focusSelectionMap.get(item);
					if (currentSelectedProperty != null) {
						checkBox.selectedProperty().bindBidirectional(currentSelectedProperty);
					}

					checkBox.setText(item.getName());
					setGraphic(checkBox);
				}
			}
		});
		loadFocusAreas();
	}

	@FXML
	private void showGoalsManager() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ck/practicejournal/goals-view.fxml"));
			Parent root = loader.load();

			GoalOverviewController controller = loader.getController();
			controller.setGoalDao(goalDao);

			Stage stage = new Stage();
			stage.setTitle("Ziele verwalten");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			loadAndDisplayGoals(datePicker.getValue());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void showFocusManager() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ck/practicejournal/focus-view.fxml"));
			Parent root = loader.load();

			FocusManagerController controller = loader.getController();
			controller.setFocusDao(focusDao); // Du musst FocusDao in MainController injizieren

			Stage stage = new Stage();
			stage.setTitle("Manage Focus Areas");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			loadFocusAreas();
		} catch (IOException e) {
			showError("Could not open focus manager: " + e.getMessage());
		}
	}

	private void loadFocusAreas() {
		Platform.runLater(() -> {
			try {
				List<FocusArea> areas = focusDao.getAllFocusAreas();
				focusList.getItems().setAll(areas);

				focusSelectionMap.clear();
				for (FocusArea area : areas) {
					focusSelectionMap.put(area, new SimpleBooleanProperty(false));
				}
			} catch (SQLException e) {
				showError("Error loading focus areas: " + e.getMessage());
			}
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
		ProgressBar progressBar = new ProgressBar(goal.getCurrentValue() / goal.getTargetValue());
		progressBar.setPrefWidth(200);
		Label label = new Label(String.format("%s: %.0f/%.0f", goal.getDescription(), goal.getCurrentValue(), goal.getTargetValue()));
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
		TableColumn<PracticeEntry, String> focusCol = new TableColumn<>("Fokusbereiche");
		focusCol.setCellValueFactory(cellData -> {
			String focusNames = cellData.getValue().getFocusAreas().stream().map(FocusArea::getName).collect(Collectors.joining(", "));
			return new SimpleStringProperty(focusNames);
		});
		TableColumn<PracticeEntry, String> exerciseCol = new TableColumn<>("Übung");
		exerciseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExercise()));
		TableColumn<PracticeEntry, Number> durationCol = new TableColumn<>("Dauer (min)");
		durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDurationMinutes()));
		TableColumn<PracticeEntry, Number> tempoCol = new TableColumn<>("BPM");
		tempoCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTempoBpm()));
		TableColumn<PracticeEntry, Number> errorCol = new TableColumn<>("Fehlerquote");
		errorCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getErrorRate()));
		TableColumn<PracticeEntry, String> notesCol = new TableColumn<>("Notizen");
		notesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));
		entriesTable.getColumns().addAll(focusCol, exerciseCol, durationCol, tempoCol, errorCol, notesCol);
		entriesTable.setItems(entries);
	}

	private void refreshData() {
		try {
			LocalDate currentDate = datePicker.getValue();
			entries.setAll(journalDao.getEntriesByDate(currentDate));
		} catch (SQLException e) {
			e.printStackTrace();
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
			ObservableList<FocusArea> selectedFocusAreas = getSelectedFocusAreas();
			for (FocusArea area : focusList.getItems()) {
				if (focusSelectionMap.containsKey(area) && focusSelectionMap.get(area).get()) {
					selectedFocusAreas.add(area);
				}
			}

			if (selectedFocusAreas.isEmpty()) {
				showError("Bitte mindestens einen Fokusbereich auswählen");
				return;
			}

			PracticeEntry entry = new PracticeEntry(date, duration, exercise, tempo, errors, notes);
			entry.getFocusAreas().addAll(selectedFocusAreas);

			journalDao.addEntry(entry);

			resetForm();

		} catch (NumberFormatException e) {
			showError("Invalid input. Please enter numbers for duration, tempo and error rate.");
		} catch (SQLException e) {
			System.out.println(e);
			showError("Database error. Could not save entry: " + e.getMessage());
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

	private ObservableList<FocusArea> getSelectedFocusAreas() {
		return focusList.getItems().stream().filter(area -> {
			BooleanProperty prop = focusSelectionMap.get(area);
			return prop != null && prop.get();
		}).collect(Collectors.toCollection(FXCollections::observableArrayList));
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

	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR, message);
		alert.showAndWait();
	}

	@Override
	public void onDataChanged() {
		Platform.runLater(this::refreshData);
	}

	public void cleanup() {
		journalDao.removeDataChangeListener(this);
	}
}