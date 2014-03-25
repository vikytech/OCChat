package com.android.occhat.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private LinearLayout messagesLayout;
    private ServerTask serverTask;
    private TextView status;
    private Context context;
    private String senderMessage;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        chatBox = (EditText) findViewById(R.id.chat_box);
        ipAddress = String.valueOf(getIntent().getExtras().get("ipAddress"));
        messagesLayout = (LinearLayout) findViewById(R.id.messageLayout);
        status = (TextView) findViewById(R.id.status);
        context = this;
        sendButton = (Button) findViewById(R.id.send_message);

        chatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setEnabled(!s.toString().matches("\\s++"));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (String.valueOf(chatBox.getText()).isEmpty()) sendButton.setEnabled(false);
            }
        });

        serverTask = new ServerTask();
        serverTask.execute(ipAddress, status, messagesLayout, context);
    }

    @Override
    public void onBackPressed() {
        serverTask.cancel(true);
        super.onBackPressed();
    }

    public void sendMessage(View view) {
        new ClientThread().start();
        TextView message = new TextView(context);
        senderMessage = String.valueOf(chatBox.getText());
        message.setText("Me: " + senderMessage);
        message.setTextColor(R.color.in_msg);
        messagesLayout.addView(message);
        chatBox.setText("");
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
                    out.write(senderMessage);
                    out.flush();
                    Log.d("ClientActivity", "C: Sent.");
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
            } finally {
                try {
                    socket.close();
                    Log.d("ClientActivity", "C: Closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
