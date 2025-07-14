package com.ck.practicejournal.view;

import java.util.stream.Collectors;

import com.ck.practicejournal.model.FocusArea;
import com.ck.practicejournal.model.PracticeEntry;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainViewInitializer {

	public void configureTableColumns(TableView<PracticeEntry> tableView) {

		TableColumn<PracticeEntry, String> exerciseCol = new TableColumn<>("Übung");
		exerciseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExercise()));

		TableColumn<PracticeEntry, String> focusCol = new TableColumn<>("Fokusbereiche");
		focusCol.setCellValueFactory(cellData -> {
			String focusNames = cellData.getValue().getFocusAreas().stream().map(FocusArea::getName).collect(Collectors.joining(", "));
			return new SimpleStringProperty(focusNames);
		});

		TableColumn<PracticeEntry, Number> durationCol = new TableColumn<>("Dauer (min)");
		durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDurationMinutes()));

		TableColumn<PracticeEntry, Number> tempoCol = new TableColumn<>("BPM");
		tempoCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTempoBpm()));

		TableColumn<PracticeEntry, Number> errorCol = new TableColumn<>("Fehlerquote");
		errorCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getErrorRate()));

		TableColumn<PracticeEntry, String> notesCol = new TableColumn<>("Notizen");
		notesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));

		tableView.getColumns().addAll(exerciseCol, focusCol, durationCol, tempoCol, errorCol, notesCol);
	}
}
