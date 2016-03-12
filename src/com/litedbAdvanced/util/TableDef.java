package com.litedbAdvanced.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.litedbAdvanced.execute.Main;

public class TableDef implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	public TableDef(String tableName, String primaryKey, List<String> keyNames, List<Integer> types,
			List<Integer> lengths, List<String> indexs) {
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
				rowSize++;// this byte is to store the length
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

	public List<String> getIndexs() {
		return indexs;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public byte[] toByteArray(List<String> values) {
		byte[] record = new byte[getRowSize()];
		ByteBuffer byteBuffer = ByteBuffer.wrap(record);

		for (int i = 0; i < types.size(); i++) {
			int type = types.get(i);
			int length = lengths.get(i);
			String value = values.get(i);
			switch (type) {
			case TableDef.TYPE_INT:
				byteBuffer.putInt(Integer.parseInt(value));
				break;
			case TableDef.TYPE_DOUBLE:
				byteBuffer.putDouble(Double.parseDouble(value));
				break;
			case TableDef.TYPE_CHAR:
				byte len = (byte)value.getBytes().length;
				byteBuffer.put(len);
				byte[] tmp = new byte[length];
				ByteBuffer tmpByteBuffer = ByteBuffer.wrap(tmp);
				tmpByteBuffer.put(value.getBytes());
				byteBuffer.put(tmpByteBuffer.array(), 0, length);
				break;
			case TableDef.TYPE_VARCHAR:
				break;
			}
		}

		return byteBuffer.array();
	}

	public List<String> toStringList(byte[] bytes) {
		List<String> values = new ArrayList<>();
		int offset = 0;
		ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, 4)).getInt();

		for (int i = 0; i < types.size(); i++) {
			int length = lengths.get(i);
			int type = types.get(i);
			switch (type) {
			case TableDef.TYPE_INT:
				int intvalue = ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + 4)).getInt();
				values.add(intvalue + "");
				offset += 4;
				break;
			case TableDef.TYPE_DOUBLE:
				double doublevalue = ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + 8)).getDouble();
				values.add(doublevalue + "");
				offset += 8;
				break;
			case TableDef.TYPE_CHAR:
				int len = ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + 1)).get();
				offset++;
				byte[] chars = ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + len)).array();
				values.add(new String(chars));
				offset += length;
				break;
			case TableDef.TYPE_VARCHAR:
				double longvalue = ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + 8)).getDouble();
				values.add(longvalue + "");
				offset += 8;
				break;
			default:
				LiteLogger.info(Main.TAG, type + " type not found");
			}
		}

		return values;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "table name: " + tableName + "\n" + "primary key: " + primaryKey + "\n";
		str += "columns \n";
		for (int i = 0; i < keyNames.size(); i++) {
			str += "name:" + keyNames.get(i) + " type:" + types.get(i) + " type:" + lengths.get(i) + "\n";
		}

		str += "indexs \n";
		for (int i = 0; i < indexs.size(); i++) {
			str += "name:" + indexs.get(i) + "\n";
		}

		return str;
	}

}
