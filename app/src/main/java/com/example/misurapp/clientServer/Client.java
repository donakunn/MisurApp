package com.example.misurapp.clientServer;

import com.example.misurapp.db.BoyscoutsInstrumentRecords;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Client {

    /*public static void sendBoyscoutsRecords(
            List<BoyscoutsInstrumentRecord> boyscoutsInstrumentRecords) throws IOException {
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        Socket socket = new Socket(addr, 8070);
        try {
            ObjectOutputStream outToServer = new ObjectOutputStream(
                    socket.getOutputStream());
            outToServer.writeObject(boyscoutsInstrumentRecords);
        } finally {
            socket.close();
        }
    }*/
}
