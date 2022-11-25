package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClosingStockActivity extends BaseActivity {

    AppCompatAutoCompleteTextView txtCurrentDate, txtAutoCompleteLocation, txtAutoCompleteInventoeyName,
            txtAutoCompleteInventoryQuantity,txtAutoCompleteStockType;
    TextInputEditText txtRemarks;
    Button btnDelete,btnSave;
    TextView lblMessage;

    private SharedPreferenceConfig preferenceConfig;

    String[] strArrayLocation,strArrayInvName,strArrayStockType;
    JSONArray jsonLocation,jsonInvName;

    private String nsModule="",stockTypeId="",nsId="",inventory_id="",
            inventory_name="",location_id="0",location_name="",nsStockType="",nsSubModule="";

    DatePickerDialog picker;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing_stock);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());


        progress=new ProgressDialog(this);
        progress.setMessage("data Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Daily Stock");
        setSupportActionBar(toolbar);


        nsModule = getIntent().getStringExtra("nsModule");
        Intent intent = getIntent();
        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }if (intent.hasExtra("nsSubModule")) {
            nsSubModule=getIntent().getStringExtra("nsSubModule");
        }



        txtCurrentDate = (AppCompatAutoCompleteTextView)findViewById(R.id.txtCurrentDate);
        txtAutoCompleteLocation = (AppCompatAutoCompleteTextView)findViewById(R.id.txtLocation);
        txtAutoCompleteInventoeyName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventoeyName);
        txtAutoCompleteInventoryQuantity = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventoryQuantity);
        txtAutoCompleteStockType = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteStockType);
        //TextInputEditText
        txtRemarks = (TextInputEditText)findViewById(R.id.txtRemarks);
        //Button
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnSave = (Button)findViewById(R.id.btnSave);
        //TextView
        lblMessage = (TextView)findViewById(R.id.lblMessage);

        final Calendar cldr1 = Calendar.getInstance();
        txtCurrentDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtCurrentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(picker,ClosingStockActivity.this,txtCurrentDate);
            }
        });

        location_id = preferenceConfig.readLocationId();
        txtAutoCompleteLocation.setText(preferenceConfig.readlocationName());
        displayDataForAutoEditText();
        txtAutoCompleteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClosingStockActivity.this, ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,1);
            }
        });

        if (intent.hasExtra("nsStockType")) {
            nsStockType=getIntent().getStringExtra("nsStockType");
            txtAutoCompleteStockType.setText(nsStockType);
            stockTypeId = getIntent().getStringExtra("nsStockTypeId");
        }


        if (intent.hasExtra("nsQuantity")) {
            txtAutoCompleteInventoryQuantity.setText(getIntent().getStringExtra("nsQuantity"));
            txtAutoCompleteInventoryQuantity.requestFocus();
        }

        if (intent.hasExtra("nsInventoryId")) {
            inventory_id=getIntent().getStringExtra("nsInventoryId");
        }

        if (intent.hasExtra("nsInventory")) {
            txtAutoCompleteInventoeyName.setText(getIntent().getStringExtra("nsInventory"));
        }



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if(txtAutoCompleteLocation.getText().toString().trim().equals("") || location_id.equals("")){
                    txtAutoCompleteLocation.setError("Invalid Entry");
                    isValidated = false;
                }else if(txtAutoCompleteInventoeyName.getText().toString().trim().equals("") || inventory_id.equals("")){
                    txtAutoCompleteInventoeyName.setError("Invalid Entry");
                    isValidated = false;
                }else if(txtAutoCompleteInventoryQuantity.getText().toString().trim().equals("")){
                    txtAutoCompleteInventoryQuantity.setError("Invalid Entry");
                    isValidated = false;
                }else if(txtAutoCompleteStockType.getText().toString().trim().equals("") || stockTypeId.equals("")){
                    txtAutoCompleteStockType.setError("Invalid Entry");
                    isValidated = false;
                }

                if(isValidated==false){
                    return;
                }

                /*btnSave.setEnabled(false);
                btnSave.setAlpha(.5f);
                btnSave.setText("wait...");*/
                Date dateDatabaseformat = ConvertFunctions.convertIndianDateStringToDate(txtCurrentDate.getText().toString());

                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(dateDatabaseformat));
                    objItems.put("prLocationId", location_id);
                    objItems.put("prInventoryId", inventory_id);
                    objItems.put("prQuantity", txtAutoCompleteInventoryQuantity.getText().toString().trim());
                    objItems.put("prTypeId", stockTypeId);
                    objItems.put("prStockType", txtAutoCompleteStockType.getText().toString());
                    objItems.put("prRemarks", txtRemarks.getText().toString().trim());
                    jsonArrObjItems.put(objItems);

                    //DialogUtility.showDialog(ClosingStockActivity.this,jsonArrObjItems.toString(4),"");
                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,nsSubModule,2,2,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        strArrayStockType = new String[2];

        strArrayStockType[0] = "Opening Stock";
        strArrayStockType[1] = "Closing Stock";
        ArrayAdapter<String> adapterStockType = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayStockType);
        txtAutoCompleteStockType.setAdapter(adapterStockType);
        txtAutoCompleteStockType.setThreshold(1);

        txtAutoCompleteStockType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection =(String)parent.getItemAtPosition(position);
                //Toast.makeText(Sales.this, "rererere "+jsonInvName, Toast.LENGTH_SHORT).show();

                if(strArrayStockType[0].equals(selection)){
                    stockTypeId = "1";
                }else {
                    stockTypeId="2";
                }

            }
        });

        if (intent.hasExtra("strArrayStockType")) {
            txtAutoCompleteStockType.setText(strArrayStockType[Integer.parseInt(getIntent().getStringExtra("strArrayStockType"))]);
            if(getIntent().getStringExtra("strArrayStockType").equals("0")){
                stockTypeId = "1";
            }else {
                stockTypeId="2";
            }

        }

        txtAutoCompleteStockType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {

                    String str = txtAutoCompleteStockType.getText().toString();

                    ListAdapter listAdapter = txtAutoCompleteStockType.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(txtAutoCompleteStockType.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                            return;
                        }
                    }

                    txtAutoCompleteInventoeyName.setError("Invalid Entry");

                }
            }
        });

        if(Integer.parseInt(nsId)>0){
            btnSave.setText("Update");
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            btnDelete.setVisibility(View.VISIBLE);
            displayData();
        }else{
            btnSave.setText("Save");
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        }

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
                Intent i = new Intent(ClosingStockActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        if(!preferenceConfig.readUserPosition().equals("1")){
            //dateLocationLayout.setVisibility(View.INVISIBLE);
            txtCurrentDate.setEnabled(false);
            txtAutoCompleteLocation.setEnabled(false);
        }

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void displayData() {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        /*progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);*/
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(Sales.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");



                                if(jsonData.length()>0) {

                                    JSONObject objData = jsonData.getJSONObject(0);

                                    String curDate = objData.getString("nsDate");
                                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(curDate);
                                    txtCurrentDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));

                                    txtAutoCompleteLocation.setText(objData.getString("nsLocationName"));
                                    location_id = objData.getString("nsLocationId");
                                    txtAutoCompleteInventoeyName.setText(objData.getString("nsBrandName"));
                                    inventory_id = objData.getString("nsInventoryId");
                                    stockTypeId = objData.getString("nsTypeId");
                                    if(stockTypeId.equals("1")){
                                        txtAutoCompleteStockType.setText(strArrayStockType[0]);
                                        //stockTypeId="1";
                                    }else{
                                        txtAutoCompleteStockType.setText(strArrayStockType[1]);
                                        //stockTypeId="2";
                                    }

                                    txtAutoCompleteInventoryQuantity.setText(objData.getString("nsQuantity"));
                                    txtRemarks.setText(objData.getString("nsRemarks"));
                                }
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
                //params.put("format", "individual");
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
        //swipeRefreshLayout.setRefreshing(false);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void displayDataForAutoEditText(/*final String locId*/) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);

        //if(Integer.parseInt(nsId)<1) {
            /*progress.setMessage("data Loading Data...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);*/
            progress.show();
        //}
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(ClosingStockActivity.this,response,"");
                        // lblMessage.setText(response);
                        progress.dismiss();
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);

                                final JSONArray jsonInvName = jsonObject.getJSONArray("dataInventory");

                                final String[] strArrayInvName = new String[jsonInvName.length()];
                                for (int i = 0; i < jsonInvName.length(); i++) {
                                    JSONObject objData = jsonInvName.getJSONObject(i);
                                    strArrayInvName[i] = objData.getString("nsBrand");
                                }

                                ArrayAdapter<String> adapterInvName = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayInvName);
                                txtAutoCompleteInventoeyName.setAdapter(adapterInvName);
                                txtAutoCompleteInventoeyName.setThreshold(1);

                                txtAutoCompleteInventoeyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selection =(String)parent.getItemAtPosition(position);
                                        //Toast.makeText(Sales.this, "rererere "+jsonInvName, Toast.LENGTH_SHORT).show();
                                        for(int i = 0; i<strArrayInvName.length; i++){
                                            if(strArrayInvName[i].equals(selection)){
                                                try {
                                                    JSONObject objDataBrand = jsonInvName.getJSONObject(i);
                                                    inventory_id = objDataBrand.getString("nsId");

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            }
                                        }
                                        txtAutoCompleteInventoryQuantity.requestFocus();
                                    }
                                });

                                txtAutoCompleteInventoeyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {

                                            String str = txtAutoCompleteInventoeyName.getText().toString();

                                            ListAdapter listAdapter = txtAutoCompleteInventoeyName.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtAutoCompleteInventoeyName.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                    return;
                                                }
                                            }
                                            txtAutoCompleteInventoeyName.setError("Select correct Brand Name from Dropdown");
                                        }
                                    }
                                });

                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            //lblMessage.setText("auto ed 1"+e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText("auto ed 2"+error.getMessage());
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
                params.put("module", nsModule);
                params.put("functionality", "fetch_typeahead_textboxes_data");
                //params.put("format", "list");
                //params.put("prLocationId",locId);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                location_name = data.getStringExtra("nsName");
                location_id = data.getStringExtra("nsId");
                txtAutoCompleteLocation.setText(location_name);
                txtAutoCompleteLocation.setError(null);
                txtAutoCompleteLocation.requestFocus();
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayDataForAutoEditText();
    }
}