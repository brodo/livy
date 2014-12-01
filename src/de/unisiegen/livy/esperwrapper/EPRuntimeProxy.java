package de.unisiegen.livy.esperwrapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julian Dax on 01.12.14.
 */
public class EPRuntimeProxy {
    private Object epRuntime;
    private Class epRuntimeClass;
    public EPRuntimeProxy(Object epRuntime) {
        this.epRuntime = epRuntime;
        epRuntimeClass = epRuntime.getClass();
    }

    public void sendEvent(HashMap event, String name){
        try {
            Method sendEvent = epRuntimeClass.getMethod("sendEvent", Map.class, String.class);
            sendEvent.invoke(epRuntime, event, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(Object event){
        try {
            Method sendEvent = epRuntimeClass.getMethod("sendEvent", Object.class);
            sendEvent.invoke(epRuntime, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
