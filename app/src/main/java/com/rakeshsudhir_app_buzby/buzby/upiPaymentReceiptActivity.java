package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class upiPaymentReceiptActivity extends BaseActivity {

    DatePickerDialog datePickerDialog;
    private ProgressDialog progress;
    Button btnSave,btnDelete;
    AppCompatAutoCompleteTextView txtDate;
    AppCompatAutoCompleteTextView txtAmount;
    EditText txtRemarks;

    String nsSubModule="0";
    String nsModule="",nsId="";

    private AppCompatSpinner spinnerUPIType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_payment_receipt);

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
        toolbar.setTitle("UPI Payments");

        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,upiPaymentReceiptActivity.this,txtDate);
            }
        });

        String[] list = {"UPI Type",
                "PhonePe",
                "GoooglePe",
                "PayTM"};

        String[] categories = new String[list.length];
        for(int i=0;i<list.length;i++) {
            categories[i] = list[i];
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(upiPaymentReceiptActivity.this, android.R.layout.simple_spinner_item, categories){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUPIType.setAdapter(dataAdapter);

        spinnerUPIType.setSelection(1);
        //Toast.makeText(this, ""+getIntent().getStringExtra("nsAmount1"), Toast.LENGTH_SHORT).show();
        //if(getIntent().hasExtra("nsAmount1")){
        txtAmount.setText(getIntent().getStringExtra("nsAmount1"));
            //Toast.makeText(this, ""+getIntent().getStringExtra("nsAmount1"), Toast.LENGTH_SHORT).show();
        //}

        if(getIntent().hasExtra("nsSpinnerType")){
            String nsSpinnerType = getIntent().getStringExtra("nsSpinnerType");
            spinnerUPIType.setSelection(Integer.parseInt(nsSpinnerType));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                boolean isValidated = true;

                if(txtAmount.getText().toString().trim().equals("")){
                    txtAmount.setError("Invalid");
                    isValidated=false;
                }else if(spinnerUPIType.getSelectedItemPosition()==0){
                    DialogUtility.showDialog(upiPaymentReceiptActivity.this,"Please Select Correct UPI Type","Error");
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
                    objItems.put("prAmount", txtAmount.getText().toString());
                    objItems.put("prRemarks", txtRemarks.getText().toString());
                    objItems.put("prUPIType", spinnerUPIType.getSelectedItemPosition());

                    jsonArrObjItems.put(objItems);

                    //DialogUtility.showDialog(upiPaymentReceiptActivity.this,jsonArrObjItems.toString(4),"");
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(upiPaymentReceiptActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });

    }

    public void init(){
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        txtDate = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDate);
        txtAmount = (AppCompatAutoCompleteTextView) findViewById(R.id.txtAmount);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        spinnerUPIType = (AppCompatSpinner) findViewById(R.id.spinnerUPIType);
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

                                    txtAmount.setText(objData.getString("nsAmount"));
                                    txtRemarks.setText(objData.getString("nsRemarks"));
                                    spinnerUPIType.setSelection(objData.getInt("nsUPIType"));

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