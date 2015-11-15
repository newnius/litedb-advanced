package com.litedbAdvanced.netio;

public class Main {
	private static Server server;
	
	public static void init(){
		server = new Server();
		server.start();
	}
}
