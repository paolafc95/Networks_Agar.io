package ServerModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Model.music;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class ServerSendMusic extends Thread{

	private ServerModel server;
	public ServerSendMusic(ServerModel ser) {
		server=ser;
	}
	public void run() {
		try {
			DatagramSocket Socket = new DatagramSocket();
			File apl =new File(
		            "./music/Megadeth - Dystopia.mp3");
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			//music ms=new music(apl);
			os.writeObject(apl);
			byte[] data = outputStream.toByteArray();
			 for (String ip : server.getNickIP().values()) {
					
					
					InetAddress ia=InetAddress.getByName(ip);
				
			
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, ia, server.PORT_SEND_MUSIC);
			Socket.send(sendPacket);
		}
       
       
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
