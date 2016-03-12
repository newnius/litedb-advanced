package com.litedbAdvanced.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.litedbAdvanced.util.Config;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

class Controller {
	private static Map<String, Integer> fileIds;
	private static Map<Integer, TableDef> tableDefs;
	private static int nextFileId = 1;
	private static Map<Integer, byte[]> isBlockNeverUsedBitMap;

	public static boolean isBlockNeverUsed(int blockId) {
		int fileId = getFileId(blockId);
		if (isBlockNeverUsedBitMap.get(fileId) == null)
			return false;
		int blockOffset = getBlockOffset(blockId);
		return isBlockNeverUsedBitMap.get(fileId)[blockOffset - 1] == 0;
	}

	public static boolean useBlock(int blockId) {
		int fileId = getFileId(blockId);
		if (isBlockNeverUsedBitMap.get(fileId) == null)
			return false;
		int blockOffset = getBlockOffset(blockId);
		isBlockNeverUsedBitMap.get(fileId)[blockOffset - 1] = 1;
		return true;
	}

	// 缓存池
	private static LRUCache<Integer, Block> lru;

	public static int getFileId(long RID) {
		int fileId = (int) (RID / 1000000);
		return fileId;
	}

	public static int getFileId(int blockId) {
		int fileId = (int) (blockId / 1000);
		return fileId;
	}

	public static int getBlockId(long RID) {
		int blockId = (int) (RID / 1000);
		return blockId;
	}

	public static int getRowOffset(long RID) {
		int rowOffset = (int) (RID % 1000);
		return rowOffset;
	}

	public static int getBlockOffset(long RID) {
		int rowOffset = (int) ((RID / 1000) % 1000);
		return rowOffset;
	}

	public static int getBlockOffset(int blockId) {
		int rowOffset = (int) (blockId % 1000);
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

	public static Map<Integer, TableDef> getTableDefs() {
		return tableDefs;
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
		tableDefs = FileManager.getTableDefs();
		Map<Integer, String> fileNames = FileManager.getFileNames();
		Set<Integer> ids = fileNames.keySet();
		for (int fileId : ids) {
			fileIds.put(fileNames.get(fileId), fileId);
			if (fileId >= nextFileId)
				nextFileId = fileId + 1;
		}

		/* load first block of all file */
		isBlockNeverUsedBitMap = new HashMap<>();
		Set<Integer> ids2 = tableDefs.keySet();
		for (int id : ids2) {
			int blockId = id * 1000;
			Block block = FileManager.loadBlock(blockId);
			isBlockNeverUsedBitMap.put(id, block.data);
		}

		// LiteLogger.info(Main.TAG, "load " + fileIds.size() + " fileId");
		// LiteLogger.info(Main.TAG, "load " + tableDefs.size() + " table def");
		// LiteLogger.info(Main.TAG, "load " + isBlockNeverUsedBitMap.size() + "
		// first block");
	}

	public static int createTable(TableDef tableDef) {
		String fileName = tableDef.getTableName() + ".dat";

		if (fileIds.containsKey(fileName)) {
			LiteLogger.info(Main.TAG, "create table fail, already exist");
			return 1;
		}

		int fileId = nextFileId++;

		/* create data file */
		FileManager.createFile(fileId, fileName);
		fileIds.put(fileName, fileId);
		tableDefs.put(fileId, tableDef);

		/* create index files */
		List<String> indexs = tableDef.getIndexs();
		indexs.add(tableDef.getPrimaryKey());
		
		for (String index : indexs) {
			fileId = nextFileId++;
			fileName = tableDef.getTableName() + "." + index + ".idx";
			fileIds.put(fileName, fileId);
			FileManager.createFile(fileId, fileName);
		}
		com.litedbAdvanced.index.Main.createTable(tableDef.getTableName());
		LiteLogger.info(Main.TAG, "create table " + tableDef.getTableName());
		return 0;
	}

	public static int deleteTable(String tableName) {
		int fileId = fileIds.get(tableName);
		TableDef tableDef = tableDefs.get(fileId);
		String fileName = tableDef.getTableName() + ".dat";

		/* delete data file */
		FileManager.deleteFile(fileName);
		fileIds.remove(fileId);
		tableDefs.remove(fileId);

		/* create index files */
		List<String> indexs = tableDef.getIndexs();
		for (String index : indexs) {
			fileName = tableDef.getTableName() + "." + index + ".idx";
			LiteLogger.info(Main.TAG, index);
			fileId = fileIds.get(fileName);
			fileIds.remove(fileId);
			tableDefs.remove(fileId);
			FileManager.deleteFile(fileName);
		}
		com.litedbAdvanced.index.Main.dropTable(tableName);
		return 0;
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
	public static int updateRow(long RID, Row row) {
		int blockId = Controller.getBlockId(RID);
		Block block = null;

		if (lru.get(blockId) == null) {
			block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
			LiteLogger.info(Main.TAG, "缓存池中无此块，读取" + blockId + "号块");
		} else {
			block = ((Block) lru.get(blockId));
			LiteLogger.info(Main.TAG, "缓存池中有此块，读取" + blockId + "号块");
		}
		block.updateRow(RID, row);
		return 0;
	}

	// 删除行数据
	public static int deleteRow(long RID) {
		int blockId = Controller.getBlockId(RID);
		Block block = null;
		if (lru.get(blockId) == null) {
			block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
			LiteLogger.info(Main.TAG, "缓存池中无此块，读取" + blockId + "号块");
		} else {
			block = ((Block) lru.get(blockId));
			LiteLogger.info(Main.TAG, "缓存池中有此块，读取" + blockId + "号块");
		}
		block.deleteRow(RID);
		return 0;
	}

	// 添加行
	public static int insertRow(long RID, Row row) {
		int blockId = Controller.getBlockId(RID);
		Block block = null;
		if (lru.get(blockId) == null) {
			block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
			LiteLogger.info(Main.TAG, "缓存池中无此块，读取" + blockId + "号块");
		} else {
			block = ((Block) lru.get(blockId));
			LiteLogger.info(Main.TAG, "缓存池中有此块，读取" + blockId + "号块");
		}
		block.insertRow(RID, row);
		return 1;
	}

	private static Block getBlock(int blockId) {
		if (lru.get(blockId) == null) {
			Block block = FileManager.loadBlock(blockId);
			lru.put(blockId, block);
			LiteLogger.info(Main.TAG, "缓存池中无此块，读取" + blockId + "号块");
		}
		return lru.get(blockId);
	}

	public static int nextFileId() {
		return nextFileId;
	}

	public synchronized static long nextRID(String fileName) {
		int blockSum = Config.FILE_SIZE / Config.BLOCK_SIZE;
		if (!fileIds.containsKey(fileName))
			return 0;
		int fileId = fileIds.get(fileName);
		int blockIdStart = fileId * 1000;

		for (int i = 0; i < blockSum; i++) {
			int blockId = blockIdStart + i + 1;
			Block block = getBlock(blockId);
			lru.put(blockId, block);

			if (isBlockNeverUsed(blockId)) {
				useBlock(blockId);
				block.clear();
			}

			long RID = blockId * 1000;
			int nextRowOffset = block.nextRowOffset();
			if (nextRowOffset != -1) {
				LiteLogger.info(Main.TAG, "nextRID = " + RID + nextRowOffset);
				return RID + nextRowOffset;
			} else {
				LiteLogger.info(Main.TAG, "no space available in block " + blockId);
			}

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
		Set<Integer> fileIds = isBlockNeverUsedBitMap.keySet();
		for (int fileId : fileIds) {
			int blockId = fileId * 1000;
			FileManager.updateBlock(blockId, new Block(blockId, isBlockNeverUsedBitMap.get(fileId)));
		}

		LiteLogger.info(Main.TAG, "缓存池关闭");
	}

}
