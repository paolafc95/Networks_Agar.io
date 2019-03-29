package ModelClient;

import View.Client;

public class ThreadCharge extends Thread{

	private Client client;
	public ThreadCharge(Client clie) {
		client=clie;
	}

	public void run() {
		try {

			while (client.isChargering()) {
				
				String resp = client.getReader().readLine().trim();
				String stad= resp.split(";")[0];
				if (stad.equals("StartGame")) {
					
					client.pGame(resp);
					client.setChargering(false);
					break;
				}
				if(stad.equals("time")) {
					client.updateTime(resp.split(";")[1]);
				}else {
					client.updateList(resp.split(";")[0]);
					client.updateTime(resp.split(";")[1]);
				}
				
			}
		} catch (Exception e) {

		}
	}
}
