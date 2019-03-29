package ModelClient;

import View.Client;

public class ThreadListenerServer extends Thread {

	private Client client;

	public ThreadListenerServer(Client clie) {
		client = clie;
	}
	public void run() {
		try {
			
			while(client.isClientConected()) {
				System.out.println("in");
				String resp=client.getReader().readLine().trim();
				
				if(resp.split(";").length>2) {
					
					String stad=resp.split(";")[0];
					String tipe=resp.split(";")[1];
					if(tipe.equals("login")) {
						
						if(stad.equals("accept")) {
							client.pCharge("login");
							client.setClientConected(false);
						}else if(stad.equals("create")){
							client.loginFail();
						}else {
							client.loginFail();
						}
					}else if(tipe.equals("create")){
						if(stad.equals("accept")) {
							client.pCharge("create");
							client.setClientConected(false);
						}else {
							client.NameExist();
						}
					}
				}else {
					System.out.println(resp);
				}
				
			}
		} catch (Exception e) {
			
		}
	}
}
