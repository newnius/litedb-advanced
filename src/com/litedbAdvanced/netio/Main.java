/*
 * @author Newnius
 * 
 * provide socket access from client
 * create a new session for each client
 * 
 * 
 * */
package com.litedbAdvanced.netio;

import com.litedbAdvanced.util.Config;

public class Main {
	private static Server server;
	private static boolean remoteAccessAvailable = false;
	public static final String TAG = "SOCKET";

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
