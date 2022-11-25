package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

public class MasterInventoryActivity extends BaseActivity{
    private AppCompatAutoCompleteTextView txtBrandName,txtSubCategory,txtUnits,
            txtSize,txtNameDuringPrint,txtNameDuringTransaction,txtPurchasePrice,
            txtSellingPrice,txtSafetyStock,txtHamali,txtigst,txtcsgst;
    private ImageView imgAddBrand,imgAddSubCategory,imgAddUnits;
    private TextView lblMessage;
    private CheckBox checkboxIsActive;
    private Button btnSave,btnDelete;


    private String nsInventorySubCategory="",nsInventorySubCategoryId="",nsInventoryCategoryId="",
                    nsBrand="",nsBrandId="",nsUnit="",nsUnitId="";
    String nsModule="",nsId="";

    private ProgressDialog progress;
    

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_inventory);

        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        init();

        nsModule = getIntent().getStringExtra("nsModule");
        Intent intent = getIntent();
        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Inventory");
        setSupportActionBar(toolbar);

        displayAutoEditText(nsModule);

        imgAddBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterInventoryActivity.this, ListActivity.class);
                i.putExtra("nsModule","master_brand");
                startActivityForResult(i,1);
            }
        });
        //imgAddSubCategory.setVisibility(View.GONE);
        imgAddSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterInventoryActivity.this, ListActivity.class);
                i.putExtra("nsModule","master_inventory_subcategory");
                startActivityForResult(i,2);
            }
        });
        //imgAddUnits.setVisibility(View.GONE);
        imgAddUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MasterInventoryActivity.this, ListActivity.class);
                i.putExtra("nsModule","master_units");
                startActivityForResult(i,3);
            }
        });

        if(Integer.parseInt(nsId)>0){
            displayData(nsId);
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setText("Update");
        }

        txtBrandName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                concateNamesDuringPrint();
            }
        });

        txtSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                concateNamesDuringPrint();
            }
        });

        txtUnits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                concateNamesDuringPrint();
            }
        });

        if(!preferenceConfig.readUserPosition().equals("1")){
            txtPurchasePrice.setText("0");
            txtPurchasePrice.setVisibility(View.INVISIBLE);
            txtPurchasePrice.setEnabled(false);
            txtPurchasePrice.setAlpha(.5f);
            checkboxIsActive.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtBrandName.hasFocus()){
                    txtBrandName.clearFocus();
                }
                if(txtSubCategory.hasFocus()){
                    txtSubCategory.clearFocus();
                }
                if(txtUnits.hasFocus()){
                    txtUnits.clearFocus();
                }

                boolean isValidated = true;

                if(txtBrandName.getText().toString().trim().equals("") || nsBrandId.equals("")){
                    txtBrandName.setError("Invalid");
                    txtBrandName.requestFocus();
                    isValidated=false;
                }else if(txtSubCategory.getText().toString().trim().equals("") || nsInventoryCategoryId.equals("") || nsInventorySubCategoryId.equals("")) {
                    txtSubCategory.setError("Invalid");
                    txtSubCategory.requestFocus();
                    isValidated=false;
                }else if(txtUnits.getText().toString().trim().equals("") && nsUnitId.equals("")) {
                    txtUnits.setError("Invalid");
                    txtUnits.requestFocus();
                    isValidated=false;
                }else if(txtSize.getText().toString().trim().equals("")){
                    txtSize.setError("Invalid");
                    txtSize.requestFocus();
                    isValidated=false;
                }else if(txtNameDuringPrint.getText().toString().trim().equals("")){
                    txtNameDuringPrint.setError("Invalid");
                    txtNameDuringPrint.requestFocus();
                    isValidated=false;
                }else if(txtNameDuringTransaction.getText().toString().trim().equals("")){
                    txtNameDuringTransaction.setError("Invalid");
                    txtNameDuringTransaction.requestFocus();
                    isValidated=false;
                }else if(txtHamali.getText().toString().trim().equals("")){
                    txtHamali.setError("Invalid");
                    txtHamali.requestFocus();
                    isValidated=false;
                }else if(txtPurchasePrice.getText().toString().trim().equals("")){
                    txtPurchasePrice.setError("Invalid");
                    txtPurchasePrice.requestFocus();
                    isValidated=false;
                }else if(txtSellingPrice.getText().toString().trim().equals("")){
                    txtSellingPrice.setError("Invalid");
                    txtSellingPrice.requestFocus();
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }

                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prCategoryId", nsInventoryCategoryId);
                    objItems.put("prSubCategoryId", nsInventorySubCategoryId);
                    objItems.put("prInventoryBrandId", nsBrandId);
                    objItems.put("prInventoryUnitId", nsUnitId);

                    objItems.put("prUnitSize", txtSize.getText().toString().trim());
                    objItems.put("prNameDuringPrint", txtNameDuringPrint.getText().toString().trim().toUpperCase());
                    objItems.put("prNameDuringTranscation", txtNameDuringTransaction.getText().toString().trim().toUpperCase());
                    objItems.put("prPurchasePrice", txtPurchasePrice.getText().toString().trim());
                    objItems.put("prSellingPrice", txtSellingPrice.getText().toString().trim());
                    double igst=0.0;
                    if(!txtigst.getText().toString().equals("")){
                        igst = Double.parseDouble(txtigst.getText().toString());
                    }

                    double csgst=0.0;
                    if(!txtcsgst.getText().toString().equals("")){
                        csgst = Double.parseDouble(txtcsgst.getText().toString());
                    }
                    objItems.put("prCSGST", csgst);
                    objItems.put("prIGST", igst);
                    objItems.put("prHamali", txtHamali.getText().toString().trim());

                    if(checkboxIsActive.isChecked()){
                        objItems.put("prIsActive", "1");
                    }else{
                        objItems.put("prIsActive", "2");
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

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MasterInventoryActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void concateNamesDuringPrint() {
        /*String brandName ="";
        String units = "";
        String size ="";
        if(!brandName.equals("")) {
            brandName = txtBrandName.getText().toString().toUpperCase().trim().concat(" ");
        }
        units = txtUnits.getText().toString().toUpperCase().trim();
        size = txtSize.getText().toString().toUpperCase().trim();

        txtNameDuringPrint.setText(brandName.concat(size).concat(units));*/

        String brand="";
        brand = txtBrandName.getText().toString();

        String size="";
        size = txtSize.getText().toString();

        String units="";
        units = txtUnits.getText().toString();

        txtNameDuringPrint.setText(size+" "+units);
        txtNameDuringTransaction.setText(brand+" "+size+" "+units);


    }

    public void init(){
        imgAddBrand = (ImageView)findViewById(R.id.imgAddBrand);
        imgAddSubCategory = (ImageView)findViewById(R.id.imgAddSubCategory);
        imgAddUnits = (ImageView)findViewById(R.id.imgAddUnits);
        txtBrandName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtBrandName);
        txtSubCategory = (AppCompatAutoCompleteTextView)findViewById(R.id.txtSubCategory);
        txtSize = (AppCompatAutoCompleteTextView)findViewById(R.id.txtSize);
        txtNameDuringPrint = (AppCompatAutoCompleteTextView)findViewById(R.id.txtNameDuringPrint);
        txtNameDuringTransaction = (AppCompatAutoCompleteTextView)findViewById(R.id.txtNameDuringTransaction);
        txtPurchasePrice = (AppCompatAutoCompleteTextView)findViewById(R.id.txtPurchasePrice);
        txtSellingPrice = (AppCompatAutoCompleteTextView)findViewById(R.id.txtSellingPrice);
        txtcsgst = (AppCompatAutoCompleteTextView)findViewById(R.id.txtcsgst);
        txtigst = (AppCompatAutoCompleteTextView)findViewById(R.id.txtigst);
        txtHamali = (AppCompatAutoCompleteTextView)findViewById(R.id.txtHamali);
        txtUnits = (AppCompatAutoCompleteTextView)findViewById(R.id.txtUnits);
        lblMessage = (TextView)findViewById(R.id.lblMessage);
        btnSave =(Button)findViewById(R.id.btnSave);
        btnDelete =(Button)findViewById(R.id.btnDelete);
        checkboxIsActive = (CheckBox)findViewById(R.id.checkboxIsActive);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                nsBrand = data.getStringExtra("nsName");
                nsBrandId = data.getStringExtra("nsId");

                txtBrandName.setText(nsBrand);
                txtNameDuringPrint.setText(nsBrand);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }else if (requestCode==2){
            if(resultCode==RESULT_OK) {
                nsInventorySubCategory = data.getStringExtra("nsSubCategoryName");
                nsInventorySubCategoryId = data.getStringExtra("nsId");
                nsInventoryCategoryId = data.getStringExtra("nsCategoryId");

                txtSubCategory.setText(nsInventorySubCategory);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }else if (requestCode==3){
            if(resultCode==RESULT_OK) {
                nsUnit = data.getStringExtra("nsName");
                nsUnitId = data.getStringExtra("nsId");

                txtUnits.setText(nsUnit);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        /*String brandName ="";
        String units = "";
        String size ="";
        if(!brandName.equals("")) {
            brandName = txtBrandName.getText().toString().toUpperCase().trim().concat(" ");
        }
        units = txtUnits.getText().toString().toUpperCase().trim();
        size = txtSize.getText().toString().toUpperCase().trim();

        txtNameDuringPrint.setText(brandName.concat(size).concat(units));*/
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

                                if(jsonData.length()>0){
                                    JSONObject objData = jsonData.getJSONObject(0);

                                    txtBrandName.setText(objData.getString("nsBrand"));

                                    nsBrandId=objData.getString("nsInventoryBrandId");
                                    nsInventoryCategoryId=objData.getString("nsInventoryCategoryId");
                                    nsInventorySubCategoryId=objData.getString("nsInventorySubCategoryId");
                                    nsUnitId=objData.getString("nsInventoryUnitId");

                                    txtSubCategory.setText(objData.getString("nsSubCategory"));
                                    txtUnits.setText(objData.getString("nsUnit"));
                                    txtSize.setText(objData.getString("nsUnitSize"));
                                    txtNameDuringPrint.setText(objData.getString("nsNameDuringPrint"));
                                    txtNameDuringTransaction.setText(objData.getString("nsNameDuringTransaction"));
                                    txtPurchasePrice.setText(objData.getString("nsPurchasePrice"));
                                    txtSellingPrice.setText(objData.getString("nsSellingPrice"));
                                    txtcsgst.setText(objData.getString("nsCSGST"));
                                    txtigst.setText(objData.getString("nsIGST"));
                                    if(!objData.isNull("nsHamali")) {
                                        txtHamali.setText(objData.getString("nsHamali"));
                                    }

                                    if(objData.getString("nsIsActive").equals("1")){
                                        checkboxIsActive.setChecked(true);
                                    }else{
                                        checkboxIsActive.setChecked(false);
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

    private void displayAutoEditText(final String mModule) {
        progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(MasterInventoryActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonDataBrand = jsonObject.getJSONArray("dataBrand");
                                final JSONArray jsonDataSubCategory = jsonObject.getJSONArray("dataSubCategory");
                                final JSONArray jsonDataUnits = jsonObject.getJSONArray("dataUnits");

                                //Brand Name Type Ahead
                                if(jsonDataBrand.length()>0) {
                                    final String[] strArrayBrand = new String[jsonDataBrand.length()];
                                    for (int i = 0; i < jsonDataBrand.length(); i++) {
                                        JSONObject objData = jsonDataBrand.getJSONObject(i);
                                        strArrayBrand[i]=objData.getString("nsName");
                                    }
                                    ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayBrand);
                                    txtBrandName.setAdapter(adapterBrand);
                                    txtBrandName.setThreshold(1);

                                    txtBrandName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String selection = (String) parent.getItemAtPosition(position);

                                            for (int i = 0; i<strArrayBrand.length; i++) {
                                                if (strArrayBrand[i].equals(selection)) {
                                                    try {
                                                        JSONObject objDataArea = jsonDataBrand.getJSONObject(i);
                                                        nsBrandId = objDataArea.getString("nsId");

                                                        //Toast.makeText(MasterInventoryActivity.this, ""+nsBrandId, Toast.LENGTH_SHORT).show();

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    break;
                                                }
                                            }

                                        }
                                    });

                                    txtBrandName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {

                                            if(!b) {
                                                imgAddBrand.setVisibility(View.GONE);
                                                String str = txtBrandName.getText().toString();
                                                ListAdapter listAdapter = txtBrandName.getAdapter();
                                                for(int i = 0; i < listAdapter.getCount(); i++) {
                                                    String temp = listAdapter.getItem(i).toString();
                                                    if(txtBrandName.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                        return;
                                                    }
                                                }
                                                nsBrandId="";
                                                txtBrandName.setError("Invalid");
                                            }else{
                                                imgAddBrand.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }


                                //SubCategory Type Ahead
                                if(jsonDataSubCategory.length()>0){
                                    final String[] strArraySubcategory = new String[jsonDataSubCategory.length()];
                                    for (int i = 0; i < jsonDataSubCategory.length(); i++) {
                                        JSONObject objData = jsonDataSubCategory.getJSONObject(i);
                                        strArraySubcategory[i]=objData.getString("nsName");
                                    }
                                    ArrayAdapter<String> adapterSubCategory = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArraySubcategory);
                                    txtSubCategory.setAdapter(adapterSubCategory);
                                    txtSubCategory.setThreshold(1);

                                    txtSubCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String selection = (String) parent.getItemAtPosition(position);

                                            for (int i = 0; i<strArraySubcategory.length; i++) {
                                                if (strArraySubcategory[i].equals(selection)) {
                                                    try {
                                                        JSONObject objDataSubCategory = jsonDataSubCategory.getJSONObject(i);
                                                        nsInventorySubCategoryId = objDataSubCategory.getString("nsId");
                                                        nsInventoryCategoryId = objDataSubCategory.getString("nsCategoryId");
                                                        //Toast.makeText(MasterInventoryActivity.this, nsInventorySubCategoryId+" "+nsInventoryCategoryId, Toast.LENGTH_SHORT).show();

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    break;
                                                }
                                            }

                                        }
                                    });

                                    txtSubCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {

                                            if(!b) {
                                                imgAddSubCategory.setVisibility(View.GONE);
                                                String str = txtSubCategory.getText().toString();
                                                ListAdapter listAdapter = txtSubCategory.getAdapter();
                                                for(int i = 0; i < listAdapter.getCount(); i++) {
                                                    String temp = listAdapter.getItem(i).toString();
                                                    if(txtSubCategory.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                        return;
                                                    }
                                                }
                                                nsInventorySubCategoryId="";
                                                nsInventoryCategoryId="";
                                                txtSubCategory.setError("Invalid");
                                            }else{
                                                imgAddSubCategory.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }

                                //Units Type Ahead
                                if(jsonDataUnits.length()>0){
                                    final String[] strArrayUnits = new String[jsonDataUnits.length()];
                                    for (int i = 0; i < jsonDataUnits.length(); i++) {
                                        JSONObject objData = jsonDataUnits.getJSONObject(i);
                                        strArrayUnits[i]=objData.getString("nsName");
                                    }
                                    ArrayAdapter<String> adapterUnits = new ArrayAdapter<String>(getApplicationContext()
                                            ,R.layout.r_dropdown_list, strArrayUnits);
                                    txtUnits.setAdapter(adapterUnits);
                                    txtUnits.setThreshold(1);

                                    txtUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String selection = (String) parent.getItemAtPosition(position);

                                            for (int i = 0; i<strArrayUnits.length; i++) {
                                                if (strArrayUnits[i].equals(selection)) {
                                                    try {
                                                        JSONObject objDataUnits = jsonDataUnits.getJSONObject(i);
                                                        nsUnitId = objDataUnits.getString("nsId");
                                                        //Toast.makeText(MasterInventoryActivity.this, ""+nsUnitId, Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                }
                                            }

                                        }
                                    });

                                    txtUnits.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {

                                            if(!b) {
                                                imgAddUnits.setVisibility(View.GONE);
                                                String str = txtUnits.getText().toString();
                                                ListAdapter listAdapter = txtUnits.getAdapter();
                                                for(int i = 0; i < listAdapter.getCount(); i++) {
                                                    String temp = listAdapter.getItem(i).toString();
                                                    if(txtUnits.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                        return;
                                                    }
                                                }
                                                nsUnitId="";
                                                txtUnits.setError("Invalid");
                                            }else{
                                                imgAddUnits.setVisibility(View.VISIBLE);
                                            }
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

                params.put("module", mModule);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        displayAutoEditText(nsModule);
    }
}
