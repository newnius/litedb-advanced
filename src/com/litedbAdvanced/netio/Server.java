package com.litedbAdvanced.netio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.litedbAdvanced.global.Config;
import com.litedbAdvanced.util.LiteLogger;

/**
*
* @author Newnius
*/
public class Server extends Thread {
	private final String TAG = "SOCKET";
	private ServerSocket server;

   @Override
   public void run() {
       try {
           server = new ServerSocket(Config.getPort());    
           LiteLogger.info(TAG, "Socket server opened, waiting for client.");
           while ( Config.isRemoteAccessAvailable()) {
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
