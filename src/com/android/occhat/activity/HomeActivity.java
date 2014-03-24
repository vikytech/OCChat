package com.android.occhat.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.android.occhat.R;

public class HomeActivity extends Activity {
    private EditText ipAddress;
    private WifiManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        manager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        String myIP = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
        TextView myIpAddress = (TextView) findViewById(R.id.my_ip_address);
        myIpAddress.setText("Your IP Address: " + myIP);
        ipAddress = (EditText) findViewById(R.id.ipAddress);
    }

    public void startChatting(View view) {
        Intent openChatWindow;
        openChatWindow = new Intent(this, ClientActivity.class);
        openChatWindow.putExtra("ipAddress", ipAddress.getText());
        startActivity(openChatWindow);
    }
}
