package com.litedbAdvanced.storage;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.litedbAdvanced.util.Config;
import com.litedbAdvanced.util.LiteLogger;

public class FileManager {
	private static Map<Integer, RandomAccessFile> files;

	public static boolean init() {
		try {

			files = new HashMap<>();

			// load system file, including all tableName
			File systemFile = new File("litedb.sys");
			Map<Integer, String> tables = new HashMap<>();
			// read system file to get fileId and filename map

			if (systemFile.exists()) {
				// load all table files
				Set<Integer> fileIds = tables.keySet();
				for (int fileId : fileIds) {
					String fileName = tables.get(fileId);
					RandomAccessFile file = new RandomAccessFile(fileName + ".dat", "rw");
					files.put(fileId, file);
				}

			} else {
				RandomAccessFile newSystemFile = new RandomAccessFile("litedb.sys", "rw");
				newSystemFile.setLength(Config.FILE_SIZE);
				newSystemFile.close();
			}
			LiteLogger.info(Main.TAG, "文件控制器初始化");
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}

	}

	public static boolean close() {
		try {
			Set<Integer> fileIds = files.keySet();
			for (int fileId : fileIds) {
				RandomAccessFile rf = files.get(fileId);
				rf.close();
			}
			files.clear();
			LiteLogger.info(Main.TAG, "文件控制器关闭");
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public static Block loadBlock(long RID) {
		try {
			int fileId = Controller.getFileId(RID);
			int blockOffset = Controller.getBlockOffset(RID);

			RandomAccessFile rf = files.get(fileId);
			if (rf == null)
				return null;

			byte[] bytes = new byte[Config.BLOCK_SIZE];
			rf.seek(blockOffset * Config.BLOCK_SIZE);
			rf.read(bytes, 0, Config.BLOCK_SIZE);
			Block block = new Block(bytes);

			return block;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	public static boolean updateBlock(long RID, Block block) {
		try {
			int fileId = Controller.getFileId(RID);
			int blockOffset = Controller.getBlockOffset(RID);

			RandomAccessFile rf = files.get(fileId);
			if (rf == null)
				return false;

			byte[] bytes = new byte[Config.BLOCK_SIZE];
			rf.seek(blockOffset * Config.BLOCK_SIZE);
			rf.write(bytes, 0, Config.BLOCK_SIZE);
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

	public static boolean createFile(int fileId, String fileName) {
		return true;
	}

	public static boolean deleteFile(String fileName) {
		try {
			File file = new File(fileName);
			int fileId = Controller.getFileId(fileName);
			RandomAccessFile rf = files.get(fileId);
			if (rf != null) {
				rf.close();
				files.remove(fileId);
			}
			if (file.exists())
				file.delete();
			return true;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

}