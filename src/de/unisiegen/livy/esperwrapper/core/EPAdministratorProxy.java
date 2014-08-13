package de.unisiegen.livy.esperwrapper.core;

import java.lang.reflect.Method;

/**
 * Created by Julian Dax on 13/08/14.
 */
public class EPAdministratorProxy {
    private final Object epAdministrator;
    private final Class epAdministratorClass;

    public EPAdministratorProxy(Object epAdministrator, Class epAdministratorClass) {
        this.epAdministrator = epAdministrator;
        this.epAdministratorClass = epAdministratorClass;
    }


    public EPStatementProxy getStatement(String name) {
        try {
            Method getStatement = epAdministratorClass.getMethod("getStatement", String.class);
            Object result =  getStatement.invoke(epAdministrator, name);
            return new EPStatementProxy(result, result.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getStatementNames() {
        try {
            Method getStatementNames = epAdministratorClass.getMethod("getStatementNames");
            Object result =  getStatementNames.invoke(epAdministrator);
            return (String[])result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public EPStatementProxy createEPL(String epl) {
        try {
            Method getStatementNames = epAdministratorClass.getMethod("createEPL", String.class);
            Object result =  getStatementNames.invoke(epAdministrator, epl);
            return new EPStatementProxy(result, result.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public EPStatementProxy createEPL(String epl, String name) {
        try {
            Method getStatementNames = epAdministratorClass.getMethod("createEPL", String.class, String.class);
            Object result =  getStatementNames.invoke(epAdministrator, epl, name);
            return new EPStatementProxy(result, result.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
