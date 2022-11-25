package com.rakeshsudhir_app_buzby.buzby;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConvertFunctions {

    public static Date convertIndianDateStringToDate(String dat){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date nsDate=null;
        try {
            if(!dat.equals("")) {
                nsDate = sdf.parse(dat);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return nsDate;
    }

    public static Date convertDatabaseDateStringToDate(String dat){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nsDate=null;
        try {
            if(!dat.equals("")) {
                nsDate = sdf.parse(dat);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return nsDate;
    }

    public static Date convertTextDateStringToDate(String dat){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy");
        Date nsDate=null;
        try {
            if(!dat.equals("")) {
                nsDate = sdf.parse(dat);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return nsDate;
    }


    public static void currentDateCalender(DatePickerDialog picker, Context context, final TextView txtDate){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtDate.setText(String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", year));
                        txtDate.setError(null);
                    }
                }, year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date tempDate=null;
        try{
            if(!txtDate.getText().equals("")) {
                tempDate = sdf.parse(txtDate.getText().toString());
            }

        }catch (ParseException e) {
            e.printStackTrace();
        }

        if(tempDate!=null) {
            picker.updateDate(tempDate.getYear()+1900, tempDate.getMonth(), tempDate.getDate());
        }

        picker.show();
    }





}
