package timmiosga.palaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";


    ListView messagelist;
    String friend;
    String[] sender;
    String[] data;
    Date[] dates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
    @Override
    protected void onStart()
    {

        // TODO Auto-generated method stub
        super.onStart();




        friend = getIntent().getExtras().getString("friend");
        setTitle(friend);

        FloatingActionButton myFab = (FloatingActionButton)  this.findViewById(R.id.fab);
        final EditText textview = (EditText)this.findViewById(R.id.input);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendText(textview.getText().toString(),friend,"text/plain");
               textview.setText("");

            }
        });

        updateList();


    }

    private void sendText(String message, String friend, String mimetype) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.SendMessage_URL);
            JSONObject jsonBody = new JSONObject();

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);



            jsonBody.put("Username", settings.getString("username",""));
            jsonBody.put("Password", settings.getString("password",""));
            jsonBody.put("Recipient", friend);
            jsonBody.put("Mimetype", mimetype);
            jsonBody.put("Data", message);

            final String requestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                     Log.i("VOLLEY", response);

                    updateList();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        try {
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject result = new JSONObject(json);
                            responseString=result.getString("MsgType");



                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }



                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateList() {





        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.GetConversation_URL);
            JSONObject jsonBody = new JSONObject();
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);



            jsonBody.put("Username", settings.getString("username",""));
            jsonBody.put("Password", settings.getString("password",""));
            jsonBody.put("Recipient", friend);

            final String requestBody = jsonBody.toString();
          //  System.out.println("REQUEST"+requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   // Log.i("VOLLEY", response);

                    try {
                        JSONArray rootArray = new JSONArray(response);
                        //System.out.println("ROOT ARRAY: "+rootArray);
                       sender = new String [rootArray.length()];
                        data = new String [rootArray.length()];
                       dates = new Date [rootArray.length()];

                        for (int i=0;i<rootArray.length();i++){

                            JSONObject jsonMessage = rootArray.getJSONObject(i);
                            data[i] = jsonMessage.getString("Data");
                            sender[i] = jsonMessage.getString("Sender");

                            String dateStr = jsonMessage.getString("DateTime");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

                            dates[i] =  sdf.parse(dateStr);




                        }
                        setAdapter();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        try {
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject result = new JSONObject(json);
                            responseString=result.getString("Data");



                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }



                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }









    }

    private void setAdapter() {
        messagelist = (ListView) findViewById(R.id.messagelist);
        messagelist.setAdapter(new CustomAdapter(this,sender,data,dates));
    }


}


class CustomAdapter extends BaseAdapter {

    Context context;
    String[] senderOrRecipient;
    String[] data;
    Date[] dates;

    public static final String PREFS_NAME = "MyPrefsFile";
    private static LayoutInflater inflater = null;

    public CustomAdapter(Context context, String[] senderOrRecipient, String[] data, Date[]dates) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.senderOrRecipient = senderOrRecipient;
        this.dates=dates;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.listitem, null);
        TextView user = (TextView) vi.findViewById(R.id.message_user);
        TextView time = (TextView) vi.findViewById(R.id.message_time);
        TextView text = (TextView) vi.findViewById(R.id.message_text);



        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);


        if (senderOrRecipient[position].equals(settings.getString("username",""))) {
            user.setText("Me:");
        }else{
            user.setText(senderOrRecipient[position]+":");
        }
        text.setText(data[position]);


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.GERMANY);
        time.setText(sdf.format(dates[position]).toString());

        return vi;
    }
}