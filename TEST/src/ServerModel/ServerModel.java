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

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
	public static final int PORT_SEND = 9001;
	/**
	 * El servidor dispone de un socket para atender a cada cliente por individual
	 */
	private ServerSocket conectionSocket;
	
	public static final int WINDOW_WIDTH = 1000;
	public static final int WINDOW_HEIGHT = 700;
	private User user;
	
	private static Vector users=new Vector<>();; 
	
	private boolean gameStart,timeLimit,startTime;
	
	Socket connectionSocket;
	
	private JPanel contentPane;


	private ThreadUsers threadUsers;
	
	private ThreadTimeToStart timeToStart;
	
	private ArrayList<String> playersOnline;
	
	private String timeLoad;
	
	private ArrayList<String> posPlayers;
	
	private HashMap<String, String> nickIP;
	
	private ArrayList<Player> jugadores;
	
	private GestorPlayer gp;
	
	private GestorVirus gc;
	private Collision collision;
	private Infecting infecting;
	
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
	public ServerModel(ServerSocket mysocket) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		user=new User();
		posPlayers=new ArrayList<>();
		timeLoad="--:--";
		playersOnline=new ArrayList<>();
		setGameStart(false);
		setTimeLimit(false);
		setStartTime(false);
		setNickIP(new HashMap<>());

		jugadores = new ArrayList<>();
		conectionSocket=mysocket;
		user.addUser("a", "123");
		user.addUser("b", "234");
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
		threadUsers = new ThreadUsers(server);
		threadUsers.start();
	}
	public void startTime() {
		timeToStart=new ThreadTimeToStart(this);
		//timeToStart.setNuMin(1);
		timeToStart.setNuSeg(5);
		timeToStart.start();
	}
	public boolean userExist(String data) {
		String nickTemp=data.split(";")[0];
		if (user.getNickName().contains(nickTemp)) {
			System.out.println(nickTemp);
			return true;
		}
		return false;
	}
	public boolean passwordCorrect(String data) {
		String nickTemp=data.split(";")[0];
		String passTemp=data.split(";")[1];
		int pos=user.getNickName().indexOf(nickTemp);
		return (passTemp.equals(user.getPassword().get(pos)));
	}

	public boolean createNewUser(String data) {
		String nickTemp=data.split(";")[0];
		String passTemp=data.split(";")[1];
		return user.addUser(nickTemp, passTemp);
		
	}
	public void StartGame() {
		System.out.println("colisiones ");
		 	 
			collision = new Collision(gp,gc);
	        infecting = new Infecting(this);
	        collision.start();
	        infecting.start();
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
		System.out.println(gc.getVirus().size()+" ngc "+Gc.getVirus().size());
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

	public int addPlayer(String data,String ip) {
		String nickTemp=data.split(";")[0];
		nickIP.put(nickTemp, ip);
		return gp.addNewPlayer(nickTemp, WINDOW_HEIGHT, WINDOW_WIDTH);
		
	}

	public void sendFood() {
		
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
}
