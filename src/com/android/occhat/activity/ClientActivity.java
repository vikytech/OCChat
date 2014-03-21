package com.android.occhat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.android.occhat.R;
import com.android.occhat.task.ServerTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class ClientActivity extends Activity {
    private String ipAddress = "";
    private PrintWriter out;
    private EditText chatBox;
    private Socket socket;
    private TextView serverStatus;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        chatBox = (EditText) findViewById(R.id.chat_box);
        ipAddress = String.valueOf(getIntent().getExtras().get("ipAddress"));
        serverStatus = (TextView) findViewById(R.id.server_status);

        ServerTask serverTask = new ServerTask();
        serverTask.execute(serverStatus, ipAddress);
    }

    public void sendMessage(View view) {
        new ClientThread().start();
    }

    public void refresh(View view) {
        new ClientThread().start();
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

    public class ClientThread extends Thread {
        public void run() {
            SocketChannel clientChannel;
            InetAddress serverAddr;
            InetSocketAddress inetSocketAddress;
            try {
                serverAddr = InetAddress.getByName(ipAddress);
                Log.d("ClientActivity", "C: Connecting...");
                clientChannel = SocketChannel.open();
                socket = clientChannel.socket();
                inetSocketAddress = new InetSocketAddress(serverAddr, 8888);
                socket.connect(inetSocketAddress);
                try {
                    Log.d("ClientActivity", "C: Sending command.");
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.write(String.valueOf(chatBox.getText()));
                    out.flush();
                    Log.d("ClientActivity", "C: Sent.");
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
