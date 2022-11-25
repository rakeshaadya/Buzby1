package com.rakeshsudhir_app_buzby.buzby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;


public class ExportDataGeneral {

    public static void exportToCSV(final Activity activity,
                                   final JSONArray myDataList,
                                   final String nsModule){

        StringBuilder data = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            JSONObject object = myDataList.optJSONObject(0);
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String currentKey = iterator.next();
                //params.put(currentKey, object.getString(currentKey));
                data.append(currentKey+",");

            }
            data.append("\n");
        }

        for(int i=0;i<myDataList.length();i++){
            JSONObject object = myDataList.optJSONObject(i);
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String currentKey = iterator.next();

                try {
                    data.append(object.getString(currentKey)+",");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            data.append("\n");
        }

        try{
            FileOutputStream out = activity.openFileOutput(nsModule.replace(" ","_")+".csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            Context context = activity.getApplicationContext();
            File fileLocation = new File(activity.getFilesDir(),nsModule.replace(" ","_")+".csv");


            Uri path = FileProvider.getUriForFile(context,"com.rakeshsudhir_app_pms.buzby.fileprovider",fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/text");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT,nsModule+".csv");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //fileIntent.putExtra(Intent.EXTRA_STREAM,path);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            activity.startActivity(Intent.createChooser(fileIntent,"Share"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void exportDetailedLedgerReportToCSV(final Activity activity,
                                   final JSONArray myDataList,final JSONArray myDataListItem,
                                   final String nsModule){

        StringBuilder data = new StringBuilder();

        data.append(nsModule.replace(" ",",") + ",");
        data.append("\n");
        data.append("\n");
        for (int i = 0; i < 1; i++) {
            JSONObject object = myDataList.optJSONObject(0);
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String currentKey = iterator.next();
                //params.put(currentKey, object.getString(currentKey));
                if(!currentKey.equals("nsTransactionId") && !currentKey.equals("nsId")) {
                    data.append(currentKey + ",");
                }

            }
            data.append("\n");
            data.append("\n");
        }

        /*for(int j=0;j<myDataList.length();j++){*/
            try {

                for (int j = 0; j < myDataList.length(); j++) {
                    JSONObject object = myDataList.optJSONObject(j);
                    Iterator<String> iterator = object.keys();
                    while (iterator.hasNext()) {
                        String currentKey = iterator.next();
                        //params.put(currentKey, object.getString(currentKey));
                        if(!currentKey.equals("nsTransactionId") && !currentKey.equals("nsId")) {
                            data.append(object.getString(currentKey)+",");
                        }

                    }
                    data.append("\n");

                    if(myDataList.optJSONObject(j).getInt("nsTransactionId")==200
                            || myDataList.optJSONObject(j).getInt("nsTransactionId")==300){

                        for (int k = 0; k < myDataListItem.length(); k++) {
                            JSONObject objectK = myDataListItem.optJSONObject(k);
                            if(objectK.getInt("nsId")==object.getInt("nsId")){
                                data.append(",");
                                data.append(",");
                                data.append(",");
                                data.append("\""+objectK.getString("nsNameDuringTransaction")+"\""+",");
                                data.append("\""+objectK.getString("nsQuantity")+"\""+",");
                                data.append("\""+objectK.getString("nsPrice")+"\""+",");
                                data.append("\""+objectK.getString("nsAmt")+"\""+",");
                                data.append("\n");
                            }

                        }


                    }


                    /*if(myDataList.optJSONObject(j).getString("nsTransactionId").equals("200")
                        && myDataList.optJSONObject(j).getString("nsTransactionId").equals("300")){

                        for (int k = 0; k < myDataListItem.length(); k++) {
                            JSONObject objectK = myDataList.optJSONObject(k);
                            if(objectK.getInt("nsId")==object.getInt("nsId")){
                                data.append(""+",");
                                data.append(""+",");
                                data.append(""+",");
                                data.append(objectK.getString("nsNameDuringTransaction")+",");
                                data.append(objectK.getString("nsQuantity")+",");
                                data.append(objectK.getString("nsPrice")+",");
                                data.append(objectK.getString("nsAmt")+",");
                            }
                        }


                    }
                        data.append("\n");*/
                        data.append("\n");

                }

                /*if(myDataList.optJSONObject(j).getString("nsTransactionId").equals("200")
                        && myDataList.optJSONObject(j).getString("nsTransactionId").equals("300")){



                }else{
                    data.append("\n");
                    data.append("\n");
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        //}





        /*for(int i=0;i<myDataList.length();i++){
            JSONObject object = myDataList.optJSONObject(i);
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String currentKey = iterator.next();

                try {
                    data.append(object.getString(currentKey)+",");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            data.append("\n");
        }*/

        try{
            FileOutputStream out = activity.openFileOutput(nsModule.replace(" ","_")+".csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            Context context = activity.getApplicationContext();
            File fileLocation = new File(activity.getFilesDir(),nsModule.replace(" ","_")+".csv");


            Uri path = FileProvider.getUriForFile(context,"com.rakeshsudhir_app_pms.buzby.fileprovider",fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/text");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT,nsModule+".csv");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //fileIntent.putExtra(Intent.EXTRA_STREAM,path);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            activity.startActivity(Intent.createChooser(fileIntent,"Share"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
