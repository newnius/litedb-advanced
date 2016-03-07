package com.litedbAdvanced.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litedbAdvanced.parser.DeleteStatement;
import com.litedbAdvanced.parser.InsertStatement;
import com.litedbAdvanced.parser.SelectStatement;
import com.litedbAdvanced.parser.UpdateStatement;
import com.litedbAdvanced.util.LiteLogger;

public class Transaction {
	private int transactionId;
	private int sqlId = 0;
	private List<Long> XLockedRIDs;
	private Map<String, Integer> savedPoints;

	public Transaction() {
		this.transactionId = Main.newTransactionId();
		this.XLockedRIDs = new ArrayList<>();
		this.savedPoints = new HashMap<>();
		LiteLogger.info(Main.TAG, "Transaction " + this.transactionId + " start.");
	}

	public void read(long rid) {
		List<Long> SLockedRIDs = new ArrayList<>();
		LockManager.SLockRID(rid);
		SLockedRIDs.add(rid);
		LiteLogger.info(Main.TAG, "read " + rid);
		select(null);
		for (Long RID : SLockedRIDs) {
			LockManager.unSLockRID(RID);
		}
	}

	public void write(long rid) {
		if (!XLockedRIDs.contains(rid)) {
			LockManager.XLockRID(rid);
			XLockedRIDs.add(rid);
		}
		update(null);
		LiteLogger.info(Main.TAG, "write " + rid);
		
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	public void select(SelectStatement stat) {
	}

	public void insert(InsertStatement stat) {
		sqlId++;
		LogManager.write(transactionId, sqlId);
	}

	public void update(UpdateStatement stat) {
		sqlId++;
		LogManager.write(transactionId, sqlId);
	}

	public void delete(DeleteStatement stat) {
		sqlId++;
		LogManager.write(transactionId, sqlId);
	}

	public void rollback(String pointName) {
		if (savedPoints.containsKey(pointName)) {
			int sqlId = savedPoints.get(pointName);
			while(LogManager.pop(transactionId, sqlId)  != null);
			LiteLogger.info(Main.TAG, "rollback to " + pointName);
		} else {
			LiteLogger.info(Main.TAG, "savepoint " + pointName + " not exist.");
		}
	}

	public void savePoint(String pointName) {
		if (savedPoints.containsKey(pointName)) {
			savedPoints.remove(pointName);
		}
		savedPoints.put(pointName, sqlId);
		LiteLogger.info(Main.TAG, "savepoint " + pointName);
	}

	public void commit() {
		/* write log to file */
		while(LogManager.read(transactionId, sqlId) != null);
		
		/* free locks */
		while (!XLockedRIDs.isEmpty()) {
			LockManager.unXLockRID(XLockedRIDs.remove(0));
		}
		LiteLogger.info(Main.TAG, "transaction commited.");
	}

	/* roll back all */
	public void abort() {
		while (!XLockedRIDs.isEmpty()) {
			LockManager.unXLockRID(XLockedRIDs.remove(0));
		}
		LogManager.clear();
		LiteLogger.info(Main.TAG, "transaction aborted.");
	}

}
