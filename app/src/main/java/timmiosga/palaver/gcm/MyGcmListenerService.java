package timmiosga.palaver.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import timmiosga.palaver.ChatActivity;
import timmiosga.palaver.FriendsList;

/**
 * Created by timmiosga on 13.07.17.
 */

public class MyGcmListenerService extends GcmListenerService {


    @Override
    public void onMessageReceived(String from, Bundle data) {





    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(data.getString("sender"))
            .setContentText(data.getString("preview"));

    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNotifyMgr.notify(0, mBuilder.build());

    update(this,data.getString("preview"),data.getString("sender"));




}
    static void update(Context context, String message, String sender) {

        Intent intent = new Intent("updateintent");

        intent.putExtra("sender", sender);
        intent.putExtra("message", message);


        context.sendBroadcast(intent);
    }

    }

