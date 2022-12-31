package com.rakeshsudhir_app_buzby.buzby;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class printString {

    public static void expensePrintString(final String response,int isDuplicate) {

        //final SharedPreferenceConfig preferenceConfig = new SharedPreferenceConfig(mContext);
        String BILL = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonExpName = jsonObject.getJSONArray("data");

            JSONObject objData = jsonExpName.getJSONObject(0);
            if(isDuplicate==1) {
                BILL = BILL + "DUPLICATE\n";
            }
            BILL = BILL +"              " + "GB-" + objData.getString("nsLocationId") + "    \n\n";

            Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));

            BILL = BILL + String.format("%1$-15s %2$15s", new SimpleDateFormat("dd-MM-yyyy").format(d), "ESTIMATE : " + objData.getString("nsVoucherno"));


            BILL = BILL + "\n";

            BILL = BILL
                    + "-------------------------------";
            BILL = BILL + "\n Name : " + objData.getString("nsRemarks") + "    \n";
            BILL = BILL + "\n Amount : " + ((objData.getInt("nsAmount") + objData.getInt("nsOtherAmount"))) + "    \n";
            BILL = BILL + "\n Expense Type : " + objData.getString("nsExpenseHead") + "    \n";


            BILL = BILL + "\n\n\n\n\n\n\n\n\n";
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RakeshPrintString", e.getMessage());
        }
        BluetoothServices bs = new BluetoothServices();
        bs.sendData(BILL);
    }

    public static void dayReportPrintString(final String response) {
        String st = "";


        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonData = jsonObject.getJSONArray("dataOpeningCash");
            JSONArray jsonData1 = jsonObject.getJSONArray("dataCashExpense");
            JSONArray jsonData2 = jsonObject.getJSONArray("dataReceiptCash");
            JSONArray jsonData3 = jsonObject.getJSONArray("dataPaymentCash");
            JSONArray jsonData4 = jsonObject.getJSONArray("dataPurchaseCash");
            JSONArray jsonData5 = jsonObject.getJSONArray("dataSalesCash");

            //if(jsonData.length()>0) {

            JSONObject objData = jsonData.getJSONObject(0);
            JSONObject objData1 = jsonData1.getJSONObject(0);
            JSONObject objData2 = jsonData2.getJSONObject(0);
            JSONObject objData3 = jsonData3.getJSONObject(0);
            JSONObject objData4 = jsonData4.getJSONObject(0);
            JSONObject objData5 = jsonData5.getJSONObject(0);

            st = "              " + "GB-" + objData1.getString("nsLocationId") + "    \n";
            Date df = ConvertFunctions.convertDatabaseDateStringToDate(objData1.getString("nsDateFrom"));
            Date dt = ConvertFunctions.convertDatabaseDateStringToDate(objData1.getString("nsDateTo"));
            st = st + String.format("%1$-15s", new SimpleDateFormat("dd-MM-yyyy").format(df) + " to " + new SimpleDateFormat("dd-MM-yyyy").format(dt)) + "\n\n\n";


            double sales = 0.0;
            double receipt = 0.0;
            double payment = 0.0;
            double expense = 0.0;
            double openingCash = 0.0;
            double purchaseCash = 0.0;


            openingCash = objData.getDouble("nsOpeningCash");
            st = st + String.format("%1$-15s %2$13s", "Opening(Cash) -    ", openingCash) + "\n";

            sales = objData5.getDouble("nsSalesCash");
            st = st + String.format("%1$-17s %2$13s", "Sales(Cash) -      ", sales) + "\n";

            receipt = objData2.getDouble("nsReceiptCash");
            st = st + String.format("%1$-17s %2$13s", "Receipt(Cash) -    ", receipt) + "\n";

            purchaseCash = objData4.getDouble("nsPurchaseCash");
            st = st + String.format("%1$-17s %2$13s", "Purchase(Cash) -   ", purchaseCash) + "\n";

            payment = objData3.getDouble("nsPaymentCash");
            st = st + String.format("%1$-17s %2$13s", "Payment(Cash) -    ", payment) + "\n";

            expense = objData1.getDouble("nsCashExpense");
            st = st + String.format("%1$-17s %2$13s", "Expense(Cash) -    ", expense) + "\n";


            double temp = 0;
            temp = (sales + receipt + openingCash) - (payment + expense + purchaseCash);
            st = st + "\n" + String.format("%1$-17s %2$13s", "Total(Cash) -      ", temp) + "\n\n";

            st = st + "\n\n\n\n\n\n\n\n";
            st = st + "\n\n\n\n\n\n\n\n";


            //}

        } catch (JSONException je) {
            je.getMessage();
            je.printStackTrace();
            Log.d("printStringRakesh123", je.getMessage());
        }
        //DialogUtility.showDialog(CumulativeReportDayWise.this,st,"");
        BluetoothServices bs = new BluetoothServices();
        bs.sendData(st);
    }

    public static void stockReportPrintString(final JSONArray cartPrint, final String nsLocationId, final String nsDate) {


        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String BILL = "";


        BILL = BILL +"              " + "GB-" + nsLocationId + "    \n\n";
        BILL = BILL + String.format("%1$-15s %2$15s", nsDate, currentTime) + "\n\n";


        BILL = BILL + String.format("%1$4s %2$4s %3$4s %4$-16s",
                "CM",
                "CA",
                "OP",
                "Brand") + "\n";
        BILL = BILL
                + "------------------------------";

        int ClosingAtotal = 0;
        int ClosingMtotal = 0;
        int Openingtotal = 0;
        try {
            for (int i = 0; i < cartPrint.length(); i++) {
                JSONObject cartObjData = null;
                cartObjData = cartPrint.getJSONObject(i);


                BILL = BILL + "\n" + String.format("%1$4s %2$4s %3$4s %4$-16s",
                        cartObjData.getString("nsManualClosingStock"),
                        cartObjData.getString("nsPhysicalStock"),
                        cartObjData.getString("nsManualOpeningStock"),
                        cartObjData.getString("nsBrand"));

                ClosingMtotal += cartObjData.getInt("nsManualClosingStock");
                ClosingAtotal += cartObjData.getInt("nsPhysicalStock");
                Openingtotal += cartObjData.getInt("nsManualOpeningStock");
            }
            BILL = BILL
                    + "\n------------------------------";

            BILL = BILL + "\n" + String.format("%1$4s %2$4s %3$4s %4$-16s",
                    ClosingMtotal,
                    ClosingAtotal,
                    Openingtotal,
                    "");
            BILL = BILL
                    + "\n------------------------------\n";

            BILL = BILL + "\n\n\n\n\n\n\n";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //DialogUtility.showDialog(DayWiseTransactionValue.this,BILL,"Bill");
        BluetoothServices bs = new BluetoothServices();
        bs.sendData(BILL);
    }

    public static void receiptPaymentPrintString(String response, Context mContext,int isDuplicate) {
        String BILL = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonData = jsonObject.getJSONArray("data");
            JSONObject objData = jsonData.getJSONObject(0);

            BILL ="";
            if(isDuplicate==1) {
                BILL = BILL + "DUPLICATE\n";
            }
            BILL = BILL + "        " + "GB-" + objData.getString("nsLocationId") + "(" + objData.getString("nsTransactionType").charAt(0) + ")" + "    \n\n";


            BILL = BILL + String.format("%1$-15s %2$15s", objData.getString("nsDate"), "ESTIMATE : " + objData.getString("nsVoucherNo"));


            BILL = BILL + "\n" + objData.getString("nsLedgerName") + "    \n";
            if (!objData.isNull("nsArea") && !objData.getString("nsArea").equals("") && !objData.getString("nsArea").equals("null")) {
                BILL = BILL + objData.getString("nsArea") + " " + objData.getString("nsCity") + " \n";
            } else {
                BILL = BILL + objData.getString("nsCity") + "\n";
            }
            BILL = BILL
                    + "-------------------------------";

            double currentBal = 0.0;
            double paidAmount = 0.0;
            double remBal = 0.0;

            currentBal = objData.getDouble("nsOpeningBalance")
                    + objData.getDouble("nsReceiptPayment")
                    + objData.getDouble("nsInOut");

            paidAmount = objData.getDouble("nsAmount");

            remBal = currentBal + paidAmount;
            SharedPreferenceConfig preferenceConfig = new SharedPreferenceConfig(mContext);


            //Toast.makeText(this, ""+currentBalWhileReprint, Toast.LENGTH_SHORT).show();
            if (objData.getInt("nsIsRestricted") == 2) {
                BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Old Bal : ", "" + currentBal);
            }

            BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Paid : ", String.valueOf(paidAmount));

            BILL = BILL + "\n" + String.format("%1$19s %2$11s", "", "--------");
            if (objData.getInt("nsIsRestricted") == 2) {
                BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Balance : ", String.valueOf(remBal));
            }/*else{
                BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Balance : ", String.valueOf(remBal));
            }*/

            BILL = BILL
                    + "\n-------------------------------";

            BILL = BILL + "\n\n\n\n\n\n\n\n\n";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //DialogUtility.showDialog(MasterReceiptCreation.this,BILL,"");
        BluetoothServices bs = new BluetoothServices();
        bs.sendData(BILL);

    }

    public static void salesPrintString(String response,int isDuplicate,int shortCut,int printBill,int printGP){
        String BILL = "";

        try {

            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonData = jsonObject.getJSONArray("data");

            JSONObject objData = jsonData.getJSONObject(0);
            JSONArray jsonDataItems = jsonObject.getJSONArray("dataItems");

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(objData.getString("nsManulTime"));
            Calendar cal123 = Calendar.getInstance();
            cal123.setTime(date);
            /*cal123.add(Calendar.MINUTE, 750);*/
            DateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String newTime = sdf1.format(cal123.getTime());
            Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));

            if(printBill==1) {
                if (isDuplicate == 1) {
                    BILL = BILL + "DUPLICATE\n";
                }
                BILL = BILL + "              " + "GB-" + objData.getString("nsLocationId") + "    \n\n";


                BILL = BILL + String.format("%1$-15s %2$15s", new SimpleDateFormat("dd-MM-yyyy").format(d), "ESTIMATE : " + objData.getString("nsVoucherNo"));
                BILL = BILL + "\n" + String.format("%1$-15s %2$15s", newTime, "");

                if (!objData.getString("nsLedgerAlias").equals("")) {
                    BILL = BILL + "\n" + objData.getString("nsLedgerAlias") + "";
                }

                BILL = BILL + "\n" + objData.getString("nsLedgerName") + "    \n";
                if (!objData.isNull("nsArea") && !objData.getString("nsArea").equals("") && !objData.getString("nsArea").equals("null")) {
                    BILL = BILL + objData.getString("nsArea") + " " + objData.getString("nsCity") + "    \n";
                } else {
                    BILL = BILL + objData.getString("nsCity") + "\n";
                }
                BILL = BILL
                        + "-------------------------------";


                //Log.d("printStringRakesh123",jsonDataItems.toString(4));
                for (int i = 0; i < jsonDataItems.length(); i++) {
                    JSONObject cartObjData = jsonDataItems.getJSONObject(i);


                    double temp = (cartObjData.getDouble("nsQuantity") * cartObjData.getDouble("nsPrice") * cartObjData.getDouble("nsUnitSize"));
                    BigDecimal bd = new BigDecimal(temp).setScale(2, RoundingMode.HALF_UP);
                    double total_amount = Math.round(bd.doubleValue());


                    BILL = BILL + "\n" + String.format("%1$-2s %2$5s %3$4s %4$7s %5$9s",
                            cartObjData.getString("nsSubCategory"),
                            "" + cartObjData.getDouble("nsPrice"), cartObjData.getString("nsQuantity"),
                            (cartObjData.getString("nsNameDuringPrint")), total_amount);

                }


                BILL = BILL + "\n" + String.format("%1$-2s %2$5s %3$4s %4$7s %5$9s", "", "", "----", "", "---------");


                double subTotal = 0.0;
                subTotal = objData.getDouble("nsTotalAmount") - objData.getDouble("nsAdditionalCharges");
                double additionalCharges = 0.0;

                additionalCharges = objData.getDouble("nsAdditionalCharges");

            /*double sTotal = 0.0;
            sTotal = subTotal - additionalCharges;*/

                BILL = BILL + "\n" + String.format("%1$-2s %2$5s %3$4s %4$7s %5$9s", "", "", objData.getString("nsTotalSalesQuantity"), "", subTotal);
                if (additionalCharges != 0) {
                    BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Add Charges : ", String.valueOf(additionalCharges));
                }

                double oldBalance = 0.0;


                oldBalance = (objData.getDouble("nsInOut") + objData.getDouble("nsOpeningBalance") + objData.getDouble("nsReceiptPayment"));


                if (objData.getInt("nsIsRestricted") == 2) {
                    if (oldBalance != 0) {
                        if(objData.getInt("nsPaymentTypeId")==2){
                            BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Phone Pe : ", String.valueOf(oldBalance));
                        }else {
                            BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Old Bal : ", String.valueOf(oldBalance));
                        }
                    }
                }


                double grandTotal = 0.0;
                grandTotal = subTotal + additionalCharges + oldBalance;


                if (objData.getInt("nsIsRestricted") == 2) {
                    grandTotal = subTotal + additionalCharges + oldBalance;
                    if (oldBalance != 0 || additionalCharges != 0) {

                        BILL = BILL + "\n" + String.format("%1$20s %2$10s", "", "---------");
                        BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Total : ", String.valueOf(grandTotal));
                    }
                } else {
                    grandTotal = subTotal + additionalCharges;
                    if (additionalCharges != 0) {
                        BILL = BILL + "\n" + String.format("%1$20s %2$10s", "", "---------");
                        BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Total : ", String.valueOf(grandTotal));
                    }
                }


                double paidAmount = 0.0;
                paidAmount = objData.getDouble("nsPaidAmount");

                BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Paid : ", paidAmount);

                double balance = 0.0;
                balance = grandTotal - paidAmount;


                if (objData.getInt("nsIsRestricted") == 2) {
                    BILL = BILL + "\n" + String.format("%1$19s %2$11s", "Balance : ", balance);
                }

                if(balance>0){
                    BILL = BILL + "\n\nNote: Request you to clear Balance\n     within 10days of Billing Date";
                }


                BILL = BILL
                        + "\n-------------------------------";


                BILL = BILL + "\n\n\n\n\n\n\n\n\n";

            }


            if(printGP==1) {

                if (isDuplicate == 1) {
                    BILL = BILL + "DUPLICATE\n";
                }
                BILL = BILL + "              " + "GB-" + objData.getString("nsLocationId") + "    \n\n";


                BILL = BILL + String.format("%1$-15s %2$15s", new SimpleDateFormat("dd-MM-yyyy").format(d), "ESTIMATE : " + objData.getString("nsVoucherNo"));


                BILL = BILL + "\n" + String.format("%1$-15s %2$15s", newTime, "");
           /* if(!objData.getString("nsLedgerAlias").equals("")){
                BILL = BILL + "\n" + objData.getString("nsLedgerAlias");
            }else {
                BILL = BILL + "\n" + objData.getString("nsLedgerName") + "    \n";
                if (!objData.isNull("nsArea") && !objData.getString("nsArea").equals("") && !objData.getString("nsArea").equals("null")) {
                    BILL = BILL + objData.getString("nsArea") + " " + objData.getString("nsCity") + "    \n";
                } else {
                    BILL = BILL + objData.getString("nsCity") + "\n";
                }
            }*/

                if (!objData.getString("nsLedgerAlias").equals("")) {
                    BILL = BILL + "\n" + objData.getString("nsLedgerAlias") + "";
                }

                BILL = BILL + "\n" + objData.getString("nsLedgerName") + "    \n";
                if (!objData.isNull("nsArea") && !objData.getString("nsArea").equals("") && !objData.getString("nsArea").equals("null")) {
                    BILL = BILL + objData.getString("nsArea") + " " + objData.getString("nsCity") + "    \n";
                } else {
                    BILL = BILL + objData.getString("nsCity") + "\n";
                }


                BILL = BILL
                        + "-------------------------------";

                for (int i = 0; i < jsonDataItems.length(); i++) {
                    JSONObject cartObjData = jsonDataItems.getJSONObject(i);
                    BILL = BILL + "\n" + String.format("%1$-14s %2$8s %3$5s",
                            cartObjData.getString("nsBrand"),
                            (cartObjData.getString("nsNameDuringPrint")),
                            cartObjData.getString("nsQuantity"));
                }

                BILL = BILL + "\n";
                BILL = BILL + String.format("%1$31s", "-------\n");
                BILL = BILL + String.format("%1$28s", objData.getString("nsTotalSalesQuantity"));
                BILL = BILL + "\n";
                if (objData.getInt("nsTransportOrder") == 1) {
                    BILL = BILL + String.format("%1$19s", "(-: TD :-)");
                }
                BILL = BILL + "\n\n";

                if (objData.getInt("nsTransactionStatus") == 3) {
                    BILL = BILL + String.format("%1$19s", "(-: Delivered :-)");
                }

                BILL = BILL + "\n\n";
                BILL = BILL + String.format("%1$-15s %2$15s", new SimpleDateFormat("dd-MM-yyyy").format(d), "ESTIMATE : " + objData.getString("nsVoucherNo"));
                //BILL = BILL + "\n"+String.format("%1$-15s %2$15s", objData.getString("nsManualTime"), "");

                BILL = BILL
                        + "\n-------------------------------";
                BILL = BILL + "\n\n\n\n\n\n\n";
            }

        }catch (JSONException je){
            je.getMessage();
            je.printStackTrace();
            Log.d("printStringRakesh123", je.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //refresh();
        //DialogUtility.showDialog(Sales.this,BILL,"Bill");
        BluetoothServices bs = new BluetoothServices();
        bs.sendData(BILL);
    }

    public static void DeliveryGatePassPrintString(JSONArray jsonDataItems,String nsName,String nsAlias){
        String BILL = "";

        try {

            if (!nsAlias.equals("")) {
                BILL = BILL + "\n" + nsAlias + "";
            }else{
                BILL = BILL + "\n" + nsName + "    \n";
            }
            BILL = BILL+"--------------";

            int sumQty=0;

            for (int i = 0; i < jsonDataItems.length(); i++) {
                JSONObject cartObjData = jsonDataItems.getJSONObject(i);
                BILL = BILL + "\n" + String.format("%1$-19s %2$8s",
                        cartObjData.getString("nsNameDuringTransaction"),
                        (cartObjData.getString("nsBalance")));

                sumQty =sumQty+cartObjData.getInt("nsBalance");
            }

            BILL = BILL + "\n";
            BILL = BILL + String.format("%1$31s", "-------\n");
            BILL = BILL + String.format("%1$28s", sumQty);


            BILL = BILL + "\n\n";


        }catch (JSONException je){
            je.getMessage();
            je.printStackTrace();
            Log.d("printStringRakesh123", je.getMessage());
        }
        //refresh();
        //DialogUtility.showDialog(Sales.this,BILL,"Bill");
        BluetoothServices bs = new BluetoothServices();
        bs.sendData(BILL);
    }
}
