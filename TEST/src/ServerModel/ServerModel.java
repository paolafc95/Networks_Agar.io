package ServerModel;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.FlowLayout;
import java.awt.List;

import javax.swing.JLabel;
import javax.swing.JButton;

public class ServerModel extends JFrame {

	
	public static final int PORT_RECEIVE = 9000;	
	/**
	 * El servidor dispone de un socket para atender a cada cliente por individual
	 */
	private Socket conectionSocket;
	
	private User user;
	
	private static Vector users=new Vector<>();; 
	
	private boolean gameStart,timeLimit,startTime;
	
	Socket connectionSocket;
	
	private JPanel contentPane;

	private ThreadGame threadGame;
	
	private ThreadUsers threadUsers;
	
	private ThreadTimeToStart timeToStart;
	
	private ArrayList<String> playersOnline;
	
	private String timeLoad;
	
	private ArrayList<String> posPlayers;
	
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
	public ServerModel(Socket sock) {
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
		
		conectionSocket=sock;
		user.addUser("a", "123");
		user.addUser("b", "234");
	}

	public void startgame() {
		setGameStart(true);
		threadUsers.stop();
		System.out.println("game");
		threadGame=new ThreadGame(this);
		threadGame.start();
		String p1= "10:10:20";
		String p2= "200:200:20";
		posPlayers.add(p1);
		posPlayers.add(p2);
		for (int i = 0; i < getUsers().size(); i++) {
			try {
				BufferedWriter bw = (BufferedWriter) getUsers().get(i);
				String enemys=String.join(",", posEnemy(posPlayers, i));
				bw.write("StartGame;"+posPlayers.get(i)+";"+enemys);
				bw.write("\r\n");
				bw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	public  Vector getUsers() {
		return users;
	}

	public int positionPlayer(String name) {
		return getPlayersOnline().indexOf(name);
	}

	public  void setUsers(Vector users) {
		this.users = users;
	}


	public Socket getConectionSocket() {
		return conectionSocket;
	}


	public void setConectionSocket(Socket conectionSocket) {
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
}
