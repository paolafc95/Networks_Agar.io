package ServerModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.swing.JOptionPane;

import Controller.GestorVirus;
import Model.Player;
import View.Session;



public class ServerThreadUser extends Thread{

	private ServerModel server;
	private Socket socket;

	public ServerThreadUser(Socket sock, ServerModel ser) {
		socket=sock;
		server=ser;
	}

	public synchronized void run() {
		
		
		
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
					*/
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
			
			System.out.println("dds");
			server.setInThread(false);
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
