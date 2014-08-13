package de.unisiegen.livy.esperwrapper.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Julian Dax on 13/08/14.
 */
public class EPStatementProxy {
    private final Object epStatement;
    private final Class epStatementClass;
    public EPStatementProxy(Object epStatement, Class epStatementClass) {
        this.epStatementClass = epStatementClass;
        this.epStatement = epStatement;
    }

    public void removeAllListeners() {
        try {
            Method removeAllListeners = epStatementClass.getMethod("removeAllListeners");
            removeAllListeners.invoke(epStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            Method destroy = epStatementClass.getMethod("destroy");
            destroy.invoke(epStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            Method start = epStatementClass.getMethod("start");
            start.invoke(epStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(final UpdateListenerProxy updateListenerProxy) {
        try {
            Class updateListenerClass =  Class.forName("com.espertech.esper.client.UpdateListener");
            Method addListener = epStatementClass.getMethod("addListener", updateListenerClass);

            Object updateListener = Proxy.newProxyInstance(epStatementClass.getClassLoader(),
                    new Class[]{updateListenerClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            updateListenerProxy.update();
                            return null;
                        }
                    });
            addListener.invoke(epStatement, updateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getText() {
        try {
            Method getText = epStatementClass.getMethod("getText");
            return (String) getText.invoke(epStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
