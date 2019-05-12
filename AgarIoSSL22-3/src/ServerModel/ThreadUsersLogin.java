package ServerModel;
import java.net.Socket;

import chat.ServerChat;

public class ThreadUsersLogin extends Thread{

	private ServerModel server;
	public ThreadUsersLogin(ServerModel serv) {
		server=serv;
		
	}
	public void run() {
		try{			
                while(true){
                
                	System.out.println("Pa que se conecten");

                	Socket miSocket2 = server.getConnectionSocketLogin().accept();	
                	ServerThreadLogin s2=new ServerThreadLogin(miSocket2,server);
                	s2.start();
                }
               
	}catch(Exception e){e.printStackTrace();}
	}
}