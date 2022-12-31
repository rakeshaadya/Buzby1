package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;
import java.util.Map;

public class ReceiptPaymentActivity extends BaseActivity implements MyDataAdapter.myAdapterListener, AdapterView.OnItemSelectedListener{

    private AppCompatSpinner spinnerPaymentType;
    private AppCompatSpinner spinnerTransactionCategory;
    private AppCompatAutoCompleteTextView txtAmount;
    private AppCompatAutoCompleteTextView txtDate;
    private AppCompatAutoCompleteTextView txtCustomerName;
    private AppCompatAutoCompleteTextView txtLocationName;
    private TextView lblLedgerBalance;
    EditText txtRemarks;
    String[] paymentType,transactionType;
    DatePickerDialog picker;
    String ledgerid="",nsCustomerName="",customerBalance="";
    private Button btnSave,btnDelete,btnRePrint;
    String nsSubModule="0";
    String nsModule="",nsId="";
    String area="",areaid="",city="",cityid="";
    private ProgressDialog progress;
    String isCC="",nsLocationId="";

    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private Context context;
    LinearLayout llAfterCustomerEntered,layoutMain;
    JSONArray checkedArrayItems = new JSONArray();

    JSONArray jsonData;

    AppCompatImageView infoButton;
    TextView sumOfCheckedValues;
    int receiptType,restrictedGlobal=0;

    CheckBox checkboxReceiptPayment,checkboxPrint;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_payment);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        spinnerPaymentType = (AppCompatSpinner) findViewById(R.id.spinnerPaymentType);
        spinnerTransactionCategory = (AppCompatSpinner) findViewById(R.id.spinnerTransactionCategory);
        txtAmount = (AppCompatAutoCompleteTextView) findViewById(R.id.txtAmount);
        txtDate = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDate);
        txtCustomerName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerName);
        txtLocationName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtLocationName);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnRePrint = (Button)findViewById(R.id.btnRePrint);
        lblLedgerBalance = (TextView)findViewById(R.id.lblLedgerBalance);
        llAfterCustomerEntered = (LinearLayout)findViewById(R.id.llAfterCustomerEntered);
        layoutMain = (LinearLayout)findViewById(R.id.layoutMain);
        checkboxReceiptPayment = (CheckBox) findViewById(R.id.checkboxReceiptPayment);
        checkboxPrint = (CheckBox) findViewById(R.id.checkboxPrint);

        infoButton = (AppCompatImageView)findViewById(R.id.infoButton);
        sumOfCheckedValues = (TextView)findViewById(R.id.sumOfCheckedValues);

        nsModule = getIntent().getStringExtra("nsModule");
        Intent intent = getIntent();

        if(getIntent().hasExtra("restrictedGlobal")){
            //Toast.makeText(this, ""+getIntent().getStringExtra("restrictedGlobal"), Toast.LENGTH_SHORT).show();
            restrictedGlobal = Integer.parseInt(getIntent().getStringExtra("restrictedGlobal"));
        }else{
            restrictedGlobal=0;
        }

        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }

        if (intent.hasExtra("customerBalance")) {
            customerBalance=getIntent().getStringExtra("customerBalance");

        }

        if(intent.hasExtra("customerid")) {
            ledgerid=getIntent().getStringExtra("customerid");

            nsCustomerName=getIntent().getStringExtra("customerName");
            txtCustomerName.setText(nsCustomerName);

            lblLedgerBalance.setText(customerBalance);

            if(!preferenceConfig.readUserPosition().equals("1")) {

                if(restrictedGlobal==1){
                    lblLedgerBalance.setVisibility(View.INVISIBLE);
                    llAfterCustomerEntered.setVisibility(View.GONE);
                }else{
                    lblLedgerBalance.setVisibility(View.VISIBLE);

                    displayCustomerData(ledgerid);
                }
            }else{
                displayCustomerData(ledgerid);
            }
        }

        if(getIntent().hasExtra("nsSubModule")){
            nsSubModule = getIntent().getStringExtra("nsSubModule");
        }



        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(picker,ReceiptPaymentActivity.this,txtDate);
            }
        });

        if(!preferenceConfig.readUserPosition().equals("1")){
            //dateLocationLayout.setVisibility(View.INVISIBLE);
            txtDate.setEnabled(false);
            txtLocationName.setEnabled(false);
        }

        txtLocationName.setText(preferenceConfig.readlocationName());
        nsLocationId = preferenceConfig.readLocationId();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(this,myDataListColumns, myDataList,this,nsModule,"");

        spinnerPaymentType.setOnItemSelectedListener(this);
        String[] paymentlist = {"Select Payment Type",
                "Cash",
                "PhonePe",
                "Bank",
                "Cheque"
        };
        paymentType = new String[paymentlist.length];
        for(int i=0;i<paymentlist.length;i++) {
            paymentType[i] = paymentlist[i];
        }

        ArrayAdapter<String> dataAdapterPayment = new ArrayAdapter<String>(ReceiptPaymentActivity.this, android.R.layout.simple_spinner_item, paymentType){
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
        dataAdapterPayment.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPaymentType.setAdapter(dataAdapterPayment);


        spinnerTransactionCategory.setOnItemSelectedListener(this);
        String[] transactionlist = {"Select Transaction Type",
                "Receipt",
                "Payment"};
        transactionType = new String[transactionlist.length];
        for(int i=0;i<transactionlist.length;i++) {
            transactionType[i] = transactionlist[i];
        }

        ArrayAdapter<String> dataAdapterTransaction = new ArrayAdapter<String>(ReceiptPaymentActivity.this, android.R.layout.simple_spinner_item, transactionType){
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

        txtCustomerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReceiptPaymentActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_ledger");
                i.putExtra("nsSubModule", "returnValue");
                startActivityForResult(i,1);
            }
        });

        txtLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReceiptPaymentActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule", "returnValue");
                startActivityForResult(i,2);
            }
        });

        dataAdapterTransaction.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTransactionCategory.setAdapter(dataAdapterTransaction);

        spinnerTransactionCategory.setSelection(1);
        spinnerPaymentType.setSelection(1);

        if(intent.hasExtra("transactionType")) {
            if (getIntent().getStringExtra("transactionType").equals("400")) {
                spinnerTransactionCategory.setSelection(1);
            } else if (getIntent().getStringExtra("transactionType").equals("500")) {
                spinnerTransactionCategory.setSelection(2);
            }
        }

        spinnerTransactionCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    layoutMain.setBackgroundColor(Color.parseColor("#FAFAD2"));
                    toolbar.setTitle("Receipt");
                    //setSupportActionBar(toolbar);
                }else if(position==2){
                    layoutMain.setBackgroundColor(Color.parseColor("#e0f3ff"));
                    toolbar.setTitle("Payment");
                    //setSupportActionBar(toolbar);
                }else{
                    toolbar.setTitle("Receipt/Payment");
                    /*setSupportActionBar(toolbar);*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                boolean isValidated = true;

                if(spinnerTransactionCategory.getSelectedItemPosition()==0){
                    DialogUtility.showDialog(ReceiptPaymentActivity.this,"Incorrect Transaction Selected","Error");
                    isValidated=false;
                }else if(txtCustomerName.getText().toString().trim().equals("")){
                    txtCustomerName.setError("Invalid");
                    isValidated=false;
                }else if(txtAmount.getText().toString().equals("")){
                    txtAmount.setError("Invalid");
                    isValidated = false;
                }else if(spinnerTransactionCategory.getSelectedItemPosition()==1){
                    double d1=0.0;
                    if(!txtAmount.getText().toString().equals("")) {
                        d1 = Double.parseDouble(txtAmount.getText().toString());
                    }
                    if(d1<0) {
                        txtAmount.setError("Invalid");
                        isValidated = false;
                    }
                }else if(spinnerTransactionCategory.getSelectedItemPosition()==2){
                    double d1=0.0;
                    if(!txtAmount.getText().toString().equals("")) {
                        if(checkedArrayItems.length()>0) {
                            d1 = Double.parseDouble(txtAmount.getText().toString());
                        }else{
                            d1 = 0-Double.parseDouble(txtAmount.getText().toString());
                        }
                    }

                    if(!txtAmount.isEnabled()) {
                        if (d1 > 0) {
                            txtAmount.setError("Invalid");
                            isValidated = false;
                        }
                    }
                }else if(spinnerPaymentType.getSelectedItemPosition()==0){
                    DialogUtility.showDialog(ReceiptPaymentActivity.this,"Incorrect Payment Selected","Error");
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
                    objItems.put("prLocationId", nsLocationId);

                    if(spinnerTransactionCategory.getSelectedItemPosition()==1){
                        objItems.put("prTransactionId", "400");//receipt
                        objItems.put("prAmount", txtAmount.getText().toString());
                    }else if(spinnerTransactionCategory.getSelectedItemPosition()==2){
                        objItems.put("prTransactionId", "500");//Payment
                            objItems.put("prAmount", txtAmount.getText().toString());
                    }

                    if(checkedArrayItems.length()>0){
                        objItems.put("arrKalamItems",checkedArrayItems.toString(4));
                    }

                    if(spinnerPaymentType.getSelectedItemPosition()==1){
                        objItems.put("prPaymentType", "1");//cash
                    }else if(spinnerPaymentType.getSelectedItemPosition()==2){
                        objItems.put("prPaymentType", "2");//PhonePe
                    }else if(spinnerPaymentType.getSelectedItemPosition()==3){
                        objItems.put("prPaymentType", "3");//bank
                    }else if(spinnerPaymentType.getSelectedItemPosition()==4){
                        objItems.put("prPaymentType", "4");//Cheque
                    }

                    objItems.put("prRemarks", txtRemarks.getText().toString());

                    jsonArrObjItems.put(objItems);

                    int printStatus=0;

                    if(checkboxPrint.isChecked()){
                        printStatus=1;
                    }else{
                        printStatus=2;
                    }

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",1,printStatus,2);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(Integer.parseInt(nsId)>0){
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnRePrint.setVisibility(View.VISIBLE);
            displayData(nsId);

        }

        txtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if(txtAmount.isEnabled() && !txtAmount.getText().toString().equals("")) {

                    //Toast.makeText(ReceiptPaymentActivity.this, "h", Toast.LENGTH_SHORT).show();

                    double a = 0;
                    if(!txtAmount.getText().toString().trim().equals("")) {
                        if (spinnerTransactionCategory.getSelectedItemPosition() == 1) {
                            a = (Double.parseDouble(txtAmount.getText().toString()) - Double.parseDouble(lblLedgerBalance.getText().toString()));
                        } else if (spinnerTransactionCategory.getSelectedItemPosition() == 2) {
                            a = (Double.parseDouble(txtAmount.getText().toString()) + Double.parseDouble(lblLedgerBalance.getText().toString()));
                        }
                    }

                    if (a == 0) {
                        checkedArrayItems = jsonData;
                    } else {
                        checkedArrayItems = new JSONArray();
                    }
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(txtAmount.isEnabled() && !txtAmount.getText().toString().equals("")) {

                    //Toast.makeText(ReceiptPaymentActivity.this, "h", Toast.LENGTH_SHORT).show();
                    double a = 0;double as=0.0;


                        if (spinnerTransactionCategory.getSelectedItemPosition() == 1) {

                            if(!txtAmount.getText().toString().trim().equals("")){
                                as=Double.parseDouble(txtAmount.getText().toString());
                                if(!lblLedgerBalance.getText().toString().equals("")) {
                                    a = (as - Double.parseDouble(lblLedgerBalance.getText().toString()));
                                }
                            }

                        } else if (spinnerTransactionCategory.getSelectedItemPosition() == 2) {
                            as=Double.parseDouble(txtAmount.getText().toString());
                            if(!lblLedgerBalance.getText().toString().equals("")) {
                                a = (as + Double.parseDouble(lblLedgerBalance.getText().toString()));
                            }
                        }

                    if (a == 0) {
                        checkedArrayItems = jsonData;
                    } else {
                        checkedArrayItems = new JSONArray();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });

        if(!preferenceConfig.readUserPosition().equals("1")) {
            if (getIntent().hasExtra("nsHideButtons")) {
                btnSave.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }

        if(getIntent().hasExtra("nsIsDeleted")){
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ReceiptPaymentActivity.this,DashboardActivity.class);
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
            if (resultCode == RESULT_OK) {
                //vehicleNo = data.getStringExtra("nsRegistrationNumber");

                isCC = data.getStringExtra("nsIsCreditCustomer");
                if(data.getStringExtra("nsIsCreditCustomer").equals("1")){
                    txtCustomerName.setTextColor(Color.parseColor("#C62828"));
                }else{
                    txtCustomerName.setTextColor(Color.parseColor("#000000"));
                }

                if(!data.getStringExtra("nsArea").equals("")) {
                    area = " "+data.getStringExtra("nsArea");
                    areaid = data.getStringExtra("nsAreaId");
                }else{
                    area="";
                    areaid="";
                }

                if(!data.getStringExtra("nsCity").equals("")) {
                    city=" "+data.getStringExtra("nsCity");
                    cityid=data.getStringExtra("nsCityId");
                }else{
                    city="";
                    cityid="";
                }

                double Ledgerbalance = Double.parseDouble(data.getStringExtra("nsBalance"));

                restrictedGlobal = Integer.parseInt(data.getStringExtra("nsIsRestricted"));
                if(Ledgerbalance>0){
                    lblLedgerBalance.setTextColor(Color.parseColor("#C62828"));
                }else{
                    lblLedgerBalance.setTextColor(Color.parseColor("#1B5E20"));
                }


                lblLedgerBalance.setText(data.getStringExtra("nsBalance"));

                ledgerid = data.getStringExtra("nsId");

                txtCustomerName.setText(data.getStringExtra("nsName"));
                txtCustomerName.setError(null);
                txtAmount.requestFocus();

                txtCustomerName.setError(null);

                if(!preferenceConfig.readUserPosition().equals("1")) {
                    if(restrictedGlobal==1){
                        lblLedgerBalance.setVisibility(View.INVISIBLE);
                        llAfterCustomerEntered.setVisibility(View.GONE);

                    }else{
                        lblLedgerBalance.setVisibility(View.VISIBLE);
                        displayCustomerData(ledgerid);
                    }
                }else{
                    displayCustomerData(ledgerid);
                }
                txtAmount.setText("");
                sumOfCheckedValues.setText("");
                checkedArrayItems = new JSONArray();


            }
            if (requestCode == RESULT_CANCELED) {

            }
        }else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                nsLocationId = data.getStringExtra("nsId");

                txtLocationName.setText(data.getStringExtra("nsName"));
                txtLocationName.setError(null);

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

                        //DialogUtility.showDialog(ReceiptPaymentActivity.this,response,"");
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

                                    txtAmount.clearFocus();
                                    txtAmount.setEnabled(false);

                                    //Toast.makeText(ReceiptPaymentActivity.this, "Hello  "+objData.getInt("nsTransactionId"), Toast.LENGTH_SHORT).show();
                                    if(objData.getInt("nsTransactionId")==400){
                                        spinnerTransactionCategory.setSelection(1);

                                    }else{
                                        spinnerTransactionCategory.setSelection(2);
                                    }

                                    spinnerPaymentType.setSelection(objData.getInt("nsReceiptType"));
                                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                                    txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                                    ledgerid=objData.getString("nsLedgerId");
                                    txtCustomerName.setText(objData.getString("nsLedgerName"));
                                    nsLocationId=objData.getString("nsLocationId");
                                    txtLocationName.setText(objData.getString("nsLocation"));

                                    if(objData.getInt("nsTransactionId")==500) {
                                        //Toast.makeText(ReceiptPaymentActivity.this, " 544 "+jsonData.toString(4), Toast.LENGTH_SHORT).show();
                                        txtAmount.setText(objData.getString("nsAmount"));
                                    }else{
                                        //Toast.makeText(ReceiptPaymentActivity.this, " 547 "+jsonData.toString(4), Toast.LENGTH_SHORT).show();
                                        txtAmount.setText(""+(0-objData.getInt("nsAmount")));
                                    }



                                    txtRemarks.setText(objData.getString("nsRemarks"));


                                    restrictedGlobal = objData.getInt("nsIsRestricted");
                                    /*if(preferenceConfig.readUserPosition().equals("2")) {
                                        if (objData.getInt("nsIsRestricted") == 1) {
                                            lblLedgerBalance.setVisibility(View.INVISIBLE);
                                            restrictedGlobal=1;
                                        } else {
                                            lblLedgerBalance.setVisibility(View.VISIBLE);
                                            restrictedGlobal=2;
                                        }
                                    }*/

                                    lblLedgerBalance.setText(""+(objData.getInt("nsOpeningBalance")+objData.getInt("nsInOut")+objData.getInt("nsReceiptPayment")));

                                    if(!preferenceConfig.readUserPosition().equals("1")) {
                                        if(restrictedGlobal==1){
                                            lblLedgerBalance.setVisibility(View.INVISIBLE);
                                        }else{
                                            lblLedgerBalance.setVisibility(View.VISIBLE);
                                            displayCustomerData(ledgerid);
                                        }
                                    }else{
                                        displayCustomerData(ledgerid);
                                    }

                                    btnRePrint.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            functionPrint.displayDataForPrint(ReceiptPaymentActivity.this, nsModule, nsId,2,1,2,1,1);
                                        }
                                    });
                                }

                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            //swipeRefreshLayout.setRefreshing(false);
                            //lblMessage.setText(e.getMessage());
                            Toast.makeText(ReceiptPaymentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ReceiptPaymentActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void displayCustomerData(final String ledgerid) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ReceiptPaymentActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        //DialogUtility.showDialog(ReceiptPaymentActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);

                                jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){

                                    llAfterCustomerEntered.setVisibility(View.VISIBLE);

                                    myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonData, (MyDataAdapter.myAdapterListener) context, "cart_customer_list_receipt_payment","");
                                    recyclerView.setAdapter(myDataAdapter);

                                }else{
                                    llAfterCustomerEntered.setVisibility(View.GONE);
                                    jsonData = new JSONArray();
                                    myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonData, (MyDataAdapter.myAdapterListener) context, "cart_customer_list_receipt_payment","");
                                    recyclerView.setAdapter(myDataAdapter);
                                }
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            progress.dismiss();
                            Toast.makeText(ReceiptPaymentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(ReceiptPaymentActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("functionality", "fetch_table_data_customer_list");
                params.put("prLedgerId",ledgerid);
                //Log.d("RakeshSudhir",params.toString());
                if(Integer.parseInt(nsId)>0){
                    params.put("id",nsId);
                    params.put("prDisplayStatus","1");
                }else{
                    params.put("prDisplayStatus","2");
                }
                Log.d("RakeshSudhir",params.toString());
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        receiptType=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {

        //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        try {
            //Toast.makeText(this, "Hello "+item.getString("nsTransactionType"), Toast.LENGTH_SHORT).show();
            if(item.getString("nsTransactionId").equals("200")){
                Intent i = new Intent(ReceiptPaymentActivity.this, SalesTransactionActivity.class);
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsModule","transactions_in_out");
                i.putExtra("nsSubModule","transaction_sales");
                i.putExtra("nsHideButtons","nsHideButtons");
                startActivity(i);
            }else if(item.getString("nsTransactionId").equals("300")){
                Intent i = new Intent(ReceiptPaymentActivity.this, TransactionPurchaseActivity.class);
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsModule","transactions_in_out");
                i.putExtra("nsSubModule","transaction_purchase");
                i.putExtra("nsHideButtons","nsHideButtons");
                startActivity(i);
            }else if(item.getInt("nsTransactionId")==400) {
                Intent i = new Intent(ReceiptPaymentActivity.this, ReceiptPaymentActivity.class);
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsModule","transaction_receipt_payment");
                i.putExtra("transactionType","400");
                startActivity(i);
            }else if(item.getInt("nsTransactionId")==500) {
                Intent i = new Intent(ReceiptPaymentActivity.this, ReceiptPaymentActivity.class);
                i.putExtra("nsId",item.getString("nsId"));
                i.putExtra("nsModule","transaction_receipt_payment");
                i.putExtra("transactionType","500");
                startActivity(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

        if(state==1) {
            try {

                //double diffrence = (item.getDouble("nsDebit")-item.getDouble("nsCredit"));
                //objItemsC.put("amt" + position, String.valueOf(diffrence));

                checkedArrayItems.put(item);


                //DialogUtility.showDialog(ReceiptPaymentActivity.this,checkedArrayItems.toString(4),"");
                double tempSumofCheckedItems = 0.0;
                //try {
                for(int i=0;i<checkedArrayItems.length();i++){
                    JSONObject obj = checkedArrayItems.getJSONObject(i);
                    tempSumofCheckedItems = tempSumofCheckedItems+(obj.getDouble("nsDebit")-obj.getDouble("nsCredit"));

                }
                sumOfCheckedValues.setText(String.valueOf(tempSumofCheckedItems));

                if(checkedArrayItems.length()>0){
                    txtAmount.setEnabled(false);
                    txtAmount.setAlpha(0.5f);
                }else{
                    txtAmount.setText("");
                    txtAmount.setEnabled(true);
                    txtAmount.setAlpha(1f);
                }
                txtAmount.setText(""+tempSumofCheckedItems);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {

                List<String> valueList = new ArrayList<String>();
                for(int i=0;i<checkedArrayItems.length();i++) {
                    valueList.add(checkedArrayItems.getString(i));
                }
                int index = valueList.indexOf(item.toString());
                checkedArrayItems.remove(index);

                //DialogUtility.showDialog(ReceiptPaymentActivity.this,checkedArrayItems.toString(4),"");
                double tempSumofCheckedItems = 0.0;

                    for(int i=0;i<checkedArrayItems.length();i++){
                        JSONObject obj = checkedArrayItems.getJSONObject(i);
                            tempSumofCheckedItems = tempSumofCheckedItems+(obj.getDouble("nsDebit")-obj.getDouble("nsCredit"));
                    }
                    sumOfCheckedValues.setText(String.valueOf(tempSumofCheckedItems));

                if(checkedArrayItems.length()>0){
                    txtAmount.setEnabled(false);
                    txtAmount.setAlpha(0.5f);
                }else{
                    txtAmount.setText("");
                    txtAmount.setEnabled(true);
                    txtAmount.setAlpha(1f);
                }
                    txtAmount.setText(""+tempSumofCheckedItems);


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "OnItemClicked"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
/*
        if(checkedArrayItems.length()>0){
            txtAmount.setEnabled(false);
            txtAmount.setAlpha(0.5f);
        }else{
            txtAmount.setText("");
            txtAmount.setEnabled(true);
            txtAmount.setAlpha(1f);
        }*/
    }
}