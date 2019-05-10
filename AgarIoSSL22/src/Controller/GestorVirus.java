package Controller;

import Model.Cell;
import Model.Player;
import View.Client;
import java.util.ArrayList;
import java.awt.Graphics;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class GestorVirus implements Serializable{
    private final int MAX_VIRUS = 100;
    private ArrayList<Cell> virus;
    private int counter;
    
    public GestorVirus() {
        this.virus = new ArrayList<>();
        this.counter = 0;
    }
    
    public boolean createVirus(){
        if (this.virus.size() < MAX_VIRUS){
            Cell newVirus = new Cell(Client.WINDOW_WIDTH, Client.WINDOW_HEIGHT,true);
            newVirus.setID(counter);
            this.counter += 1;
            this.virus.add(newVirus);
            return true;
        }
        return false;
    }
    
    public int size() throws RemoteException{
        return this.virus.size();
    }
    
    public void deleteVirus(int id){
        Cell v = findVirus(id);
        if(v != null){
            this.virus.remove(v);
        }
    }
    
    private Cell findVirus(int id){
        for(int i = 0; i < virus.size(); i++){
            Cell v = this.virus.get(i);
            if (v.getID() == id)
                return v;
        }
        return null;
    }
    
    public void render(Graphics g, double scale){
        for(int i = 0; i < virus.size(); i++){
            Cell v = virus.get(i);
            v.render(g, scale);
        }
    }

    public boolean checkCollisions(Player p){
    	
        for(int i = 0; i < virus.size(); i++){
            Cell v = virus.get(i);
            boolean collision = p.checkCollision(v);
            if (collision){
                virus.remove(i);
               return true;
            }
        }
        return false;
    }
    
    public Cell getVirus(int index) {
        return this.virus.get(index);
    }

	public ArrayList<Cell> getVirus() {
		return virus;
	}

	public void setVirus(ArrayList<Cell> virus) {
		this.virus = virus;
	}
}
