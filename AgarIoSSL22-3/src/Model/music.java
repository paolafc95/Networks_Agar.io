package Model;

import java.io.FileInputStream;
import java.io.Serializable;
import javazoom.jl.player.Player;
public class music implements Serializable {

	private Player fl;
	
	public music(Player f) {
		// TODO Auto-generated constructor stub
		setFl(f);
	}

	public Player getFl() {
		return fl;
	}

	public void setFl(Player fl) {
		this.fl = fl;
	}

}
