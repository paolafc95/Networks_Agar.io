package ServerModel;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{

	private ArrayList<String> nickName;
	private ArrayList<String> password;
	public User() {
		setNickName(new ArrayList<>());
		setPassword(new ArrayList<>());
	}
	public boolean addUser(String nick,String pass) {
		if(nickName.contains(nick)) {
			return false;
		}
		nickName.add(nick);
		password.add(pass);
		return true;
	}
	public ArrayList<String> getNickName() {
		return nickName;
	}
	public void setNickName(ArrayList<String> nickName) {
		this.nickName = nickName;
	}
	public ArrayList<String> getPassword() {
		return password;
	}
	public void setPassword(ArrayList<String> password) {
		this.password = password;
	}
	
	
}
