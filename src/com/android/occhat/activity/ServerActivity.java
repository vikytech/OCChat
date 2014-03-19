package com.android.occhat.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.android.occhat.R;
import com.android.occhat.task.ServerTask;

public class ServerActivity extends Activity {
    private TextView serverStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        serverStatus = (TextView) findViewById(R.id.server_status);
        ServerTask serverTask = new ServerTask();
        serverTask.execute(serverStatus);
    }

}
