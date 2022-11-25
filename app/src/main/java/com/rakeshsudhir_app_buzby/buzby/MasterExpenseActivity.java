package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MasterExpenseActivity extends BaseActivity {

    AppCompatAutoCompleteTextView txtAmount,txtCurrentDate,txtOtherAmount,txtExpenseHead,txtDWName,txtLocationName;
    Button btnSave,btnDelete,btnRePrint;
    TextView lblMessage;
    EditText txtRemarks;

    TextInputLayout layoutDWName,layoutOtherAmount;
    SharedPreferenceConfig preferenceConfig;
    private ProgressDialog progress;
    String nsModule,nsId;
    String nsExpenseHeadId,nsExpenseHead;
    String nsName;

    DatePickerDialog picker;

    String nsLocationId,nsNameId;

    CheckBox checkboxPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_expense);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        nsModule = getIntent().getStringExtra("nsModule");
        nsId=getIntent().getStringExtra("nsId");




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (Integer.parseInt(nsId)>0) {
            toolbar.setTitle("Edit Expense");
        }else{
            toolbar.setTitle("Add Expense");
        }
        setSupportActionBar(toolbar);

        init();

        txtLocationName.setText(preferenceConfig.readlocationName());
        nsLocationId = preferenceConfig.readLocationId();

        if(getIntent().hasExtra("nsSubModule") && getIntent().getStringExtra("nsSubModule").equals("directedfromHamaliCalculation")){
            if(getIntent().getStringExtra("nsExpenseHeadId").equals("1")){
                layoutOtherAmount.setVisibility(View.VISIBLE);
            }else{
                layoutOtherAmount.setVisibility(View.GONE);

            }
            txtAmount.setText(getIntent().getStringExtra("nsAmount"));
            txtOtherAmount.setText(getIntent().getStringExtra("nsOthersAmount"));
            nsExpenseHeadId=getIntent().getStringExtra("nsExpenseHeadId");
            nsExpenseHead = getIntent().getStringExtra("nsExpenseHead");
            txtExpenseHead.setText(nsExpenseHead);
        }

        if(Integer.parseInt(nsId)>0){
            btnSave.setText("Update");
            btnDelete.setVisibility(View.VISIBLE);
            btnRePrint.setVisibility(View.VISIBLE);
            /*btnRePrint.setEnabled(false);
            btnRePrint.setAlpha(0.5f);*/
            displayData();
        }else{
            btnSave.setText("Save");
        }

        final Calendar cldr1 = Calendar.getInstance();
        txtCurrentDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtCurrentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(picker,MasterExpenseActivity.this,txtCurrentDate);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if(txtExpenseHead.getText().toString().trim().matches("")) {
                    txtExpenseHead.setError("Required");
                    isValidated=false;
                }else if(layoutDWName.getVisibility()==View.VISIBLE && txtDWName.getText().toString().trim().matches("")){
                    txtDWName.setError("Required");
                    isValidated=false;
                }else if(txtAmount.getText().toString().trim().matches("")) {
                    txtAmount.setError("Required");
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }

                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    Date d = ConvertFunctions.convertIndianDateStringToDate(txtCurrentDate.getText().toString());
                    objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(d));
                    objItems.put("prExpenseHeadId", nsExpenseHeadId);
                    objItems.put("prLocationId", nsLocationId);
                    if(layoutDWName.getVisibility()==View.VISIBLE) {
                        objItems.put("prDWNameId", nsNameId);
                    }
                    objItems.put("prAmount", txtAmount.getText().toString().trim());
                    double tempOthersAmt = 0.0;
                    if(!txtOtherAmount.getText().toString().trim().equals("")){
                        tempOthersAmt=Double.parseDouble(txtOtherAmount.getText().toString().trim());
                    }
                    if(layoutOtherAmount.getVisibility()==View.VISIBLE) {
                        objItems.put("prOthersAmount", tempOthersAmt);
                    }

                    objItems.put("prRemarks", txtRemarks.getText().toString().trim());
                    jsonArrObjItems.put(objItems);

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        if(!preferenceConfig.readUserPosition().equals("1")){
            //dateLocationLayout.setVisibility(View.INVISIBLE);
            txtCurrentDate.setEnabled(false);
            txtLocationName.setEnabled(false);
        }

        txtLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterExpenseActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule", "returnValue");
                startActivityForResult(i,1);
            }
        });

        txtExpenseHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterExpenseActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_expense_head");
                i.putExtra("nsSubModule", "returnValue");
                startActivityForResult(i,2);
            }
        });

        txtDWName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterExpenseActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_ledger");
                i.putExtra("nsSubModule", "returnValue");
                i.putExtra("nsLedgerCategoryE", "4");
                i.putExtra("nsLedgerCategoryH", "5");
                startActivityForResult(i,3);
            }
        });

        checkboxPrint.setChecked(true);


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
                Intent i = new Intent(MasterExpenseActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void init(){
        txtExpenseHead = (AppCompatAutoCompleteTextView) findViewById(R.id.txtExpenseHead);
        txtOtherAmount = (AppCompatAutoCompleteTextView)findViewById(R.id.txtOtherAmount);
        txtDWName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtDWName);
        txtLocationName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtLocationName);
        txtAmount = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAmount);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        txtCurrentDate = (AppCompatAutoCompleteTextView)findViewById(R.id.txtCurrentDate);
        lblMessage = (TextView)findViewById(R.id.lblMessage);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnRePrint = (Button)findViewById(R.id.btnRePrint);

        layoutDWName = (TextInputLayout)findViewById(R.id.layoutDWName);
        layoutOtherAmount = (TextInputLayout)findViewById(R.id.layoutOtherAmount);

        checkboxPrint= (CheckBox)findViewById(R.id.checkboxPrint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                nsLocationId = data.getStringExtra("nsId");

                txtLocationName.setText(data.getStringExtra("nsName"));
                txtLocationName.setError(null);

            }
            if (requestCode == RESULT_CANCELED) {

            }
        }else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                nsExpenseHeadId = data.getStringExtra("nsId");

                txtExpenseHead.setText(data.getStringExtra("nsName"));
                if(nsExpenseHeadId.equals("2")){
                    layoutDWName.setVisibility(View.VISIBLE);
                }else{
                    layoutDWName.setVisibility(View.GONE);
                    txtDWName.setText("");
                }

                if(nsExpenseHeadId.equals("1")){
                    layoutOtherAmount.setVisibility(View.VISIBLE);
                }else{
                    layoutOtherAmount.setVisibility(View.GONE);
                    txtOtherAmount.setText("");
                }

                txtExpenseHead.setError(null);

            }
            if (requestCode == RESULT_CANCELED) {

            }
        }else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                nsNameId = data.getStringExtra("nsId");

                txtDWName.setText(data.getStringExtra("nsName"));
                txtDWName.setError(null);

            }
            if (requestCode == RESULT_CANCELED) {

            }
        }
    }

    private void displayData() {
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        //DialogUtility.showDialog(MasterExpenseActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);

                                    Date dat = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));

                                    txtCurrentDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(dat));
                                    txtExpenseHead.setText(objData.getString("nsExpenseHead"));
                                    nsExpenseHeadId=objData.getString("nsExpenseHeadId");
                                    txtLocationName.setText(objData.getString("nsLocation"));
                                    nsLocationId=objData.getString("nsLocationId");
                                    if(nsExpenseHeadId.equals("2")) {
                                        layoutDWName.setVisibility(View.VISIBLE);
                                        txtDWName.setText(objData.getString("nsName"));
                                    }else{
                                        layoutDWName.setVisibility(View.GONE);
                                        txtDWName.setText("");
                                    }

                                    if(nsExpenseHeadId.equals("1")) {
                                        layoutOtherAmount.setVisibility(View.VISIBLE);
                                        txtOtherAmount.setText(objData.getString("nsOtherAmount"));
                                    }else{
                                        layoutOtherAmount.setVisibility(View.GONE);
                                        txtOtherAmount.setText("");
                                    }
                                    nsNameId=objData.getString("nsLedgerId");
                                    txtAmount.setText(objData.getString("nsAmount"));
                                    txtOtherAmount.setText(objData.getString("nsOtherAmount"));
                                    txtRemarks.setText(objData.getString("nsRemarks"));

                                    btnRePrint.setEnabled(true);
                                    btnRePrint.setAlpha(1f);

                                    btnRePrint.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            functionPrint.displayDataForPrint(MasterExpenseActivity.this, nsModule, nsId,2,1,2,1,1);
                                        }
                                    });

                                }
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            lblMessage.setText(e.getMessage());
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lblMessage.setText(error.getMessage());
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
                params.put("id",nsId);

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

        requestQueue.add(stringRequest);
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
}