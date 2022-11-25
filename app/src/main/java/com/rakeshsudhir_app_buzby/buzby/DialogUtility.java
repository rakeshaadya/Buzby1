package com.rakeshsudhir_app_buzby.buzby;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONArray;

public class DialogUtility extends BaseActivity{

    public static void showDialog(final Activity activity, String response, String dialogTitle)
    {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setTitle(dialogTitle);
        alertbox.setMessage(response);
        /*alertbox.setPositiveButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });*/
        alertbox.setNegativeButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alertbox.show();
    }

    /*public static void showDialogBeforeSave(final Activity activity, String response, String dialogTitle)
    {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setTitle(dialogTitle);
        alertbox.setMessage(response);
        *//*alertbox.setPositiveButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });*//*
        alertbox.setNegativeButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alertbox.show();
    }*/

    public static void dialogForSalesRangeGB(JSONArray jsonArrayData, String nsModule, String nsId,Activity context) {



    }


}
