package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MasterUserActivity extends BaseActivity {

    private AppCompatAutoCompleteTextView txtUsername,txtOldPassword,txtNewPassword,txtConfirmPassword,txtUserPosition;
    private Button btnSave,btnDelete;
    private TextView lblMessage;
    private CheckBox checkboxIsActive;
    private TextInputLayout layoutOldPassword;

    private SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progress;
    private String nsModule="",nsId="";
    private String user_position_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_user);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Users Master");
        setSupportActionBar(toolbar);

        progress=new ProgressDialog(this);
        progress.setMessage("Loading data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");

        init();

        displayDataForAutoEditText(nsModule);
        lblMessage.setText("");
        if(Integer.parseInt(nsId)>0){
            displayData(nsId);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtUserPosition.clearFocus();
                boolean isValidated = true;

                if(txtUsername.getText().toString().trim().matches("")) {
                    txtUsername.setError("Invalid");
                    isValidated=false;
                }

                if(txtUserPosition.getText().toString().trim().matches("") || user_position_id.equals("")) {
                    txtUserPosition.setError("Invalid");
                    isValidated=false;
                }

                if(Integer.parseInt(nsId)>0){
                    if(!txtOldPassword.getText().toString().trim().matches("")
                            && !txtNewPassword.getText().toString().trim().matches("")
                            && !txtConfirmPassword.getText().toString().trim().matches("")) {

                        if(!txtNewPassword.getText().toString().trim().equals(txtConfirmPassword.getText().toString().trim())){
                            DialogUtility.showDialog(MasterUserActivity.this,"Password Mismatch","Alert");
                            isValidated = false;
                        }

                    }else if(!txtOldPassword.getText().toString().trim().matches("")
                            || !txtNewPassword.getText().toString().trim().matches("")
                            || !txtConfirmPassword.getText().toString().trim().matches("")){
                        if(txtOldPassword.getText().toString().trim().matches("")) {
                            txtOldPassword.setError("Invalid");
                            isValidated=false;
                        }if(txtNewPassword.getText().toString().trim().matches("") || txtNewPassword.getText().toString().contains(" ")) {
                            txtNewPassword.setError("Invalid");
                            isValidated=false;
                        }if(txtConfirmPassword.getText().toString().trim().matches("")) {
                            txtConfirmPassword.setError("Invalid");
                            isValidated=false;
                        }
                    }
                }else {

                    if (txtNewPassword.getText().toString().trim().matches("") || txtNewPassword.getText().toString().contains(" ")) {
                        txtNewPassword.setError("Invalid");
                        isValidated = false;
                    }else if (txtConfirmPassword.getText().toString().trim().matches("")) {
                        txtConfirmPassword.setError("Invalid");
                        isValidated = false;
                    }else if (!txtNewPassword.getText().toString().trim().equals(txtConfirmPassword.getText().toString().trim())) {
                        DialogUtility.showDialog(MasterUserActivity.this, "Password Mismatch", "Alert");
                        isValidated = false;
                    }
                }

                if (txtNewPassword.getText().toString().contains(" ")) {
                    txtNewPassword.setError("No Spaces Allowed");
                    isValidated = false;
                }

                if(isValidated==false){
                    return;
                }

                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prName", txtUsername.getText().toString().trim());
                    objItems.put("prUserPoitionId",user_position_id);

                    if(!txtOldPassword.getText().toString().trim().equals("")
                            || !txtNewPassword.getText().toString().trim().equals("")){
                        objItems.put("prOldPassword",txtOldPassword.getText().toString().trim());
                        objItems.put("prNewPassword",txtNewPassword.getText().toString().trim());
                    }
                    
                    if(checkboxIsActive.isChecked()){
                        objItems.put("prIsActive","1");
                    }else {
                        objItems.put("prIsActive","2");
                    }

                    jsonArrObjItems.put(objItems);

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MasterUserActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });

    }
    public void init() {
        txtUsername = (AppCompatAutoCompleteTextView) findViewById(R.id.txtUsername);
        txtUserPosition = (AppCompatAutoCompleteTextView) findViewById(R.id.txtUserPosition);
        txtOldPassword = (AppCompatAutoCompleteTextView) findViewById(R.id.txtOldPassword);
        txtNewPassword = (AppCompatAutoCompleteTextView) findViewById(R.id.txtNewPassword);
        txtConfirmPassword = (AppCompatAutoCompleteTextView) findViewById(R.id.txtConfirmPassword);
        layoutOldPassword = (TextInputLayout) findViewById(R.id.layoutOldPassword);
        checkboxIsActive = (CheckBox)findViewById(R.id.checkboxIsActive);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        lblMessage = (TextView)findViewById(R.id.lblMessage);
    }

    private void displayDataForAutoEditText(final String mModule) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonUsers = jsonObject.getJSONArray("dataUsersPosition");

                                final String[] strArrayUserPosition = new String[jsonUsers.length()];

                                for (int i = 0; i < jsonUsers.length(); i++) {
                                    JSONObject objData = jsonUsers.getJSONObject(i);
                                    strArrayUserPosition[i]=objData.getString("nsName");
                                }

                                ArrayAdapter<String> adapterUserPosition = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayUserPosition);
                                txtUserPosition.setAdapter(adapterUserPosition);
                                txtUserPosition.setThreshold(1);

                                txtUserPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selection = (String) parent.getItemAtPosition(position);

                                        for (int i = 0; i<strArrayUserPosition.length; i++) {
                                            if (strArrayUserPosition[i].equals(selection)) {
                                                try {
                                                    JSONObject objDataUserName = jsonUsers.getJSONObject(i);
                                                    user_position_id = objDataUserName.getString("nsId");

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                break;
                                            }
                                        }

                                    }
                                });

                                txtUserPosition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {

                                            String str = txtUserPosition.getText().toString();

                                            ListAdapter listAdapter = txtUserPosition.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtUserPosition.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                    return;
                                                }
                                            }
                                            user_position_id="";
                                            txtUserPosition.setError("Invalid");
                                        }
                                    }
                                });

                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            lblMessage.setText("auto ed 1"+e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lblMessage.setText("auto ed 2"+error.getMessage());
                progress.dismiss();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCaption",preferenceConfig.readCompanyCaption());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", mModule);
                params.put("functionality", "fetch_typeahead_textboxes_data");
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


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void displayData(final String id) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                JSONObject objData = jsonData.getJSONObject(0);

                                txtUsername.setText(objData.getString("nsName"));
                                txtUserPosition.setText(objData.getString("nsUserPosition"));
                                user_position_id=objData.getString("nsUserPositionId");

                                if(objData.getInt("nsIsActiveId")==1){
                                    checkboxIsActive.setChecked(true);
                                }else{
                                    checkboxIsActive.setChecked(false);
                                }
                                layoutOldPassword.setVisibility(View.VISIBLE);
                                btnDelete.setVisibility(View.VISIBLE);

                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            lblMessage.setText(e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lblMessage.setText(error.getMessage());
                progress.dismiss();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCaption",preferenceConfig.readCompanyCaption());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", "master_users");
                params.put("functionality", "fetch_table_data");
                params.put("id", id);

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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }
}
