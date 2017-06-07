package timmiosga.palaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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

import static android.R.attr.id;
import static android.R.id.list;

//TODO BUILT THIS WITH FRAGMENTS!
//TODO PUSH WITH GCM SO THAT MESSAGES ARE BEING ALSO PUSHED IN THE CHATACTIVITY (AND IF YOU CLICK ON THE NOTIFICATION FROM OUTSIDE THAT THE APP DIRECTLY GOES TO THE RIGHT CONVERSATION)
//TODO PALAVER ICON
//TODO SQLITE DATABSE FOR BETTER PERFORMANCE (WITH REQUEST AT TIMES)

public class FriendsList extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private List<String> friends_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        Toast.makeText(this, "Welcome, "+settings.getString("username","")+". You were successfully logged in.",Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        getAndListAllFriends(settings.getString("username",""),settings.getString("password",""));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.signout) {
            Toast.makeText(this, "You will be logged out...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.commit();

                    finish();
                }
            }, 2000);
            return true;
        }
        if (id == R.id.AddFriend) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            addFriend(settings.getString("username",""),settings.getString("password",""));

            return true;
        }
        if (id == R.id.DeleteFriend) {

            RelativeLayout linearLayout = new RelativeLayout(this);
            final NumberPicker aNumberPicker = new NumberPicker(this);
            aNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            aNumberPicker.setWrapSelectorWheel(false);
            aNumberPicker.setMaxValue(friends_list.size());
            aNumberPicker.setMinValue(1);

            String[] arr = new String[friends_list.size()];
            arr = friends_list.toArray(arr);
            aNumberPicker.setDisplayedValues(arr);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
            RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            linearLayout.setLayoutParams(params);
            linearLayout.addView(aNumberPicker,numPicerParams);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Select a friend");
            alertDialogBuilder.setView(linearLayout);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    deleteFriendAndRefresh(aNumberPicker.getValue()-1);


                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteFriendAndRefresh(int value) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        try {

            System.out.println("FRIEND TO DELETE: "+friends_list.get(value));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.DeleteAFriend_URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Username", settings.getString("username",""));
            jsonBody.put("Password", settings.getString("password",""));
            jsonBody.put("Friend", friends_list.get(value));

            final String requestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

                    getAndListAllFriends(settings.getString("username",""),settings.getString("password",""));




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
                            responseString=result.getString("Info");



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

                        };
                    }


                    final ListView lv = (ListView) findViewById(R.id.friendslist);


                    if (response.equals("[]")){

                        results = new String[1];
                        results[0]="Add a friend by clicking on the menu button.";
                    }
                    friends_list = new ArrayList<String>(Arrays.asList(results));


                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (FriendsList.this, android.R.layout.simple_list_item_1, friends_list);


                    lv.setAdapter(arrayAdapter);


                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String selectedItem = (String) parent.getItemAtPosition(position);

                            if (!selectedItem.equals("Add a friend by clicking on the menu button.")) {
                                gotoChatActivity(selectedItem);
                            }



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




    private void addFriend(final String username, final String password) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Friend");
        builder.setMessage("Please enter your friend's username.");


        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


               boolean notavailable=true;
                for(String str: friends_list) {
                    if(str.contains(input.getText().toString()))
                        notavailable=false;
                    break;
                }
                if (!input.getText().toString().equals(username)&&notavailable) {
                    addFriendandRefresh(input.getText().toString(), username, password);
                }if (!input.getText().toString().equals(username)&&notavailable==false){
                    dialog.cancel();
                    AlertDialog alertDialog = new AlertDialog.Builder(FriendsList.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("This friend is already on your list.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } if(input.getText().toString().equals(username)){

                    AlertDialog alertDialog = new AlertDialog.Builder(FriendsList.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("You entered your own username. You do not need a chat app to talk to yourself!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void addFriendandRefresh(String friend, String username, String password) {


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.AddAFriend_URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Username", username);
            jsonBody.put("Password", password);
            jsonBody.put("Friend", friend);

            final String requestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    if (response.equals("0")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(FriendsList.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("The given username does not exist. Your friend has not been added.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }else{
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

                        getAndListAllFriends(settings.getString("username",""),settings.getString("password",""));


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


    private void gotoChatActivity(String friend) {


        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend", friend);

        startActivity(intent);

        FriendsList.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
