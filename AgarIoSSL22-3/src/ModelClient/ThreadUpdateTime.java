package ModelClient;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import View.Client;

public class ThreadUpdateTime extends Thread{

	private Client client;
	public ThreadUpdateTime(Client client) {
		this.client=client;
	}

	public void run() {
		try {
			ServerSocket server_cl=new ServerSocket(client.PORT_RECIVETIME);
			Socket recive;
			
			while(true) {
				
				recive=server_cl.accept();
				ObjectInputStream flujo_entrada = new ObjectInputStream(recive.getInputStream());
				
				String resp=(String) flujo_entrada.readObject();
				if(resp.split(";")[0].equals("wait")) {
					client.getDs().updateWait(resp.split(";")[1]);
					
				}else {
					
					client.getDs().updateGame(resp.split(";")[1]);
					
					
					if(resp.split(";")[1].equals("--:--")) {
						client.terminarEnvio();
					}else {
						
						client.iniciarEnvio();
					}
				}
				recive.close();
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
			
	}
}
