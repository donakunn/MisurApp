package com.example.misurapp.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */

public class BluetoothConnectionService {
    // Debugging
    protected static final String TAG = "BluetoothConnService";

    /**
     * Name for the SDP record when creating server socket
     */
    protected static final String NAME = "BluetoothConnectionService";

    /**
     * Unique UUID for this application
     */
    protected static final UUID MY_UUID =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    /**
     * Member fields
     */
    protected final BluetoothAdapter mAdapter;
    protected final Handler mHandler;
    protected int mState;
    protected int mNewState;

    /**
     * Constants that indicate the current connection state
     */
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param handler A Handler to send messages back to the UI Activity
     */

    public BluetoothConnectionService(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }

    /**
     * Update UI title according to the current state of the chat connection
     */

    synchronized void updateUserInterfaceTitle() {
        mState = getState();
        Log.d(TAG, "updateUserInterfaceTitle() " + mNewState + " -> " + mState);
        mNewState = mState;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mNewState, -1)
                .sendToTarget();
    }

    /**
     * Return the current connection state.
     */

    public synchronized int getState() {
        return mState;
    }

    protected void connectionFailed() {
        // Send a failure message back to the Activity
        sendStringToastToHandler(Constants.CONNECTIONFAILED);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    protected void connectionLost() {
        // Send a failure message back to the Activity
        sendStringToastToHandler(Constants.CONNECTIONLOST);
    }

    /**
     * Send a state message to be shown on a Toast to the Handler.
     *
     * @param message String message to send back to the activity
     */
    protected void sendStringToastToHandler(String message) {
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, message);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setStateAndUpdateTitle(STATE_NONE);
    }

    /**
     * Change connection state and call updateUserInterfaceTitle()
     *
     * @param state new state of the connection
     */
    protected void setStateAndUpdateTitle(int state) {
        mState = state;
        updateUserInterfaceTitle();
    }
}


