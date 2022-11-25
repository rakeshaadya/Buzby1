package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class MasterModules extends BaseActivity {

    private AppCompatAutoCompleteTextView txtModule,txtModuleAlias;
    private Button btnSave,btnDelete;
    private TextView lblMessage;


    private ProgressDialog progress;
    private String nsModule="",nsId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_modules);

        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        txtModule = (AppCompatAutoCompleteTextView)findViewById(R.id.txtModule);
        txtModuleAlias = (AppCompatAutoCompleteTextView)findViewById(R.id.txtModuleAlias);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        lblMessage = (TextView) findViewById(R.id.lblMessage);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Module");
        setSupportActionBar(toolbar);

        if(Integer.parseInt(nsId)>0){
            displayData();
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setText("Update");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtModule.hasFocus()){
                    txtModule.clearFocus();
                }

                boolean isValidated = true;

                if(txtModule.getText().toString().trim().matches("")) {
                    txtModule.setError("Required");
                    isValidated=false;
                }else if(txtModuleAlias.getText().toString().trim().equals("")){
                    txtModuleAlias.setError("Required");
                    isValidated=false;
                }
                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prName", txtModule.getText().toString().trim());
                    objItems.put("prNameAlias", txtModuleAlias.getText().toString().trim());
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
                Intent i = new Intent(MasterModules.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void displayData(){
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        //DialogUtility.showDialog(ExpenseHeadActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(MasterModules.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);
                                    txtModule.setText(objData.getString("nsName"));
                                    txtModuleAlias.setText(objData.getString("nsNameAlias"));
                                }

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(MasterModules.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(MasterInventorySubCategoryActivity.this,"Error: Please Check your Internet Connection","Alert");
                }else {*/
                    Log.e("Rakesh", "VolleyError: " + error.getMessage());
                //}
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
