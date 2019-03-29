package ServerModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ThreadGame extends Thread {

	private ServerModel server;
	public ThreadGame(ServerModel serv) {
		server=serv;
	}
	public void run() {
		try {
			System.out.println("ready");
			BufferedReader reader =
	        		new BufferedReader(new InputStreamReader(server.getConectionSocket().getInputStream()));
	        BufferedWriter writer= 
	        		new BufferedWriter(new OutputStreamWriter(server.getConectionSocket().getOutputStream()));
			while(server.isGameStart()){
                String data1 = reader.readLine().trim();
                System.out.println("ready2 : datos"+ data1);
                String stad=data1.split(";")[0];
                int posPlayer=server.positionPlayer(stad);
                System.out.println("after remove: "+server.getPosPlayers());
                server.getPosPlayers().remove(posPlayer);
                String nPos=data1.split(";")[1];
                server.getPosPlayers().add(posPlayer, nPos);
                System.out.println("before remove: "+server.getPosPlayers());

                for (int i = 0; i < server.getUsers().size(); i++) {
        			try {
        				BufferedWriter bw = (BufferedWriter) server.getUsers().get(i);
        				String enemys=String.join(",", server.posEnemy(server.getPosPlayers(), i));
        				bw.write(enemys+";00");
        				bw.write("\r\n");
        				bw.flush();
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
                /*
                for (int i=0;i<server.getUsers().size();i++){
                    try{
                        BufferedWriter bw= (BufferedWriter)server.getUsers().get(i);
                        bw.write(data1);
                        bw.write("\r\n");
                        bw.flush();
                    }catch(Exception e){e.printStackTrace();}
                }
                */
            }
			
		} catch (Exception e) {
			
		}
	}
}
