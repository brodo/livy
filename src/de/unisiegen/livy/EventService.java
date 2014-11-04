package de.unisiegen.livy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import de.unisiegen.livy.esperwrapper.core.AsperLoader;
import de.unisiegen.livy.esperwrapper.core.EsperWrapper;
import de.unisiegen.livy.esperwrapper.core.IComplexEventListener;

import java.util.HashMap;


/**
 * Created by Julian Dax on 24/03/14.
 * This service receives events, queries and survey ids and puts them into Esper. When a complex event happens,
 * it sends an intent with the "de.unisiegen.livy.COMPLEX_EVENT" action.
 */
public class EventService extends Service {
    public static String DBG_TAG = "EventService";
    public static final String ACTION_COMPLEX_EVENT = "de.unisiegen.livy.COMPLEX_EVENT";
    public static final String ACTION_CEP_SERVICE = "de.unisiegen.livy.EventService";
    public static final int ADD_EVENT = 0;
    public static final int ADD_EVENT_MAP = 1;
    public static final int REGISTER_SURVEY = 2;
    public static final int MAKE_EPL_QUERY = 3;
    public static final int MAKE_EPL_QUERY_AND_TRIGGER_SURVEY = 4;
    public static final int DELETE_SURVEY = 5;
    public static final int DELETE_QUERY = 6;
    public static final int DELETE_ALL_QUERIES_BESIDES = 7;
    public static final int DELETE_ALL_SURVEYS_BESIDES = 8;
    public static final int WAKE_UP = 9;
    public EsperWrapper esperWrapper;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            switch (intent.getIntExtra("command", -1)) {
                case ADD_EVENT:
                    esperWrapper.eventHappened(intent.getParcelableExtra("event"));
                    break;
                case ADD_EVENT_MAP:
                        esperWrapper.eventHappened((HashMap)intent.getSerializableExtra("event"), intent.getStringExtra("name"));
                        break;
                case REGISTER_SURVEY:
                    Log.d(DBG_TAG, "Survey Registered:");
                    Log.d(DBG_TAG, String.valueOf(getSurvey(intent)));
                    esperWrapper.registerQuestionnaire(getSurveyName(intent), new IComplexEventListener() {
                        @Override
                        public void eventOccurred() {
                            Log.d(DBG_TAG, "A Complex event happened");
                            Intent sendingIntent = new Intent(ACTION_COMPLEX_EVENT);
                            sendingIntent.putExtra("survey", getSurvey(intent));
                            sendBroadcast(sendingIntent);
                        }
                    });
                    break;
                case MAKE_EPL_QUERY:
                    esperWrapper.doEplQuery(getQuery(intent));
                    break;
                case MAKE_EPL_QUERY_AND_TRIGGER_SURVEY:
                    Log.d(DBG_TAG, "EPL Registered for survey:");
                    Log.d(DBG_TAG, getQuery(intent));
                    Log.d(DBG_TAG, getSurveyName(intent));
                    esperWrapper.triggerQuestionnaire(getSurveyName(intent))
                            .onQuery(getQuery(intent), intent.getIntExtra("id", -1));
                    break;
                case DELETE_SURVEY:
                    esperWrapper.unregisterQuestionnaire(Integer.valueOf(getSurvey(intent)).toString());
                    break;
                case DELETE_QUERY:
                    esperWrapper.removeQuery(getQueryId(intent));
                    break;
                case DELETE_ALL_QUERIES_BESIDES:
                    String[] queries = intent.getStringArrayExtra("queries");
                    esperWrapper.removeAllQueriesBesides(queries);
                    break;
                case DELETE_ALL_SURVEYS_BESIDES:
                    String[] surveyIds = intent.getStringArrayExtra("surveys");
                    esperWrapper.removeAllSurveysBesides(surveyIds);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DBG_TAG, "Livy Service Created");
        esperWrapper =  new EsperWrapper(new AsperLoader(this));
        IntentFilter  filter = new IntentFilter();
        filter.addAction(EventService.ACTION_CEP_SERVICE);
        registerReceiver(receiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private String getSurveyName(Intent intent) {
        return Integer.toString(intent.getIntExtra("survey", -1));
    }

    private String getQuery(Intent intent) { return intent.getStringExtra("query");}
    private int getQueryId(Intent intent){ return getIntFromIntent(intent, "queryId");}
    private int getSurvey(Intent intent) { return  getIntFromIntent(intent, "survey");}
    private int getIntFromIntent(Intent intent, String name) {
        return intent.getIntExtra(name, -1);
    }


}