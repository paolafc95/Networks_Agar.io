package ServerModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ThreadUsers extends Thread{

	private ServerModel server;
	
	public ThreadUsers(ServerModel serv) {
		server=serv;
		
	}
	public void run() {
		try{
			System.out.println("en hilo");
	        BufferedReader reader =
	        		new BufferedReader(new InputStreamReader(server.getConectionSocket().getInputStream()));
	        BufferedWriter writer= 
	        		new BufferedWriter(new OutputStreamWriter(server.getConectionSocket().getOutputStream()));
                while(server.getUsers().size()<6&&!server.isTimeLimit()){
                	
                	
                	String data1 = reader.readLine().trim(); 
        	        if(data1.equals("load")) {
        	        	System.out.println("hilo t");
                    	if (server.getUsers().size()==2&&!server.isStartTime()) {
    						server.setStartTime(true);
    						server.startTime();
    					}
                    	
        	        	String players= String.join(",", server.getPlayersOnline());
        	        	System.out.println(server.getUsers().size());
                        for (int i=0;i<server.getUsers().size();i++){
                            try{
                                BufferedWriter bw= (BufferedWriter)server.getUsers().get(i);  
                                bw.write(players+";"+server.getTimeLoad());
                                bw.write("\r\n");
                                bw.flush();
                            }catch(Exception e){e.printStackTrace();}
                        }
        	        }else if(!server.isTimeLimit()){
        	        	BufferedWriter bw1=writer;
        	        	System.out.println(data1+" hilo users ");
            	        String tipe=data1.split(";")[2];
            	        if(tipe.equals("login")) {
            	        
            	        	if(server.userExist(data1)) {
            		        	if(server.passwordCorrect(data1)) {
            		        		server.getUsers().add(writer);
            		        		server.getPlayersOnline().add(data1.split(";")[0]);
            			        	bw1.write("accept;login;2:00");
            			        	bw1.write("\r\n");
            			        	bw1.flush();
            			        	System.out.println("login 1");
            		        	}else {
            		        		bw1.write("error;login;2:00");
            		        		bw1.write("\r\n");
            		        		bw1.flush();
            		        		System.out.println("login 2");
            		        	}
            		        	
            		        }else {
            		        	bw1.write("create;login;2:00");
            		        	bw1.write("\r\n");
            		        	bw1.flush();
            		        	System.out.println("login 3");
            		        }
            	        }else {
            	        	System.out.println("create"+data1);
            	        	if(server.createNewUser(data1)) {
            	        		server.getUsers().add(writer);
            	        		server.getPlayersOnline().add(data1.split(";")[0]);
            		        	bw1.write("accept;create;2:00");
            		        	bw1.write("\r\n");
            		        	bw1.flush();
            	        	}else {
            	        		bw1.write("error;create;2:00");
            	        		bw1.write("\r\n");
            	        		bw1.flush();
            	        	}
            	        }
        	        }
                    System.out.println("nn se cerro!!");
                }
                System.out.println("se cerro!!");
                //server.startgame();
               
	}catch(Exception e){e.printStackTrace();}
	}
}
