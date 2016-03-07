package com.litedbAdvanced.netio;

import com.litedbAdvanced.util.Config;

public class Main {
	private static Server server;
	private static boolean remoteAccessAvailable = false;

	public static void init() {
		remoteAccessAvailable = true;
		server = new Server(Config.getPort());
		server.start();
	}

	public static void close() {
		remoteAccessAvailable = false;
	}

	public static boolean isRemoteAccessAvailable() {
		return remoteAccessAvailable;
	}
	
	
}
