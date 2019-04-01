package Controller;

import Model.Player;
import java.awt.Graphics;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GestorPlayer implements Serializable{
    //ONLY TO CHECK COLLISIONS
    private GestorVirus gv;
    private int counter;
    private ArrayList<Player> players;
    private static final Random ran = new Random();
    
    public GestorPlayer(GestorVirus gv){
        this.gv = gv;
        this.players = new ArrayList<>();
        this.counter = 0;
    }
    
    public Player getPlayerID(int id){
        for(Player p: players){
            if (p.getID() == id)
                return p;
        }
        return null;
    }

    public int addNewPlayer(String nickname,int xMax, int yMax){
        this.players.add(new Player(this.counter,nickname, xMax, yMax));
        this.counter += 1;
        return this.counter - 1;
    }
    
    public int size() throws RemoteException{
        return this.players.size();
    }
    
    
    public void checkCollisions() throws RemoteException{
        for(int i = 0; i < this.players.size();i++){
            Player p1 = this.players.get(i);
            for(int j = i+1; j < this.players.size(); j++){
                Player p2 = this.players.get(j);
                p1.checkCollision(p2);
                if(p1.getMustDie()){
                    p1.reset();
                }
                if(p2.getMustDie()){
                    p2.reset();
                }
            }
        }
    }
    
    public void render(Graphics g, double scale){
        for(int i = 0; i < players.size(); i++){
            Player p = players.get(i);
            p.render(g, scale);
        }
    }
    
    public boolean mover(int id,double x, double y){
        Player p = this.getPlayerID(id);
        if(p!=null){
            p.mover(x, y);
            return true;
        }else{
            return false;
        }
    }
    
    public boolean checkCollisionVirus(int id) { 
        Player p = this.getPlayerID(id);
        boolean r=false;
        if(p!=null){
            r=gv.checkCollisions(p);
        }
        return r;
    }

    public void split(int id){
        Player p = this.getPlayerID(id);
        if(p != null){
            p.split();
        }
    }
    
    public ArrayList getTop(int n) {
        ArrayList<Player> playersTop = new ArrayList<Player>(players);
        Collections.sort(playersTop, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p2.getMass() - p1.getMass();
            }            
        });
        
        if (playersTop.size() > n) {
            playersTop.subList(n, playersTop.size()).clear();
            return playersTop;
        } else {
            return playersTop;
        }
    }
    
    public Player getPlayerIterator(int index) {
        return this.players.get(index);
    }
    
    public void incrementTimeDuration(int id,int time){
        Player p = this.getPlayerID(id);
        if(p != null){
            p.incrementTimeCreation(time);
        }
    }
    
    public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public void fusion(int id){
        Player p = this.getPlayerID(id);
        if(p != null){
            p.fusion();
        }
    }

	public GestorVirus getGv() {
		return gv;
	}

	public void setGv(GestorVirus gv) {
		this.gv = gv;
	}
}