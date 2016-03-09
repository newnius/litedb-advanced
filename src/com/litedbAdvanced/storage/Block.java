package com.litedbAdvanced.storage;

import com.litedbAdvanced.util.Config;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

public class Block {

	// 数据
	public byte[] data;

	public Block(byte[] data) {
		this.data = data;
	}

	public Row getRow(long RID) {
		int fileId = Controller.getFileId(RID);
		int rowOffset = Controller.getRowOffset(RID);
		TableDef tableDef = Controller.getTableDef(fileId);
		int rowByteArrayLength = tableDef.getRowSize();

		try {
			// another 1 byte is used to store if used
			int offset = rowOffset * (rowByteArrayLength + 1);
			if (data[offset] == 0)
				return null;
			byte[] bytes = new byte[rowByteArrayLength];
			for (int i = 0; i < rowByteArrayLength; i++) {
				bytes[i] = data[offset + i + 1];
			}
			return new Row(bytes);
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	public boolean insertRow(long RID, Row row) {
		try {
			int fileId = Controller.getFileId(RID);
			int rowOffset = Controller.getRowOffset(RID);
			TableDef tableDef = Controller.getTableDef(fileId);
			int rowByteArrayLength = tableDef.getRowSize();

			int offset = rowOffset * (rowByteArrayLength + 1);
			if (data[offset] == 0)
				return false;

			byte[] bytes = row.toByteArray();

			data[offset] = 1;
			for (int i = 0; i < rowByteArrayLength; i++) {
				data[offset + i + 1] = bytes[i];
			}

			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public boolean deleteRow(long RID) {
		try {
			int fileId = Controller.getFileId(RID);
			int rowOffset = Controller.getRowOffset(RID);
			TableDef tableDef = Controller.getTableDef(fileId);
			int rowByteArrayLength = tableDef.getRowSize();

			int offset = rowOffset * (rowByteArrayLength + 1);
			if (data[offset] == 0)
				return false;
			data[offset] = 0;
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public boolean updateRow(long RID, Row row) {
		try {
			int fileId = Controller.getFileId(RID);
			int rowOffset = Controller.getRowOffset(RID);
			TableDef tableDef = Controller.getTableDef(fileId);
			int rowByteArrayLength = tableDef.getRowSize();

			int offset = rowOffset * (rowByteArrayLength + 1);
			if (data[offset] == 0)
				return false;

			byte[] bytes = row.toByteArray();
			for (int i = 0; i < rowByteArrayLength; i++) {
				data[offset + i + 1] = bytes[i];
			}
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public int nextRowOffset(long RID) {
		try {
			int fileId = Controller.getFileId(RID);
			int rowOffset = Controller.getRowOffset(RID);
			TableDef tableDef = Controller.getTableDef(fileId);
			int rowByteArrayLength = tableDef.getRowSize();

			int offset = rowOffset * (rowByteArrayLength + 1);
			for (int i = 0; i < Config.BLOCK_SIZE / (rowByteArrayLength + 1); i++) {
				if (data[offset] == 0)
					return offset;
				offset += rowByteArrayLength;
			}
			return -1;

		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return -1;
		}

	}

}
