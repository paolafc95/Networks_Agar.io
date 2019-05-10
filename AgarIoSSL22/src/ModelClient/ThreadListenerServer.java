package ModelClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import Controller.GestorPlayer;
import Controller.GestorVirus;
import View.Client;
import View.Session;
import chat.ClientChat;


public class ThreadListenerServer extends Thread {

	private Client client;

	public ThreadListenerServer(Client clie) {
		client = clie;
	}
	public void run() {
		try {
			
			ServerSocket server_cl=new ServerSocket(client.PORT_RECIVE_LOGIN);
			
			Socket recive;
			
			while(client.isClientConected()) {
				
				recive=server_cl.accept();
				
				ObjectInputStream flujo_entrada = new ObjectInputStream(recive.getInputStream());
				
				String resp=(String) flujo_entrada.readObject();
				if(resp.equals("Account")) {
					
					ObjectInputStream entrada = null;
					SSLSocket sslsocket = client.getSslsocket();
					Session session;
					try {
						
						entrada = client.getEntrada();

						session = (Session) entrada.readObject();
						if (session.getEstado().equals(Session.ACCEPT)) {
							ThreadListenerServerGame ts=new ThreadListenerServerGame(client);
							ts.start();
							RecibeMusic rm=new RecibeMusic(client);
							rm.start();
							GestorPlayer nGp = (GestorPlayer) flujo_entrada.readObject();
							client.updatePlayers(nGp);

							client.updateComida((GestorVirus) flujo_entrada.readObject());

							client.pCharge("login", session.getValue());
							break;
						} else if (session.getEstado().equals(Session.ERROR_PASSWORD)) {
							client.loginFail();
						} else if (session.getEstado().equals(Session.ERROR_NICK)) {
							client.NameExist();
						} else if(session.getEstado().equals(Session.ESPECT)) {
							RecibeMusic rm=new RecibeMusic(client);
							rm.start();
							ClientChat cl=new ClientChat();
						
						}

						entrada.close();
						sslsocket.close();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
					
					
					
					
					
					
					
					
				}
				recive.close();
			}
		} catch (Exception e) {
			
		}
	}
}
