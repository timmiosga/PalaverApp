package timmiosga.palaver.gcm;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by timmiosga on 13.07.17.
 */

public class MyGcmListenerService extends GcmListenerService {
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

if (!settings.getString("actualFriend","").equals(data.getString("sender"))){
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(data.getString("sender"))
            .setContentText(data.getString("preview"));

    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNotifyMgr.notify(0, mBuilder.build());

}


    }
}
