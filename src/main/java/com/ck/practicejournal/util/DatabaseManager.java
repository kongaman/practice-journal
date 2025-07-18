package com.ck.practicejournal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	private static final String DB_URL = "jdbc:sqlite:practicejournal.db";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}

	public static void initializeDatabase() {
		try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

			String createEntriesTable = "CREATE TABLE IF NOT EXISTS entries (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL,"
					+ "duration INTEGER NOT NULL, exercise TEXT NOT NULL," + "tempo INTEGER DEFAULT 0, error_rate INTEGER DEFAULT 0," + "notes TEXT)";
			stmt.execute(createEntriesTable);

			String createGoalsTable = "CREATE TABLE IF NOT EXISTS goals (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "type TEXT NOT NULL CHECK(type IN ('WEEKLY','MONTHLY','QUARTERLY','SEMI_ANNUAL','ANNUAL'))," + "start_date TEXT NOT NULL,"
					+ "description TEXT NOT NULL," + "target_value REAL NOT NULL," + "current_value REAL DEFAULT 0," + "achieved BOOLEAN DEFAULT 0)";
			stmt.execute(createGoalsTable);

			String createFocusTable = "CREATE TABLE IF NOT EXISTS focus_areas (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL)";
			stmt.execute(createFocusTable);

			String createExcerciseFocus = "CREATE TABLE IF NOT EXISTS entry_focus (entry_id INTEGER NOT NULL REFERENCES entries(id) ON DELETE CASCADE,"
					+ " focus_id INTEGER NOT NULL REFERENCES focus_areas(id), PRIMARY KEY (entry_id, focus_id))";
			stmt.execute(createExcerciseFocus);

			String countQuery = "SELECT COUNT(*) FROM focus_areas";
			try (ResultSet rs = stmt.executeQuery(countQuery)) {
				if (rs.next() && rs.getInt(1) == 0) {
					String[] initialFocusAreas = { "Alternate Picking", "Economy Picking", "Sweeping", "Legato", "Bending", "Vibrato", "Tapping",
							"Timing", "Improvisation", "Fretboard Knowledge", "Scales" };

					String insertSql = "INSERT INTO focus_areas (name) VALUES (?)";
					try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
						for (String name : initialFocusAreas) {
							pstmt.setString(1, name);
							pstmt.executeUpdate();
						}
					}

					System.out.println("Standard-Focus Areas eingefügt.");
				}
			}

			System.out.println("Datenbank erfolgreich initialisiert!");

		} catch (SQLException e) {
			System.err.println("Datenbank-Initialisierungsfehler: " + e.getMessage());
		}
	}
}