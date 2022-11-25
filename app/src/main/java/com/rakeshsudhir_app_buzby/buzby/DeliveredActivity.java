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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeliveredActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, MyDataAdapter.myAdapterListener {

    private String nsModule="offset_items";
    TextView lblMessage;
    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private Context context;
    String nsId="",shop_location_id="",nsSalesLocation="",nsCustomerDetails="",nsSalesLocationId="",nsSalesDate="",nsVoucherNo="",nsRemarks="";

    ProgressDialog progress;
    private DatePickerDialog datePickerDialog;

    CheckBox checkboxNOGP;

    AppCompatAutoCompleteTextView txtLocation,txtDate;
    EditText txtRemarks;
    Button btnSave,btnDelete;

    TextView lblDeliveredItems,lblCustomerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Delivered Items");
        setSupportActionBar(toolbar);

        itemInitialise();

        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        txtLocation.setText(preferenceConfig.readlocationName());
        shop_location_id=preferenceConfig.readLocationId();

        nsId=getIntent().getStringExtra("nsId");

        Toast.makeText(this, ""+nsId, Toast.LENGTH_SHORT).show();
        nsModule=getIntent().getStringExtra("nsModule");
        nsSalesDate=getIntent().getStringExtra("nsSalesDate");
        nsVoucherNo=getIntent().getStringExtra("nsVoucherNo");
        nsSalesLocationId=getIntent().getStringExtra("nsSalesLocationId");
        nsSalesLocation=getIntent().getStringExtra("nsSalesLocationName");
        nsRemarks=getIntent().getStringExtra("nsRemarks");
        nsCustomerDetails="Name : "+getIntent().getStringExtra("nsLedgerName")+"\n"+"City : "+getIntent().getStringExtra("nsCity");
        Date d1 = ConvertFunctions.convertDatabaseDateStringToDate(nsSalesDate);
        lblCustomerDetails.setText("No : "+nsVoucherNo+"\n"+nsCustomerDetails+"\n"+"Sales Location : "+nsSalesLocation+"\nSales Date : "+new SimpleDateFormat("dd-MM-yyy").format(d1)+"\nSales Remarks : "+nsRemarks);



        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,DeliveredActivity.this,txtDate);
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
        myDataAdapter = new MyDataAdapter(DeliveredActivity.this,myDataListColumns, myDataList,this,nsModule,"");

        displayData();

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DeliveredActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,1);
            }
        });

        if(Integer.parseInt(nsId)>0){
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteData(nsId,"0",nsModule,0);
                }
            });
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DeliveredActivity.this,DashboardActivity.class);
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
                        //DialogUtility.showDialog(DeliveredActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                //DialogUtility.showDialog(DeliveredActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");
                                final JSONArray dataSalesHistory = jsonObject.getJSONArray("dataSalesHistory");

                                if(jsonData.length()>0){
                                    int totalQty=0;
                                    //int totalPendingQty=0;
                                    for(int i = 0;i<jsonData.length();i++) {
                                        JSONObject objData = jsonData.getJSONObject(i);
                                        totalQty+=objData.getInt("nsDeliveredQty");
                                       // totalPendingQty+=objData.getInt("nsBalance");
                                    }

                                    TextView txtQty = (TextView)findViewById(R.id.txtQty);
                                    txtQty.setText(""+totalQty);
                                    //TextView lblTotalPending = (TextView)findViewById(R.id.lblTotalPending);
                                    //lblTotalPending.setText(""+totalPendingQty);
                                    myDataListColumns.clear();
                                    myDataListColumns.add("nsInventoryName");
                                    //myDataListColumns.add("nsIMEI");
                                    myDataList=jsonData;

                                    //DialogUtility.showDialog(DeliveredActivity.this,myDataList.toString(4),"");
                                    myDataAdapter = new MyDataAdapter(context,myDataListColumns, myDataList, (MyDataAdapter.myAdapterListener) context,"offset_items_delivered","");
                                    recyclerView.setAdapter(myDataAdapter);

                                }
                                if(dataSalesHistory.length()>0){
                                    String st = "";
                                    for (int i = 0; i<dataSalesHistory.length();i++) {
                                        JSONObject objData = dataSalesHistory.getJSONObject(i);

                                        st += "\n" + (i+1);
                                        st += "\t\t" + objData.getString("nsNameDuringTransaction");
                                        st += "\t\t" + objData.getString("nsOriginValue");
                                        st += "\t\t" + objData.getString("nsIsGp");
                                        st += "\n------------------";
                                    }

                                    lblDeliveredItems.setText(st);
                                }

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(DeliveredActivity.this, "DD  "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("functionality", "fetch_table_data_deliveredList");
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
        btnDelete = (Button) findViewById(R.id.btnDelete);
        txtLocation = (AppCompatAutoCompleteTextView) findViewById(R.id.txtLocation);
        txtDate = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDate);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        lblDeliveredItems = (TextView) findViewById(R.id.lblDeliveredItems);
        lblCustomerDetails = (TextView) findViewById(R.id.lblCustomerDetails);
        checkboxNOGP = (CheckBox) findViewById(R.id.checkboxNOGP);
    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {

    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }
}