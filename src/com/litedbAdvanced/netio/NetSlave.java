package com.litedbAdvanced.netio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.litedbAdvanced.global.Config;
import com.litedbAdvanced.util.LiteLogger;


class NetSlave extends Thread{
	private final static String TAG = "SOCKET";
	private final Socket socket;
	
	public NetSlave(Socket socket){
        this.socket=socket;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));//获得客户端的输入流
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);//获得客户端输出流)
            if (socket.isConnected()) {
                LiteLogger.info(TAG, "Accept " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() );
                out.println(new Gson().toJson(new Message(Message.CONNECTION_CREATED, "")));
            }
            
            com.litedbAdvanced.execute.Main executor = new com.litedbAdvanced.execute.Main();

            while ( Config.isRemoteAccessAvailable()) {
                String query = reader.readLine();
                LiteLogger.info(TAG, "Received：" + query);
                
                if(query.toLowerCase().equals("quit;")){
                    break;
                } else {
                    out.println(new Gson().toJson(new Message(0, query)));
                	executor.execute(query.replace(";", ""));
                }
            }
            out.println(new Gson().toJson(new Message(Message.CONNECTION_CLOSED, "")));
            socket.close();
        } catch (Exception e) {
        	LiteLogger.info(TAG, socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " disconnected.");
        }
    }
    
}