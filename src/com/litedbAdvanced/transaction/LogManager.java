package com.litedbAdvanced.transaction;

import java.util.ArrayList;
import java.util.List;

import com.litedbAdvanced.util.LiteLogger;

public class LogManager {
	private static List<String> logs;

	public static boolean init() {
		logs = new ArrayList<>();
		return true;
	}

	public static boolean close() {
		logs.clear();
		logs = null;
		return true;
	}

	public static void write(int transactionId, int sqlId) {
		String log = "(Tid=" + transactionId + ", Sid=" + sqlId + ")";
		logs.add(log);
		LiteLogger.info(Main.TAG, "write log " + log);
	}

	public static void read(int transactionId, int sqlId) {
		String log = "(Tid=" + transactionId + ", Sid=" + sqlId + ")";
		LiteLogger.info(Main.TAG, "read log " + log);
	}

}
