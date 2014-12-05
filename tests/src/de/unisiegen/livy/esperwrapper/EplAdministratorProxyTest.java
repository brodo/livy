package de.unisiegen.livy.esperwrapper;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.List;

public class EplAdministratorProxyTest extends AndroidTestCase {

    EplAdministratorProxy eplAdministratorProxy;

    public void setUp() throws Exception {
        super.setUp();
        AsperLoader loader = new AsperLoader(getContext());
        eplAdministratorProxy = loader.getEPAdministrator();
    }

    public void testGetStatementWhenNoneExists() throws Exception {
        EPStatementProxy statement = eplAdministratorProxy.getStatement("test");
        assertNull(statement);
    }

    public void testGetStatementNamesWhenNoneExists() throws Exception {
        String[] names = eplAdministratorProxy.getStatementNames();
        assertTrue(names.length == 0);
    }

    public void testCreateEPLShouldReturnAStatement() throws Exception {
        EPStatementProxy statementProxy = eplAdministratorProxy.createEPL("create schema Foo()");
        assertNotNull(statementProxy);
        assertEquals(statementProxy.getClass(), EPStatementProxy.class);
    }

    public void testCreateAndFindNamedEPL() throws Exception {
        EPStatementProxy s1 = eplAdministratorProxy.createEPL("create schema Foo()", "test");
        EPStatementProxy s2 = eplAdministratorProxy.getStatement("test");
        assertEquals(s1,s2);
    }

    public void testGetStatementNames() throws Exception {
        eplAdministratorProxy.createEPL("create schema Foo()", "test1");
        eplAdministratorProxy.createEPL("Select * from Foo", "test2");
        List<String> names = Arrays.asList(eplAdministratorProxy.getStatementNames());
        assertTrue(names.size() == 2);
        assertTrue(names.contains("test1"));
        assertTrue(names.contains("test2"));
    }

    public void testDestroyAllStatements() throws Exception {
        eplAdministratorProxy.createEPL("create schema Foo()");
        eplAdministratorProxy.createEPL("select * from Foo");
        assertTrue(eplAdministratorProxy.getStatementNames().length > 0);
        eplAdministratorProxy.destroyAllStatements();
        assertEquals(eplAdministratorProxy.getStatementNames().length, 0);

    }
}