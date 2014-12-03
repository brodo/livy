package de.unisiegen.livy.esperwrapper;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Julian Dax on 01.12.14.
 */
public class EPRuntimeProxy {
    private Object epRuntime;
    private Class epRuntimeClass;
    private static String LOG_TAG = "Livy/EPRuntimeProxy";

    public EPRuntimeProxy(Object epRuntime) {
        this.epRuntime = epRuntime;
        epRuntimeClass = epRuntime.getClass();
    }

    public void sendEvent(Map event, String name){
        try {
            Method sendEvent = epRuntimeClass.getMethod("sendEvent", Map.class, String.class);
            sendEvent.invoke(epRuntime, event, name);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not send event", e);
        }
    }

    public void sendEvent(Object event){
        try {
            Method sendEvent = epRuntimeClass.getMethod("sendEvent", Object.class);
            sendEvent.invoke(epRuntime, event);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not send event", e);
        }
    }
}
