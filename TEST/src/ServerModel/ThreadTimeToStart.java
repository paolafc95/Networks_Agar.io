package ServerModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ThreadTimeToStart extends Thread{

	private ServerModel server;
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

			while (server.getUsers().size() <= 5 && !server.isTimeLimit()) {
				System.out.println("inicio");
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
							server.setTimeLimit(true);
							
							break;
						}
					}
				}
				String time = nuMin + ":" + nuSeg;
				server.setTimeLoad(time);
				for (int i = 0; i < server.getUsers().size(); i++) {
					try {
						BufferedWriter bw = (BufferedWriter) server.getUsers().get(i);
						bw.write("time;"+time);
						bw.write("\r\n");
						bw.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				sleep(998);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
