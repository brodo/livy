package de.unisiegen.livy.esperwrapper.core;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Proxy class for com.espertech.esper.client.EPRuntime
 * Created by Julian Dax on 13/08/14.
 *
 */

public class EPRuntimeProxy {
    private Object epRuntime;
    private Class epRuntimeClass;

    public EPRuntimeProxy(Object epRuntime, Class epRuntimeClass) {
        this.epRuntime = epRuntime;
        this.epRuntimeClass = epRuntimeClass;
    }

    public void sendEvent(Object event) {
        try {
            Method sendEvent = epRuntimeClass.getMethod("sendEvent", Object.class);
            sendEvent.invoke(epRuntime, event);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendEvent(Map event, String name) {
        try {
            Method sendEvent = epRuntimeClass.getMethod("sendEvent", Map.class, Object.class);
            sendEvent.invoke(epRuntime, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getCurrentTime() {
        try {
            Method getCurrentTime = epRuntimeClass.getMethod("getCurrentTime");
            Object result = getCurrentTime.invoke(epRuntime);
            return ((Double) result).longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
