/*
package com.example.misurapp.clientServer;

import com.example.misurapp.db.BoyscoutsInstrumentRecords;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.LinkedList;

public class ServeOneClient extends Thread {

    private Socket socket;
    private ObjectInputStream inFromClient;

    public ServeOneClient(Socket s) throws IOException {
        socket = s;
        inFromClient = new ObjectInputStream(s.getInputStream());
        start();
    }

    public void run() {
        try {
            LinkedList<BoyscoutsInstrumentRecord> boyscoutsInstrumentRecords =
                    (LinkedList<BoyscoutsInstrumentRecord>) inFromClient.readObject();
            //scrivi arrayList su db
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); //anche qui!!
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Unable to close socket"); //da cambiare con l'eccezione
            }
        }

    }

}
*/
