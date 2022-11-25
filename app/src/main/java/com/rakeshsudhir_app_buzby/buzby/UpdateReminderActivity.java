package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateReminderActivity extends BaseActivity {

    AppCompatAutoCompleteTextView txtDate;
    EditText txtRemarks;
    Button btnSave;
    CheckBox checkboxPaymentClear;

    DatePickerDialog picker;

    private ProgressDialog progress;
    String nsModule="",nsId="",nsDate="";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        txtDate = (AppCompatAutoCompleteTextView) findViewById(R.id.txtDate);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);
        checkboxPaymentClear = (CheckBox) findViewById(R.id.checkboxPaymentClear);
        btnSave = (Button) findViewById(R.id.btnSave);

        Intent intent = getIntent();

        if (intent.hasExtra("nsModule")) {
            nsModule = getIntent().getStringExtra("nsModule");
        }

        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }

        if (intent.hasExtra("nsDate")) {
            nsDate = getIntent().getStringExtra("nsDate");
        }
        //Toast.makeText(this, ""+nsDate, Toast.LENGTH_SHORT).show();

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFunctions.currentDateCalender(picker,UpdateReminderActivity.this,txtDate);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidated = true;

                if(txtDate.getText().toString().trim().equals("") && checkboxPaymentClear.isChecked()==false){
                    DialogUtility.showDialog(UpdateReminderActivity.this,"Cannot save Empty form","Error");
                    isValidated=false;
                }
                if(isValidated==false){
                    return;
                }

                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    String dat="";
                    if(txtDate.getText().toString().equals("")){
                        dat = nsDate;
                    }else{
                        dat = txtDate.getText().toString();
                    }
                    Date d = ConvertFunctions.convertIndianDateStringToDate(dat);
                    objItems.put("prDate", new SimpleDateFormat("yyyy-MM-dd").format(d));
                    objItems.put("prRemarks", txtRemarks.getText().toString());

                    if(checkboxPaymentClear.isChecked()==true){
                        objItems.put("prClearPayment", "2");
                    }

                    objItems.put("prSalesPurchaseId",nsId);

                    jsonArrObjItems.put(objItems);

                    insertData(nsId,
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UpdateReminderActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });

    }
}