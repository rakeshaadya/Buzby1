package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayReportActivity extends BaseActivity {

    TextInputEditText txtSalesCash,txtPurchaseCash,txtExpenseCash,txtReceiptCash,txtPaymentCash,txtOpeningCash,txtTotalCash;
    TextInputEditText txtSalesCredit,txtPurchaseCredit,txtReceiptCredit,txtPaymentCredit,txtTotalCredit;
    TextInputEditText txtSalesTotal,txtPurchaseTotal,txtExpenseTotal,txtReceiptTotal,txtPaymentTotal,txtTotal;

    private ProgressDialog progress;
    String nsModule,nsId;

    String strResponse="";

    BottomNavigationView bottomNavigationView;
    Intent filterIntent=new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_report);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");

        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Day Report");
        setSupportActionBar(toolbar);

        txtSalesCash = (TextInputEditText) findViewById(R.id.txtSalesCash);
        txtPurchaseCash = (TextInputEditText) findViewById(R.id.txtPurchaseCash);
        txtExpenseCash = (TextInputEditText) findViewById(R.id.txtExpenseCash);
        txtReceiptCash = (TextInputEditText) findViewById(R.id.txtReceiptCash);
        txtPaymentCash = (TextInputEditText) findViewById(R.id.txtPaymentCash);
        txtOpeningCash = (TextInputEditText) findViewById(R.id.txtOpeningCash);
        txtTotalCash = (TextInputEditText) findViewById(R.id.txtTotalCash);
        txtSalesCredit = (TextInputEditText) findViewById(R.id.txtSalesCredit);
        txtPurchaseCredit = (TextInputEditText) findViewById(R.id.txtPurchaseCredit);
        txtReceiptCredit = (TextInputEditText) findViewById(R.id.txtReceiptCredit);
        txtPaymentCredit = (TextInputEditText) findViewById(R.id.txtPaymentCredit);
        txtTotalCredit = (TextInputEditText) findViewById(R.id.txtTotalCredit);
        txtSalesTotal = (TextInputEditText) findViewById(R.id.txtSalesTotal);
        txtPurchaseTotal = (TextInputEditText) findViewById(R.id.txtPurchaseTotal);
        txtExpenseTotal = (TextInputEditText) findViewById(R.id.txtExpenseTotal);
        txtReceiptTotal = (TextInputEditText) findViewById(R.id.txtReceiptTotal);
        txtPaymentTotal = (TextInputEditText) findViewById(R.id.txtPaymentTotal);
        txtTotal = (TextInputEditText) findViewById(R.id.txtTotal);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        Date date;
        Calendar cal=Calendar.getInstance();
        date = cal.getTime();
        filterIntent.putExtra("nsModule", nsModule);
        filterIntent.putExtra("nsUserId", preferenceConfig.readUserId());
        filterIntent.putExtra("nsUser", preferenceConfig.readUserName());
        filterIntent.putExtra("nsLocationId", preferenceConfig.readLocationId());
        filterIntent.putExtra("nsLocation", preferenceConfig.readlocationName());
        filterIntent.putExtra("sortType", "radioDescending");
        filterIntent.putExtra("toDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
        filterIntent.putExtra("toDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));
        filterIntent.putExtra("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(date));
        filterIntent.putExtra("fromDate1",new SimpleDateFormat("EEE, MMM dd yyyy").format(date));



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.action_filter:
                        Intent i = new Intent(DayReportActivity.this, SortAndFilterActivity.class);
                        i.putExtras(filterIntent.getExtras());
                        startActivityForResult(i, 1);
                        break;

                    case R.id.action_excel:

                        break;

                    case R.id.action_reprint:
                        printString.dayReportPrintString(strResponse);
                    break;
                }
                return true;
            }
        });

        bottomNavigationView.getMenu().findItem(R.id.action_reprint).setVisible(true);

        fetchDataById();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DayReportActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(resultCode == Activity.RESULT_OK){
                filterIntent=data;

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void fetchDataById(){
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        //DialogUtility.showDialog(DayReportActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(DayReportActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray dataOpeningCash = jsonObject.getJSONArray("dataOpeningCash");
                                JSONArray dataCashExpense = jsonObject.getJSONArray("dataCashExpense");
                                JSONArray dataReceiptCash = jsonObject.getJSONArray("dataReceiptCash");
                                JSONArray dataReceiptCredit = jsonObject.getJSONArray("dataReceiptCredit");
                                JSONArray dataPaymentCash = jsonObject.getJSONArray("dataPaymentCash");
                                JSONArray dataPaymentCredit = jsonObject.getJSONArray("dataPaymentCredit");
                                JSONArray dataPurchaseCash = jsonObject.getJSONArray("dataPurchaseCash");
                                JSONArray dataPurchaseCredit = jsonObject.getJSONArray("dataPurchaseCredit");
                                JSONArray dataSalesCash = jsonObject.getJSONArray("dataSalesCash");
                                JSONArray dataSalesCredit = jsonObject.getJSONArray("dataSalesCredit");


                                JSONObject dataCashExpense1 = dataCashExpense.getJSONObject(0);
                                txtExpenseCash.setText(""+dataCashExpense1.getDouble("nsCashExpense"));
                                double expenseCash = dataCashExpense1.getDouble("nsCashExpense");

                                JSONObject dataOpeningCash1 = dataOpeningCash.getJSONObject(0);
                                txtOpeningCash.setText(""+dataOpeningCash1.getDouble("nsOpeningCash"));
                                double openingCash = dataOpeningCash1.getDouble("nsOpeningCash");

                                JSONObject dataReceiptCash1 = dataReceiptCash.getJSONObject(0);
                                txtReceiptCash.setText(""+dataReceiptCash1.getDouble("nsReceiptCash"));
                                double ReceiptCash = dataReceiptCash1.getDouble("nsReceiptCash");


                                JSONObject dataReceiptCredit1 = dataReceiptCredit.getJSONObject(0);
                                txtReceiptCredit.setText(""+dataReceiptCredit1.getDouble("nsReceiptCredit"));
                                double ReceiptCredit = dataReceiptCredit1.getDouble("nsReceiptCredit");

                                JSONObject dataPaymentCash1 = dataPaymentCash.getJSONObject(0);
                                txtPaymentCash.setText(""+dataPaymentCash1.getDouble("nsPaymentCash"));
                                double PaymentCash = dataPaymentCash1.getDouble("nsPaymentCash");

                                JSONObject dataPaymentCredit1 = dataPaymentCredit.getJSONObject(0);
                                txtPaymentCredit.setText(""+dataPaymentCredit1.getDouble("nsPaymentCredit"));
                                double paymentCredit = dataPaymentCredit1.getDouble("nsPaymentCredit");

                                JSONObject dataPurchaseCash1 = dataPurchaseCash.getJSONObject(0);
                                txtPurchaseCash.setText(""+dataPurchaseCash1.getDouble("nsPurchaseCash"));
                                double PurchaseCash = dataPurchaseCash1.getDouble("nsPurchaseCash");

                                JSONObject dataPurchaseCredit1 = dataPurchaseCredit.getJSONObject(0);
                                txtPurchaseCredit.setText(""+dataPurchaseCredit1.getDouble("nsPurchaseCredit"));
                                double purchaseCredit = dataPurchaseCredit1.getDouble("nsPurchaseCredit");

                                JSONObject dataSalesCash1 = dataSalesCash.getJSONObject(0);
                                txtSalesCash.setText(""+dataSalesCash1.getDouble("nsSalesCash"));
                                double salesCash = dataSalesCash1.getDouble("nsSalesCash");

                                JSONObject dataSalesCredit1 = dataSalesCredit.getJSONObject(0);
                                txtSalesCredit.setText(""+dataSalesCredit1.getDouble("nsSalesCredit"));
                                double salesCredit = dataSalesCredit1.getDouble("nsSalesCredit");

                                txtTotalCash.setText(""+(openingCash+salesCash+ReceiptCash-PaymentCash-expenseCash-PurchaseCash));

                                txtSalesTotal.setText(""+(salesCash+salesCredit));
                                txtReceiptTotal.setText(""+(ReceiptCash+ReceiptCredit));
                                txtPurchaseTotal.setText(""+(purchaseCredit+PurchaseCash));
                                txtPaymentTotal.setText(""+(PaymentCash+paymentCredit));
                                txtExpenseTotal.setText(""+(expenseCash));

                                strResponse = response;

                                /*bottomNavigationView.getMenu().findItem(R.id.action_excel).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        //printString.expensePrintString(strResponse);

                                        //Toast.makeText(DayReportActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                });*/

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(DayReportActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(DayReportActivity.this,"Error: Please Check your Internet Connection","Alert");
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
                params.put("module", "master_reports");
                params.put("functionality", "fetch_detailed_day_report");

                if(!filterIntent.getStringExtra("fromDate").equals("")) {
                    Date dateFrom = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("fromDate"));
                    params.put("datefrom", new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
                }

                if(!filterIntent.getStringExtra("toDate").equals("")) {
                    Date dateTo = ConvertFunctions.convertIndianDateStringToDate(filterIntent.getStringExtra("toDate"));
                    params.put("dateto", new SimpleDateFormat("yyyy-MM-dd").format(dateTo));
                }

                //params.put("sortfilter",filterIntent.getStringExtra("sortType"));

                if(!filterIntent.getStringExtra("nsUserId").equals("")) {
                    params.put("prUserId", filterIntent.getStringExtra("nsUserId"));
                }
                if(!filterIntent.getStringExtra("nsLocationId").equals("")) {
                    params.put("prLocationId", filterIntent.getStringExtra("nsLocationId"));
                    params.put("nsLocation", filterIntent.getStringExtra("nsLocation"));
                }

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
    public void onRestart(){
        super.onRestart();
        //refresh();
        fetchDataById();
    }
}