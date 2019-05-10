package ModelClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Model.music;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import View.Client;

public class RecibeMusic extends Thread{

	private Client client;
	public RecibeMusic(Client cli) {
		// TODO Auto-generated constructor stub
		client=cli;
	}

	public void run() {
		try {
			System.out.println("es");
			DatagramSocket socket = new DatagramSocket(client.PORT_RECIVE_MUSIC);
			byte[] incomingData = new byte[1024];

			while (true) {
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incomingPacket);
				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				try {
					File player =   (File) is.readObject();
					Player pl=new Player(new FileInputStream(player));
					System.out.println("ds");
					pl.play();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
}
