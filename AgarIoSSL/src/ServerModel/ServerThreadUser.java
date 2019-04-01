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

	public void run() {
		
		InetAddress ipCliente = socket.getInetAddress();
		String ipClienteString = ipCliente.getHostAddress(); //Aqui queda almacenada la IP del cliente en un string
		ObjectInputStream flujo_entrada;
		String passwClient = "cliente";
		String passwKs = "keystore";
		String nameKs = "pfc.store"; // se establece el nombre de la KeyStore -sujeta a cambios-
		char[] passwordKs = passwKs.toCharArray();
		KeyStore ks; // se crea una nueva KeyStore
		SSLSocket sslsocket = null;
		ObjectInputStream entrada = null;
		ObjectOutputStream salida = null;
		try {
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(nameKs), passwordKs);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SUNX509");
			kmf.init(ks, passwordKs);
			SSLContext scon = SSLContext.getInstance("TLS");
			scon.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory ssf = scon.getServerSocketFactory();
			SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(8000); // (SERVER_PORT)
			sslsocket = (SSLSocket) s.accept();
			
		}catch (Exception e) {
				// TODO: handle exception
			}
		try {
			flujo_entrada = new ObjectInputStream(socket.getInputStream());
			String data=(String) flujo_entrada.readObject();
			
			if(data.equals("game")) {
				//flujo_entrada2 = new ObjectInputStream(socket.getInputStream());
				Player actualizacionPersonaje = (Player) flujo_entrada.readObject();
				
				if (actualizacionPersonaje.isActivo()) {

					
					// Ya hemos recibido la informacion del ballon del cliente, ahora hay que
					// reenvirlo a todos los clinetes y actualizar
					server.getGp().getPlayers().remove(actualizacionPersonaje.getID());
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
			}else if(data.equals("session")&&!server.isInGame()){
				
			
				
				try {
					entrada = new ObjectInputStream(sslsocket.getInputStream());
					salida = new ObjectOutputStream(sslsocket.getOutputStream());

					// while (true){
					
					
					Session usuPass = (Session) entrada.readObject();; //Objeto Cliente con Usuario y Contraseña enviado por el cliente.
					
					System.out.println("LL");
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
				         System.out.println(usuPass.getEmail()+" "+usuPass.getPass());
				         while(linea!=null&&!seEncontroUsuario&&!errorPassword) {
				        	 //Vamos buscando usuario y contraseña
				        	 String[] datos = linea.split(";");
				        	
				        	 if(datos[2].equals(usuPass.getEmail())) {
				        		 if(datos[1].equals(usuPass.getPass())) {
				        			System.out.println("ds");
				        			 //AQUI VA EL CODIGO PARA INICIAR EL JUEGO (Abrir la pestaña del juego)
				        			seEncontroUsuario = true;
				        			
									int id = server.addPlayer(datos[0], ipClienteString);
									
									Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND);
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
				        			if(server.getGp().getPlayers().size()==1&&!server.isTimeLimit()) {
				        				server.startTime();
				        			}
				        			if(server.getGp().getPlayers().size()==5) {
				        				server.setTimeLimit(true);
				        				server.StartGame();
				        			}
				        		 }else {
				        			 //Usuario correcto, contraseña invalida
				        			 
				        			errorPassword=true;
				        			Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND);
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
				        		 Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND);
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
				         br.close();
				         if(!seEncontroUsuario) {
				        	 server.registrarUsuario(usuPass);
				        	 	int id = server.addPlayer(usuPass.getId(), ipClienteString);
								
								Socket envioActualizacionMovimiento = new Socket(ipClienteString, server.PORT_SEND);
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
				         }
						
					}
					entrada.close();
					salida.close();
					sslsocket.close();
				} catch (Exception e) {

				}
			}
			System.out.println("sl");
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
