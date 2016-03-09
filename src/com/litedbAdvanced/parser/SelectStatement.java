package com.litedbAdvanced.parser;


public class SelectStatement {
	private String tableName ;
	private String[] keys;
	private String whereClause = "1";
	private String orderBy = null;
	private int offset = 0;
	private int count = -1;
	
	
	public SelectStatement(String tableName, String[] keys, String whereClause, String orderBy, int offset,
			int count) {
		super();
		this.tableName = tableName;
		this.keys = keys;
		this.whereClause = whereClause;
		this.orderBy = orderBy;
		this.offset = offset;
		this.count = count;
	}

	public String getTableName(){
		return tableName;
	}
	
	public String[] getSelectedKeys(){
		return keys;
	}
	
	public String getOrderBy(){
		return orderBy;
	}
	
	public String getWhere(){
		return whereClause;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getCount(){
		return count;
	}
	
	

}
