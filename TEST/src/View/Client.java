package View;

import Controller.Collision;
import Controller.GestorPlayer;
import Controller.GestorVirus;
import Controller.Infecting;
import Controller.Moving;
import ModelClient.ThreadCharge;
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
    private Moving movPlayer;
    
    //Variables conection
    private PanelNewUser newUser;
	
	private String name;
	
	private String pass;
	
	private BufferedWriter writer;
	
    private BufferedReader reader;
    
    private ThreadListenerServer listenerServer;
    
    private ThreadCharge threadCharge;
    
    private boolean chargering;
    
    private boolean gaming;
    
    private ThreadGameClient gameClient;
    
	public static final String LOCAL_HOST = "localhost";
	/**
	 * Puerto por donde se establecera la conexion
	 */
	public static final int PORT_SEND = 9000;

	private boolean isClientConected;
    //
    public Client(String ip, String port) throws NotBoundException, MalformedURLException, RemoteException{
        initComponents(ip, port);
        this.loginWindow = new Login(this, false);
        this.loginWindow.setVisible(true);
    }
    
    
    public void play(){
        //ESPERA A QUE INGRESE SU NICKNAME
        while(!this.loginWindow.getState()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            	ex.getStackTrace();
            }
        }
        
        this.createBufferStrategy(2);
        this.setLocationRelativeTo(null);
        this.setIgnoreRepaint(false);
        
        //ADD PLAYER
        this.nick = this.loginWindow.getNickname();
        try {
            this.id = this.players.addNewPlayer(nick, this.getWidth(), this.getHeight());
            
            this.ds.setID(this.id);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //LOCAL
        this.movPlayer = new Moving(id,players,this);
        this.movPlayer.start();
        
        /*
        //SERVER
        this.collision = new Collision(players,virus);
        this.collision.start();
        this.infecting = new Infecting(virus);
        this.infecting.start();
        */
        
        //PLAY
        while(true){
            this.ds.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
        
        //SERVER
        //this.virus = new GestorVirus();
        //this.players = new GestorPlayer(this.virus);
        this.virus = new GestorVirus();
        this.players = new GestorPlayer(virus);
        
        ///
        Collision collision = new Collision(players,virus);
        Infecting infecting = new Infecting(virus);
        //collision.start();
        infecting.start();
        this.ds = new DrawingSpace(this.players,this.virus, new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.ds.setFocusable(false);
        this.ds.setIgnoreRepaint(false);
        this.add((Component)this.ds);
        
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                formKeyPressed(e);
            }
        });
    }
    
    @Override
    public void paint(Graphics g){
        if(this.ds != null){
            this.ds.paint(g);
        }
    }
    
    int contador = 0;
    
    public void formKeyPressed(KeyEvent e){
        //SPLIT
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE){
            try {
                this.players.split(id);
            } catch (RemoteException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //funtions Conection
    
    public void Connect() {

		try {
			
			isClientConected = true;
			try {
				
				Socket socketClient = new Socket("sd", PORT_SEND);
				writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
				reader =new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
				
				listenerServer=new ThreadListenerServer(this);
				listenerServer.start();
				threadCharge=new ThreadCharge(this);
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
		
		
		try {
			writer.write(name+";"+pass+";"+tipe);
			writer.write("\r\n");
			writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void pCharge(String tipe) {
		
		if(tipe.equals("login")) {
			//contentPane.remove(login);
			//contentPane.add(panelCarga);
			
		}else {
			//this.contentPane.remove(newUser);
			//this.contentPane.add(panelCarga);
			
			
		}
		
		//contentPane.revalidate();
		
		setChargering(true);
		
		threadCharge.start();
		
		try {
			writer.write("load");
			writer.write("\r\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public void updatePlayers(String dataPlayers) {
		/*
		panelGame.updateList(dataPlayers);
		contentPane.repaint();
		contentPane.revalidate();
		*/
		
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

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
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
}
