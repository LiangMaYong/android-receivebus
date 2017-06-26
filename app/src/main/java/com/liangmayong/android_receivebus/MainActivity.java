package com.liangmayong.android_receivebus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.liangmayong.receivebus.ReceiveBus;
import com.liangmayong.receivebus.annotations.OnReceiveEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReceiveBus.initialize(this);
        ReceiveBus.get("name").register(this);
        ReceiveBus.get("name").post(new Name("nihao"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveBus.get("name").unregister(this);
    }

    @OnReceiveEvent
    protected void toast(Name name) {
        Toast.makeText(this, this.getPackageName() + "\n" + name, Toast.LENGTH_SHORT).show();
    }
}
