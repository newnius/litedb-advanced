package com.litedbAdvanced.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litedbAdvanced.parser.DeleteStatement;
import com.litedbAdvanced.parser.InsertStatement;
import com.litedbAdvanced.parser.SelectStatement;
import com.litedbAdvanced.parser.UpdateStatement;

public class Transaction {
	private int transactionId;
	private int sqlId = 0;
	private List<Long> XLockedRIDs;
	private Map<String, Integer> savedPoints;
	
	
	public Transaction(){
		this.transactionId = Main.newTransactionId();
		this.XLockedRIDs = new ArrayList<>();
		this.savedPoints = new HashMap<>();
	}
	
	public int getTransactionId(){
		return this.transactionId;
	}
	
	public void select(SelectStatement stat){
		
	}
	
	public void insert(InsertStatement stat){}
	
	public void update(UpdateStatement stat){
		LogManager.write(transactionId, sqlId);
	}
	
	public void delete(DeleteStatement stat){}
	
	public void rollback(String pointName){
		if(savedPoints.containsKey(pointName)){
			int sqlId = savedPoints.get(pointName);
			LogManager.read(transactionId, sqlId);
		}
	}
	
	public void savePoint(String pointName){
		savedPoints.put(pointName, sqlId);
	}
	
	public void commit(){
		while(!XLockedRIDs.isEmpty()){
			LockManager.unXLockRID(XLockedRIDs.remove(0));
		}
	}
	

}
