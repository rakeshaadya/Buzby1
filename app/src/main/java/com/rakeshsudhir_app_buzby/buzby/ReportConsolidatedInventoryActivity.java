package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SearchView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportConsolidatedInventoryActivity extends BaseActivity implements MyDataAdapter.myAdapterListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener{

    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;

    private Context context;
    TextView lblMessage;
    public SwipeRefreshLayout swipeRefreshLayout;
    DatePickerDialog picker;

    private String nsModule;
    private String dateFrom,sales_location_id;

    String Cdate;

    BottomNavigationView bottomNavigationView;
    String filterLocationId = "",filterLocationName="";
    Intent filterIntent=new Intent();
    String sortType="1";
    private Date filterDateFrom,filterDateTo;
    Toolbar toolbar;
    JSONArray jsonDataforPrint = new JSONArray();

    CheckBox checkboxZeroValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_consolidated_inventory);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Consolidate Inventory Report");
        setSupportActionBar(toolbar);

        lblMessage = (TextView)findViewById(R.id.lblMessage);
        checkboxZeroValue = (CheckBox) findViewById(R.id.checkboxZeroValue);

        final Calendar cldr1 = Calendar.getInstance();
        dateFrom = new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime());

        nsModule = getIntent().getStringExtra("nsModule");

        sales_location_id=preferenceConfig.readLocationId();
        filterLocationId = preferenceConfig.readLocationId();
        filterLocationName = preferenceConfig.readlocationName();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        //Toast.makeText(this, ""+sales_location_id, Toast.LENGTH_SHORT).show();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        Cdate = String.format("%02d", year) + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
        filterDateFrom = cldr.getTime();
        filterDateTo = cldr.getTime();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(this,myDataListColumns, myDataList,this,nsModule,"");

        displayData();

        filterIntent.putExtra("nsModule", nsModule);

        //if(nsModule.equals("fetch_inventory_detailed_report")){
            filterIntent.putExtra("nsLocationId", filterLocationId);
            filterIntent.putExtra("nsLocation", filterLocationName);
        //}

        filterIntent.putExtra("sortType","radioAscending");

        Date date;
        Calendar cal=Calendar.getInstance();
        date = cal.getTime();

        filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
        filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
        filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
        filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        bottomNavigationView.getMenu().findItem(R.id.action_reprint).setVisible(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.action_filter:
                        Intent i = new Intent(ReportConsolidatedInventoryActivity.this, SortAndFilterActivity.class);
                        i.putExtras(filterIntent.getExtras());
                        startActivityForResult(i, 1);
                        break;

                    case R.id.action_excel:

                        break;

                        case R.id.action_reprint:
                            Date dfrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));

                            printString.stockReportPrintString(jsonDataforPrint,filterIntent.getStringExtra("nsLocationId")
                                    ,new SimpleDateFormat("yyyy-MM-dd").format(dfrom));
                        break;
                }
                return true;
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                displayData();
            }
        });


    }

    @Override
    public void onRefresh() {
        myDataAdapter.clear();
        swipeRefreshLayout.setRefreshing(true);
        displayData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(resultCode == Activity.RESULT_OK){
                filterIntent=data;



                /*DialogUtility.showDialog(ListActivity.this,
                        filterIntent.getStringExtra("toDate")+"\n"
                                +filterIntent.getStringExtra("toDate1")+"\n"
                                +filterIntent.getStringExtra("fromDate")+"\n"
                                +filterIntent.getStringExtra("fromDate1")+"\n"
                                +filterIntent.getStringExtra("nsDelivered")+"\n"
                                +filterIntent.getStringExtra("nsUnDelivered")+"\n"
                                +filterIntent.getStringExtra("nsPartial")+"\n"
                                +filterIntent.getStringExtra("nsCCType")+"\n"
                                +filterIntent.getStringExtra("nsPaymentType")+"\n"
                                +filterIntent.getStringExtra("nsArea")+"\n"
                                +filterIntent.getStringExtra("nsAreaId")+"\n"
                                +filterIntent.getStringExtra("nsCity")+"\n"
                                +filterIntent.getStringExtra("nsCityId")+"\n"
                                +filterIntent.getStringExtra("nsDeleted")+"\n"
                                +filterIntent.getStringExtra("nsSubModule")+"\n"
                                +filterIntent.getStringExtra("nsLocationId")+"\n"
                                +filterIntent.getStringExtra("nsLocation")+"\n"

                                +filterIntent.getStringExtra("sortType")+"\n"
                        ,"");*/

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void displayData() {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        //Toast.makeText(this, ""+JSON_URL, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(ReportConsolidatedInventoryActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                lblMessage.setText(response);
                                Toast.makeText(ReportConsolidatedInventoryActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                            }else {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                //DialogUtility.showDialog(ReportConsolidatedInventoryActivity.this,jsonData.toString(4),"");

                                jsonDataforPrint = jsonObject.getJSONArray("data");

                                filterLocationId = filterIntent.getStringExtra("nsLocationId");
                                filterLocationName = filterIntent.getStringExtra("nsLocation");

                                toolbar = (Toolbar) findViewById(R.id.toolbar);
                                toolbar.setTitle("Stock Report - "+filterLocationName);
                                setSupportActionBar(toolbar);

                                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(ReportConsolidatedInventoryActivity.this,DashboardActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addCategory(Intent.CATEGORY_HOME);
                                        startActivity(i);
                                    }
                                });

                                if(jsonData.length()>0) {

                                    myDataList = jsonData;
                                    myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonData, (MyDataAdapter.myAdapterListener) context, nsModule, "0");
                                    recyclerView.setAdapter(myDataAdapter);
                                }
                            }
                        } catch (Exception e) {
                            //lblMessage.setText("1 "+e.getMessage());
                            //Toast.makeText(ReportConsolidatedInventoryActivity.this, "1 "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText("2 "+error.getMessage());
                //Toast.makeText(ReportConsolidatedInventoryActivity.this, "2 "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCaption",preferenceConfig.readCompanyCaption());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", "master_reports");
                params.put("functionality", "fetch_consolidated_inventory_report");
                Date d = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                params.put("prDateFrom",new SimpleDateFormat("yyyy-MM-dd").format(d));
                params.put("prLocationId", filterIntent.getStringExtra("nsLocationId"));

                if(checkboxZeroValue.isChecked()){
                    params.put("prNonZero", ""+2);
                }else{
                    params.put("prNonZero", ""+1);
                }

                Log.d("RakeshSudhirrr",params.toString());
                //params.put("format", "list");
                //params.put("format", "list");
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

        swipeRefreshLayout.setRefreshing(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {
        try {
            Toast.makeText(this, " "+item.getString("nsId"), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(ReportConsolidatedInventoryActivity.this,ListActivity.class);
            i.putExtra("nsInventoryId",item.getString("nsId"));
            i.putExtra("nsInventoryName",item.getString("nsBrand"));
            i.putExtra("nsModule","fetch_inventory_detailed_report");
            i.putExtra("nsLocationId",filterIntent.getStringExtra("nsLocationId"));
            i.putExtra("nsLocation",filterIntent.getStringExtra("nsLocation"));
            startActivity(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }

    @Override
    public void onRestart(){
        super.onRestart();
        //refresh();
        displayData();
        //Toast.makeText(this, "restart "+filterIntent.getStringExtra("nsLocationId"), Toast.LENGTH_SHORT).show();
    }
}