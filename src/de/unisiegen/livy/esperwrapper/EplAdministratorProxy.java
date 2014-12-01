package de.unisiegen.livy.esperwrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian Dax on 30.11.14.
 */
public class EplAdministratorProxy {
    private Object administrator;
    private Class administratorClass;
    private Class updateListenerClass;

    public EplAdministratorProxy(Object administrator, Class updateListenerClass) {
        this.administrator = administrator;
        this.administratorClass = administrator.getClass();
        this.updateListenerClass = updateListenerClass;
    }


    public EPStatementProxy getStatement(String queryName){
        try {
            Method getStatement = administratorClass.getMethod("getStatement", String.class);
            return new EPStatementProxy(getStatement.invoke(administrator, queryName), updateListenerClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getStatementNames() {
        Method getStatementNames = null;
        try {
            getStatementNames = administratorClass.getMethod("getStatementNames");
            return (List<String>) getStatementNames.invoke(administrator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public EPStatementProxy createEPL(String epl){
        try {
            Method createEPL = administratorClass.getMethod("createEPL", String.class);
            return new EPStatementProxy(createEPL.invoke(administrator, epl), updateListenerClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public EPStatementProxy createEPL(String query, String name){
        try {
            Method createEPL = administratorClass.getMethod("createEPL", String.class, String.class);
            return new EPStatementProxy(createEPL.invoke(administrator, query, name), updateListenerClass);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
