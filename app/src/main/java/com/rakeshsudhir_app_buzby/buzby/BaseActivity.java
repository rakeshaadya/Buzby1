package com.rakeshsudhir_app_buzby.buzby;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener  {

    DrawerLayout drawer;
    NavigationView navigationView;
    SharedPreferenceConfig preferenceConfig;
    String locationName;
    TextView txtlocation;
    //String JSON;

    String strData="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        preferenceConfig=new SharedPreferenceConfig(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);



        Menu menu = navigationView.getMenu();

        MenuItem headMaster= menu.findItem(R.id.headMaster);
        SpannableString s = new SpannableString(headMaster.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        headMaster.setTitle(s);

        MenuItem headTransaction= menu.findItem(R.id.headTransaction);
        SpannableString s1 = new SpannableString(headTransaction.getTitle());
        s1.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s1.length(), 0);
        s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
        headTransaction.setTitle(s1);

        MenuItem headGeneral= menu.findItem(R.id.headGeneral);
        SpannableString s2 = new SpannableString(headGeneral.getTitle());
        s2.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s2.length(), 0);
        s2.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s2.length(), 0);
        headGeneral.setTitle(s2);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        View headView = navigationView.getHeaderView(0);

        TextView tv_company_caption = headView.findViewById(R.id.tv_company_caption);

        TextView tv_user_name = (TextView) headView.findViewById(R.id.tv_user_name);

        txtlocation = (TextView) headView.findViewById(R.id.location);

        if(preferenceConfig.readlocationName().equals("") || preferenceConfig.readLocationId().equals("")){
            displayLocationToSetasDefault();
        }else{
            txtlocation.setText(preferenceConfig.readlocationName());
        }

        TextView lblversionCode = (TextView) headView.findViewById(R.id.lblversionCode);

        tv_user_name.setText("Welcome "+preferenceConfig.readUserName());

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        lblversionCode.setText("Version: " + version);

        Menu nav_menu = navigationView.getMenu();
        if(!preferenceConfig.readUserPosition().equals("1")){
            nav_menu.findItem(R.id.nav_user_role_management).setVisible(false);
            nav_menu.findItem(R.id.nav_android_id).setVisible(false);
            nav_menu.findItem(R.id.nav_users).setVisible(false);
            nav_menu.findItem(R.id.nav_position).setVisible(false);
        }

        if(preferenceConfig.readUserId().equals("1")){
            nav_menu.findItem(R.id.nav_module).setVisible(true);
        }

        if(Integer.parseInt(preferenceConfig.readUserPosition())>1){
            nav_menu.findItem(R.id.nav_consolidated_ledger_balance_report).setVisible(false);
            nav_menu.findItem(R.id.nav_OpeningStock).setVisible(false);
            nav_menu.findItem(R.id.nav_OpeningLedgerBalance).setVisible(false);
            nav_menu.findItem(R.id.nav_location).setVisible(false);
            nav_menu.findItem(R.id.nav_expense_head).setVisible(false);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuConnect = menu.findItem(R.id.menuConnect);
        menuConnect.setVisible(true);

        MenuItem menuSearch = menu.findItem(R.id.menuSearch);
        menuSearch.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuConnect) {

            Intent i = new Intent(BaseActivity.this, PrintActivity.class);
            startActivity(i);
            return true;
        }else if (id == R.id.menuAdd){

            View menuItemView = findViewById(R.id.menuAdd);
            onDateIconClicked(menuItemView);

            /*Intent i = new Intent(BaseActivity.this,SalesTransactionActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsId","-1");
            startActivity(i);*/

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void onDateIconClicked(View icon){
        PopupMenu popupMenu = new PopupMenu(BaseActivity.this, icon);

        popupMenu.inflate(R.menu.dash_shortcut_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_filter_sales_order:
                        Intent iso = new Intent(BaseActivity.this,SalesOrderActivity.class);
                        iso.putExtra("nsModule","transactions_in_out");
                        iso.putExtra("nsId","-1");
                        startActivity(iso);
                        break;
                    case R.id.menu_filter_sales:
                        Intent is = new Intent(BaseActivity.this,SalesTransactionActivity.class);
                        is.putExtra("nsModule","transactions_in_out");
                        is.putExtra("nsId","-1");
                        startActivity(is);

                        break;
                    case R.id.menu_filter_receipt:
                        Intent ir = new Intent(BaseActivity.this,ReceiptPaymentActivity.class);
                        ir.putExtra("nsModule","transaction_receipt_payment");
                        ir.putExtra("nsId","-1");
                        startActivity(ir);

                        break;
                    case R.id.menu_filter_undelivered:
                        Intent iu = new Intent(BaseActivity.this,ListActivity.class);
                        iu.putExtra("nsModule","transactions_in_out");
                        iu.putExtra("nsSubModule","transaction_undelivered");
                        iu.putExtra("nsId","-1");
                        startActivity(iu);

                        break;
                    case R.id.menu_filter_purchase:
                        Intent i = new Intent(BaseActivity.this,TransactionPurchaseActivity.class);
                        i.putExtra("nsModule","transactions_in_out");
                        i.putExtra("nsId","-1");
                        startActivity(i);


                        break;
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.show();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_ledger) {

            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_ledger");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        }else if (id == R.id.nav_Inventory) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_inventory");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        }else if (id == R.id.nav_location) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_location");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        }else if (id == R.id.nav_users) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_users");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        }else if (id == R.id.nav_user_role_management) {
            Intent i = new Intent(BaseActivity.this,UserPositionRolesList.class);
            i.putExtra("nsModule","master_user_role_management");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_OpeningStock) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transaction_openingstock");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_safteyStock) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","inventory_saftey_stock");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_stock_management) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transaction_stock_management");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);
        }else if (id == R.id.nav_OpeningLedgerBalance) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transaction_ledger_opening_balance");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_upi_payment_receipt) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","upi_payment_receipt");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_price_variation) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transaction_price_variation");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_StockTransfer) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_stock_transfer");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);
        }else if (id == R.id.nav_sales) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsSubModule","transaction_sales");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_sales_order) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsSubModule","transaction_sales_order");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_reminders) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsSubModule","transaction_sales_purchase_reminders");
            i.putExtra("nsId","-1");
            startActivity(i);

        }else if (id == R.id.nav_hamali_calculation) {
            Intent i = new Intent(BaseActivity.this,HamaliCalculation.class);
            i.putExtra("nsModule","master_hamali_calculation");
            startActivity(i);

        } /*else if (id == R.id.nav_sales_order) {

            Intent i = new Intent(BaseActivity.this,SalesOrderActivity.class);
            startActivity(i);

        }*/ else if (id == R.id.nav_salesUndelivered) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsSubModule","transaction_undelivered");
            i.putExtra("nsId","-1");
            startActivity(i);
        } else if (id == R.id.nav_salesDeliveredList) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsSubModule","transaction_delivered");
            i.putExtra("nsId","-1");
            startActivity(i);
        }else if (id == R.id.nav_consolidated_ledger_balance_report) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","report_consolidated_ledger_balance");
            i.putExtra("nsSubModule","master_reports");
            i.putExtra("nsId","-1");
            startActivity(i);
        }else if (id == R.id.nav_day_report) {
            Intent i = new Intent(BaseActivity.this,DayReportActivity.class);
            i.putExtra("nsModule","master_day_reports");
            i.putExtra("nsId","-1");
            startActivity(i);
        } else if (id == R.id.nav_receipt) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transaction_receipt_payment");
            i.putExtra("nsSubModule","0");
            i.putExtra("nsId","-1");
            startActivity(i);

        } else if (id == R.id.nav_purchase) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","transactions_in_out");
            i.putExtra("nsSubModule","transaction_purchase");
            i.putExtra("nsId","-1");
            startActivity(i);
        } /*else if (id == R.id.nav_payment) {

        } */else if (id == R.id.nav_online_balance) {
            Intent i = new Intent(BaseActivity.this,ConsolidateOnlineBalanceActivity.class);
            i.putExtra("nsModule","consolidated_online_balance_report");
            i.putExtra("nsSubModule","0");
            startActivity(i);
        } else if (id == R.id.nav_android_id) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","android_signedin_users");
            i.putExtra("nsSubModule","0");
            startActivity(i);
        }else if (id == R.id.nav_module) {
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_modules");
            i.putExtra("nsSubModule","0");
            startActivity(i);
        } else if (id == R.id.nav_expenses){
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_expense");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        }else if (id == R.id.nav_expense_head){
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_expense_head");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        }else if (id == R.id.nav_closing_stock){
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","master_daily_stock_closing");
            i.putExtra("nsSubModule","0");
            startActivity(i);
        }else if (id == R.id.nav_opening_cash){
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","opening_cash_balance");
            i.putExtra("nsSubModule","0");
            startActivity(i);
        }else if (id == R.id.nav_consolidated_inventory_report){
            Intent i = new Intent(BaseActivity.this,ReportConsolidatedInventoryActivity.class);
            i.putExtra("nsModule","consolidated_inventory_report");
            i.putExtra("nsSubModule","0");
            startActivity(i);
        }else if (id == R.id.nav_position){
            Intent i = new Intent(BaseActivity.this,ListActivity.class);
            i.putExtra("nsModule","user_position");
            i.putExtra("nsSubModule","0");
            startActivity(i);

        } else if (id == R.id.nav_set_default_location) {
            displayLocationToSetasDefault();

        } else if (id == R.id.nav_logout) {
            preferenceConfig.writeLoginStatus(false,"", "","","","","","");
            preferenceConfig.writeLocationDetails("","");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        //drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }

    public void insertData(final String nsId,
                                  final String arrInsertDetails,
                                  final String module, final String nsSubModule, final int printGP,
                                  final int printStatus, final int receiptPrintStatus){

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        final ProgressDialog progress;
        progress=new ProgressDialog(BaseActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setMessage("Saving Data...");
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        requestQueue.stop();
                        requestQueue.getCache().clear();
                        //DialogUtility.showDialog(BaseActivity.this,response,"");
                        Toast.makeText(BaseActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(BaseActivity.this,response,"Error");
                                progress.dismiss();
                            }else{

                                JSONArray jsonArray1  = new JSONArray(arrInsertDetails);
                                JSONObject object1 = jsonArray1.optJSONObject(0);

                                //Toast.makeText(preferenceConfig, "", Toast.LENGTH_SHORT).show();

                                if(Integer.parseInt(nsId)>0){
                                    /*if(module.equals("master_expense")) {
                                        functionPrint.displayDataForPrint(BaseActivity.this, module, nsId);
                                    }*/
                                    /*Intent intentPrint=new Intent();
                                    if(module.equals("transaction_sales")) {
                                        if(preferenceConfig.readPrintAfterSalesUpdate()==true) {
                                            intentPrint.putExtra("nsPrintName", "invoice");
                                            intentPrint.putExtra("nsPrintId", response);
                                        }

                                    }else if(module.equals("company_details")){
                                        JSONArray jsonArray  = new JSONArray(arrInsertDetails);
                                        JSONObject object = jsonArray.optJSONObject(0);

                                        preferenceConfig.writeCompanyDetails(object.getString("param1"),
                                                object.getString("prPrintAfterSalesCreate"),
                                                object.getString("prPrintAfterSalesUpdate"),
                                                object.getString("prEnableMeasurementTakenBy"),
                                                object.getString("prEnableSubCustomer"),
                                                object.getString("prEnableTrialDate"),
                                                object.getString("prSMSAfterSales"),
                                                object.getString("prSMSAfterDelivery"));
                                    }*/
                                    //activity.setResult(Activity.RESULT_OK, intentPrint);
                                    progress.dismiss();
                                    finish();
                                }else{
                                    if(
                                        module.equals("master_user_role_management")
                                        || module.equals("master_expense")
                                    ){
                                        if(module.equals("master_expense")) {
                                            functionPrint.displayDataForPrint(BaseActivity.this, module, response,receiptPrintStatus,2,2,1,1);
                                        }
                                        finish();
                                    }else if(module.equals("transactions_in_out")){
                                        if(object1.getInt("prConvertToSales")==1) {
                                            Intent i = new Intent(BaseActivity.this, ListActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            i.putExtra("nsModule", "transactions_in_out");
                                            i.putExtra("nsSubModule", "transaction_sales");
                                            i.putExtra("nsId", "-1");
                                            startActivity(i);
                                        }else{
                                            if(printStatus==1 || printGP==1) {
                                                if(receiptPrintStatus==1) {
                                                    functionPrint.displayDataForPrint(BaseActivity.this, "transaction_receipt_payment", response, receiptPrintStatus,2,2,printStatus,printGP);
                                                }
                                                functionPrint.displayDataForPrint(BaseActivity.this, module, response,receiptPrintStatus,2,2,printStatus,printGP);
                                            }/*else if(printStatus==10){
                                                functionPrint.displayDataForPrint(BaseActivity.this, module, response,receiptPrintStatus,2,1,printStatus,printGP);
                                            }*/
                                            finish();
                                            Intent returnIntent = getIntent();
                                            startActivity(returnIntent);
                                        }
                                    }else if(
                                            module.equals("transaction_receipt_payment")
                                            || module.equals("offset_items")
                                    ){

                                        if(module.equals("transaction_receipt_payment")) {
                                            if(printStatus==1) {
                                                functionPrint.displayDataForPrint(BaseActivity.this, module, response,receiptPrintStatus,2,2,1,1);
                                            }
                                            //Toast.makeText(BaseActivity.this, ""+, Toast.LENGTH_SHORT).show();
                                        }

                                        finish();
                                    }else if(module.equals("transaction_stock_management")){
                                        if(object1.getString("prReturnToList").equals("return")) {
                                            finish();
                                        }else{
                                            finish();
                                            Intent returnIntent = getIntent();
                                            startActivity(returnIntent);
                                        }
                                    }
                                    else if(module.equals("master_daily_stock_closing")){
                                        if(nsSubModule.equals("returnToStockReport")) {
                                            finish();
                                        }else {
                                            finish();
                                            Intent returnIntent = getIntent();
                                            returnIntent.putExtra("nsStockType", object1.getString("prStockType"));
                                            returnIntent.putExtra("nsStockTypeId", object1.getString("prTypeId"));
                                            startActivity(returnIntent);
                                        }
                                    }else if(module.equals("upi_payment_receipt")){
                                        finish();
                                    }else{
                                        finish();
                                        Intent returnIntent = getIntent();
                                        startActivity(returnIntent);
                                    }

                                    progress.dismiss();

                                    /*if(module.equals("transaction_sales")){
                                        if(preferenceConfig.readPrintAfterSalesCreate()==true){
                                            returnIntent.putExtra("nsPrintId", response);
                                        }
                                        if(preferenceConfig.readSMSAfterSales()==true){
                                            JSONArray jsonArray  = new JSONArray(arrInsertDetails);
                                            JSONObject object = jsonArray.optJSONObject(0);
                                            //returnIntent.putExtra("nsSMSTo",object.getString("nsCustomerMobile"));
                                            String message="We thank you for placing an order with us. We will inform you once your items are ready.\n\nRegards\n" + preferenceConfig.readCompanyName();
                                            SMSUtility smsUtility=new SMSUtility(activity,object.getString("nsCustomerMobile"),message);
                                            smsUtility.sendSMS();
                                        }
                                    }*/
                                    //startActivity(returnIntent);
                                }

                            }

                        }  catch (Exception e) {
                            Toast.makeText(BaseActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(BaseActivity.this,"Error: Please Check your Internet Connection","Error");
                }else{
                    Toast.makeText(BaseActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("module", module);
                if(Integer.parseInt(nsId)>0){
                    params.put("functionality", "edit");
                    params.put("id", nsId);
                }else{
                    params.put("functionality", "add");
                }


                /*if(module.equals("offset_items")){
                    params.put("prCartItems", arrInsertDetails.toString());
                }else {*/
                    try {
                        JSONArray jsonArray = new JSONArray(arrInsertDetails);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.optJSONObject(i);
                            Iterator<String> iterator = object.keys();
                            while (iterator.hasNext()) {
                                String currentKey = iterator.next();
                                params.put(currentKey, object.getString(currentKey));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("RRakeshSudhirrr", "" + e.getMessage());
                    }
                //}


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

        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void insertListData(
                           final String arrInsertDetails,
                           final String module,final double noOfCopies, final int printGP,
                           final int printStatus, final int receiptPrintStatus){

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        final ProgressDialog progress;
        progress=new ProgressDialog(BaseActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setMessage("Saving Data...");
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        requestQueue.stop();
                        requestQueue.getCache().clear();
                        //DialogUtility.showDialog(BaseActivity.this,response,"");
                        //Toast.makeText(BaseActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(BaseActivity.this,response,"Error");
                                progress.dismiss();
                            }else{
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray1  = jsonObject.getJSONArray("dataArray");
                                //JSONObject object1 = jsonArray1.optJSONObject(0);


                                    for (int i=0;i<jsonArray1.length();i++) {

                                        //JSONObject object1 = jsonArray1.optJSONObject(i);

                                        //DialogUtility.showDialog(BaseActivity.this, jsonArray1.getString(i),"asdf rakesh");

                                        //object1.

                                        if (module.equals("transactions_in_out")) {
                                            functionPrint.displayDataForPrint(BaseActivity.this,
                                                    module,
                                                    jsonArray1.getString(i),
                                                    2,
                                                    2,
                                                    2,
                                                    printStatus,
                                                    printGP);
                                        }
                                    }

                                    progress.dismiss();

                            }

                        }  catch (Exception e) {
                            Toast.makeText(BaseActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(BaseActivity.this,"Error: Please Check your Internet Connection","Error");
                }else{
                    Toast.makeText(BaseActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("module", module);

                    params.put("functionality", "addListEntries");
                    if(noOfCopies>0) {
                        params.put("prNoOfCopies", String.valueOf(noOfCopies));
                    }



                /*if(module.equals("offset_items")){
                    params.put("prCartItems", arrInsertDetails.toString());
                }else {*/
                try {
                    JSONArray jsonArray = new JSONArray(arrInsertDetails);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.optJSONObject(i);
                        Iterator<String> iterator = object.keys();
                        while (iterator.hasNext()) {
                            String currentKey = iterator.next();
                            params.put(currentKey, object.getString(currentKey));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("RRakeshSudhirrr", "" + e.getMessage());
                }
                //}


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

        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }


    public void restoreDataSalesOrder(

            final String module, final String nsId){

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        final ProgressDialog progress;
        progress=new ProgressDialog(BaseActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setMessage("Saving Data...");
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        requestQueue.stop();
                        requestQueue.getCache().clear();

                        //Toast.makeText(BaseActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(BaseActivity.this,response,"Error");
                                progress.dismiss();
                            }else{

                                Toast.makeText(BaseActivity.this, "Restored", Toast.LENGTH_SHORT).show();
                                finish();


                                Intent i = new Intent(BaseActivity.this,ListActivity.class);
                                i.putExtra("nsModule","transactions_in_out");
                                i.putExtra("nsSubModule","transaction_sales_order");
                                i.putExtra("nsId","-1");
                                startActivity(i);


                                progress.dismiss();

                            }

                        }  catch (Exception e) {
                            Toast.makeText(BaseActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(BaseActivity.this,"Error: Please Check your Internet Connection","Error");
                }else{
                    Toast.makeText(BaseActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("module", module);
                params.put("nsId", nsId);

                params.put("functionality", "restore");




                /*if(module.equals("offset_items")){
                    params.put("prCartItems", arrInsertDetails.toString());
                }else {*/

                //}


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

        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void deleteData(final String nsId,
                                  final String parentId,
                                  final String module,
                                  final int subModuleId
                           ) {

        final ProgressDialog progress;
        progress=new ProgressDialog(BaseActivity.this);
        //progress.setMessage("Downloading Music");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        //progress.setProgress(0);
        progress.show();
        progress.setMessage("Deleting Data...");

        AlertDialog.Builder alert = new AlertDialog.Builder(BaseActivity.this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
                //final TextView lblText = (TextView) BaseActivity.this.findViewById(R.id.lblMessage);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Log.d("Rakesh12345", "onResponse: "+response);
                                //DialogUtility.showDialog(BaseActivity.this,response,"");
                                try {
                                    if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                        //lblText.setText(response);
                                        DialogUtility.showDialog(BaseActivity.this,response,"Error");
                                        progress.dismiss();
                                    }else{

                                        /*if(module.equals("master_ledger") || module.equals("employee")){
                                            progress.dismiss();
                                            finish();
                                            startActivity(getIntent());
                                        }else{*/
                                            progress.dismiss();
                                            finish();
                                        //}
                                    }

                                }  catch (Exception e) {
                                    Toast.makeText(BaseActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.getMessage().startsWith("java.net.ConnectException")){
                            DialogUtility.showDialog(BaseActivity.this,"Error: Please Check your Internet Connection","Error");
                        }else{
                            Toast.makeText(BaseActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                        params.put("module", module);

                        if(Integer.parseInt(parentId)>0){
                            params.put("id", nsId);
                            params.put("parentId", parentId);
                        }else{
                            params.put("id", nsId);
                        }

                        params.put("subModuleId", String.valueOf(subModuleId));



                        params.put("functionality", "delete");
                        Log.d("RRakeshSudhir",params.toString());
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

                RequestQueue requestQueue = Volley.newRequestQueue(BaseActivity.this);
                //adding the string request to request queue
                requestQueue.add(stringRequest);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                progress.dismiss();
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void displayLocationToSetasDefault() {

        final ProgressDialog progress;
        progress=new ProgressDialog(BaseActivity.this);
        //progress.setMessage("Downloading Music");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        //progress.setProgress(0);
        progress.show();
        progress.setMessage("Loading...");

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(BaseActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                progress.dismiss();
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonData = jsonObject.getJSONArray("data");

                                LayoutInflater inflater = getLayoutInflater();
                                View alertLayout = inflater.inflate(R.layout.activity_location_dialog, null);

                                final AppCompatSpinner spinner = alertLayout.findViewById(R.id.txtChooseItem);
                                final TextView btnSave = alertLayout.findViewById(R.id.btnSave);

                                spinner.setOnItemSelectedListener(BaseActivity.this);

                                final String[] location = new String[jsonData.length()+1];
                                location[0]="Select";

                                for(int i=0;i<jsonData.length();i++){
                                    JSONObject objData = jsonData.getJSONObject(i);
                                    location[i+1]=objData.getString("nsName");
                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BaseActivity.this, android.R.layout.simple_spinner_item, location){
                                    @Override
                                    public View getDropDownView(int position, View convertView,
                                                                ViewGroup parent) {
                                        View view = super.getDropDownView(position, convertView, parent);
                                        TextView tv = (TextView) view;
                                        if(position == 0){
                                            // Set the hint text color gray
                                            tv.setTextColor(Color.GRAY);
                                        }
                                        else {
                                            tv.setTextColor(Color.BLACK);
                                        }
                                        return view;
                                    }
                                };

                                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                                spinner.setAdapter(dataAdapter);

                                Toolbar toolbarFilter = alertLayout.findViewById(R.id.myFilterToolbar);
                                AlertDialog.Builder alert = new AlertDialog.Builder(BaseActivity.this);
                                toolbarFilter.setTitle("Select Default Branch");
                                alert.setCancelable(false);
                                alert.setView(alertLayout);

                                final AlertDialog dialog = alert.create();
                                dialog.show();

                                btnSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        boolean isValidated = true;
                                        if(spinner.getSelectedItemPosition()==0){
                                            DialogUtility.showDialog(BaseActivity.this,"Invalid Selection. \nPlease Select Correct Location from Dropdown","Error");
                                            isValidated = false;
                                        }
                                        if(isValidated==false){
                                            return;
                                        }

                                        for(int i=0;i<jsonData.length();i++){
                                            JSONObject objData = null;
                                            try {
                                                objData = jsonData.getJSONObject(i);
                                                if(locationName.equals(objData.getString("nsName"))){
                                                    preferenceConfig.writeLocationDetails(
                                                            objData.getString("nsName"),
                                                            objData.getString("nsId"));
                                                    txtlocation.setText(preferenceConfig.readlocationName());

                                                    TextView lblDetails;
                                                    lblDetails = (TextView)findViewById(R.id.lblDetails);
                                                    final Calendar cldr1 = Calendar.getInstance();
                                                    lblDetails.setText(preferenceConfig.readlocationName()+"\n"+new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));

                                                    DialogUtility.showDialog(BaseActivity.this,"Default Location Selected","Alert");
                                                    dialog.dismiss();
                                                    return;
                                                }/*else {
                                                    DialogUtility.showDialog(BaseActivity.this,"Invalid Selection. \nPlease Select Correct Location from Dropdown","Error");
                                                }*/
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            }
                        }  catch (Exception e) {
                            Log.d("Rakesh", "Exception "+e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(BaseActivity.this,"Error: Please Check your Internet Connection","Alert");
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

                params.put("module", "master_location");
                //params.put("initialAccessValues", "1");
                params.put("functionality", "fetch_typeahead_textboxes_data");

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

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public String fetchModuleNameAlias(String nam){
        String str="";
        if(nam.equals("master_location")){
            str="Location";
        }
        if(nam.equals("master_ledger")){
            str="Ledger";
        }
        if(nam.equals("master_area")){
            str="Area";
        }
        if(nam.equals("master_city")){
            str="City";
        }
        if(nam.equals("master_inventory")){
            str="Inventory";
        }
        if(nam.equals("master_inventory_category")){
            str="Inventory Category";
        }
        if(nam.equals("master_inventory_subcategory")){
            str="Inventory SubCategory";
        }
        if(nam.equals("master_brand")){
            str="Inventory Brand";
        }
        if(nam.equals("master_units")){
            str="Inventory Units";
        }
        return str;
    }

    public String fetchModuleName(String nam){
        String str="";
        if(nam.equals("Location")){
            str="master_location";
        }
        if(nam.equals("Ledger")){
            str="master_ledger";
        }
        if(nam.equals("Area")){
            str="master_area";
        }
        if(nam.equals("City")){
            str="master_city";
        }
        if(nam.equals("Inventory")){
            str="master_inventory";
        }
        if(nam.equals("Inventory Category")){
            str="master_inventory_category";
        }
        if(nam.equals("Inventory SubCategory")){
            str="master_inventory_subcategory";
        }
        if(nam.equals("Inventory Brand")){
            str="master_brand";
        }
        if(nam.equals("Inventory Units")){
            str="master_units";
        }

        return str;
    }


    /*private void displayAreaCityArray() {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                //lblMessage.setText(response);
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                            }
                        }  catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company",preferenceConfig.readCompany());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("module", "hf_master_imei");
                params.put("functionality", "fetch_table_data");


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
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }*/


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        locationName = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}