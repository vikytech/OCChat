package com.android.occhat.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    private LinearLayout messagesLayout;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    private String line = "";
    private TextView status;
    private Context context;
    private ScrollView scrollView;

    @Override
    protected Object doInBackground(Object... params) {
        serverIp = String.valueOf(params[0]);
        status = (TextView) params[1];
        messagesLayout = (LinearLayout) params[2];
        context = (Context) params[3];
        scrollView = (ScrollView) params[4];

        try {
            if (serverIp != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Listening on IP: " + serverIp);
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
                            status.setText("Connected.");
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
                                    TextView message = new TextView(context);
                                    message.setText(line);
                                    message.setGravity(Gravity.RIGHT);
                                    messagesLayout.addView(message);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            scrollView.fullScroll(HorizontalScrollView.FOCUS_DOWN);
                                        }
                                    }, 100L);
                                }
                            });
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("Oops. Connection interrupted. Please reconnect your phones.");
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
                        status.setText("Couldn't detect internet connection.");
                    }
                });
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    status.setText("Error");
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
