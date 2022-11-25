package com.rakeshsudhir_app_buzby.buzby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class functionPrint{

    public static void displayDataForPrint(final Context mContext,final String nsModule,
                                           final String nsId,final int receiptPrintStatus,
                                           final int isDuplicate,final int shortCut,
                                           final int printBill, final int printGP
    ){


        Log.d("AadyaRakesh", "onResponse id : "+nsId);
        final ProgressDialog progress;
        progress=new ProgressDialog(mContext);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        final SharedPreferenceConfig preferenceConfig = new SharedPreferenceConfig(mContext);
        progress.show();

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress.setMessage("Fetching Data...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //lblMessage.setText(response);
                        //DialogUtility.showDialog((Activity) mContext,response,"");
                        Log.d("fuctionPrintRakesh",response);
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog((Activity) mContext,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    if(nsModule.equals("master_expense")) {
                                        printString.expensePrintString(response,isDuplicate);
                                    }else if(nsModule.equals("transaction_receipt_payment")){
                                        printString.receiptPaymentPrintString(response,mContext,isDuplicate);
                                    }else if(nsModule.equals("transactions_in_out")){
                                        //Toast.makeText((Activity) mContext, "id : "+response, Toast.LENGTH_SHORT).show();
                                        //Log.d("AadyaRakesh", "onResponse: "+response);
                                        printString.salesPrintString(response,isDuplicate,shortCut,printBill,printGP);
                                    }
                                }
                                progress.dismiss();
                            }

                        }  catch (Exception e) {
                            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog((Activity) mContext,"Error: Please Check your Internet Connection","Alert");
                }else {
                    Log.e("Rakesh", "VolleyError: " + error.getMessage());
                }
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCaption",preferenceConfig.readCompanyCaption());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("AndroidID", Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", nsModule);
                params.put("functionality", "fetch_table_data_invoice_print");
                if(nsModule.equals("transaction_receipt_payment") && receiptPrintStatus==1) {
                    params.put("nsReceiptPrintStatus", String.valueOf(receiptPrintStatus));
                }
                params.put("id", nsId);


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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        requestQueue.add(stringRequest);

    }
}
