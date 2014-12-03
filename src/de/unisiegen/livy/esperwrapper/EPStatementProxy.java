package de.unisiegen.livy.esperwrapper;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian Dax on 01.12.14.
 */
public class EPStatementProxy {
    Object statement;
    Class statementClass;
    Class updateListenerClass;
    private static final String LOG_TAG = "Livy/EPStatementProxy";

    public EPStatementProxy(Object statement, Class updateListenerClass) {
        this.statement = statement;
        this.statementClass = statement.getClass();
        this.updateListenerClass = updateListenerClass;
    }

    public String getText(){
        try {
            Method getText = statementClass.getMethod("getText");
            return (String) getText.invoke(statement);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not get statment text", e);
        }
        return null;
    }

    public void destroy(){
        try {
            Method destroy = statementClass.getMethod("destroy");
            destroy.invoke(statement);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not destroy statement", e);
        }
    }

    public void  removeAllListeners(){
        try {
            Method removeAllListeners = statementClass.getMethod("removeAllListeners");
            removeAllListeners.invoke(statement);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not remove listeners", e);
        }

    }

    public void start(){
        try {
            Method start = statementClass.getMethod("start");
            start.invoke(statement);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not start statement", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EPStatementProxy that = (EPStatementProxy) o;

        if (statement != null ? !statement.equals(that.statement) : that.statement != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return statement != null ? statement.hashCode() : 0;
    }

    public void addListener(final UpdateListener listener){
        try {
            Method addListener = statementClass.getMethod("addListener", updateListenerClass);
            Object updateListener = Proxy.newProxyInstance(statement.getClass().getClassLoader(),
                    new Class[]{updateListenerClass}, new ComplexEventHandler(listener));
            addListener.invoke(statement, updateListener);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not add listener to statement", e);
        }
    }

    private List<EventBeanProxy> objectsToEventBeanProxies(Object[] events){
        ArrayList<EventBeanProxy> result = new ArrayList<EventBeanProxy>();
        if(events == null) return result;
        for(Object event :  events){
            result.add(new EventBeanProxy(event));
        }
        return result;
    }


    private class ComplexEventHandler implements InvocationHandler {
        private final UpdateListener listener;

        public ComplexEventHandler(UpdateListener listener) {
            this.listener = listener;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object[] newEvents = {};
            Object[] oldEvents = {};
            if(args.length > 0) newEvents = (Object[])args[0];
            if(args.length > 1) oldEvents = (Object[])args[1];
            listener.update(objectsToEventBeanProxies(newEvents), objectsToEventBeanProxies(oldEvents));
            return null;
        }
    }
}
