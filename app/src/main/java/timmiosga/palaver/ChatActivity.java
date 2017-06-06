package timmiosga.palaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    ListView messagelist;
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

        updateList();


    }

    private void updateList() {

        String friend = getIntent().getExtras().getString("friend");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.AddAFriend_URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Username", settings.getString("username",""));
            jsonBody.put("Password", settings.getString("password",""));
            jsonBody.put("Recipient", friend);

            final String requestBody = jsonBody.toString();
            System.out.println("REQUEST"+requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);




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
                           System.out.println("RESULT"+result);
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







        messagelist = (ListView) findViewById(R.id.messagelist);
        messagelist.setAdapter(new CustomAdapter(this, new String[] { "tim",
                "max","moritz"}, new String[]{"hallo","nochmal hallo","nochmal nochmal hallo"},new Date[]{new Date(),new Date(),new Date()}));


    }


}


class CustomAdapter extends BaseAdapter {

    Context context;
    String[] senderOrRecipient;
    String[] data;
    Date[]dates;
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
        text.setText(data[position]);
        user.setText(senderOrRecipient[position]);
        time.setText(dates[position].toString());
        return vi;
    }
}