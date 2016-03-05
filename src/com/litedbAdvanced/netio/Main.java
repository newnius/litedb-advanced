package com.litedbAdvanced.netio;

import com.litedbAdvanced.global.Config;

public class Main {
	private static Server server;
	
	public static void init(){
		Config.setRemoteAccessAvailable(true);
		server = new Server();
		server.start();
	}
	
	public static void close(){
		Config.setRemoteAccessAvailable(false);
	}
}
