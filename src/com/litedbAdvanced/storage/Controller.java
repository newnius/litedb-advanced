package com.litedbAdvanced.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.litedbAdvanced.util.Config;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

public class Controller {
	private static Map<String, Integer> fileIds;
	private static Map<String, TableDef> tableDefs;

	// 缓存池
	private static LRUCache<Integer, Block> lru;

	public static int getFileId(long RID) {
		int fileId = (int) (RID / 1000000);
		return fileId;
	}

	public static int getBlockId(long RID) {
		int blockId = (int) (RID / 1000);
		return blockId;
	}

	public static int getRowOffset(long RID) {
		int rowOffset = (int) (RID % 1000) - 1;
		return rowOffset;
	}

	public static int getBlockOffset(long RID) {
		int rowOffset = (int) ((RID / 1000) % 1000) - 1;
		return rowOffset;
	}

	public static int getFileId(String tableName) {
		if (fileIds.containsKey(tableName))
			return fileIds.get(tableName);
		return 0;
	}

	public static TableDef getTableDef(String tableName) {
		int fileId = fileIds.get(tableName);
		return getTableDef(fileId);
	}

	public static TableDef getTableDef(int fileId) {
		if (tableDefs.containsKey(fileId))
			return tableDefs.get(fileId);
		return null;
	}

	public static void init() {
		// 缓存池初始化
		lru = new LRUCache<Integer, Block>(10);
		LiteLogger.info(Main.TAG, "缓存池初始化");

		fileIds = new HashMap<>();
		tableDefs = new HashMap<>();
	}

	public static boolean createTable(TableDef tableDef) {

		return true;
	}

	public static boolean deleteTable(String tableName) {
		int fileId = fileIds.get(tableName);
		return deleteTable(fileId);
	}

	// 删除表相关所属的所有文件
	public static boolean deleteTable(int tableId) {
		TableDef tableDef = tableDefs.get(tableId);
		String tableName = tableDef.getTableName();
		List<String> indexs = tableDef.getIndexs();
		FileManager.deleteFile(tableName + ".dat");

		for (String index : indexs) {
			FileManager.deleteFile(tableName + "." + index + ".dat"); // delete
		}
		FileManager.deleteFile(tableName + ".def");
		return true;
	}

	// 根据 RID 返回行数据
	public static Row getRow(long RID) {
		int blockId = Controller.getBlockId(RID);
		if (lru.get(blockId) == null) {
			Block block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
			LiteLogger.info(Main.TAG, "缓存池中无此块，读取" + blockId + "号块");
			return block.getRow(RID);
		} else {
			LiteLogger.info(Main.TAG, "缓存池中有此块，读取" + blockId + "号块");
			return ((Block) lru.get(blockId)).getRow(RID);
		}
	}

	// 更新行数据
	public static boolean updateRow(long RID, Row row) {
		int blockId = Controller.getBlockId(RID);
		Block block = null;

		if (lru.get(blockId) == null) {
			block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
		} else {
			block = ((Block) lru.get(blockId));
		}
		block.updateRow(RID, row);
		return true;
	}

	// 删除行数据
	public static boolean deteteRow(long RID) {
		int blockId = Controller.getBlockId(RID);
		Block block = null;
		if (lru.get(blockId) == null) {
			block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
		} else {
			block = ((Block) lru.get(blockId));
		}
		block.deleteRow(RID);
		return true;
	}

	// 添加行
	public static boolean insertRow(long RID, Row row) {
		int blockId = Controller.getBlockId(RID);
		Block block = null;
		if (lru.get(blockId) == null) {
			block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
		} else {
			block = ((Block) lru.get(blockId));
		}
		block.insertRow(RID, row);
		return true;
	}

	public static Block getBlock(int blockId) {
		if (lru.get(blockId) == null) {
			Block block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
		}
		return lru.get(blockId);
	}

	public static long nextRID(String tableName) {
		int blockSum = Config.FILE_SIZE / Config.BLOCK_SIZE;
		int fileId = fileIds.get(tableName);
		int blockIdStart = fileId * 1000;

		for (int i = 0; i < blockSum; i++) {
			int blockId = blockIdStart + i;
			Block block = getBlock(blockId);
			long RID = blockId * 1000;
			int nextRowOffset = block.nextRowOffset(RID);
			if (nextRowOffset != -1)
				return RID + nextRowOffset;
		}
		return -1;
	}

	public static void close() {
		// write back
		Set<Integer> blockIds = lru.getKeys();
		for (int blockId : blockIds) {
			Block block = lru.get(blockId);
			FileManager.updateBlock(blockId, block);
		}
		lru.clear();
		LiteLogger.info(Main.TAG, "缓存池关闭");
	}

}
