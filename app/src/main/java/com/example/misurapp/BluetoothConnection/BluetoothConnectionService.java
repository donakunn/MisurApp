
        /*
         * Copyright (C) 2014 The Android Open Source Project
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */


        package com.example.misurapp.BluetoothConnection;

        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
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
            protected static final String TAG = "BluetoothChatService";

            // Name for the SDP record when creating server socket
            protected static final String NAME = "BluetoothConnectionService";

            // Unique UUID for this application
            protected static final UUID MY_UUID =
                    UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

            // Member fields
            protected final BluetoothAdapter mAdapter;
            protected final Handler mHandler;
            protected int mState;
            protected int mNewState;

            // Constants that indicate the current connection state
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
                Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TOAST,Constants.CONNECTIONFAILED);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                // Update UI title
                setStateAndUpdateTitle(STATE_NONE);

            }

            /**
             * Send the name of the connected device back to the UI Activity
             *
             * @param device object of the connected device
             */
            protected void sendDeviceNameToHandler(BluetoothDevice device) {
                Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DEVICE_NAME, device.getName());
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }


            /**
             * Indicate that the connection was lost and notify the UI Activity.
             */
            protected void connectionLost() {
                // Send a failure message back to the Activity
                Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TOAST, Constants.CONNECTIONLOST);
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


