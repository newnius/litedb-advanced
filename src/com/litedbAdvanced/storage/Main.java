package com.litedbAdvanced.storage;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

public class Main {
	public static final String TAG = "STORAGE";

	public static boolean init() {
		com.litedbAdvanced.storage.FileManager.init();
		com.litedbAdvanced.storage.Controller.init();
		LiteLogger.info(TAG, "started.");
		return true;
	}

	public static boolean close() {
		LiteLogger.info(TAG, "closed.");
		return true;
	}

	/* next available RID */
	public static long nextRID(String tableName) {
		return Controller.nextRID(tableName);
	}

	public static Row getRow(long RID) {
		return Controller.getRow(RID);
	}

	public static boolean insertRow(int RID, Row newrow) {
		return Controller.insertRow(RID, newrow);
	}

	public static boolean deleteRow(int RID) {
		return Controller.deleteTable(RID);
	}

	public static boolean updateROw(int RID, Row newrow) {
		return Controller.updateRow(RID, newrow);
	}

	public static boolean createTable(TableDef tableDef) {
		return Controller.createTable(tableDef);
	}

	public static boolean dropTable(String tableName) {
		return Controller.deleteTable(tableName);
	}

	public static TableDef getTableStructure(String tableName) {
		return Controller.getTableDef(tableName);
	}

}
