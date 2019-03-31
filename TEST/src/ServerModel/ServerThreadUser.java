package ServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import Controller.GestorVirus;
import Model.Player;


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
		ObjectInputStream flujo_entrada2;
		
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
					server.getGp().getPlayers().add(actualizacionPersonaje.getID(),actualizacionPersonaje);
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
				
				server.StartGame();
			}else {
				System.out.println("data user"+data);
				if(server.userExist(data)) {
					if(server.passwordCorrect(data)) {
						int id=server.addPlayer(data,ipClienteString);
						Socket envioActualizacionMovimiento = new Socket(ipClienteString,
								server.PORT_SEND);
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
								envioActualizacionMovimiento.getOutputStream());
						paqueteReenvio.writeObject("accept;login;"+id);
						paqueteReenvio.writeObject(server.getGp());
						paqueteReenvio.writeObject(server.getGc());
						envioActualizacionMovimiento.close();
					}
				}
				System.out.println("data user send");
			}
			
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
