package de.unisiegen.livy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Julian Dax on 10/04/14.
 */
public class LivyAlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        EventUtils.wakeUpService(context);
    }
}
