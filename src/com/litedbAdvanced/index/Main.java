package com.litedbAdvanced.index;

import java.util.List;

import com.litedbAdvanced.util.Row;

public class Main {
	public static final String TAG = "INDEX";

	public static int init() {
		IndexManager.init();
		return 0;
	}

	public static int close() {
		IndexManager.close();
		return 0;
	}

	public static List<Row> getAllRows(String tableName) {
		return IndexManager.getAllRows(tableName);
	}
	
	public static List<Long> getAllRIDs(String tableName) {
		return IndexManager.getAllRIDs(tableName);
	}

	public static int addRow(String tableName, long RID, Row row) {
		return IndexManager.addRow(tableName, RID, row);
	}

	public static int deleteRow(String tableName, long RID) {
		return IndexManager.deleteRow(tableName, RID);
	}

	public static int updateRow(String tableName, long RID, Row row) {
		return IndexManager.updateRow(tableName, RID, row);
	}

}
