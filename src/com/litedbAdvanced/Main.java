package com.litedbAdvanced;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.litedbAdvanced.global.Config;
import com.litedbAdvanced.util.LiteLogger;

public class Main {

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
		LiteLogger.addTag("LOGGER");
		LiteLogger.addTag("MAIN");
		LiteLogger.addTag("SOCKET");
		LiteLogger.info("MAIN", "Database starting...");
		
		/* start each module */
		// com.litedbAdvanced.memory.Main.init();
		// com.litedbAdvanced.disk.Main.init();
		com.litedbAdvanced.netio.Main.init();
		Config.setDBPrepared(true);
		LiteLogger.info("MAIN", "Database started.");
	}
	
	private static void close(){
		/* close each module*/
		com.litedbAdvanced.netio.Main.close();
		Config.setDBPrepared(false);
		LiteLogger.info("MAIN", "Database closed.");
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
			default:
				LiteLogger.info("MAIN", "Unknown command.");
		}
	}
	
	public static void modifyLoggerTag(String[] args){
		if(args.length < 3){
			LiteLogger.info("MAIN", ".");
			return;
		}
		String tag = args[2];
		if(args[1].equals("show")){
			LiteLogger.addTag(tag);
		}else if(args[1].equals("hide")){
			LiteLogger.removeTag(tag);
		}else{
			LiteLogger.info("MAIN", "Unknown command.");
		}
	}

}