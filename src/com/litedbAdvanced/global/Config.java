package com.litedbAdvanced.global;

public class Config {
	
    private static boolean isDBPrepared = false;
    private static boolean isRemoteAccessAvailable = false;
    
    private static int port = 1888;

	public static boolean isDBPrepared() {
		return isDBPrepared;
	}

	public static void setDBPrepared(boolean isDBPrepared) {
		Config.isDBPrepared = isDBPrepared;
	}

	public static boolean isRemoteAccessAvailable() {
		return isRemoteAccessAvailable;
	}

	public static void setRemoteAccessAvailable(boolean isRemoteAccessAvailable) {
		Config.isRemoteAccessAvailable = isRemoteAccessAvailable;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Config.port = port;
	}
    
    
    
}
