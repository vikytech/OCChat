package com.android.occhat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.android.occhat.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class ClientActivity extends Activity {
    private String serverIpAddress = "";
    private boolean connected = false;
    private PrintWriter out;
    private EditText chatBox;
    private Socket socket;

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
        chatBox = (EditText) findViewById(R.id.chat_box);
    }

    @Override
    protected void onStop() {
        try {
            socket.close();
            Log.d("ClientActivity", "C: Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    public void sendMessage(View view) {
        message();
    }

    private void message() {
        out.print(String.valueOf(chatBox.getText()));
        Log.d("ClientActivity", "C: Sent.");
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
                socket = clientChannel.socket();
                inetSocketAddress = new InetSocketAddress(serverAddr, 8888);
                socket.connect(inetSocketAddress);
                connected = true;
                try {
                    Log.d("ClientActivity", "C: Sending command.");
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}