package com.example.misurapp.bluetoothConnection;

/**
 * Defines several constants used.
 */
public interface Constants {

    // Message types sent from the BluetoothConnectionService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothConnectionService Handler
    String TOAST = "toast";
    String CONNECTIONLOST= "connection_lost";
    String CONNECTIONFAILED= "connection_failed";
    String DATASENDCOMPLETE= "data_send_complete";
}
