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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
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

public class AndroidIDActivity extends BaseActivity {

    AppCompatAutoCompleteTextView txtName,txtAndroidID,txtDeviceName;
    Button btnSave,btnDelete;

    SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progress;
    String nsModule,nsId;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_id);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Unique ID");
        setSupportActionBar(toolbar);

        init();
        fetchDataTypeahead();
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
                }

                if(txtDeviceName.getText().toString().trim().matches("")) {
                    txtDeviceName.setError("Required");
                    isValidated=false;
                }
                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prUserId", user_id);
                    objItems.put("prAndroidID", txtAndroidID.getText().toString().trim());
                    objItems.put("prDeviceName", txtDeviceName.getText().toString().trim());
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
                Intent i = new Intent(AndroidIDActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void init() {
        txtName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtName);
        txtAndroidID = (AppCompatAutoCompleteTextView) findViewById(R.id.txtAndroidID);
        txtDeviceName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDeviceName);
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
                        //DialogUtility.showDialog(ExpenseHeadActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(AndroidIDActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);
                                    txtName.setText(objData.getString("nsName"));
                                    txtAndroidID.setText(objData.getString("nsAndroidID"));
                                    txtDeviceName.setText(objData.getString("nsDeviceName"));
                                    user_id=objData.getString("nsUserId");
                                }

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(AndroidIDActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(AndroidIDActivity.this,"Error: Please Check your Internet Connection","Alert");
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

    private void fetchDataTypeahead(){
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(AndroidIDActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");

                                final String[] strArrayName ;
                                strArrayName = new String[jsonData.length()];

                                for (int i = 0; i < jsonData.length(); i++) {
                                    JSONObject objDataName = jsonData.getJSONObject(i);
                                    strArrayName[i] = objDataName.getString("nsName");
                                }
                                ArrayAdapter<String> adapterCustomerName = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayName);
                                txtName.setAdapter(adapterCustomerName);
                                txtName.setThreshold(1);

                                txtName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //setCustomerArray((String)parent.getItemAtPosition(position));

                                        String selection =(String)parent.getItemAtPosition(position);
                                        for(int i = 0; i<strArrayName.length; i++){
                                            if(strArrayName[i].equals(selection)){
                                                try {
                                                    JSONObject objDataUserID = jsonData.getJSONObject(i);
                                                    user_id =objDataUserID.getString("nsId");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                break;
                                            }
                                        }
                                        //txtAndroidID.requestFocus();
                                    }
                                });



                                txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {
                                            String str = txtName.getText().toString();
                                            ListAdapter listAdapter = txtName.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtName.getText().toString().trim().matches("") || str.compareTo(temp) == 0) {
                                                    return;
                                                }
                                            }
                                            txtName.setError("Invalid");

                                        }
                                    }
                                });


                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(AndroidIDActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(AndroidIDActivity.this,"Error: Please Check your Internet Connection","Alert");
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
                params.put("functionality", "fetch_typeahead_textboxes_data");
                //params.put("id",nsId);

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
