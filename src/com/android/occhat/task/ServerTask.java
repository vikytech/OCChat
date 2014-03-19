package com.android.occhat.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class ServerTask extends AsyncTask{
    public static final int SERVERPORT = 8888;
    public static String SERVERIP = "10.16.3.174";
    private TextView serverStatus;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    private String line = null;

    @Override
    protected Object doInBackground(Object... params) {
        serverStatus = (TextView) params[0];
        try {
            if (SERVERIP != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Listening on IP: " + SERVERIP);
                    }
                });
                ServerSocketChannel channel = ServerSocketChannel.open();
                serverSocket = channel.socket();
                serverSocket.bind(new InetSocketAddress(SERVERPORT));
                while (true) {
                    Socket client = serverSocket.accept();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Connected.");
                        }
                    });

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        while ((line = in.readLine()) != null) {
                            Log.d("ServerActivity", line);
                            serverStatus.setText(line);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText(line);
                                    // DO WHATEVER YOU WANT TO THE FRONT END
                                    // THIS IS WHERE YOU CAN BE CREATIVE
                                }
                            });
                        }
                        break;
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                            }
                        });
                        e.printStackTrace();
                    }
                }
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Couldn't detect internet connection.");
                    }
                });
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    serverStatus.setText("Error");
                }
            });
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        super.onPostExecute(o);
    }
}
