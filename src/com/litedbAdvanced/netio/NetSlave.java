package com.litedbAdvanced.netio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.litedbAdvanced.Slave;
import com.litedbAdvanced.global.Config;


class NetSlave extends Thread{
	private final Socket socket;
    private Slave slave;
	
	public NetSlave(Socket socket){
        this.socket=socket;
        this.slave = new Slave();
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));//获得客户端的输入流
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);//获得客户端输出流)
            if (socket.isConnected()) {
                Logger.getLogger(NetSlave.class.getName()).log(Level.INFO, null, "Accept " + socket.getInetAddress().getHostAddress());
                out.println("Welcome");
            }

            while (Config.isDBPrepared()&&Config.isRemoteAccessAvailable()) {
                String query = reader.readLine();
                Logger.getLogger(NetSlave.class.getName()).log(Level.INFO, null, "Received：" + query);
                if(query.toLowerCase().equals("quit;")){
                    break;
                } else {
                    out.println(slave.execute(query.getBytes()));
                }
            }
            out.println("Connection is closing.");
            socket.close();
        } catch (Exception e) {
            //Logger.getLogger(RemoteAccess.class.getName()).log(Level.INFO, null, e);
        }        
    }
    
}

