package com.ck.practicejournal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ck.practicejournal.model.FocusArea;
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
		Connection conn = null;
		try {
			conn = DatabaseManager.getConnection();
			conn.setAutoCommit(false);

			String entrySql = "INSERT INTO entries(date, duration, exercise, tempo, error_rate, notes) " + "VALUES(?,?,?,?,?,?)";

			try (PreparedStatement entryStmt = conn.prepareStatement(entrySql)) {
				entryStmt.setString(1, entry.getDate().toString());
				entryStmt.setInt(2, entry.getDurationMinutes());
				entryStmt.setString(3, entry.getExercise());
				entryStmt.setInt(4, entry.getTempoBpm());
				entryStmt.setInt(5, entry.getErrorRate());
				entryStmt.setString(6, entry.getNotes());

				entryStmt.executeUpdate();

				// SQLite-specific
				try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
					if (rs.next()) {
						int entryId = rs.getInt(1);
						saveFocusRelationships(conn, entryId, entry.getFocusAreas());
					}
				}
			}
			conn.commit();
			notifyDataChanged();
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);
			}
		}
	}

	private void saveFocusRelationships(Connection conn, int entryId, List<FocusArea> focusAreas) throws SQLException {
		String relationSql = "INSERT INTO entry_focus(entry_id, focus_id) VALUES(?,?)";

		try (PreparedStatement relationStmt = conn.prepareStatement(relationSql)) {
			for (FocusArea focus : focusAreas) {
				relationStmt.setInt(1, entryId);
				relationStmt.setInt(2, focus.getId());
				relationStmt.addBatch();
			}
			relationStmt.executeBatch();
		}
	}

	public List<PracticeEntry> getEntriesByDate(LocalDate date) throws SQLException {
		List<PracticeEntry> entries = new ArrayList<>();
		String sql = "SELECT e.*, f.id as focus_id, f.name as focus_name " + "FROM entries e " + "LEFT JOIN entry_focus ef ON e.id = ef.entry_id "
				+ "LEFT JOIN focus_areas f ON ef.focus_id = f.id " + "WHERE e.date = ?";

		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, date.toString());
			ResultSet rs = pstmt.executeQuery();

			Map<Integer, PracticeEntry> entryMap = new HashMap<>();

			while (rs.next()) {
				int entryId = rs.getInt("id");

				PracticeEntry entry;
				if (!entryMap.containsKey(entryId)) {
					entry = new PracticeEntry(LocalDate.parse(rs.getString("date")), rs.getInt("duration"), rs.getString("exercise"),
							rs.getInt("tempo"), rs.getInt("error_rate"), rs.getString("notes"));
					entry.setId(entryId);
					entryMap.put(entryId, entry);
					entries.add(entry);
				} else {
					entry = entryMap.get(entryId);
				}

				if (rs.getObject("focus_id") != null) {
					FocusArea focus = new FocusArea(rs.getInt("focus_id"), rs.getString("focus_name"), 0 // usageCount nicht benötigt
					);
					entry.getFocusAreas().add(focus);
				}
			}
		}
		return entries;
	}
}