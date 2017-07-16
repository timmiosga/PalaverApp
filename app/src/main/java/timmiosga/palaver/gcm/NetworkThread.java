package timmiosga.palaver.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import timmiosga.palaver.R;

/**
 * Created by timmiosga on 13.07.17.
 */

public class NetworkThread extends Thread {
    public static final String PREFS_NAME = "MyPrefsFile";
    private Context context;

    public NetworkThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {


        InstanceID instanceID = InstanceID.getInstance(context);

        try {
            String token = instanceID.getToken("594324547505", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


            SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("token", token);


            editor.commit();


        } catch (IOException e) {
            e.printStackTrace();
        }




    }



}