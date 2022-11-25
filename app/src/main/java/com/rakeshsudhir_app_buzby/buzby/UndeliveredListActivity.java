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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UndeliveredListActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, MyDataAdapter.myAdapterListener {

    private String nsModule="offset_items";
    TextView lblMessage;
    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private Context context;
    String nsId="",shop_location_id="",nsSalesLocation="",nsCustomerDetails="",
            nsSalesLocationId="",nsSalesDate="",nsVoucherNo="",nsRemarks="",nsTransportOrder=" ";

    ProgressDialog progress;
    private DatePickerDialog datePickerDialog;

    CheckBox checkboxNOGP,checkboxNightStockDeduction;

    AppCompatAutoCompleteTextView txtLocation,txtDate;
    EditText txtRemarks;
    Button btnSave,btnRePrint;

    TextView lblDeliveredItems,lblCustomerDetails;
    LinearLayout dateLocationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undelivered_list);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Undelivered Items");
        setSupportActionBar(toolbar);

        itemInitialise();

        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        txtLocation.setText(preferenceConfig.readlocationName());
        shop_location_id=preferenceConfig.readLocationId();

        nsId=getIntent().getStringExtra("nsId");
        //Toast.makeText(this, ""+nsId, Toast.LENGTH_SHORT).show();
        nsModule=getIntent().getStringExtra("nsModule");
        nsSalesDate=getIntent().getStringExtra("nsSalesDate");
        nsVoucherNo=getIntent().getStringExtra("nsVoucherNo");
        nsSalesLocationId=getIntent().getStringExtra("nsSalesLocationId");
        nsSalesLocation=getIntent().getStringExtra("nsSalesLocationName");
        nsRemarks=getIntent().getStringExtra("nsRemarks");
        nsTransportOrder=getIntent().getStringExtra("nsTransportOrder");
        nsCustomerDetails="Name : "+getIntent().getStringExtra("nsLedgerName")+"\n"+"City : "+getIntent().getStringExtra("nsCity");
        Date d1 = ConvertFunctions.convertDatabaseDateStringToDate(nsSalesDate);
        lblCustomerDetails.setText("No : "+nsVoucherNo+"\n"+nsCustomerDetails+"\n"+"Sales Location : "+nsSalesLocation+"\nSales Date : "+new SimpleDateFormat("dd-MM-yyy").format(d1)+"\nSales Remarks : "+nsRemarks);

        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,UndeliveredListActivity.this,txtDate);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(UndeliveredListActivity.this,myDataListColumns, myDataList,this,nsModule,"");

        displayData();

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UndeliveredListActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,1);
            }
        });

        if(!preferenceConfig.readUserPosition().equals("1")){
            txtDate.setEnabled(false);
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UndeliveredListActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //vehicleNo = data.getStringExtra("nsRegistrationNumber");
                shop_location_id = data.getStringExtra("nsId");

                txtLocation.setText(data.getStringExtra("nsName"));
                txtLocation.setError(null);

            }
            if (requestCode == RESULT_CANCELED) {

            }
        }

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
                        //DialogUtility.showDialog(UndeliveredListActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(UndeliveredListActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");
                                final JSONArray dataDeliveredHistory = jsonObject.getJSONArray("dataDeliveredHistory");

                                if(jsonData.length()>0){
                                    int totalQty=0;
                                    int totalPendingQty=0;
                                    for(int i = 0;i<jsonData.length();i++) {
                                        JSONObject objData = jsonData.getJSONObject(i);
                                        totalQty+=objData.getInt("nsOriginValue");
                                        totalPendingQty+=objData.getInt("nsBalance");
                                    }

                                    TextView txtQty = (TextView)findViewById(R.id.txtQty);
                                    txtQty.setText(""+totalQty);
                                    TextView lblTotalPending = (TextView)findViewById(R.id.lblTotalPending);
                                    lblTotalPending.setText(""+totalPendingQty);
                                    myDataListColumns.clear();
                                    myDataListColumns.add("nsNameDuringTransaction");
                                    //myDataListColumns.add("nsIMEI");
                                    myDataList=jsonData;
                                    myDataAdapter = new MyDataAdapter(context,myDataListColumns, myDataList, (MyDataAdapter.myAdapterListener) context,nsModule,"");
                                    recyclerView.setAdapter(myDataAdapter);

                                    final JSONArray jsonCartItems = new JSONArray();
                                    //final JSONObject objItems = new JSONObject();

                                    final JSONObject objItems = new JSONObject();
                                    final JSONArray jsonArrObjItems = new JSONArray();

                                    btnRePrint.setText("Print GP");
                                    btnRePrint.setVisibility(View.VISIBLE);

                                    btnRePrint.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(UndeliveredListActivity.this, ""+jsonData+"\n"+getIntent().getStringExtra("nsLedgerName"), Toast.LENGTH_SHORT).show();
                                            printString.DeliveryGatePassPrintString(jsonData,getIntent().getStringExtra("nsLedgerName"),"");
                                        }
                                    });

                                    btnSave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String deliveredQty = "0",pendingQty="0";
                                            EditText deliveredQuantity = null;

                                            if(Integer.parseInt(getIntent().getStringExtra("nsAdditionalCharges"))>0 && txtRemarks.getText().toString().equals("")){

                                                DialogUtility.showDialog(UndeliveredListActivity.this,"Remarks is Mandatory","Error");
                                            }else if(nsTransportOrder.equals("1") && txtRemarks.getText().toString().equals("")){
                                                DialogUtility.showDialog(UndeliveredListActivity.this,"Remarks is Mandatory : Please Enter Transporters Name","Error");
                                            }
                                            else {
                                                try {
                                                    for (int i = 0; i < jsonData.length(); i++) {
                                                        View view = recyclerView.getChildAt(i);
                                                        deliveredQuantity = (EditText) view.findViewById(R.id.txtDelivered);
                                                        //if (deliveredQuantity.getText().toString().equals("")) {
                                                            deliveredQty = deliveredQuantity.getText().toString();
                                                        //}

                                                        TextView pendingQuantity = (TextView) view.findViewById(R.id.lblPending);
                                                        pendingQty = pendingQuantity.getText().toString();

                                                        JSONObject objData = jsonData.getJSONObject(i);
                                                        JSONObject objectProductList = new JSONObject();
                                                        objectProductList = objData;
                                                        objectProductList.put("nsDeliveredQty", deliveredQty);
                                                        objectProductList.put("nsPendingQty", pendingQty);
                                                        objectProductList.put("nsDeliveryLocationId", shop_location_id);
                                                        objectProductList.put("nsTransactionType", "600");
                                                        if (checkboxNOGP.isChecked()) {
                                                            objectProductList.put("nsNoGP", "2");
                                                        } else {
                                                            objectProductList.put("nsNoGP", "1");
                                                        }
                                                        if (checkboxNightStockDeduction.isChecked()) {
                                                            objectProductList.put("prNightStockStatus", "2");
                                                        } else {
                                                            objectProductList.put("prNightStockStatus", "1");
                                                        }
                                                        objectProductList.put("nsRemarks", txtRemarks.getText().toString());
                                                        objectProductList.put("nsTransactionId", nsId);
                                                        Date deliveryDate = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString());
                                                        objectProductList.put("nsDeliveryDate", new SimpleDateFormat("yyyy-MM-dd").format(deliveryDate));

                                                        jsonCartItems.put(objectProductList);

                                                        boolean isValidated1 = true;

                                                        if (deliveredQuantity.getText().toString().trim().equals("")) {
                                                            Toast.makeText(UndeliveredListActivity.this, "Please Enter Value or Enter zero", Toast.LENGTH_SHORT).show();
                                                            isValidated1 = false;
                                                        }else if (Integer.parseInt(deliveredQty) > Integer.parseInt(pendingQty)) {
                                                            Toast.makeText(UndeliveredListActivity.this, "no of delivery item exceeds Pending Items", Toast.LENGTH_SHORT).show();
                                                            isValidated1 = false;
                                                        }

                                                        if (isValidated1 == false) {
                                                            return;
                                                        }

                                                    }

                                                    objItems.put("id", nsId);
                                                    objItems.put("prCartItems", jsonCartItems.toString(4));
                                                    jsonArrObjItems.put(objItems);



                                                    //DialogUtility.showDialog(UndeliveredListActivity.this, "" + jsonCartItems, "");
                                                    insertData("-1", jsonArrObjItems.toString(4), nsModule,"",2,2,2);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(UndeliveredListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                        }
                                    });
                                }
                                if(dataDeliveredHistory.length()>0){
                                    String st = "";
                                    for (int i = 0; i<dataDeliveredHistory.length();i++) {
                                        JSONObject objData = dataDeliveredHistory.getJSONObject(i);

                                        st += "\n Sno : " + objData.getString("nsSlot");
                                        st += "\n Name : " + objData.getString("nsInventoryName");
                                        st += "\n Delivered Location : " + objData.getString("nsDeliveredLocation");
                                        Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDeliveryDate"));
                                        st += "\n Delivered Date : " + new SimpleDateFormat("dd-MM-yyyy").format(d);
                                        st += "\n Delivered Qty : " + objData.getString("nsDeliveredQty");
                                        st += "\n Remarks : " + objData.getString("nsRemarks");
                                        st += "\n GP : " + objData.getString("nsIsGp");
                                        st += "\n------------------\n\n";
                                    }

                                    lblDeliveredItems.setText(st);


                                }

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(UndeliveredListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("functionality", "fetch_table_data_undeliveredList");
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

    public void itemInitialise(){
        btnSave = (Button) findViewById(R.id.btnSave);
        btnRePrint = (Button) findViewById(R.id.btnRePrint);
        txtLocation = (AppCompatAutoCompleteTextView) findViewById(R.id.txtLocation);
        txtDate = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDate);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        lblDeliveredItems = (TextView) findViewById(R.id.lblDeliveredItems);
        lblCustomerDetails = (TextView) findViewById(R.id.lblCustomerDetails);
        checkboxNOGP = (CheckBox) findViewById(R.id.checkboxNOGP);
        checkboxNightStockDeduction = (CheckBox) findViewById(R.id.checkboxNightStockDeduction);
        dateLocationLayout = (LinearLayout) findViewById(R.id.dateLocationLayout);
    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {

    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }
}