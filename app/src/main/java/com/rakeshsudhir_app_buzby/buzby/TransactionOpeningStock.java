package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class TransactionOpeningStock extends BaseActivity {

    private AppCompatAutoCompleteTextView txtLocation,txtInventory,txtQuantity;
    private Button btnSave,btnDelete;
    private TextView lblMessage;
    private ImageView imgAddLocation,imgAddInventory;

    private ProgressDialog progress;
    private String nsModule="",nsId="",inventory_id="",inventory_name="",location_id="",location_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_opening_stock);

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        txtLocation = (AppCompatAutoCompleteTextView) findViewById(R.id.txtLocation);
        txtInventory = (AppCompatAutoCompleteTextView) findViewById(R.id.txtInventory);
        txtQuantity = (AppCompatAutoCompleteTextView) findViewById(R.id.txtQuantity);
        imgAddLocation = (ImageView) findViewById(R.id.imgAddLocation);
        imgAddInventory = (ImageView) findViewById(R.id.imgAddInventory);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        lblMessage = (TextView) findViewById(R.id.lblMessage);

        imgAddLocation.setVisibility(View.GONE);
        txtInventory.requestFocus();

        nsModule = getIntent().getStringExtra("nsModule");
        nsId = getIntent().getStringExtra("nsId");

        if (Integer.parseInt(nsId) < 0 && !preferenceConfig.readLocationId().equals("") && !preferenceConfig.readUserName().equals("")) {
            txtLocation.setText(preferenceConfig.readlocationName());
            location_id = preferenceConfig.readLocationId();
        }

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionOpeningStock.this, ListActivity.class);
                i.putExtra("nsModule", "master_location");
                i.putExtra("nsSubModule", "returnValue");
                startActivityForResult(i, 1);
            }
        });

        /*imgAddInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionOpeningStock.this, ListActivity.class);
                i.putExtra("nsModule","master_inventory");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,2);
            }
        });*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (nsModule.equals("transaction_openingstock")){
            toolbar.setTitle("Opening Stock");
        }else if(nsModule.equals("inventory_saftey_stock")){
            toolbar.setTitle("Saftey Stock");
        }
        setSupportActionBar(toolbar);

        displayDataForAutoEditText();

        if(Integer.parseInt(nsId)>0){
            displayData();
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setText("Update");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtLocation.hasFocus()){
                    txtLocation.clearFocus();
                }

                boolean isValidated = true;

                if(txtLocation.getText().toString().trim().equals("") || location_id.equals("")){
                    txtLocation.setError("Required");
                    isValidated=false;
                }

                if(txtInventory.getText().toString().trim().equals("") || inventory_id.equals("")){
                    txtInventory.setError("Required");
                    isValidated=false;
                }

                if(txtQuantity.getText().toString().trim().equals("")){
                    txtQuantity.setError("Required");
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prLocationId", location_id);
                    objItems.put("prInventoryId", inventory_id);
                    if (nsModule.equals("transaction_openingstock")) {
                        objItems.put("prOpeningQuantity", txtQuantity.getText().toString().trim());
                    }else if (nsModule.equals("inventory_saftey_stock")){
                        objItems.put("prQuantity", txtQuantity.getText().toString().trim());
                    }
                    jsonArrObjItems.put(objItems);

                    //DialogUtility.showDialog(TransactionOpeningStock.this,jsonArrObjItems.toString(4),"");
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
                Intent i = new Intent(TransactionOpeningStock.this,DashboardActivity.class);
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
                        //DialogUtility.showDialog(TransactionOpeningStock.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(TransactionOpeningStock.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);
                                    txtLocation.setText(objData.getString("nsLocation"));
                                    location_id = objData.getString("nsLocationId");
                                    txtInventory.setText(objData.getString("nsName"));
                                    inventory_id = objData.getString("nsInventoryId");
                                    if (nsModule.equals("transaction_openingstock")){
                                        txtQuantity.setText(objData.getString("nsOpeningQuantity"));
                                    }else if(nsModule.equals("inventory_saftey_stock")){
                                        txtQuantity.setText(objData.getString("nsQuantity"));
                                    }
                                }
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(TransactionOpeningStock.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse==null){
                    DialogUtility.showDialog(TransactionOpeningStock.this,"Error: Please Check your Internet Connection","Alert");
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

    private void displayDataForAutoEditText(/*final String locId*/) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);

        if(Integer.parseInt(nsId)<1) {
            progress.setMessage("data Loading Data...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(SalesTransactionActivity.this,response,"");
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
                                txtInventory.setAdapter(adapterInvName);
                                txtInventory.setThreshold(1);

                                txtInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                        txtInventory.requestFocus();
                                    }
                                });

                                txtInventory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {

                                            String str = txtInventory.getText().toString();

                                            ListAdapter listAdapter = txtInventory.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtInventory.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                    return;
                                                }
                                            }
                                            txtInventory.setError("Select correct Brand Name from Dropdown");
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

    /*private void fetchDataTypeahead(){
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("fetchdataTypeahead", response);
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(TransactionOpeningStock.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonDataLocation = jsonObject.getJSONArray("dataLocation");
                                final JSONArray jsonDataInventory = jsonObject.getJSONArray("dataInventory");

                                //Location
                                final String[] strArrayLocation ;
                                strArrayLocation = new String[jsonDataLocation.length()];
                                for (int i = 0; i < jsonDataLocation.length(); i++) {
                                    JSONObject objDataLocation = jsonDataLocation.getJSONObject(i);
                                    strArrayLocation[i] = objDataLocation.getString("nsName");
                                }
                                ArrayAdapter<String> adapterLocation = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayLocation);
                                txtLocation.setAdapter(adapterLocation);
                                txtLocation.setThreshold(1);

                                txtLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //setCustomerArray((String)parent.getItemAtPosition(position));

                                        String selection =(String)parent.getItemAtPosition(position);
                                        for(int i = 0; i<strArrayLocation.length; i++){
                                            if(strArrayLocation[i].equals(selection)){
                                                try {
                                                    JSONObject objDataLocation = jsonDataLocation.getJSONObject(i);
                                                    location_id =objDataLocation.getString("nsId");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                txtLocation.setError(null);
                                                break;
                                            }
                                        }
                                        txtInventory.requestFocus();
                                    }
                                });
                                txtLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {
                                            imgAddLocation.setVisibility(View.GONE);
                                            String str = txtLocation.getText().toString();
                                            ListAdapter listAdapter = txtLocation.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtLocation.getText().toString().trim().matches("") || str.compareTo(temp) == 0) {
                                                    return;
                                                }
                                            }
                                            txtLocation.setError("Invalid");

                                        }else{
                                            imgAddLocation.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });//Location Ends

                                //Inventory
                                final String[] strArrayInventory ;
                                strArrayInventory = new String[jsonDataInventory.length()];
                                for (int i = 0; i < jsonDataInventory.length(); i++) {
                                    JSONObject objDataInventory = jsonDataInventory.getJSONObject(i);
                                    strArrayInventory[i] = objDataInventory.getString("nsName");
                                }
                                ArrayAdapter<String> adapterInventory = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayInventory);
                                txtInventory.setAdapter(adapterInventory);
                                txtInventory.setThreshold(1);

                                txtInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //setCustomerArray((String)parent.getItemAtPosition(position));

                                        String selection =(String)parent.getItemAtPosition(position);
                                        for(int i = 0; i<strArrayInventory.length; i++){
                                            if(strArrayInventory[i].equals(selection)){
                                                try {
                                                    JSONObject objDataLocation = jsonDataInventory.getJSONObject(i);
                                                    inventory_id =objDataLocation.getString("nsId");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                txtInventory.setError(null);
                                                break;
                                            }
                                        }
                                        txtInventory.requestFocus();
                                    }
                                });
                                txtInventory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {
                                            imgAddInventory.setVisibility(View.GONE);
                                            String str = txtInventory.getText().toString();
                                            ListAdapter listAdapter = txtInventory.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtInventory.getText().toString().trim().matches("") || str.compareTo(temp) == 0) {
                                                    return;
                                                }
                                            }
                                            txtInventory.setError("Invalid");

                                        }else{
                                            imgAddInventory.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });//Inventory Ends


                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(TransactionOpeningStock.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse==null){
                    DialogUtility.showDialog(TransactionOpeningStock.this,"Error: Please Check your Internet Connection","Alert");
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
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                location_name = data.getStringExtra("nsName");
                location_id = data.getStringExtra("nsId");
                txtLocation.setText(location_name);
                txtLocation.setError(null);
                txtInventory.requestFocus();
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==2){
            if(resultCode==RESULT_OK) {
                inventory_name = data.getStringExtra("nsName");
                inventory_id = data.getStringExtra("nsId");
                txtInventory.setText(inventory_name);
                txtInventory.setError(null);
                txtQuantity.requestFocus();
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
