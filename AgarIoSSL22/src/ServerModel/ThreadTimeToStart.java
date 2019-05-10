package ServerModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ThreadTimeToStart extends Thread{

	private ServerModel server;
	private static String tipeTime="";
	public ThreadTimeToStart(ServerModel serv) {
		server=serv;
	}
	private static int nuMin=0;
	public static int getNuMin() {
		return nuMin;
	}

	public static void setNuMin(int nuMin) {
		ThreadTimeToStart.nuMin = nuMin;
	}
	private static int nuSeg=0;
	private static int nuHor=0;

	public static int getNuSeg() {
		return nuSeg;
	}

	public static void setNuSeg(int nuSeg) {
		ThreadTimeToStart.nuSeg = nuSeg;
	}

	public void run() {
		try {

			while (server.isStartTime()) {
				
				if (nuSeg != 0) {
					nuSeg--;
				} else {
					if (nuMin != 0) {
						nuSeg = 59;
						nuMin--;
					} else {
						if (nuHor != 0) {
							nuHor--;
							nuMin = 59;
							nuSeg = 59;
						} else {
							server.setStartTime(false);
							String time = "--:--";
							server.setTimeLoad(time);
							for (String ip : server.getNickIP().values()) {
								
								try {
									Socket envioActualizacionMovimiento = new Socket(ip, server.PORT_SENDTIME);
									ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
											envioActualizacionMovimiento.getOutputStream());
									paqueteReenvio.writeObject(getTipeTime()+";"+time);
									envioActualizacionMovimiento.close();
								} catch (Exception e) {
									// TODO: handle exception
								}
									
								
							}
							if(getTipeTime().equals("wait")) {
								server.StartGame();
							}else {
								System.out.println("finish");
								server.finishGame();
							}
							break;
						}
					}
				}
				String seg=nuSeg+"";
				String min=nuMin+"";
				if(nuSeg<10) {
					seg="0"+nuSeg;
				}if(nuSeg<10) {
					min="0"+nuMin;
				}
				String time = min + ":" + seg;
				server.setTimeLoad(time);
				
				for (String ip : server.getNickIP().values()) {
					
					try {
						Socket envioActualizacionMovimiento = new Socket(ip, server.PORT_SENDTIME);
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(
								envioActualizacionMovimiento.getOutputStream());
						paqueteReenvio.writeObject(getTipeTime()+";"+time);
						envioActualizacionMovimiento.close();
					} catch (Exception e) {
						// TODO: handle exception
					}
						
					
				}
				sleep(998);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getTipeTime() {
		return tipeTime;
	}

	public static void setTipeTime(String tipeTime) {
		ThreadTimeToStart.tipeTime = tipeTime;
	}
}
