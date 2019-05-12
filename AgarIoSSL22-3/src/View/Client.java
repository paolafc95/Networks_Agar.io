package View;

import Controller.Collision;
import Controller.GestorPlayer;
import Controller.GestorVirus;
import Controller.Infecting;
import Controller.Moving;
import Model.Player;
import ModelClient.ThreadGameClient;
import ModelClient.ThreadListenerServer;
import ModelClient.ThreadUpdateTime;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Client extends JFrame{
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final int WINDOW_POS_X = 100;
    public static final int WINDOW_POS_Y = 50;
    
    private GestorPlayer players;
    private GestorVirus virus;
    private String nick;
    private int id;
    private VentanaLoginP loginWindow;
    private DrawingSpace ds;
    public DrawingSpace getDs() {
		return ds;
	}
    private boolean mover;

	public void setDs(DrawingSpace ds) {
		this.ds = ds;
	}

	private SSLSocket sslsocket;
	private Moving movPlayer;
    
    //Variables conection
	private String name;
	
	private String pass;
	
	private ObjectOutputStream writer;
	
    private ObjectInputStream reader;
    
    private ThreadListenerServer listenerServer;
    
    private boolean chargering;
    
    private boolean gaming;
    
    private ThreadGameClient gameClient;
    
    private ThreadUpdateTime updateTime;
    
	public static final String LOCAL_HOST="192.168.43.204";
	/**
	 * Puerto por donde se establecera la conexion
	 */
	public static final int PORT_SEND = 9000;
	public static final int PORT_SEND_LOGIN = 9004;
	public static final int PORT_RECIVE = 9001;
	public static final int PORT_RECIVETIME = 9002;
	public static final int PORT_RECIVE_LOGIN = 9005;
	public static final int PORT_RECIVE_MUSIC=9876;
	
	private boolean isClientConected;
    //
    public Client(String ip, String port) throws NotBoundException, MalformedURLException, RemoteException{
        initComponents(ip, port);
        this.loginWindow = new VentanaLoginP(this);
        this.loginWindow.setVisible(true);
    }
    
    private void initComponents(String ip, String port) throws NotBoundException, MalformedURLException, RemoteException{
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(false);
        this.setBounds(WINDOW_POS_X, WINDOW_POS_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.setFocusable(true);
        this.setLocationRelativeTo(null);
       
        Connect();
        //SERVER
        //this.virus = new GestorVirus();
        //this.players = new GestorPlayer(this.virus);
        
        
        /*
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                formKeyPressed(e);
            }
        });
        */
    }
    
    @Override
    public void paint(Graphics g){
        if(this.ds != null){
            this.ds.paint(g);
        }
    }
    
    int contador = 0;
	private Socket socketClientr;
	private ObjectInputStream entrada;
    
    public void formKeyPressed(KeyEvent e){
        //SPLIT
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE){
            
                this.players.split(id);
          
        }
    }
    //funtions Conection
    
    public void Connect() {

		try {
			
			isClientConected = true;
			try {
				
			
				//reader =new ObjectInputStream(socketClientr.getInputStream());
				
				listenerServer=new ThreadListenerServer(this);
				
				listenerServer.start();
				
				this.virus = new GestorVirus();
		        this.players = new GestorPlayer(virus);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {

			System.out.println(e.getMessage());
		}

	}

	public void userPass(Session sessionLogin) {
		
		String loginUser="session";
		try {
			System.setProperty("javax.net.ssl.trustStore", "pfc.store"); // pfc.store cambiar por esto
			ObjectOutputStream salida = null;
			sslsocket = null;
			

			
			Socket envioActualizacionMovimiento = new Socket(LOCAL_HOST, PORT_SEND_LOGIN);
			
			ObjectOutputStream paqueteReenvio = new ObjectOutputStream(envioActualizacionMovimiento.getOutputStream());
			paqueteReenvio.writeObject(loginUser);
			
			SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sslsocket = (SSLSocket) fact.createSocket(LOCAL_HOST,8000); // ("localhost",SERVER_PORT)
			
			// hacer handshake
			sslsocket.startHandshake();
			salida = new ObjectOutputStream(sslsocket.getOutputStream());
			setEntrada(new ObjectInputStream(sslsocket.getInputStream()));
			salida.writeObject(sessionLogin);
			//paqueteReenvio.writeObject(sessionLogin);
			envioActualizacionMovimiento.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void pNewUser() {
		JOptionPane.showMessageDialog(this,
			    "Crear usuario");
		
		//this.contentPane.remove(login);
		//this.contentPane.add(newUser);
		this.update(getGraphics());
	}
	public void NameExist() {
		JOptionPane.showMessageDialog(this,"Usuario en uso cambielo pto copion",
				"Advertencia",JOptionPane.WARNING_MESSAGE);
	}
	public void loginFail() {
		JOptionPane.showMessageDialog(this,"Contraseña o usuario incorrecto",
				"Advertencia",JOptionPane.WARNING_MESSAGE);
	}
	public void PlayersTop() {
		setMover(false);
		ArrayList<Player> tp=players.getTop(3);
		ArrayList<String> ga=new ArrayList<>();
		System.out.println("finish2"+ tp.size());
		for (int i = 0; i < tp.size(); i++) {
			
				ga.add(tp.get(i).getNickname()+" Puntos: "+tp.get(i).getMass());
			
				
			
		}
		ga.add("---");
		ga.add("---");
		System.out.println("finish2"+ tp.size());
		String send=String.join(";", ga);
		
		String[]playersTop=send.split(";");
		JOptionPane.showMessageDialog(this,"Top de los 3 mejores jugadores\nPrimer lugar: "+playersTop[0]+"\nSegundo lugar: "+playersTop[1]+"\nTercer lugar: "+playersTop[2],
				"Juego Finalizado",JOptionPane.WARNING_MESSAGE);
	}
	public void pCharge(String tipe, int id) {
		
		if(tipe.equals("login")) {
			this.ds = new DrawingSpace(this.players,this.virus, new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            this.ds.setFocusable(false);
            this.ds.setIgnoreRepaint(false);
            this.add((Component)this.ds);
			setVisible(true);
			loginWindow.setVisible(false);
			this.createBufferStrategy(2);
			this.setLocationRelativeTo(null);
			this.setIgnoreRepaint(false);
			this.nick = this.name;
			
	           // this.id = this.players.addNewPlayer(nick, this.getWidth(), this.getHeight());
	            this.id=id;
	            this.ds.setID(this.id);
	           
	        //LOCAL
	        
	        this.movPlayer = new Moving(id,players,this);
	        this.movPlayer.start();
	        
	        this.setMover(true);
			//contentPane.remove(login);
			//contentPane.add(panelCarga);
			
		}else {
			//this.contentPane.remove(newUser);
			//this.contentPane.add(panelCarga);
			
			
		}
		/*
		try {
			Socket envioActualizacionMovimiento = new Socket(LOCAL_HOST,
					PORT_SEND);
			ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
					envioActualizacionMovimiento.getOutputStream());
			paqueteReenvio.writeObject("gameStart");
			envioActualizacionMovimiento.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		gameClient= new ThreadGameClient(this);
		gameClient.start();
		updateTime=new ThreadUpdateTime(this);
		updateTime.start();
		setChargering(true);
	}
	public void iniciarEnvio() {
		setGaming(true);
	}
	public void terminarEnvio() {
		setGaming(false);
	}

	
	public void updatePlayers(GestorPlayer nGp) {
	
		players=nGp;
	}
	public void updateComida(GestorVirus nGp) {
		
		
		virus=nGp;
		
	}
	public void updateDataGame(GestorVirus nGp) {
		
		virus=nGp;
		ds.setVirus(virus);
		players.setGv(virus);
	}
	public void updateDataGp(GestorPlayer nGp) {
		players=nGp;
		ds.setPlayers(players);
		players.setGv(virus);
	}
	public void sendPlayer(int id, boolean SendGv) {
		try {
			
			Player p=players.getPlayerID(id);
			Socket envioActualizacionMovimiento = new Socket(LOCAL_HOST,
					PORT_SEND);
			ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
					envioActualizacionMovimiento.getOutputStream());
			paqueteReenvio.writeObject("game");
			paqueteReenvio.writeObject(p);
			
			
			if(SendGv) {
				
				paqueteReenvio.writeObject("gc");
				paqueteReenvio.writeObject(players.getGv());
			}else {
				paqueteReenvio.writeObject("no gc");
			}
			envioActualizacionMovimiento.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void updateList(String data) {
		//panelCarga.updateTime(data);
		
	}
	public void updateTime(String dataw) {
		
		//panelCarga.updateTime(dataw);
	}
	public boolean isClientConected() {
		return isClientConected;
	}

	public void setClientConected(boolean isClientConected) {
		this.isClientConected = isClientConected;
	}
	public String getName() {
		return name;
	}
	public String getPass() {
		return pass;
	}
	public void setName(String n) {
		name=n;
	}
	public void setPass(String n) {
		pass=n;
	}

	public ObjectOutputStream getWriter() {
		return writer;
	}

	public void setWriter(ObjectOutputStream writer) {
		this.writer = writer;
	}

	public ObjectInputStream getReader() {
		return reader;
	}

	public void setReader(ObjectInputStream reader) {
		this.reader = reader;
	}

	
	public boolean isChargering() {
		return chargering;
	}
	
	public void setChargering(boolean chargering) {
		this.chargering = chargering;
	}
	
	public boolean isGaming() {
		return gaming;
	}
	
	public void setGaming(boolean gaming) {
		this.gaming = gaming;
	}


	public Socket getSocketClientr() {
		return socketClientr;
	}


	public void setSocketClientr(Socket socketClientr) {
		this.socketClientr = socketClientr;
	}


	public GestorPlayer getPlayers() {
		return players;
	}


	public void setPlayers(GestorPlayer players) {
		this.players = players;
	}

	public boolean isMover() {
		return mover;
	}

	public SSLSocket getSslsocket() {
		return sslsocket;
	}

	public void setSslsocket(SSLSocket sslsocket) {
		this.sslsocket = sslsocket;
	}

	public void setMover(boolean mover) {
		this.mover = mover;
	}

	public ObjectInputStream getEntrada() {
		return entrada;
	}

	public void setEntrada(ObjectInputStream entrada) {
		this.entrada = entrada;
	}
}
