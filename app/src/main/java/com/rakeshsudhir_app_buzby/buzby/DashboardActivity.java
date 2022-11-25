package com.rakeshsudhir_app_buzby.buzby;


import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DashboardActivity extends BaseActivity{

    TextView lblDetails;
    CardView btnLinkMaster,btnLinkTransaction,cardGeneral;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.content_dashboard_, null, false);
        drawer.addView(contentView, 0);

        lblDetails = (TextView)findViewById(R.id.lblDetails);
        btnLinkMaster = (CardView)findViewById(R.id.btnLinkMaster);
        btnLinkTransaction = (CardView)findViewById(R.id.btnLinkTransaction);
        cardGeneral = (CardView)findViewById(R.id.cardGeneral);

        btnLinkMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this,MasterActivity.class);
                i.putExtra("nsMenuItemhead","masters");
                startActivity(i);
                //PrintThread();
            }
        });

        btnLinkTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this,MasterActivity.class);
                i.putExtra("nsMenuItemhead","transaction");
                startActivity(i);

                /*Intent i = new Intent(DashboardActivity.this, PrintActivity.class);

                //i.putExtra("DeviceAddress", mDeviceAddress);
                startActivity(i);*/


            }
        });

        cardGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*Toast.makeText(DashboardActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");

                StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(DashboardActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                               
                                    if (response.length() >= 5 && response.substring(0, 5).equals("Error")) {
                                        //DialogUtility.showDialog(DashboardActivity.this, response, "Error");
                                        //Toast.makeText(DashboardActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                                        Log.d("1234567890",response);
                                    } else {
                                        Toast.makeText(DashboardActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                    }
                                


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DashboardActivity.this, "Error Response "+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("companyCaption",preferenceConfig.readCompanyCaption());
                        params.put("UserID",preferenceConfig.readUserId());
                        params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                        params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                        params.put("module", "master_insert_old_data_to_new_db_a");
                        params.put("functionality", "add");

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

                RequestQueue requestQueue = Volley.newRequestQueue(DashboardActivity.this);

                requestQueue.add(stringRequest);*/
            }
        });




        final Calendar cldr1 = Calendar.getInstance();
        lblDetails.setText(preferenceConfig.readlocationName()+"\n"+new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));


        if(!preferenceConfig.readLoginDate().equals(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()))){
            preferenceConfig.writeLoginStatus(false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
            preferenceConfig.writeLocationDetails("","");
            Intent i = new Intent(DashboardActivity.this,LoginActivity.class);
            startActivity(i);
            finish();

        }
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        final Calendar cldr1 = Calendar.getInstance();
        if(preferenceConfig.readlocationName().equals("") || preferenceConfig.readLocationId().equals("")){
            displayLocationToSetasDefault();
        }

        //lblDetails.setText(preferenceConfig.readlocationName()+"\n"+new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()));
        //Toast.makeText(this, ""+preferenceConfig.readLoginDate(), Toast.LENGTH_SHORT).show();

        if(!preferenceConfig.readLoginDate().equals(new SimpleDateFormat("dd-MM-yyyy").format(cldr1.getTime()))){
            preferenceConfig.writeLoginStatus(false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
            preferenceConfig.writeLocationDetails("","");
            Intent i = new Intent(DashboardActivity.this,LoginActivity.class);
            startActivity(i);
            finish();

        }
    }
}
