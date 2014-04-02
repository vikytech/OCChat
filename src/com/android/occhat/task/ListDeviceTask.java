package com.android.occhat.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.occhat.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ListDeviceTask extends AsyncTask {
    private final int START = 1;
    private final int END = 255;
    private final String PING_COMMAND = "/system/bin/ping -A -q -n -w 1 -W 1 -c 1 ";
    private String myIP;
    private List<String> deviceList;
    private ListView deviceListView;
    private Context context;

    @Override
    protected void onProgressUpdate(Object... values) {
        ListAdapter listAdapter = new ArrayAdapter<String>(context, R.layout.device_list_view, (ArrayList<String>) values[0]);
        deviceListView.setAdapter(listAdapter);
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object... params) {
        deviceList = new ArrayList<String>();
        myIP = (String) params[0];
        deviceListView = (ListView) params[1];
        context = (Context) params[2];

        String[] mySubnetMasks = myIP.split(Pattern.quote("."));
        String myPartialIp = mySubnetMasks[0] + "." + mySubnetMasks[1] + "." + mySubnetMasks[2] + ".";

        for (int i = START; i <= END; i++) {
            String potentialHostIP = myPartialIp + i;
            try {
                Process process = Runtime.getRuntime().exec(PING_COMMAND + potentialHostIP);
                if (process.waitFor() == 0) {
                    deviceList.add(potentialHostIP);
                    publishProgress(deviceList);
                    Log.d("Adding", potentialHostIP);
                } else {
                    Log.d("Not Adding", potentialHostIP);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return deviceList;
    }

    @Override
    protected void onPostExecute(Object o) {
        this.cancel(true);
        Toast.makeText(context, R.string.device_scan_complete, Toast.LENGTH_LONG).show();
        super.onPostExecute(o);
    }
}
