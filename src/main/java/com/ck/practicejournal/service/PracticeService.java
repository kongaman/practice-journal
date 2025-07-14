package com.ck.practicejournal.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.ck.practicejournal.dao.PracticeDao;
import com.ck.practicejournal.model.PracticeEntry;

public class PracticeService {

	private final PracticeDao practiceDao = new PracticeDao();

	public List<PracticeEntry> getEntriesForDate(LocalDate date) throws SQLException {
		return practiceDao.getEntriesByDate(date);
	}

	public void addEntry(PracticeEntry entry) throws SQLException {
		practiceDao.addEntry(entry);
	}

	public void addDataChangeListener(Runnable listener) {
		practiceDao.addDataChangeListener(() -> listener.run());
	}

	public void removeDataChangeListener(Runnable listener) {
		practiceDao.removeDataChangeListener(() -> listener.run());
	}
}
