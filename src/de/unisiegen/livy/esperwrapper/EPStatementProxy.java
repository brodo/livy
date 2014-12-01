package de.unisiegen.livy.esperwrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Julian Dax on 01.12.14.
 */
public class EPStatementProxy {
    Object statement;
    Class statementClass;
    Class updateListenerClass;

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
            e.printStackTrace();
        }
        return null;
    }

    public void destroy(){
        try {
            Method destroy = statementClass.getMethod("destroy");
            destroy.invoke(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void  removeAllListeners(){
        try {
            Method removeAllListeners = statementClass.getMethod("removeAllListeners");
            removeAllListeners.invoke(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start(){
        try {
            Method start = statementClass.getMethod("start");
            start.invoke(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(final UpdateListener listener){
        try {
            Method addListener = statementClass.getMethod("addListener", updateListenerClass);
            Object updateListener = Proxy.newProxyInstance(statement.getClass().getClassLoader(),
                    new Class[]{updateListenerClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            listener.update((Object[])args[0], (Object[])args[1]);
                            return null;
                        }
                    });
            addListener.invoke(statement, updateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
