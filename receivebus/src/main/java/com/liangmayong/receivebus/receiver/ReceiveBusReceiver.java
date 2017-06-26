package com.liangmayong.receivebus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.liangmayong.receivebus.converter.ReceiveBusConverter;
import com.liangmayong.receivebus.listener.ReceiveBusListener;

/**
 * ReceiveBusReceiver
 *
 * @author LiangMaYong
 * @version 1.0
 */
public class ReceiveBusReceiver extends BroadcastReceiver {

    private String receiveName = "";
    private ReceiveBusListener airBusListener = null;

    public ReceiveBusReceiver(String receiveName, ReceiveBusListener airBusListener) {
        this.receiveName = receiveName;
        this.airBusListener = airBusListener;
    }

    private String getReceiveName() {
        return receiveName;
    }

    public ReceiveBusListener getAirBusListener() {
        return airBusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(getReceiveName())) {
            if (getAirBusListener() != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object event = ReceiveBusConverter.parserEvent(bundle);
                    if (event != null) {
                        getAirBusListener().onReceiveEvent(event);
                    }
                }
            }
        }
    }

}
