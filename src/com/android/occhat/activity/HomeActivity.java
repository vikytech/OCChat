package com.android.occhat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.android.occhat.R;

public class HomeActivity extends Activity {
    private EditText ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    public void startChatting(View view) {
        Intent openChatWindow;
        openChatWindow = new Intent(this, ClientActivity.class);
        ipAddress = (EditText) findViewById(R.id.ipAddress);
        openChatWindow.putExtra("ipAddress", ipAddress.getText());
        startActivity(openChatWindow);
    }

    public void startServer(View view) {
        Intent server;
        server = new Intent(this, ServerActivity.class);
        startActivity(server);
    }
}
