package View;

import Controller.Collision;
import Controller.GestorPlayer;
import Controller.GestorVirus;
import Controller.Infecting;
import Controller.Moving;
import Model.Player;
import ModelClient.ThreadGameClient;
import ModelClient.ThreadListenerServer;
import ModelClient.Login;
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
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Login loginWindow;
    private DrawingSpace ds;
    public DrawingSpace getDs() {
		return ds;
	}


	public void setDs(DrawingSpace ds) {
		this.ds = ds;
	}

	private Moving movPlayer;
    
    //Variables conection
    private PanelNewUser newUser;
	
	private String name;
	
	private String pass;
	
	private ObjectOutputStream writer;
	
    private ObjectInputStream reader;
    
    private ThreadListenerServer listenerServer;
    
    private boolean chargering;
    
    private boolean gaming;
    
    private ThreadGameClient gameClient;
    
	public static final String LOCAL_HOST = "localhost";
	/**
	 * Puerto por donde se establecera la conexion
	 */
	public static final int PORT_SEND = 9000;

	public static final int PORT_RECIVE = 9001;
	
	private boolean isClientConected;
    //
    public Client(String ip, String port) throws NotBoundException, MalformedURLException, RemoteException{
        initComponents(ip, port);
        this.loginWindow = new Login(this, false,this);
        this.loginWindow.setVisible(true);
    }
    
    
    public void play(){
        while(true){
        	System.out.println(isChargering());
        	while(isChargering()) {
        		this.ds.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                   
                }
        	}
        	try {
                Thread.sleep(100000);
            } catch (InterruptedException ex) {
               
            }
        }
    }
    
    private void initComponents(String ip, String port) throws NotBoundException, MalformedURLException, RemoteException{
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(false);
        this.setBounds(WINDOW_POS_X, WINDOW_POS_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.setFocusable(true);
        this.setLocationRelativeTo(null);
        System.out.println("inicio a conectar");
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

	public void userPass(String nam,String pas,String tipe) {
		name=nam;
		pass=pas;
		String loginUser=name+";"+pass;
		try {
			Socket envioActualizacionMovimiento = new Socket(LOCAL_HOST,
					PORT_SEND);
			ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
					envioActualizacionMovimiento.getOutputStream());
			paqueteReenvio.writeObject(loginUser);
			envioActualizacionMovimiento.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("user pass ready");
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
	public void pCharge(String tipe, int id) {
		
		if(tipe.equals("login")) {
			this.ds = new DrawingSpace(this.players,this.virus, new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            this.ds.setFocusable(false);
            this.ds.setIgnoreRepaint(false);
            this.add((Component)this.ds);
			setVisible(true);
			loginWindow.setVisible(false);
			loginWindow.setState(true);
			this.createBufferStrategy(2);
			this.setLocationRelativeTo(null);
			this.setIgnoreRepaint(false);
			this.nick = this.name;
			
	           // this.id = this.players.addNewPlayer(nick, this.getWidth(), this.getHeight());
	            this.id=id;
	            this.ds.setID(this.id);
	           
	        //LOCAL
	        System.out.println("hilo mover");
	        this.movPlayer = new Moving(id,players,this);
	        this.movPlayer.start();
	        System.out.println("hilo mover2");
	        
			//contentPane.remove(login);
			//contentPane.add(panelCarga);
			
		}else {
			//this.contentPane.remove(newUser);
			//this.contentPane.add(panelCarga);
			
			
		}
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
		gameClient= new ThreadGameClient(this);
		gameClient.start();
		setChargering(true);
		setGaming(true);
	}
	public void pGame(String data) {
		//contentPane.remove(panelCarga);
		//contentPane.add(panelGame);
		String posPlayer=data.split(";")[1];
		int x=Integer.parseInt(posPlayer.split(":")[0]);
		int y=Integer.parseInt(posPlayer.split(":")[1]);
		int r=Integer.parseInt(posPlayer.split(":")[2]);
		//panelGame.setXs(x);
		//panelGame.setYs(y);
		//panelGame.setG(r);
		String posPlayers=data.split(";")[2];
		//panelGame.updateList(posPlayers);
		//contentPane.repaint();
		//contentPane.revalidate();
		setGaming(true);
		gameClient= new ThreadGameClient(this);
		
	}
	public void updatePlayers(GestorPlayer nGp) {
		/*
		panelGame.updateList(dataPlayers);
		contentPane.repaint();
		contentPane.revalidate();
		*/
		players=nGp;
	}
	public void updateComida(GestorVirus nGp) {
		/*
		panelGame.updateList(dataPlayers);
		contentPane.repaint();
		contentPane.revalidate();
		*/
		System.out.println("food1");
		virus=nGp;
		System.out.println("food2");
		
		System.out.println("food1");
	}
	public void updateDataGame(GestorVirus nGp) {
		System.out.println("cfood");
		virus=nGp;
		ds.setVirus(virus);
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
}
