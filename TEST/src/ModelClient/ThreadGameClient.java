package ModelClient;

import java.io.IOException;

import View.Client;

public class ThreadGameClient extends Thread{

	private Client client;
	public ThreadGameClient(Client clien) {
		client=clien;
	}

	public void run() {
		while(client.isGaming()) {
			String resp;
			try {
				resp = client.getReader().readLine().trim();
				String stad= resp.split(";")[0];
				client.updateList(stad);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
