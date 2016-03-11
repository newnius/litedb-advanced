package com.litedbAdvanced.util;

import java.util.List;

public class Row {
	private TableDef table;
	private List<String> keys;
	private List<String> values;

	public Row(TableDef table, List<String> values) {
		this.table = table;
		this.keys = table.getKeyNames();
		this.values = values;
	}

	public Row(TableDef table, List<String> keys, List<String> values) {
		this.table = table;
		this.keys = keys;
		this.values = values;
	}

	public Row(TableDef table, byte[] bytes) {
		this.table = table;
		this.values = table.toStringList(bytes);
	}

	public byte[] toByteArray() {
		return table.toByteArray(values);
	}

	public int getInt(int index) {
		return 0;
	}

	public double getDouble(int index) {
		return 0;
	}

	public byte[] getChar(int index) {
		return null;
	}

	public String getVarchar(int index) {
		return null;
	}

	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < keys.size(); i++) {
			str += keys.get(i) + "-->" + values.get(i) + "\n";
		}
		return str;
	}

	public String get(int i) {
		return values.get(i);
	}

	public boolean set(int index, String value) {
		values.set(index, value);
		return true;
	}

}
