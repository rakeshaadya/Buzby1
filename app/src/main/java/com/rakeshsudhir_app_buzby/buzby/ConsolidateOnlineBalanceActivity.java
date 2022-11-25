package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ConsolidateOnlineBalanceActivity extends BaseActivity implements MyDataAdapter.myAdapterListener, AdapterView.OnItemSelectedListener{

    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;

    private Context context;
    TextView lblMessage;

    DatePickerDialog picker;
    String Cdate;

    private String nsModule;
    private String sales_location_id;

    BottomNavigationView bottomNavigationView;
    Intent filterIntent=new Intent();
    String sortType="2";
    private Date filterDateFrom,filterDateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consolidate_online_balance);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Online Transfers");
        setSupportActionBar(toolbar);

        lblMessage = (TextView)findViewById(R.id.lblMessage);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setVisibility(View.VISIBLE);

        final Calendar cldr1 = Calendar.getInstance();

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        Cdate = String.format("%02d", year) + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
        filterDateFrom = cldr.getTime();
        filterDateTo = cldr.getTime();
        //dateFrom = new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime());

        nsModule = getIntent().getStringExtra("nsModule");

        sales_location_id=preferenceConfig.readLocationId();

        //Toast.makeText(this, ""+sales_location_id, Toast.LENGTH_SHORT).show();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(this,myDataListColumns, myDataList,this,nsModule,"");

        //displayData(filterDateFrom,sales_location_id);



        if(nsModule.equals("consolidated_online_balance_report")){

            //lblTotal.setVisibility(View.VISIBLE);
            filterIntent.putExtra("nsModule",nsModule);
            filterIntent.putExtra("sortType","radioDescending");

            Date date;
            Calendar cal=Calendar.getInstance();
            date = cal.getTime();

            //displayData(sales_location_id);

            filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
            //cal.add(Calendar.DATE,-30);
            //date=cal.getTime();
            filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
            filterIntent.putExtra("bank","2"); //yes by default
            filterIntent.putExtra("upi","1"); //yes by default
            filterIntent.putExtra("cheque","2"); //yes by default
            filterIntent.putExtra("nsLocationId", "");
            filterIntent.putExtra("nsLocation", "");
            filterIntent.putExtra("sortType","radioAscending");


            /*DialogUtility.showDialog(ConsolidateOnlineBalanceActivity.this,
                    filterIntent.getStringExtra("toDate")+"\n"
                            +filterIntent.getStringExtra("toDate1")+"\n"
                            +filterIntent.getStringExtra("fromDate")+"\n"
                            +filterIntent.getStringExtra("fromDate1")+"\n"
                            +filterIntent.getStringExtra("bank")+"\n"
                            +filterIntent.getStringExtra("upi")+"\n"
                            +filterIntent.getStringExtra("cheque")+"\n"
                            +filterIntent.getStringExtra("sortType")+"\n"
                    ,"");*/
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.action_filter:
                        Intent i = new Intent(ConsolidateOnlineBalanceActivity.this, SortAndFilterActivity.class);
                        i.putExtras(filterIntent.getExtras());
                        startActivityForResult(i, 1);
                        break;

                    case R.id.action_excel:

                        break;
                }
                return true;
            }
        });

        displayData();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConsolidateOnlineBalanceActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(resultCode == Activity.RESULT_OK){
                filterIntent=data;

                /*DialogUtility.showDialog(ConsolidateOnlineBalanceActivity.this,
                        filterIntent.getStringExtra("toDate")+"\n"
                                +filterIntent.getStringExtra("toDate1")+"\n"
                                +filterIntent.getStringExtra("fromDate")+"\n"
                                +filterIntent.getStringExtra("fromDate1")+"\n"
                                +filterIntent.getStringExtra("bank")+"\n"
                                +filterIntent.getStringExtra("upi")+"\n"
                                +filterIntent.getStringExtra("cheque")+"\n"
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
                        //DialogUtility.showDialog(ConsolidateOnlineBalanceActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                lblMessage.setText(response);
                                Toast.makeText(ConsolidateOnlineBalanceActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                            }else {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                TextView lblTotalAmount = (TextView)findViewById(R.id.lblTotalAmount);
                                double totalAmount = 0.0;
                                for(int i=0;i<jsonData.length();i++){
                                    JSONObject objData = jsonData.getJSONObject(i);
                                    totalAmount+=objData.getDouble("nsAmount");
                                }

                                final double finalTotalAmount = totalAmount;
                                lblTotalAmount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(ConsolidateOnlineBalanceActivity.this,upiPaymentReceiptActivity.class);
                                        i.putExtra("nsModule","upi_payment_receipt");
                                        i.putExtra("nsSpinnerType","1");
                                        i.putExtra("nsAmount1", ""+finalTotalAmount);
                                        i.putExtra("nsId","-1");
                                        startActivity(i);
                                        //Toast.makeText(ConsolidateOnlineBalanceActivity.this, ""+finalTotalAmount, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                lblTotalAmount.setText(""+totalAmount);

                                myDataList=jsonData;
                                myDataAdapter = new MyDataAdapter(context, myDataListColumns, myDataList, (MyDataAdapter.myAdapterListener) context, nsModule,"");
                                recyclerView.setAdapter(myDataAdapter);
                            }
                        }  catch (Exception e) {
                            lblMessage.setText("111 "+e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lblMessage.setText("222 "+error.getMessage());
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
                params.put("functionality", "fetch_consolidated_online_balance_report");

                if(!filterIntent.getStringExtra("fromDate").equals("")) {
                    Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                    params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                }

                if(!filterIntent.getStringExtra("toDate").equals("")) {
                    Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                    params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                }

                if(!filterIntent.getStringExtra("nsLocationId").equals("")) {
                    params.put("prLocationId", filterIntent.getStringExtra("nsLocationId"));
                }

                params.put("prBank", filterIntent.getStringExtra("bank"));
                params.put("prUPI", filterIntent.getStringExtra("upi"));
                params.put("prCheque", filterIntent.getStringExtra("cheque"));

                params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                /*DialogUtility.showDialog(ConsolidateOnlineBalanceActivity.this,
                        filterIntent.getStringExtra("toDate")+"\n"
                                +filterIntent.getStringExtra("toDate1")+"\n"
                                +filterIntent.getStringExtra("fromDate")+"\n"
                                +filterIntent.getStringExtra("fromDate1")+"\n"
                                +filterIntent.getStringExtra("bank")+"\n"
                                +filterIntent.getStringExtra("upi")+"\n"
                                +filterIntent.getStringExtra("cheque")+"\n"
                                +filterIntent.getStringExtra("sortType")+"\n"
                        ,"");*/


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

            Intent i = new Intent(ConsolidateOnlineBalanceActivity.this, ReceiptPaymentActivity.class);
            i.putExtra("nsId",item.getString("nsId"));
            i.putExtra("nsModule","transaction_receipt_payment");
            i.putExtra("transactionType","400");
            startActivity(i);
            //Toast.makeText(ConsolidateOnlineBalanceActivity.this, ""+item.toString(4), Toast.LENGTH_SHORT).show();

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
    }
}