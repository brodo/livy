package de.unisiegen.livy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class EventServiceTest extends ServiceTestCase<EventService> {

    public EventServiceTest() {
        super(EventService.class);
    }

    public void testFindingComplexEvents() throws Exception {
        doQuery("create schema TestSchema(test string)");
        doQuery("select * from TestSchema", 1, 1);

        final CountDownLatch signal = new CountDownLatch(1);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Serializable[] newEvents = (Serializable[]) intent.getSerializableExtra("newEvents");
                Map<String,String> event = (Map<String, String>) newEvents[0];
                assertEquals(event.get("test"), "lala");
                signal.countDown();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(EventService.ACTION_COMPLEX_EVENT);
        getContext().registerReceiver(receiver, filter);

        HashMap<String,String> event = new HashMap<String,String>();
        event.put("test", "lala");
        sendEvent(event, "TestSchema");

        signal.await();

    }


    public void testFindingComplexEventsTwice() throws Exception {
        doQuery("create schema TestSchema(test string)");
        doQuery("select * from TestSchema", 1, 1);

        final CountDownLatch signal = new CountDownLatch(2);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                signal.countDown();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(EventService.ACTION_COMPLEX_EVENT);
        getContext().registerReceiver(receiver, filter);

        HashMap<String,String> event = new HashMap<String,String>();
        event.put("test", "lala");
        String schemaName = "TestSchema";
        sendEvent(event, schemaName);
        sendEvent(event, "TestSchema");
        signal.await();

    }

    public void testFindPoi() throws Exception {
        doQuery("create schema GeoProbe(location_longitude double, location_latitude double, location_altitude double, location_speed double, location_action string, timestamp double)");
        doQuery("select * from [GeoProbe()");


    }

    private void sendEvent(HashMap<String, String> event, String schemaName) {
        startService(Livy.makeIntentWithEventMap(event, schemaName, getContext()));
    }



    private void doQuery(String query, int queryId, int surveyId) {
        startService(Livy.makeIntentWithEplQueryAndSurveyId(query, queryId, surveyId, getContext()));
    }

    private void doQuery(String query) {
        startService(Livy.makeIntentWithEplQuery(query, getContext()));
    }

}