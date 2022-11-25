package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockTransferActivity extends BaseActivity implements MyDataAdapter.myAdapterListener{

    AppCompatAutoCompleteTextView txtDate;
    AppCompatAutoCompleteTextView txtFromLocation;
    AppCompatAutoCompleteTextView txtToLocation;
    AppCompatAutoCompleteTextView txtAutoCompleteInventoryName;
    AppCompatAutoCompleteTextView txtAutoCompleteInventoryQuantity;

    Button btn_add_cart_details;
    Button btn_del_party_details;
    Button btn_save;
    Button btn_delete;

    LinearLayout tableCartItems;

    TextView accountingStockPhysicalStock;
    TextView lblTotalQty,lblVoucherNo;

    EditText txtRemarks;


    private DatePickerDialog datePickerDialog;
    private ProgressDialog progress;
    private String nsSubModule="0";
    private String nsModule="",nsId="",nsFromLocationId="",nsToLocationId="";
    String nsInventoryId;
    JSONArray jsonCartItems = new JSONArray();
    int n = -1;

    private ArrayList<String> myDataListColumns;
    private RecyclerView recyclerView;
    private Context context;
    private MyDataAdapter myDataAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);


        txtDate = (AppCompatAutoCompleteTextView)findViewById(R.id.txtDate);
        txtFromLocation = (AppCompatAutoCompleteTextView)findViewById(R.id.txtFromLocation);
        txtToLocation = (AppCompatAutoCompleteTextView)findViewById(R.id.txtToLocation);
        txtAutoCompleteInventoryName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventoryName);
        txtAutoCompleteInventoryQuantity = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventoryQuantity);

        btn_add_cart_details  = (Button)findViewById(R.id.btn_add_cart_details);
        btn_del_party_details  = (Button)findViewById(R.id.btn_del_party_details);
        btn_save  = (Button)findViewById(R.id.btn_save);
        btn_delete  = (Button)findViewById(R.id.btn_delete);

        tableCartItems  = (LinearLayout)findViewById(R.id.tableCartItems);

        accountingStockPhysicalStock = (TextView)findViewById(R.id.accountingStockPhysicalStock);
        lblTotalQty = (TextView)findViewById(R.id.lblTotalQty);
        lblVoucherNo = (TextView)findViewById(R.id.lblVoucherNo);

        txtRemarks  = (EditText)findViewById(R.id.txtRemarks);


        btn_del_party_details.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        nsModule = getIntent().getStringExtra("nsModule");
        Intent intent = getIntent();
        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }

        if(getIntent().hasExtra("nsSubModule")){
            nsSubModule = getIntent().getStringExtra("nsSubModule");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stock Transfer");
        setSupportActionBar(toolbar);
        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,StockTransferActivity.this,txtDate);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        LinearLayoutManager userManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(userManager4);
        myDataListColumns = new ArrayList<>();
        context=this;

        txtFromLocation.setText(preferenceConfig.readlocationName());
        nsFromLocationId = preferenceConfig.readLocationId();

        txtFromLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StockTransferActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,1);
            }
        });

        txtToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StockTransferActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,2);
            }
        });

        displayDataForAutoEditText();

        btn_add_cart_details.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if(txtFromLocation.getText().toString().trim().matches("")){
                    txtFromLocation.setError("Invalid");
                    isValidated=false;
                }else if(txtToLocation.getText().toString().trim().matches("")){
                    txtToLocation.setError("Invalid");
                    isValidated=false;
                }else if(txtAutoCompleteInventoryName.getText().toString().trim().matches("")){
                    txtAutoCompleteInventoryName.setError("Invalid");
                    isValidated=false;
                }else if(txtAutoCompleteInventoryQuantity.getText().toString().trim().matches("")){
                    txtAutoCompleteInventoryQuantity.setError("Invalid");
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }

                try {
                    JSONObject objProduct = new JSONObject();
                    objProduct.put("nsInventoryName", txtAutoCompleteInventoryName.getText().toString().trim());
                    objProduct.put("nsQuantity", txtAutoCompleteInventoryQuantity.getText().toString().trim());
                    objProduct.put("nsInventoryId", nsInventoryId);

                    if (n == -1) {

                        if(jsonCartItems.toString().contains("\"nsInventoryName\":\""+txtAutoCompleteInventoryName.getText().toString()+"\"")){
                            Toast.makeText(StockTransferActivity.this, "Duplicate Inventory Name", Toast.LENGTH_LONG).show();
                        }else{
                            objProduct.put("nsSelectedPosition", jsonCartItems.length());
                            jsonCartItems.put(objProduct);
                        }
                    }else{
                        objProduct.put("nsSelectedPosition", n);
                        jsonCartItems.put(n, objProduct);
                    }
                    myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonCartItems, (MyDataAdapter.myAdapterListener) context, "cart_items_stock_transfer","");
                    recyclerView.setAdapter(myDataAdapter);
                    n=-1;
                    tableCartItems.setVisibility(View.VISIBLE);

                    txtAutoCompleteInventoryName.setText("");
                    txtAutoCompleteInventoryQuantity.setText("");

                    calculateTotal();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockTransferActivity.this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                txtAutoCompleteInventoryName.requestFocus();

            }
        });

        btn_del_party_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonCartItems.remove(n);
                n=-1;
                for(int i=0;i<jsonCartItems.length();i++){
                    try {
                        //JSONObject objData = jsonPartyCartItems.getJSONObject(i);
                        JSONObject itemArr = (JSONObject)jsonCartItems.get(i);
                        itemArr.put("nsSelectedPosition", i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                calculateTotal();
                myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonCartItems, (MyDataAdapter.myAdapterListener) context, "cart_items_stock_transfer","");
                recyclerView.setAdapter(myDataAdapter);

                if(jsonCartItems.length()<1){
                    tableCartItems.setVisibility(View.GONE);
                }

                txtAutoCompleteInventoryName.setText("");
                txtAutoCompleteInventoryQuantity.setText("");
                nsInventoryId="";
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidated = true;

                if(jsonCartItems.length()==0){
                    DialogUtility.showDialog(StockTransferActivity.this,"CART CANNOT BE EMPTY!","ERROR");
                    isValidated = false;
                }
                if(isValidated==false){
                    return;
                }

                btn_save.setEnabled(false);
                btn_save.setAlpha(.5f);
                btn_save.setText("wait...");


                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();
                    Date d = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString());

                    objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(d));
                    objItems.put("prFromLocationId", nsFromLocationId);
                    objItems.put("prToLocationId", nsToLocationId);

                    objItems.put("prRemarks", txtRemarks.getText().toString().trim());
                    objItems.put("prCartItems", jsonCartItems.toString(4));
                    objItems.put("prTransactionTypeId", "1000");
                    objItems.put("prStatus", "1");

                    jsonArrObjItems.put(objItems);

                    //DialogUtility.showDialog(SalesTransactionActivity.this,jsonArrObjItems.toString(4),"Alert");

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(Integer.parseInt(nsId)>0){
            displayData();
            btn_delete.setVisibility(View.VISIBLE);
            btn_save.setText("Update");
            txtDate.setEnabled(false);
            txtDate.setAlpha(0.5f);
            txtDate.setClickable(false);
        }

        if(!preferenceConfig.readUserPosition().equals("1")){
            //dateLocationLayout.setVisibility(View.INVISIBLE);
            txtDate.setEnabled(false);
            //txt.setEnabled(false);
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StockTransferActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                                txtAutoCompleteInventoryName.setAdapter(adapterInvName);
                                txtAutoCompleteInventoryName.setThreshold(1);

                                txtAutoCompleteInventoryName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selection =(String)parent.getItemAtPosition(position);
                                        //Toast.makeText(Sales.this, "rererere "+jsonInvName, Toast.LENGTH_SHORT).show();
                                        for(int i = 0; i<strArrayInvName.length; i++){
                                            if(strArrayInvName[i].equals(selection)){
                                                try {
                                                    JSONObject objDataBrand = jsonInvName.getJSONObject(i);

                                                    nsInventoryId = objDataBrand.getString("nsId");

                                                    String nsAccountingStock="",nsPhysicalStock="";
                                                    nsAccountingStock = objDataBrand.getString("nsAccountingStock");
                                                    nsPhysicalStock = objDataBrand.getString("nsPhysicalStock");
                                                    accountingStockPhysicalStock.setText(nsAccountingStock+"/"+nsPhysicalStock);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            }
                                        }
                                        txtAutoCompleteInventoryQuantity.requestFocus();
                                    }
                                });

                                txtAutoCompleteInventoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {

                                            String str = txtAutoCompleteInventoryName.getText().toString();

                                            ListAdapter listAdapter = txtAutoCompleteInventoryName.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtAutoCompleteInventoryName.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                    return;
                                                }
                                            }
                                            txtAutoCompleteInventoryName.setError("Select correct Brand Name from Dropdown");
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
                Date d = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString().trim());
                params.put("prDateFrom",new SimpleDateFormat("yyyy-MM-dd").format(d));
                params.put("prLocationId",nsFromLocationId);

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

    private void calculateTotal() {
        double totalQty = 0.0;
        try{
            for (int i = 0; i < jsonCartItems.length(); i++) {
                totalQty += jsonCartItems.getJSONObject(i).getDouble("nsQuantity");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lblTotalQty.setText(String.valueOf(totalQty));
    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {
        try {

            Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
            btn_add_cart_details.setText("Update");
            btn_del_party_details.setVisibility(View.VISIBLE);
            txtAutoCompleteInventoryName.setText(item.getString("nsInventoryName"));
            txtAutoCompleteInventoryQuantity.setText(item.getString("nsQuantity"));
            n=position;
            nsInventoryId=item.getString("nsInventoryId");
            txtAutoCompleteInventoryQuantity.requestFocus();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }

    private void displayData() {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(StockTransferActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                DialogUtility.showDialog(StockTransferActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                JSONArray jsonDataItems = jsonObject.getJSONArray("dataItems");

                                final JSONObject objData = jsonData.getJSONObject(0);
                                //DialogUtility.showDialog(Sales.this,"GB Display data bal"+jsonDataItems.toString(4),"");

                                if(jsonData.length()>0) {
                                    lblVoucherNo.setText("E.No. "+objData.getString("nsVoucherNo"));
                                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                                    txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                                    txtFromLocation.setText(objData.getString("nsFromLocation"));
                                    nsFromLocationId = objData.getString("nsFromLocationId");
                                    txtToLocation.setText(objData.getString("nsToLocation"));
                                    nsToLocationId = objData.getString("nsToLocationId");
                                    txtRemarks.setText(objData.getString("nsRemarks"));
                                }

                                tableCartItems.setVisibility(View.VISIBLE);

                                for (int i=0; i<jsonDataItems.length();i++){
                                    JSONObject objDataItem = jsonDataItems.getJSONObject(i);
                                    JSONObject objProductItems = new JSONObject();
                                    objProductItems.put("nsInventoryName", objDataItem.getString("nsInventoryName"));
                                    objProductItems.put("nsQuantity", objDataItem.getString("nsQuantity"));
                                    objProductItems.put("nsInventoryId", objDataItem.getString("nsInventoryId"));

                                    jsonCartItems.put(objProductItems);
                                }

                                for(int i=0;i<jsonCartItems.length();i++){
                                    try {
                                        //JSONObject objData = jsonPartyCartItems.getJSONObject(i);
                                        JSONObject itemArr = (JSONObject)jsonCartItems.get(i);
                                        itemArr.put("nsSelectedPosition", i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                calculateTotal();
                                myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonCartItems, (MyDataAdapter.myAdapterListener) context, "cart_items_stock_transfer","");
                                recyclerView.setAdapter(myDataAdapter);

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            //lblMessage.setText(e.getMessage());
                            Toast.makeText(StockTransferActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText(error.getMessage());
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
                params.put("functionality", "fetch_table_data_for_invoice");
                params.put("id",nsId);
                Log.d("RRakeshSSudhir",params.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                nsFromLocationId = data.getStringExtra("nsId");
                txtFromLocation.setText(data.getStringExtra("nsName"));
                txtFromLocation.setError(null);
            }

            displayDataForAutoEditText();

            if (requestCode == RESULT_CANCELED) {

            }
        }else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                nsToLocationId = data.getStringExtra("nsId");
                txtToLocation.setText(data.getStringExtra("nsName"));
                txtToLocation.setError(null);
            }
            if (requestCode == RESULT_CANCELED) {

            }
        }

    }
}