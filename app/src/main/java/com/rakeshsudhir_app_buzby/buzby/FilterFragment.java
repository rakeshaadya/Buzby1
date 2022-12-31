package com.rakeshsudhir_app_buzby.buzby;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FilterFragment extends Fragment {

    OnFilterSelectedListener callback;
    String nsArea="";
    String nsAreaId="";
    String nsCity="";
    String nsCityId="";
    String nsUserId="";
    String nsUser="";
    String nsLocation="";
    String nsLocationId="";
    String nsCategoryId="";
    String nsSubCategoryId="";
    String nsBrandId="";
    String nsExpenseHead="";
    String nsExpenseHeadId="";
    String nsLedgerNameExp="";
    String nsLedgerNameExpId="";

    public void setOnFilterSelectedListener(OnFilterSelectedListener callback) {
        this.callback = callback;
    }

    public interface OnFilterSelectedListener {
        public void onCommonFilterButtonClicked(String buttonFunctionality);
    }

    public View v;

    Intent intent;
    SharedPreferenceConfig preferenceConfig;

    public FilterFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intent = getActivity().getIntent();
        if(
                intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")
                || intent.getStringExtra("nsModule").equals("upi_payment_receipt")
                || intent.getStringExtra("nsModule").equals("report_ledger_transaction")
        ){

            v = inflater.inflate(R.layout.fragment_filter, container, false);
            preferenceConfig = new SharedPreferenceConfig(v.getContext());
            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatAutoCompleteTextView txtCategory,txtSubCategory,txtBrand,txtLocation;
            final CheckBox checkboxUPI, checkBank,checkboxCheque;
            final ImageView imgClear,imgClearCategory,imgClearSubCategory,imgClearBrand;
            final LinearLayout checkBoxLayout,linearLayoutCatSubCatBrand;
            final Button btnGO;

            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);

            TextInputLayout layoutTransactionType;
            AppCompatSpinner spinnerTransactionType;

            layoutTransactionType = (TextInputLayout) v.findViewById(R.id.layoutTransactionType);
            spinnerTransactionType = (AppCompatSpinner) v.findViewById(R.id.spinnerTransactionType);

            txtCategory = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCategory);
            txtSubCategory = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtSubCategory);
            txtBrand = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtBrand);
            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);

            checkBoxLayout = (LinearLayout) v.findViewById(R.id.checkBoxLayout);
            linearLayoutCatSubCatBrand = (LinearLayout) v.findViewById(R.id.linearLayoutCatSubCatBrand);
            checkboxUPI = (CheckBox) v.findViewById(R.id.checkboxUPI);
            checkBank = (CheckBox) v.findViewById(R.id.checkBank);
            checkboxCheque = (CheckBox) v.findViewById(R.id.checkboxCheque);
            btnGO = (Button) v.findViewById(R.id.btnGO);

            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);
            imgClearCategory = (ImageView) v.findViewById(R.id.imgClearCategory);
            imgClearSubCategory = (ImageView) v.findViewById(R.id.imgClearSubCategory);
            imgClearBrand = (ImageView) v.findViewById(R.id.imgClearBrand);
            imgClear = (ImageView) v.findViewById(R.id.imgClear);

            if(
                    intent.getStringExtra("nsModule").equals("upi_payment_receipt")
                    || intent.getStringExtra("nsModule").equals("report_ledger_transaction")
            ){
                checkBoxLayout.setVisibility(View.GONE);
            }

            if(intent.getStringExtra("nsModule").equals("report_ledger_transaction")
            ){
                linearLayoutCatSubCatBrand.setVisibility(View.VISIBLE);
                layoutTransactionType.setVisibility(View.VISIBLE);
            }

            //initialize end

            /*DialogUtility.showDialog(getActivity(),intent.getStringExtra("toDateRecordTrip")+"\n"
                            +intent.getStringExtra("fromDateRecordTrip")+"\n"
                            +intent.getStringExtra("completedTrip")+"\n"
                            +intent.getStringExtra("pendingTrip")+"\n"
                            +intent.getStringExtra("vehicleId")+"\n"
                            +intent.getStringExtra("vehicleNo")+"\n"
                    ,"");*/




            txtFromDate.setText(intent.getStringExtra("fromDate1"));
            txtToDate.setText(intent.getStringExtra("toDate1"));

            if(!intent.getStringExtra("nsLocationId").equals("")){
                txtLocation.setText(intent.getStringExtra("nsLocation"));
                nsLocationId = intent.getStringExtra("nsLocationId");
            }



            if (intent.getStringExtra("bank").equals("1")) {
                checkBank.setChecked(true);
            }else if (intent.getStringExtra("bank").equals("2")) {
                checkBank.setChecked(false);
            }

            if (intent.getStringExtra("upi").equals("1")) {
                checkboxUPI.setChecked(true);
            }else if (intent.getStringExtra("upi").equals("2")) {
                checkboxUPI.setChecked(false);
            }

            if (intent.getStringExtra("cheque").equals("1")) {
                checkboxCheque.setChecked(true);
            }else if (intent.getStringExtra("cheque").equals("2")) {
                checkboxCheque.setChecked(false);
            }

            //Toast.makeText(preferenceConfig, "", Toast.LENGTH_SHORT).show();

            /*if(!intent.getStringExtra("vehicleId").equals("0")) {
                vehicleNoId = intent.getStringExtra("vehicleId");
                txtVehicleNo.setText(intent.getStringExtra("vehicleNo"));
            }*/

            dateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onDateIconClicked(dateIcon, txtFromDate, txtToDate, true);

                }
            });

            txtFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedFromDate();
                    assignCalendar(txtFromDate);
                }
            });
            txtToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedToDate();
                    assignCalendar(txtToDate);
                }
            });


            if(intent.getStringExtra("nsModule").equals("upi_payment_receipt")) {
                if (Integer.parseInt(preferenceConfig.readUserPosition())>1) {
                    dateIcon.setVisibility(View.GONE);
                    txtFromDate.setEnabled(false);
                    txtFromDate.setAlpha(0.5f);
                    txtToDate.setEnabled(false);
                    txtToDate.setAlpha(0.8f);
                }
            }

            txtCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_inventory_category");
                    startActivityForResult(i,4);
                }
            });

            txtSubCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_inventory_subcategory");
                    startActivityForResult(i,5);
                }
            });

            txtBrand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_brand");
                    startActivityForResult(i,6);
                }
            });

            btnGO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchDataInformation("master_reports",
                            "fetch_report_customer_previous_bill",
                            nsCategoryId,nsSubCategoryId,nsBrandId,intent.getStringExtra("ledgerid")
                            );
                }
            });

            if(intent.getStringExtra("nsModule").equals("report_ledger_transaction")
            ) {

                String[] listType = {"All",
                        "Sales",
                        "Receipt",
                        "Purchase",
                        "Payment",
                        "Purchase Return",
                        "Sales Return",
                };
                String[] transactionType = new String[listType.length];
                for (int i = 0; i < listType.length; i++) {
                    transactionType[i] = listType[i];
                }
                ArrayAdapter<String> dataAdapterType = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, transactionType) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                dataAdapterType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerTransactionType.setAdapter(dataAdapterType);

                spinnerTransactionType.setSelection(Integer.parseInt(intent.getStringExtra("nsTransactionType")));
            }

            /*txtVehicleNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_vehicle");
                    i.putExtra("nsSubModule","returnVehicleNoforDailyStatus");
                    startActivityForResult(i,1);
                }
            });*/

            imgClearCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtCategory.setText("");
                    nsCategoryId="";
                }
            });

            imgClearSubCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtSubCategory.setText("");
                    nsSubCategoryId="";
                }
            });

            imgClearBrand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtBrand.setText("");
                    nsBrandId="";
                }
            });

            imgClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtLocation.setText("");
                    nsLocationId="";
                }
            });

            txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_location");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,3);
                }
            });

        }else if(
                intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
                || intent.getStringExtra("nsModule").equals("transaction_sales_purchase_reminders")
                || intent.getStringExtra("nsModule").equals("opening_cash_balance")
                || intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                || intent.getStringExtra("nsModule").equals("transaction_price_variation")
                || intent.getStringExtra("nsModule").equals("master_day_reports")
                || intent.getStringExtra("nsModule").equals("transactions_stock_transfer")
                || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
        ){

            v = inflater.inflate(R.layout.fragment_filter_date, container, false);
            preferenceConfig = new SharedPreferenceConfig(v.getContext());
            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatAutoCompleteTextView txtLocation,txtUser;
            final ImageView imgLocation,imgClearUser;

            final RelativeLayout layoutRelUser;
            final LinearLayout spinnerLayout;
            final AppCompatSpinner spinnerPaymentType,spinnerReceiptType;

            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);

            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);
            txtUser = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtUser);
            layoutRelUser = (RelativeLayout) v.findViewById(R.id.layoutRelUser);
            spinnerLayout = (LinearLayout) v.findViewById(R.id.spinnerLayout);
            spinnerPaymentType = (AppCompatSpinner) v.findViewById(R.id.spinnerPaymentType);
            spinnerReceiptType = (AppCompatSpinner) v.findViewById(R.id.spinnerReceiptType);

            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);

            txtFromDate.setText(intent.getStringExtra("fromDate1"));
            txtToDate.setText(intent.getStringExtra("toDate1"));
            imgLocation = (ImageView) v.findViewById(R.id.imgClearLocation);
            imgClearUser = (ImageView) v.findViewById(R.id.imgClearUser);


            if(intent.getStringExtra("nsModule").equals("master_day_reports")){
                layoutRelUser.setVisibility(View.VISIBLE);
                txtUser.setText(intent.getStringExtra("nsUser"));
                nsUser = intent.getStringExtra("nsUser");
                nsUserId = intent.getStringExtra("nsUserId");
            }

            if(
                    intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
                    || intent.getStringExtra("nsModule").equals("opening_cash_balance")
                    || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                    || intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                    || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                    || intent.getStringExtra("nsModule").equals("master_day_reports")
                    || intent.getStringExtra("nsModule").equals("transactions_stock_transfer")
                    || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
            ) {
                if (Integer.parseInt(preferenceConfig.readUserPosition())>1) {
                    dateIcon.setVisibility(View.GONE);
                    txtFromDate.setEnabled(false);
                    txtFromDate.setAlpha(0.5f);
                    txtToDate.setEnabled(false);
                    txtToDate.setAlpha(0.8f);
                }
            }

            if(
                    intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                    || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                    || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                    || intent.getStringExtra("nsModule").equals("master_day_reports")
                    || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
                    || intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
            ){
                txtLocation.setText(intent.getStringExtra("nsLocation"));
                nsLocation = intent.getStringExtra("nsLocation");
                nsLocationId = intent.getStringExtra("nsLocationId");


            }else{
                RelativeLayout layoutRelLocation = (RelativeLayout) v.findViewById(R.id.layoutRelLocation);
                layoutRelLocation.setVisibility(View.GONE);
            }

            if(
                    intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                   || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
            ) {
                imgLocation.setVisibility(View.GONE);
            }
            if(intent.getStringExtra("nsModule").equals("consolidated_inventory_report")){
                txtToDate.setVisibility(View.GONE);
                imgLocation.setVisibility(View.GONE);
                dateIcon.setVisibility(View.GONE);
            }

            if(intent.getStringExtra("nsModule").equals("transaction_receipt_payment")){
                spinnerLayout.setVisibility(View.VISIBLE);

                String[] listType = {"All",
                        "Receipt",
                        "Payment"
                };
                String[] categoriesType= new String[listType.length];
                for(int i=0;i<listType.length;i++){
                    categoriesType[i] = listType[i];
                }
                ArrayAdapter<String> dataAdapterType = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, categoriesType){
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                dataAdapterType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerReceiptType.setAdapter(dataAdapterType);

                String[] listPaymentType = {"All",
                        "Cash",
                        "Phone Pe",
                        "Bank",
                        "Cheque"

                };

                String[] categoriesPaymentType = new String[listPaymentType.length];
                for(int i=0;i<listPaymentType.length;i++) {
                    categoriesPaymentType[i] = listPaymentType[i];
                }
                ArrayAdapter<String> dataAdapterPaymentType = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, categoriesPaymentType){
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                dataAdapterPaymentType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerPaymentType.setAdapter(dataAdapterPaymentType);

                spinnerReceiptType.setSelection(Integer.parseInt(intent.getStringExtra("nsReceiptTypeId")));
                spinnerPaymentType.setSelection(Integer.parseInt(intent.getStringExtra("nsPaymentTypeId")));
            }



            txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_location");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,3);
                }
            });

            txtUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_users");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,9);
                }
            });

            dateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDateIconClicked(dateIcon,txtFromDate,txtToDate,true);
                }
            });

            txtFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedFromDate();
                    assignCalendar(txtFromDate);
                }
            });
            txtToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedToDate();
                    assignCalendar(txtToDate);
                }
            });

            imgLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtLocation.setText("");
                    nsLocationId="";
                }
            });

            imgClearUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtUser.setText("");
                    nsUserId="";
                    nsUser="";
                }
            });

        }else if(
                intent.getStringExtra("nsModule").equals("master_expense")
        ){

            v = inflater.inflate(R.layout.fragment_filter_expense, container, false);
            preferenceConfig = new SharedPreferenceConfig(v.getContext());
            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatAutoCompleteTextView txtLocation,txtName,txtExpenseHead;
            ImageView imgClearName,imgClearExpense,imgClearLocation;

            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);

            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);
            txtName = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtName);
            txtExpenseHead = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtExpenseHead);

            imgClearExpense = (ImageView) v.findViewById(R.id.imgClearExpense);
            imgClearName = (ImageView) v.findViewById(R.id.imgClearName);
            imgClearLocation = (ImageView) v.findViewById(R.id.imgClearLocation);

            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);



                if (Integer.parseInt(preferenceConfig.readUserPosition())>1) {
                    dateIcon.setVisibility(View.GONE);
                    txtFromDate.setEnabled(false);
                    txtFromDate.setAlpha(0.5f);
                    txtToDate.setEnabled(false);
                    txtToDate.setAlpha(0.8f);
                }


            txtFromDate.setText(intent.getStringExtra("fromDate1"));
            txtToDate.setText(intent.getStringExtra("toDate1"));
            txtLocation.setText(intent.getStringExtra("nsLocation"));
            nsLocation = intent.getStringExtra("nsLocation");
            nsLocationId = intent.getStringExtra("nsLocationId");

            txtExpenseHead.setText(intent.getStringExtra("nsExpenseHead"));
            nsExpenseHead=intent.getStringExtra("nsExpenseHead");
            nsExpenseHeadId=intent.getStringExtra("nsExpenseHeadId");

            txtName.setText(intent.getStringExtra("nsName"));
            nsLedgerNameExp=intent.getStringExtra("nsName");
            nsLedgerNameExpId=intent.getStringExtra("nsNameId");

            /*if(intent.getStringExtra("nsModule").equals("consolidated_inventory_report")){
                txtToDate.setVisibility(View.GONE);

                dateIcon.setVisibility(View.GONE);
            }*/

            txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_location");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,3);
                }
            });

            imgClearLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtLocation.setText("");
                    nsLocationId="";
                    nsLocation="";
                }
            });

            imgClearExpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtExpenseHead.setText("");
                    nsExpenseHeadId="";
                    nsExpenseHead="";
                }
            });

            imgClearName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtName.setText("");
                    nsLedgerNameExp="";
                    nsLedgerNameExpId="";
                }
            });

            txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_location");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,3);
                }
            });

            txtExpenseHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_expense_head");
                    i.putExtra("nsSubModule", "returnValue");
                    startActivityForResult(i,7);
                }
            });

            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_ledger");
                    i.putExtra("nsSubModule", "returnValue");
                    i.putExtra("nsLedgerCategoryE", "4");
                    i.putExtra("nsLedgerCategoryH", "5");
                    startActivityForResult(i,8);
                }
            });

            dateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDateIconClicked(dateIcon,txtFromDate,txtToDate,true);
                }
            });

            txtFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedFromDate();
                    assignCalendar(txtFromDate);
                }
            });
            txtToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedToDate();
                    assignCalendar(txtToDate);
                }
            });

        }else if(
                intent.getStringExtra("nsModule").equals("transactions_in_out")
        ){

            v = inflater.inflate(R.layout.fragment_filter_in_out, container, false);
            preferenceConfig = new SharedPreferenceConfig(v.getContext());
            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);
            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);


            LinearLayout checkBoxLayout,spinnerLayout;
            checkBoxLayout = (LinearLayout) v.findViewById(R.id.checkBoxLayout);
            spinnerLayout = (LinearLayout) v.findViewById(R.id.spinnerLayout);

            RelativeLayout layoutRelCity,layoutRelArea,layoutRelUser;
            layoutRelArea = (RelativeLayout) v.findViewById(R.id.layoutRelArea);
            layoutRelCity = (RelativeLayout) v.findViewById(R.id.layoutRelCity);

            CheckBox checkboxDelivered,checkUndelivered,checkboxCredit,checkboxPartial,checkboxDeleted,checkboxIsVerified,checkboxIsTransportOrder;
            checkboxDelivered = (CheckBox) v.findViewById(R.id.checkboxDelivered);
            checkUndelivered = (CheckBox) v.findViewById(R.id.checkUndelivered);
            checkboxPartial = (CheckBox) v.findViewById(R.id.checkboxPartial);
            checkboxDeleted = (CheckBox) v.findViewById(R.id.checkboxDeleted);
            checkboxIsVerified = (CheckBox) v.findViewById(R.id.checkboxIsVerified);
            checkboxIsTransportOrder = (CheckBox) v.findViewById(R.id.checkboxIsTransportOrder);
            checkboxCredit = (CheckBox) v.findViewById(R.id.checkboxCredit);

            AppCompatSpinner spinnerCashCreditAll,spinnerPaymentType;
            spinnerCashCreditAll = (AppCompatSpinner) v.findViewById(R.id.spinnerCashCreditAll);
            spinnerPaymentType = (AppCompatSpinner) v.findViewById(R.id.spinnerPaymentType);

            final AppCompatAutoCompleteTextView txtArea,txtCity,txtName,txtUser;
            final AppCompatAutoCompleteTextView txtLocation;
            txtArea = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtArea);
            txtCity = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCity);
            txtUser = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtUser);
            txtName = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtName);

            ImageView imgClearCity,imgClearArea,imgClearLocation,imgClearCustomerName,imgClearUser;

            imgClearArea = (ImageView) v.findViewById(R.id.imgClearArea);
            imgClearCity = (ImageView) v.findViewById(R.id.imgClearCity);
            imgClearCustomerName = (ImageView) v.findViewById(R.id.imgClearCustomerName);
            imgClearLocation = (ImageView) v.findViewById(R.id.imgClearLocation);
            imgClearUser = (ImageView) v.findViewById(R.id.imgClearUser);
            TextInputLayout layoutPaymentType = (TextInputLayout) v.findViewById(R.id.layoutPaymentType);

            layoutRelUser = (RelativeLayout) v.findViewById(R.id.layoutRelUser);

            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);
            //Initialization Ends Here

            txtFromDate.setText(intent.getStringExtra("fromDate1"));
            txtToDate.setText(intent.getStringExtra("toDate1"));

            if(intent.getStringExtra("nsSubModule").equals("transaction_sales_order")){
                checkBoxLayout.setVisibility(View.GONE);
                layoutPaymentType.setVisibility(View.GONE);
                checkboxCredit.setVisibility(View.VISIBLE);
            }else if(intent.getStringExtra("nsSubModule").equals("transaction_undelivered")){
                checkboxDelivered.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                layoutPaymentType.setVisibility(View.GONE);
                checkboxDeleted.setVisibility(View.GONE);
            }else if(intent.getStringExtra("nsSubModule").equals("transaction_delivered")){
                checkBoxLayout.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                layoutRelArea.setVisibility(View.GONE);
                layoutRelCity.setVisibility(View.GONE);
                checkboxDeleted.setVisibility(View.GONE);
            }else if(intent.getStringExtra("nsSubModule").equals("transaction_purchase")){
                checkBoxLayout.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                layoutRelArea.setVisibility(View.GONE);
                layoutRelCity.setVisibility(View.GONE);
                checkboxDeleted.setVisibility(View.GONE);
                checkboxIsVerified.setVisibility(View.VISIBLE);
                checkboxIsTransportOrder.setVisibility(View.GONE);


                    if (Integer.parseInt(preferenceConfig.readUserPosition())>1) {
                        dateIcon.setVisibility(View.GONE);
                        txtFromDate.setEnabled(false);
                        txtFromDate.setAlpha(0.5f);
                        txtToDate.setEnabled(false);
                        txtToDate.setAlpha(0.8f);
                    }

            }

            if(intent.getStringExtra("nsSubModule").equals("transaction_sales")) {
                checkboxCredit.setVisibility(View.VISIBLE);
                if (Integer.parseInt(preferenceConfig.readUserPosition()) > 1) {
                    dateIcon.setVisibility(View.GONE);
                    txtFromDate.setEnabled(false);
                    txtFromDate.setAlpha(0.5f);
                    txtToDate.setEnabled(false);
                    txtToDate.setAlpha(0.8f);

                }
            }

            dateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDateIconClicked(dateIcon,txtFromDate,txtToDate,true);
                }
            });

            txtFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedFromDate();
                    assignCalendar(txtFromDate);
                }
            });
            txtToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //BookedToDate();
                    assignCalendar(txtToDate);
                }
            });

            String[] listType = {"All",
                    "Cash",
                    "Credit"
            };
            String[] categoriesType= new String[listType.length];
            for(int i=0;i<listType.length;i++){
                categoriesType[i] = listType[i];
            }
            ArrayAdapter<String> dataAdapterType = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, categoriesType){
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    tv.setTextColor(Color.BLACK);
                    return view;
                }
            };
            dataAdapterType.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerCashCreditAll.setAdapter(dataAdapterType);

            String[] listPaymentType = {"All",
                    "Phone Pe",
                    "Cheque",
                    "Bank"
            };

            String[] categoriesPaymentType = new String[listPaymentType.length];
            for(int i=0;i<listPaymentType.length;i++) {
                categoriesPaymentType[i] = listPaymentType[i];
            }
            ArrayAdapter<String> dataAdapterPaymentType = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, categoriesPaymentType){
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    tv.setTextColor(Color.BLACK);
                    return view;
                }
            };
            dataAdapterPaymentType.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerPaymentType.setAdapter(dataAdapterPaymentType);

            //Set Custom Data

            if (intent.getStringExtra("nsDelivered").equals("1")) {
                checkboxDelivered.setChecked(true);
            }else if (intent.getStringExtra("nsDelivered").equals("2")) {
                checkboxDelivered.setChecked(false);
            }
            if (intent.getStringExtra("nsUnDelivered").equals("1")) {
                checkUndelivered.setChecked(true);
            }else if (intent.getStringExtra("nsUnDelivered").equals("2")) {
                checkUndelivered.setChecked(false);
            }
            if (intent.getStringExtra("nsPartial").equals("1")) {
                checkboxPartial.setChecked(true);
            }else if (intent.getStringExtra("nsPartial").equals("2")) {
                checkboxPartial.setChecked(false);
            }

            if (intent.getStringExtra("nsVerified").equals("1")) {
                checkboxIsVerified.setChecked(true);
            }else if (intent.getStringExtra("nsPartial").equals("2")) {
                checkboxIsVerified.setChecked(false);
            }
            if (intent.getStringExtra("nsDeleted").equals("1")) {
                checkboxDeleted.setChecked(true);
            }else if (intent.getStringExtra("nsDeleted").equals("2")) {
                checkboxDeleted.setChecked(false);
            }
            if (intent.getStringExtra("nsIsTransportOrder").equals("1")) {
                checkboxIsTransportOrder.setChecked(true);
            }else if (intent.getStringExtra("nsIsTransportOrder").equals("2")) {
                checkboxIsTransportOrder.setChecked(false);
            }

            if (intent.getStringExtra("nsIsCreditCustomer").equals("1")) {
                checkboxCredit.setChecked(true);
            }else if (intent.getStringExtra("nsIsCreditCustomer").equals("2")) {
                checkboxCredit.setChecked(false);
            }

            spinnerCashCreditAll.setSelection(Integer.parseInt(intent.getStringExtra("nsCCType")));
            spinnerPaymentType.setSelection(Integer.parseInt(intent.getStringExtra("nsPaymentType")));

            if(!intent.getStringExtra("nsLedgerId").equals("")) {
                txtName.setText(intent.getStringExtra("nsLedgerName"));
                nsLedgerNameExp = intent.getStringExtra("nsLedgerName");
                nsLedgerNameExpId = intent.getStringExtra("nsLedgerId");
            }

            layoutRelUser.setVisibility(View.VISIBLE);
            if(!intent.getStringExtra("nsUserId").equals("")) {
                txtUser.setText(intent.getStringExtra("nsUser"));
                nsUser = intent.getStringExtra("nsUser");
                nsUserId = intent.getStringExtra("nsUserId");
            }

            if(!intent.getStringExtra("nsAreaId").equals("")){
                txtArea.setText(intent.getStringExtra("nsArea"));
                nsAreaId = intent.getStringExtra("nsAreaId");
            }

            if(!intent.getStringExtra("nsLocationId").equals("")){
                txtLocation.setText(intent.getStringExtra("nsLocation"));
                nsLocationId = intent.getStringExtra("nsLocationId");
            }

            if(!intent.getStringExtra("nsCityId").equals("")){
                txtCity.setText(intent.getStringExtra("nsCity"));
                nsCityId = intent.getStringExtra("nsCityId");
            }

            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_ledger");
                    i.putExtra("nsSubModule", "returnValue");
                    startActivityForResult(i,8);
                }
            });

            txtUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_users");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,9);
                }
            });

            txtArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_area");
                    startActivityForResult(i,1);
                }
            });

            txtCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_city");
                    startActivityForResult(i,2);
                }
            });

            txtLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_location");
                    i.putExtra("nsSubModule","returnValue");
                    startActivityForResult(i,3);
                }
            });

            imgClearArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtArea.setText("");
                    nsAreaId="";
                    nsArea="";
                }
            });

            imgClearCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtCity.setText("");
                    nsCityId="";
                    nsCity="";
                }
            });

            imgClearUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtUser.setText("");
                    nsUserId="";
                    nsUser="";
                }
            });

            imgClearCustomerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtName.setText("");
                    nsLedgerNameExp="";
                    nsLedgerNameExpId="";
                    nsCity="";
                }
            });

            imgClearLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtLocation.setText("");
                    nsLocationId="";
                    nsLocation="";
                }
            });


            /*DialogUtility.showDialog((Activity) v.getContext(),
                    intent.getStringExtra("toDate")+"\n"
                            +intent.getStringExtra("toDate1")+"\n"
                            +intent.getStringExtra("fromDate")+"\n"
                            +intent.getStringExtra("fromDate1")+"\n"
                            +intent.getStringExtra("nsDelivered")+"\n"
                            +intent.getStringExtra("nsUnDelivered")+"\n"
                            +intent.getStringExtra("nsPartial")+"\n"
                            +intent.getStringExtra("nsCCType")+"\n"
                            +intent.getStringExtra("nsPaymentType")+"\n"
                            +intent.getStringExtra("nsArea")+"\n"
                            +intent.getStringExtra("nsAreaId")+"\n"
                            +intent.getStringExtra("nsCity")+"\n"
                            +intent.getStringExtra("nsCityId")+"\n"
                            +intent.getStringExtra("nsDeleted")+"\n"
                            +intent.getStringExtra("nsSubModule")+"\n"
                            +intent.getStringExtra("nsLocationId")+"\n"
                            +intent.getStringExtra("nsLocation")+"\n"
                            +intent.getStringExtra("sortType")+"\n"
                    ,"");*/
        }else if(
                intent.getStringExtra("nsModule").equals("report_consolidated_ledger_balance")
        ){

            v = inflater.inflate(R.layout.fragment_filter_area_city, container, false);
            preferenceConfig = new SharedPreferenceConfig(v.getContext());
            //initialize start - should be repeated in returnFragmentValues


            final AppCompatAutoCompleteTextView txtArea,txtCity,txtRangeFrom,txtRangeTo;
            txtArea = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtArea);
            txtCity = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCity);
            txtRangeFrom = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtRangeFrom);
            txtRangeTo = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtRangeTo);



            ImageView imgClearCity,imgClearArea,imgClearRangeFrom,imgClearRangeTo;

            imgClearArea = (ImageView) v.findViewById(R.id.imgClearArea);
            imgClearCity = (ImageView) v.findViewById(R.id.imgClearCity);
            imgClearRangeTo = (ImageView) v.findViewById(R.id.imgClearRangeTo);
            imgClearRangeFrom = (ImageView) v.findViewById(R.id.imgClearRangeFrom);

            //initialize Ends


            if(!intent.getStringExtra("nsAreaId").equals("")){
                txtArea.setText(intent.getStringExtra("nsArea"));
                nsAreaId = intent.getStringExtra("nsAreaId");
            }

            if(!intent.getStringExtra("nsCityId").equals("")){
                txtCity.setText(intent.getStringExtra("nsCity"));
                nsCityId = intent.getStringExtra("nsCityId");
            }

            if(!intent.getStringExtra("nsRangefrom").equals("")){
                txtRangeFrom.setText(intent.getStringExtra("nsRangefrom"));
            }

            if(!intent.getStringExtra("nsRangeTo").equals("")){
                txtRangeTo.setText(intent.getStringExtra("nsRangeTo"));
            }

            txtArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_area");
                    startActivityForResult(i,1);
                }
            });

            txtCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),ListActivity.class);
                    i.putExtra("nsModule","master_city");
                    startActivityForResult(i,2);
                }
            });



            imgClearArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtArea.setText("");
                    nsAreaId="";
                    nsArea="";
                }
            });

            imgClearCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtCity.setText("");
                    nsCityId="";
                    nsCity="";
                }
            });


            imgClearRangeFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtRangeFrom.setText("");

                }
            });

            imgClearRangeTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtRangeTo.setText("");

                }
            });


        }
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                AppCompatAutoCompleteTextView txtArea;
                txtArea = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtArea);
                nsArea = data.getStringExtra("nsArea");
                nsAreaId = data.getStringExtra("nsId");

                if(
                        intent.getStringExtra("nsModule").equals("transactions_in_out")
                ) {
                    final TextInputEditText txtToDate, txtFromDate;
                    txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
                    txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);
                    txtFromDate.setText("");
                    txtToDate.setText("");
                }

                txtArea.setText(nsArea);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==2){
            if(resultCode==RESULT_OK) {
                nsCity = data.getStringExtra("nsCity");
                nsCityId = data.getStringExtra("nsId");
                final AppCompatAutoCompleteTextView txtCity;
                txtCity = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCity);
                txtCity.setText(nsCity);

                if(
                        intent.getStringExtra("nsModule").equals("transactions_in_out")
                ) {
                    final TextInputEditText txtToDate, txtFromDate;
                    txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
                    txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);
                    txtFromDate.setText("");
                    txtToDate.setText("");
                }

            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==3){
            if(resultCode==RESULT_OK) {
                nsLocation = data.getStringExtra("nsName");
                nsLocationId = data.getStringExtra("nsId");
                final AppCompatAutoCompleteTextView txtLocation;
                txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);
                txtLocation.setText(nsLocation);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==4){
            if(resultCode==RESULT_OK) {
                //nsLocation = data.getStringExtra("nsName");
                nsCategoryId = data.getStringExtra("nsId");
                final AppCompatAutoCompleteTextView txtCategory;
                txtCategory = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCategory);
                txtCategory.setText(data.getStringExtra("nsName"));
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==5){
            if(resultCode==RESULT_OK) {
                //nsLocation = data.getStringExtra("nsName");
                nsSubCategoryId = data.getStringExtra("nsId");
                final AppCompatAutoCompleteTextView txtSubCategory;
                txtSubCategory = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtSubCategory);
                txtSubCategory.setText(data.getStringExtra("nsSubCategoryName"));
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==6){
            if(resultCode==RESULT_OK) {
                //nsLocation = data.getStringExtra("nsName");
                nsBrandId = data.getStringExtra("nsId");
                final AppCompatAutoCompleteTextView txtBrand;
                txtBrand = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtBrand);
                txtBrand.setText(data.getStringExtra("nsName"));
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==7){
            if(resultCode==RESULT_OK) {
                nsExpenseHeadId = data.getStringExtra("nsId");
                nsExpenseHead = data.getStringExtra("nsName");
                final AppCompatAutoCompleteTextView txtExpenseHead;
                txtExpenseHead = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtExpenseHead);
                txtExpenseHead.setText(data.getStringExtra("nsName"));
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==8){
            if(resultCode==RESULT_OK) {
                nsLedgerNameExpId = data.getStringExtra("nsId");
                nsLedgerNameExp = data.getStringExtra("nsName");
                final AppCompatAutoCompleteTextView txtName;
                txtName = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtName);
                txtName.setText(data.getStringExtra("nsName"));
            }
            if(requestCode==RESULT_CANCELED){

            }
        }

        if (requestCode==9){
            if(resultCode==RESULT_OK) {
                nsUserId = data.getStringExtra("nsId");
                nsUser = data.getStringExtra("nsName");
                final AppCompatAutoCompleteTextView txtName;
                txtName = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtUser);
                txtName.setText(data.getStringExtra("nsName"));
            }
            if(requestCode==RESULT_CANCELED){

            }
        }
    }

    public Bundle getFragmentValues() {
        Bundle bundle = new Bundle();
        if(
                intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")
                || intent.getStringExtra("nsModule").equals("upi_payment_receipt")
                || intent.getStringExtra("nsModule").equals("report_ledger_transaction")
        ){
            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final CheckBox checkboxUPI, checkBank,checkboxCheque;
            final ImageView imgClear;
            final AppCompatAutoCompleteTextView txtLocation;


            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);

            checkboxUPI = (CheckBox) v.findViewById(R.id.checkboxUPI);
            checkBank = (CheckBox) v.findViewById(R.id.checkBank);
            checkboxCheque = (CheckBox) v.findViewById(R.id.checkboxCheque);
            imgClear = (ImageView) v.findViewById(R.id.imgClear);

            AppCompatSpinner spinnerTransactionType;

            spinnerTransactionType = (AppCompatSpinner) v.findViewById(R.id.spinnerTransactionType);
            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);

            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);
            final LinearLayout checkBoxLayout;
            checkBoxLayout = (LinearLayout) v.findViewById(R.id.checkBoxLayout);
            if(intent.getStringExtra("nsModule").equals("upi_payment_receipt")
                    || intent.getStringExtra("nsModule").equals("report_ledger_transaction")
            ){
                checkBoxLayout.setVisibility(View.GONE);
            }
            //initialize end


            Date nsDateFrom = ConvertFunctions.convertTextDateStringToDate(txtFromDate.getText().toString());
            Date nsDateTo = ConvertFunctions.convertTextDateStringToDate(txtToDate.getText().toString());


            if(!txtFromDate.getText().toString().equals("")){
                bundle.putString("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateFrom));
            }else{
                bundle.putString("fromDate", txtFromDate.getText().toString());
            }

            if(!txtToDate.getText().toString().equals("")){
                bundle.putString("toDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateTo));
            }else{
                bundle.putString("toDate", txtToDate.getText().toString());
            }

            //bundle.putString("fromDateBooked", new SimpleDateFormat("dd-MM-yyyy").format(nsDateBFrom));
            bundle.putString("fromDate1", txtFromDate.getText().toString());
            //bundle.putString("toDateBooked", new SimpleDateFormat("dd-MM-yyyy").format(nsDateBTo));
            bundle.putString("toDate1", txtToDate.getText().toString());

            bundle.putString("nsTransactionType",""+spinnerTransactionType.getSelectedItemPosition());

            if(!txtLocation.getText().toString().equals("")){
                bundle.putString("nsLocation", txtLocation.getText().toString());
                bundle.putString("nsLocationId", nsLocationId);
            }else{
                bundle.putString("nsLocation", "");
                bundle.putString("nsLocationId", "");
            }

            if (checkBank.isChecked()) {
                bundle.putString("bank", "1");
            } else {
                bundle.putString("bank", "2");
            }
            if (checkboxUPI.isChecked()) {
                bundle.putString("upi", "1");
            } else {
                bundle.putString("upi", "2");
            }

            if (checkboxCheque.isChecked()) {
                bundle.putString("cheque", "1");
            } else {
                bundle.putString("cheque", "2");
            }

        }else if(
                intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
                || intent.getStringExtra("nsModule").equals("transaction_sales_purchase_reminders")
                || intent.getStringExtra("nsModule").equals("opening_cash_balance")
                || intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                || intent.getStringExtra("nsModule").equals("transaction_price_variation")
                || intent.getStringExtra("nsModule").equals("master_day_reports")
                || intent.getStringExtra("nsModule").equals("transactions_stock_transfer")
                || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
        ){
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatImageView dateIcon;
            final AppCompatAutoCompleteTextView txtLocation;
            final AppCompatSpinner spinnerPaymentType,spinnerReceiptType;

            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);

            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);

            spinnerPaymentType = (AppCompatSpinner) v.findViewById(R.id.spinnerPaymentType);
            spinnerReceiptType = (AppCompatSpinner) v.findViewById(R.id.spinnerReceiptType);


            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);
            //initialize end


            Date nsDateFrom = ConvertFunctions.convertTextDateStringToDate(txtFromDate.getText().toString());
            Date nsDateTo = ConvertFunctions.convertTextDateStringToDate(txtToDate.getText().toString());


            if(!txtFromDate.getText().toString().equals("")){
                bundle.putString("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateFrom));
            }else{
                bundle.putString("fromDate", txtFromDate.getText().toString());
            }

            if(!txtToDate.getText().toString().equals("")){
                bundle.putString("toDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateTo));
            }else{
                bundle.putString("toDate", txtToDate.getText().toString());
            }

            //bundle.putString("fromDateBooked", new SimpleDateFormat("dd-MM-yyyy").format(nsDateBFrom));
            bundle.putString("fromDate1", txtFromDate.getText().toString());
            //bundle.putString("toDateBooked", new SimpleDateFormat("dd-MM-yyyy").format(nsDateBTo));
            bundle.putString("toDate1", txtToDate.getText().toString());

            bundle.putString("nsReceiptTypeId",""+spinnerReceiptType.getSelectedItemPosition());
            bundle.putString("nsPaymentTypeId",""+spinnerPaymentType.getSelectedItemPosition());

            if(
                    intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                    || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                    || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                    || intent.getStringExtra("nsModule").equals("master_day_reports")
                    || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
                    || intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
            ){
                bundle.putString("nsLocation", txtLocation.getText().toString());
                bundle.putString("nsLocationId", nsLocationId);
            }

            if(intent.getStringExtra("nsModule").equals("master_day_reports")){

                bundle.putString("nsUser", nsUser);
                bundle.putString("nsUserId", nsUserId);
            }

        }else if(
                intent.getStringExtra("nsModule").equals("master_expense")
         ){

            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatAutoCompleteTextView txtLocation,txtName,txtExpenseHead;
            ImageView imgClearName,imgClearExpense,imgClearLocation;

            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);

            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);
            txtName = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtName);
            txtExpenseHead = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtExpenseHead);

            imgClearExpense = (ImageView) v.findViewById(R.id.imgClearExpense);
            imgClearName = (ImageView) v.findViewById(R.id.imgClearName);
            imgClearLocation = (ImageView) v.findViewById(R.id.imgClearLocation);

            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);

            //initialize end


            Date nsDateFrom = ConvertFunctions.convertTextDateStringToDate(txtFromDate.getText().toString());
            Date nsDateTo = ConvertFunctions.convertTextDateStringToDate(txtToDate.getText().toString());


            if(!txtFromDate.getText().toString().equals("")){
                bundle.putString("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateFrom));
            }else{
                bundle.putString("fromDate", txtFromDate.getText().toString());
            }

            if(!txtToDate.getText().toString().equals("")){
                bundle.putString("toDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateTo));
            }else{
                bundle.putString("toDate", txtToDate.getText().toString());
            }

            //bundle.putString("fromDateBooked", new SimpleDateFormat("dd-MM-yyyy").format(nsDateBFrom));
            bundle.putString("fromDate1", txtFromDate.getText().toString());
            //bundle.putString("toDateBooked", new SimpleDateFormat("dd-MM-yyyy").format(nsDateBTo));
            bundle.putString("toDate1", txtToDate.getText().toString());

            bundle.putString("nsLocation", txtLocation.getText().toString());
            bundle.putString("nsLocationId", nsLocationId);

            bundle.putString("nsName", txtName.getText().toString());
            bundle.putString("nsNameId", nsLedgerNameExpId);

            bundle.putString("nsExpenseHead", txtExpenseHead.getText().toString());
            bundle.putString("nsExpenseHeadId", nsExpenseHeadId);

        }else if(
                intent.getStringExtra("nsModule").equals("transactions_in_out")
        ){
            //initialize start - should be repeated in returnFragmentValues
            final TextInputEditText txtToDate,txtFromDate;
            final AppCompatImageView dateIcon;
            txtFromDate = (TextInputEditText) v.findViewById(R.id.txtFromDate);
            txtToDate = (TextInputEditText) v.findViewById(R.id.txtToDate);
            dateIcon = (AppCompatImageView) v.findViewById(R.id.dateIcon);


            LinearLayout checkBoxLayout,spinnerLayout;
            checkBoxLayout = (LinearLayout) v.findViewById(R.id.checkBoxLayout);
            spinnerLayout = (LinearLayout) v.findViewById(R.id.spinnerLayout);

            RelativeLayout layoutRelCity,layoutRelArea,layoutRelUser;
            layoutRelArea = (RelativeLayout) v.findViewById(R.id.layoutRelArea);
            layoutRelCity = (RelativeLayout) v.findViewById(R.id.layoutRelCity);

            CheckBox checkboxDelivered,checkUndelivered,checkboxCredit,checkboxPartial,checkboxDeleted,checkboxIsVerified,checkboxIsTransportOrder;
            checkboxDelivered = (CheckBox) v.findViewById(R.id.checkboxDelivered);
            checkUndelivered = (CheckBox) v.findViewById(R.id.checkUndelivered);
            checkboxPartial = (CheckBox) v.findViewById(R.id.checkboxPartial);
            checkboxDeleted = (CheckBox) v.findViewById(R.id.checkboxDeleted);
            checkboxIsVerified = (CheckBox) v.findViewById(R.id.checkboxIsVerified);
            checkboxIsTransportOrder = (CheckBox) v.findViewById(R.id.checkboxIsTransportOrder);
            checkboxCredit = (CheckBox) v.findViewById(R.id.checkboxCredit);

            AppCompatSpinner spinnerCashCreditAll,spinnerPaymentType;
            spinnerCashCreditAll = (AppCompatSpinner) v.findViewById(R.id.spinnerCashCreditAll);
            spinnerPaymentType = (AppCompatSpinner) v.findViewById(R.id.spinnerPaymentType);

            final AppCompatAutoCompleteTextView txtArea,txtCity,txtName,txtUser;
            final AppCompatAutoCompleteTextView txtLocation;
            txtArea = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtArea);
            txtCity = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCity);
            txtUser = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtUser);
            txtName = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtName);

            ImageView imgClearCity,imgClearArea,imgClearLocation,imgClearCustomerName,imgClearUser;

            imgClearArea = (ImageView) v.findViewById(R.id.imgClearArea);
            imgClearCity = (ImageView) v.findViewById(R.id.imgClearCity);
            imgClearCustomerName = (ImageView) v.findViewById(R.id.imgClearCustomerName);
            imgClearLocation = (ImageView) v.findViewById(R.id.imgClearLocation);
            imgClearUser = (ImageView) v.findViewById(R.id.imgClearUser);
            TextInputLayout layoutPaymentType = (TextInputLayout) v.findViewById(R.id.layoutPaymentType);

            layoutRelUser = (RelativeLayout) v.findViewById(R.id.layoutRelUser);

            txtLocation = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtLocation);
            //Initialization Ends Here


            Date nsDateFrom = ConvertFunctions.convertTextDateStringToDate(txtFromDate.getText().toString());
            Date nsDateTo = ConvertFunctions.convertTextDateStringToDate(txtToDate.getText().toString());

            if(!txtFromDate.getText().toString().equals("")){
                bundle.putString("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateFrom));
            }else{
                bundle.putString("fromDate", txtFromDate.getText().toString());
            }

            if(!txtToDate.getText().toString().equals("")){
                bundle.putString("toDate", new SimpleDateFormat("dd-MM-yyyy").format(nsDateTo));
            }else{
                bundle.putString("toDate", txtToDate.getText().toString());
            }

            bundle.putString("fromDate1", txtFromDate.getText().toString());
            bundle.putString("toDate1", txtToDate.getText().toString());

            bundle.putString("nsLedgerName", txtName.getText().toString());
            bundle.putString("nsLedgerId", nsLedgerNameExpId);

            bundle.putString("nsUser", nsUser);
            bundle.putString("nsUserId", nsUserId);

            if (checkboxDelivered.isChecked()) {
                bundle.putString("nsDelivered", "1");
            }else {
                bundle.putString("nsDelivered", "2");
            }
            if (checkUndelivered.isChecked()) {
                bundle.putString("nsUnDelivered", "1");
            }else {
                bundle.putString("nsUnDelivered", "2");
            }
            if (checkboxPartial.isChecked()) {
                bundle.putString("nsPartial", "1");
            }else {
                bundle.putString("nsPartial", "2");
            }
            if (checkboxDeleted.isChecked()) {
                bundle.putString("nsDeleted", "1");
            }else {
                bundle.putString("nsDeleted", "2");
            }
            if (checkboxIsVerified.isChecked()) {
                bundle.putString("nsVerified", "1");
            }else {
                bundle.putString("nsVerified", "2");
            }
            if (checkboxIsTransportOrder.isChecked()) {
                bundle.putString("nsIsTransportOrder", "1");
            }else {
                bundle.putString("nsIsTransportOrder", "2");
            }

            if (checkboxCredit.isChecked()) {
                bundle.putString("nsIsCreditCustomer", "1");
            }else {
                bundle.putString("nsIsCreditCustomer", "2");
            }

            bundle.putString("nsSubModule", intent.getStringExtra("nsSubModule"));

            bundle.putString("nsCCType",""+spinnerCashCreditAll.getSelectedItemPosition());
            bundle.putString("nsPaymentType",""+spinnerPaymentType.getSelectedItemPosition());

            if(!txtArea.getText().toString().equals("")){
                bundle.putString("nsArea", txtArea.getText().toString());
                bundle.putString("nsAreaId", nsAreaId);
            }else{
                bundle.putString("nsArea", "");
                bundle.putString("nsAreaId", "");
            }

            if(!txtCity.getText().toString().equals("")){
                bundle.putString("nsCity", txtCity.getText().toString());
                bundle.putString("nsCityId", nsCityId);
            }else{
                bundle.putString("nsCity", "");
                bundle.putString("nsCityId", "");
            }

            if(!txtLocation.getText().toString().equals("")){
                bundle.putString("nsLocation", txtLocation.getText().toString());
                bundle.putString("nsLocationId", nsLocationId);
            }else{
                bundle.putString("nsLocation", "");
                bundle.putString("nsLocationId", "");
            }
        }else if(
                intent.getStringExtra("nsModule").equals("report_consolidated_ledger_balance")
        ) {

            //initialize start - should be repeated in returnFragmentValues


            final AppCompatAutoCompleteTextView txtArea,txtCity,txtRangeFrom,txtRangeTo;
            txtArea = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtArea);
            txtCity = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtCity);
            txtRangeFrom = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtRangeFrom);
            txtRangeTo = (AppCompatAutoCompleteTextView) v.findViewById(R.id.txtRangeTo);



            ImageView imgClearCity,imgClearArea,imgClearRangeFrom,imgClearRangeTo;

            imgClearArea = (ImageView) v.findViewById(R.id.imgClearArea);
            imgClearCity = (ImageView) v.findViewById(R.id.imgClearCity);
            imgClearRangeTo = (ImageView) v.findViewById(R.id.imgClearRangeTo);
            imgClearRangeFrom = (ImageView) v.findViewById(R.id.imgClearRangeFrom);

            //initialize Ends

            if(!txtArea.getText().toString().equals("")){
                bundle.putString("nsArea", txtArea.getText().toString());
                bundle.putString("nsAreaId", nsAreaId);
            }else{
                bundle.putString("nsArea", "");
                bundle.putString("nsAreaId", "");
            }

            if(!txtCity.getText().toString().equals("")){
                bundle.putString("nsCity", txtCity.getText().toString());
                bundle.putString("nsCityId", nsCityId);
            }else{
                bundle.putString("nsCity", "");
                bundle.putString("nsCityId", "");
            }

            if(!txtRangeFrom.getText().toString().equals("")){
                bundle.putString("nsRangefrom", txtRangeFrom.getText().toString());
            }else{
                bundle.putString("nsRangefrom", "");
            }

            if(!txtRangeTo.getText().toString().equals("")){
                bundle.putString("nsRangeTo", txtRangeTo.getText().toString());
            }else{
                bundle.putString("nsRangeTo", "");
            }

        }
        return bundle;
    }

    public  void assignCalendar(final TextInputEditText TextViewDate){
        Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog picker;
        picker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //Date date = new Date();

                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
                        Date dayFormat = new Date(year, monthOfYear, dayOfMonth-1);
                        String dayInWords = simpledateformat.format(dayFormat);

                        SimpleDateFormat simpledateformatmonth = new SimpleDateFormat("MMM");
                        Date monthFormat = new Date(year, monthOfYear, dayOfMonth);
                        String monthInWords = simpledateformatmonth.format(monthFormat);

                        TextViewDate.setText(dayInWords+", "+monthInWords+" "+String.format("%02d", dayOfMonth)+" "+String.format("%02d", year));/*,String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", year));*/
                    }
                }, year, month, day);


        //picker.getDatePicker().setMaxDate(System.currentTimeMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy");
        Date tempDate=null;
        try{
            if(!TextViewDate.getText().equals("")) {
                tempDate = sdf.parse(TextViewDate.getText().toString());
            }

        }catch (ParseException e) {
            e.printStackTrace();
        }

        if(tempDate!=null) {
            picker.updateDate(tempDate.getYear()+1900, tempDate.getMonth(), tempDate.getDate());
        }
        picker.show();
    }

    private void onDateIconClicked(ImageView dateIcon, final TextView lblFromDate, final TextView lblToDate, final Boolean setFromDate){
        PopupMenu popupMenu = new PopupMenu(getContext(), dateIcon);
        popupMenu.inflate(R.menu.filter_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_filter_today:
                        Calendar cldr = Calendar.getInstance();
                        if(setFromDate==false){
                            lblFromDate.setText("");
                        }else if(setFromDate==true){
                            lblFromDate.setText(new SimpleDateFormat("EEE, MMM dd yyyy").format(cldr.getTime()));
                        }
                        lblToDate.setText(new SimpleDateFormat("EEE, MMM dd yyyy").format(cldr.getTime()));
                        break;
                    case R.id.menu_filter_month:

                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
                        lblToDate.setText(new SimpleDateFormat("EEE, MMM dd yyyy").format(c1.getTime()));

                        if(setFromDate==false){
                            lblFromDate.setText("");
                        }else if(setFromDate==true){
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.DAY_OF_MONTH, 1);
                            lblFromDate.setText(new SimpleDateFormat("EEE, MMM dd yyyy").format(c.getTime()));
                        }
                        break;
                    case R.id.menu_filter_quarter:

                        if(setFromDate==false){
                            lblFromDate.setText("");
                        }else if(setFromDate==true){
                            Calendar calFirstDayOfQuarter = Calendar.getInstance();
                            calFirstDayOfQuarter.set(Calendar.MONTH, calFirstDayOfQuarter.get(Calendar.MONTH) / 3 * 3);
                            calFirstDayOfQuarter.set(Calendar.DAY_OF_MONTH, 1);
                            String firstDayOfQuarter = new SimpleDateFormat("EEE, MMM dd yyyy").format(calFirstDayOfQuarter.getTime());
                            lblFromDate.setText(firstDayOfQuarter);
                        }

                        Calendar calLastDayOfQuarter = Calendar.getInstance();
                        calLastDayOfQuarter.set(Calendar.MONTH, calLastDayOfQuarter.get(Calendar.MONTH) / 3 * 3 + 2);
                        calLastDayOfQuarter.set(Calendar.DAY_OF_MONTH, calLastDayOfQuarter.getActualMaximum(Calendar.DAY_OF_MONTH));
                        String lastDayOfQuarter = new SimpleDateFormat("EEE, MMM dd yyyy").format(calLastDayOfQuarter.getTime());
                        lblToDate.setText(lastDayOfQuarter);
                        break;
                    case R.id.menu_filter_fy_year:

                        Calendar cldfy = Calendar.getInstance();
                        int day = cldfy.get(Calendar.DAY_OF_MONTH);
                        int month = cldfy.get(Calendar.MONTH);
                        int year = cldfy.get(Calendar.YEAR);


                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
                        Date date123 = new Date(year, month, 31-1);
                        String day1 = simpledateformat.format(date123);

                        SimpleDateFormat simpledateformat1 = new SimpleDateFormat("EEE");
                        Date date1 = new Date(year, month, 01-1);
                        String day2 = simpledateformat1.format(date1);


                        SimpleDateFormat simpledateformat2 = new SimpleDateFormat("MMM");
                        Date date2 = new Date(year, 03-1, 15);
                        String month1 = simpledateformat2.format(date2);


                        SimpleDateFormat simpledateformat3 = new SimpleDateFormat("MMM");
                        Date date3 = new Date(year, 04-1, 15);
                        String month2 = simpledateformat3.format(date3);

                        if (month < 3) {
                            lblToDate.setText(day1+", "+month1+" 31"+" "+(year));
                            if(setFromDate==false){
                                lblFromDate.setText("");
                            }else if(setFromDate==true){
                                lblFromDate.setText(day2+", "+month2+" 01" + " " +(year - 1));
                            }
                        } else {
                            lblToDate.setText(day1+", "+month1+" 31" + " "+ (year + 1));
                            if(setFromDate==false){
                                lblFromDate.setText("");
                            }else if(setFromDate==true){
                                lblFromDate.setText(day2+", "+month2+" 01"+ " " + year);
                            }

                        }
                        break;
                    case R.id.menu_filter_no_filter:

                        if(intent.getStringExtra("nsModule").equals("transaction_sales_purchase_reminders")
                                || intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                                || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                                || intent.getStringExtra("nsModule").equals("master_day_reports")
                        ){
                            Toast.makeText(getContext(), "Not Allowed", Toast.LENGTH_SHORT).show();
                        }else {
                            lblToDate.setText("");
                            lblFromDate.setText("");
                        }

                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void fetchDataInformation(final String nsModule,
                                      final String nsFunctionality,
                                      final String nsCategory,
                                      final String nsSubCategory,
                                      final String nsBrand,
                                      final String nsLedgerId
    ){
        //progress.show();
        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        //progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        //DialogUtility.showDialog(getActivity(),response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(getActivity(),response,"Error");
                                //progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                String st="";

                                if(jsonData.length()>0){
                                    for(int i=0;i<jsonData.length();i++) {
                                        JSONObject objData = jsonData.getJSONObject(i);


                                        st+= (i+1)+"\nDate: "+objData.getString("nsSaleDate")+"\nName: "+objData.getString("nsNameDuringTransaction")+
                                                "\nPrice: "+objData.getString("nsPrice")+"\nQuantity: "+objData.getString("nsQuantity")+
                                                "\nLocation: "+objData.getString("nsLocationName")+"\n\n";

                                    }
                                    JSONObject objData = jsonData.getJSONObject(0);
                                    DialogUtility.showDialog(getActivity(),st,objData.getString("nsLedgerName"));
                                }
                                //progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            //progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(getActivity(),"Error: Please Check your Internet Connection","Alert");
                }else {
                    Log.e("Rakesh", "VolleyError: " + error.getMessage());
                }
                //progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCaption",preferenceConfig.readCompanyCaption());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("AndroidID", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", nsModule);
                params.put("functionality", nsFunctionality);
                if(!nsCategory.equals("")) {
                    params.put("prCategoryId", nsCategory);
                }
                if(!nsSubCategory.equals("")) {
                    params.put("prSubCategoryId", nsSubCategory);
                }
                if(!nsBrand.equals("")) {
                    params.put("prBrandId", nsBrand);
                }

                params.put("prLedgerId", nsLedgerId);

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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        requestQueue.add(stringRequest);
    }
}