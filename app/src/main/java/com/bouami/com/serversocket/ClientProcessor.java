package com.bouami.com.serversocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Mohammed on 15/02/2017.
 */
public class ClientProcessor implements Runnable {

    private Socket sock;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;
    private Handler mUpdateHandler;

    public ClientProcessor(Socket pSock,Handler handler){
        sock = pSock;
        mUpdateHandler = handler;
    }

    private void AfficherMessage(String message) {
        Message clientMessage = Message.obtain();
        clientMessage.obj = message;
        mUpdateHandler.sendMessage(clientMessage);
    }

    //Le traitement lancé dans un thread séparé
    @Override
    public void run(){
//        System.err.println("Lancement du traitement de la connexion cliente");
//        Log.d("CLIENT_PROCESSOR","Lancement du traitement de la connexion cliente");
//        AfficherMessage("Lancement du traitement de la connexion cliente");
        boolean closeConnexion = false;
        //tant que la connexion est active, on traite les demandes
//        while(!sock.isClosed()){

            try {

                //Ici, nous n'utilisons pas les mêmes objets que précédemment
                //Je vous expliquerai pourquoi ensuite
                writer = new PrintWriter(sock.getOutputStream());
                reader = new BufferedInputStream(sock.getInputStream());

                //On attend la demande du client
                String response = read();
                InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
                AfficherMessage("Message reçu de la part de : "+remote.getAddress().getHostAddress()+"--"+response.toUpperCase());
                //On affiche quelques infos, pour le débuggage
//                String debug = "";
//                debug = "Thread : " + Thread.currentThread().getName() + ". ";
//                debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() +".";
//                debug += " Sur le port : " + remote.getPort() + ".\n";
//                debug += "\t -> Commande reçue : " + response + "\n";
////                System.err.println("\n" + debug);
//                Log.d("CLIENT_PROCESSOR","Message reçu de la part de : "+remote.getAddress().getHostAddress()+"--"+response.toUpperCase());
                //On traite la demande du client en fonction de la commande envoyée
                String toSend = "";
//                MainActivity.getZoneAffichage().setText(response.toUpperCase());
                switch(response.toUpperCase()){
                    case "FULL":
                        toSend = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(new Date());
                        break;
                    case "DATE":
                        toSend = DateFormat.getDateInstance(DateFormat.FULL).format(new Date());
                        break;
                    case "HOUR":
                        toSend = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date());
                        break;
                    case "CLOSE":
                        toSend = "Communication terminée";
                        closeConnexion = true;
                        break;
                    default :
                        toSend = "Commande inconnu !";
                        break;
                }

                //On envoie la réponse au client
                writer.write(toSend);
                //Il FAUT IMPERATIVEMENT UTILISER flush()
                //Sinon les données ne seront pas transmises au client
                //et il attendra indéfiniment
                writer.flush();

                if(closeConnexion){
                    AfficherMessage("COMMANDE CLOSE DETECTEE ! ");
                    writer = null;
                    reader = null;
                    sock.close();
//                    break;
                }
            }catch(SocketException e){
//                System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
//                Log.d("CLIENT_PROCESSOR","LA CONNEXION A ETE INTERROMPUE ! ");
                AfficherMessage("LA CONNEXION A ETE INTERROMPUE ! ");
//                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//    }

    //La méthode que nous utilisons pour lire les réponses
    private String read() throws IOException{
        String response = "";
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        response = new String(b, 0, stream);
        return response;
    }
}
