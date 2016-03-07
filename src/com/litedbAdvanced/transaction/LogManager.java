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
		logs.add(0, log);
		LiteLogger.info(Main.TAG, "write log " + log);
		return;
	}

	public static String read(int transactionId, int sqlId) {
		String log = null;
		if (!logs.isEmpty()) {
			log = logs.remove(logs.size() - 1);
			LiteLogger.info(Main.TAG, "do log " + log);
		}
		return log;
	}

	public static String pop(int transactionId, int sqlId) {
		String log = null;
		if (!logs.isEmpty()) {
			log = logs.remove(0);
			LiteLogger.info(Main.TAG, "undo log " + log);
		}
		return log;
	}
	
	public static void clear(){
		logs.clear();
	}

}
