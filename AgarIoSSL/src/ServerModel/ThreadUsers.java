package ServerModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ModelClient.ThreadListenerServer;

public class ThreadUsers extends Thread{

	private ServerModel server;
	
	public ThreadUsers(ServerModel serv) {
		server=serv;
		
	}
	public void run() {
		try{			
			
			
                while(true){
                
                	Socket miSocket = server.getConectionSocket().accept();	
                	ServerThreadUser s=new ServerThreadUser(miSocket,server);
                	s.start();
                }
               
	}catch(Exception e){e.printStackTrace();}
	}
}
