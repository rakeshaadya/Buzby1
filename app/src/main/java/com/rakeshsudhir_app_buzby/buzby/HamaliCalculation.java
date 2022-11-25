package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HamaliCalculation extends BaseActivity implements MyDataAdapter.myAdapterListener {

    private String nsModule="";
    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private Context context;
    String Cdate;
    private ProgressDialog progress;
    String filterLocationId = "";
    private Date filterDateFrom;
    EditText txtOtherAmount,txtRemarks;
    TextView lblTQty,lblTAmount;
    JSONArray jsonData;
    //Rakesh Sudhir

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamali_calculation);

        nsModule = getIntent().getStringExtra("nsModule");

        txtOtherAmount = (EditText) findViewById(R.id.txtOtherAmount);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        lblTAmount = (TextView) findViewById(R.id.lblTAmount);
        lblTQty = (TextView) findViewById(R.id.lblTQty);

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

        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(this,myDataListColumns, myDataList,this,nsModule,"");

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        Cdate = String.format("%02d", year) + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
        filterDateFrom = cldr.getTime();

        displayData();

        txtOtherAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                calculateTotal();

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hamali Calculation");

        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HamaliCalculation.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
    }

    private void calculateTotal() {
        double addAmt = 0.0,otherAmt=0.0;
        double totalQty = 0.0;
        double btotal=0.0,btotal1=0;

        if(!txtOtherAmount.getText().toString().equals("")){
            otherAmt = Double.parseDouble(txtOtherAmount.getText().toString());
        }else{
            otherAmt=0.0;
        }

        try{
            for (int i = 0; i < jsonData.length(); i++) {
                totalQty += jsonData.getJSONObject(i).getDouble("nsQuantity");
                btotal1 += (jsonData.getJSONObject(i).getDouble("nsQuantity") * jsonData.getJSONObject(i).getDouble("nsHamliPrice"));
            }

            btotal1 = btotal1+otherAmt;
            BigDecimal bd = new BigDecimal(btotal1).setScale(2, RoundingMode.HALF_UP);
            btotal = Math.round(bd.doubleValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lblTQty.setText(String.valueOf(totalQty));
        lblTAmount.setText(String.valueOf(btotal));


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
                        //DialogUtility.showDialog(HamaliCalculation.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(HamaliCalculation.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0) {
                                    myDataListColumns.clear();
                                    myDataListColumns.add("nsName");

                                    myDataList = jsonData;
                                    myDataAdapter = new MyDataAdapter(context, myDataListColumns, myDataList, (MyDataAdapter.myAdapterListener) context, nsModule, "");
                                    recyclerView.setAdapter(myDataAdapter);

                                    calculateTotal();

                                    double btotal1=0.0;
                                    try{
                                        for (int i = 0; i < jsonData.length(); i++) {
                                            btotal1 += (jsonData.getJSONObject(i).getDouble("nsQuantity") * jsonData.getJSONObject(i).getDouble("nsHamliPrice"));
                                        }
                                        BigDecimal bd = new BigDecimal(btotal1).setScale(2, RoundingMode.HALF_UP);
                                        btotal1 = Math.round(bd.doubleValue());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    final double finalBtotal = btotal1;
                                    lblTAmount.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(HamaliCalculation.this, ""+finalBtotal, Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(HamaliCalculation.this,MasterExpenseActivity.class);
                                            i.putExtra("nsAmount", ""+finalBtotal);
                                            i.putExtra("nsOthersAmount",txtOtherAmount.getText().toString());
                                            i.putExtra("nsExpenseHeadId","1");
                                            i.putExtra("nsExpenseHead","A.H");
                                            i.putExtra("nsModule","master_expense");
                                            i.putExtra("nsId","-1");
                                            i.putExtra("nsSubModule","directedfromHamaliCalculation");
                                            startActivity(i);
                                        }
                                    });

                                }else{
                                    DialogUtility.showDialog(HamaliCalculation.this,"No Purchase Recorded","Alert");
                                }
                                progress.dismiss();

                            }
                        }  catch (Exception e) {
                            //lblMessage.setText(e.getMessage());
                            progress.dismiss();
                            Toast.makeText(HamaliCalculation.this, "expec "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*if(error.networkResponse==null){
                    DialogUtility.showDialog(ListActivity.this,"Error: Please Check your Internet Connection","Alert");
                }else {*/
                Log.e("Rakesh", error.getCause()+" @\n "+error.getLocalizedMessage()+" @\n "+error.networkResponse+" VolleyError: \n" + error.getNetworkTimeMs());
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
                params.put("functionality", "fetch_table_data");
                params.put("prLocationId", preferenceConfig.readLocationId());


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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }



    @Override
    public void OnItemSelected(JSONObject item, int position) {

    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }
}