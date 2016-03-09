package com.litedbAdvanced.optimizer;

import java.util.ArrayList;
import java.util.List;

import com.litedbAdvanced.parser.SelectStatement;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.TableDef;

public class Main {
	public static final String TAG = "OPTIMIZER";
	private static final int LEVEL_FULL_SCAN = 0;// scan the full table
	private static final int LEVEL_USE_INDEX = 1;// use index to determine which
	private static final int LEVEL_SCAN_INDEX = 2;// scan index only

	public static int analyze(SelectStatement stat, TableDef table) {
		LiteLogger.info(TAG, "full table scan");

		List<String> whereKeys = new ArrayList<>();

		String useIndex = null;
		for (String index : table.getIndexs()) {
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

		if (useIndex == null)
			return Main.LEVEL_FULL_SCAN;

		/* else continue judge use index or use index only */
		boolean indexOnly = true;
		for (String key : stat.getSelectedKeys()) {
			if (!useIndex.equals(key) && !table.getPrimaryKey().equals(key)) {
				indexOnly = false;
				break;
			}
		}

		if (indexOnly)
			return Main.LEVEL_SCAN_INDEX;
		else
			return Main.LEVEL_USE_INDEX;

	}

	/* analyze */
	public static List<Long> RIDsToGet() {
		List<Long> RIDsToGet = new ArrayList<>();
		int level = analyze(null, null);
		switch (level) {
		case Main.LEVEL_FULL_SCAN:
			int n = (int) com.litedbAdvanced.storage.Main.nextRID("tableName");
			for (int i = 0; i < n; i++) {
				RIDsToGet.add((long) i);
			}
			break;
		case Main.LEVEL_USE_INDEX:
			break;

		case Main.LEVEL_SCAN_INDEX:
			break;
		}
		return RIDsToGet;
	}

}
