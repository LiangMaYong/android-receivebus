package com.liangmayong.receivebus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

import com.liangmayong.receivebus.converter.ReceiveBusConverter;
import com.liangmayong.receivebus.dispatch.ReceiveBusDispatchListener;
import com.liangmayong.receivebus.listener.ReceiveBusListener;
import com.liangmayong.receivebus.receiver.ReceiveBusReceiver;

import java.util.HashMap;
import java.util.Map;

/**
 * ReceiveBus
 *
 * @author LiangMaYong
 * @version 1.0
 */
public class ReceiveBus {

    private static final String TAG = ReceiveBus.class.getSimpleName();

    private static Context context = null;

    /**
     * initialize receive bus
     *
     * @param context context
     */
    public static void initialize(Context context) {
        if (context != null) {
            ReceiveBus.context = context.getApplicationContext();
        }
    }

    /**
     * getContext
     *
     * @return context
     */
    private static Context getContext() {
        return context;
    }


    /**
     * isDebuggable
     *
     * @return true or false
     */
    private static boolean isDebuggable() {
        try {
            ApplicationInfo info = getContext().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    // airbus
    private final static Map<String, ReceiveBus> RECEIVE_BUS = new HashMap<String, ReceiveBus>();

    /**
     * getDefault
     *
     * @return receive bus
     */
    public static ReceiveBus getDefault() {
        return get(getContext().getPackageName());
    }

    /**
     * get
     *
     * @param receiveName receiveName
     * @return receive bus
     */
    public static ReceiveBus get(String receiveName) {
        if (getContext() == null) {
            throw new IllegalArgumentException(ReceiveBus.class.getSimpleName() + " not initialized");
        }
        if (RECEIVE_BUS.containsKey(receiveName)) {
            return RECEIVE_BUS.get(receiveName);
        } else {
            ReceiveBus airbus = new ReceiveBus(receiveName);
            RECEIVE_BUS.put(receiveName, airbus);
            return airbus;
        }
    }

    private final String receiveName;
    private final Map<Object, BroadcastReceiver> receivers;
    private ReceiveBusListener commonListener = null;

    /**
     * ReceiveBus
     *
     * @param receiveName receiveName
     */
    private ReceiveBus(String receiveName) {
        if (RECEIVE_BUS.containsKey(receiveName)) {
            throw new IllegalArgumentException("ReceiveBus already exists：" + receiveName);
        }
        this.receiveName = receiveName;
        this.receivers = new HashMap<>();
        registerCommonListener();
    }

    /**
     * setCommonListener
     *
     * @param commonListener commonListener
     */
    public void setCommonListener(ReceiveBusListener commonListener) {
        this.commonListener = commonListener;
    }

    /**
     * registerCommonListener
     */
    private void registerCommonListener() {
        try {
            BroadcastReceiver broadcastReceiver = new ReceiveBusReceiver(receiveName, new ReceiveBusListener() {
                @Override
                public void onReceiveEvent(Object event) {
                    if (commonListener != null) {
                        commonListener.onReceiveEvent(event);
                    }
                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(receiveName);
            getContext().registerReceiver(broadcastReceiver, filter);
        } catch (Exception e) {
        }
    }

    /**
     * post
     *
     * @param event event
     */
    public void post(Object event) {
        try {
            if (isDebuggable()) {
                Log.d(TAG, "ReceiveBus post:" + receiveName + " event:" + event);
            }
            Intent intent = new Intent();
            intent.setAction(receiveName);
            Bundle extras = ReceiveBusConverter.parserExtras(event);
            if (extras != null) {
                intent.putExtras(extras);
                getContext().sendBroadcast(intent, getContext().getPackageName() + ".permission.RECEIVE_BUS");
            }
        } catch (Exception e) {
            if (isDebuggable()) {
                Log.e(TAG, "ReceiveBus post fail : " + receiveName + " event:" + event, e);
            }
        }
    }

    public void postOverall(Object event) {
        try {
            if (isDebuggable()) {
                Log.d(TAG, "ReceiveBus post:" + receiveName + " event:" + event);
            }
            Intent intent = new Intent();
            intent.setAction(receiveName);
            Bundle extras = ReceiveBusConverter.parserExtras(event);
            if (extras != null) {
                intent.putExtras(extras);
                getContext().sendBroadcast(intent);
            }
        } catch (Exception e) {
            if (isDebuggable()) {
                Log.e(TAG, "ReceiveBus post fail : " + receiveName + " event:" + event, e);
            }
        }
    }

    /**
     * register
     *
     * @param object object
     */
    public void register(Object object) {
        try {
            if (object == null) {
                return;
            }
            if (isDebuggable()) {
                Log.e(TAG, "ReceiveBus register : " + receiveName + " object:" + object);
            }
            if (receivers.containsKey(object)) {
                return;
            }
            BroadcastReceiver broadcastReceiver = new ReceiveBusReceiver(receiveName, new ReceiveBusDispatchListener(object));
            IntentFilter filter = new IntentFilter();
            filter.addAction(receiveName);
            getContext().registerReceiver(broadcastReceiver, filter);
            receivers.put(object, broadcastReceiver);
        } catch (Exception e) {
            if (isDebuggable()) {
                Log.e(TAG, "ReceiveBus register fail : " + receiveName + " object:" + object, e);
            }
        }
    }

    /**
     * unregister
     *
     * @param object object
     */
    public void unregister(Object object) {
        try {
            if (object == null) {
                return;
            }
            if (isDebuggable()) {
                Log.e(TAG, "ReceiveBus unregister : " + receiveName + " object:" + object);
            }
            if (receivers.containsKey(object)) {
                BroadcastReceiver broadcastReceiver = receivers.get(object);
                try {
                    getContext().unregisterReceiver(broadcastReceiver);
                } catch (Exception e) {
                }
                receivers.remove(object);
            }
        } catch (Exception e) {
            if (isDebuggable()) {
                Log.e(TAG, "ReceiveBus unregister fail : " + receiveName + " object:" + object, e);
            }
        }
    }
}
