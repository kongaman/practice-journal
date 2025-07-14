package com.ck.practicejournal.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.ck.practicejournal.dao.GoalDao;
import com.ck.practicejournal.model.Goal;

public class GoalService {

	private final GoalDao goalDao = new GoalDao();

	public List<Goal> getActiveGoals(LocalDate date) throws SQLException {
		return goalDao.getActiveGoals(date);
	}

	public GoalDao getGoalDao() {
		return goalDao;
	}
}
