package com.ck.practicejournal.service;

import java.sql.SQLException;
import java.util.List;

import com.ck.practicejournal.dao.FocusDao;
import com.ck.practicejournal.model.FocusArea;

public class FocusService {

	private final FocusDao focusDao = new FocusDao();

	public List<FocusArea> getAllFocusAreas() throws SQLException {
		return focusDao.getAllFocusAreas();
	}

	public FocusDao getFocusDao() {
		return focusDao;
	}
}
