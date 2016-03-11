package com.litedbAdvanced.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;

class IndexManager {
	private static Map<String, Map<Long, Row>> allRows;

	public static int init() {
		allRows = new HashMap<String, Map<Long, Row>>();
		LiteLogger.info(Main.TAG, "indexManager started");
		return 0;
	}

	public static int close() {
		LiteLogger.info(Main.TAG, "indexManager stoped");
		return 0;
	}

	public static long smallestRowGreaterThan(String tableName, String compareKey, String compareValue) {
		return 0;
	}

	public static long largestRowGreaterThan(String tableName, String compareKey, String compareValue) {
		return 0;
	}

	public static Row getRIDAfter(String tableName, long RID) {
		return null;
	}

	public static List<Row> getAllRows(String tableName) {
		if (!allRows.containsKey(tableName)) {
			return null;
		}
		Map<Long, Row> rows = allRows.get(tableName);
		return new ArrayList<>(rows.values());
	}

	public static int addRow(String tableName, long RID, Row row) {
		if (!allRows.containsKey(tableName)) {
			return 0;
		}
		Map<Long, Row> indexs = allRows.get(tableName);
		indexs.put(RID, row);
		return 0;
	}

	public static int deleteRow(String tableName, long RID) {
		if (!allRows.containsKey(tableName)) {
			return 0;
		}
		Map<Long, Row> rows = allRows.get(tableName);
		if (rows.containsKey(RID))
			rows.remove(RID);
		return 0;
	}

	public static int updateRow(String tableName, long RID, Row row) {
		if (!allRows.containsKey(tableName)) {
			return 0;
		}
		Map<Long, Row> rows = allRows.get(tableName);
		if (rows.containsKey(RID)){
			rows.remove(RID);
			rows.put(RID, row);
		}
		return 0;
	}

}
