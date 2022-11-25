package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MasterActivity extends BaseActivity {

    LinearLayout ll_masters,ll_transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        ll_masters = (LinearLayout) findViewById(R.id.ll_masters);
        ll_transaction = (LinearLayout) findViewById(R.id.ll_transaction);

        Intent intent = getIntent();
        if (intent.hasExtra("nsMenuItemhead") && getIntent().getStringExtra("nsMenuItemhead").equals("masters")) {
            ll_masters.setVisibility(View.VISIBLE);
        }

        if (intent.hasExtra("nsMenuItemhead") && getIntent().getStringExtra("nsMenuItemhead").equals("transaction")) {
            ll_transaction.setVisibility(View.VISIBLE);
        }
        toolbar.setTitle(getIntent().getStringExtra("nsMenuItemhead"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MasterActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
    }
}