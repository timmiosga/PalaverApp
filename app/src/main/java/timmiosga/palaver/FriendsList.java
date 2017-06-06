package timmiosga.palaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

//TODO BUILT THIS WITH FRAGMENTS!
public class FriendsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        Toast.makeText(this, "Welcome, "+this.getIntent().getExtras().getString("username")+". You were successfully logged in.",Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        getAndListAllFriends(this.getIntent().getExtras().getString("username"),this.getIntent().getExtras().getString("password"));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.signout) {
            Toast.makeText(this, "You will be signed out...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
            return true;
        }
        if (id == R.id.AddFriend) {

            return true;
        }
        if (id == R.id.DeleteFriend) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAndListAllFriends(String username, String password) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.Friends_URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Username", username);
            jsonBody.put("Password", password);
            final String requestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);


                    String[] items = response.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").replaceAll("\"","").split(",");

                    String[] results = new String[items.length];

                    for (int i = 0; i < items.length; i++) {
                        try {
                            results[i] = (items[i]);

                        } catch (NumberFormatException nfe) {
                            //NOTE: write something here if you need to recover from formatting errors
                        };
                    }

                    Log.d("this is my array", "arr: " + results[0]);
                    final ListView lv = (ListView) findViewById(R.id.friendslist);


                    // Create a List from String Array elements
                    List<String> fruits_list = new ArrayList<String>(Arrays.asList(results));

                    // Create a ArrayAdapter from List
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (FriendsList.this, android.R.layout.simple_list_item_1, fruits_list);

                    // Populate ListView with items from ArrayAdapter
                    lv.setAdapter(arrayAdapter);

                    // Set an item click listener for ListView
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Get the selected item text from ListView
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            Log.i("Selected: ",selectedItem);

                        }
                    });


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
}
