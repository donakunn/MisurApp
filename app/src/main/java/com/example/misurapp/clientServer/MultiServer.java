package com.example.misurapp.clientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {
    private static final int PORT = 8070;
    private static boolean scoutMaster = false;

    public static boolean isScoutMaster() {
        return scoutMaster;
    }

    public static void setScoutMaster() {
        scoutMaster = !scoutMaster;
    }

    public static void startServer() throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        try {
            while (isScoutMaster()) {
                Socket socket= s.accept();

                try {
                    new ServeOneClient(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }

        } finally {
            s.close();
        }

    }
}
