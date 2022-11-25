package com.rakeshsudhir_app_buzby.buzby;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class SharedPreferenceConfig extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Context context;

    public SharedPreferenceConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.login_preference), context.MODE_PRIVATE);
    }
    public void writeLoginStatus(boolean status, String userId,
                                 String userName, String userPosition,
                                 String company, String companyCaption,
                                 String androidUQ, String loginDate){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_status), status);

        editor.putString(context.getResources().getString(R.string.nsId),userId);
        editor.putString(context.getResources().getString(R.string.nsName),userName);

        editor.putString(context.getResources().getString(R.string.nsUserPosition),userPosition);
        editor.putString(context.getResources().getString(R.string.nsCompany),company);
        editor.putString(context.getResources().getString(R.string.nsCompanyCaption),companyCaption);
        editor.putString(context.getResources().getString(R.string.nsAndroidUQ),androidUQ);
        editor.putString(context.getResources().getString(R.string.nsLoginDate),loginDate);

        editor.commit();
    }


    public void writeBluetoothDeviceName(String bluetoothDeviceName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.bluetoothDeviceName), String.valueOf(bluetoothDeviceName));
        editor.commit();
    }
    public String readBluetoothDeviceName() {
        String bluetoothDeviceName;

        bluetoothDeviceName = sharedPreferences.getString(context.getResources().getString(R.string.bluetoothDeviceName),"");
        return bluetoothDeviceName;
    }



    public void writeLocationDetails(String locationName,String locationId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.locationName), locationName);
        editor.putString(context.getResources().getString(R.string.locationId), locationId);
        editor.commit();
    }

    public Boolean readLoginStatus() {
        boolean status = false;
        status = sharedPreferences.getBoolean(context.getResources().getString(R.string.login_status), false);
        return status;
    }

    public String readUserId(){
        String userId;

        userId = sharedPreferences.getString(context.getResources().getString(R.string.nsId),"");
        return userId;
    }

    public String readLoginDate(){
        String mDate;

        mDate = sharedPreferences.getString(context.getResources().getString(R.string.nsLoginDate),"");
        return mDate;
    }


    public String readUserName(){
        String userName;

        userName = sharedPreferences.getString(context.getResources().getString(R.string.nsName),"");
        return userName;
    }

    public String readUserPosition(){
        String userPosition;

        userPosition = sharedPreferences.getString(context.getResources().getString(R.string.nsUserPosition),"");
        return userPosition;
    }

    public String readCompany(){
        String company;

        company = sharedPreferences.getString(context.getResources().getString(R.string.nsCompany),"");
        return company;
    }

    public String readCompanyCaption(){
        String companyCaption;

        companyCaption = sharedPreferences.getString(context.getResources().getString(R.string.nsCompanyCaption),"");
        return companyCaption;
    }

    public String readAndroidUQ(){
        String androidUQ;

        androidUQ = sharedPreferences.getString(context.getResources().getString(R.string.nsAndroidUQ),"");
        return androidUQ;
    }

    public String readlocationName(){
        String locationName;

        locationName = sharedPreferences.getString(context.getResources().getString(R.string.locationName),"");
        return locationName;
    }

    public String readLocationId(){
        String locationId;

        locationId = sharedPreferences.getString(context.getResources().getString(R.string.locationId),"");
        return locationId;
    }

    public String readJSONUrl(){
         //String JSON_URL = "http://192.168.43.7:8080/android_buzby/nAjax/";
        //String JSON_URL = "http://rakeshsudhir.in/android_buzby/nAjax/";
        //String JSON_URL = "http://rakeshsudhir.in/android_buzby/nAjax/";
        //String JSON_URL = "http://192.168.1.100:8080/android_buzby/nAjax/";
        String JSON_URL = "http://216.10.240.57/android_buzby/nAjax/";
        //String JSON_URL = "http://216.10.240.57/android_buzby/";
        return JSON_URL;
    }

}
