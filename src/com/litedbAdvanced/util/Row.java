package com.litedbAdvanced.util;

import java.util.List;

public class Row {
	private TableDef table;
	private List<String> values;

	public Row(byte[] values) {
	}

	public Row(TableDef table, byte[] bytes) {
		this.table = table;
		this.values = table.toStringList(bytes);
	}

	public byte[] toByteArray() {
		return table.toByteArray(this);
	}

	public long getRID() {
		return 0;
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

	public Object get(String key) {
		return null;
	}

	@Override
	public String toString() {
		String str = "";
		for (String tmp : values) {
			str += tmp + " ";
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
