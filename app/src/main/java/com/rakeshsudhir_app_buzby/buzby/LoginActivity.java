package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView androidId;

    private ProgressDialog progressBar;

    SharedPreferenceConfig preferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        init();//Initialise Values

        //**Hide Status Bar**
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);//**Hide Status Bar ends**

        //Disable Focus on EditText and hides KeyPads
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        androidId.setText(Settings.Secure.getString(LoginActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID));

        if (preferenceConfig.readLoginStatus()) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidated = true;

                if(txtUsername.getText().toString().trim().matches("")) {
                    txtUsername.setError("Required");
                    isValidated=false;
                }else if(txtUsername.getText().toString().lastIndexOf("@")==-1) {
                    DialogUtility.showDialog(LoginActivity.this,"Error: Invalid Credentials","Alert");
                    isValidated=false;
                }
                if(txtPassword.getText().toString().trim().matches("")) {
                    txtUsername.setError("Required");
                    isValidated=false;
                }
                if(isValidated==false){
                    return;
                }
                validateLogin(txtUsername.getText().toString(), txtPassword.getText().toString());
                //Toast.makeText(LoginActivity.this, "Hello", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void init(){
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        androidId = (TextView) findViewById(R.id.androidId);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    private void validateLogin(final String userName, final String userPassword) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        //String JSON_URL = "http://192.168.1.101:8080/android_buzby/nAjax/cf.php";
        //Toast.makeText(this, ""+JSON_URL, Toast.LENGTH_SHORT).show();
        progressBar=new ProgressDialog(this);
        progressBar.setMessage("wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);
        progressBar.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Rakesh", "onResponse: "+response);
                        if(response.length()>=5 && response.substring(0,5).equals("Error")){
                            DialogUtility.showDialog(LoginActivity.this,response,"Alert");
                            progressBar.dismiss();
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonLogin = jsonObject.getJSONArray("login");

                                if (jsonLogin.length() == 0) {
                                    DialogUtility.showDialog(LoginActivity.this,"Error: Invalid Credentials","Alert");
                                    progressBar.dismiss();
                                    return;
                                }else {
                                    JSONObject objLogin = jsonLogin.getJSONObject(0);
                                    progressBar.dismiss();

                                    final Calendar cldr1 = Calendar.getInstance();

                                    preferenceConfig.writeLoginStatus(true,
                                            objLogin.getString("nsId"),
                                            objLogin.getString("nsName"),
                                            objLogin.getString("nsUserPosition"),
                                            objLogin.getString("nsCompany"),
                                            objLogin.getString("nsCompanyCaption"),
                                            objLogin.getString("AndroidUQ"),
                                            new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));

                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    //progressBar.dismiss();
                                    finish();
                                    //Log.d("Rakesh", "onResponse: User Logged in successfully");
                                }
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(LoginActivity.this,"Error: Please Check your Internet Connection","Alert");
                }else {*/
                    Log.e("Rakesh", "VolleyError: " + error.getMessage());
               // }
                progressBar.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("module", "login");
                params.put("functionality", "check_credentials");

                String user = userName.substring(0,userName.lastIndexOf("@"));
                String company = userName.substring(userName.lastIndexOf("@")+1);

                params.put("txtCompany", company);
                params.put("txtUsername", user);
                params.put("txtPassword", userPassword);
                params.put("AndroidID", Settings.Secure.getString(LoginActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID));
                Log.d("LoginActivityParams123", "getParams: "+params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("abc", "value");
                return headers;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
