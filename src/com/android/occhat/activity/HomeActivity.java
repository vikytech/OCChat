package com.android.occhat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.occhat.R;
import com.android.occhat.task.ListDeviceTask;
import com.android.occhat.task.WifiReceiver;

import java.lang.reflect.Method;

import static android.widget.AdapterView.OnItemLongClickListener;
import static com.android.occhat.R.string;

public class HomeActivity extends Activity {
    private final String IP_ADDRESS_FORMAT_REGEX = "\\d+.\\d+.\\d+.\\d+";
    private EditText ipAddress;
    private WifiManager manager;
    private String myIP;
    private ListView deviceListView;
    private ListDeviceTask devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        manager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        myIP = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
        TextView myIpAddress = (TextView) findViewById(R.id.my_ip_address);
        myIpAddress.setText("Your IP Address: " + myIP);

        ipAddress = (EditText) findViewById(R.id.ipAddress);
        deviceListView = (ListView) findViewById(R.id.onlineDevices);
        deviceListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String targetDeviceIP = ((TextView) view).getText().toString();
                ipAddress.setText(targetDeviceIP);
                return true;
            }
        });

        myIpAddress.setText(myIpAddress.getText());
        devices = new ListDeviceTask();
        WifiReceiver wifiReceiver = new WifiReceiver(manager);
        wifiReceiver.start();
        Toast.makeText(this, string.device_scan_started, Toast.LENGTH_LONG).show();
        devices.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, myIP, deviceListView, this, manager);
        ipAddress.setText(myIP);
    }

    public void startChatting(View view) {
        Intent openChatWindow;
        openChatWindow = new Intent(this, ClientActivity.class);
        String ipAddressText = String.valueOf(ipAddress.getText());
        if (!ipAddressText.matches(IP_ADDRESS_FORMAT_REGEX)) {
            Toast.makeText(this, getString(string.warning_for_valid_ip), Toast.LENGTH_LONG).show();
        }
        openChatWindow.putExtra("ipAddress", ipAddressText);
        startActivity(openChatWindow);

    }

    public void createHotSpot(View view) {
        if (manager.isWifiEnabled()) {
            AlertDialog.Builder createHotspotDialog = new AlertDialog.Builder(this);
            createHotspotDialog.setTitle(getString(string.creating_hotspot))
                    .setMessage(getString(string.wifi_off_warning))
                    .setCancelable(false)
                    .setPositiveButton(getString(string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toggleHotspot();
                        }
                    })
                    .setNegativeButton(getString(string.cancel), new DialogInterface.OnClickListener() {
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
