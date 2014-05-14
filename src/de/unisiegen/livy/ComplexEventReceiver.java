package de.unisiegen.livy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Julian Dax on 24/03/14.
 * Starts surveys if a complex event occurred.
 */
public class ComplexEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int surveyId = intent.getIntExtra("survey", -1);
        if (surveyId != -1) {
            Log.d("ComplexEventReceiver", String.format("Survey No %d triggered!", surveyId));
        }
    }
}