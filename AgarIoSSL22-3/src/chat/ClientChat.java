package chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.View;
import View.Client;

public class ClientChat {
	
	//VARIABLES GLOBALES PARA CHAT
	public static final int PUERTO_CLIENTE = 9999; //Puerto por el que el cliente se conecta con el servidor para que se almacene su informacion
	public static final int PUERTO_CLIENTE2 = 9090; //Puerto por el que el servidor usa para conectarse con cada usuario y enviar texto

	public ClientChat() {
		MarcoCliente mimarco=new MarcoCliente();
	}
	
}

class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
		
		setBounds(600,300,280,350);
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		add(milamina);
		setVisible(true);
		//addWindowListener(new EnvioOnline());
	}	
	
}
//Para que al abrir la ventana envie la direccion IP al servidor
//YA NO SE VA A USAR. BORRAR
/*
class EnvioOnline extends WindowAdapter{
	
	@Override
	public void windowOpened(WindowEvent e) {
		
		try {
			
			Socket misocket = new Socket("localhost", Client.PUERTO_CLIENTE); //Aqui debe ir la direccion del servidor
			PaqueteEnvio datos = new PaqueteEnvio();
			datos.setMensaje(" online");
			
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
			paqueteDatos.writeObject(datos);
			misocket.close();
			
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
}
*/
class LaminaMarcoCliente extends JPanel implements Runnable{
	
	private JTextField campo1;
	private JButton miboton;
	private JTextArea campochat;
	private JLabel nick;
	private JComboBox ip;
	
	public LaminaMarcoCliente(){
	
		String nickUsuario = JOptionPane.showInputDialog("Nick: ");
		
		JLabel nNick = new JLabel("Nick: ");
		add(nNick);
		
		nick = new JLabel();
		nick.setText(nickUsuario);
		add(nick);
		
		JLabel texto=new JLabel("Online: ");
		
		add(texto);
		ip = new JComboBox();
		/*
		ip.addItem("Usuario1");
		ip.addItem("Usuario2");
		ip.addItem("Usuario3");
		*/
		add(ip);
		
		campochat = new JTextArea(12,20);
		
		add(campochat);
	
		campo1 = new JTextField(20);
	
		add(campo1);		
	
		//-------------BOTON-----------------
		miboton=new JButton("Enviar");
		EnviaTexto mievento =new EnviaTexto();
		miboton.addActionListener(mievento);
		add(miboton);
		//-----------------------------------
		Thread mihilo = new Thread(this);
		
		peticionUsuario(nickUsuario);
		
		mihilo.start();
		
	}
	
	public void peticionUsuario(String nick) {
		try {
			
			Socket misocket = new Socket(Client.LOCAL_HOST, ClientChat.PUERTO_CLIENTE); //Aqui debe ir la direccion del servidor
			PaqueteEnvio datos = new PaqueteEnvio();
			datos.setMensaje(" online");
			datos.setEstado(PaqueteEnvio.OFFLINE);
			datos.setNick(nick);
			
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
			paqueteDatos.writeObject(datos);
			misocket.close();
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//System.out.println(campo1.getText());
			
			
			try {
				campochat.append("\n" + campo1.getText());
				
				Socket misocket = new Socket(Client.LOCAL_HOST, ClientChat.PUERTO_CLIENTE); //AQUI DEBE IR LA IP DEL SERVIDOR
				/*
				DataOutputStream flujoSalida = new DataOutputStream(misocket.getOutputStream());
				flujoSalida.writeUTF(campo1.getText());
				flujoSalida.close();
				*/
				PaqueteEnvio datos = new PaqueteEnvio();
				datos.setNick(nick.getText());
				//datos.setIp(ip.getSelectedItem().toString());
				datos.setMensaje(campo1.getText());
				datos.setEstado(PaqueteEnvio.ONLINE);
				
				ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
				paqueteDatos.writeObject(datos);
				misocket.close();
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());;
			}
			
			
			
		}
		
	}

	@Override
	public void run() {
		
		try {
			
			ServerSocket servidorCliente = new ServerSocket(ClientChat.PUERTO_CLIENTE2);
			
			Socket cliente;
			
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				cliente = servidorCliente.accept();
				
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
				
				paqueteRecibido = (PaqueteEnvio)flujoEntrada.readObject();
				
				campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
				/*
				if(!paqueteRecibido.getMensaje().equals(" online")) {
					campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
				}else {
					//campochat.append("\n" + paqueteRecibido.getIps());
					//Vamos a agregar las IPs al JComboBox
					ArrayList<String> ipsMenu = new ArrayList<String>();
					ipsMenu = paqueteRecibido.getIps();
					
					ip.removeAllItems();
					
					for(String z: ipsMenu) {
						ip.addItem(z);
					}
					
				}
				*/
				
			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

class PaqueteEnvio implements Serializable {
	
	public static final String OFFLINE = "offline";
	public static final String ONLINE = "online";
	
	private String nick;
	private String ip;
	private String mensaje;
	private String estado;
	
	//LISTAS IP NO SE USUARA EN LA PRACTICA REAL
	private ArrayList<String> Ips;
	
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public ArrayList<String> getIps() {
		return Ips;
	}
	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
}

