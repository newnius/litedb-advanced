package com.litedbAdvanced.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;

public class Transaction {
	private int transactionId;
	private int sqlId = 0;
	private List<Long> XLockedRIDs;
	private List<Long> SLockedRIDs;
	private Map<String, Integer> savedPoints;

	public Transaction() {
		this.transactionId = Main.newTransactionId();
		this.XLockedRIDs = new ArrayList<>();
		this.SLockedRIDs = new ArrayList<>();
		this.savedPoints = new HashMap<>();
		LiteLogger.info(Main.TAG, "Transaction " + this.transactionId + " start.");
	}

	/*
	 * read data, add SLock
	 * 
	 * */
	public Row read(long rid) {
		LockManager.SLockRID(rid);
		SLockedRIDs.add(rid);
		LiteLogger.info(Main.TAG, "read " + rid);
		return com.litedbAdvanced.storage.Main.getRow(rid);
	}
	
	/* stop read, unlock all SLock*/
	public void stopRead(){
		for (Long RID : SLockedRIDs) {
			LockManager.unSLockRID(RID);
		}
	}
	
	/*
	 * read and add XLock
	 * */
	public Row readForUpdate(long rid){
		if (!XLockedRIDs.contains(rid)) {
			LockManager.XLockRID(rid);
			XLockedRIDs.add(rid);
		}
		return com.litedbAdvanced.storage.Main.getRow(rid);
	}
	
	

	/* lock for
	 * update or insert or delete record
	 * */
	private void XLockRID(long rid) {
		if (!XLockedRIDs.contains(rid)) {
			LockManager.XLockRID(rid);
			XLockedRIDs.add(rid);
		}
		sqlId++;
		LogManager.write(transactionId, sqlId);
		LiteLogger.info(Main.TAG, "write " + rid);
		
	}
	
	public int insert(long RID, Row row){
		XLockRID(RID);
		return com.litedbAdvanced.storage.Main.insertRow(RID, row);
	}

	public int getTransactionId() {
		return this.transactionId;
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
