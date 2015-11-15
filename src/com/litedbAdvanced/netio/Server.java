package com.litedbAdvanced.netio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.litedbAdvanced.global.Config;

/**
*
* @author Newnius
*/
public class Server extends Thread {

   public Server() {

   }

   @Override
   public void run() {
       try {
           ServerSocket server = new ServerSocket(Config.getPort());//创建服务器套接字
           Logger.getLogger(Server.class.getName()).log(Level.INFO, null, "Server opened, waiting for client.");
           while (Config.isDBPrepared()&&Config.isRemoteAccessAvailable()) {
               Socket socket = server.accept();//等待客户端连接
               new NetSlave(socket).start();
           }
           server.close();
       } catch (IOException e) {
           //e.printStackTrace();
       }
       
   }
}
