package com.litedbAdvanced.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

public class Main {
	public static final String TAG = "STORAGE";

	public static boolean init() {
		com.litedbAdvanced.storage.FileManager.init();
		com.litedbAdvanced.storage.Controller.init();
		LiteLogger.info(TAG, "started.");

		List<String> keyNames = new ArrayList<>();
		keyNames.add("id");
		keyNames.add("name");
		keyNames.add("age");
		List<Integer> types = new ArrayList<>();
		types.add(TableDef.TYPE_INT);
		types.add(TableDef.TYPE_CHAR);
		types.add(TableDef.TYPE_INT);
		List<Integer> lengths = new ArrayList<>();
		lengths.add(11);
		lengths.add(10);
		lengths.add(11);

		TableDef tableDef = new TableDef("test", "id", keyNames, types, lengths, new ArrayList<String>());
		createTable(tableDef);
		// LiteLogger.info(Main.TAG, tableDef.toString());

		List<String> values = new ArrayList<>();
		values.add("1");
		values.add("newnius");
		values.add("20");

		// Row row = new Row(tableDef, values);

		// Controller.deleteRow(1001001);
		/*
		 * for (int i = 0; i < 20; i++) { long RID =
		 * nextRID(tableDef.getTableName() + ".dat"); Controller.insertRow(RID,
		 * row); }
		 */
		//
		// ow newrow = getRow(1001001);
		// LiteLogger.info(Main.TAG, newrow.get(1));

		return true;
	}

	public static boolean close() {
		com.litedbAdvanced.storage.Controller.close();
		com.litedbAdvanced.storage.FileManager.close();
		LiteLogger.info(TAG, "closed.");
		return true;
	}

	/* next available RID */
	public static long nextRID(String tableName) {
		return Controller.nextRID(tableName + ".dat");
	}

	/* next available fileId */
	public static int nextFileId() {
		return Controller.nextFileId();
	}

	public static Row getRow(long RID) {
		return Controller.getRow(RID);
	}

	public static int insertRow(long RID, Row newrow) {
		return Controller.insertRow(RID, newrow);
	}

	public static int deleteRow(long RID) {
		return Controller.deleteRow(RID);
	}

	public static int updateRow(long RID, Row newrow) {
		return Controller.updateRow(RID, newrow);
	}

	public static int createTable(TableDef tableDef) {
		return Controller.createTable(tableDef);
	}

	public static int dropTable(String tableName) {
		return Controller.deleteTable(tableName + ".dat");
	}

	public static TableDef getTableStructure(String tableName) {
		return Controller.getTableDef(tableName + ".dat");
	}

	public static Map<Integer, TableDef> getTableDefs() {
		return Controller.getTableDefs();
	}

}
