package ServerModel;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import Controller.GestorVirus;
import Model.Player;
import ModelClient.ThreadListenerServer;

public class ThreadUsers extends Thread{

	private ServerModel server;
	
	public ThreadUsers(ServerModel serv) {
		server=serv;
		
	}
	public void run() {
		try{		
			
                while(true){
                
                	if(!server.isInThread()) {
                		
                	server.setInThread(true);
                	Socket socket = server.getConectionSocket().accept();
                	
                	ServerThreadUser s=new ServerThreadUser(socket,server);
                	s.start();
                	System.out.println("d");
                	}
                	
                	
                	/*
                	String ipClienteString ="";
            		
            		String n1=socket.getRemoteSocketAddress().toString().replaceAll("/", "").split(":")[0];
            		ipClienteString = n1;//Aqui queda almacenada la IP del cliente en un string
            		ObjectInputStream flujo_entrada;
            		ObjectInputStream entrada = null;
            		ObjectOutputStream salida = null;
            		
            		try {
            			flujo_entrada = new ObjectInputStream(socket.getInputStream());
            			String data=(String) flujo_entrada.readObject();
            			
            			if(data.equals("game")) {
            				//flujo_entrada2 = new ObjectInputStream(socket.getInputStream());
            				Player actualizacionPersonaje = (Player) flujo_entrada.readObject();
            				
            				if (actualizacionPersonaje.isActivo()) {

            					server.getGp().getPlayers().remove(actualizacionPersonaje.getID());
            					server.getGp().getPlayers().add(actualizacionPersonaje);
            					// Ya hemos recibido la informacion del ballon del cliente, ahora hay que
            					// reenvirlo a todos los clinetes y actualizar
            					/*
            					
            					if(actualizacionPersonaje.getID()==server.getGp().size()-1) {
            						server.getGp().getPlayers().add(actualizacionPersonaje);
            					}else {
            						server.getGp().getPlayers().add(actualizacionPersonaje.getID(),actualizacionPersonaje);
            					}
            					
            					for (String ip : server.getNickIP().values()) {
            						if (!ip.equals(ipClienteString)) {
            							// la primera condicion es que si la IP de donde llega el paquete es diferente a
            							// la IP actual en listaIP, envie el flujo
            							Socket envioActualizacionMovimiento = new Socket(ip,
            									server.PORT_SEND);
            							ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
            									envioActualizacionMovimiento.getOutputStream());
            							
            							paqueteReenvio.writeObject("game");
            							paqueteReenvio.writeObject(server.getGp());
            							paqueteReenvio.close();
            							envioActualizacionMovimiento.close();
            						}
            					}
            					//ObjectInputStream flujo_entrada3=new ObjectInputStream(socket.getInputStream());
            					String data3=(String) flujo_entrada.readObject();
            					if(data3.equals("gc")) {
            						//ObjectInputStream flujo_entrada4=new ObjectInputStream(socket.getInputStream());
            						GestorVirus gvN=(GestorVirus) flujo_entrada.readObject();
            						server.gestorActualizado(gvN);
            						for (String ip : server.getNickIP().values()) {
            							if (!ip.equals(ipClienteString)) {
            								Socket envioActualizacionMovimiento = new Socket(ip,
            								server.PORT_SEND);
            								ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
            								envioActualizacionMovimiento.getOutputStream());
            								paqueteReenvio.writeObject("gc");
            								paqueteReenvio.writeObject(server.getGc());
            								envioActualizacionMovimiento.close();
            								
            							}
            						}
            						
            					}else {
            						
            						Socket envioActualizacionMovimiento = new Socket(ipClienteString,
            								server.PORT_SEND);
            						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
            								envioActualizacionMovimiento.getOutputStream());
            						paqueteReenvio.writeObject("no gc");
            						envioActualizacionMovimiento.close();
            					}
            					
            				}
            				
            			}else if(data.equals("gameStart")){
            				
            				//server.StartGame();
            			}
            			
            				
            			server.setInThread(false);
            			socket.close();
            		} catch (IOException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		} catch (ClassNotFoundException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
               */
                }
               
	}catch(Exception e){e.printStackTrace();}
	}
}
