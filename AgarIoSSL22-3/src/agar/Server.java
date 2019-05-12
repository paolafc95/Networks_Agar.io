
package agar;


import ServerModel.ServerModel;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
    	ServerSocket mysocket = new ServerSocket(ServerModel.PORT_RECEIVE);
    	ServerSocket mysocketw = new ServerSocket(ServerModel.PORT_RECEIVE_LOGIN);
		//while (true) {
			
			ServerModel server = new ServerModel(mysocket,mysocketw);
			server.StartThread(server);
		//}
		
		/*
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		*/
    	/*
        try {
            GestorVirus gv = new GestorVirus();
            GestorPlayer gp = new GestorPlayer(gv);
            Collision collision = new Collision(gp,gv);
            Infecting infecting = new Infecting(gv);
            collision.start();
            infecting.start();
            System.out.println("Server created");
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (RemoteException| UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
}
