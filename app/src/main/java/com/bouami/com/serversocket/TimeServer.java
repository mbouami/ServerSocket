package com.bouami.com.serversocket;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Mohammed on 15/02/2017.
 */

public class TimeServer {

    //On initialise des valeurs par défaut
    private int mPort;
    private String mHost;
    private ServerSocket server = null;
    private boolean isRunning = true;
    private Handler mUpdateHandler;

//    public TimeServer(DataDisplay dataDisplay){
//        try {
////            server = new ServerSocket(mPort, 100, InetAddress.getByName(mHost));
//            server = new ServerSocket(mPort);
//            m_dataDisplay = dataDisplay;
////            Message serverMessage = Message.obtain();
////            serverMessage.obj = "Serveur à l'écoute."+InetAddress.getByName(mHost);
//////            Log.d("TIMER_SERVER","Serveur à l'écoute."+InetAddress.getByName(mHost));
////            mHandler.sendMessage(serverMessage);
//            AfficherMessage("Serveur à l'écoute."+InetAddress.getByName(mHost));
////            m_dataDisplay.Display("Serveur à l'écoute."+InetAddress.getByName(mHost));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public TimeServer(String pHost, Handler handler){
        mHost = pHost;
        mUpdateHandler = handler;
        try {
            server = new ServerSocket(0);
            setLocalPort(server.getLocalPort());
//            server = new ServerSocket(mPort, 100, InetAddress.getByName(mHost));
            AfficherMessage("Serveur à l'écoute."+InetAddress.getByName(mHost)+" sur le Port : "+getLocalPort());
//            Log.d("TIMER_SERVER","Serveur à l'écoute."+InetAddress.getByName(mHost));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLocalPort() {
        return mPort;
    }

    public void setLocalPort(int port) {
        mPort = port;
    }

    private void AfficherMessage(String message) {
        Message clientMessage = Message.obtain();
        clientMessage.obj = message;
        mUpdateHandler.sendMessage(clientMessage);
    }


    //On lance notre serveur
    public void open(){
        //Toujours dans un thread à part vu qu'il est dans une boucle infinie
        Thread t = new Thread(new Runnable(){
            @Override
            public void run(){
                while(isRunning == true){

                    try {
                        //On attend une connexion d'un client
                        Socket client = server.accept();
                        //Une fois reçue, on la traite dans un thread séparé
//                        System.out.println("Connexion cliente reçue.");
//                        Log.d("TIMER_SERVER","Connexion cliente reçue.");
//                        AfficherMessage("Connexion cliente reçue.");
//                        MainActivity.getZoneAffichage().setText("Connexion cliente reçue.");
                        Thread t = new Thread(new ClientProcessor(client,mUpdateHandler));
                        t.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    server = null;
                }
            }
        });

        t.start();
    }

    public void close(){
        isRunning = false;
    }

}
