package com.litedbAdvanced.storage;

import com.litedbAdvanced.util.Config;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

public class Block {

	private int rowByteArrayLength;

	// 数据
	public byte[] data;

	public Block(int blockId, byte[] data) {
		this.data = data;
		int fileId = Controller.getFileId(blockId);
		TableDef tableDef = Controller.getTableDef(fileId);
		this.rowByteArrayLength = tableDef.getRowSize();
	}

	public Row getRow(long RID) {
		int fileId = Controller.getFileId(RID);
		int rowOffset = Controller.getRowOffset(RID);
		TableDef tableDef = Controller.getTableDef(fileId);
		try {
			// first row is used to store if used
			int offset = rowOffset * rowByteArrayLength;
			if (data[rowOffset - 1] == 0)
				return null;
			byte[] bytes = new byte[rowByteArrayLength];
			for (int i = 0; i < rowByteArrayLength; i++) {
				bytes[i] = data[offset + i];
			}
			return new Row(tableDef, bytes);
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	public boolean insertRow(long RID, Row row) {
		try {
			int rowOffset = Controller.getRowOffset(RID);
			int blockId = Controller.getBlockId(RID);
			int offset = rowOffset * rowByteArrayLength;

			if (data[rowOffset - 1] == 1) {
				LiteLogger.info(Main.TAG, "row " + RID + " is already occupied");
				return false;
			}

			byte[] bytes = row.toByteArray();

			data[rowOffset - 1] = 1;
			for (int i = 0; i < rowByteArrayLength; i++) {
				data[offset + i] = bytes[i];
			}
			FileManager.updateBlock(blockId, this);
			LiteLogger.info(Main.TAG, "insert row to " + RID);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public boolean deleteRow(long RID) {
		try {
			int rowOffset = Controller.getRowOffset(RID);
			int blockId = Controller.getBlockId(RID);
			if (data[rowOffset - 1] == 0)
				return false;
			data[rowOffset - 1] = 0;
			FileManager.updateBlock(blockId, this);
			LiteLogger.info(Main.TAG, "delete row " + RID);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public boolean updateRow(long RID, Row row) {
		try {
			int rowOffset = Controller.getRowOffset(RID);
			int blockId = Controller.getBlockId(RID);

			int offset = rowOffset * rowByteArrayLength;
			if (data[rowOffset - 1] == 0)
				return false;

			byte[] bytes = row.toByteArray();
			for (int i = 0; i < rowByteArrayLength; i++) {
				data[offset + i] = bytes[i];
			}
			FileManager.updateBlock(blockId, this);
			LiteLogger.info(Main.TAG, "update row " + RID);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	/* init block */
	public boolean clear() {
		for (int i = 0; i < Config.BLOCK_SIZE / rowByteArrayLength - 1; i++) {
			data[i] = 0;
		}
		LiteLogger.info(Main.TAG, "clear block");
		return true;
	}

	public int nextRowOffset() {
		try {
			for (int i = 0; i < Config.BLOCK_SIZE / rowByteArrayLength - 1; i++) {
				if (data[i] == 0)
					return i + 1;
			}
			return -1;

		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return -1;
		}
	}

}
