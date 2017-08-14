package com.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.Executors;

public class AlarmConsole {
	public static void main(String[] args) {
   	try{
   		System.setProperty("java.rmi.server.hostname",InetAddress.getLocalHost().getHostAddress());
           
           Executors.newSingleThreadExecutor().execute(new Runnable(){
               public void run() {
                   try{
                       while(true){
                           System.out.println("JVM MAX MEMORY: "+Runtime.getRuntime().maxMemory()/1024/1024+"M");
                           System.out.println("JVM IS USING MEMORY:"+Runtime.getRuntime().totalMemory()/1024/1024+"M");
                           Thread.sleep(10*60000);
                       }
                   }catch(Exception e){e.printStackTrace();}
               }
           });
           
           Thread mainT = new Thread(new AlarmTest());
           mainT.start();
       	
   		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
           do {
	            String line = in.readLine();
	            if(line == null || line.equals("quit"))
	                break;
	            if(line == null || line.equals("exit"))
	                break;
	        } while (!Thread.interrupted());
           mainT.interrupt();
   	}catch(Exception e){
   	}
       
       System.exit(0);
	}
}
