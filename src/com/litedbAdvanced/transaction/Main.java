package com.litedbAdvanced.transaction;

public class Main {
	private static Integer transactionId = 1;

	public static final String TAG = "TRANSACTION";

	public static boolean init() {
		LockManager.init(10, 20);
		return true;
	}

	public static boolean close() {
		return true;
	}

	public static int newTransactionId() {
		int tid;
		synchronized (transactionId) {
			tid = transactionId++;
		}
		return tid;
	}

}
