package com.ck.practicejournal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ck.practicejournal.model.PracticeEntry;
import com.ck.practicejournal.util.DataChangeListener;
import com.ck.practicejournal.util.DatabaseManager;

public class PracticeDao {

	private final List<DataChangeListener> listeners = new ArrayList<>();

	public void addDataChangeListener(DataChangeListener listener) {
		listeners.add(listener);
	}

	public void removeDataChangeListener(DataChangeListener listener) {
		listeners.remove(listener);
	}

	private void notifyDataChanged() {
		for (DataChangeListener listener : listeners) {
			listener.onDataChanged();
		}
	}

	public void addEntry(PracticeEntry entry) throws SQLException {
		String sql = "INSERT INTO entries(date, duration, focus_area, exercise, tempo, error_rate, notes) " + "VALUES(?,?,?,?,?,?,?)";

		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, entry.getDate().toString());
			pstmt.setInt(2, entry.getDurationMinutes());
			pstmt.setString(3, entry.getFocusArea());
			pstmt.setString(4, entry.getExercise());
			pstmt.setInt(5, entry.getTempoBpm());
			pstmt.setInt(6, entry.getErrorRate());
			pstmt.setString(7, entry.getNotes());

			pstmt.executeUpdate();
			notifyDataChanged();
		}
	}

	public List<PracticeEntry> getEntriesByDate(LocalDate date) throws SQLException {
		List<PracticeEntry> entries = new ArrayList<>();
		String sql = "SELECT * FROM entries WHERE date = ?";

		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, date.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				PracticeEntry entry = new PracticeEntry(LocalDate.parse(rs.getString("date")), rs.getInt("duration"), rs.getString("focus_area"),
						rs.getString("exercise"), rs.getInt("tempo"), rs.getInt("error_rate"), rs.getString("notes"));
				entries.add(entry);
				System.out.println(entry.toString());
			}
		}
		return entries;
	}
}