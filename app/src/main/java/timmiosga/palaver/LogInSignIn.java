package timmiosga.palaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogInSignIn extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_sign_in);


        final TextInputEditText username   = (TextInputEditText)findViewById(R.id.username);
        final TextInputEditText password   = (TextInputEditText)findViewById(R.id.password);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (!settings.getString("username","").equals("")){


            username.setText(settings.getString("username",""));
            password.setText(settings.getString("password",""));
            Login(settings.getString("username",""),settings.getString("password",""));

        }else{

            username.setText("");
            password.setText("");
        }





        Button login = (Button) findViewById(R.id.log_in_button);
        Button signup = (Button) findViewById(R.id.sign_up_button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkEditTextinCorrect(username,password)){
                    showError();
                }else{
                    showToast();


                    validateAndLoginSignUp(username.getText().toString(),password.getText().toString(),true);
                    // LogIn Process
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkEditTextinCorrect(username,password)){
                    showError();
                }else{

                    showToast();

                    validateAndLoginSignUp(username.getText().toString(),password.getText().toString(),false);

                    // SignUp Process
                }
            }
        });


    }

    private void showToast() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Toast.makeText(this, "You will be logged in...", Toast.LENGTH_SHORT).show();
    }

    private void showError() {
        AlertDialog alertDialog = new AlertDialog.Builder(LogInSignIn.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Please type in a username and password.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private boolean checkEditTextinCorrect(EditText username,EditText password) {
        if(username.getText().toString().trim().length() == 0||password.getText().toString().trim().length() == 0) {
            return true;
        }else{
            return false;
        }
    }



private void validateAndLoginSignUp(final String username, final String password, final boolean loginSignUp){

    try {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = getString(R.string.Validation_URL);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("Username", username);
        jsonBody.put("Password", password);
        final String requestBody = jsonBody.toString();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                if (loginSignUp) {
                    if (response.equals("0")) {

                        //Benutzer nicht vorhanden. Fehler ausgeben!

                        AlertDialog alertDialog = new AlertDialog.Builder(LogInSignIn.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Your username/password-combination is incorrect.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    } else {
//Benutzer ist vorhanden. Einloggen!
                        Login(username, password);

                    }

                }else{

                    if (response.equals("0")){

                        //Benutzer nicht vorhanden. Bei Paluno anmelden und einloggen!

                        SignupAndLogin(username,password);
                    }else{

//Benutzer ist bereits vorhanden. Fehler ausgeben!

                        AlertDialog alertDialog = new AlertDialog.Builder(LogInSignIn.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("The given username already exists. Please choose another one.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }

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










    private void SignupAndLogin(final String username, final String password) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.Register_URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Username", username);
            jsonBody.put("Password", password);
            final String requestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);

                        if (response.equals("1")) {

                            Login(username,password);


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

    private void Login(String username, String password) {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password",password);

        editor.commit();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start();

            }
        }, 2000);



    }

    private void start() {

        startActivity(new Intent(this, FriendsList.class));
        final TextInputEditText username   = (TextInputEditText)findViewById(R.id.username);
        final TextInputEditText password   = (TextInputEditText)findViewById(R.id.password);
        username.setText("");
        password.setText("");
    }

}


