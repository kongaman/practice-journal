package com.ck.practicejournal.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.ck.practicejournal.dao.GoalDao;
import com.ck.practicejournal.model.Goal;
import com.ck.practicejournal.util.AlertUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GoalOverviewController {

	@FXML
	private TableView<Goal> goalsTable;

	private GoalDao goalDao;

	public void setGoalDao(GoalDao goalDao) {
		this.goalDao = goalDao;
		refreshGoals();
	}

	@FXML
	public void initialize() {
		// Spalten werden über FXML via PropertyValueFactory gesetzt
	}

	@FXML
	private void handleNewGoal() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ck/practicejournal/goals-edit.fxml"));
			Parent root = loader.load();

			GoalEditController controller = loader.getController();
			controller.setGoalDao(goalDao);
			controller.prepareForNewGoal();
			controller.setOnSaveCallback(this::refreshGoals);

			Stage stage = new Stage();
			stage.setTitle("Neues Ziel");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

		} catch (IOException e) {
			AlertUtils.showError("Error", "Could not open Window: " + e.getMessage());
		}
	}

	@FXML
	private void handleEditSelected() {
		Goal selected = goalsTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ck/practicejournal/goals-edit.fxml"));
				Parent root = loader.load();

				GoalEditController controller = loader.getController();
				controller.setGoalDao(goalDao);
				controller.setGoalForEditing(selected);
				controller.setOnSaveCallback(this::refreshGoals);

				Stage stage = new Stage();
				stage.setTitle("Ziel bearbeiten");
				stage.setScene(new Scene(root));
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();

			} catch (IOException e) {
				AlertUtils.showError("Error", "Could not open Window: " + e.getMessage());
			}
		}
	}

	@FXML
	private void handleDeleteSelected() {
		Goal selected = goalsTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			try {
				goalDao.deleteGoal(selected.getId());
				refreshGoals();
			} catch (SQLException e) {
				AlertUtils.showError("Error", "Delete failed: " + e.getMessage());
			}
		}
	}

	@FXML
	private void handleClose() {
		goalsTable.getScene().getWindow().hide();
	}

	private void refreshGoals() {
		try {
			goalsTable.getItems().setAll(goalDao.getAllGoals());
		} catch (SQLException e) {
			AlertUtils.showError("Error", "Load Goals failed: " + e.getMessage());
		}
	}
}
