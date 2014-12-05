package de.unisiegen.livy.esperwrapper;

import android.test.AndroidTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class EPRuntimeProxyTest extends AndroidTestCase {

    EplAdministratorProxy administratorProxy;
    EPRuntimeProxy runtimeProxy;

    public void setUp() throws Exception {
        super.setUp();
        AsperLoader loader = new AsperLoader(getContext());
        administratorProxy = loader.getEPAdministrator();
        runtimeProxy = loader.getEPRuntime();

    }

    public void testSendEventSimple() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        administratorProxy.createEPL("create schema Test(name string)");
        EPStatementProxy statementProxy = administratorProxy.createEPL("select * from Test");
        statementProxy.addListener(new UpdateListener() {
            @Override
            public void update(List<EventBeanProxy> newEvents, List<EventBeanProxy> oldEvents) {
                assertEquals(newEvents.size(), 1);
                assertEquals(newEvents.get(0).get("name"), "TEST");
                signal.countDown();
            }
        });
        statementProxy.start();
        HashMap<String, String> event = new HashMap<String,String>();
        event.put("name", "TEST");
        runtimeProxy.sendEvent(event, "Test");
        signal.await();
    }

    public void testReceiveEventTwice() throws Exception {
        final CountDownLatch signal = new CountDownLatch(2);
        administratorProxy.createEPL("create schema Test(name string)");
        EPStatementProxy statementProxy = administratorProxy.createEPL("select * from Test");
        statementProxy.addListener(new UpdateListener() {
            @Override
            public void update(List<EventBeanProxy> newEvents, List<EventBeanProxy> oldEvents) {
                assertEquals(newEvents.size(), 1);
                assertEquals(newEvents.get(0).get("name"), "TEST");
                signal.countDown();
            }
        });
        statementProxy.start();
        HashMap<String, String> event = new HashMap<String,String>();
        event.put("name", "TEST");
        runtimeProxy.sendEvent(event, "Test");
        runtimeProxy.sendEvent(event, "Test");
        signal.await();


    }
}