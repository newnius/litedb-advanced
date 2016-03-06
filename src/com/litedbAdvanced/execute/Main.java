package com.litedbAdvanced.execute;

import com.litedbAdvanced.transaction.LockManager;
import com.litedbAdvanced.transaction.Transaction;
import com.litedbAdvanced.util.LiteLogger;

public class Main {
	public static final String TAG = "EXECUTOR";
	private boolean autoCommit = true;
	private Transaction transaction = null;

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public void execute(String command) {
		if (command == null)
			return;
		String[] params = command.split(" ");
		switch (params[0]) {
		case "SLock":
			SLock(params);
			break;
		case "unSLock":
			unSLock(params);
			break;
		case "XLock":
			XLock(params);
			break;
		case "unXLock":
			unXLock(params);
			break;
		case "begin":
			begin();
			break;
		case "read":
			read(params);
			break;
		case "write":
			write(params);
			break;
		case "commit":
			commit();
			break;

		default:
			LiteLogger.info(TAG, "Unknown command.");
		}
		if (autoCommit)
			commit();
	}

	public void begin() {
		transaction = new Transaction();
		autoCommit = false;
		LiteLogger.info(TAG, "Transaction begin.(Tid=" + transaction.getTransactionId() + ")");
	}

	public void commit() {
		transaction.commit();
		LiteLogger.info(TAG, "Transaction commited.(Tid=" + transaction.getTransactionId() + ")");
	}

	public void read(String[] args) {
		if (args.length < 2) {
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		SLock(args);
	}

	public void write(String[] args) {
		if (args.length < 2) {
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		XLock(args);
	}

	public static void SLock(String[] args) {
		if (args.length < 2) {
			LiteLogger.info("MAIN", "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.SLockRID(rid);
	}

	public static void unSLock(String[] args) {
		if (args.length < 2) {
			LiteLogger.info("MAIN", "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.unSLockRID(rid);
	}

	public static void XLock(String[] args) {
		if (args.length < 2) {
			LiteLogger.info("MAIN", "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.XLockRID(rid);
	}

	public static void unXLock(String[] args) {
		if (args.length < 2) {
			LiteLogger.info("MAIN", "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.unXLockRID(rid);
	}

}
