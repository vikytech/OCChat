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

public class ServerTask extends AsyncTask {
    private static final int SERVERPORT = 8888;
    private String serverIp;
    private TextView serverStatus;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    private String line = null;

    @Override
    protected Object doInBackground(Object... params) {
        serverStatus = (TextView) params[0];
        serverIp = String.valueOf(params[1]);
        try {
            if (serverIp != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Listening on IP: " + serverIp);
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
                    while (true) {
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            line = in.readLine();
                            Log.d("Test message", line);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.append(":" + line + '\n');
                                }
                            });
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
            e.printStackTrace();
        }
        super.onPostExecute(o);
    }
}
