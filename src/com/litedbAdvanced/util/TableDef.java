package com.litedbAdvanced.util;

import java.util.List;

public class TableDef {
	private String tableName;
	private String primaryKey;
	
	
	private List<String> keyNames;
	private List<Integer> types;
	private List<Integer> lengths;
	
	public static final int TYPE_INT = 0;
	public static final int TYPE_DOUBLE = 1;
	public static final int TYPE_CHAR = 2;
	public static final int TYPE_VARCHAR = 3;

	private List<String> indexs;

	private int rowSize = 0;

	public TableDef(String tableName, String primaryKey, List<String> keyNames, List<Integer> types, List<Integer> lengths,
			List<String> indexs) {
		super();
		this.tableName = tableName;
		this.primaryKey = primaryKey;
		this.keyNames = keyNames;
		this.types = types;
		this.lengths = lengths;
		this.indexs = indexs;
	}

	
	public String getTableName() {
		return tableName;
	}
	
	
	

	public List<String> getKeyNames() {
		return keyNames;
	}


	public int getRowSize() {
		if (this.rowSize != 0)
			return rowSize;
		int rowSize = 0;
		for (int i = 0; i < types.size(); i++) {
			int type = types.get(i);
			int length = lengths.get(i);
			switch (type) {
			case TableDef.TYPE_INT:
				rowSize += 4;
				break;
			case TableDef.TYPE_DOUBLE:
				rowSize += 8;
				break;
			case TableDef.TYPE_CHAR:
				rowSize += length;
				break;
			case TableDef.TYPE_VARCHAR:
				rowSize += 8;
				break;
			}
		}
		this.rowSize = rowSize;
		return rowSize;
	}

	public List<Integer> getTypes() {
		return types;
	}

	public List<Integer> getLengths() {
		return lengths;
	}
	
	public List<String> getIndexs(){
		return indexs;
	}
	
	public String getPrimaryKey(){
		return primaryKey;
	}
	
	public byte[] toByteArray(Row row){
		
		for (int i = 0; i < types.size(); i++) {
			int type = types.get(i);
			int length = lengths.get(i);
			switch (type) {
			case TableDef.TYPE_INT:
				rowSize += 4;
				break;
			case TableDef.TYPE_DOUBLE:
				rowSize += 8;
				break;
			case TableDef.TYPE_CHAR:
				rowSize += length;
				break;
			case TableDef.TYPE_VARCHAR:
				rowSize += 8;
				break;
			}
		}
		
		
		
		return null;
	}
	
public List<String> toStringList(byte[] bytes){
		
		for (int i = 0; i < types.size(); i++) {
			int type = types.get(i);
			int length = lengths.get(i);
			switch (type) {
			case TableDef.TYPE_INT:
				rowSize += 4;
				break;
			case TableDef.TYPE_DOUBLE:
				rowSize += 8;
				break;
			case TableDef.TYPE_CHAR:
				rowSize += length;
				break;
			case TableDef.TYPE_VARCHAR:
				rowSize += 8;
				break;
			}
		}
		
		
		
		return null;
	}
	
}
