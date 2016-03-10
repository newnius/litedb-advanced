package com.litedbAdvanced.util;

public class Config {

	public static final int FILE_SIZE = 10 * 1024 * 1024;// 10M
	public static final int BLOCK_SIZE = 4 * 1024;// 4K
	public static final String DATA_DICTORY = "db";

	private static int port = 1888;

	public static int getPort() {
		return port;
	}

}
