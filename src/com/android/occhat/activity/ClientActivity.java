package com.android.occhat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.android.occhat.R;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class ClientActivity extends Activity {
    private String serverIpAddress = "";
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        if (!connected) {
            serverIpAddress = "10.16.3.174";
            if (!serverIpAddress.equals("")) {
                ClientThread cThread = new ClientThread();
                cThread.start();
            }
        }
    }

    public class ClientThread extends Thread {

        public void run() {
            SocketChannel clientChannel;
            InetAddress serverAddr;
            InetSocketAddress inetSocketAddress;
            try {
                serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                clientChannel = SocketChannel.open();
                Socket socket = clientChannel.socket();
                inetSocketAddress = new InetSocketAddress(serverAddr, 8888);
                socket.connect(inetSocketAddress);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println("Pinging the Server!");
                        Log.d("ClientActivity", "C: Sent.");
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}