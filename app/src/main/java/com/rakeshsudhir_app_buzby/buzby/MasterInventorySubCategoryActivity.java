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

public class MasterInventorySubCategoryActivity extends BaseActivity {

    private AppCompatAutoCompleteTextView txtInventorySubCategory,txtInventoryCategory;
    private Button btnSave,btnDelete;
    private TextView lblMessage;
    private ImageView imgAddInventoryCategory;

    private ProgressDialog progress;
    private String nsModule="",nsId="",category_id="",nsCategory="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_inventory_subcategory);

        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        txtInventorySubCategory = (AppCompatAutoCompleteTextView)findViewById(R.id.txtInventorySubCategory);
        txtInventoryCategory = (AppCompatAutoCompleteTextView)findViewById(R.id.txtInventoryCategory);
        imgAddInventoryCategory = (ImageView) findViewById(R.id.imgAddInventoryCategory);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        lblMessage = (TextView) findViewById(R.id.lblMessage);

        imgAddInventoryCategory.setVisibility(View.GONE);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");

        imgAddInventoryCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterInventorySubCategoryActivity.this, ListActivity.class);
                i.putExtra("nsModule","master_inventory_category");
                startActivityForResult(i,1);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Inventory SubCategory");
        setSupportActionBar(toolbar);

        fetchDataTypeahead();

        if(Integer.parseInt(nsId)>0){
            displayData();
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setText("Update");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtInventoryCategory.hasFocus()){
                    txtInventoryCategory.clearFocus();
                }

                boolean isValidated = true;

                if(txtInventorySubCategory.getText().toString().trim().matches("")) {
                    txtInventorySubCategory.setError("Required");
                    isValidated=false;
                }else if(txtInventoryCategory.getText().toString().trim().equals("") || category_id.equals("")){
                    txtInventoryCategory.setError("Required");
                    isValidated=false;
                }
                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prInventoryCategoryId", category_id);
                    objItems.put("prName", txtInventorySubCategory.getText().toString().toUpperCase().trim());
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
                Intent i = new Intent(MasterInventorySubCategoryActivity.this,DashboardActivity.class);
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
                                DialogUtility.showDialog(MasterInventorySubCategoryActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);
                                    txtInventorySubCategory.setText(objData.getString("nsName"));
                                    txtInventoryCategory.setText(objData.getString("nsInventoryCategory"));
                                    category_id=objData.getString("nsCategoryId");
                                }

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(MasterInventorySubCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(MasterInventorySubCategoryActivity.this,"Error: Please Check your Internet Connection","Alert");
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
                                DialogUtility.showDialog(MasterInventorySubCategoryActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("dataCategory");

                                final String[] strArrayName ;
                                strArrayName = new String[jsonData.length()];

                                for (int i = 0; i < jsonData.length(); i++) {
                                    JSONObject objDataName = jsonData.getJSONObject(i);
                                    strArrayName[i] = objDataName.getString("nsName");
                                }
                                ArrayAdapter<String> adapterCustomerName = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayName);
                                txtInventoryCategory.setAdapter(adapterCustomerName);
                                txtInventoryCategory.setThreshold(1);

                                txtInventoryCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //setCustomerArray((String)parent.getItemAtPosition(position));

                                        String selection =(String)parent.getItemAtPosition(position);
                                        for(int i = 0; i<strArrayName.length; i++){
                                            if(strArrayName[i].equals(selection)){
                                                try {
                                                    JSONObject objDataCategory = jsonData.getJSONObject(i);
                                                    category_id =objDataCategory.getString("nsId");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                break;
                                            }
                                        }
                                        //txtAndroidID.requestFocus();
                                    }
                                });



                                txtInventoryCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {
                                            imgAddInventoryCategory.setVisibility(View.GONE);
                                            String str = txtInventoryCategory.getText().toString();
                                            ListAdapter listAdapter = txtInventoryCategory.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtInventoryCategory.getText().toString().trim().matches("") || str.compareTo(temp) == 0) {
                                                    return;
                                                }
                                            }
                                            txtInventoryCategory.setError("Invalid");

                                        }else{
                                            imgAddInventoryCategory.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });


                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(MasterInventorySubCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(MasterInventorySubCategoryActivity.this,"Error: Please Check your Internet Connection","Alert");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                nsCategory = data.getStringExtra("nsName");
                category_id = data.getStringExtra("nsId");
                txtInventoryCategory.setText(nsCategory);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchDataTypeahead();

    }
}
