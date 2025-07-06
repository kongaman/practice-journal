package com.ck.practicejournal.model;

public enum GoalType {
	WEEKLY("Wöchentlich"),
	MONTHLY("Monatlich"),
	QUARTERLY("Quartalsweise"),
	SEMI_ANNUAL("Halbjährlich"),
	ANNUAL("Jährlich");

	private final String displayName;

	GoalType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
