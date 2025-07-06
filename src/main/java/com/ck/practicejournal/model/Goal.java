package com.ck.practicejournal.model;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Goal {
	private final IntegerProperty id = new SimpleIntegerProperty();
	private final ObjectProperty<GoalType> type = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
	private final StringProperty description = new SimpleStringProperty();
	private final DoubleProperty targetValue = new SimpleDoubleProperty();
	private final DoubleProperty currentValue = new SimpleDoubleProperty();
	private final BooleanProperty achieved = new SimpleBooleanProperty();

	public Goal() {
	}

	public Goal(int id, GoalType type, LocalDate startDate,
			String description, double targetValue,
			double currentValue, boolean achieved) {
		setId(id);
		setType(type);
		setStartDate(startDate);
		setDescription(description);
		setTargetValue(targetValue);
		setCurrentValue(currentValue);
		setAchieved(achieved);
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

	public ObjectProperty<GoalType> typeProperty() {
		return type;
	}

	public GoalType getType() {
		return type.get();
	}

	public void setType(GoalType type) {
		this.type.set(type);
	}

	public ObjectProperty<LocalDate> startDateProperty() {
		return startDate;
	}

	public LocalDate getStartDate() {
		return startDate.get();
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate.set(startDate);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public DoubleProperty targetValueProperty() {
		return targetValue;
	}

	public double getTargetValue() {
		return targetValue.get();
	}

	public void setTargetValue(double targetValue) {
		this.targetValue.set(targetValue);
	}

	public DoubleProperty currentValueProperty() {
		return currentValue;
	}

	public double getCurrentValue() {
		return currentValue.get();
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue.set(currentValue);
	}

	public BooleanProperty achievedProperty() {
		return achieved;
	}

	public boolean isAchieved() {
		return achieved.get();
	}

	public void setAchieved(boolean achieved) {
		this.achieved.set(achieved);
	}

	public double getProgressPercentage() {
		if (getTargetValue() == 0) {
			return 0;
		}
		return getCurrentValue() / getTargetValue();
	}

	@Override
	public String toString() {
		return String.format("%s: %.0f/%.0f", getDescription(), getCurrentValue(), getTargetValue());
	}
}