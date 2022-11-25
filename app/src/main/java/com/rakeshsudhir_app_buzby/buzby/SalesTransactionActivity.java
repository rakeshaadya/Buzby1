
package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ListAdapter;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalesTransactionActivity extends BaseActivity implements MyDataAdapter.myAdapterListener{

    private TextView lblVoucherNo,lblStatus,lblLedgerBalance,accountingStockPhysicalStock,lblTotalQty,lblGrandTotal;
    private ImageView imgTelephone,imgInfo,imgOldTransactions,imgLedgerTransactions,imgReceipt,imgPayment,imgGST;
    private AppCompatAutoCompleteTextView txtDate,txtLocation,txtLedgerName,txtLedgerNameAlias,txtAutoCompleteInventoryName,
            txtAutoCompleteInventoryQuantity,
            txtAutoCompleteInventorySellingPrice,txtReminderDate;
    private LinearLayout linearLayoutReceiptAmount,layoutLinearCSIGST;
    private Button btn_add_cart_details,btn_del_party_details,btn_save,btn_delete,btn_reprint,btn_i_details;
    private LinearLayout tableCartItems;
    private TextInputEditText txtExtraChargeType,txtExtraCharge, txtTotalAmount,
            txtPaidAmount, txtBalanceAmount,txtReceiptAmount;
    private EditText txtRemarks;
    private AppCompatSpinner spinnerPaymentMode;
    private CheckBox checkboxPrint,checkboxDeliver,checkboxTransport;

    CheckBox checkboxPrintGp;

    private DatePickerDialog datePickerDialog;
    private ProgressDialog progress;
    private String nsSubModule="0";
    private String nsModule="",nsId="-1",nsLocationId="",nsLedgerId="0";
    String area="",areaid="",city="",cityid="";
    String nsUnitSize="",nsSellingPrice="",nsInventoryId="",nsSellingPrice1="";
    JSONArray jsonCartItems = new JSONArray();
    JSONArray jsonCartItemsch = new JSONArray();
    private String cartSellingPrice="",nsBaseSellingPrice="";
    int n = -1;
    String chPrice="0";
    private ArrayList<String> myDataListColumns;
    private RecyclerView recyclerView;
    private Context context;
    private MyDataAdapter myDataAdapter;
    private String mTimeAfterInsert="";
    String isCC="",nsReferenceId="";
    String nsTransactionStatus="0";
    String stateCode="0";
    String nsCSGST="0",nsIGST="0";

    double Ledgerbalance;

    int restrictedGlobal=0;
    double sellPrice = 0.0;

    Intent data1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_transaction);
        init();

        btn_del_party_details.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);
        btn_reprint.setVisibility(View.GONE);

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

        if(getIntent().hasExtra("nsTransactionStatus")){
            nsTransactionStatus = getIntent().getStringExtra("nsTransactionStatus");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sales");
        setSupportActionBar(toolbar);

        final Calendar cldr1 = Calendar.getInstance();
        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,SalesTransactionActivity.this,txtDate);
            }
        });

        txtReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(datePickerDialog,SalesTransactionActivity.this,txtReminderDate);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        LinearLayoutManager userManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(userManager4);
        myDataListColumns = new ArrayList<>();
        context=this;

        txtLocation.setText(preferenceConfig.readlocationName());
        nsLocationId = preferenceConfig.readLocationId();

        String[] list = {"Payment Type",
                "PhonePe",
                "Bank",
                "Cheque"
                };

        String[] categories = new String[list.length];
        for(int i=0;i<list.length;i++) {
            categories[i] = list[i];
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SalesTransactionActivity.this, android.R.layout.simple_spinner_item, categories){
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
        spinnerPaymentMode.setAdapter(dataAdapter);

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SalesTransactionActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_location");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,1);
            }
        });

        txtLedgerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SalesTransactionActivity.this,ListActivity.class);
                i.putExtra("nsModule","master_ledger");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,2);
            }
        });

        displayDataForAutoEditText();
        if(Integer.parseInt(nsId)>0){
            displayData();
            btn_delete.setVisibility(View.VISIBLE);
            btn_save.setText("Update");
            txtDate.setEnabled(false);
            txtDate.setAlpha(0.5f);
            txtDate.setClickable(false);
            checkboxDeliver.setVisibility(View.GONE);
            linearLayoutReceiptAmount.setVisibility(View.GONE);
            btn_reprint.setVisibility(View.VISIBLE);
            imgPayment.setVisibility(View.GONE);
        }

        if(!preferenceConfig.readUserPosition().equals("1")){
            //dateLocationLayout.setVisibility(View.INVISIBLE);
            txtDate.setEnabled(false);
            txtLocation.setEnabled(false);
        }

        imgTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SalesTransactionActivity.this,ListActivity.class);
                i.putExtra("nsModule","transactions_in_out");
                i.putExtra("nsSubModule","transaction_sales_order");
                i.putExtra("nsId","-1");
                startActivity(i);
            }
        });



        if(!preferenceConfig.readUserPosition().equals("1")) {
            if (getIntent().hasExtra("nsHideButtons")) {
                btn_save.setVisibility(View.GONE);
                btn_delete.setVisibility(View.GONE);
            }
        }

        /*txtAutoCompleteInventorySellingPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!txtAutoCompleteInventorySellingPrice.getText().toString().equals("")) {
                    sellPrice = Double.parseDouble(txtAutoCompleteInventorySellingPrice.getText().toString());
                }
            }
        });*/



        btn_add_cart_details.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                boolean isValidated = true;



                if(!txtAutoCompleteInventorySellingPrice.getText().toString().trim().equals("")){
                    sellPrice = Double.parseDouble(txtAutoCompleteInventorySellingPrice.getText().toString().trim());
                }else{
                    sellPrice=0.0;
                }

                if(txtLedgerName.getText().toString().trim().matches("")){

                    errorDialog("Customer Name Field Cannot be empty");
                    isValidated=false;
                }else if(txtAutoCompleteInventoryName.getText().toString().trim().matches("")){

                    errorDialog("Inventory Name Field Cannot be empty");
                    isValidated=false;
                }else if(txtAutoCompleteInventoryQuantity.getText().toString().trim().matches("")){

                    errorDialog("Quantity Field Cannot be empty");
                    isValidated=false;
                }else if(txtAutoCompleteInventorySellingPrice.getText().toString().trim().matches("")){
                    errorDialog("Price Field Cannot be empty");
                    isValidated=false;
                }

                if(isValidated==false){
                    return;
                }

               //DialogUtility.showDialog(SalesTransactionActivity.this,sellPrice+" "+nsSellingPrice1,"");

                if(sellPrice<(Double.parseDouble(nsSellingPrice1)-((Double.parseDouble(nsSellingPrice1)*2)/100))){
                    android.app.AlertDialog alertbox = new android.app.AlertDialog.Builder(SalesTransactionActivity.this)
                            .setMessage("Price Variation - Low \n"
                                    +"Amount Entered "+sellPrice)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cartSellingPrice = txtAutoCompleteInventorySellingPrice.getText().toString().trim();
                                    addItemsToCart();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cartSellingPrice = nsSellingPrice1;
                                    addItemsToCart();
                                }
                            })
                            .show();

                }else if(sellPrice>(Double.parseDouble(nsSellingPrice1)+((Double.parseDouble(nsSellingPrice1)*2)/100))){

                    android.app.AlertDialog alertbox = new android.app.AlertDialog.Builder(SalesTransactionActivity.this)
                            .setMessage("Price Variation - High \n"
                                    +"Amount Entered "+sellPrice)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cartSellingPrice = txtAutoCompleteInventorySellingPrice.getText().toString().trim();
                                    addItemsToCart();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cartSellingPrice = nsSellingPrice1;
                                    addItemsToCart();
                                }
                            })
                            .show();

                }else{
                    cartSellingPrice = txtAutoCompleteInventorySellingPrice.getText().toString().trim();
                    addItemsToCart();
                }

            }
        });

        btn_del_party_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonCartItems.remove(n);
                n=-1;
                for(int i=0;i<jsonCartItems.length();i++){
                    try {
                        //JSONObject objData = jsonPartyCartItems.getJSONObject(i);
                        JSONObject itemArr = (JSONObject)jsonCartItems.get(i);
                        itemArr.put("nsSelectedPosition", i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                calculateTotal();
                myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonCartItems, (MyDataAdapter.myAdapterListener) context, "cart_items","");
                recyclerView.setAdapter(myDataAdapter);

                if(jsonCartItems.length()<1){
                    tableCartItems.setVisibility(View.GONE);
                }

                txtAutoCompleteInventoryName.setText("");
                txtAutoCompleteInventoryQuantity.setText("");
                txtAutoCompleteInventorySellingPrice.setText("");
                nsInventoryId="";
                nsSellingPrice="";
                nsSellingPrice1="";
                nsBaseSellingPrice="";
                nsUnitSize="";
                btn_i_details.setVisibility(View.GONE);

            }
        });

        txtPaidAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPaidAmount.setText(txtTotalAmount.getText().toString().trim());
            }
        });


        txtExtraCharge.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(isCC.equals("1")){
                    txtPaidAmount.setText("0");
                }else{
                    txtPaidAmount.setText(txtTotalAmount.getText().toString().trim());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                calculateTotal();
                txtPaidAmount.setText(txtTotalAmount.getText().toString());

                if(txtExtraCharge.getText().toString().length()>0){
                    checkboxTransport.setChecked(true);
                }else{
                    checkboxTransport.setChecked(false);
                }
            }
        });

        txtPaidAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

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



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidated = true;
                double balanceBox=0.0;
                int receiptPrintStatus = 2;
                if(!txtBalanceAmount.getText().toString().trim().equals("")){
                    balanceBox = Double.parseDouble(txtBalanceAmount.getText().toString());
                }

                if(jsonCartItems.length()==0){
                    errorDialog("Cart Cannot be Empty");
                    isValidated = false;
                }else if(balanceBox!=0.0){
                    if(txtRemarks.getText().toString().trim().matches("")) {
                        errorDialog("Credit Customer\nPlease Enter Remarks");
                        txtRemarks.requestFocus();
                        isValidated = false;
                    }
                }else if(spinnerPaymentMode.getSelectedItemPosition()>0 && balanceBox<1){
                    //if(Integer.parseInt(txtBalanceAmount.getText().toString())<1){
                    errorDialog("Balance Amount Cannot be Zero if Payment Mode is PhonePe");
                    txtPaidAmount.requestFocus();
                    txtPaidAmount.selectAll();
                    isValidated=false;
                    //}
                }

                if(isValidated==false){
                    return;
                }

                btn_save.setEnabled(false);
                btn_save.setAlpha(.5f);
                btn_save.setText("wait...");

                try {
                    JSONObject objItems = new JSONObject();
                    final JSONArray jsonArrObjItems = new JSONArray();
                    Date d = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString());

                    objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(d));
                    objItems.put("prLocationId", nsLocationId);
                    objItems.put("prLedgerId", nsLedgerId);
                    objItems.put("prLedgerAlias", txtLedgerNameAlias.getText().toString().toUpperCase());
                    objItems.put("prAdditionalChargeType", txtExtraChargeType.getText().toString().toUpperCase());
                    objItems.put("prAdditionalCharges", txtExtraCharge.getText().toString());
                    objItems.put("prTotalAmount", txtTotalAmount.getText().toString());
                    objItems.put("prPaidAmount", txtPaidAmount.getText().toString());
                    if(Integer.parseInt(nsId)>0) {
                        if(!nsReferenceId.equals("") && !nsReferenceId.equals("0"))
                        objItems.put("prReferenceId", nsReferenceId);
                    }
                    double ta = 0.0;
                    if(!txtTotalAmount.getText().toString().equals("")) {
                        ta = Double.parseDouble(txtTotalAmount.getText().toString());
                    }

                    double pa=0.0;
                    if(!txtPaidAmount.getText().toString().equals("")) {
                        pa = Double.parseDouble(txtPaidAmount.getText().toString());
                    }
                    //Toast.makeText(SalesTransactionActivity.this, ""+(ta-pa), Toast.LENGTH_SHORT).show();
                    if((ta-pa)==0){
                        objItems.put("prDisplayStatusId", "1");
                    }else if(spinnerPaymentMode.getSelectedItemPosition()==1){
                        objItems.put("prDisplayStatusId", "1");
                    }
                    else{
                        objItems.put("prDisplayStatusId", "2");
                    }

                    if(Integer.parseInt(nsId)<0){
                        double receiptAmt = 0.0;
                        double receiptAmt1 = 0.0;
                        if(!txtReceiptAmount.getText().toString().equals("")) {
                            receiptAmt1 = Double.parseDouble(txtReceiptAmount.getText().toString());
                        }
                        if(!txtReceiptAmount.getText().toString().equals("") || receiptAmt1!=0){
                            receiptAmt = Double.parseDouble(txtReceiptAmount.getText().toString());

                            receiptPrintStatus=1;
                        }
                        if(receiptAmt>0){
                            objItems.put("prReceiptAmount", ""+(0-receiptAmt));
                            if(Ledgerbalance==receiptAmt){
                                objItems.put("prReceiptKalamid", "1");
                            }
                        }


                    }

                    objItems.put("prTransactionTypeId", "200");//Sales
                    objItems.put("prTransactionStatus", "1");//1.Undelivered,2.PartiallyDelivered 3.Delivered
                    objItems.put("prStatus", "1");//2.Deleted, 1.Active
                    //if(spinnerPaymentMode.getSelectedItemPosition()!=0) {
                    if((ta-pa)==0 && spinnerPaymentMode.getSelectedItemPosition()==0){
                        objItems.put("prPaymentTypeId", 5);//1:Cash 2:PhonePe; 3.Bank, 4.Cheque
                    }else{
                        if((ta-pa)!=0 && spinnerPaymentMode.getSelectedItemPosition()==0){
                            objItems.put("prPaymentTypeId", 6);
                        }else {
                            objItems.put("prPaymentTypeId", (spinnerPaymentMode.getSelectedItemPosition() + 1));//1:Cash 2:PhonePe; 3.Bank, 4.Cheque
                        }
                    }

                    //}
                    objItems.put("prRemarks", txtRemarks.getText().toString().trim());
                    objItems.put("prVerifiedStatus","2");//1.Verified 2.Not Verified
                    if(!txtReminderDate.getText().toString().equals("")){
                        Date rd = ConvertFunctions.convertIndianDateStringToDate(txtReminderDate.getText().toString().trim());
                        objItems.put("prReminderDate",new SimpleDateFormat("yyyy-MM-dd").format(rd));
                    }
                    objItems.put("prConvertToSales", "2");
                    if(checkboxDeliver.isChecked()){
                        objItems.put("prDeliverItemsInSales", "1");
                    }

                    if(checkboxTransport.isChecked()){
                        objItems.put("PrTransportOrder",1);
                    }else{
                        objItems.put("PrTransportOrder",2);
                    }

                    objItems.put("prCartItems", jsonCartItems.toString(4));

                    jsonArrObjItems.put(objItems);

                    btn_save.setEnabled(true);
                    btn_save.setAlpha(1f);
                    btn_save.setText("SAVE");

                    //DialogUtility.showDialog(SalesTransactionActivity.this,jsonArrObjItems.toString(4),"Alert");



                    if(Integer.parseInt(nsTransactionStatus)>1){
                        android.app.AlertDialog alertbox = new android.app.AlertDialog.Builder(SalesTransactionActivity.this)
                                .setMessage("Some items or all items are delivered, please confirm whether you still would like to update the changes?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    // do something when the button is clicked
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {

                                            int printBill;

                                            if(checkboxPrint.isChecked()){
                                                printBill=1;
                                            }else{
                                                printBill=2;
                                            }

                                            int printGP;

                                            if(checkboxPrintGp.isChecked()){
                                                printGP=1;
                                            }else{
                                                printGP=2;
                                            }
                                            insertData(nsId,
                                                    jsonArrObjItems.toString(4),
                                                    nsModule,"", 2, 2,2);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    // do something when the button is clicked
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                })
                                .show();
                    }else {

                        //DialogUtility.showDialog(SalesTransactionActivity.this,""+(spinnerPaymentMode.getSelectedItemPosition()+1),"");
                        int printBill;

                        if(checkboxPrint.isChecked()){
                            printBill=1;
                        }else{
                            printBill=2;
                        }

                        int printGP;

                        if(checkboxPrintGp.isChecked()){
                            printGP=1;
                        }else{
                            printGP=2;
                        }


                        //Toast.makeText(SalesTransactionActivity.this, printBill+" - "+printGP, Toast.LENGTH_SHORT).show();

                        insertData(nsId,
                                jsonArrObjItems.toString(4),
                                nsModule,"",printGP,printBill,receiptPrintStatus);
                        //DialogUtility.showDialog(SalesTransactionActivity.this,jsonArrObjItems.toString(4),"");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SalesTransactionActivity.this, "Save "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(SalesTransactionActivity.this,ReceiptPaymentActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("transactionType","400");
                i.putExtra("nsModule","transaction_receipt_payment");
                i.putExtra("nsId","-1");
                if(!txtLedgerName.getText().toString().equals("")) {
                    i.putExtra("customerid", nsLedgerId);
                    i.putExtra("customerName", txtLedgerName.getText().toString());
                    i.putExtra("restrictedGlobal", String.valueOf(restrictedGlobal));
                    i.putExtra("customerBalance", lblLedgerBalance.getText().toString());

                }
                startActivity(i);
            }
        });

        /*imgPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(SalesTransactionActivity.this,ReceiptPaymentActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("transactionType","500");
                i.putExtra("nsModule","transaction_receipt_payment");
                i.putExtra("nsId","-1");
                if(!txtLedgerName.getText().toString().equals("")) {
                    i.putExtra("customerid", nsLedgerId);
                    i.putExtra("customerName", txtLedgerName.getText().toString());
                    i.putExtra("restrictedGlobal", String.valueOf(restrictedGlobal));
                }
                startActivity(i);
            }
        });*/



        btn_i_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySalesTrancationAutoFetchPrice(nsLedgerId,nsInventoryId);
            }
        });

        /*if(Integer.parseInt(nsId)>0){
            displayData();
            btn_delete.setVisibility(View.VISIBLE);
            btn_save.setText("Update");
            txtDate.setEnabled(false);
            txtDate.setAlpha(0.5f);
            txtDate.setClickable(false);
            checkboxDeliver.setVisibility(View.GONE);
            layoutReceiptAmount.setVisibility(View.GONE);
        }*/

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(nsId,"0",nsModule,0);
            }
        });

        if(getIntent().hasExtra("nsIsDeleted")){
            btn_save.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
        }

        //Toast.makeText(this, ""+Integer.parseInt(nsLedgerId), Toast.LENGTH_SHORT).show();

        if(!nsInventoryId.equals("") && !nsLedgerId.equals("")) {
            btn_i_details.setVisibility(View.VISIBLE);
        }else{
            btn_i_details.setVisibility(View.GONE);
        }




        btn_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int printBill;

                if(checkboxPrint.isChecked()){
                    printBill=1;
                }else{
                    printBill=2;
                }

                int printGP;

                if(checkboxPrintGp.isChecked()){
                    printGP=1;
                }else{
                    printGP=2;
                }
                functionPrint.displayDataForPrint(SalesTransactionActivity.this, nsModule, nsId,2,1,2, printBill, printGP);
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SalesTransactionActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void addItemsToCart(){
        try {
            final JSONObject objProduct = new JSONObject();

            objProduct.put("nsInventoryName", txtAutoCompleteInventoryName.getText().toString().trim());
            objProduct.put("nsInventoryQuantity", txtAutoCompleteInventoryQuantity.getText().toString().trim());


            objProduct.put("nsUnitSize", nsUnitSize);
            objProduct.put("nsInventoryId", nsInventoryId);




            //DialogUtility.showDialog(SalesTransactionActivity.this,jsonCartItems.toString(4),"");



            objProduct.put("nsInventoryPrice", cartSellingPrice);
            objProduct.put("nsBaseSellingPrice", nsBaseSellingPrice);
            double fivePercentDiff = 0.0;
            double baseAmount = 0.0;
            baseAmount = Double.parseDouble(nsBaseSellingPrice);
            fivePercentDiff = (baseAmount*5.5)/100;
            double minAmount =0.0,maxAmount=0.0;
            minAmount = baseAmount-fivePercentDiff;
            maxAmount = baseAmount+fivePercentDiff;

            //DialogUtility.showDialog(SalesTransactionActivity.this,minAmount+" "+maxAmount,"");

            if(minAmount>Double.parseDouble(cartSellingPrice) || maxAmount<Double.parseDouble(cartSellingPrice)){
                objProduct.put("nsPriceVariation", "1");
            }else{
                objProduct.put("nsPriceVariation", "0");

            }



            if (n == -1) {

                if(jsonCartItems.toString().contains("\"nsInventoryName\":\""+txtAutoCompleteInventoryName.getText().toString()+"\"")){
                    Toast.makeText(SalesTransactionActivity.this, "Duplicate Inventory Name", Toast.LENGTH_LONG).show();
                }else{
                    objProduct.put("nsSelectedPosition", jsonCartItems.length());
                    jsonCartItems.put(objProduct);
                }
            }else{
                objProduct.put("nsSelectedPosition", n);
                jsonCartItems.put(n, objProduct);
            }

            //DialogUtility.showDialog(SalesTransactionActivity.this,jsonCartItems.toString(4),"");
            nsSellingPrice=txtAutoCompleteInventorySellingPrice.getText().toString();
            myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonCartItems, (MyDataAdapter.myAdapterListener) context, "cart_items","");
            recyclerView.setAdapter(myDataAdapter);
            n=-1;
            tableCartItems.setVisibility(View.VISIBLE);


            txtAutoCompleteInventoryName.setText("");
            txtAutoCompleteInventoryQuantity.setText("");
            txtAutoCompleteInventorySellingPrice.setText("");
            btn_i_details.setVisibility(View.GONE);

            calculateTotal();

            if(isCC.equals("1")){
                txtPaidAmount.setText("0");
            }else{
                txtPaidAmount.setText(txtTotalAmount.getText().toString().trim());
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SalesTransactionActivity.this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        txtAutoCompleteInventoryName.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //vehicleNo = data.getStringExtra("nsRegistrationNumber");
                nsLocationId = data.getStringExtra("nsId");

                txtLocation.setText(data.getStringExtra("nsName"));
                txtLocation.setError(null);
                displayDataForAutoEditText();

            }
            if (requestCode == RESULT_CANCELED) {

            }
        }else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                //vehicleNo = data.getStringExtra("nsRegistrationNumber");


                /*DialogUtility.showDialog(SalesTransactionActivity.this,
                        data.getStringExtra("nsName")+"\n"
                                +data.getStringExtra("nsId")+"\n"
                                +data.getStringExtra("nsIsBlocked")+"\n"
                                +data.getStringExtra("nsIsGSTCustomer")+"\n"
                                +data.getStringExtra("nsIsRestricted")+"\n"
                                +data.getStringExtra("nsIsCreditCustomer")+"\n"
                                +data.getStringExtra("nsLedgerCategoryId")+"\n"
                                +data.getStringExtra("nsBalance")+"\n"

                                +data.getStringExtra("nsCompany")+"\n"
                                +data.getStringExtra("nsGSTIN")+"\n"
                                +data.getStringExtra("nsArea")+"\n"
                                +data.getStringExtra("nsAreaId")+"\n"
                                +data.getStringExtra("nsCity")+"\n"
                                +data.getStringExtra("nsCityId")+"\n"

                        ,"");*/



                String customerStatus="";
                if(data.getStringExtra("nsIsBlocked").equals("1")){
                    customerStatus+="BLOCKED";
                }

                if(data.getStringExtra("nsIsBlocked").equals("1") && data.getStringExtra("nsIsGSTCustomer").equals("1")){
                    customerStatus+=" & ";
                }
                String tempCompany="";
                tempCompany+=data.getStringExtra("nsCompany")+"\n"+data.getStringExtra("nsGSTIN");



                if(data.getStringExtra("nsIsGSTCustomer").equals("1")){
                    customerStatus+="GST";
                    imgGST.setVisibility(View.VISIBLE);
                    final String finalTempCompany = tempCompany;
                    imgGST.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtility.showDialog(SalesTransactionActivity.this, finalTempCompany,"GST Name");
                        }
                    });
                }
                if(data.getStringExtra("nsIsBlocked").equals("1") || data.getStringExtra("nsIsGSTCustomer").equals("1")) {
                    lblStatus.setText(customerStatus);
                    DialogUtility.showDialog(SalesTransactionActivity.this,customerStatus,"Alert");
                }else{
                    lblStatus.setText("");
                }

                isCC = data.getStringExtra("nsIsCreditCustomer");
                if(data.getStringExtra("nsIsCreditCustomer").equals("1")){
                    txtLedgerName.setTextColor(Color.parseColor("#C62828"));
                }else{
                    txtLedgerName.setTextColor(Color.parseColor("#000000"));
                }

                /*if(isCC.equals("1")){
                    txtPaidAmount.setText("0");
                }else{
                    txtPaidAmount.setText(txtTotalAmount.getText().toString().trim());
                }*/



                if(!data.getStringExtra("nsAreaId").equals("")) {
                    area = " "+data.getStringExtra("nsArea");
                    areaid = data.getStringExtra("nsAreaId");
                }else{
                    area="";
                    areaid="";
                }

                if(!data.getStringExtra("nsCityId").equals("")) {
                    city=" "+data.getStringExtra("nsCity");
                    cityid=data.getStringExtra("nsCityId");
                }else{
                    city="";
                    cityid="";
                }

                if(!preferenceConfig.readUserPosition().equals("1")) {
                    if (Integer.parseInt(data.getStringExtra("nsIsRestricted")) == 1) {
                        imgLedgerTransactions.setVisibility(View.INVISIBLE);
                        imgOldTransactions.setVisibility(View.INVISIBLE);
                        imgInfo.setVisibility(View.INVISIBLE);
                    }else{
                        imgLedgerTransactions.setVisibility(View.VISIBLE);
                        imgOldTransactions.setVisibility(View.VISIBLE);
                        imgInfo.setVisibility(View.VISIBLE);
                    }
                }

                Ledgerbalance = Double.parseDouble(data.getStringExtra("nsBalance"));

                if(Ledgerbalance>0){
                    lblLedgerBalance.setTextColor(Color.parseColor("#C62828"));
                }else{
                    lblLedgerBalance.setTextColor(Color.parseColor("#1B5E20"));
                }
                lblLedgerBalance.setText(data.getStringExtra("nsBalance"));

                restrictedGlobal = Integer.parseInt(data.getStringExtra("nsIsRestricted"));

                stateCode = data.getStringExtra("nsStateCode");


               // Toast.makeText(this, "RVS "+data.getStringExtra("nsIsRestricted"), Toast.LENGTH_SHORT).show();

                if(jsonCartItems.length()>0){
                    if(isCC.equals("1")){
                        txtPaidAmount.setText("0");
                    }else{
                        txtPaidAmount.setText(txtTotalAmount.getText().toString().trim());
                    }
                }


                nsLedgerId = data.getStringExtra("nsId");

                if(!nsInventoryId.equals("") && !nsLedgerId.equals("")) {
                    btn_i_details.setVisibility(View.VISIBLE);
                }else{
                    btn_i_details.setVisibility(View.GONE);
                }

                final Date filterDateFrom,filterDateTo;
                Calendar cal=Calendar.getInstance();
                filterDateTo = cal.getTime();
                cal.add(Calendar.DATE,-90);
                filterDateFrom=cal.getTime();

                if(Integer.parseInt(nsLedgerId)>0){
                    imgInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displaySalesTrancationInfo(nsLedgerId);
                        }
                    });

                    imgOldTransactions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Toast.makeText(SalesTransactionActivity.this, nsLedgerId, Toast.LENGTH_SHORT).show();
                            displaySalesTrancation3MonthsOldInfo(nsLedgerId,new SimpleDateFormat("yyyy-MM-dd").format(filterDateFrom),
                                    new SimpleDateFormat("yyyy-MM-dd").format(filterDateTo));
                        }
                    });

                    data1 = data;

                    imgLedgerTransactions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(SalesTransactionActivity.this, ListActivity.class);
                            i.putExtra("nsModule","report_ledger_transaction");
                            i.putExtra("nsSubModule","master_reports");

                            i.putExtra("nsId",nsLedgerId);
                            i.putExtra("nsName",data1.getStringExtra("nsName"));
                            i.putExtra("nsCity",city);
                            startActivity(i);
                        }
                    });


                }



                txtLedgerName.setText(data.getStringExtra("nsName"));
                txtLedgerName.setError(null);
                txtAutoCompleteInventoryName.requestFocus();
            }
            if (requestCode == RESULT_CANCELED) {

            }
        }

    }

    private void errorDialog(String s){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SalesTransactionActivity.this);

        dlgAlert.setMessage(s);
        dlgAlert.setTitle("Error Message...");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }

    private void editDialogBeforeSave(String s){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SalesTransactionActivity.this);
        dlgAlert.setMessage(s);
        dlgAlert.setTitle("Message");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        dlgAlert.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dlgAlert.setNegativeButton("No", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
    }

    public void init(){
        lblVoucherNo = (TextView)findViewById(R.id.lblVoucherNo);
        lblStatus = (TextView)findViewById(R.id.lblStatus);
        lblLedgerBalance = (TextView)findViewById(R.id.lblLedgerBalance);
        accountingStockPhysicalStock = (TextView)findViewById(R.id.accountingStockPhysicalStock);
        lblTotalQty = (TextView)findViewById(R.id.lblTotalQty);
        lblGrandTotal = (TextView)findViewById(R.id.lblGrandTotal);

        imgTelephone = (ImageView)findViewById(R.id.imgTelephone);
        imgInfo = (ImageView)findViewById(R.id.imgInfo);
        imgOldTransactions = (ImageView)findViewById(R.id.imgOldTransactions);
        imgLedgerTransactions = (ImageView)findViewById(R.id.imgLedgerTransactions);
        imgReceipt = (ImageView)findViewById(R.id.imgReceipt);
        imgPayment = (ImageView)findViewById(R.id.imgPayment);
        imgGST = (ImageView)findViewById(R.id.imgGST);

        txtDate = (AppCompatAutoCompleteTextView)findViewById(R.id.txtDate);
        txtLocation = (AppCompatAutoCompleteTextView)findViewById(R.id.txtLocation);
        txtLedgerName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtLedgerName);
        txtLedgerNameAlias = (AppCompatAutoCompleteTextView)findViewById(R.id.txtLedgerNameAlias);
        txtAutoCompleteInventoryName = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventoryName);
        txtAutoCompleteInventoryQuantity = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventoryQuantity);
        txtAutoCompleteInventorySellingPrice = (AppCompatAutoCompleteTextView)findViewById(R.id.txtAutoCompleteInventorySellingPrice);
        txtReminderDate = (AppCompatAutoCompleteTextView)findViewById(R.id.txtReminderDate);


        linearLayoutReceiptAmount = (LinearLayout) findViewById(R.id.linearLayoutReceiptAmount);

        btn_add_cart_details = (Button) findViewById(R.id.btn_add_cart_details);
        btn_del_party_details = (Button) findViewById(R.id.btn_del_party_details);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_reprint = (Button) findViewById(R.id.btn_reprint);
        btn_i_details = (Button) findViewById(R.id.btn_i_details);


        txtExtraChargeType = (TextInputEditText) findViewById(R.id.txtExtraChargeType);
        txtExtraCharge = (TextInputEditText) findViewById(R.id.txtExtraCharge);
        txtTotalAmount = (TextInputEditText) findViewById(R.id.txtTotalAmount);
        txtPaidAmount = (TextInputEditText) findViewById(R.id.txtPaidAmount);
        txtBalanceAmount = (TextInputEditText) findViewById(R.id.txtBalanceAmount);
        txtReceiptAmount = (TextInputEditText) findViewById(R.id.txtReceiptAmount);

        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        spinnerPaymentMode = (AppCompatSpinner)findViewById(R.id.spinnerPaymentMode);
        checkboxPrint = (CheckBox)findViewById(R.id.checkboxPrint);
        checkboxPrintGp = (CheckBox)findViewById(R.id.checkboxPrintGp);
        checkboxDeliver = (CheckBox)findViewById(R.id.checkboxDeliver);
        checkboxTransport = (CheckBox)findViewById(R.id.checkboxTransport);
        tableCartItems = (LinearLayout)findViewById(R.id.tableCartItems);



    }

    private void displaySalesTrancationInfo(final String ledgerid) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);

        progress.setMessage("data Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(SalesTransactionActivity.this,response,"");
                        progress.dismiss();
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);

                                final JSONArray dataSalesPreviousTransaction = jsonObject.getJSONArray("dataSalesPreviousTransaction");
                                final JSONArray dataSalesItemPreviousTransaction = jsonObject.getJSONArray("dataSalesItemPreviousTransaction");

                                if(dataSalesPreviousTransaction.length()>0){
                                    String s = "";
                                    for (int i = 0; i < dataSalesItemPreviousTransaction.length(); i++) {
                                        s += dataSalesItemPreviousTransaction.getJSONObject(i).getString("nsNameDuringTransaction") + "\n" +
                                                "Qty : " + dataSalesItemPreviousTransaction.getJSONObject(i).getString("nsQuantity") + "\n" +
                                                "Rate : " + dataSalesItemPreviousTransaction.getJSONObject(i).getString("nsPrice") + "\n" +
                                                "Rem Qty : " + dataSalesItemPreviousTransaction.getJSONObject(i).getString("nsBalaceQuantity") + "\n";
                                        //s+="\n";
                                        s += "\n";
                                    }

                                    final String st = s;

                                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(dataSalesPreviousTransaction.getJSONObject(0).getString("nsDate"));
                                    DialogUtility.showDialog(SalesTransactionActivity.this,
                                            "Invoice Date : " + new SimpleDateFormat("dd-MMM-yyyy").format(d) + "\n" +
                                                    "Invoice Number : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsVoucherNo") + "\n" +
                                                    "Ledger Name : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsLedgerName") +" "+dataSalesPreviousTransaction.getJSONObject(0).getString("nsCity") + "\n" +
                                                    "Location : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsLocationName") + "\n" +
                                                    "Total Amount : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsTotalAmount") + "\n" +
                                                    "Paid Amount : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsPaidAmount") + "\n" +
                                                    "Payment Type : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsPaymentType") + "\n" +
                                                    "\n" +
                                                    st + "\n\n" + "Sales Remarks : " + dataSalesPreviousTransaction.getJSONObject(0).getString("nsRemarks") + "\n\n\n"

                                            , "Previous Invoice Details");

                                }else{
                                    DialogUtility.showDialog(SalesTransactionActivity.this,"No Transactions Recorded","Alert");
                                }



                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            Toast.makeText(SalesTransactionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText("auto ed 2"+error.getMessage());
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
                params.put("functionality", "fetch_table_data_old_info");
                //params.put("format", "list");
                params.put("prLedgerId",ledgerid);

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

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void displaySalesTrancationAutoFetchPrice(final String ledgerid,final String inventoryid) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);

        progress.setMessage("data Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(SalesOrderActivity.this,response,"");
                        progress.dismiss();
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);

                                final JSONArray dataSalesAutoFetchPrice = jsonObject.getJSONArray("dataSales3MonPreviousTransaction");

                                if(dataSalesAutoFetchPrice.length()>0){
                                    txtAutoCompleteInventorySellingPrice.setText(dataSalesAutoFetchPrice.getJSONObject(0).getString("nsOldInventoryPrice"));
                                }else{
                                    DialogUtility.showDialog(SalesTransactionActivity.this,"No Transactions Recorded","Alert");
                                }



                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            //lblMessage.setText("auto ed 1"+e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText("auto ed 2"+error.getMessage());
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
                params.put("functionality", "fetch_table_data_auto_price_info");
                //params.put("format", "list");
                params.put("prLedgerId",ledgerid);
                params.put("prInventoryId",inventoryid);

                Log.d("rakeshsudhirrrr", "getParams: "+params);

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

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void displaySalesTrancation3MonthsOldInfo(final String ledgerid,final String datefrom,final String dateto) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);

        progress.setMessage("data Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(SalesTransactionActivity.this,response,"");
                        progress.dismiss();
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);

                                final JSONArray dataSales3MonPreviousTransaction = jsonObject.getJSONArray("dataSales3MonPreviousTransaction");
                                final JSONArray dataSalesItem3MonPreviousTransaction = jsonObject.getJSONArray("dataSalesItem3MonPreviousTransaction");

                                if(dataSales3MonPreviousTransaction.length()>0){
                                    String s = "";
                                    for (int i = 0; i < dataSalesItem3MonPreviousTransaction.length(); i++) {
                                        Date d = ConvertFunctions.convertDatabaseDateStringToDate(dataSalesItem3MonPreviousTransaction.getJSONObject(i).getString("nsDate"));
                                        s += "Date : "+new SimpleDateFormat("dd-MM-yyyy").format(d) + "\n" +
                                                "Item : "+dataSalesItem3MonPreviousTransaction.getJSONObject(i).getString("nsNameDuringTransaction") + "\n" +
                                                "Qty : " + dataSalesItem3MonPreviousTransaction.getJSONObject(i).getString("nsQuantity") + "\n" +
                                                "Rate : " + dataSalesItem3MonPreviousTransaction.getJSONObject(i).getString("nsPrice") + "\n" +
                                                "Location : " + dataSalesItem3MonPreviousTransaction.getJSONObject(i).getString("nsLocation") + "\n"
                                        ;
                                        //s+="\n";
                                        s += "\n";
                                    }

                                    final String st = s;

                                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsDate"));
                                    DialogUtility.showDialog(SalesTransactionActivity.this,
                                            "Last Invoice Date : " + new SimpleDateFormat("dd-MMM-yyyy").format(d) + "\n" +
                                                    "Last Invoice Number : " + dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsVoucherNo") + "\n" +
                                                    "Ledger Name : " + dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsLedgerName") +" "+dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsCity") + "\n" +
                                                    "Last Invoice Location : " + dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsLocationName") + "\n" +
                                                    "Last Total Amount : " + dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsTotalAmount") + "\n" +
                                                    "Last Paid Amount : " + dataSales3MonPreviousTransaction.getJSONObject(0).getString("nsPaidAmount") + "\n" +

                                                    "\n" +
                                                    st +"\n\n"

                                            , "Previous Invoice Details");

                                }else{
                                    DialogUtility.showDialog(SalesTransactionActivity.this,"No Transactions Recorded","Alert");
                                }



                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            //lblMessage.setText("auto ed 1"+e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText("auto ed 2"+error.getMessage());
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
                params.put("functionality", "fetch_table_data_3m_old_info");
                //params.put("format", "list");
                params.put("prLedgerId",ledgerid);
                params.put("prFromDate",datefrom);
                params.put("prToDate",dateto);

                Log.d("rakeshsudhirrrr", "getParams: "+params);

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

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }


    private void displayDataForAutoEditText(/*final String locId*/) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);

        if(Integer.parseInt(nsId)<1) {
            progress.setMessage("data Loading Data...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(SalesTransactionActivity.this,response,"");
                        // lblMessage.setText(response);
                        progress.dismiss();
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                //lblMessage.setText(response);
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);

                                final JSONArray jsonInvName = jsonObject.getJSONArray("dataInventory");



                                final String[] strArrayInvName = new String[jsonInvName.length()];
                                for (int i = 0; i < jsonInvName.length(); i++) {
                                    JSONObject objData = jsonInvName.getJSONObject(i);
                                    strArrayInvName[i] = objData.getString("nsBrand");
                                    if(objData.getInt("nsId")==14){
                                        chPrice=objData.getString("nsSellingPrice");
                                    }
                                }

                                ArrayAdapter<String> adapterInvName = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayInvName);
                                txtAutoCompleteInventoryName.setAdapter(adapterInvName);
                                txtAutoCompleteInventoryName.setThreshold(1);

                                txtAutoCompleteInventoryName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selection =(String)parent.getItemAtPosition(position);
                                        //Toast.makeText(Sales.this, "rererere "+jsonInvName, Toast.LENGTH_SHORT).show();
                                        for(int i = 0; i<strArrayInvName.length; i++){
                                            if(strArrayInvName[i].equals(selection)){
                                                try {
                                                    JSONObject objDataBrand = jsonInvName.getJSONObject(i);

                                                    nsSellingPrice = objDataBrand.getString("nsSellingPrice");
                                                    nsSellingPrice1 = objDataBrand.getString("nsSellingPrice");

                                                    txtAutoCompleteInventorySellingPrice.setText(nsSellingPrice);
                                                    nsUnitSize = objDataBrand.getString("nsUnitSize");
                                                    nsInventoryId = objDataBrand.getString("nsId");
                                                    nsBaseSellingPrice = objDataBrand.getString("nsSellingPrice");
                                                    String nsAccountingStock="",nsPhysicalStock="";
                                                    nsAccountingStock = objDataBrand.getString("nsAccountingStock");
                                                    nsPhysicalStock = objDataBrand.getString("nsPhysicalStock");
                                                    if(objDataBrand.getInt("nsSalesOrderStock")!=0){
                                                        accountingStockPhysicalStock.setText(nsAccountingStock+" ("+objDataBrand.getString("nsSalesOrderStock")+")"+"/"+nsPhysicalStock);
                                                    }else {
                                                        accountingStockPhysicalStock.setText(nsAccountingStock + "/" + nsPhysicalStock);
                                                    }
                                                    //accountingStockPhysicalStock.setText(nsAccountingStock+" ("+objDataBrand.getString("nsSalesOrderStock")+")"+"/"+nsPhysicalStock);

                                                    if(!nsInventoryId.equals("") && !nsLedgerId.equals("")) {
                                                        btn_i_details.setVisibility(View.VISIBLE);
                                                    }else{
                                                        btn_i_details.setVisibility(View.GONE);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            }
                                        }
                                        txtAutoCompleteInventoryQuantity.requestFocus();
                                    }
                                });

                                txtAutoCompleteInventoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(!b) {

                                            String str = txtAutoCompleteInventoryName.getText().toString();

                                            ListAdapter listAdapter = txtAutoCompleteInventoryName.getAdapter();
                                            for(int i = 0; i < listAdapter.getCount(); i++) {
                                                String temp = listAdapter.getItem(i).toString();
                                                if(txtAutoCompleteInventoryName.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                    return;
                                                }
                                            }

                                            txtAutoCompleteInventoryName.setError("Select correct Brand Name from Dropdown");

                                        }
                                    }
                                });

                                imgPayment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                            LayoutInflater inflater = getLayoutInflater();
                                            View alertLayout = inflater.inflate(R.layout.dialog_for_range_print_gb, null);
                                            final AppCompatAutoCompleteTextView txtAutoCompleteQuantity = alertLayout.findViewById(R.id.txtAutoCompleteInventoryQuantity);
                                            final CheckBox chkBill = alertLayout.findViewById(R.id.chkBill);
                                            final CheckBox chkGP = alertLayout.findViewById(R.id.chkGP);
                                            final CheckBox chkDelivery = alertLayout.findViewById(R.id.chkDelivery);
                                            /*TextView lblPartyTotalDialog = alertLayout.findViewById(R.id.lblPartyTotalDialog);
                                            TextView lblExpenseTotalDialog = alertLayout.findViewById(R.id.lblExpenseTotalDialog);*/

                                            Toolbar toolbarFilter = alertLayout.findViewById(R.id.myFilterToolbar);
                                            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SalesTransactionActivity.this);
                                            toolbarFilter.setTitle("Churmuri 116LTR");
                                            alert.setCancelable(false);
                                            alert.setView(alertLayout);

                                            alert.setPositiveButton("Save", new
                                                    DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                            try {

                                                                boolean isValidated = true;

                                                                if(txtAutoCompleteQuantity.getText().toString().trim().matches("")) {
                                                                    txtAutoCompleteQuantity.setError("Required");
                                                                    Toast.makeText(SalesTransactionActivity.this, "Incorrect Entry", Toast.LENGTH_SHORT).show();
                                                                    isValidated=false;
                                                                }else if(Integer.parseInt(txtAutoCompleteQuantity.getText().toString().trim())>10) {
                                                                    txtAutoCompleteQuantity.setError("Incorrect entry");
                                                                    Toast.makeText(SalesTransactionActivity.this, "Incorrect Entry", Toast.LENGTH_SHORT).show();
                                                                    isValidated=false;
                                                                }
                                                                if(isValidated==false){
                                                                    return;
                                                                }

                                                                int printBill=2;
                                                                int printGp=2;
                                                                int delivered=1;

                                                                if(chkBill.isChecked()){
                                                                    printBill=1;
                                                                }

                                                                if(chkGP.isChecked()){
                                                                    printGp=1;
                                                                }

                                                                if(chkDelivery.isChecked()){
                                                                        delivered=3;
                                                                }

                                                                JSONObject objItems = new JSONObject();
                                                                final JSONArray jsonArrObjItems = new JSONArray();
                                                                Date d = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString());

                                                                objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(d));
                                                                objItems.put("prLocationId", preferenceConfig.readLocationId());
                                                                objItems.put("prLedgerId", "3230");
                                                                objItems.put("prLedgerAlias", txtLedgerNameAlias.getText().toString().trim());
                                                                objItems.put("prAdditionalChargeType", "CART");
                                                                objItems.put("prAdditionalCharges", "");
                                                                objItems.put("prTotalAmount", chPrice);
                                                                objItems.put("prPaidAmount", chPrice);
                                                                objItems.put("prDisplayStatusId", "1");
                                                                objItems.put("prTransactionTypeId", "200");//Sales
                                                                objItems.put("prTransactionStatus", delivered);//1.Undelivered,2.PartiallyDelivered 3.Delivered
                                                                objItems.put("prStatus", "1");//2.Deleted, 1.Active
                                                                objItems.put("prPaymentTypeId", "5");//1:Cash 2:PhonePe; 3.Bank, 4.Cheque
                                                                objItems.put("prRemarks", "Created Using ShortCut");
                                                                objItems.put("prVerifiedStatus","2");//1.Verified 2.Not Verified
                                                                objItems.put("prConvertToSales", "2");
                                                                objItems.put("PrTransportOrder","2");

                                                                objItems.put("nsInventoryQuantity", "1");
                                                                objItems.put("nsInventoryId", "14");
                                                                objItems.put("nsInventoryPrice", chPrice);

                                                                objItems.put("nsPriceVariation", "0");
                                                                objItems.put("nsHamliPrice", "0");

                                                                jsonArrObjItems.put(objItems);

                                                                //DialogUtility.showDialog(SalesTransactionActivity.this, jsonArrObjItems.toString(4),"");

                                                                insertListData(
                                                                        jsonArrObjItems.toString(4),
                                                                        nsModule,
                                                                        Double.parseDouble(txtAutoCompleteQuantity.getText().toString().trim()),
                                                                        printGp,printBill,2);

                                                                displayDataForAutoEditText();

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });

                                            alert.setNegativeButton("Cancel", new
                                                    DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface arg0, int arg1) {

                                                        }
                                                    });

                                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



                                            final android.app.AlertDialog dialog = alert.create();
                                            dialog.show();


                                    }
                                });

                                progress.dismiss();
                            }
                        }  catch (Exception e) {
                            Toast.makeText(SalesTransactionActivity.this, "Main TypeAhead "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText("auto ed 2"+error.getMessage());
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
                params.put("functionality", "fetch_typeahead_textboxes_data");
                //params.put("format", "list");
                params.put("prLocationId",nsLocationId);
                Date d = ConvertFunctions.convertIndianDateStringToDate(txtDate.getText().toString().trim());
                params.put("prDateFrom",new SimpleDateFormat("yyyy-MM-dd").format(d));
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

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void calculateTotal() {
        double addAmt = 0.0,paidAmt=0.0;
        double totalQty = 0.0;
        double btotal=0.0,btotal1=0;

        try{
            for (int i = 0; i < jsonCartItems.length(); i++) {
                totalQty += jsonCartItems.getJSONObject(i).getDouble("nsInventoryQuantity");
                btotal1 += (jsonCartItems.getJSONObject(i).getDouble("nsInventoryQuantity") * jsonCartItems.getJSONObject(i).getDouble("nsInventoryPrice")* jsonCartItems.getJSONObject(i).getDouble("nsUnitSize"));
                BigDecimal bd = new BigDecimal(btotal1).setScale(2, RoundingMode.HALF_UP);
                btotal = Math.round(bd.doubleValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lblTotalQty.setText(String.valueOf(totalQty));
        lblGrandTotal.setText(String.valueOf(btotal));

        if(txtExtraCharge.getText().toString().trim().matches("")){
            addAmt=0.0;
        }else{
            addAmt = Double.parseDouble(txtExtraCharge.getText().toString().trim());
        }
        txtTotalAmount.setText(String.valueOf((btotal+addAmt)));

        /*if(isCC.equals("1")){
            txtPaidAmount.setText("0");
        }else{
            txtPaidAmount.setText(txtTotalAmount.getText().toString().trim());
        }*/

        if(txtPaidAmount.getText().toString().trim().matches("")){
            paidAmt = 0.0;
        }else{
            paidAmt = Double.parseDouble(txtPaidAmount.getText().toString().trim());
        }
        //txtPaidAmount.setText(txtTotalAmount.getText().toString());
        txtBalanceAmount.setText(String.valueOf(((btotal+addAmt)-paidAmt)));
    }

    @Override
    public void OnItemSelected(JSONObject item, int position) {

        try {
            btn_add_cart_details.setText("Update");
            btn_del_party_details.setVisibility(View.VISIBLE);
            txtAutoCompleteInventoryName.setText(item.getString("nsInventoryName"));
            txtAutoCompleteInventoryQuantity.setText(item.getString("nsInventoryQuantity"));
            txtAutoCompleteInventorySellingPrice.setText(item.getString("nsInventoryPrice"));
            n=position;
            nsSellingPrice = item.getString("nsInventoryPrice");


            nsSellingPrice1 = item.getString("nsBaseSellingPrice");
            nsUnitSize=item.getString("nsUnitSize");
            nsInventoryId=item.getString("nsInventoryId");
            nsBaseSellingPrice=item.getString("nsBaseSellingPrice");
            txtAutoCompleteInventorySellingPrice.requestFocus();
            btn_i_details.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }

    private void displayData() {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(SalesTransactionActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                DialogUtility.showDialog(SalesTransactionActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                JSONArray jsonDataItems = jsonObject.getJSONArray("dataItems");

                                final JSONObject objData = jsonData.getJSONObject(0);
                                //DialogUtility.showDialog(Sales.this,"GB Display data bal"+jsonDataItems.toString(4),"");

                                if(jsonData.length()>0) {

                                    //Toast.makeText(SalesTransactionActivity.this, ""+objData.getString("nsManulTime"), Toast.LENGTH_SHORT).show();

                                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = sdf.parse(objData.getString("nsManulTime"));
                                    Calendar cal123 = Calendar.getInstance();
                                    cal123.setTime(date);
                                    DateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                                    mTimeAfterInsert=sdf1.format(cal123.getTime());

                                    lblVoucherNo.setText("E.No. "+objData.getString("nsVoucherNo"));

                                    String curDate = objData.getString("nsDate");
                                    SimpleDateFormat spf = new SimpleDateFormat("yyyy-mm-dd");
                                    Date newDate = spf.parse(curDate);
                                    spf = new SimpleDateFormat("dd-mm-yyyy");
                                    curDate = spf.format(newDate);
                                    txtDate.setText(curDate);

                                    txtLocation.setText(objData.getString("nsLocationName"));
                                    nsLocationId = objData.getString("nsLocationId");
                                    if(!objData.isNull("nsReferenceId")
                                            && !objData.getString("nsReferenceId").equals("0")
                                            && !objData.getString("nsReferenceId").equals("")) {
                                        nsReferenceId = objData.getString("nsReferenceId");
                                    }

                                    if(objData.isNull("nsArea")){
                                        txtLedgerName.setText(objData.getString("nsLedgerName")+" "+objData.getString("nsCity"));
                                    }else {
                                        txtLedgerName.setText(objData.getString("nsLedgerName") + " " + objData.getString("nsArea") + " " + objData.getString("nsCity"));
                                    }
                                    nsLedgerId = objData.getString("nsLedgerId");
                                    imgInfo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            displaySalesTrancationInfo(nsLedgerId);
                                        }
                                    });

                                    final Date filterDateFrom,filterDateTo;
                                    Calendar cal=Calendar.getInstance();
                                    filterDateTo = cal.getTime();
                                    cal.add(Calendar.DATE,-90);
                                    filterDateFrom=cal.getTime();

                                    imgOldTransactions.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            //Toast.makeText(SalesTransactionActivity.this, nsLedgerId, Toast.LENGTH_SHORT).show();
                                            displaySalesTrancation3MonthsOldInfo(nsLedgerId,new SimpleDateFormat("yyyy-MM-dd").format(filterDateFrom),
                                                    new SimpleDateFormat("yyyy-MM-dd").format(filterDateTo));
                                        }
                                    });


                                    imgLedgerTransactions.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(SalesTransactionActivity.this, ListActivity.class);
                                            i.putExtra("nsModule","report_ledger_transaction");
                                            i.putExtra("nsSubModule","master_reports");

                                            i.putExtra("nsId",nsLedgerId);
                                            try {
                                                i.putExtra("nsName",objData.getString("nsLedgerName"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            i.putExtra("nsCity",city);
                                            startActivity(i);
                                        }
                                    });

                                    if(objData.getInt("nsIsGSTCustomer")==1){
                                        imgGST.setVisibility(View.VISIBLE);
                                        imgGST.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    DialogUtility.showDialog(SalesTransactionActivity.this, objData.getString("nsGSTCustomerName"),"GST Name");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }

                                    isCC = objData.getString("nsIsCC");
                                    if(objData.getInt("nsIsCC")==1){
                                        txtLedgerName.setTextColor(Color.parseColor("#C62828"));
                                    }else{
                                        txtLedgerName.setTextColor(Color.parseColor("#000000"));
                                    }
                                    txtLedgerNameAlias.setText(objData.getString("nsLedgerAlias"));
                                    if((objData.getDouble("nsInOut")-objData.getDouble("nsOpeningBalance")-objData.getDouble("nsReceiptPayment"))>0){
                                        lblLedgerBalance.setTextColor(Color.parseColor("#C62828"));
                                    }else{
                                        lblLedgerBalance.setTextColor(Color.parseColor("#1B5E20"));
                                    }
                                    lblLedgerBalance.setText(""+(objData.getDouble("nsInOut")+objData.getDouble("nsOpeningBalance")+objData.getDouble("nsReceiptPayment")));
                                    if(preferenceConfig.readUserPosition().equals("2")) {
                                        if (objData.getInt("nsIsRestricted") == 1) {
                                            lblLedgerBalance.setVisibility(View.INVISIBLE);
                                            restrictedGlobal=1;
                                        } else {
                                            lblLedgerBalance.setVisibility(View.VISIBLE);
                                            restrictedGlobal=2;
                                        }
                                    }
                                    txtExtraChargeType.setText(objData.getString("nsAdditionalChargesType"));
                                    txtExtraCharge.setText(objData.getString("nsAdditionalCharges"));
                                    txtPaidAmount.setText(objData.getString("nsPaidAmount"));
                                    txtRemarks.setText(objData.getString("nsRemarks"));

                                    if(!objData.isNull("nsReminderDate")){
                                        Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsReminderDate"));
                                        txtReminderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                                    }
                                    if(!objData.isNull("nsPaymentTypeId")) {
                                        if(objData.getInt("nsPaymentTypeId")<5) {
                                            spinnerPaymentMode.setSelection((objData.getInt("nsPaymentTypeId") - 1));
                                        }else{
                                            spinnerPaymentMode.setSelection(0);
                                        }
                                    }

                                    if(objData.getInt("nsTransportOrder")==1){
                                        checkboxTransport.setChecked(true);
                                    }else{
                                        checkboxTransport.setChecked(false);
                                    }

                                    progress.dismiss();
                                }

                                tableCartItems.setVisibility(View.VISIBLE);

                                for (int i=0; i<jsonDataItems.length();i++){
                                    JSONObject objDataItem = jsonDataItems.getJSONObject(i);
                                    JSONObject objProductItems = new JSONObject();
                                    objProductItems.put("nsInventoryName", objDataItem.getString("nsNameDuringTransaction"));
                                    objProductItems.put("nsInventoryQuantity", objDataItem.getString("nsQuantity"));
                                    objProductItems.put("nsInventoryPrice", objDataItem.getString("nsPrice"));
                                    objProductItems.put("nsUnitSize", objDataItem.getString("nsUnitSize"));
                                    objProductItems.put("nsInventoryId", objDataItem.getString("nsInventoryId"));
                                    objProductItems.put("nsBaseSellingPrice", objDataItem.getString("nsBaseSellingPrice"));
                                    objProductItems.put("nsPriceVariation", objDataItem.getString("nsPriceVariation"));

                                    jsonCartItems.put(objProductItems);
                                }

                                for(int i=0;i<jsonCartItems.length();i++){
                                    try {
                                        //JSONObject objData = jsonPartyCartItems.getJSONObject(i);
                                        JSONObject itemArr = (JSONObject)jsonCartItems.get(i);
                                        itemArr.put("nsSelectedPosition", i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                calculateTotal();
                                myDataAdapter = new MyDataAdapter(context, myDataListColumns, jsonCartItems, (MyDataAdapter.myAdapterListener) context, "cart_items","");
                                recyclerView.setAdapter(myDataAdapter);
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            //lblMessage.setText(e.getMessage());
                            Toast.makeText(SalesTransactionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //lblMessage.setText(error.getMessage());
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
                params.put("functionality", "fetch_table_data_for_invoice");
                params.put("id",nsId);
                Log.d("RRakeshSSudhir",params.toString());
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


//Todo: icons Redirection
//Todo: Print Functionality
