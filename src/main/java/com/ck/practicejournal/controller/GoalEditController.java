package com.ck.practicejournal.controller;

import com.ck.practicejournal.dao.GoalDao;
import com.ck.practicejournal.model.Goal;
import com.ck.practicejournal.model.GoalType;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class GoalEditController {

	@FXML
	private ComboBox<GoalType> typeCombo;
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private TextField descriptionField;
	@FXML
	private TextField targetValueField;

	private GoalDao goalDao;
	private Goal currentGoal;
	private Runnable onSaveCallback;

	public void setGoalDao(GoalDao goalDao) {
		this.goalDao = goalDao;
	}

	public void setOnSaveCallback(Runnable callback) {
		onSaveCallback = callback;
	}

	@FXML
	public void initialize() {
		typeCombo.getItems().addAll(GoalType.values());
	}

	public void setGoalForEditing(Goal goal) {
		currentGoal = goal;
		typeCombo.setValue(goal.getType());
		startDatePicker.setValue(goal.getStartDate());
		descriptionField.setText(goal.getDescription());
		targetValueField.setText(String.valueOf(goal.getTargetValue()));
	}

	public void prepareForNewGoal() {
		currentGoal = null;
		typeCombo.getSelectionModel().clearSelection();
		startDatePicker.setValue(null);
		descriptionField.clear();
		targetValueField.clear();
	}

	@FXML
	private void handleSaveGoal() {
		try {
			Goal goal = currentGoal != null ? currentGoal : new Goal();

			goal.setType(typeCombo.getValue());
			goal.setStartDate(startDatePicker.getValue());
			goal.setDescription(descriptionField.getText());
			goal.setTargetValue(Double.parseDouble(targetValueField.getText()));

			if (currentGoal == null) {
				goalDao.createGoal(goal);
			} else {
				goalDao.updateGoal(goal);
			}

			if (onSaveCallback != null) {
				onSaveCallback.run();
			}

			closeWindow();

		} catch (Exception e) {
			showAlert("Fehler", "Ungültige Eingabe: " + e.getMessage());
		}
	}

	@FXML
	private void handleCancel() {
		closeWindow();
	}

	private void closeWindow() {
		descriptionField.getScene().getWindow().hide();
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
