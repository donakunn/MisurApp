package com.example.misurapp.bluetoothConnection;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class manage bluetooth connection and data transfer on server-side
 */
public final class BluetoothServer extends BluetoothConnectionService {
    /**
     * debug tag
     */
    private final String TAG = "BluetoothServer";
    /**
     * AcceptThread object for incoming connections
     */
    private AcceptThread mAcceptThread;
    /**
     * ConnectedThread object for managing connection
     */
    private ConnectedThread mConnectedThread;
    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothServer(Handler handler) {
        super(handler);
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        // Update UI title
        updateUserInterfaceTitle();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setStateAndUpdateTitle(STATE_NONE);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    @Override
    protected void connectionFailed() {
        super.connectionFailed();

        // Start the service over to restart listening mode
        BluetoothServer.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    @Override
    protected void connectionLost() {
        super.connectionLost();

        // Start the service over to restart listening mode
        BluetoothServer.this.start();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     */
    public synchronized void connected(BluetoothSocket socket) {
        Log.d(TAG, "connected");

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        updateUserInterfaceTitle();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */

    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,
                        MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;

            setStateAndUpdateTitle(STATE_LISTEN);
        }

        public void run() {
            Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();

                    setStateAndUpdateTitle(STATE_CONNECTING);
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothServer.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");

        }

        public void cancel() {
            Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            } finally {
                setStateAndUpdateTitle(STATE_NONE);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final DataInputStream dataInputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            InputStream mmInStream = tmpIn;
            dataInputStream = new DataInputStream(mmInStream);
            setStateAndUpdateTitle(STATE_CONNECTED);
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    int length = dataInputStream.readInt();
                    if (length > 0) {
                        byte[] data = new byte[length];
                        dataInputStream.readFully(data, 0, data.length);
                        mHandler.obtainMessage(Constants.MESSAGE_READ, data.length, -1, data)
                                .sendToTarget();
                        setStateAndUpdateTitle(STATE_NONE);
                        BluetoothServer.this.start();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    BluetoothServer.this.start();
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            } finally {
                setStateAndUpdateTitle(STATE_NONE);
            }
        }
    }
}