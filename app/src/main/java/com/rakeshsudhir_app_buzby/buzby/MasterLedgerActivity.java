package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;


public class MasterLedgerActivity extends BaseActivity {

    private TextView lblMessage;
    private AppCompatAutoCompleteTextView txtCustomerContactPerson,txtCustomerCompanyName,txtCustomerOpeningBalance,
            txtCustomerPhoneNumber,txtCustomerPhoneNumber2,txtCustomerEmail,txtCustomerGSTIN,
            txtCustomerBankName,txtCustomerBankIFSC,txtCustomerBankCity,txtCustomerState,txtCustomerBankAccountNumber,
            txtCustomerAltName1,txtCustomerAltName2;
    private Button btnSave,btn_other_details,btn_bank_details,btnDelete;
    private LinearLayout llOtherDetailsSection,llBankSection;
    private CheckBox is_restricted,is_blocked,is_active,is_gstCustomer,is_credit_customer;
    private AppCompatSpinner categorySpinner;
    private AppCompatAutoCompleteTextView txtCustomerName, txtCustomerArea, txtCustomerCity;
    private EditText txtRemarks;
    private ImageView imgAddCity,imgAddArea;
    private ProgressDialog progress;

    String nsModule="",nsId="",nsArea="",nsCity="",nsAreaId="",nsCityId="",nsState="",nsStateId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_ledger);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ledger");
        setSupportActionBar(toolbar);

        initializeFeilds();
        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        nsModule = getIntent().getStringExtra("nsModule");
        Intent intent = getIntent();
        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }

        //displayAutoEditText(nsModule);

        is_active.setChecked(true);
        is_blocked.setChecked(false);
        is_restricted.setChecked(false);
        is_gstCustomer.setChecked(false);



        /*txtCustomerCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    imgAddCity.setVisibility(View.GONE);
                }else{
                    imgAddCity.setVisibility(View.VISIBLE);
                }
            }
        });*/

        categorySpinner.setOnItemSelectedListener(this);

        String[] list = {"Customer",
                        "Vendor",
                        "Bank",
                        "E1",
                        "H1"
        };

        String[] categories = new String[list.length];
        for(int i=0;i<list.length;i++) {
            categories[i] = list[i];
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MasterLedgerActivity.this, android.R.layout.simple_spinner_item, categories){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                /*TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }*/
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    is_restricted.setChecked(true);
                }else{
                    is_restricted.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_other_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(llOtherDetailsSection.getVisibility() == View.VISIBLE) {
                    llOtherDetailsSection.setVisibility(View.GONE);
                }else{
                    llOtherDetailsSection.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_bank_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llBankSection.getVisibility() == View.VISIBLE) {
                    llBankSection.setVisibility(View.GONE);
                } else {
                    llBankSection.setVisibility(View.VISIBLE);
                }
            }
        });
        //imgAddArea.setVisibility(View.GONE);

        imgAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCustomerArea.setText("");
                nsAreaId="";
            }
        });
        txtCustomerArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterLedgerActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_area");
                startActivityForResult(i,1);
            }
        });
        //imgAddCity.setVisibility(View.GONE);
        txtCustomerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterLedgerActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_city");
                startActivityForResult(i,2);
            }
        });

        txtCustomerState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterLedgerActivity.this,ListActivity.class);
                i.putExtra("nsModule","ff_master_state_code");
                startActivityForResult(i,3);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                /*if(txtCustomerArea.hasFocus()){
                    txtCustomerArea.clearFocus();
                }

                if(txtCustomerCity.hasFocus()){
                    txtCustomerCity.clearFocus();
                }
*/
                boolean isValidated = true;

                if(txtCustomerName.getText().toString().trim().equals("")){
                    txtCustomerName.setError("Invalid");
                    txtCustomerName.requestFocus();
                    isValidated=false;
                }/*else if(categorySpinner.getSelectedItemPosition()==0){
                    DialogUtility.showDialog(MasterLedgerActivity.this,"Incorrect Ledger Category Selected","Alert");
                    isValidated=false;
                }*//*else if(txtCustomerArea.length()>0 && nsAreaId.equals("")) {
                    txtCustomerArea.setError("Invalid");
                    txtCustomerArea.requestFocus();
                    isValidated=false;
                }*/else if(txtCustomerCity.getText().toString().equals("") || nsCityId.equals("")) {
                    txtCustomerCity.setError("Invalid");
                    txtCustomerCity.requestFocus();
                    imgAddCity.setVisibility(View.GONE);
                    isValidated=false;
                }else if(txtCustomerState.getText().toString().equals("") || nsStateId.equals("")) {
                    txtCustomerState.setError("Invalid");
                    txtCustomerState.requestFocus();
                    isValidated=false;
                }else if(categorySpinner.getSelectedItemPosition()==1 && !is_restricted.isChecked()) {
                    DialogUtility.showDialog(MasterLedgerActivity.this,"Category Selected is Vendor, Please tick Restricted Checkbox","Error");
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }
                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prCustomerName", txtCustomerName.getText().toString().toUpperCase().trim());
                    objItems.put("prLedgerCategoryId", (categorySpinner.getSelectedItemPosition()+1));
                    objItems.put("prCustomerName2", txtCustomerAltName2.getText().toString().toUpperCase().trim());
                    objItems.put("prCustomerName1", txtCustomerAltName1.getText().toString().toUpperCase().trim());
                    objItems.put("prCompany", txtCustomerCompanyName.getText().toString().toUpperCase().trim());
                    if(!nsAreaId.equals("") && !txtCustomerArea.getText().toString().trim().equals("")) {
                        objItems.put("prAreaId", nsAreaId);
                    }
                    if(!nsCityId.equals("") && !txtCustomerCity.getText().toString().trim().equals("")) {
                        objItems.put("prCityId", nsCityId);
                    }

                    if(!nsStateId.equals("") && !txtCustomerState.getText().toString().trim().equals("")) {
                        objItems.put("prStateId", nsStateId);
                    }

                    if(txtCustomerOpeningBalance.getText().toString().trim().equals("")){
                        objItems.put("prOpeningBal", "0");
                    }else {
                        objItems.put("prOpeningBal", txtCustomerOpeningBalance.getText().toString().trim());
                    }
                    objItems.put("prPhone", txtCustomerPhoneNumber.getText().toString().trim());
                    objItems.put("prPhone2", txtCustomerPhoneNumber2.getText().toString().trim());
                    objItems.put("prContactPerson", txtCustomerContactPerson.getText().toString().toUpperCase().trim());
                    objItems.put("prRemarks", txtRemarks.getText().toString().toUpperCase().trim());
                    objItems.put("prEmailId", txtCustomerEmail.getText().toString().trim());
                    objItems.put("prGSTIN", txtCustomerGSTIN.getText().toString().toUpperCase().trim());
                    objItems.put("prBankName", txtCustomerBankName.getText().toString().toUpperCase().trim());
                    objItems.put("prBankIFSC", txtCustomerBankIFSC.getText().toString().toUpperCase().trim());
                    objItems.put("prBankCity", txtCustomerBankCity.getText().toString().toUpperCase().trim());
                    objItems.put("prBankAccountNumber", txtCustomerBankAccountNumber.getText().toString().trim());

                    if(is_restricted.isChecked()){
                        objItems.put("prIsRestricted", "1");
                    }else{
                        objItems.put("prIsRestricted", "2");
                    }

                    if(is_blocked.isChecked()){
                        objItems.put("prIsBlocked", "1");
                    }else{
                        objItems.put("prIsBlocked", "2");
                    }

                    if(is_active.isChecked()){
                        objItems.put("prIsActive", "1");
                    }else{
                        objItems.put("prIsActive", "2");
                    }

                    if(is_gstCustomer.isChecked()){
                        objItems.put("prIsGST", "1");
                    }else{
                        objItems.put("prIsGST", "2");
                    }

                    if(is_credit_customer.isChecked()){
                        objItems.put("prIsCreditCustomer", "1");
                    }else{
                        objItems.put("prIsCreditCustomer", "2");
                    }

                    jsonArrObjItems.put(objItems);

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });

        if(Integer.parseInt(nsId)>0){

            if(Integer.parseInt(preferenceConfig.readUserPosition())>1) {
                is_restricted.setEnabled(false);
                is_restricted.setAlpha(.5f);

                is_blocked.setEnabled(false);
                is_blocked.setAlpha(.5f);
            }


            btnDelete.setVisibility(View.VISIBLE);
            displayData(nsId);
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MasterLedgerActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initializeFeilds(){

        categorySpinner = (AppCompatSpinner) findViewById(R.id.spinnerCategory);

        txtCustomerName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerName);
        //txtCustomerArea = (AppCompatEditText) findViewById(R.id.txtCustomerArea);
        txtCustomerArea = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerArea);
        txtCustomerCity = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerCity);
        txtCustomerState = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerState);

        txtCustomerCompanyName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerCompanyName);
        txtCustomerOpeningBalance = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerOpeningBalance);
        txtCustomerPhoneNumber = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerPhoneNumber);
        txtCustomerPhoneNumber2 = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerPhoneNumber2);
        txtCustomerContactPerson = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerContactPerson);
        txtCustomerAltName1 = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerAltName1);
        txtCustomerAltName2 = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerAltName2);
        txtRemarks = (EditText)findViewById(R.id.txtRemarks);
        txtCustomerEmail = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerEmail);
        txtCustomerGSTIN = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerGSTIN);
        txtCustomerBankName = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerBankName);
        txtCustomerBankCity = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerBankCity);
        txtCustomerBankIFSC = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerBankIFSC);
        txtCustomerBankAccountNumber = (AppCompatAutoCompleteTextView) findViewById(R.id.txtCustomerBankAccountNumber);

        imgAddCity = (ImageView) findViewById(R.id.imgAddCity);
        imgAddArea = (ImageView) findViewById(R.id.imgAddArea);

        btn_other_details = (Button) findViewById(R.id.btn_other_details);
        btn_bank_details = (Button) findViewById(R.id.btn_bank_details);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        llOtherDetailsSection = (LinearLayout) findViewById(R.id.llOtherDetailsSection);
        llBankSection = (LinearLayout) findViewById(R.id.llBankSection);

        is_restricted = (CheckBox) findViewById(R.id.is_restricted);
        is_blocked = (CheckBox) findViewById(R.id.is_blocked);
        is_active = (CheckBox) findViewById(R.id.is_active);
        is_gstCustomer = (CheckBox) findViewById(R.id.is_gstCustomer);
        is_credit_customer = (CheckBox) findViewById(R.id.is_credit_customer);

        lblMessage = (TextView) findViewById(R.id.lblMessage);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                nsArea = data.getStringExtra("nsArea");
                nsAreaId = data.getStringExtra("nsId");

                txtCustomerArea.setText(nsArea);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }else if (requestCode==2){
            if(resultCode==RESULT_OK) {
                nsCity = data.getStringExtra("nsCity");
                nsCityId = data.getStringExtra("nsId");

                txtCustomerCity.setText(nsCity);

                txtCustomerCity.setError(null);
                imgAddCity.setVisibility(View.VISIBLE);
            }
            if(requestCode==RESULT_CANCELED){
                //txtBrandName.setText("");
            }
        }else if (requestCode==3){
            if(resultCode==RESULT_OK) {
                nsState = data.getStringExtra("nsName");
                nsStateId = data.getStringExtra("nsId");
                txtCustomerState.setText(nsState);
                txtCustomerState.setError(null);
                DialogUtility.showDialog(MasterLedgerActivity.this,nsState+" "+nsStateId,"");
            }
            if(requestCode==RESULT_CANCELED){
                //txtBrandName.setText("");
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
                        //Toast.makeText(MasterLedgerCreation.this, ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                // JSONArray jsonColSettings = jsonObject.getJSONArray("dataColSettings");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);

                                    categorySpinner.setSelection((objData.getInt("nsLedgerCategoryId")-1));
                                    txtCustomerName.setText(objData.getString("nsName"));
                                    if(!objData.isNull("nsCompany")) {
                                        txtCustomerCompanyName.setText(objData.getString("nsCompany"));
                                    }

                                    if(!objData.isNull("nsCustomerName1")) {
                                        txtCustomerAltName1.setText(objData.getString("nsCustomerName1"));
                                    }

                                    if(!objData.isNull("nsCustomerName2")) {
                                        txtCustomerAltName2.setText(objData.getString("nsCustomerName2"));
                                    }
                                    if(!objData.isNull("nsArea")) {
                                        txtCustomerArea.setText(objData.getString("nsArea"));
                                        nsAreaId=objData.getString("nsAreaId");
                                    }
                                    if(!objData.isNull("nsCity")) {
                                        txtCustomerCity.setText(objData.getString("nsCity"));
                                        nsCityId=objData.getString("nsCityId");
                                    }
                                    if(!objData.isNull("nsStateId")) {
                                        //Toast.makeText(MasterLedgerActivity.this, ""+objData.getString("nsStateId"), Toast.LENGTH_SHORT).show();
                                        txtCustomerState.setText(objData.getString("nsState"));
                                        nsStateId=objData.getString("nsStateId");
                                    }
                                    txtCustomerOpeningBalance.setText(objData.getString("nsOpeningBalance"));
                                    if(!objData.isNull("nsContactPerson")) {
                                        txtCustomerContactPerson.setText(objData.getString("nsContactPerson"));
                                    }
                                    if(!objData.isNull("nsRemarks")) {
                                        txtRemarks.setText(objData.getString("nsRemarks"));
                                    }
                                    if(!objData.isNull("nsBankAccountNumber")) {
                                        txtCustomerBankAccountNumber.setText(objData.getString("nsBankAccountNumber"));
                                    }
                                    if(!objData.isNull("nsBankCity")) {
                                        txtCustomerBankCity.setText(objData.getString("nsBankCity"));
                                    }
                                    if(!objData.isNull("nsBankIFSC")) {
                                        txtCustomerBankIFSC.setText(objData.getString("nsBankIFSC"));
                                    }
                                    if(!objData.isNull("nsBankName")) {
                                        txtCustomerBankName.setText(objData.getString("nsBankName"));
                                    }
                                    if(!objData.isNull("nsGSTIN")) {
                                        txtCustomerGSTIN.setText(objData.getString("nsGSTIN"));
                                    }
                                    if(!objData.isNull("nsPhone")) {
                                        txtCustomerPhoneNumber.setText(objData.getString("nsPhone"));
                                    }
                                    if(!objData.isNull("nsPhone2")) {
                                        txtCustomerPhoneNumber2.setText(objData.getString("nsPhone2"));
                                    }
                                    if(!objData.isNull("nsEmail")) {
                                        txtCustomerEmail.setText(objData.getString("nsEmail"));
                                    }
                                    if(objData.getString("nsIsActive").equals("1")){
                                        is_active.setChecked(true);
                                    }else{
                                        is_active.setChecked(false);
                                    }
                                    if(objData.getString("nsIsRestricted").equals("1")){
                                        is_restricted.setChecked(true);
                                    }else{
                                        is_restricted.setChecked(false);
                                    }
                                    if(objData.getString("nsIsBlocked").equals("1")){
                                        is_blocked.setChecked(true);
                                    }else{
                                        is_blocked.setChecked(false);
                                    }
                                    if(objData.getString("nsIsGSTCustomer").equals("1")){
                                        is_gstCustomer.setChecked(true);
                                    }else{
                                        is_gstCustomer.setChecked(false);
                                    }

                                    if(objData.getString("nsIsCreditCustomer").equals("1")){
                                        is_credit_customer.setChecked(true);
                                    }else{
                                        is_credit_customer.setChecked(false);
                                    }
                                    progress.dismiss();
                                }
                            }

                        }  catch (Exception e) {
                            //swipeRefreshLayout.setRefreshing(false);
                            lblMessage.setText(e.getMessage());
                            progress.dismiss();
                        }
                        //swipeRefreshLayout.setRefreshing(false);
                        //progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lblMessage.setText(error.getMessage());
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
                params.put("module", "master_ledger");
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
    @Override
    protected void onRestart() {
        super.onRestart();
        //displayAutoEditText(nsModule);

    }
}
