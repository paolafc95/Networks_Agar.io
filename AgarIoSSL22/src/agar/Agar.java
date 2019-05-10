package agar;

import View.Client;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Agar {
    public static void main(String[] args) {
        try {
            //WindowGame w = new WindowGame(args[0], args[1]);
            Client c = new Client("localhost", "1099");
           // c.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
