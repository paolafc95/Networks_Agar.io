package chat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import ServerModel.ServerModel;

public class ServerChat implements Runnable {
	
	public ServerChat() {
		Thread mihilo = new Thread(this);
		mihilo.start();
	}

	@Override
	public void run() {
		
		try {
			
			ServerSocket servidor = new ServerSocket(ClientChat.PUERTO_CLIENTE);
			
			String nick, ip, mensaje, estado;
			
			ArrayList<String> listaIP = new ArrayList<String>();
			//NECESITO HASMAP
			HashMap<String, String> NickIP = new HashMap<String, String>();
			
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				Socket miSocket = servidor.accept();
				
				ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream());
				paqueteRecibido = (PaqueteEnvio)paqueteDatos.readObject();
				
				nick = paqueteRecibido.getNick();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getMensaje();
				estado = paqueteRecibido.getEstado();
				
				InetAddress localizacion = miSocket.getInetAddress();
				String ipRemota = localizacion.getHostAddress();
				
				if(estado.equals(PaqueteEnvio.ONLINE)) { //Cambiar esto por un estado
					
					//areatexto.append("\n" + nick + ": " + mensaje);
					/*
					DataInputStream flujoEntrada = new DataInputStream(miSocket.getInputStream());
					String mensajeTexto = flujoEntrada.readUTF();
					areatexto.append("\n" + mensajeTexto);
					miSocket.close();
					*/
					
					//Debe enviar el mensaje a todos los demas clientes
					
					for(String ipActual : NickIP.values()) {
						
						if(!ipRemota.equals(ipActual)) {
							Socket enviaDestinatario = new Socket(ipActual, ClientChat.PUERTO_CLIENTE2);
							
							ObjectOutputStream paqueteReEnvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
							paqueteReEnvio.writeObject(paqueteRecibido);
							
							paqueteReEnvio.close();
							
							enviaDestinatario.close();
							
							miSocket.close();
						}
					}
					
					
				}else { //Entra si apenas se va a conectar
					//-------------DETECTA ONLINE-----------------------
					
					//Debo almacenar esos datos en el hashmap
					NickIP.put(nick, ipRemota);
					System.out.println(ipRemota);
					DatagramSocket Socket = new DatagramSocket();
					File apl =new File(
				            "./music/Megadeth - Dystopia.mp3");
					
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(outputStream);
					//music ms=new music(apl);
					os.writeObject(apl);
					byte[] data2 = outputStream.toByteArray();
					 			
							
							InetAddress ia=InetAddress.getByName(ipRemota);
						
					
					DatagramPacket sendPacket = new DatagramPacket(data2, data2.length, ia, ServerModel.PORT_SEND_MUSIC);
					Socket.send(sendPacket);
					/*
					System.out.println("Online" + ipRemota);
					listaIP.add(ipRemota);
					
					paqueteRecibido.setIps(listaIP);
					
					for(String z : listaIP){
						
						Socket enviaDestinatario = new Socket(z, Client.PUERTO_CLIENTE2);
						
						ObjectOutputStream paqueteReEnvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
						paqueteReEnvio.writeObject(paqueteRecibido);
						
						paqueteReEnvio.close();
						
						enviaDestinatario.close();
						
						miSocket.close();
					}
					*/
					//-------------------------------------------------
				}
				
				
			}
			
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
