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


public class ThreadListenerServer extends Thread {

	private Client client;

	public ThreadListenerServer(Client clie) {
		client = clie;
	}
	public void run() {
		try {
			
			ServerSocket server_cl=new ServerSocket(client.PORT_RECIVE);
			Socket recive;
			
			while(client.isClientConected()) {
				
				recive=server_cl.accept();
				ObjectInputStream flujo_entrada = new ObjectInputStream(recive.getInputStream());
				
				String resp=(String) flujo_entrada.readObject();
				
				if(resp.equals("game")) {
					GestorPlayer nGp=(GestorPlayer) flujo_entrada.readObject();
					client.updatePlayers(nGp);
					String nGc=(String) flujo_entrada.readObject();
					if(nGc.equals("gc")) {
						client.updateComida((GestorVirus)flujo_entrada.readObject());
					}
				}else if(resp.equals("Account")) {
					
					ObjectInputStream entrada = null;
					SSLSocket sslsocket = client.getSslsocket();
					Session session;
					try {
						
						entrada = client.getEntrada();

						session = (Session) entrada.readObject();
						if (session.getEstado().equals(Session.ACCEPT)) {

							GestorPlayer nGp = (GestorPlayer) flujo_entrada.readObject();
							client.updatePlayers(nGp);

							client.updateComida((GestorVirus) flujo_entrada.readObject());

							client.pCharge("login", session.getValue());

						} else if (session.getEstado().equals(Session.ERROR_PASSWORD)) {
							client.loginFail();
						} else if (session.getEstado().equals(Session.ERROR_NICK)) {
							client.NameExist();
						}

						entrada.close();
						sslsocket.close();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
					
					
					
					
					
					
					
					
				}else if(resp.equals("newgc")){
					
					client.updateDataGame((GestorVirus)flujo_entrada.readObject());
				}else if(resp.equals("newgp")) {
					client.updateDataGp((GestorPlayer) flujo_entrada.readObject());
				}else if(resp.equals("finish")) {
					
					client.terminarEnvio();
					client.updateDataGp((GestorPlayer) flujo_entrada.readObject());
					client.PlayersTop();
				}
				recive.close();
			}
			System.out.println("efe");
		} catch (Exception e) {
			
		}
	}
}
