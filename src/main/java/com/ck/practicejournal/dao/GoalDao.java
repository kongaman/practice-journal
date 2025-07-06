package com.ck.practicejournal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ck.practicejournal.model.Goal;
import com.ck.practicejournal.model.GoalType;
import com.ck.practicejournal.util.DatabaseManager;

public class GoalDao {

	public void createGoal(Goal goal) throws SQLException {
		String sql = "INSERT INTO goals(type, start_date, description, target_value) "
				+ "VALUES(?,?,?,?)";

		try (Connection conn = DatabaseManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, goal.getType().name());
			pstmt.setString(2, goal.getStartDate().toString());
			pstmt.setString(3, goal.getDescription());
			pstmt.setDouble(4, goal.getTargetValue());

			pstmt.executeUpdate();
		}
	}

	public List<Goal> getActiveGoals(LocalDate forDate) throws SQLException {
		List<Goal> goals = new ArrayList<>();
		String sql = "SELECT * FROM goals WHERE start_date <= ? "
				+ "AND date(?, '+' || "
				+ "CASE type "
				+ "  WHEN 'WEEKLY' THEN '7 days' "
				+ "  WHEN 'MONTHLY' THEN '1 month' "
				+ "  WHEN 'QUARTERLY' THEN '3 months' "
				+ "  WHEN 'SEMI_ANNUAL' THEN '6 months' "
				+ "  WHEN 'ANNUAL' THEN '1 year' "
				+ "END) > start_date "
				+ "AND achieved = 0";

		try (Connection conn = DatabaseManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, forDate.toString());
			pstmt.setString(2, forDate.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Goal goal = new Goal(
						rs.getInt("id"),
						GoalType.valueOf(rs.getString("type")),
						LocalDate.parse(rs.getString("start_date")),
						rs.getString("description"),
						rs.getDouble("target_value"),
						rs.getDouble("current_value"),
						rs.getBoolean("achieved")
						);
				goals.add(goal);
			}
		}
		return goals;
	}
}
