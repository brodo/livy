package de.unisiegen.livy.esperwrapper;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Julian Dax on 30.11.14.
 */
public class EplAdministratorProxy {
    private Object administrator;
    private Class administratorClass;
    private Class updateListenerClass;
    private static String LOG_TAG = "Livy/EplAdministratorProxy";

    public EplAdministratorProxy(Object administrator, Class updateListenerClass) {
        this.administrator = administrator;
        this.administratorClass = administrator.getClass();
        this.updateListenerClass = updateListenerClass;
    }


    public EPStatementProxy getStatement(String statementName){
        try {
            Method getStatement = administratorClass.getMethod("getStatement", String.class);
            Object statement = getStatement.invoke(administrator, statementName);
            if(statement == null) return null;
            return new EPStatementProxy(statement, updateListenerClass);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not get statement by name", e);
        }
        return null;
    }

    public String[] getStatementNames() {
        try {
            Method getStatementNames = administratorClass.getMethod("getStatementNames");
            return (String[]) getStatementNames.invoke(administrator);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not get statement names", e);
        }
        return new String[]{};
    }

    public EPStatementProxy createEPL(String epl){
        try {
            Method createEPL = administratorClass.getMethod("createEPL", String.class);
            return new EPStatementProxy(createEPL.invoke(administrator, epl), updateListenerClass);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not create EPL", e);
        }
        return null;
    }

    public EPStatementProxy createEPL(String query, String name){
        try {
            Method createEPL = administratorClass.getMethod("createEPL", String.class, String.class);
            return new EPStatementProxy(createEPL.invoke(administrator, query, name), updateListenerClass);
        } catch (Exception e){
            Log.e(LOG_TAG, "Could not create EPL", e);
        }
        return null;
    }

    public void destroyAllStatements(){
        try {
            Method destroyAllStatements = administratorClass.getMethod("destroyAllStatements");
            destroyAllStatements.invoke(administrator);
        } catch (Exception e){
            Log.e(LOG_TAG, "Could not destroy all EPL statements", e);
        }

    }

}
