package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MasterOneActivity extends BaseActivity {

    TextInputEditText txtName;
    Button btnSave,btnDelete;

    SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progress;
    String nsModule,nsId;
    //TextView lblMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_one);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(nsModule.equals("master_location")){
            toolbar.setTitle("Location");
        }else if(nsModule.equals("user_position")){
            toolbar.setTitle("User Position");
        }else if(nsModule.equals("master_brand")){
            toolbar.setTitle("Inventory Brand");
        }else if(nsModule.equals("master_units")){
            toolbar.setTitle("Inventory Units");
        }else if(nsModule.equals("master_area")){
            toolbar.setTitle("Area");
        }else if(nsModule.equals("master_city")){
            toolbar.setTitle("City");
        }else if(nsModule.equals("master_expense_head")){
            toolbar.setTitle("Expense Head");
        }
        setSupportActionBar(toolbar);

        init();

        if(Integer.parseInt(nsId)>0){
            btnSave.setText("Update");
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            btnDelete.setVisibility(View.VISIBLE);
            fetchDataById();
        }else{
            btnSave.setText("Save");
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if(txtName.getText().toString().trim().matches("")) {
                    txtName.setError("Required");
                    isValidated=false;
                }else if(nsModule.equals("master_location") && Integer.parseInt(nsId)>0 && nsId.equals(preferenceConfig.readLocationId())){
                    DialogUtility.showDialog(MasterOneActivity.this,"Your default location is "+preferenceConfig.readlocationName()+". Please Change your default location settings to edit.","Error");
                    isValidated=false;
                }
                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prName", txtName.getText().toString().toUpperCase().trim());
                    jsonArrObjItems.put(objItems);

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MasterOneActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void init() {
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        //lblMessage = (TextView)findViewById(R.id.lblMessage);
    }

    private void fetchDataById(){
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        //DialogUtility.showDialog(MasterOneActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(MasterOneActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);
                                    txtName.setText(objData.getString("nsName"));
                                }
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(MasterOneActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(MasterOneActivity.this,"Error: Please Check your Internet Connection","Alert");
                }else {
                    Log.e("Rakesh", "VolleyError: " + error.getMessage());
                }
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCaption",preferenceConfig.readCompanyCaption());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", nsModule);
                params.put("functionality", "fetch_table_data");
                params.put("id",nsId);

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

        requestQueue.add(stringRequest);
    }
}
