package com.ck.practicejournal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	private static final String DB_URL = "jdbc:sqlite:practicejournal.db";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}

	public static void initializeDatabase() {
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {

			String createEntriesTable = "CREATE TABLE IF NOT EXISTS entries ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "date TEXT NOT NULL,"
					+ "duration INTEGER NOT NULL,"
					+ "focus_area TEXT NOT NULL,"
					+ "exercise TEXT NOT NULL,"
					+ "tempo INTEGER DEFAULT 0,"
					+ "error_rate REAL DEFAULT 0.0,"
					+ "notes TEXT)";
			stmt.execute(createEntriesTable);

			String createGoalsTable = "CREATE TABLE IF NOT EXISTS goals ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "type TEXT NOT NULL CHECK(type IN ('WEEKLY','MONTHLY','QUARTERLY','SEMI_ANNUAL','ANNUAL')),"
					+ "start_date TEXT NOT NULL,"
					+ "description TEXT NOT NULL,"
					+ "target_value REAL NOT NULL,"
					+ "current_value REAL DEFAULT 0,"
					+ "achieved BOOLEAN DEFAULT 0)";
			stmt.execute(createGoalsTable);

			System.out.println("Datenbank erfolgreich initialisiert!");

		} catch (SQLException e) {
			System.err.println("Datenbank-Initialisierungsfehler: " + e.getMessage());
		}
	}
}