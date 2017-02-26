package com.bouami.com.serversocket;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Mohammed on 07/02/2017.
 */

public class MyServer {
    Thread m_objThread;
    ServerSocket m_server;
    String m_strMessage;
    Object m_connect;
    DataDisplay m_dataDisplay;
    private  static final  int PORT=60123;

    public MyServer() {

    }

    public void setEventListner(DataDisplay dataDisplay) {
        m_dataDisplay = dataDisplay;
    }

    public void startListner() {
        m_objThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_server = new ServerSocket(PORT);
//                    Message serverStatus = Message.obtain();
//                    serverStatus.obj = "le serveur est à l'écoute sur le port 2001";
//                    mHandler.sendMessage(serverStatus);
                    Socket connectSocket =  m_server.accept();
                    Message clientMessage = Message.obtain();
                    ObjectInputStream ois = new ObjectInputStream(connectSocket.getInputStream());
                    String strMessage = (String) ois.readObject();
                    clientMessage.obj = strMessage;
                    mHandler.sendMessage(clientMessage);
                    ObjectOutputStream oos = new ObjectOutputStream(connectSocket.getOutputStream());
                    oos.writeObject("Bonne continuation .......");
                    ois.close();
                    oos.close();
                    m_server.close();
                } catch (IOException e) {
                    Message msg3 = Message.obtain();
                    msg3.obj = e.getMessage();
                    mHandler.sendMessage(msg3);
//                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        m_objThread.start();
    }
    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message status) {
            m_dataDisplay.Display(status.obj.toString());
        }
    };
}
