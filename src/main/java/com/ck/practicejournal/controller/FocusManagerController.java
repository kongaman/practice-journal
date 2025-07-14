package com.ck.practicejournal.controller;

import java.sql.SQLException;
import java.util.Optional;

import com.ck.practicejournal.dao.FocusDao;
import com.ck.practicejournal.model.FocusArea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class FocusManagerController {

	@FXML
	private TableView<FocusArea> focusTable;
	@FXML
	private TextField newFocusField;

	private FocusDao focusDao;
	private final ObservableList<FocusArea> focusAreas = FXCollections.observableArrayList();

	public void setFocusDao(FocusDao focusDao) {
		this.focusDao = focusDao;
		refreshFocusAreas();
	}

	@FXML
	public void initialize() {
		focusTable.setItems(focusAreas);
	}

	@FXML
	private void handleAddFocus() {
		String name = newFocusField.getText().trim();
		if (!name.isEmpty()) {
			try {
				FocusArea newFocus = new FocusArea();
				newFocus.setName(name);
				focusDao.createFocusArea(newFocus);
				refreshFocusAreas();
				newFocusField.clear();
			} catch (SQLException e) {
				showAlert("Database Error", "Could not add focus area: " + e.getMessage());
			}
		}
	}

	@FXML
	private void handleEditSelected() {
		FocusArea selected = focusTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			TextInputDialog dialog = new TextInputDialog(selected.getName());
			dialog.setTitle("Edit Focus Area");
			dialog.setHeaderText("Edit Focus Area Name");
			dialog.setContentText("New name:");

			Optional<String> result = dialog.showAndWait();
			result.ifPresent(newName -> {
				try {
					selected.setName(newName);
					focusDao.updateFocusArea(selected);
					refreshFocusAreas();
				} catch (SQLException e) {
					showAlert("Update Error", "Could not update focus area: " + e.getMessage());
				}
			});
		}
	}

	@FXML
	private void handleDeleteSelected() {
		FocusArea selected = focusTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setTitle("Confirm Deletion");
			confirm.setHeaderText("Delete Focus Area");
			confirm.setContentText("Are you sure you want to delete '" + selected.getName() + "'?");

			Optional<ButtonType> result = confirm.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				try {
					focusDao.deleteFocusArea(selected.getId());
					refreshFocusAreas();
				} catch (SQLException e) {
					showAlert("Deletion Error", "Could not delete focus area: " + e.getMessage());
				}
			}
		}
	}

	@FXML
	private void handleClose() {
		((Stage) focusTable.getScene().getWindow()).close();
	}

	private void refreshFocusAreas() {
		try {
			focusAreas.setAll(focusDao.getAllFocusAreas());
		} catch (SQLException e) {
			showAlert("Load Error", "Could not load focus areas: " + e.getMessage());
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}