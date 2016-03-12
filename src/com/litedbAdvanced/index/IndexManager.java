package com.litedbAdvanced.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

class IndexManager {
	private static Map<String, Map<Long, Row>> allRows;
	private static Map<String, List<Long>> allRids;

	public static int init() {
		allRows = new HashMap<String, Map<Long, Row>>();
		allRids = new HashMap<String, List<Long>>();

		Map<Integer, TableDef> tableDefs = com.litedbAdvanced.storage.Main.getTableDefs();
		Set<Integer> fileIds = tableDefs.keySet();
		for (Integer fileId : fileIds) {
			allRids.put(tableDefs.get(fileId).getTableName(), new ArrayList<>());
			allRows.put(tableDefs.get(fileId).getTableName(), new HashMap<>());
		}

		LiteLogger.info(Main.TAG, "indexManager started");
		return 0;
	}

	public static int createTable(String tableName) {
		if (allRids.containsKey(tableName))
			allRids.remove(tableName);
		allRids.put(tableName, new ArrayList<>());

		if (allRows.containsKey(tableName))
			allRows.remove(tableName);
		allRows.put(tableName, new HashMap<>());
		return 0;
	}

	public static int dropTable(String tableName) {
		if (allRids.containsKey(tableName))
			allRids.remove(tableName);
		if (allRows.containsKey(tableName))
			allRows.remove(tableName);
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
		allRids.get(tableName).add(RID);
		return 0;
	}

	public static List<Long> getAllRIDs(String tableName) {
		return allRids.get(tableName);
	}

	public static int deleteRow(String tableName, long RID) {
		if (!allRows.containsKey(tableName)) {
			return 0;
		}
		Map<Long, Row> rows = allRows.get(tableName);
		if (rows.containsKey(RID))
			rows.remove(RID);
		allRids.get(tableName).remove(RID);
		return 0;
	}

	public static int updateRow(String tableName, long RID, Row row) {
		if (!allRows.containsKey(tableName)) {
			return 0;
		}
		Map<Long, Row> rows = allRows.get(tableName);
		if (rows.containsKey(RID)) {
			rows.remove(RID);
			rows.put(RID, row);
		}
		return 0;
	}

}
