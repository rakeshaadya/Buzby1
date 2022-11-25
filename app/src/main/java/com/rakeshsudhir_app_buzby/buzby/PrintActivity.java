package com.rakeshsudhir_app_buzby.buzby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.Set;
import java.util.UUID;

public class PrintActivity extends Activity /*implements Runnable*/ {

    private SharedPreferenceConfig preferenceConfig;
    private JSONArray myDataList;



    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;


    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;

    BluetoothDevice mBluetoothDevice;

    TextView lblPrinterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);


        lblPrinterName = (TextView)findViewById(R.id.lblPrinterName);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        if(preferenceConfig.readBluetoothDeviceName().equals("")){
            lblPrinterName.setText("No Device Connected");
        }else{
            lblPrinterName.setText("You Have Connected to "+preferenceConfig.readBluetoothDeviceName());
        }


        mScan = (Button) findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter == null) {
                    Toast.makeText(PrintActivity.this, "NULL", Toast.LENGTH_SHORT).show();
                }else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintActivity.this,
                                DeviceActivityList.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });
        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setVisibility(View.GONE);


        //mPrint.setVisibility(View.GONE);

        /*mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {

                displayData();

            }
        });
*/
        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                lblPrinterName.setText("Device DisConnected. Please Scan and Select New Device if Required");
                Intent i = new Intent(PrintActivity.this, BluetoothServices.class);
                stopService(i);
            }
        });

        // Rakesh(preferenceConfig.readBluetoothDeviceName());
    }

    /*@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }*/
    /*public void PrintThread(final JSONArray cartPrint){
        Toast.makeText(this, ""+cartPrint, Toast.LENGTH_SHORT).show();
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    String BILL = "";

                    BILL = "              "+"GB"+"    \n";
                    BILL = BILL+String.format("%1$-15s %2$15s","02-FEB-2020","ESTIMATE : 123");
                    BILL = BILL+"\n"+"Rahul S Gare"+"    \n";
                    BILL = BILL+"Keshwapur Hubli"+"    \n";
                    BILL = BILL
                            + "-------------------------------";


                    *//*BILL = BILL + String.format("%1$-14s %2$14s %3$15s %4$14s %514s", "Item", "Qty", "Rate", "Total");
                    BILL = BILL + "\n";
                    BILL = BILL
                            + "---------------------------------------------------------------";*//*

                    for(int i=0; i<cartPrint.length();i++){
                        JSONObject cartObjData = cartPrint.getJSONObject(i);
                        BILL = BILL + "\n" + String.format("%1$-2s %2$5s %3$4s %4$7s %5$9s",
                                cartObjData.getString("nsSubCatName"),
                                cartObjData.getDouble("nsPrice"), cartObjData.getInt("nsQuantity"),
                                cartObjData.getString("nsDisplayName"),
                                cartObjData.getInt("nsQuantity")*cartObjData.getDouble("nsPrice")*cartObjData.getDouble("nsUnitSize"));

                    }
                    BILL = BILL + "\n" + String.format("%1$-2s %2$5s %3$4s %4$7s %5$9s","","","----","","---------");
                    BILL = BILL + "\n" + String.format("%1$-2s %2$5s %3$4s %4$7s %5$9s","","","(2)","","10000.0");
                    BILL = BILL + "\n" + String.format("%1$20s %2$10s","Add Charges : ","12345.0");
                    BILL = BILL + "\n" + String.format("%1$20s %2$10s","Old Bal : ","123456.0");
                    BILL = BILL + "\n" + String.format("%1$20s %2$10s","","---------");
                    BILL = BILL + "\n" + String.format("%1$20s %2$10s","Total : ","1000000.0");
                    BILL = BILL + "\n" + String.format("%1$20s %2$10s","Paid : ","10000000.0");
                    BILL = BILL + "\n" + String.format("%1$20s %2$10s","Balance : ","10000000.0");


                    *//*BILL = BILL  + "\n " + String.format("%1$-3s %2$5s %3$3s %4$5s %5$10s", "","", "(2)", "","99999");
                    BILL = BILL  + "\n" +String.format("%1$-1s %2$8s %3$3s %4$5s %5$10s", "","O/B Bal", "", "","55555");*//*


                    BILL = BILL
                            + "-------------------------------";

                    BILL = BILL + "\n\n\n\n\n\n\n\n\n";

                    BILL = BILL+"              "+"GB"+"    \n";
                    BILL = BILL+String.format("%1$-15s %2$15s","02-FEB-2020","ESTIMATE : 123");
                    BILL = BILL+"\n"+"Rahul S Gare"+"    \n";
                    BILL = BILL+"Keshwapur Hubli"+"    \n";
                    BILL = BILL
                            + "-------------------------------";

                    for(int i=0; i<cartPrint.length();i++){
                        JSONObject cartObjData = cartPrint.getJSONObject(i);
                        BILL = BILL + "\n" + String.format("%1$-3s %2$-12s %3$7s %4$5s",
                                cartObjData.getString("nsSubCatName"),
                                cartObjData.getString("nsBrandName"),
                                cartObjData.getString("nsDisplayName"),
                                cartObjData.getInt("nsQuantity"));
                    }

                    BILL = BILL + "\n";
                    BILL = BILL+String.format("%1$30s","(2)");
                    BILL = BILL + "\n";
                    BILL = BILL+String.format("%1$19s","(-: TD :-)");
                    BILL = BILL + "\n\n";
                    BILL = BILL+String.format("%1$-15s %2$15s","02-FEB-2020","ESTIMATE : 123");
                    BILL = BILL
                            + "\n-------------------------------";


                    BILL = BILL + "\n\n\n\n\n\n\n\n";


                    byte[] bb2 = new byte[]{0x1B,0x21,0x20};


                    os.write(bb2);

                    os.write(BILL.getBytes());


                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }*/

    /*@Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }*/


    /*public void Rakesh(final String mDeviceAddress){


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            //Toast.makeText(PrintActivity.this, "Message1", Toast.LENGTH_SHORT).show();
        }else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            } else {

                mBluetoothDevice = mBluetoothAdapter
                        .getRemoteDevice(mDeviceAddress);
                //Toast.makeText(this, "zzzzzzzzzzzzzzzzz "+mBluetoothDevice, Toast.LENGTH_SHORT).show();
                *//*mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                        "Connecting...", mBluetoothDevice.getName() + " : "
                                + mBluetoothDevice.getAddress(), true, false);*//*
                Thread mBlutoothConnectThread = new Thread((Runnable) this);
                mBlutoothConnectThread.start();

            }
        }
    }*/

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
        //Toast.makeText(this, REQUEST_CONNECT_DEVICE+" "+mDataIntent +" "+mRequestCode, Toast.LENGTH_SHORT).show();
        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    String mDeviceName = mExtra.getString("DeviceName");
                    preferenceConfig.writeBluetoothDeviceName(mDeviceName);
                    lblPrinterName.setText("You Have Connected to "+preferenceConfig.readBluetoothDeviceName());
                    Log.v(TAG, "Coming incoming address " + mExtra);

                    /*mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);*/
                    /*mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);*/
                    /*Thread mBlutoothConnectThread = new Thread((Runnable) this);
                    mBlutoothConnectThread.start();*/
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread

                    Intent i = new Intent(PrintActivity.this, BluetoothServices.class);
                    i.putExtra("bluetooth_device",mDeviceAddress);
                    startService(i);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintActivity.this,
                            DeviceActivityList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }



    /*@Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }*/

    /*private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrintActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }
*/
    private void displayData() {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        //Toast.makeText(this, ""+JSON_URL, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(PrintActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            if(response.length()>=5 && response.substring(0,5)=="Error"){
                                //Toast.makeText(PrintActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                JSONArray jsonDataItems = jsonObject.getJSONArray("dataItems");
                                //Toast.makeText(PrintActivity.this, ""+jsonDataItems, Toast.LENGTH_SHORT).show();
                                // PrintThread(jsonDataItems);

                                /*JSONArray jsonCartItems = new JSONArray();


                                for (int i=0; i<jsonDataItems.length();i++){
                                    JSONObject cartObjData = jsonDataItems.getJSONObject(i);
                                    JSONObject objProduct = new JSONObject();
                                    objProduct.put("nsInventoryName",cartObjData.getString("nsInventoryName"));
                                    objProduct.put("nsInventoryPrice",cartObjData.getDouble("nsPrice"));
                                    objProduct.put("nsSelectedPosition",i);
                                    //objProduct.put("nsInventoryId", sales_inv_id);
                                    objProduct.put("nsInventoryQuantity",cartObjData.getDouble("nsQuantity"));
                                    objProduct.put("nsUnitSize",cartObjData.getDouble("nsUnitSize"));
                                    objProduct.put("nsInvId",cartObjData.getString("nsInventoryId"));
                                    objProduct.put("nsDeliveredQty",cartObjData.getString("nsDeliveredQty"));
                                    objProduct.put("nsDeliveryStatus",cartObjData.getString("nsDeliveryStatus"));
                                    objProduct.put("nsRemainingQty",cartObjData.getString("nsRemainingQty"));
                                    objProduct.put("nsLocationId",cartObjData.getString("nsLocationId"));

                                    jsonCartItems.put(objProduct);

                                }*/
                                myDataList=jsonDataItems;




                            }

                        }  catch (Exception e) {
                            Toast.makeText(PrintActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PrintActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company",preferenceConfig.readCompany());
                params.put("UserID",preferenceConfig.readUserId());
                params.put("module", "nf_transaction_sales");
                params.put("functionality", "fetch_table_data_for_invoice");
                params.put("format", "individual");
                params.put("id","20");

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
