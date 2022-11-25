package com.rakeshsudhir_app_buzby.buzby;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;


public class SortFragment extends Fragment {

    public RadioGroup radioGroup;
    public RadioButton radioAscending,radioDescending;

    View v;

    public String radioString;
    Intent intent;
    SharedPreferenceConfig preferenceConfig;

    public SortFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intent = getActivity().getIntent();

        if(intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")
                || intent.getStringExtra("nsModule").equals("upi_payment_receipt")
                || intent.getStringExtra("nsModule").equals("report_ledger_transaction")
                || intent.getStringExtra("nsModule").equals("transactions_in_out")
                || intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
                || intent.getStringExtra("nsModule").equals("transaction_sales_purchase_reminders")
                || intent.getStringExtra("nsModule").equals("opening_cash_balance")
                || intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                || intent.getStringExtra("nsModule").equals("master_expense")
                || intent.getStringExtra("nsModule").equals("transaction_price_variation")
                || intent.getStringExtra("nsModule").equals("master_day_reports")
                || intent.getStringExtra("nsModule").equals("transactions_stock_transfer")
                || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
                || intent.getStringExtra("nsModule").equals("report_consolidated_ledger_balance")
        ) {
            // Inflate the layout for this fragment
            v = inflater.inflate(R.layout.fragment_sort, container, false);
            preferenceConfig=new SharedPreferenceConfig(v.getContext());
            radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);

            radioString = intent.getStringExtra("sortType");

            radioAscending = (RadioButton) v.findViewById(R.id.radioAscending);
            radioDescending = (RadioButton) v.findViewById(R.id.radioDescending);

            if (radioString.equals("radioAscending")) {
                radioAscending.setChecked(true);
            } else if (radioString.equals("radioDescending")) {
                radioDescending.setChecked(true);
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")
                        || intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")
                        || intent.getStringExtra("nsModule").equals("report_ledger_transaction")
                        || intent.getStringExtra("nsModule").equals("transactions_in_out")
                        || intent.getStringExtra("nsModule").equals("transaction_receipt_payment")
                        || intent.getStringExtra("nsModule").equals("transaction_sales_purchase_reminders")
                        || intent.getStringExtra("nsModule").equals("opening_cash_balance")
                        || intent.getStringExtra("nsModule").equals("fetch_inventory_detailed_report")
                        || intent.getStringExtra("nsModule").equals("transaction_stock_management")
                        || intent.getStringExtra("nsModule").equals("consolidated_inventory_report")
                        || intent.getStringExtra("nsModule").equals("master_expense")
                        || intent.getStringExtra("nsModule").equals("transaction_price_variation")
                        || intent.getStringExtra("nsModule").equals("master_day_reports")
                        || intent.getStringExtra("nsModule").equals("transactions_stock_transfer")
                        || intent.getStringExtra("nsModule").equals("master_daily_stock_closing")
                        || intent.getStringExtra("nsModule").equals("report_consolidated_ledger_balance")
                ) {
                    switch (checkedId) {
                        case R.id.radioAscending:
                            radioString = "radioAscending";
                            break;

                        case R.id.radioDescending:
                            radioString = "radioDescending";
                            break;

                    }
                }
            }
        });

        return v;
    }

    public void setSortOption(String message_option){
        if(intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")) {

        }
    }
}