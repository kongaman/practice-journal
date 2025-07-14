package com.ck.practicejournal.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FocusArea {
	private final IntegerProperty id = new SimpleIntegerProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final IntegerProperty usageCount = new SimpleIntegerProperty();

	public FocusArea() {
	}

	public FocusArea(int id, String name, int usageCount) {
		setId(id);
		setName(name);
		setUsageCount(usageCount);
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

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public IntegerProperty usageCountProperty() {
		return usageCount;
	}

	public int getUsageCount() {
		return usageCount.get();
	}

	public void setUsageCount(int count) {
		usageCount.set(count);
	}

}