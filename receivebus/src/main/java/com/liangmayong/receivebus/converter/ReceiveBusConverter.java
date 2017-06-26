package com.liangmayong.receivebus.converter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * ReceiveBusConverter
 *
 * @author LiangMaYong
 * @version 1.0
 */
public class ReceiveBusConverter {

    private ReceiveBusConverter() {
    }

    private static final String EVENT_EXTRAS = "receive_bus_event_extras";
    private static final String EVENT_TYPE = "receive_bus_event_type";

    private static final HashMap<String, Object> OBJECTS = new HashMap<>();
    private static final int TYPE_PARCELABLE = 1;
    private static final int TYPE_SERIALIZABLE = 2;
    private static final int TYPE_OBJECT = 3;

    private static Handler mHandler = new Handler();

    private static class RemoveRunnable implements Runnable {

        private String eventId = "";

        public RemoveRunnable(String eventId) {
            this.eventId = eventId;
        }

        @Override
        public void run() {
            OBJECTS.remove(eventId);
        }
    }

    /**
     * parserEvent
     *
     * @param extras extras
     * @return event
     */
    public static Object parserEvent(Bundle extras) {
        try {
            int type = extras.getInt(EVENT_TYPE);
            if (type == TYPE_PARCELABLE) {
                return extras.getParcelable(EVENT_EXTRAS);
            } else if (type == TYPE_SERIALIZABLE) {
                return extras.getSerializable(EVENT_EXTRAS);
            } else {
                String eventId = extras.getString(EVENT_EXTRAS);
                if (OBJECTS.containsKey(eventId)) {
                    Object object = OBJECTS.get(eventId);
                    mHandler.postDelayed(new RemoveRunnable(eventId), 3000);
                    return object;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * parserExtras
     *
     * @param event event
     * @return extras
     */
    public static Bundle parserExtras(Object event) {
        Bundle extras = new Bundle();
        if (event instanceof Parcelable) {
            extras.putParcelable(EVENT_EXTRAS, (Parcelable) event);
            extras.putInt(EVENT_TYPE, TYPE_PARCELABLE);
        } else if (event instanceof Serializable) {
            extras.putSerializable(EVENT_EXTRAS, (Serializable) event);
            extras.putInt(EVENT_TYPE, TYPE_SERIALIZABLE);
        } else {
            String eventId = UUID.randomUUID().toString();
            OBJECTS.put(eventId, event);
            extras.putString(EVENT_EXTRAS, eventId);
            extras.putInt(EVENT_TYPE, TYPE_OBJECT);
        }
        return extras;
    }
}
