package com.litedbAdvanced.storage;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.litedbAdvanced.util.Config;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Serialization;
import com.litedbAdvanced.util.TableDef;

public class FileManager {
	private static Map<Integer, RandomAccessFile> files;
	private static Map<Integer, String> fileNames;
	private static Map<Integer, TableDef> tableDefs;

	@SuppressWarnings("unchecked")
	public static boolean init() {
		try {

			files = new HashMap<>();

			// load system file, including all tableName, including index files
			// name
			File systemFile = new File(Config.DATA_DICTORY + "/litedb.sys");

			if (systemFile.exists()) {
				RandomAccessFile rf = new RandomAccessFile(systemFile, "rw");
				// read system file to get fileId and filename map
				byte[] tem = new byte[Config.FILE_SIZE];
				byte[] byteLength = new byte[4];
				rf.read(byteLength, 0, 4);
				int length = Serialization.byteArrayToInt(byteLength);
				rf.read(tem, 0, length);
				rf.close();

				fileNames = (Map<Integer, String>) Serialization.ByteToObject(tem);

				// load all table files
				Set<Integer> fileIds = fileNames.keySet();
				for (int fileId : fileIds) {
					String fileName = fileNames.get(fileId);
					RandomAccessFile file = new RandomAccessFile(Config.DATA_DICTORY + "/" + fileName, "rw");
					files.put(fileId, file);
				}

			} else {
				RandomAccessFile newSystemFile = new RandomAccessFile(Config.DATA_DICTORY + "/litedb.sys", "rw");
				newSystemFile.setLength(Config.FILE_SIZE);
				byte[] dataBytes = Serialization.ObjectToByte(new HashMap<Integer, String>());
				byte[] bytes = Serialization.intToByteArray(dataBytes.length);
				newSystemFile.write(bytes, 0, 4);
				newSystemFile.write(dataBytes, 0, dataBytes.length);
				newSystemFile.close();
				files = new HashMap<>();
				fileNames = new HashMap<>();
			}
			loadTableDefs();
			LiteLogger.info(Main.TAG, "文件控制器初始化完毕,共加载" + files.size() + "文件");
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}

	}

	public static boolean close() {
		try {
			/* update system file */
			RandomAccessFile newSystemFile = new RandomAccessFile(Config.DATA_DICTORY + "/litedb.sys", "rw");
			byte[] dataBytes = Serialization.ObjectToByte(fileNames);
			byte[] bytes = Serialization.intToByteArray(dataBytes.length);
			newSystemFile.write(bytes, 0, 4);
			newSystemFile.write(dataBytes, 0, dataBytes.length);
			newSystemFile.close();

			Set<Integer> fileIds = files.keySet();
			for (int fileId : fileIds) {
				RandomAccessFile rf = files.get(fileId);
				rf.close();
			}
			files.clear();

			/* update definition file */
			/* update system file */
			RandomAccessFile defFile = new RandomAccessFile(Config.DATA_DICTORY + "/def.sys", "rw");
			byte[] dataBytes2 = Serialization.ObjectToByte(tableDefs);
			byte[] bytes2 = Serialization.intToByteArray(dataBytes2.length);
			defFile.write(bytes2, 0, 4);
			defFile.write(dataBytes2, 0, dataBytes2.length);
			defFile.close();
			tableDefs.clear();

			LiteLogger.info(Main.TAG, "文件控制器关闭");
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadTableDefs() {
		try {
			File defFile = new File(Config.DATA_DICTORY + "/def.sys");

			if (defFile.exists()) {
				RandomAccessFile rf = new RandomAccessFile(defFile, "rw");
				// read definition file to get fileId and tableDef map
				byte[] tem = new byte[Config.FILE_SIZE];
				byte[] byteLength = new byte[4];
				rf.read(byteLength, 0, 4);
				int length = Serialization.byteArrayToInt(byteLength);
				rf.read(tem, 0, length);
				rf.close();
				tableDefs = (Map<Integer, TableDef>) Serialization.ByteToObject(tem);
			} else {
				RandomAccessFile newDefFile = new RandomAccessFile(Config.DATA_DICTORY + "/def.sys", "rw");
				newDefFile.setLength(Config.FILE_SIZE);
				byte[] dataBytes = Serialization.ObjectToByte(new HashMap<Integer, TableDef>());
				byte[] bytes = Serialization.intToByteArray(dataBytes.length);
				newDefFile.write(bytes, 0, 4);
				newDefFile.write(dataBytes, 0, dataBytes.length);
				newDefFile.close();
				tableDefs = new HashMap<>();
			}
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
		}
	}

	public static Block loadBlock(int blockId) {
		try {
			int fileId = Controller.getFileId(blockId);
			int blockOffset = Controller.getBlockOffset(blockId);

			RandomAccessFile rf = files.get(fileId);
			if (rf == null)
				return null;

			byte[] bytes = new byte[Config.BLOCK_SIZE];
			rf.seek(blockOffset * Config.BLOCK_SIZE);
			rf.read(bytes, 0, Config.BLOCK_SIZE);
			Block block = new Block(blockId, bytes);
			LiteLogger.info(Main.TAG, "load block " + blockId);
			return block;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	public static boolean updateBlock(int blockId, Block block) {
		try {
			int fileId = Controller.getFileId(blockId);
			int blockOffset = Controller.getBlockOffset(blockId);

			RandomAccessFile rf = files.get(fileId);
			if (rf == null)
				return false;

			byte[] bytes = block.data;
			rf.seek(blockOffset * Config.BLOCK_SIZE);
			rf.write(bytes, 0, Config.BLOCK_SIZE);
			LiteLogger.info(Main.TAG, "update block " + blockId);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public static boolean createFile(int fileId, String fileName) {
		try {

			RandomAccessFile rf = new RandomAccessFile(Config.DATA_DICTORY + "/" + fileName, "rw");
			rf.setLength(Config.FILE_SIZE);

			for (int i = 0; i < Config.BLOCK_SIZE; i++)
				rf.write(0);

			files.put(fileId, rf);
			fileNames.put(fileId, fileName);
			LiteLogger.info(Main.TAG, "create file " + fileName);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public static boolean deleteFile(String fileName) {
		try {
			File file = new File(Config.DATA_DICTORY + "/" + fileName);
			int fileId = Controller.getFileId(fileName);
			RandomAccessFile rf = files.get(fileId);
			if (rf != null) {
				rf.close();
				files.remove(fileId);
			}
			fileNames.remove(fileId);

			if (file.exists())
				file.delete();
			LiteLogger.info(Main.TAG, "delete file " + fileName);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public static Map<Integer, String> getFileNames() {
		return fileNames;
	}

	public static Map<Integer, TableDef> getTableDefs() {
		return tableDefs;
	}

}