package com.litedbAdvanced.optimizer;

import java.util.ArrayList;
import java.util.List;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.TableDef;

public class Main {
	public static final String TAG = "OPTIMIZER";
	private static final int LEVEL_FULL_SCAN = 0;// scan the full table
	private static final int LEVEL_USE_INDEX = 1;// use index to determine which
	private static final int LEVEL_SCAN_INDEX = 2;// scan index only

	public static int analyze(TableDef table, List<String> selectedKeys, List<String> whereKeys) {
		List<String> indexs = table.getIndexs();
		indexs.add(table.getPrimaryKey());
		String useIndex = null;
		for (String index : indexs) {
			useIndex = index;
			// ought to be table.getWhereKeys()
			for (String key : whereKeys) {
				if (!index.equals(key) && !table.getPrimaryKey().equals(key)) {
					useIndex = null;
					break;
				}
			}
			if (useIndex != null)
				break;
		}

		if (useIndex == null) {
			LiteLogger.info(Main.TAG, "full table scan");
			return Main.LEVEL_FULL_SCAN;
		}

		/* else continue judge use index or use index only */
		boolean indexOnly = true;

		for (String key : selectedKeys) {
			if (!useIndex.equals(key) && !table.getPrimaryKey().equals(key)) {
				indexOnly = false;
				break;
			}
		}

		if (indexOnly) {
			LiteLogger.info(Main.TAG, "only scan the index " + useIndex);
			return Main.LEVEL_SCAN_INDEX;
		} else {
			LiteLogger.info(Main.TAG, "use index " + useIndex);
			return Main.LEVEL_USE_INDEX;
		}
	}

	/* analyze */
	public static List<Long> RIDsToGet(TableDef tableDef, List<String> selectedKeys, List<String> whereKeys) {
		List<Long> RIDsToGet = new ArrayList<>();
		RIDsToGet = com.litedbAdvanced.index.Main.getAllRIDs(tableDef.getTableName());
		/*
		 * List<Row> rows =
		 * com.litedbAdvanced.index.Main.getAllRows(tableDef.getTableName());
		 * for (Row row : rows) { RIDsToGet.add(Long.parseLong(row.get("RID")));
		 * }
		 */

		int level = analyze(tableDef, selectedKeys, whereKeys);

		switch (level) {
		case Main.LEVEL_FULL_SCAN:

			// int n = (int)
			// com.litedbAdvanced.storage.Main.nextRID(tableDef.getTableName());
			// for (int i = 0; i < n; i++) {
			// RIDsToGet.add((long) i);
			// }

			break;
		case Main.LEVEL_USE_INDEX:

			break;

		case Main.LEVEL_SCAN_INDEX:

			break;
		}
		return RIDsToGet;
	}

}
