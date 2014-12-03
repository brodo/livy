package de.unisiegen.livy.esperwrapper;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Julian Dax on 02.12.14.
 */
public class EventBeanProxy {
    private static final String LOG_TAG = "Livy/EventBeanProxy";
    private Object eventBean;
    private Class eventBeanClass;

    public EventBeanProxy(Object eventBean) {
        this.eventBean = eventBean;
        this.eventBeanClass = eventBean.getClass();
    }

    public Object get(String propertyExpression){
        try {
            Method get = eventBeanClass.getMethod("get", String.class);
            return get.invoke(eventBean, propertyExpression);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not execute get function.", e);
        }
        return null;
    }

    public EventTypeProxy getEventType(){
        try {
            Method getEventType = eventBeanClass.getMethod("getEventType");
            return new EventTypeProxy(getEventType.invoke(eventBean));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not execute getEventType.", e);
        }
        return null;
    }

    public Object getUnderlying(){
        try {
            Method getUnderlying = eventBeanClass.getMethod("getUnderlying");
            return getUnderlying.invoke(eventBean);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not execute getEventType.", e);
        }
        return null;
    }

    public boolean isUnderlyingMap(){
        return getUnderlying() instanceof Map;
    }

}
