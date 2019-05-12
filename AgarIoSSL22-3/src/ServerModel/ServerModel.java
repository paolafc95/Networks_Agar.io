package ServerModel;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import Controller.Collision;
import Controller.GestorPlayer;
import Controller.GestorVirus;
import Controller.Infecting;
import Model.Cell;
import Model.Player;
import View.Session;
import chat.ServerChat;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.SocketSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.awt.FlowLayout;
import java.awt.List;

import javax.swing.JLabel;
import javax.swing.JButton;

public class ServerModel extends JFrame {

	
	public static final int PORT_RECEIVE = 9000;
	public static final int PORT_RECEIVE_LOGIN = 9004;
	public static final int PORT_SEND = 9001;
	public static final int PORT_SEND_LOGIN = 9005;
	public static final int PORT_SEND_MUSIC=9876;
	/**
	 * El servidor dispone de un socket para atender a cada cliente por individual
	 */
	private ServerSocket conectionSocket;
	
	public static final int WINDOW_WIDTH = 1000;
	public static final int WINDOW_HEIGHT = 700;
	public static final int PORT_SENDTIME = 9002;
	public static final int MINPLAYERS=1;
	public static final int MAXPLAYERS=5;
	public static final int TIMEGAME=5;
	public static final int TIMEWAIT=2;
	private static Vector users=new Vector<>();; 
	
	private boolean gameStart,timeLimit,startTime;
	
	private ServerChat servidorChat;
	
	Socket connectionSocket;
	
	ServerSocket connectionSocketLogin;
	
	private JPanel contentPane;

	private boolean inGame;

	private ThreadUsers threadUsers;
	
	private ThreadUsersLogin threadLogin;
	
	private ThreadTimeToStart timeToStart;
	
	private ServerSendMusic sendMusic;
	
	private ArrayList<String> playersOnline;
	
	private String timeLoad;
	
	private ArrayList<String> posPlayers;
	
	private HashMap<String, String> nickIP;
	
	private ArrayList<Player> jugadores;
	
	private GestorPlayer gp;
	
	private GestorVirus gc;
	private Collision collision;
	private Infecting infecting;
	private boolean inThread;
	public ArrayList<String> getPosPlayers() {
		return posPlayers;
	}

	public void setPosPlayers(ArrayList<String> posPlayers) {
		this.posPlayers = posPlayers;
	}

	/**
	 * 
	 * Create the frame.
	 */
	public ServerModel(ServerSocket mysocket,ServerSocket mysocket2) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		posPlayers=new ArrayList<>();
		timeLoad="--:--";
		playersOnline=new ArrayList<>();
		setGameStart(false);
		setTimeLimit(false);
		setStartTime(false);
		setNickIP(new HashMap<>());

		jugadores = new ArrayList<>();
		conectionSocket=mysocket;
		connectionSocketLogin=mysocket2;
		gc=new GestorVirus();
		gp=new GestorPlayer(gc);
	}
	public ArrayList<String> posEnemy(ArrayList<String> pos, int t){
		ArrayList<String> post= new ArrayList<>(); 
		int cont=0;
		for (int i = 0; i < pos.size(); i++) {
			if(i!=t) {
				post.add(cont, pos.get(i));
				cont++;
			}
		}
		return post;
	}
	public void StartThread(ServerModel server) {
		threadLogin=new ThreadUsersLogin(server);
		threadLogin.start();
		
		threadUsers = new ThreadUsers(server);
		threadUsers.start();
		
	}
	public void startTime() {
		setStartTime(true);
		timeToStart=new ThreadTimeToStart(this);
		timeToStart.setTipeTime("wait");
		//timeToStart.setNuMin(TIMEWAIT );
		timeToStart.setNuSeg(6);
		timeToStart.start();
	}
	
	public void StartGame() {
		setStartTime(true);
		setInGame(true);
		timeToStart = new ThreadTimeToStart(this);
		timeToStart.setNuMin(TIMEGAME);
		timeToStart.setTipeTime("game");;
		timeToStart.start();
		setGameStart(true);
		collision = new Collision(this);
		infecting = new Infecting(this);
		collision.start();
		infecting.start();
		sendMusic=new ServerSendMusic(this);
		sendMusic.start();
	}

	public void finishGame() {
		setGameStart(false);
		
		
		for (String ip : getNickIP().values()) {
			
			try {
				Socket envioActualizacionMovimiento = new Socket(ip, PORT_SEND);
				ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
						envioActualizacionMovimiento.getOutputStream());
				paqueteReenvio.writeObject("finish");
				paqueteReenvio.writeObject(getGp());
				envioActualizacionMovimiento.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
				
			
		}
		
	}
	public  Vector getUsers() {
		return users;
	}

	public int positionPlayer(String name) {
		return getPlayersOnline().indexOf(name);
	}

	public  void setUsers(Vector users) {
		this.users = users;
	}

	public void gestorActualizado(GestorVirus Gc) {
		
		gc.setVirus(Gc.getVirus());
	}

	public ServerSocket getConectionSocket() {
		return conectionSocket;
	}


	public void setConectionSocket(ServerSocket conectionSocket) {
		this.conectionSocket = conectionSocket;
	}


	public boolean isGameStart() {
		return gameStart;
	}


	public void setGameStart(boolean gameStart) {
		this.gameStart = gameStart;
	}


	public boolean isTimeLimit() {
		return timeLimit;
	}


	public void setTimeLimit(boolean timeLimit) {
		this.timeLimit = timeLimit;
	}


	
	public boolean isStartTime() {
		return startTime;
	}


	public void setStartTime(boolean startTime) {
		this.startTime = startTime;
	}
	public ArrayList<String> getPlayersOnline() {
		return playersOnline;
	}

	public void setPlayersOnline(ArrayList<String> playersOnline) {
		this.playersOnline = playersOnline;
	}

	public String getTimeLoad() {
		return timeLoad;
	}

	public void setTimeLoad(String timeLoad) {
		this.timeLoad = timeLoad;
	}

	public HashMap<String, String> getNickIP() {
		return nickIP;
	}

	public void setNickIP(HashMap<String, String> nickIP) {
		this.nickIP = nickIP;
	}

	public GestorPlayer getGp() {
		return gp;
	}

	public void setGp(GestorPlayer gp) {
		this.gp = gp;
	}

	public GestorVirus getGc() {
		return gc;
	}

	public void setGc(GestorVirus gc) {
		this.gc = gc;
	}

	public int addPlayer(String nick,String ip) {
		
		nickIP.put(nick, ip);
		return gp.addNewPlayer(nick, WINDOW_HEIGHT, WINDOW_WIDTH);
		
	}

	public synchronized void sendFood() {
		
		for (String ip : getNickIP().values()) {
			
			try {
				Socket envioActualizacionMovimiento = new Socket(ip, PORT_SEND);
				ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
						envioActualizacionMovimiento.getOutputStream());
				paqueteReenvio.writeObject("newgc");
				paqueteReenvio.writeObject(getGc());
				envioActualizacionMovimiento.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
				
			
		}
	}
	public void registrarUsuario(Session usuarioNuevo)
    {
    	FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
        	fichero = new FileWriter("./data/archivo.txt", true);
            pw = new PrintWriter(fichero);
            
            pw.println(usuarioNuevo.getId() + ";" + usuarioNuevo.getPass() + ";" + usuarioNuevo.getEmail());
            
            fichero.close();
            
            //DESPUES DE ESTO, EL CLIENTE SE DEBE LOGEAR Y EMPEZAR EL JUEGO
        	
        }
        
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public ServerSocket getConnectionSocketLogin() {
		return connectionSocketLogin;
	}

	public void setConnectionSocketLogin(ServerSocket connectionSocketLogin) {
		this.connectionSocketLogin = connectionSocketLogin;
	}

	public boolean isInThread() {
		return inThread;
	}

	public void setInThread(boolean inThread) {
		this.inThread = inThread;
	}

	public ServerChat getServidorChat() {
		return servidorChat;
	}

	public void setServidorChat(ServerChat servidorChat) {
		this.servidorChat = servidorChat;
	}
}
