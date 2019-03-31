package ModelClient;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Controller.GestorPlayer;
import Controller.GestorVirus;
import View.Client;


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
				}else if(resp.split(";").length==3) {
					
					String stad=resp.split(";")[0];
					String tipe=resp.split(";")[1];
					if(tipe.equals("login")) {
						
						if(stad.equals("accept")) {
							System.out.println("ngp");
							GestorPlayer nGp=(GestorPlayer) flujo_entrada.readObject();
							client.updatePlayers(nGp);
							System.out.println("ngc2");
							System.out.println("ngc");
							client.updateComida((GestorVirus)flujo_entrada.readObject());
							System.out.println("c");
							client.pCharge("login",Integer.parseInt(resp.split(";")[2]));
							System.out.println("cd");
							//client.setClientConected(false);
						}else if(stad.equals("create")){
							client.loginFail();
						}else {
							client.loginFail();
						}
					}else if(tipe.equals("create")){
						if(stad.equals("accept")) {
							//client.pCharge("create");
							client.setClientConected(false);
						}else {
							client.NameExist();
						}
					}
				}else if(resp.equals("newgc")){
					
					client.updateDataGame((GestorVirus)flujo_entrada.readObject());
				}
				recive.close();
			}
		} catch (Exception e) {
			
		}
	}
}
