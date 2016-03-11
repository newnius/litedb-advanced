package com.litedbAdvanced;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.litedbAdvanced.transaction.LockManager;
import com.litedbAdvanced.util.LiteLogger;

public class Main {
	public static final String TAG = "MAIN";

	public static void main(String[] args) {
		init();
		String command;
		while ((command = readCommand()) != null) {
			executeCommand(command);
		}
	}
	
	private static void init(){
		/* select which log (identified by tag) to be shown 
		 * if LOGGER tag is set, it must be the first
		 * */
		//LiteLogger.addTag("LOGGER");
		//LiteLogger.addTag("MAIN");
		LiteLogger.addTag("SOCKET");
		LiteLogger.addTag("TRANSACTION");
		LiteLogger.addTag("EXECUTOR");
		LiteLogger.addTag("STORAGE");
		LiteLogger.addTag("PARSER");
		LiteLogger.addTag("OPTIMIZER");
		
		
		
		LiteLogger.info("MAIN", "Database starting...");
		
		/* start each module */
		// com.litedbAdvanced.memory.Main.init();
		// com.litedbAdvanced.storage.Main.init();
		com.litedbAdvanced.netio.Main.init();
		com.litedbAdvanced.storage.Main.init();
		com.litedbAdvanced.transaction.Main.init();
		com.litedbAdvanced.parser.Main.init();
		LiteLogger.info(TAG, "Database started.");
	}
	
	private static void close(){
		/* close each module*/
		com.litedbAdvanced.netio.Main.close();
		com.litedbAdvanced.storage.Main.close();
		com.litedbAdvanced.transaction.Main.close();
		LiteLogger.info(TAG, "Database closed.");
	}

	private static String readCommand() {
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
			String command = bf.readLine();
			return command;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void executeCommand(String command) {
		if (command == null)
			return;
		String[] params = command.split(" ");
		switch(params[0]){
			case "shutdown":
				close();
				System.exit(0);
				break;
			case "logger":
				modifyLoggerTag(params);
				break;
			case "SLock":
				SLock(params);
				break;
			case "unSLock":
				unSLock(params);
				break;
			case "XLock":
				XLock(params);
				break;
			case "unXLock":
				unXLock(params);
				break;
			case "testWhere":
				testWhere(params);
				break;
			case "commit":
				break;
				
			default:
				LiteLogger.info(TAG, "Unknown command.");
		}
	}
	
	public static void modifyLoggerTag(String[] args){
		if(args.length < 3){
			LiteLogger.info(TAG, "tag not specified.");
			return;
		}
		String tag = args[2];
		if(args[1].equals("show")){
			LiteLogger.addTag(tag);
		}else if(args[1].equals("hide")){
			LiteLogger.removeTag(tag);
		}else{
			LiteLogger.info(TAG, "Unknown command.");
		}
	}
	
	public static void SLock(String[] args){
		if(args.length < 2){
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.SLockRID(rid);
	}
	
	public static void unSLock(String[] args){
		if(args.length < 2){
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.unSLockRID(rid);
	}
	
	public static void XLock(String[] args){
		if(args.length < 2){
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.XLockRID(rid);
	}
	
	public static void unXLock(String[] args){
		if(args.length < 2){
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		int rid = Integer.parseInt(args[1]);
		LockManager.unXLockRID(rid);
	}
	
	public static void testWhere(String[] args){
		if(args.length < 2){
			LiteLogger.info(TAG, "whereClause not specified.");
			return;
		}
		LiteLogger.info(Main.TAG, com.litedbAdvanced.execute.Main.testWhere(args[1])?"true":"false");
	}

}