package com.litedbAdvanced.netio;

import java.util.Scanner;

import com.litedbAdvanced.global.Config;

public class Main {
	private static Server server;
	
	public static void init(){
		server = new Server();
		server.start();
		
        Config.setDBPrepared(true);
        Config.setRemoteAccessAvailable(true);
        
        
        
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String sql = "";
            while (scanner.hasNext()) {
                sql += " "+scanner.next();
                if (sql.endsWith(";")) {
                    break;
                }
            }

            if (sql.equals(" shutdown;")) {
            	scanner.close();
                System.exit(0);
            }
            System.out.println(sql);
        }
	}
}
