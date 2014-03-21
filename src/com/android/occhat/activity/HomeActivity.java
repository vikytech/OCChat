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
        ipAddress = (EditText) findViewById(R.id.ipAddress);
    }

    public void startChatting(View view) {
        Intent openChatWindow;
        openChatWindow = new Intent(this, ClientActivity.class);
        openChatWindow.putExtra("ipAddress", ipAddress.getText());
        startActivity(openChatWindow);
    }
}
