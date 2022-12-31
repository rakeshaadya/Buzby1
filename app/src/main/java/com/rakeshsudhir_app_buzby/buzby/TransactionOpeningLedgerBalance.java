package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransactionOpeningLedgerBalance extends BaseActivity {

    DatePickerDialog datePickerDialog;
    private ProgressDialog progress;
    Button btnSave,btnDelete;
    AppCompatAutoCompleteTextView txtDate;
    AppCompatAutoCompleteTextView txtAmount;
    AppCompatAutoCompleteTextView txtLedgerName;

    String ledgerid="0",ledgerName="";
    String nsSubModule="0";
    String nsModule="",nsId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_opening_ledger_balance);

        init();
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
        toolbar.setTitle("O/P Ledger Balance");

        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,TransactionOpeningLedgerBalance.this,txtDate);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                boolean isValidated = true;

                if(txtLedgerName.getText().toString().trim().equals("") || ledgerid.equals("0")){
                    txtLedgerName.setError("Invalid");
                    isValidated=false;
                }else if(txtAmount.getText().toString().trim().equals("")){
                    txtAmount.setError("Invalid");
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    Date d = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString());
                    objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(d));
                    objItems.put("prLedgerId", ledgerid);
                    objItems.put("prAmount", txtAmount.getText().toString());

                    jsonArrObjItems.put(objItems);

                    //DialogUtility.showDialog(ReceiptPaymentActivity.this,jsonArrObjItems.toString(4),"");
                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(Integer.parseInt(nsId)>0){
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            displayData(nsId);
        }

        txtLedgerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionOpeningLedgerBalance.this,ListActivity.class);
                i.putExtra("nsModule","master_ledger");
                i.putExtra("nsSubModule", "returnValue");
                i.putExtra("ledgerCategory", "1");
                startActivityForResult(i,1);
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
                Intent i = new Intent(TransactionOpeningLedgerBalance.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
    }

    public void init(){
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        txtDate = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDate);
        txtAmount = (AppCompatAutoCompleteTextView) findViewById(R.id.txtAmount);
        txtLedgerName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtLedgerName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //vehicleNo = data.getStringExtra("nsRegistrationNumber");
                ledgerid = data.getStringExtra("nsId");


                txtLedgerName.setText(data.getStringExtra("nsName"));
                txtLedgerName.setError(null);
            }
            if (requestCode == RESULT_CANCELED) {

            }
        }
    }

    private void displayData(final String id) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                // JSONArray jsonColSettings = jsonObject.getJSONArray("dataColSettings");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);


                                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                                    txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));

                                    ledgerid=objData.getString("nsLedgerId");
                                    txtLedgerName.setText(objData.getString("nsName"));

                                    txtAmount.setText(objData.getString("nsAmount"));

                                }
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            //swipeRefreshLayout.setRefreshing(false);
                            //lblMessage.setText(e.getMessage());
                            progress.dismiss();
                        }
                        //swipeRefreshLayout.setRefreshing(false);
                        //progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText(error.getMessage());
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
                params.put("functionality", "fetch_table_data");
                params.put("id",id);

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

}