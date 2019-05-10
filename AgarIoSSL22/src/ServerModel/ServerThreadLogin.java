package ServerModel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import Controller.GestorVirus;
import Model.Player;
import View.Session;
import chat.ClientChat;
import chat.ClientChat;
import chat.ServerChat;

public class ServerThreadLogin extends Thread{

	private ServerModel server;
	private Socket socket;
	

	public ServerThreadLogin(Socket sock, ServerModel ser) {
		socket=sock;
		server=ser;
		
		}
	public void run() {
		
		InetAddress ipCliente = socket.getInetAddress();
		String ipClienteString ="";
		String n1=socket.getRemoteSocketAddress().toString().replaceAll("/", "").split(":")[0];
		System.out.println(n1);
		ipClienteString = n1;
		ObjectInputStream flujo_entrada;
		String passwClient = "cliente";
		String passwKs = "keystore";
		String nameKs = "pfc.store"; // se establece el nombre de la KeyStore -sujeta a cambios-
		char[] passwordKs = passwKs.toCharArray();
		KeyStore ks; // se crea una nueva KeyStore
		SSLSocket sslsocket = null;
		ObjectInputStream entrada = null;
		ObjectOutputStream salida = null;
		SSLServerSocketFactory ssf =null;
		SSLServerSocket s = null;
		try {
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(nameKs), passwordKs);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SUNX509");
			kmf.init(ks, passwordKs);
			SSLContext scon = SSLContext.getInstance("TLS");
			scon.init(kmf.getKeyManagers(), null, null);
			
			ssf = scon.getServerSocketFactory();
			
			s = (SSLServerSocket) ssf.createServerSocket(8000); // (SERVER_PORT)
			sslsocket = (SSLSocket) s.accept();
			
		}catch (Exception e) {
				System.out.println(e.getMessage()+"ssl error");
			}
		try {
			flujo_entrada = new ObjectInputStream(socket.getInputStream());
			String data=(String) flujo_entrada.readObject();
			flujo_entrada.close();
			//!server.isInGame()
			
			if(!server.isInGame()){
					
				try {
					
					entrada = new ObjectInputStream(sslsocket.getInputStream());
					
					salida = new ObjectOutputStream(sslsocket.getOutputStream());

					
					// while (true){
					
					
					Session usuPass = (Session) entrada.readObject(); //Objeto Cliente con Usuario y Contraseña enviado por el cliente.
					
					if(usuPass.getEstado().equals(Session.VERIFICAR_LOGIN)) {
						//El servidor va a abrir el archivo de texto buscando el usuario
						//El formato de cada linea es: nickname;password;email
						File archivo = null;
					    FileReader fr = null;
					    BufferedReader br = null;
					    
					    // Apertura del fichero y creacion de BufferedReader para poder
				        // hacer una lectura comoda (disponer del metodo readLine()).
					      
					    archivo = new File ("./data/archivo.txt"); //RUTA DONDE SE SUPONE SE VA A GUARDAR EL TXT EN EL SERVIDOR
				        fr = new FileReader (archivo);
				        br = new BufferedReader(fr);
				        
				     // Lectura del fichero
				         String linea;
				         linea = br.readLine();
				         boolean seEncontroUsuario = false;
				         boolean errorPassword=false;
				        
				         while(linea!=null&&!seEncontroUsuario&&!errorPassword) {
				        	 //Vamos buscando usuario y contraseña
				        	 String[] datos = linea.split(";");
				        	
				        	 if(datos[2].equals(usuPass.getEmail())) {
				        		 if(datos[1].equals(usuPass.getPass())) {
				  
				        			 //AQUI VA EL CODIGO PARA INICIAR EL JUEGO (Abrir la pestaña del juego)
				        			seEncontroUsuario = true;
				        			
									int id = server.addPlayer(datos[0], ipClienteString);
									
									Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND_LOGIN);
									ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
											envioActualizacionMovimiento.getOutputStream());
									usuPass.setEstado(Session.ACCEPT);
									usuPass.setValue(id);
									paqueteReenvio.writeObject("Account");
									salida.writeObject(usuPass);
									//paqueteReenvio.writeObject(usuPass);
									paqueteReenvio.writeObject(server.getGp());
									paqueteReenvio.writeObject(server.getGc());
									envioActualizacionMovimiento.close();
									//envia a los demas usuarios la informacion del nuevo jugador
									for (String ip : server.getNickIP().values()) {
										if (!ip.equals(ipClienteString)) {
											// la primera condicion es que si la IP de donde llega el paquete es diferente a
											// la IP actual en listaIP, envie el flujo
											Socket envioActualizacionPlayer = new Socket(ip,
													server.PORT_SEND);
											ObjectOutputStream paqueteReenvioPlayer = new ObjectOutputStream(
													envioActualizacionPlayer.getOutputStream());	
											paqueteReenvioPlayer.writeObject("newgp");
											paqueteReenvioPlayer.writeObject(server.getGp());
											envioActualizacionPlayer.close();
										}
									}
				        			if(server.getGp().getPlayers().size()==server.MINPLAYERS&&!server.isTimeLimit()) {
				        				server.startTime();
				        			}
				        			if(server.getGp().getPlayers().size()==server.MAXPLAYERS) {
				        				server.setTimeLimit(true);
				        				server.StartGame();
				        			}
				        			if(server.isTimeLimit()) {
				        				System.out.println("Ya no se puede unir al juego, pero será espectador");
				        				
				        				//DEBO OBTENER LA IMAGEN DE ALGUNO DE LOS JUGADORES ACTUALES
				        				
				        				//DEBO PINTAR EL NUEVO ESCENARIO PARA CHAT
				        				if(server.getServidorChat() ==null) {//SI ES EL PRIMER ESPECTADOR, INICIE EL HILO DEL CHATSERVER
				        					//Esto es equivalente a decir, si servidorChat == null
				        					server.setServidorChat(new ServerChat());
				        				}
				        				
				       
				        				
				        			}
				        		 }else {
				        			 //Usuario correcto, contraseña invalida
				        			 
				        			errorPassword=true;
				        			Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND_LOGIN);
									ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
											envioActualizacionMovimiento.getOutputStream());
									usuPass.setEstado(Session.ERROR_PASSWORD);
									paqueteReenvio.writeObject("Account");
									salida.writeObject(usuPass);
									//paqueteReenvio.writeObject(usuPass);
									envioActualizacionMovimiento.close();
				        		 }
				        	 }
				        	 
				        	 linea = br.readLine();
				         }
				         if(!seEncontroUsuario) {
				        	 //JOptionPane.showMessageDialog(null, "Usuario o contraseña invalida");
				         }
				         
				         fr.close();

				         br.close();
						
					}else if(usuPass.getEstado().equals(Session.VERIFICAR_REGISTRO)) {
						
						//Codigo para verificar el registro.
						//El servidor va a abrir el archivo de texto buscando el usuario
						//El formato de cada linea es: nickname;password;email
						File archivo = null;
					    FileReader fr = null;
					    BufferedReader br = null;
					    
					    // Apertura del fichero y creacion de BufferedReader para poder
				        // hacer una lectura comoda (disponer del metodo readLine()).
					      
					    archivo = new File ("./data/archivo.txt"); //RUTA DONDE SE SUPONE SE VA A GUARDAR EL TXT EN EL SERVIDOR
				        fr = new FileReader (archivo);
				        br = new BufferedReader(fr);
				        
				     // Lectura del fichero
				         String linea;
				         linea = br.readLine();
				         boolean seEncontroUsuario = false;
				         while(linea!=null&&!seEncontroUsuario) {
				        	 //Vamos a verificar que el email no se encuentre en ninguno de los registros.
				        	 String[] datos = linea.split(";");
				        	 if(datos[2].equals(usuPass.getEmail())) {
				        		 Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND_LOGIN);
									ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
											envioActualizacionMovimiento.getOutputStream());
									usuPass.setEstado(Session.ERROR_NICK);
									paqueteReenvio.writeObject("Account");
									salida.writeObject(usuPass);
									//paqueteReenvio.writeObject(usuPass);
									envioActualizacionMovimiento.close();
				        		 seEncontroUsuario = true;
				        		 
				        	 }
				        	 
				        	 linea = br.readLine();
				         }
				         
				         //Si 
				         fr.close();

				         br.close();
				         if(!seEncontroUsuario) {
				        	 server.registrarUsuario(usuPass);
				        	 	int id = server.addPlayer(usuPass.getId(), ipClienteString);
								
								Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND_LOGIN);
								ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
										envioActualizacionMovimiento.getOutputStream());
								usuPass.setEstado(Session.ACCEPT);
								usuPass.setValue(id);
								paqueteReenvio.writeObject("Account");
								salida.writeObject(usuPass);
								//paqueteReenvio.writeObject(usuPass);
								paqueteReenvio.writeObject(server.getGp());
								paqueteReenvio.writeObject(server.getGc());
								envioActualizacionMovimiento.close();
								for (String ip : server.getNickIP().values()) {
									if (!ip.equals(ipClienteString)) {
										// la primera condicion es que si la IP de donde llega el paquete es diferente a
										// la IP actual en listaIP, envie el flujo
										Socket envioActualizacionPlayer = new Socket(ip,
												server.PORT_SEND);
										ObjectOutputStream paqueteReenvioPlayer = new ObjectOutputStream(
												envioActualizacionPlayer.getOutputStream());	
										paqueteReenvioPlayer.writeObject("newgp");
										paqueteReenvioPlayer.writeObject(server.getGp());
										envioActualizacionPlayer.close();
									}
								}
								if(server.getGp().getPlayers().size()==1&&!server.isTimeLimit()) {
			        				server.startTime();
			        			}
			        			if(server.getGp().getPlayers().size()==5) {
			        				server.setTimeLimit(true);
			        				server.StartGame();
			        			}
			        			if(server.isTimeLimit()) {
			        				System.out.println("Ya no se puede unir al juego, pero será espectador");
			        				
			        				//DEBO OBTENER LA IMAGEN DE ALGUNO DE LOS JUGADORES ACTUALES
			        				
			        				//DEBO PINTAR EL NUEVO ESCENARIO PARA CHAT
			        				if(server.getServidorChat() ==null) {//SI ES EL PRIMER ESPECTADOR, INICIE EL HILO DEL CHATSERVER
			        					//Esto es equivalente a decir, si servidorChat == null
			        					server.setServidorChat(new ServerChat());
			        				}
			        				
			        				ClientChat nuevoCliente = new ClientChat();
			        				
			        			}
				         }
						
					}
					
					s.close();
					sslsocket.close();
				} catch (Exception e) {

					System.out.println(e.getMessage());
				}
			}else {
				
				try {
					entrada = new ObjectInputStream(sslsocket.getInputStream());
					
					salida = new ObjectOutputStream(sslsocket.getOutputStream());
					
					Session usuPass = (Session) entrada.readObject();
					Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND_LOGIN);
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
							envioActualizacionMovimiento.getOutputStream());
					usuPass.setEstado(Session.ESPECT);
					paqueteReenvio.writeObject("Account");
					salida.writeObject(usuPass);
					paqueteReenvio.writeObject(usuPass);
					envioActualizacionMovimiento.close();
					s.close();
					sslsocket.close();
					System.out.println("Ya no se puede unir al juego, pero será espectador");
					//DEBO OBTENER LA IMAGEN DE ALGUNO DE LOS JUGADORES ACTUALES
					
					//DEBO PINTAR EL NUEVO ESCENARIO PARA CHAT
					if(server.getServidorChat() ==null) {//SI ES EL PRIMER ESPECTADOR, INICIE EL HILO DEL CHATSERVER
    					//Esto es equivalente a decir, si servidorChat == null
    					server.setServidorChat(new ServerChat());
    				}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				
				// while (true){
				
				
				
				
				
			}
			
			socket.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
