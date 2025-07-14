package com.ck.practicejournal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ck.practicejournal.model.FocusArea;
import com.ck.practicejournal.util.DatabaseManager;

public class FocusDao {

	public void createFocusArea(FocusArea focus) throws SQLException {
		String sql = "INSERT INTO focus_areas(name) VALUES(?)";
		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, focus.getName());
			pstmt.executeUpdate();

			// SQLite-specific
			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
				if (rs.next()) {
					focus.setId(rs.getInt(1));
				}
			}
		}
	}

	public List<FocusArea> getAllFocusAreas() throws SQLException {
		String sql = "SELECT f.id, f.name, COUNT(ef.entry_id) AS usage_count " + "FROM focus_areas f "
				+ "LEFT JOIN entry_focus ef ON f.id = ef.focus_id " + "GROUP BY f.id, f.name " + "ORDER BY f.name";

		List<FocusArea> focusAreas = new ArrayList<>();
		try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				FocusArea focus = new FocusArea(rs.getInt("id"), rs.getString("name"), rs.getInt("usage_count"));
				focusAreas.add(focus);
			}
		}
		return focusAreas;
	}

	public void updateFocusArea(FocusArea focus) throws SQLException {
		String sql = "UPDATE focus_areas SET name = ? WHERE id = ?";
		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, focus.getName());
			pstmt.setInt(2, focus.getId());
			pstmt.executeUpdate();
		}
	}

	public void deleteFocusArea(int id) throws SQLException {
		String sql = "DELETE FROM focus_areas WHERE id = ?";
		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	}
}