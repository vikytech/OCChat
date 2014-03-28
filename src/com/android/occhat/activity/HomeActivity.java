package com.android.occhat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.android.occhat.R;

import java.lang.reflect.Method;

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

    public void createHotSpot(View view) {
        if (manager.isWifiEnabled()) {
            AlertDialog.Builder createHotspotDialog = new AlertDialog.Builder(this);
            createHotspotDialog.setTitle(getString(R.string.creating_hotspot))
                    .setMessage(getString(R.string.wifi_off_warning))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toggleHotspot();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        } else toggleHotspot();
    }

    private void toggleHotspot() {
        Method[] methods = manager.getClass().getDeclaredMethods();
        Boolean hotSpotStatus = false;
        for (Method method : methods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    manager.setWifiEnabled(false);
                    hotSpotStatus = (Boolean) method.invoke(manager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(manager, null, !hotSpotStatus);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }
}
