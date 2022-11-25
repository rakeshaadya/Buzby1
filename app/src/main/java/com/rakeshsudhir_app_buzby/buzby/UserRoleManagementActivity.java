package com.rakeshsudhir_app_buzby.buzby;

import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserRoleManagementActivity extends BaseActivity{
    private ImageButton imageBtnFetch;
    private Button btnSave,btnDelete;
    private LinearLayout layoutCheckbox;
    private CheckBox checkboxModule,checkBoxAdd,
            checkBoxEditSelf, checkBoxEditAll,
            checkBoxDeleteSelf,checkBoxDeleteAll,
            checkBoxViewSelf,checkBoxViewAll;
    private TextView lblModule;
    private LinearLayout llModule;

    private String user_position_id="";
    private String nsModule="",nsId="",nsSubModule="",nsModuleId="",nsModuleAlias="",nsPosition="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_role_management);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);


        btnSave = (Button)findViewById(R.id.btnSave);
        btnDelete = (Button)findViewById(R.id.btnDelete);

        layoutCheckbox = (LinearLayout)findViewById(R.id.layoutCheckbox);
        llModule = (LinearLayout)findViewById(R.id.llModule);
        checkboxModule = (CheckBox)findViewById(R.id.checkboxModule);
        checkBoxAdd = (CheckBox)findViewById(R.id.checkboxAdd);
        checkBoxEditSelf = (CheckBox)findViewById(R.id.checkboxEditSelf);
        checkBoxEditAll = (CheckBox)findViewById(R.id.checkboxEditAll);
        checkBoxDeleteSelf = (CheckBox)findViewById(R.id.checkboxDeleteSelf);
        checkBoxDeleteAll = (CheckBox)findViewById(R.id.checkboxDeleteAll);
        checkBoxViewSelf = (CheckBox)findViewById(R.id.checkboxViewSelf);
        checkBoxViewAll = (CheckBox)findViewById(R.id.checkboxViewAll);
        lblModule = (TextView)findViewById(R.id.lblModule);

        nsModule = getIntent().getStringExtra("nsModule");
        nsSubModule = getIntent().getStringExtra("nsSubModule");
        nsId=getIntent().getStringExtra("nsId");
        nsModuleId=getIntent().getStringExtra("nsModuleId");
        nsModuleAlias=getIntent().getStringExtra("nsModuleAlias");
        nsPosition=getIntent().getStringExtra("nsPosition");

        toolbar.setTitle(nsPosition);
        setSupportActionBar(toolbar);
        lblModule.setText(nsModuleAlias);

        checkboxModule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkboxModule.isChecked()){
                    if(!checkBoxAdd.isChecked()
                            &&!checkBoxEditAll.isChecked()
                            &&!checkBoxEditSelf.isChecked()
                            &&!checkBoxDeleteSelf.isChecked()
                            &&!checkBoxDeleteAll.isChecked()
                            &&!checkBoxViewAll.isChecked()
                            &&!checkBoxViewSelf.isChecked()) {
                        checkBoxAdd.setChecked(true);
                        checkBoxEditAll.setChecked(true);
                        checkBoxEditSelf.setChecked(true);
                        checkBoxDeleteSelf.setChecked(true);
                        checkBoxDeleteAll.setChecked(true);
                        checkBoxViewAll.setChecked(true);
                        checkBoxViewSelf.setChecked(true);
                    }
                }else{
                    checkBoxAdd.setChecked(false);
                    checkBoxEditAll.setChecked(false);
                    checkBoxEditSelf.setChecked(false);
                    checkBoxDeleteSelf.setChecked(false);
                    checkBoxDeleteAll.setChecked(false);
                    checkBoxViewAll.setChecked(false);
                    checkBoxViewSelf.setChecked(false);
                }
            }
        });

        checkBoxAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxAdd.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }

                }else{
                    if(!checkBoxEditAll.isChecked()
                            &&!checkBoxEditSelf.isChecked()
                            &&!checkBoxDeleteSelf.isChecked()
                            &&!checkBoxDeleteAll.isChecked()
                            &&!checkBoxViewAll.isChecked()
                            &&!checkBoxViewSelf.isChecked()){
                        checkboxModule.setChecked(false);
                    }
                }
            }
        });

        checkBoxEditAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxEditAll.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }
                    checkBoxEditSelf.setChecked(true);
                    checkBoxViewAll.setChecked(true);
                    checkBoxViewSelf.setChecked(true);
                }else{
                    if(!checkBoxAdd.isChecked()
                            &&!checkBoxEditSelf.isChecked()
                            &&!checkBoxDeleteSelf.isChecked()
                            &&!checkBoxDeleteAll.isChecked()
                            &&!checkBoxViewAll.isChecked()
                            &&!checkBoxViewSelf.isChecked()){
                        checkboxModule.setChecked(false);
                    }
                }
            }
        });

        checkBoxEditSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxEditSelf.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }
                    checkBoxViewSelf.setChecked(true);
                }else{
                    checkBoxEditAll.setChecked(false);
                    if(!checkBoxAdd.isChecked()
                            &&!checkBoxEditAll.isChecked()
                            &&!checkBoxDeleteSelf.isChecked()
                            &&!checkBoxDeleteAll.isChecked()
                            &&!checkBoxViewAll.isChecked()
                            &&!checkBoxViewSelf.isChecked()){
                        checkboxModule.setChecked(false);
                    }
                }
            }
        });

        /*----------*/
        checkBoxDeleteAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxDeleteAll.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }
                    checkBoxDeleteSelf.setChecked(true);
                    checkBoxViewAll.setChecked(true);
                    checkBoxViewSelf.setChecked(true);
                }else{
                    if(!checkBoxAdd.isChecked()
                            &&!checkBoxEditSelf.isChecked()
                            &&!checkBoxDeleteSelf.isChecked()
                            &&!checkBoxEditAll.isChecked()
                            &&!checkBoxViewAll.isChecked()
                            &&!checkBoxViewSelf.isChecked()){
                        checkboxModule.setChecked(false);
                    }
                }
            }
        });

        checkBoxDeleteSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxDeleteSelf.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }
                    checkBoxViewSelf.setChecked(true);
                }else{
                    checkBoxDeleteAll.setChecked(false);
                    if(!checkBoxAdd.isChecked()
                            &&!checkBoxEditAll.isChecked()
                            &&!checkBoxEditSelf.isChecked()
                            &&!checkBoxDeleteAll.isChecked()
                            &&!checkBoxViewAll.isChecked()
                            &&!checkBoxViewSelf.isChecked()){
                        checkboxModule.setChecked(false);
                    }
                }
            }
        });
       /* ------------*/

        checkBoxViewAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxViewAll.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }
                    checkBoxViewSelf.setChecked(true);
                }else{
                    if(!checkBoxAdd.isChecked()
                            &&!checkBoxEditAll.isChecked()
                            &&!checkBoxEditSelf.isChecked()
                            &&!checkBoxDeleteAll.isChecked()
                            &&!checkBoxDeleteSelf.isChecked()
                            &&!checkBoxViewSelf.isChecked()){
                        checkboxModule.setChecked(false);
                    }
                }
            }
        });

        checkBoxViewSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxViewSelf.isChecked()){
                    if(!checkboxModule.isChecked()) {
                        checkboxModule.setChecked(true);
                    }
                    //checkBoxViewSelf.setChecked(true);
                }else{
                    if(!checkBoxAdd.isChecked()){
                        checkboxModule.setChecked(false);
                    }

                    checkBoxEditAll.setChecked(false);
                    checkBoxEditSelf.setChecked(false);
                    checkBoxDeleteSelf.setChecked(false);
                    checkBoxDeleteAll.setChecked(false);
                    checkBoxViewAll.setChecked(false);

                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                try {
                    JSONObject objItems = new JSONObject();
                    JSONArray jsonArrObjItems = new JSONArray();

                    objItems.put("prModuleName", nsSubModule);
                    objItems.put("prUserPositionId", user_position_id);
                    objItems.put("prModuleId",nsModuleId);

                    if(checkboxModule.isChecked()){
                        objItems.put("prModuleAccess", "1");
                    }else{
                        objItems.put("prModuleAccess", "2");
                    }

                    if(checkBoxAdd.isChecked()){
                        objItems.put("prAddAccess", "1");
                    }else{
                        objItems.put("prAddAccess", "2");
                    }

                    if(checkBoxEditAll.isChecked()){
                        objItems.put("prEditAllAccess", "1");
                    }else{
                        objItems.put("prEditAllAccess", "2");
                    }

                    if(checkBoxEditSelf.isChecked()){
                        objItems.put("prEditSelfAccess", "1");
                    }else{
                        objItems.put("prEditSelfAccess", "2");
                    }

                    if(checkBoxDeleteAll.isChecked()){
                        objItems.put("prDeleteAllAccess", "1");
                    }else{
                        objItems.put("prDeleteAllAccess", "2");
                    }

                    if(checkBoxDeleteSelf.isChecked()){
                        objItems.put("prDeleteSelfAccess", "1");
                    }else{
                        objItems.put("prDeleteSelfAccess", "2");
                    }

                    if(checkBoxViewAll.isChecked()){
                        objItems.put("prViewAllAccess", "1");
                    }else{
                        objItems.put("prViewAllAccess", "2");
                    }

                    if(checkBoxViewSelf.isChecked()){
                        objItems.put("prViewSelfAccess", "1");
                    }else{
                        objItems.put("prViewSelfAccess", "2");
                    }

                    jsonArrObjItems.put(objItems);

                    insertData("0",
                            jsonArrObjItems.toString(4),
                            nsModule,"",2,2,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        displayCheckBoxDetails();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserRoleManagementActivity.this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        });
    }



    public void displayCheckBoxDetails(){

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
                        //DialogUtility.showDialog(UserRoleManagementActivity.this,response,"");
                        try {
                            if(response.length()>=5 && response.substring(0,5).equals("Error")){
                                DialogUtility.showDialog(UserRoleManagementActivity.this,response,"Error");
                                progress.dismiss();
                            }else {
                                progress.dismiss();
                               JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonUserRole = jsonObject.getJSONArray("data");

                                //Toast.makeText(UserRoleManagementActivity.this, ""+jsonUserRole.toString(4), Toast.LENGTH_SHORT).show();


                                 if(jsonUserRole.length()>0){

                                    JSONObject objData = jsonUserRole.getJSONObject(0);

                                    user_position_id=objData.getString("nsUserPositionId");
                                    if(objData.getInt("nsAccessModule")==1){
                                        checkboxModule.setChecked(true);
                                    }else{
                                        checkboxModule.setChecked(false);
                                    }

                                    if(objData.getInt("nsAdd")==1){
                                        checkBoxAdd.setChecked(true);
                                    }else{
                                        checkBoxAdd.setChecked(false);
                                    }

                                    if(objData.getInt("nsEditAll")==1){
                                        checkBoxEditAll.setChecked(true);
                                    }else{
                                        checkBoxEditAll.setChecked(false);
                                    }

                                    if(objData.getInt("nsEditSelf")==1){
                                        checkBoxEditSelf.setChecked(true);
                                    }else{
                                        checkBoxEditSelf.setChecked(false);
                                    }

                                    if(objData.getInt("nsDeleteAll")==1){
                                        checkBoxDeleteAll.setChecked(true);
                                    }else{
                                        checkBoxDeleteAll.setChecked(false);
                                    }

                                    if(objData.getInt("nsDeleteSelf")==1){
                                        checkBoxDeleteSelf.setChecked(true);
                                    }else{
                                        checkBoxDeleteSelf.setChecked(false);
                                    }

                                    if(objData.getInt("nsViewAll")==1){
                                        checkBoxViewAll.setChecked(true);
                                    }else{
                                        checkBoxViewAll.setChecked(false);
                                    }

                                    if(objData.getInt("nsViewSelf")==1){
                                        checkBoxViewSelf.setChecked(true);
                                    }else{
                                        checkBoxViewSelf.setChecked(false);
                                    }


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
                if(error.getMessage().startsWith("java.net.ConnectException")){
                    DialogUtility.showDialog(UserRoleManagementActivity.this,"Error: Please Check your Internet Connection","Alert");
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

                params.put("id",nsId);
                params.put("module", nsModule);
                params.put("functionality", "fetch_access_values");

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


}
