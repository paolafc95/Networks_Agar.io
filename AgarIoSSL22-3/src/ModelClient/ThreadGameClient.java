package ModelClient;

import java.io.IOException;

import View.Client;

public class ThreadGameClient extends Thread{

	private Client client;
	public ThreadGameClient(Client clien) {
		client=clien;
	}

	public void run() {
		while(true) {
			String resp;
			try {
				client.getDs().repaint();
				Thread.sleep(10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
