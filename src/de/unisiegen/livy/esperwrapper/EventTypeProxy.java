package de.unisiegen.livy.esperwrapper;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Julian Dax on 02.12.14.
 */
public class EventTypeProxy {

    private static final String LOG_TAG = "Livy/EventTypeProxy";
    private final Object eventType;
    private final Class<? extends Object> eventTypeClass;

    public EventTypeProxy(Object eventType) {
        this.eventType = eventType;
        this.eventTypeClass = eventType.getClass();
    }

    public String getName(){
        try {
            Method getName = eventTypeClass.getMethod("get");
            return (String) getName.invoke(eventType);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not execute getName function.", e);
        }
        return null;
    }

    public String[] getPropertyNames(){
        try {
            Method getPropertyNames = eventTypeClass.getMethod("getPropertyNames");
            return (String[]) getPropertyNames.invoke(eventType);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not execute getPropertyNames function.", e);
        }
        return null;

    }
}
