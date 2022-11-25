package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SortAndFilterActivity extends AppCompatActivity  implements FilterFragment.OnFilterSelectedListener{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public Intent intent;

    SortFragment sortFragment = new SortFragment();
    FilterFragment filterFragment = new FilterFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_and_filter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sort and Filter");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TextView btnApply = (TextView) findViewById(R.id.btnApply);

        intent = getIntent();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("nsModule",intent.getStringExtra("nsModule"));
                if(
                        intent.getStringExtra("nsModule").equals("consolidated_online_balance_report")
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
                ){
                    returnIntent.putExtras(filterFragment.getFragmentValues());
                    returnIntent.putExtra("sortType", sortFragment.radioString);
                }
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //}
        //adapter.addFragment(sortFragment, "SORT");
        adapter.addFragment(filterFragment, "FILTER");

        //if(!intent.getStringExtra("nsModule").equals("report_ledger_transaction")) {
            adapter.addFragment(sortFragment, "SORT");


        viewPager.setAdapter(adapter);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FilterFragment) {
            FilterFragment filterFragment = (FilterFragment) fragment;
            filterFragment.setOnFilterSelectedListener(this);
        }
    }

    @Override
    public void onCommonFilterButtonClicked(String buttonFunctionality) {
        //if(buttonFunctionality.equals("DueTomorrow")){
        sortFragment.setSortOption(buttonFunctionality);
        //}
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}