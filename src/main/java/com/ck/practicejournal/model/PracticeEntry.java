package com.ck.practicejournal.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PracticeEntry {
	private IntegerProperty id = new SimpleIntegerProperty();
	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private final IntegerProperty durationMinutes = new SimpleIntegerProperty();
	private final StringProperty exercise = new SimpleStringProperty();
	private final IntegerProperty tempoBpm = new SimpleIntegerProperty();
	private final IntegerProperty errorRate = new SimpleIntegerProperty();
	private final StringProperty notes = new SimpleStringProperty();
	private final ObservableList<FocusArea> focusAreas = FXCollections.observableArrayList();

	public PracticeEntry() {
	}

	public PracticeEntry(LocalDate date, int durationMinutes, String exercise, int tempoBPM, int errorRate, String notes) {
		setDate(date);
		setDurationMinutes(durationMinutes);
		setExercise(exercise);
		setTempoBpm(tempoBPM);
		setErrorRate(errorRate);
		setNotes(notes);
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public ObservableList<FocusArea> getFocusAreas() {
		return focusAreas;
	}

	public ObjectProperty<LocalDate> dateProperty() {
		return date;
	}

	public LocalDate getDate() {
		return date.get();
	}

	public void setDate(LocalDate date) {
		this.date.set(date);
	}

	public IntegerProperty durationMinutesProperty() {
		return durationMinutes;
	}

	public int getDurationMinutes() {
		return durationMinutes.get();
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes.set(durationMinutes);
	}

	public StringProperty exerciseProperty() {
		return exercise;
	}

	public String getExercise() {
		return exercise.get();
	}

	public void setExercise(String exercise) {
		this.exercise.set(exercise);
	}

	public IntegerProperty tempoBpmProperty() {
		return tempoBpm;
	}

	public int getTempoBpm() {
		return tempoBpm.get();
	}

	public void setTempoBpm(int tempoBpm) {
		this.tempoBpm.set(tempoBpm);
	}

	public IntegerProperty errorRateProperty() {
		return errorRate;
	}

	public int getErrorRate() {
		return errorRate.get();
	}

	public void setErrorRate(int errorRate) {
		this.errorRate.set(errorRate);
	}

	public StringProperty notesProperty() {
		return notes;
	}

	public String getNotes() {
		return notes.get();
	}

	public void setNotes(String notes) {
		this.notes.set(notes);
	}

	@Override
	public String toString() {
		return "PracticeEntry [id=" + id + ", date=" + date + ", durationMinutes=" + durationMinutes + ", exercise=" + exercise + ", tempoBpm="
				+ tempoBpm + ", errorRate=" + errorRate + ", notes=" + notes + ", focusAreas=" + focusAreas + "]";
	}

}
