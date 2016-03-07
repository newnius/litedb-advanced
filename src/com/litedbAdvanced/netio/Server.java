package com.litedbAdvanced.netio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.litedbAdvanced.util.LiteLogger;

/**
 *
 * @author Newnius
 */
public class Server extends Thread {
	private final String TAG = "SOCKET";
	private ServerSocket server;
	private int port;

	public Server(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			LiteLogger.info(TAG, "Socket server opened, waiting for client.");
			while (Main.isRemoteAccessAvailable()) {
				Socket socket = server.accept();
				new NetSlave(socket).start();
			}
			server.close();
			LiteLogger.info(TAG, "Socket server closed.");
		} catch (IOException e) {
			LiteLogger.info(TAG, "Socket server can not be started.");
		}
	}
}
