package com.rakeshsudhir_app_buzby.buzby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ListActivity extends AppCompatActivity implements MyDataAdapter.myAdapterListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    //TextView lblMessage,lblTotalAmt,lblCSV;
    LinearLayout layoutListTotal,layoutInventoryDetailedReportTableHeads,layoutPriceVariation,layoutStockManagement;
    SharedPreferenceConfig preferenceConfig;

    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String nsModule="",nsId="";
    private Context context;
    String Cdate;
    JSONArray myExportDataList = new JSONArray();
    private ProgressDialog progress;

    JSONArray jsonExcelData = new JSONArray();
    String filterType = "All",nsSubModule="";
    String filterLocationId = "",filterLocation="";

    String[] strArrayLocation;
    JSONArray jsonLocation;

    AppCompatAutoCompleteTextView txtRange;
    Button btnGo;

    BottomNavigationView bottomNavigationView;
    Intent filterIntent=new Intent();
    String sortType="1";
    private Date filterDateFrom,filterDateTo;

    int mPosition=0;

    CheckBox checkboxZeroBalance;

    String nsName="",nsCity="",nsInventoryName="",nsInventoryId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());


        //filterLocationId = preferenceConfig.readLocationId();
        nsModule = getIntent().getStringExtra("nsModule");
        if (getIntent().hasExtra("nsSubModule")) {
            nsSubModule = getIntent().getStringExtra("nsSubModule");
        }

        layoutPriceVariation = (LinearLayout) findViewById(R.id.layoutPriceVariation);
        layoutStockManagement = (LinearLayout) findViewById(R.id.layoutStockManagement);

        //Toast.makeText(this, nsModule+" - "+nsSubModule, Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        if (intent.hasExtra("nsId")) {
            nsId = getIntent().getStringExtra("nsId");
            //Toast.makeText(this, ""+nsId, Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra("nsLocationId")) {
            filterLocationId = getIntent().getStringExtra("nsLocationId");
            filterLocation = getIntent().getStringExtra("nsLocation");

            //Toast.makeText(this, "Return "+preferenceConfig.readLocationId(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, ""+nsId, Toast.LENGTH_SHORT).show();
        }else{
            filterLocationId = preferenceConfig.readLocationId();
            filterLocation = preferenceConfig.readlocationName();
            //Toast.makeText(this, "Default "+preferenceConfig.readLocationId(), Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra("nsInventoryId")) {
            nsInventoryId = getIntent().getStringExtra("nsInventoryId");
            //Toast.makeText(this, "nsInventoryId "+nsInventoryId, Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra("nsName")) {
            nsName = getIntent().getStringExtra("nsName");
            //Toast.makeText(this, ""+nsId, Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra("nsInventoryName")) {
            nsInventoryName = getIntent().getStringExtra("nsInventoryName");
            //Toast.makeText(this, ""+nsInventoryName, Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra("nsCity")) {
            nsCity = getIntent().getStringExtra("nsCity");
            //Toast.makeText(this, ""+nsId, Toast.LENGTH_SHORT).show();
        }

        txtRange = (AppCompatAutoCompleteTextView) findViewById(R.id.txtRange);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = 0;

                if(!txtRange.getText().toString().trim().equals("")){
                    r = Integer.parseInt(txtRange.getText().toString().trim());
                }
                rangFilterData(r);
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);

        /*lblTotalAmt = (TextView) findViewById(R.id.lblTotalAmt);
        lblCSV = (TextView) findViewById(R.id.lblCSV);
        layoutListTotal = (LinearLayout) findViewById(R.id.layoutListTotal);
        lblMessage = (TextView) findViewById(R.id.lblMessage);*/
        checkboxZeroBalance = (CheckBox) findViewById(R.id.checkboxZeroBalance);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        if (!preferenceConfig.readUserPosition().equals("1")){
            if (
                    nsModule.equals("report_consolidated_ledger_balance")
                            || nsModule.equals("master_ledger")
            ) {
                llm = new LinearLayoutManager(context) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
            }
        }

        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        //Toast.makeText(this, ""+preferenceConfig.readUserPosition(), Toast.LENGTH_SHORT).show();

        /*if(
            nsModule.equals("report_consolidated_ledger_balance")
                || nsModule.equals("master_ledger")
        ){

*/

            /*CustomGridLayoutManager cglm = new CustomGridLayoutManager(ListActivity.this);
            cglm.setScrollEnabled(false);*/

  /*      }*/

        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(this,myDataListColumns, myDataList,this,nsModule,nsSubModule);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        //progressBar.setIndeterminate(true);

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        Cdate = String.format("%02d", year) + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
        filterDateFrom = cldr.getTime();
        filterDateTo = cldr.getTime();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(nsModule.equals("master_location")) {
            toolbar.setTitle("Location");
        }else if(nsModule.equals("user_position")){
            toolbar.setTitle("User Position");
        }else if(nsModule.equals("android_signedin_users")){
            toolbar.setTitle("Unique ID");
        }else if(nsModule.equals("master_users")){
            toolbar.setTitle("Users");
        }else if(nsModule.equals("master_area")){
            toolbar.setTitle("Area");
        }else if(nsModule.equals("master_expense_head")){
            toolbar.setTitle("Expense Head");
        }else if(nsModule.equals("master_city")){
            toolbar.setTitle("City");
        }else if(nsModule.equals("master_ledger")){
            toolbar.setTitle("Ledger");
        }else if(nsModule.equals("master_brand")){
            toolbar.setTitle("Inventory Brand");
        }else if(nsModule.equals("master_units")){
            toolbar.setTitle("Inventory Units");
        }else if(nsModule.equals("master_inventory_category")){
            toolbar.setTitle("Inventory Category");
        }else if(nsModule.equals("master_inventory_subcategory")){
            toolbar.setTitle("Inventory SubCategory");
        }else if(nsModule.equals("master_inventory")){
            toolbar.setTitle("Inventory");
        }else if(nsModule.equals("ff_master_state_code")){
            toolbar.setTitle("State");
        }else if(nsModule.equals("master_modules")){
            toolbar.setTitle("Modules");
        }else if(nsModule.equals("transaction_openingstock")){
            toolbar.setTitle("Opening Stock");
        }else if(nsModule.equals("inventory_saftey_stock")){
            toolbar.setTitle("Saftey Stock");
        }else if(nsModule.equals("transaction_stock_management")){
            toolbar.setTitle("Stock Management");
            layoutStockManagement.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transaction_receipt_payment")){
            toolbar.setTitle("Receipt/Payment");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transactions_stock_transfer")){
            toolbar.setTitle("Stock Transfer");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transaction_ledger_opening_balance")){
            toolbar.setTitle("o/p Ledger Balance");
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales")){
            toolbar.setTitle("Sales");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_purchase_reminders")){
            toolbar.setTitle("Payment Reminders");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_order")){
            toolbar.setTitle("Sales Order");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_purchase")){
            toolbar.setTitle("Purchase");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_undelivered")){
            toolbar.setTitle("Undelivered");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_delivered")){
            toolbar.setTitle("Delivered");
            bottomNavigationView.setVisibility(View.VISIBLE);

        }else if(nsModule.equals("master_daily_stock_closing")){
            toolbar.setTitle("Closing Stock");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("fetch_inventory_detailed_report")){
            toolbar.setTitle(nsInventoryName);
            layoutInventoryDetailedReportTableHeads = (LinearLayout) findViewById(R.id.layoutInventoryDetailedReportTableHeads);
            layoutInventoryDetailedReportTableHeads.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("report_consolidated_ledger_balance")){
            toolbar.setTitle("Ledger Balance");
            LinearLayout layoutLedgerBalanceTableHeads = (LinearLayout)findViewById(R.id.layoutLedgerBalanceTableHeads);
            //bottomNavigationView.setVisibility(View.VISIBLE);
            layoutLedgerBalanceTableHeads.setVisibility(View.VISIBLE);
            checkboxZeroBalance.setChecked(true);
            if(preferenceConfig.readUserPosition().equals("1")){
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.getMenu().findItem(R.id.action_excel).setVisible(true);
                //bottomNavigationView.getMenu().findItem(R.id.action_filter).setVisible(false);
                            }
        }else if(nsModule.equals("report_ledger_transaction")){
            toolbar.setTitle(nsName+"\t"+nsCity);
            LinearLayout layoutLedgerTransactionTableHeads = (LinearLayout)findViewById(R.id.layoutLedgerTransactionTableHeads);
            //LinearLayout layoutLedgerTransactionTableHeads = (LinearLayout)findViewById(R.id.layoutLedgerTransactionTableHeads);
            bottomNavigationView.setVisibility(View.VISIBLE);
            //bottomNavigationView.getMenu().addSubMenu(R.id.action_excel);

            if(preferenceConfig.readUserPosition().equals("1")){
                bottomNavigationView.getMenu().findItem(R.id.action_excel).setVisible(true);
            }

            layoutLedgerTransactionTableHeads.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("upi_payment_receipt")){
            toolbar.setTitle("UPI Payment Receipt");
            LinearLayout layoutUPITableHeads = (LinearLayout)findViewById(R.id.layoutUPITableHeads);
            layoutUPITableHeads.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("opening_cash_balance")){
            toolbar.setTitle("Opening Cash");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("master_expense")){
            toolbar.setTitle("Expense");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(nsModule.equals("transaction_price_variation")){
            toolbar.setTitle("Price Variation");
            layoutPriceVariation.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
       
        if(
            nsModule.equals("upi_payment_receipt")
            || nsModule.equals("report_ledger_transaction")
        ){

            //lblTotal.setVisibility(View.VISIBLE);
            filterIntent.putExtra("nsModule",nsModule);
            filterIntent.putExtra("sortType","radioAscending");

            Date date;
            Calendar cal=Calendar.getInstance();
            date = cal.getTime();

            //displayData(sales_location_id);

            filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
            if(nsModule.equals("report_ledger_transaction")) {
                cal.add(Calendar.DATE, -90);
                date = cal.getTime();
            }
            filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
            filterIntent.putExtra("bank","1"); //yes by default
            filterIntent.putExtra("upi","1"); //yes by default
            filterIntent.putExtra("cheque","1"); //yes by default
            filterIntent.putExtra("ledgerid",nsId);
            filterIntent.putExtra("nsTransactionType","0");

            filterIntent.putExtra("nsLocation","");
            filterIntent.putExtra("nsLocationId","");


            /*DialogUtility.showDialog(ListActivity.this,
                    filterIntent.getStringExtra("toDate")+"\n"
                            +filterIntent.getStringExtra("toDate1")+"\n"
                            +filterIntent.getStringExtra("fromDate")+"\n"
                            +filterIntent.getStringExtra("fromDate1")+"\n"
                            +filterIntent.getStringExtra("bank")+"\n"
                            +filterIntent.getStringExtra("upi")+"\n"
                            +filterIntent.getStringExtra("cheque")+"\n"
                            +filterIntent.getStringExtra("sortType")+"\n"
                    ,"");*/
        }else if(nsModule.equals("transactions_in_out")
                && !nsSubModule.equals("transaction_sales_purchase_reminders")){
            //nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_purchase_reminders")
            //lblTotal.setVisibility(View.VISIBLE);
            filterIntent.putExtra("nsModule",nsModule);
            filterIntent.putExtra("nsSubModule",nsSubModule);
            filterIntent.putExtra("sortType","radioDescending");

            Date date;
            Calendar cal=Calendar.getInstance();
            date = cal.getTime();

            //displayData(sales_location_id);

            filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));

            if(nsSubModule.equals("transaction_sales_order")){

                //filterIntent.putExtra("nsToSales","2");

                cal.add(Calendar.DATE,-10);
                date=cal.getTime();
            }else {
                //filterIntent.putExtra("nsDelivered", "1");
            }
            //cal.add(Calendar.DATE,-30);
            //date=cal.getTime();
            filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));

            if(nsSubModule.equals("transaction_undelivered")){
                filterIntent.putExtra("nsDelivered","2");
            }else {
                filterIntent.putExtra("nsDelivered", "1");
            }

            if(nsSubModule.equals("transaction_delivered")){
                filterIntent.putExtra("nsUnDelivered","2");
                filterIntent.putExtra("nsPartial","2");
            }else{
                filterIntent.putExtra("nsUnDelivered","1");
                filterIntent.putExtra("nsPartial","1");
            }
            filterIntent.putExtra("nsCCType","0");
            filterIntent.putExtra("nsPaymentType","0");
            filterIntent.putExtra("nsArea","");
            filterIntent.putExtra("nsAreaId","");
            filterIntent.putExtra("nsCity","");
            filterIntent.putExtra("nsCityId","");
            filterIntent.putExtra("nsDeleted","2");
            filterIntent.putExtra("nsVerified","2");
            filterIntent.putExtra("nsIsTransportOrder","2");
            filterIntent.putExtra("nsIsCreditCustomer","2");


            if(nsSubModule.equals("transaction_sales")){
                filterIntent.putExtra("nsLocation",preferenceConfig.readlocationName());
                filterIntent.putExtra("nsLocationId",preferenceConfig.readLocationId());
            }else{
                filterIntent.putExtra("nsLocation","");
                filterIntent.putExtra("nsLocationId","");
            }

            filterIntent.putExtra("nsLedgerName","");
            filterIntent.putExtra("nsLedgerId","");

            filterIntent.putExtra("nsUser","");
            filterIntent.putExtra("nsUserId","");

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

            /*DialogUtility.showDialog(ListActivity.this,
                    filterIntent.getStringExtra("nsLedgerName")+"\n"
                            +filterIntent.getStringExtra("nsLedgerId")+"\n"
                            +filterIntent.getStringExtra("nsUser")+"\n"
                            +filterIntent.getStringExtra("nsUserId")+"\n"
                            +filterIntent.getStringExtra("nsDelivered")+"\n","");*/
        }else if(nsModule.equals("transaction_receipt_payment")
                || nsSubModule.equals("transaction_sales_purchase_reminders")
                || nsModule.equals("transaction_price_variation")
                || nsModule.equals("opening_cash_balance")
                || nsModule.equals("fetch_inventory_detailed_report")
                || nsModule.equals("transaction_stock_management")
                || nsModule.equals("transactions_stock_transfer")
                || nsModule.equals("master_daily_stock_closing")
        ){
            //lblTotal.setVisibility(View.VISIBLE);

            if(nsSubModule.equals("transaction_sales_purchase_reminders")){
                filterIntent.putExtra("nsModule", nsSubModule);
            }else{
                filterIntent.putExtra("nsModule", nsModule);
            }

            if(
                    nsModule.equals("fetch_inventory_detailed_report")
                   || nsModule.equals("master_daily_stock_closing")
                   || nsModule.equals("transaction_receipt_payment")
            ){
                filterIntent.putExtra("nsLocationId", filterLocationId);
                filterIntent.putExtra("nsLocation", filterLocation);
            }

            if(nsModule.equals("transaction_stock_management")){
                filterIntent.putExtra("nsLocationId", preferenceConfig.readLocationId());
                filterIntent.putExtra("nsLocation", preferenceConfig.readlocationName());
            }

            if(nsModule.equals("transaction_receipt_payment")){
                filterIntent.putExtra("nsPaymentTypeId", ""+0);
                filterIntent.putExtra("nsReceiptTypeId", ""+0);
            }

            if(nsModule.equals("transaction_receipt_payment")
                    || nsModule.equals("transactions_stock_transfer")) {
                filterIntent.putExtra("sortType", "radioDescending");
            }else{
                filterIntent.putExtra("sortType", "radioAscending");
            }

            Date date;
            Calendar cal=Calendar.getInstance();
            date = cal.getTime();

            filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
            filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));

            /*DialogUtility.showDialog(ListActivity.this,
                    filterIntent.getStringExtra("toDate")+"\n"
                            +filterIntent.getStringExtra("toDate1")+"\n"
                            +filterIntent.getStringExtra("fromDate")+"\n"
                            +filterIntent.getStringExtra("fromDate1")+"\n"


                    ,"");*/

        }else if(nsModule.equals("master_expense")
        ){
            //lblTotal.setVisibility(View.VISIBLE);
            filterIntent.putExtra("nsModule", nsModule);
            filterIntent.putExtra("nsLocationId", preferenceConfig.readLocationId());
            filterIntent.putExtra("nsLocation", preferenceConfig.readlocationName());
            filterIntent.putExtra("sortType","radioAscending");

            Date date;
            Calendar cal=Calendar.getInstance();
            date = cal.getTime();

            filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
            filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
            filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));

            filterIntent.putExtra("nsNameId", "");
            filterIntent.putExtra("nsName", "");
            filterIntent.putExtra("nsExpenseHeadId", "");
            filterIntent.putExtra("nsExpenseHead", "");

            /*DialogUtility.showDialog(ListActivity.this,
                    filterIntent.getStringExtra("toDate")+"\n"
                            +filterIntent.getStringExtra("toDate1")+"\n"
                            +filterIntent.getStringExtra("fromDate")+"\n"
                            +filterIntent.getStringExtra("fromDate1")+"\n"
                            +filterIntent.getStringExtra("nsLocationId")+"\n"
                            +filterIntent.getStringExtra("nsLocation")+"\n"
                            +filterIntent.getStringExtra("nsNameId")+"\n"
                            +filterIntent.getStringExtra("nsName")+"\n"
                            +filterIntent.getStringExtra("nsExpenseHeadId")+"\n"
                            +filterIntent.getStringExtra("nsExpenseHead")+"\n"

                    ,"");*/


            //DialogUtility.showDialog(ListActivity.this,filterIntent.getData().toString(),"");

        }else if(nsModule.equals("report_consolidated_ledger_balance")
        ){
            //lblTotal.setVisibility(View.VISIBLE);
            filterIntent.putExtra("nsModule", nsModule);

            filterIntent.putExtra("sortType","radioAscending");



            filterIntent.putExtra("nsAreaId", "");
            filterIntent.putExtra("nsArea", "");
            filterIntent.putExtra("nsCityId", "");
            filterIntent.putExtra("nsCity", "");


        }

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                displayData();
            }
        });

        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.action_filter:
                        Intent i = new Intent(ListActivity.this, SortAndFilterActivity.class);
                        i.putExtras(filterIntent.getExtras());
                        startActivityForResult(i, 1);
                        break;

                    case R.id.action_excel:
                        if(nsModule.equals("report_consolidated_ledger_balance")){
                            ExportDataGeneral.exportToCSV(ListActivity.this,jsonExcelData,"Ledger Balance Report");
                        }else if(nsModule.equals("report_ledger_transaction")) {
                            ExportDataToExcel();

                        }
                        break;
                }
                return true;
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
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
                        filterIntent.getStringExtra("nsLedgerName")+"\n"
                                +filterIntent.getStringExtra("nsLedgerId")+"\n"
                                +filterIntent.getStringExtra("nsUser")+"\n"
                                +filterIntent.getStringExtra("nsUserId")+"\n"
                                +filterIntent.getStringExtra("nsDelivered")+"\n","");*/

                /*DialogUtility.showDialog(ListActivity.this,
                        filterIntent.getStringExtra("toDate")+"\n"
                                +filterIntent.getStringExtra("toDate1")+"\n"
                                +filterIntent.getStringExtra("fromDate")+"\n"
                                +filterIntent.getStringExtra("fromDate1")+"\n"
                        ,"");*/


                /*DialogUtility.showDialog(ListActivity.this,
                        filterIntent.getStringExtra("toDate")+"\n"
                                +filterIntent.getStringExtra("toDate1")+"\n"
                                +filterIntent.getStringExtra("fromDate")+"\n"
                                +filterIntent.getStringExtra("fromDate1")+"\n"
                                +filterIntent.getStringExtra("nsLocationId")+"\n"
                                +filterIntent.getStringExtra("nsLocation")+"\n"
                                +filterIntent.getStringExtra("nsNameId")+"\n"
                                +filterIntent.getStringExtra("nsName")+"\n"
                                +filterIntent.getStringExtra("nsExpenseHeadId")+"\n"
                                +filterIntent.getStringExtra("nsExpenseHead")+"\n"

                        ,"");


*/

                //DialogUtility.showDialog(ListActivity.this,filterIntent.getStringExtra("nsDeleted"),"");


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
        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //DialogUtility.showDialog(ListActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(ListActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                //if(jsonData.length()>0) {
                                    myDataListColumns.clear();
                                    if (nsModule.equals("master_location")
                                            || nsModule.equals("user_position")
                                            || nsModule.equals("master_area")
                                            || nsModule.equals("master_expense_head")
                                            || nsModule.equals("master_city")
                                            || nsModule.equals("master_brand")
                                            || nsModule.equals("master_units")
                                            || nsModule.equals("ff_master_state_code")
                                            || nsModule.equals("master_inventory_category")){
                                        myDataListColumns.add("nsName");

                                    }else if (nsModule.equals("android_signedin_users")) {
                                        myDataListColumns.add("nsName");

                                    }else if (nsModule.equals("master_users")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsUserPosition");

                                    }else if (nsModule.equals("master_ledger")) {
                                        myDataListColumns.add("nsNameLedger");
                                        myDataListColumns.add("nsBalance");
                                        myDataListColumns.add("nsCompany");
                                        myDataListColumns.add("nsPhone");
                                        myDataListColumns.add("nsPhone2");
                                        myDataListColumns.add("nsCustomerName1");
                                        myDataListColumns.add("nsCity");
                                        myDataListColumns.add("nsArea");
                                        /*myDataListColumns.add("nsCompany");
                                        myDataListColumns.add("nsPhone");
                                        myDataListColumns.add("nsPhone2");
                                        myDataListColumns.add("nsLedgerCategory");
                                        myDataListColumns.add("nsCustomerName1");
                                        myDataListColumns.add("nsCustomerName2");*/

                                    }else if (nsModule.equals("master_inventory_subcategory")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsInventoryCategory");

                                    }else if (nsModule.equals("master_modules")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsNameAlias");

                                    }else if (nsModule.equals("master_inventory")) {
                                        myDataListColumns.add("nsCategory");
                                        myDataListColumns.add("nsSubCategory");
                                        myDataListColumns.add("nsBrand");
                                        myDataListColumns.add("nsUnit");
                                        myDataListColumns.add("nsNameDuringPrint");
                                        myDataListColumns.add("nsNameDuringTransaction");
                                    }else if (nsModule.equals("transaction_openingstock")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsLocation");
                                    }else if (nsModule.equals("inventory_saftey_stock")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsLocation");
                                    }else if (nsModule.equals("transaction_stock_management")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsLocation");
                                    }else if (nsModule.equals("transaction_ledger_opening_balance")) {
                                        myDataListColumns.add("nsId");
                                        myDataListColumns.add("nsName");
                                    }else if (nsModule.equals("master_expense")) {
                                        myDataListColumns.add("nsDate");
                                        myDataListColumns.add("nsExpenseHead");
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsLocation");
                                        myDataListColumns.add("nsRemarks");
                                        myDataListColumns.add("nsUser");
                                    }else if (nsModule.equals("opening_cash_balance")) {
                                        myDataListColumns.add("nsId");
                                        myDataListColumns.add("nsDate");
                                        myDataListColumns.add("nsAmount");
                                        myDataListColumns.add("nsLocation");
                                    }else if (nsModule.equals("transaction_price_variation")) {
                                        myDataListColumns.add("nsName");
                                    }else if (nsModule.equals("fetch_inventory_detailed_report")) {
                                        myDataListColumns.add("nsLedgerName");
                                        myDataListColumns.add("nsTransactionType");
                                        myDataListColumns.add("nsLocation");

                                        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                                        toolbar.setTitle(nsInventoryName+" - "+filterIntent.getStringExtra("nsLocation"));
                                        setSupportActionBar(toolbar);


                                        JSONArray dataSalesQtyTotal = jsonObject.getJSONArray("dataSalesQtyTotal");
                                        JSONArray dataDeliveredQtyTotal = jsonObject.getJSONArray("dataDeliveredQtyTotal");
                                        JSONArray dataUnDeliveredQtyTotal = jsonObject.getJSONArray("dataUnDeliveredQtyTotal");
                                        JSONArray dataPurchaseTotal = jsonObject.getJSONArray("dataPurchaseTotal");
                                        JSONArray dataStockInTotal = jsonObject.getJSONArray("dataStockInTotal");
                                        JSONArray dataStockOutTotal = jsonObject.getJSONArray("dataStockOutTotal");
                                        final JSONArray dataUnDeliveredQtyDetails = jsonObject.getJSONArray("dataUnDeliveredQtyDetails");
                                        final JSONArray dataOldSalesDeliveryDetails = jsonObject.getJSONArray("dataOldSalesDeliveryDetails");

                                        JSONObject obj1 = dataSalesQtyTotal.getJSONObject(0);
                                        JSONObject obj2 = dataDeliveredQtyTotal.getJSONObject(0);
                                        JSONObject obj3 = dataUnDeliveredQtyTotal.getJSONObject(0);
                                        JSONObject obj4 = dataPurchaseTotal.getJSONObject(0);
                                        JSONObject obj5 = dataStockInTotal.getJSONObject(0);
                                        JSONObject obj6 = dataStockOutTotal.getJSONObject(0);

                                        TextView lblTotalSales = (TextView) findViewById(R.id.lblTotalSales);
                                        TextView lblTotalDelivered = (TextView) findViewById(R.id.lblTotalDelivered);
                                        TextView lblTotalUnDelivered = (TextView) findViewById(R.id.lblTotalUnDelivered);
                                        TextView lblTotalPurchase = (TextView) findViewById(R.id.lblTotalPurchase);
                                        TextView lblTotalStockOut = (TextView) findViewById(R.id.lblTotalStockOut);
                                        TextView lblTotalStockIn = (TextView) findViewById(R.id.lblTotalStockIn);

                                        int totalSalesQty = 0;
                                        int totalDeliveredQty = 0;
                                        int totalUnDeliveredQty = 0;
                                        int totalPurchase = 0;
                                        int totalStockIn = 0;
                                        int totalStockOut = 0;

                                        if(!obj1.isNull("nsTotalSalesQuantity")){
                                            totalSalesQty = obj1.getInt("nsTotalSalesQuantity");
                                        }

                                        if(!obj2.isNull("nsDeliveredQuantity")){
                                            totalDeliveredQty = obj2.getInt("nsDeliveredQuantity");
                                        }

                                        if(!obj3.isNull("nsUnDeliveredQuantity")){
                                            totalUnDeliveredQty = obj3.getInt("nsUnDeliveredQuantity");
                                        }

                                        if(!obj4.isNull("nsPurchaseTotal")){
                                            totalPurchase = obj4.getInt("nsPurchaseTotal");
                                        }

                                        if(!obj5.isNull("nsStockInTotal")){
                                            totalStockIn = obj5.getInt("nsStockInTotal");
                                        }

                                        if(!obj6.isNull("nsStockOutTotal")){
                                            totalStockOut = obj6.getInt("nsStockOutTotal");
                                        }

                                        lblTotalSales.setText("Total Sales : "+totalSalesQty);
                                        lblTotalDelivered.setText("Total Delivered : "+totalDeliveredQty);
                                        lblTotalUnDelivered.setText("Total UnDelivered : "+totalUnDeliveredQty);
                                        lblTotalPurchase.setText("Total Purchase : "+totalPurchase);
                                        lblTotalStockIn.setText("Total Stock In : "+totalStockIn);
                                        lblTotalStockOut.setText("Total Stock Out : "+totalStockOut);

                                        lblTotalUnDelivered.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    if(dataUnDeliveredQtyDetails.length()>0) {

                                                        String st = "";int totalQty=0;
                                                        for (int i = 0; i < dataUnDeliveredQtyDetails.length(); i++) {
                                                            JSONObject obj = dataUnDeliveredQtyDetails.getJSONObject(i);
                                                            st += "Voucher No : " + obj.getString("nsVoucherno") + "\n";
                                                            st += "Sales Date : " + obj.getString("nsSalesDate") + "\n";
                                                            st += "Sales Location : " + obj.getString("nsSalesLocation") + "\n";
                                                            st += "Name : " + obj.getString("nsLedgerName") + "\n";
                                                            st += "Quantity : " + obj.getString("nsUnDeliveredQuantity") + "\n\n";
                                                            /*st += "Delivery Date : " + obj.getString("nsDeliveryDate") + "\n";
                                                            st += "Delivery Location : " + obj.getString("nsDeliveryLocation") + "\n\n\n";*/

                                                            totalQty+=obj.getInt("nsUnDeliveredQuantity");
                                                        }

                                                        st+="Total : "+totalQty;

                                                        DialogUtility.showDialog(ListActivity.this, st, "UNDELIVERED LIST");
                                                    }
                                                } catch (JSONException e) {
                                                    Toast.makeText(ListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        lblTotalDelivered.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    if(dataOldSalesDeliveryDetails.length()>0){
                                                        String st = "";int totalQty=0;
                                                        for (int i = 0; i < dataOldSalesDeliveryDetails.length(); i++) {
                                                            JSONObject obj = dataOldSalesDeliveryDetails.getJSONObject(i);
                                                            st += "Voucher No : " + obj.getString("nsVoucherno") + "\n";
                                                            st += "Sales Date : " + obj.getString("nsSalesDate") + "\n";
                                                            st += "Sales Location : " + obj.getString("nsSalesLocation") + "\n";
                                                            st += "Name : " + obj.getString("nsLedgerName") + "\n";
                                                            st += "Quantity : " + obj.getString("nsOffsetValue") + "\n";
                                                            st += "Delivery Date : " + obj.getString("nsDeliveryDate") + "\n";
                                                            st += "Delivery Location : " + obj.getString("nsDeliveryLocation") + "\n\n\n";

                                                            totalQty+=obj.getInt("nsOffsetValue");
                                                        }

                                                        st+="Total : "+totalQty;

                                                        DialogUtility.showDialog(ListActivity.this, st, "OLD SALES DELIVERED LIST");
                                                    }
                                                } catch (JSONException e) {
                                                    Toast.makeText(ListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }else if (nsModule.equals("transactions_in_out")) {
                                        myDataListColumns.add("nsVoucherNo");
                                        myDataListColumns.add("nsTotalAmount");
                                        myDataListColumns.add("nsLedgerName");
                                        myDataListColumns.add("nsArea");
                                        myDataListColumns.add("nsCity");
                                        myDataListColumns.add("nsLedgerAlias");
                                        myDataListColumns.add("nsPaidAmount");
                                        myDataListColumns.add("nsLocationName");
                                        myDataListColumns.add("nsTransactionTypeId");

                                        //ExportDataGeneral.exportToCSV(ListActivity.this,jsonData,nsModule);

                                    }else if (nsModule.equals("transaction_receipt_payment")){
                                        myDataListColumns.add("nsTransactionType");
                                        myDataListColumns.add("nsReceiptName");
                                        myDataListColumns.add("nsLedgerName");
                                        myDataListColumns.add("nsCity");
                                        myDataListColumns.add("nsArea");
                                        myDataListColumns.add("nsAmount");
                                        myDataListColumns.add("nsLocation");
                                        myDataListColumns.add("nsUser");
                                    }else if (nsModule.equals("transactions_stock_transfer")) {
                                        myDataListColumns.add("nsVoucherNo");
                                        myDataListColumns.add("nsFromLocation");
                                        myDataListColumns.add("nsToLocation");
                                        myDataListColumns.add("nsBrand");
                                    }else if (nsModule.equals("master_daily_stock_closing")) {
                                        myDataListColumns.add("nsName");
                                        myDataListColumns.add("nsBrandName");
                                    }else if (nsModule.equals("upi_payment_receipt")) {
                                        myDataListColumns.add("nsUPIName");
                                        myDataListColumns.add("nsAmount");
                                        myDataListColumns.add("nsRemarks");
                                    }else if (nsModule.equals("report_consolidated_ledger_balance")) {
                                        myDataListColumns.add("nsNameLedger");
                                        myDataListColumns.add("nsArea");
                                        myDataListColumns.add("nsCity");
                                        myDataListColumns.add("nsBalance");
                                        myDataListColumns.add("nsCompany");
                                        myDataListColumns.add("nsPhone");


                                        jsonExcelData = new JSONArray();

                                        for (int i = 0;i<jsonData.length();i++){
                                            JSONObject obj = jsonData.getJSONObject(i);
                                            JSONObject objExcel = new JSONObject();
                                            objExcel.put("SNO",(i+1));
                                            objExcel.put("CATEGORY",obj.getString("nsLedgerCategory"));
                                            objExcel.put("NAME",obj.getString("nsName"));
                                            if(obj.isNull("nsArea")){
                                                objExcel.put("AREA", "");
                                            }else {
                                                objExcel.put("AREA", obj.getString("nsArea"));
                                            }

                                            objExcel.put("CITY", obj.getString("nsCity"));
                                            objExcel.put("BALANCE", obj.getString("nsBalance"));
                                            jsonExcelData.put(objExcel);
                                        }

                                    }else if (nsModule.equals("report_ledger_transaction")) {
                                        myDataListColumns.add("nsLocationName");
                                        myDataListColumns.add("nsTransactionType");
                                    }

                                    myDataList = jsonData;
                                //Toast.makeText(ListActivity.this, ""+myDataList.toString(), Toast.LENGTH_SHORT).show();
                                    filterData(searchView.getQuery().toString());
                                //}
                                progress.dismiss();

                            }
                        }  catch (Exception e) {
                            //lblMessage.setText(e.getMessage());
                            progress.dismiss();
                            Toast.makeText(ListActivity.this, "expec "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*if(error.networkResponse==null){
                    DialogUtility.showDialog(ListActivity.this,"Error: Please Check your Internet Connection","Alert");
                }else {*/

                    Log.e("Rakesh123242523", error.getCause()+" @\n "+error.getLocalizedMessage()+" @\n "+error.networkResponse+" VolleyError: \n" + error.getNetworkTimeMs());
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
                /*if(nsModule.equals("master_location")
                        || nsModule.equals("user_position")
                        || nsModule.equals("android_signedin_users")
                        || nsModule.equals("master_users")
                        || nsModule.equals("master_area")
                        || nsModule.equals("master_city")
                        || nsModule.equals("master_brand")
                        || nsModule.equals("master_units")
                        || nsModule.equals("master_inventory_category")
                        || nsModule.equals("master_inventory_subcategory")
                        || nsModule.equals("master_modules")
                        || nsModule.equals("master_ledger")
                        || nsModule.equals("transaction_openingstock")
                        || nsModule.equals("master_inventory")
                ){*/
                    params.put("module", nsModule);
                //}

                if (nsModule.equals("master_ledger")){
                    Intent intent = getIntent();
                    if (intent.hasExtra("nsLedgerCategoryE")) {
                        params.put("prEmpId", getIntent().getStringExtra("nsLedgerCategoryE"));
                        //Toast.makeText(this, "nsInventoryId "+nsInventoryId, Toast.LENGTH_SHORT).show();
                    }
                    if (intent.hasExtra("nsLedgerCategoryH")) {
                        params.put("prHamId", getIntent().getStringExtra("nsLedgerCategoryH"));
                        //Toast.makeText(this, "nsInventoryId "+nsInventoryId, Toast.LENGTH_SHORT).show();
                    }
                }
                if(nsModule.equals("master_daily_stock_closing")){
                    params.put("prLocationId", filterLocationId);

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }
                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }
                }

                if(nsModule.equals("fetch_inventory_detailed_report")
                || nsModule.equals("transaction_stock_management")){
                    //params.put("prLocationId", filterLocationId);
                    if(!filterIntent.getStringExtra("nsLocationId").equals("")) {
                        params.put("prLocationId", filterIntent.getStringExtra("nsLocationId"));
                    }
                    if(!nsInventoryId.equals("")) {
                        params.put("prInventoryId", nsInventoryId);
                    }
                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }
                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }
                    if(nsModule.equals("transaction_stock_management")){
                        params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                    }
                }

                if(nsModule.equals("transaction_receipt_payment")){
                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));

                    if(!filterIntent.getStringExtra("nsLocationId").equals("")) {
                        params.put("prLocationId", filterIntent.getStringExtra("nsLocationId"));
                    }
                    if(!filterIntent.getStringExtra("nsPaymentTypeId").equals("0")) {
                        params.put("prPaymentTypeId", filterIntent.getStringExtra("nsPaymentTypeId"));
                    }
                    if(!filterIntent.getStringExtra("nsReceiptTypeId").equals("0")) {
                        if(filterIntent.getStringExtra("nsReceiptTypeId").equals("1")){
                            params.put("prReceiptTypeId", ""+400);
                        }else if(filterIntent.getStringExtra("nsReceiptTypeId").equals("2")){
                            params.put("prReceiptTypeId", ""+500);
                        }
                    }
                }

                if(nsModule.equals("opening_cash_balance")){
                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                }

                if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales")){
                    params.put("prTransactionTypeId", "200");

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("prDelivered",filterIntent.getStringExtra("nsDelivered"));
                    params.put("prUnDelivered",filterIntent.getStringExtra("nsUnDelivered"));
                    params.put("prPartial",filterIntent.getStringExtra("nsPartial"));
                    params.put("prDeleted",filterIntent.getStringExtra("nsDeleted"));
                    params.put("prCCType",filterIntent.getStringExtra("nsCCType"));


                    if(!filterIntent.getStringExtra("nsPaymentType").equals("0")){
                        int n=0;
                        n = Integer.parseInt(filterIntent.getStringExtra("nsPaymentType"));
                        params.put("prPaymentType",""+(n+1));
                    }else{
                        params.put("prPaymentType","0");
                    }

                    if(!filterIntent.getStringExtra("nsAreaId").equals("")){
                        params.put("prAreaId",filterIntent.getStringExtra("nsAreaId"));
                    }

                    if(!filterIntent.getStringExtra("nsCityId").equals("")){
                        params.put("prCityId",filterIntent.getStringExtra("nsCityId"));
                    }

                    if(!filterIntent.getStringExtra("nsLocationId").equals("")){
                        params.put("prLocationId",filterIntent.getStringExtra("nsLocationId"));
                    }

                    if(!filterIntent.getStringExtra("nsLedgerId").equals("")){
                        params.put("prLedgerId",filterIntent.getStringExtra("nsLedgerId"));
                    }

                    if(!filterIntent.getStringExtra("nsUserId").equals("")){
                        params.put("prUserId",filterIntent.getStringExtra("nsUserId"));
                    }

                    if(filterIntent.getStringExtra("nsIsTransportOrder").equals("1")){
                        params.put("prIsTransportOrder","1");
                    }

                    if(filterIntent.getStringExtra("nsIsCreditCustomer").equals("1")){
                        params.put("nsIsCreditCustomer","1");
                    }



                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));

                }

                if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_order")){
                    params.put("prTransactionTypeId", "210");

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("prDelivered",filterIntent.getStringExtra("nsDelivered"));
                    params.put("prUnDelivered",filterIntent.getStringExtra("nsUnDelivered"));
                    params.put("prPartial",filterIntent.getStringExtra("nsPartial"));
                    params.put("prDeleted",filterIntent.getStringExtra("nsDeleted"));
                    params.put("prCCType",filterIntent.getStringExtra("nsCCType"));
                    if(filterIntent.getStringExtra("nsIsCreditCustomer").equals("1")){
                        params.put("nsIsCreditCustomer","1");
                    }

                    /*if(!filterIntent.getStringExtra("nsPaymentType").equals("0")){
                        int n=0;
                        n = Integer.parseInt(filterIntent.getStringExtra("nsPaymentType"));
                        params.put("prPaymentType",""+(n+1));
                    }else{*/
                        params.put("prPaymentType","0");
                    //}

                    if(!filterIntent.getStringExtra("nsAreaId").equals("")){
                        params.put("prAreaId",filterIntent.getStringExtra("nsAreaId"));
                    }

                    if(!filterIntent.getStringExtra("nsLocationId").equals("")){
                        params.put("prLocationId",filterIntent.getStringExtra("nsLocationId"));
                    }

                    if(!filterIntent.getStringExtra("nsCityId").equals("")){
                        params.put("prCityId",filterIntent.getStringExtra("nsCityId"));
                    }

                    if(filterIntent.getStringExtra("nsIsTransportOrder").equals("1")){
                        params.put("prIsTransportOrder","1");
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                }

                if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_purchase")){
                    params.put("prTransactionTypeId", "300");

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    if(filterIntent.getStringExtra("nsVerified").equals("1")) {
                        params.put("prIsNotVerified", "2");
                    }

                    params.put("prCCType",filterIntent.getStringExtra("nsCCType"));

                    params.put("prPaymentType","0");

                    if(!filterIntent.getStringExtra("nsAreaId").equals("")){
                        params.put("prAreaId",filterIntent.getStringExtra("nsAreaId"));
                    }

                    if(!filterIntent.getStringExtra("nsCityId").equals("")){
                        params.put("prCityId",filterIntent.getStringExtra("nsCityId"));
                    }

                    if(!filterIntent.getStringExtra("nsLocationId").equals("")){
                        params.put("prLocationId",filterIntent.getStringExtra("nsLocationId"));
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));

                }

                if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_purchase_reminders")){

                    params.put("prSubModule",nsSubModule);

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }


                }

                if(nsModule.equals("transaction_price_variation")){

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                }

                if(nsModule.equals("transactions_stock_transfer")){

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));

                }

                if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_undelivered")){
                    params.put("prTransactionTypeId", "200");
                    params.put("prTransactionStatus", "3");

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("prDelivered",filterIntent.getStringExtra("nsDelivered"));
                    params.put("prUnDelivered",filterIntent.getStringExtra("nsUnDelivered"));
                    params.put("prPartial",filterIntent.getStringExtra("nsPartial"));

                    params.put("prCCType",filterIntent.getStringExtra("nsCCType"));

                    params.put("prPaymentType","0");

                    if(!filterIntent.getStringExtra("nsAreaId").equals("")){
                        params.put("prAreaId",filterIntent.getStringExtra("nsAreaId"));
                    }

                    if(!filterIntent.getStringExtra("nsCityId").equals("")){
                        params.put("prCityId",filterIntent.getStringExtra("nsCityId"));
                    }

                    if(!filterIntent.getStringExtra("nsLocationId").equals("")){
                        params.put("prLocationId",filterIntent.getStringExtra("nsLocationId"));
                    }

                    if(filterIntent.getStringExtra("nsIsTransportOrder").equals("1")){
                        params.put("prIsTransportOrder","1");
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                }

                if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_delivered")){
                    params.put("prTransactionTypeId", "200");
                    params.put("prTransactionStatus", "3");
                    params.put("prSubModule", "transaction_delivered");

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }
                    params.put("prDelivered",filterIntent.getStringExtra("nsDelivered"));
                    params.put("prUnDelivered",filterIntent.getStringExtra("nsUnDelivered"));
                    params.put("prPartial",filterIntent.getStringExtra("nsPartial"));

                    params.put("prCCType",filterIntent.getStringExtra("nsCCType"));

                    params.put("prPaymentType","0");

                    if(!filterIntent.getStringExtra("nsAreaId").equals("")){
                        params.put("prAreaId",filterIntent.getStringExtra("nsAreaId"));
                    }

                    if(!filterIntent.getStringExtra("nsCityId").equals("")){
                        params.put("prCityId",filterIntent.getStringExtra("nsCityId"));
                    }

                    if(!filterIntent.getStringExtra("nsLocationId").equals("")){
                        params.put("prLocationId",filterIntent.getStringExtra("nsLocationId"));
                    }

                    if(filterIntent.getStringExtra("nsIsTransportOrder").equals("1")){
                        params.put("prIsTransportOrder","1");
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                }

                if(nsModule.equals("upi_payment_receipt")){
                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));
                }

                if(nsModule.equals("master_expense")){
                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }

                    params.put("sortfilter",filterIntent.getStringExtra("sortType"));

                    if(!filterIntent.getStringExtra("nsNameId").equals("")) {
                        params.put("nsNameId", filterIntent.getStringExtra("nsNameId"));
                        params.put("nsName", filterIntent.getStringExtra("nsName"));
                    }
                    if(!filterIntent.getStringExtra("nsLocationId").equals("")) {
                        params.put("nsLocationId", filterIntent.getStringExtra("nsLocationId"));
                        params.put("nsLocation", filterIntent.getStringExtra("nsLocation"));
                    }
                    if(!filterIntent.getStringExtra("nsExpenseHeadId").equals("")) {
                        params.put("nsExpenseHead", filterIntent.getStringExtra("nsExpenseHead"));
                        params.put("nsExpenseHeadId", filterIntent.getStringExtra("nsExpenseHeadId"));
                    }

                }

                if(nsModule.equals("report_consolidated_ledger_balance")){
                    params.put("module", nsSubModule);

                    if(checkboxZeroBalance.isChecked()){
                        params.put("prZeroBalance", "1");
                    }else{
                        params.put("prZeroBalance", "2");
                    }
                    if(!filterIntent.getStringExtra("nsAreaId").equals("")){
                        params.put("prAreaId",filterIntent.getStringExtra("nsAreaId"));
                    }

                    if(!filterIntent.getStringExtra("nsCityId").equals("")){
                        params.put("prCityId",filterIntent.getStringExtra("nsCityId"));
                    }

                    params.put("functionality", "fetch_consolidated_ledger_balance_report");
                }else if(nsModule.equals("fetch_inventory_detailed_report")){
                    params.put("module", "master_reports");
                    params.put("functionality", "fetch_inventory_detailed_report");
                }else if(nsModule.equals("report_ledger_transaction")){
                    params.put("module", nsSubModule);
                    params.put("functionality", "fetch_ledger_transaction_report");
                    params.put("prLedgerId", nsId);

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }else{
                        params.put("datefrom", "2020-01-01");
                    }

                    if(!filterIntent.getStringExtra("nsTransactionType").equals("0")){
                        String tranType = "0";

                        if(filterIntent.getStringExtra("nsTransactionType").equals("1")){
                            params.put("prTransactionType","200");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("2")){
                            params.put("prTransactionType","400");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("3")){
                            params.put("prTransactionType","300");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("4")){
                            params.put("prTransactionType","500");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("5")){
                            params.put("prTransactionType","600"); //Purchase Return
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("6")){
                            params.put("prTransactionType","700"); //Sales Return
                        }


                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }else{
                        Date date;
                        Calendar cal=Calendar.getInstance();
                        date = cal.getTime();
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(date));
                    }

                }else {
                    params.put("module", nsModule);
                    params.put("functionality", "fetch_table_data");
                }

                Log.d("Rakeshsudirrrr",params.toString());
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
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //if(nsModule.equals("master_ledger")){
            if(nsSubModule.equals("returnValue")){
                searchView.setIconifiedByDefault(false);
                searchView.requestFocus();
            }
            //if(searchView.getQuery().toString().length())
        //}
        //searchView.requestFocus();

        MenuItem menuAddItem = menu.findItem(R.id.menuAdd);
        if(nsSubModule.equals("transaction_undelivered") 
                || nsSubModule.equals("transaction_delivered")
                || nsModule.equals("report_consolidated_ledger_balance")
                || nsModule.equals("report_ledger_transaction")
                || nsSubModule.equals("transaction_sales_purchase_reminders")
                || nsSubModule.equals("fetch_inventory_detailed_report")
                || nsSubModule.equals("transaction_price_variation")
                || nsSubModule.equals("ff_master_state_code")
        ){
            menuAddItem.setVisible(false);
        }

        MenuItem menuConnect = menu.findItem(R.id.menuConnect);

        menuConnect.setVisible(true);


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterData(query);
                return false;
            }
        });

        return true;
    }

    private void rangFilterData(int range){
        JSONObject objData;
        Boolean isFound;
        JSONArray filteredList = new JSONArray();

        try {

            for(int i=0;i<myDataList.length();i++){
                objData = myDataList.getJSONObject(i);
                Calendar cal = Calendar.getInstance();
                int day1 = cal.get(Calendar.DAY_OF_MONTH);
                int month1 = cal.get(Calendar.MONTH);
                int year1 = cal.get(Calendar.YEAR);

                String cDate = String.format("%02d", year1) + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", day1);

                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = originalFormat.parse(objData.getString("nsLastSalesDate"));
                Date d = originalFormat.parse(cDate);
                long diff = d.getTime() - date.getTime();
                int conDays = Integer.parseInt(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
                if(conDays>=range){
                    filteredList.put(objData);
                }
            }

            myDataAdapter = new MyDataAdapter(context,myDataListColumns, filteredList, (MyDataAdapter.myAdapterListener) context,nsModule,nsSubModule);
            //Toast.makeText(ReportActivity.this, String.valueOf(filteredList.length()), Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(myDataAdapter);
            recyclerView.scrollToPosition(mPosition);

        }catch (JSONException e) {
            e.printStackTrace();
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void filterData(String query){
        JSONArray filteredList = new JSONArray();
        JSONObject objData;
        Boolean isFound;
        String[] arrOfStr = query.split(" ", 100);


        for(int i=0;i<myDataList.length();i++){
            try {
                objData = myDataList.getJSONObject(i);
                /*if(objData.getString("nsName").toLowerCase().contains(query.toLowerCase())){
                    filteredList.put(objData);
                }*/
                for(int j=0;j<myDataListColumns.size();j++){
                    isFound=true;
                    for(int ai=0;ai<arrOfStr.length;ai++){
                        if(!objData.getString(myDataListColumns.get(j)).toLowerCase().contains(arrOfStr[ai].toLowerCase())){
                            isFound=false;

                            break;
                        }
                    }
                    if(isFound){
                        filteredList.put(objData);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(!preferenceConfig.readUserPosition().equals("1")) {
            if (
                    nsModule.equals("report_consolidated_ledger_balance")
                            || nsModule.equals("master_ledger")
            ) {
                int searchLength = 0;
                searchLength = searchView.getQuery().toString().length();
                if (searchLength > 1) {
                    LinearLayoutManager llm = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(llm);
                } else {
                    LinearLayoutManager llm = new LinearLayoutManager(this);

                    llm = new LinearLayoutManager(context) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    };
                    recyclerView.setLayoutManager(llm);
                }
            }
        }


        myDataAdapter = new MyDataAdapter(context,myDataListColumns, filteredList, (MyDataAdapter.myAdapterListener) context,nsModule,nsSubModule);
        //Toast.makeText(ReportActivity.this, String.valueOf(filteredList.length()), Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(myDataAdapter);
        recyclerView.scrollToPosition(mPosition);
        //myDataAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuSearch) {
            return true;
        }else if (id == R.id.menuConnect) {
            Intent i = new Intent(ListActivity.this, PrintActivity.class);
            startActivity(i);
            return true;
        }else if (id == R.id.menuAdd){

            //Master One
            if(nsModule.equals("master_location")
                    || nsModule.equals("user_position")
                    || nsModule.equals("master_area")
                    || nsModule.equals("master_expense_head")
                    || nsModule.equals("master_city")
                    || nsModule.equals("master_brand")
                    ||nsModule.equals("master_units")
                    ||nsModule.equals("master_inventory_category")){

                Intent filter = new Intent(ListActivity.this, MasterOneActivity.class);
                filter.putExtra("nsModule", nsModule);
                filter.putExtra("nsId","-1");
                startActivity(filter);
            }else if(nsModule.equals("android_signedin_users")){
                Intent filter = new Intent(ListActivity.this, AndroidIDActivity.class);
                filter.putExtra("nsModule", nsModule);
                filter.putExtra("nsId","-1");
                startActivity(filter);
            }else if(nsModule.equals("master_users")){
                Intent filter = new Intent(ListActivity.this, MasterUserActivity.class);
                filter.putExtra("nsModule", nsModule);
                filter.putExtra("nsId","-1");
                startActivity(filter);
            }else if(nsModule.equals("master_expense")){
                Intent filter = new Intent(ListActivity.this, MasterExpenseActivity.class);
                filter.putExtra("nsModule", nsModule);
                filter.putExtra("nsId","-1");
                startActivity(filter);
            }else if(nsModule.equals("master_ledger")){
                Intent i = new Intent(ListActivity.this, MasterLedgerActivity.class);
                i.putExtra("nsModule", nsModule);
                i.putExtra("nsId", "-1");
                startActivity(i);
            }else if(nsModule.equals("master_modules")){
                Intent i = new Intent(ListActivity.this,MasterModules.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("master_inventory_subcategory")){
                Intent i = new Intent(ListActivity.this,MasterInventorySubCategoryActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("master_inventory")){
                Intent i = new Intent(ListActivity.this,MasterInventoryActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transaction_openingstock") || nsModule.equals("inventory_saftey_stock")){
                Intent i = new Intent(ListActivity.this,TransactionOpeningStock.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transaction_stock_management")){
                Intent i = new Intent(ListActivity.this,StockManagementActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transaction_ledger_opening_balance")){
                Intent i = new Intent(ListActivity.this,TransactionOpeningLedgerBalance.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transaction_receipt_payment")){
                Intent i = new Intent(ListActivity.this,ReceiptPaymentActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transactions_stock_transfer")){
                Intent i = new Intent(ListActivity.this,StockTransferActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales")){
                Intent i = new Intent(ListActivity.this,SalesTransactionActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_order")){
                Intent i = new Intent(ListActivity.this,SalesOrderActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_purchase")){
                Intent i = new Intent(ListActivity.this,TransactionPurchaseActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("master_daily_stock_closing")){
                Intent i = new Intent(ListActivity.this,ClosingStockActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("upi_payment_receipt")){
                Intent i = new Intent(ListActivity.this,upiPaymentReceiptActivity.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }else if(nsModule.equals("opening_cash_balance")){
                Intent i = new Intent(ListActivity.this,OpeningCashBalance.class);
                i.putExtra("nsModule",nsModule);
                i.putExtra("nsId","-1");
                startActivity(i);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {

        mPosition=position;
        if(nsModule.equals("master_location")
                || nsModule.equals("user_position")
                || nsModule.equals("master_expense_head")
        ){
            if(nsSubModule.equals("returnValue")){
                Intent i = new Intent();
                try {
                    i.putExtra("nsName",item.getString("nsName"));
                    i.putExtra("nsId",item.getString("nsId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK,i);
                finish();
            }else {
                Intent i = new Intent(ListActivity.this, MasterOneActivity.class);
                i.putExtra("nsModule", nsModule);
                try {
                    i.putExtra("nsId", item.getString("nsId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }

        }else if(nsModule.equals("master_area")){
            Intent i = new Intent();
            try {
                i.putExtra("nsArea",item.getString("nsName"));
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK,i);
            finish();

        }/*else if(nsModule.equals("master_expense_head")){
            Intent i = new Intent();
            try {
                i.putExtra("nsName",item.getString("nsName"));
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK,i);
            finish();

        }*/else if(nsModule.equals("master_city")){
            Intent i = new Intent();
            try {
                i.putExtra("nsCity",item.getString("nsName"));
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK,i);
            finish();

        }else if(nsModule.equals("master_brand")
                ||nsModule.equals("master_units")
                ||nsModule.equals("ff_master_state_code")
                ||nsModule.equals("master_inventory_category")){
            Intent i = new Intent();
            try {
                i.putExtra("nsName",item.getString("nsName"));
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK,i);
            finish();

        }else if(nsModule.equals("android_signedin_users")){
            Intent i = new Intent(ListActivity.this, AndroidIDActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("master_users")){
            if(nsSubModule.equals("returnValue")){
                Intent i = new Intent();
                try {
                    i.putExtra("nsName",item.getString("nsName"));
                    i.putExtra("nsId",item.getString("nsId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK,i);
                finish();
            }else {

                Intent i = new Intent(ListActivity.this, MasterUserActivity.class);
                i.putExtra("nsModule", nsModule);
                try {
                    i.putExtra("nsId", item.getString("nsId"));
                    i.putExtra("nsName", item.getString("nsName"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }


        }else if(nsModule.equals("transaction_receipt_payment")){
            Intent i = new Intent(ListActivity.this, ReceiptPaymentActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("report_consolidated_ledger_balance")){
            Intent i = new Intent(ListActivity.this, ListActivity.class);
            i.putExtra("nsModule","report_ledger_transaction");
            i.putExtra("nsSubModule",nsSubModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsName",item.getString("nsName"));
                i.putExtra("nsCity",item.getString("nsCity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

        }else if(nsModule.equals("opening_cash_balance")){
            Intent i = new Intent(ListActivity.this, OpeningCashBalance.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);
        }else if(nsModule.equals("master_expense")){
            Intent i = new Intent(ListActivity.this, MasterExpenseActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);
        }else if(nsModule.equals("report_ledger_transaction")){
            Intent i=new Intent();
            try {
                if(item.getInt("nsTransactionId")==200) {
                    i = new Intent(ListActivity.this, SalesTransactionActivity.class);
                    i.putExtra("nsId",item.getString("nsId"));
                    i.putExtra("nsModule","transactions_in_out");
                    i.putExtra("nsSubModule","transaction_sales");
                    i.putExtra("nsHideButtons","nsHideButtons");

                }else if(item.getInt("nsTransactionId")==300) {
                    i = new Intent(ListActivity.this, TransactionPurchaseActivity.class);
                    i.putExtra("nsId",item.getString("nsId"));
                    i.putExtra("nsModule","transactions_in_out");
                    i.putExtra("nsSubModule","transaction_purchase");
                    i.putExtra("nsHideButtons","nsHideButtons");
                }else if(item.getInt("nsTransactionId")==400) {
                    i = new Intent(ListActivity.this, ReceiptPaymentActivity.class);
                    i.putExtra("nsId",item.getString("nsId"));
                    i.putExtra("nsModule","transaction_receipt_payment");
                    i.putExtra("transactionType","400");
                }else if(item.getInt("nsTransactionId")==500) {
                    i = new Intent(ListActivity.this, ReceiptPaymentActivity.class);
                    i.putExtra("nsId",item.getString("nsId"));
                    i.putExtra("nsModule","transaction_receipt_payment");
                    i.putExtra("transactionType","500");
                }else{
                    return;
                }
                //Toast.makeText(this, "Hello "+item.getString("nsTransactionStatus"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            startActivity(i);


        }else if(nsModule.equals("transactions_stock_transfer")){
            Intent i = new Intent(ListActivity.this, StockTransferActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("master_ledger")){
            if(nsSubModule.equals("returnValue")) {
                Intent i = new Intent();
                try {
                    i.putExtra("nsName",item.getString("nsName"));
                    i.putExtra("nsId",item.getString("nsId"));
                    i.putExtra("nsIsBlocked",item.getString("nsIsBlocked"));
                    i.putExtra("nsIsGSTCustomer",item.getString("nsIsGSTCustomer"));
                    i.putExtra("nsIsRestricted",item.getString("nsIsRestricted"));
                    i.putExtra("nsIsCreditCustomer",item.getString("nsIsCreditCustomer"));
                    i.putExtra("nsLedgerCategoryId",item.getString("nsLedgerCategoryId"));
                    i.putExtra("nsBalance",item.getString("nsBalance"));
                    i.putExtra("nsStateCode",item.getString("nsStateCode"));

                    if(!item.isNull("nsCompany")) {
                        i.putExtra("nsCompany", item.getString("nsCompany"));
                        //Toast.makeText(this, ""+item.getString("nsCompany"), Toast.LENGTH_SHORT).show();
                    }else{
                        i.putExtra("nsCompany", "");
                    }

                    if(!item.isNull("nsGSTIN")) {
                        i.putExtra("nsGSTIN", item.getString("nsGSTIN"));
                        //Toast.makeText(this, ""+item.getString("nsCompany"), Toast.LENGTH_SHORT).show();
                    }else{
                        i.putExtra("nsGSTIN", "");
                    }

                    if(!item.isNull("nsAreaId")) {
                        i.putExtra("nsArea", item.getString("nsArea"));
                        i.putExtra("nsAreaId",item.getString("nsAreaId"));
                    }else{
                        i.putExtra("nsArea", "");
                        i.putExtra("nsAreaId", "");
                    }
                    if(!item.isNull("nsCityId")) {
                        i.putExtra("nsCity",item.getString("nsCity"));
                        i.putExtra("nsCityId",item.getString("nsCityId"));
                    }else{
                        i.putExtra("nsCity", "");
                        i.putExtra("nsCityId", "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK,i);
                finish();
            }else{
                Intent i = new Intent(ListActivity.this, MasterLedgerActivity.class);
                i.putExtra("nsModule", nsModule);
                try {
                    i.putExtra("nsId", item.getString("nsId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }

        }else if(nsModule.equals("master_inventory")){

            if(nsSubModule.equals("returnValue")){
                Intent i = new Intent();
                try {
                    i.putExtra("nsName",item.getString("nsNameDuringTransaction"));
                    i.putExtra("nsId",item.getString("nsId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK,i);
                finish();

            }else {
                Intent i = new Intent(ListActivity.this, MasterInventoryActivity.class);
                i.putExtra("nsModule", nsModule);
                try {
                    i.putExtra("nsId", item.getString("nsId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }

        }else if(nsModule.equals("master_modules")){
            Intent i = new Intent(ListActivity.this, MasterModules.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transaction_openingstock") || nsModule.equals("inventory_saftey_stock")){
            Intent i = new Intent(ListActivity.this, TransactionOpeningStock.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transaction_stock_management")){
            Intent i = new Intent(ListActivity.this, StockManagementActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales")){
            Intent i = new Intent(ListActivity.this, SalesTransactionActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
                if(item.getInt("nsStatus")==2){
                    i.putExtra("nsIsDeleted","nsIsDeleted");
                }
                i.putExtra("nsTransactionStatus",item.getString("nsTransactionStatus"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_order")){
            Intent i = new Intent(ListActivity.this, SalesOrderActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
                if(item.getInt("nsStatus")==2){
                    i.putExtra("nsIsDeleted","nsIsDeleted");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_purchase")){
            Intent i = new Intent(ListActivity.this, TransactionPurchaseActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
                if(item.getInt("nsStatus")==2){
                    i.putExtra("nsIsDeleted","nsIsDeleted");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("master_daily_stock_closing")){
            Intent i = new Intent(ListActivity.this, ClosingStockActivity.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("upi_payment_receipt")){
            try{
                if(item.getInt("nsId")>0) {
                    Intent i = new Intent(ListActivity.this, upiPaymentReceiptActivity.class);
                    i.putExtra("nsModule", nsModule);
                    i.putExtra("nsId", item.getString("nsId"));
                    startActivity(i);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_undelivered")){
            Intent i = new Intent(ListActivity.this, UndeliveredListActivity.class);
            i.putExtra("nsModule","offset_items");
            try {
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsVoucherNo",item.getString("nsVoucherNo"));
                i.putExtra("nsSalesDate",item.getString("nsDate"));
                i.putExtra("nsSalesLocationId",item.getString("nsLocationId"));
                i.putExtra("nsSalesLocationName",item.getString("nsLocationName"));
                //i.putExtra("nsArea",item.getString("nsArea"));
                i.putExtra("nsLedgerName",item.getString("nsLedgerName"));
                i.putExtra("nsCity",item.getString("nsCity"));
                i.putExtra("nsRemarks",item.getString("nsRemarks"));
                i.putExtra("nsTransportOrder",item.getString("nsTransportOrder"));
                i.putExtra("nsAdditionalCharges",item.getString("nsAdditionalCharges"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_sales_purchase_reminders")){
            Intent i = new Intent(ListActivity.this, UpdateReminderActivity.class);
            i.putExtra("nsModule","transaction_sales_purchase_reminders");
            try {
                i.putExtra("nsId",item.getString("nsId"));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(item.getString("nsReminderDate"));
                i.putExtra("nsDate",new SimpleDateFormat("dd-MM-yyyy").format(d));
                i.putExtra("nsSalesLocationId",item.getString("nsLocationId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("fetch_inventory_detailed_report")){
            try {
                if(item.getInt("nsTransactionTypeId")==600){
                    Intent i = new Intent(ListActivity.this, DeliveredActivity.class);
                    i.putExtra("nsModule","offset_items");

                    i.putExtra("nsId",item.getString("nsTransactionId"));
                    i.putExtra("nsVoucherNo",item.getString("nsVoucherNumber"));
                    i.putExtra("nsSalesDate",item.getString("nsDate"));
                    i.putExtra("nsSalesLocationId","");
                    i.putExtra("nsSalesLocationName","");
                    i.putExtra("nsLedgerName",item.getString("nsLedgerName"));
                    //i.putExtra("nsIsDeleted","nsIsDeleted");
                    i.putExtra("nsCity","");
                    i.putExtra("nsRemarks","");
                    startActivity(i);
                }else if(item.getInt("nsTransactionTypeId")==200){
                    Intent i = new Intent(ListActivity.this, SalesTransactionActivity.class);
                    i.putExtra("nsModule","transactions_in_out");
                    i.putExtra("nsId",item.getString("nsTransactionId"));
                    i.putExtra("nsIsDeleted","nsIsDeleted");
                    i.putExtra("nsTransactionStatus",1);

                    startActivity(i);
                }else if(item.getInt("nsTransactionTypeId")==300){
                    Intent i = new Intent(ListActivity.this, TransactionPurchaseActivity.class);
                    i.putExtra("nsModule","transactions_in_out");
                    i.putExtra("nsId",item.getString("nsTransactionId"));
                    i.putExtra("nsIsDeleted","nsIsDeleted");
                    startActivity(i);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }else if(nsModule.equals("transactions_in_out") && nsSubModule.equals("transaction_delivered")){
            Intent i = new Intent(ListActivity.this, DeliveredActivity.class);
            i.putExtra("nsModule","offset_items");
            try {
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsVoucherNo",item.getString("nsVoucherNo"));
                i.putExtra("nsSalesDate",item.getString("nsDate"));
                i.putExtra("nsSalesLocationId",item.getString("nsLocationId"));
                i.putExtra("nsSalesLocationName",item.getString("nsLocationName"));
                //i.putExtra("nsArea",item.getString("nsArea"));
                i.putExtra("nsLedgerName",item.getString("nsLedgerName"));
                i.putExtra("nsCity",item.getString("nsCity"));
                i.putExtra("nsRemarks",item.getString("nsRemarks"));
            }catch (JSONException e){
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("transaction_ledger_opening_balance")){
            Intent i = new Intent(ListActivity.this, TransactionOpeningLedgerBalance.class);
            i.putExtra("nsModule",nsModule);
            try {
                i.putExtra("nsId",item.getString("nsId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }else if(nsModule.equals("master_inventory_subcategory")){
            Intent i = new Intent();
            try {
                i.putExtra("nsSubCategoryName",item.getString("nsName"));
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsCategoryId",item.getString("nsCategoryId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK,i);
            finish();
        }
    }
    //fetch_inventory_detailed_report
    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.setIconifiedByDefault(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        //refresh();
        displayData();
    }

    private void ExportDataToExcel() {
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(ListActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                //DialogUtility.showDialog(ListActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                JSONArray dataSalesExport = jsonObject.getJSONArray("dataSalesExport");

                                if (nsModule.equals("report_ledger_transaction")) {
                                    JSONArray myExportData=new JSONArray();
                                    JSONArray myExportDataItems = new JSONArray();
                                    for (int i = 0; i < jsonData.length(); i++) {

                                        JSONObject exportObject = new JSONObject();
                                        if(jsonData.getJSONObject(i).getString("nsDate").equals("")){
                                            exportObject.put("DATE", "");
                                        }else {
                                            Date d = ConvertFunctions.convertDatabaseDateStringToDate(jsonData.getJSONObject(i).getString("nsDate"));
                                            exportObject.put("DATE", new SimpleDateFormat("dd-MM-yyyy").format(d));
                                        }

                                        exportObject.put("VNO",jsonData.getJSONObject(i).getString("nsVoucherno"));
                                        exportObject.put("LOCATION",jsonData.getJSONObject(i).getString("nsLocationName")+" - "+jsonData.getJSONObject(i).getString("nsTransactionType"));
                                        exportObject.put("ITEM","");
                                        exportObject.put("QUANTITY","");
                                        exportObject.put("RATE","");
                                        exportObject.put("AMT","");
                                        exportObject.put("ADDITIONAL CHARGES",jsonData.getJSONObject(i).getString("nsAdditionalCharges"));
                                        exportObject.put("TOTAL AMOUNT",jsonData.getJSONObject(i).getString("nsTotalAmount"));
                                        exportObject.put("PAID AMOUNT",jsonData.getJSONObject(i).getString("nsPaidAmount"));
                                        exportObject.put("BALANCE AMOUNT",jsonData.getJSONObject(i).getString("nsBalanceAmount"));
                                        exportObject.put("RUNNING TOTAL",jsonData.getJSONObject(i).getString("nsRunningTotal"));
                                        exportObject.put("REMARKS",jsonData.getJSONObject(i).getString("nsRemarks"));
                                        exportObject.put("nsTransactionId",jsonData.getJSONObject(i).getString("nsTransactionId"));
                                        exportObject.put("nsId",jsonData.getJSONObject(i).getString("nsId"));

                                        myExportData.put(exportObject);
                                    }

                                    for (int i = 0; i < dataSalesExport.length(); i++) {

                                        JSONObject exportObject = new JSONObject();
                                        exportObject.put("nsId",dataSalesExport.getJSONObject(i).getString("nsInOutId"));
                                        exportObject.put("nsNameDuringTransaction",dataSalesExport.getJSONObject(i).getString("nsNameDuringTransaction"));
                                        exportObject.put("nsQuantity",dataSalesExport.getJSONObject(i).getString("nsQuantity"));
                                        exportObject.put("nsPrice",dataSalesExport.getJSONObject(i).getString("nsPrice"));
                                        exportObject.put("nsAmt",dataSalesExport.getJSONObject(i).getString("nsAmt"));

                                        myExportDataItems.put(exportObject);
                                    }

                                    //DialogUtility.showDialog(ListActivity.this,myExportData.toString(4),"");
                                    ExportDataGeneral.exportDetailedLedgerReportToCSV(ListActivity.this,myExportData,myExportDataItems,nsName+" "+nsCity+" Ledger Detailed Report");
                                }
                                progress.dismiss();

                            }
                        }  catch (Exception e) {
                            //lblMessage.setText(e.getMessage());
                            progress.dismiss();
                            Toast.makeText(ListActivity.this, "expec "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rakesh123242523", error.getCause()+" @\n "+error.getLocalizedMessage()+" @\n "+error.networkResponse+" VolleyError: \n" + error.getNetworkTimeMs());
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


                if(nsModule.equals("report_ledger_transaction")){
                    params.put("module", "master_reports");
                    params.put("functionality", "fetch_ledger_transaction_report");
                    params.put("prLedgerId", nsId);
                    params.put("prDataForExport", "1");

                    if(!filterIntent.getStringExtra("fromDate").equals("")) {
                        Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                        params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                    }else{
                        params.put("datefrom", "2020-01-01");
                    }

                    if(!filterIntent.getStringExtra("nsTransactionType").equals("0")){
                        String tranType = "0";

                        if(filterIntent.getStringExtra("nsTransactionType").equals("1")){
                            params.put("prTransactionType","200");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("2")){
                            params.put("prTransactionType","400");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("3")){
                            params.put("prTransactionType","300");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("4")){
                            params.put("prTransactionType","500");
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("5")){
                            params.put("prTransactionType","600"); //Purchase Return
                        }

                        if(filterIntent.getStringExtra("nsTransactionType").equals("6")){
                            params.put("prTransactionType","700"); //Sales Return
                        }

                    }

                    if(!filterIntent.getStringExtra("toDate").equals("")) {
                        Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                    }else{
                        Date date;
                        Calendar cal=Calendar.getInstance();
                        date = cal.getTime();
                        params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(date));
                    }

                }


                Log.d("Rakeshsudirrrr",params.toString());
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
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }
}
