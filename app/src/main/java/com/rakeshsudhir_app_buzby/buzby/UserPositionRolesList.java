package com.rakeshsudhir_app_buzby.buzby;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserPositionRolesList extends BaseActivity implements MyDataAdapter.myAdapterListener, SwipeRefreshLayout.OnRefreshListener{

    //SharedPreferenceConfig preferenceConfig;

    private TextInputLayout tilUserPosition;
    private AppCompatAutoCompleteTextView txtUserPosition;
    private LinearLayout llPosition;
    private TextView lblPosition;
    private ImageView imgAddPosition;
    private RelativeLayout rlUserPosition;

    private JSONArray myDataList;
    private ArrayList<String> myDataListColumns;
    private MyDataAdapter myDataAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progress;

    private String nsModule="",user_position_id="0",nsId="",nsUserPositionName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Role Management");
        setSupportActionBar(toolbar);

        nsModule = getIntent().getStringExtra("nsModule");
        Intent intent = getIntent();
        if (intent.hasExtra("nsId")) {
            nsId=getIntent().getStringExtra("nsId");
        }



        tilUserPosition = (TextInputLayout)findViewById(R.id.tilUserPosition);
        llPosition = (LinearLayout) findViewById(R.id.llPosition);
        txtUserPosition = (AppCompatAutoCompleteTextView)findViewById(R.id.txtUserPosition);
        lblPosition = (TextView)findViewById(R.id.lblPosition);
        imgAddPosition = (ImageView) findViewById(R.id.imgAddPosition);
        rlUserPosition = (RelativeLayout) findViewById(R.id.rlUserPosition);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myDataList = new JSONArray();
        myDataListColumns = new ArrayList<>();
        context=this;
        myDataAdapter = new MyDataAdapter(this,myDataListColumns, myDataList,this,nsModule,"");


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setRefreshing(true);

        //displaydata();

        displayDataForAutoEditText();

        imgAddPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserPositionRolesList.this,ListActivity.class);
                i.putExtra("nsModule","user_position");
                i.putExtra("nsSubModule","returnValue");
                startActivityForResult(i,1);
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserPositionRolesList.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });


    }


    private void displayData(final String userPositionId) {

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        progress=new ProgressDialog(this);
        progress.setMessage("Loading Data...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(temp.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(UserPositionRolesList.this,response,"Error");
                                progress.dismiss();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonData = jsonObject.getJSONArray("data");

                                if(jsonData.length()>0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    rlUserPosition.setVisibility(View.GONE);
                                    llPosition.setVisibility(View.VISIBLE);

                                    lblPosition.setText(jsonData.getJSONObject(0).getString("nsUserPosition"));

                                    myDataList=jsonData;
                                    myDataAdapter = new MyDataAdapter(context, myDataListColumns, myDataList, (MyDataAdapter.myAdapterListener) context, nsModule,"");
                                    recyclerView.setAdapter(myDataAdapter);
                                }
                                progress.dismiss();

                            }
                        }  catch (Exception e) {
                            //lblMessage.setText(e.getMessage());
                            progress.dismiss();
                            Toast.makeText(UserPositionRolesList.this, "expec "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(error.getMessage().equals("null") || error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(UserPositionRolesList.this,"Error: Please Check your Internet Connection","Alert");
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
                params.put("AndroidID", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("AndroidUQ",preferenceConfig.readAndroidUQ());
                params.put("module", nsModule);
                params.put("functionality", "fetch_access_values");
                params.put("prUserPositionId",userPositionId);
                //params.put("format", "list");
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

        swipeRefreshLayout.setRefreshing(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    public void displayDataForAutoEditText() {

        final ProgressDialog progress;
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setMessage("Loading...");

        String JSON_URL = preferenceConfig.readJSONUrl().concat("cf.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //DialogUtility.showDialog(temp.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(UserPositionRolesList.this,response,"Error");
                                progress.dismiss();
                            }else {
                                progress.dismiss();
                                JSONObject jsonObject = new JSONObject(response);
                                final JSONArray jsonUsers = jsonObject.getJSONArray("data");
                                if(jsonUsers.length()>0){

                                    final String[] strArrayUserPosition = new String[jsonUsers.length()];

                                    for (int i = 0; i < jsonUsers.length(); i++) {
                                        JSONObject objData = jsonUsers.getJSONObject(i);
                                        strArrayUserPosition[i]=objData.getString("nsName");
                                    }

                                    ArrayAdapter<String> adapterUserPosition = new ArrayAdapter<String>(getApplicationContext(),R.layout.r_dropdown_list, strArrayUserPosition);
                                    txtUserPosition.setAdapter(adapterUserPosition);
                                    txtUserPosition.setThreshold(1);

                                    txtUserPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String selection = (String) parent.getItemAtPosition(position);

                                            for (int i = 0; i<strArrayUserPosition.length; i++) {
                                                if (strArrayUserPosition[i].equals(selection)) {
                                                    try {
                                                        JSONObject objDataUserName = jsonUsers.getJSONObject(i);
                                                        user_position_id = objDataUserName.getString("nsId");
                                                        displayData(user_position_id);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    break;
                                                }
                                            }

                                        }
                                    });

                                    txtUserPosition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            if(!b) {

                                                String str = txtUserPosition.getText().toString();
                                                imgAddPosition.setVisibility(View.GONE);
                                                ListAdapter listAdapter = txtUserPosition.getAdapter();
                                                for(int i = 0; i < listAdapter.getCount(); i++) {
                                                    String temp = listAdapter.getItem(i).toString();
                                                    if(txtUserPosition.getText().toString().trim().matches("") || str.compareTo(temp ) == 0) {
                                                        return;
                                                    }
                                                }
                                                user_position_id="";
                                                txtUserPosition.setError("Invalid");
                                            }else {
                                                imgAddPosition.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                        }  catch (Exception e) {
                            Log.d("Rakesh", "Exception "+e.getMessage());
                            progress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(UserPositionRolesList.this,"Error: Please Check your Internet Connection","Alert");
                }else {*/
                    Log.e("Rakesh", "VolleyError: " + error.getMessage());
                //}
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

                params.put("module", "user_position");
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
    public void OnItemSelected(JSONObject item, int position) {
        try {
            Intent i = new Intent(UserPositionRolesList.this,UserRoleManagementActivity.class);
            i.putExtra("nsModule",nsModule);
            i.putExtra("nsSubModule",item.getString("nsModule"));
            i.putExtra("nsId",item.getString("nsId"));
            i.putExtra("nsModuleId",item.getString("nsModuleId"));
            i.putExtra("nsModuleAlias",item.getString("nsModuleAlias"));
            i.putExtra("nsPosition",item.getString("nsUserPosition"));
            startActivity(i);

            //Toast.makeText(this, item.getString("nsId")+" "+item.getString("nsModule"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recyclerCheckboxChecked(JSONObject item, int position, int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if(resultCode==RESULT_OK) {
                nsUserPositionName = data.getStringExtra("nsName");
                user_position_id = data.getStringExtra("nsId");

                txtUserPosition.setText(nsUserPositionName);
            }
            if(requestCode==RESULT_CANCELED){

            }
        }
    }

    @Override
    public void onRefresh() {

        final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setTitle("Alert");
        alertbox.setMessage("Are you sure, you want to refresh this activity?");
        alertbox.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        myDataAdapter.clear();
                        /*swipeRefreshLayout.setRefreshing(true);
                        displayData123("9");*/
                        finish();
                        Intent returnIntent = getIntent();
                        startActivity(returnIntent);
                    }
                });
        alertbox.setNegativeButton("No", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        alertbox.show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayData(user_position_id);
    }
}
