/*
 * @author Newnius
 * provide services related to transaction, manage transaction log, lock etc.
 * 
 * */

package com.litedbAdvanced.transaction;

public class Main {
	private static Integer transactionId = 1;

	public static final String TAG = "TRANSACTION";

	public static boolean init() {
		LockManager.init(30, 20, 20);
		LogManager.init();
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
