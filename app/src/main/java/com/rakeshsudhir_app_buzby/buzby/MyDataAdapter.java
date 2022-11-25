    package com.rakeshsudhir_app_buzby.buzby;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.myViewHolder>{
    private Context context;
    private ArrayList<String> myListColumns;
    private JSONArray myList;
    private JSONArray myListFiltered;
    private String contextName;
    private String subModule;
    private myAdapterListener listener;
    private SharedPreferenceConfig preferenceConfig;
    private View view;

    SparseBooleanArray array=new SparseBooleanArray();

    public MyDataAdapter(Context context, ArrayList<String> myListColumns, JSONArray myList, myAdapterListener listener, String contextName,String subModule) {
        this.context = context;
        this.myListColumns = myListColumns;
        this.myList = myList;
        this.myListFiltered = myList;
        this.listener = listener;
        this.contextName=contextName;
        this.subModule=subModule;
        //Toast.makeText(this.context, "1  "+myListFiltered.toString(), Toast.LENGTH_SHORT).show();
        preferenceConfig = new SharedPreferenceConfig(context);



        //array=new SparseBooleanArray();
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        if(contextName.equals("master_location")
                ||contextName.equals("user_position")
                ||contextName.equals("master_users")
                ||contextName.equals("master_area")
                ||contextName.equals("master_city")
                ||contextName.equals("master_expense_head")
                ||contextName.equals("master_brand")
                ||contextName.equals("master_units")
                ||contextName.equals("master_inventory_category")
                ||contextName.equals("master_inventory_subcategory")
                ||contextName.equals("master_modules")
                ||contextName.equals("master_inventory")
                ||contextName.equals("transactions_stock_transfer")
                ||contextName.equals("opening_cash_balance")
                ||contextName.equals("ff_master_state_code")

        ){
            view = layoutInflater.inflate(R.layout.recycler_list_master_one, viewGroup, false);
        }else if(contextName.equals("android_signedin_users")) {
            view = layoutInflater.inflate(R.layout.recycler_list_master_one, viewGroup, false);
        }else if(contextName.equals("master_ledger")) {
            view = layoutInflater.inflate(R.layout.recycler_list_master_ledger, viewGroup, false);
        }else if(contextName.equals("master_user_role_management")) {
            view = layoutInflater.inflate(R.layout.recycler_list_role_management, viewGroup, false);
        }else if(contextName.equals("transaction_openingstock") || contextName.equals("inventory_saftey_stock")) {
            view = layoutInflater.inflate(R.layout.recycler_list_transaction_opening_stock, viewGroup, false);
        }else if(contextName.equals("transaction_stock_management")) {
            view = layoutInflater.inflate(R.layout.recycler_list_transaction_stock_management, viewGroup, false);
        }else if(contextName.equals("transaction_ledger_opening_balance")){
            view = layoutInflater.inflate(R.layout.recycler_list_transcation_ledger_opbal, viewGroup, false);
        }else if(contextName.equals("cart_items")){
            view = layoutInflater.inflate(R.layout.cart_details_list, viewGroup, false);
        }else if(contextName.equals("cart_items_stock_transfer")){
            view = layoutInflater.inflate(R.layout.cart_details_stock_transfer_list, viewGroup, false);
        }else if(contextName.equals("transactions_in_out")){
            view = layoutInflater.inflate(R.layout.sales_recycler_list, viewGroup, false);
        }else if(contextName.equals("transaction_receipt_payment")) {
            view = layoutInflater.inflate(R.layout.receipt_payment_list, viewGroup, false);
        }else if(contextName.equals("cart_customer_list_receipt_payment")) {
            view = layoutInflater.inflate(R.layout.receipt_customer_list_recycler_view, viewGroup, false);
        }else if(contextName.equals("offset_items")) {
            view = layoutInflater.inflate(R.layout.deliver_layout_list, viewGroup, false);
        }else if(contextName.equals("offset_items_delivered")) {
            view = layoutInflater.inflate(R.layout.delivered_layout_list, viewGroup, false);
        }else if(contextName.equals("master_daily_stock_closing")){
            view = layoutInflater.inflate(R.layout.recycler_list_daily_stock, viewGroup, false);
        }else if(contextName.equals("consolidated_inventory_report")){
            view = layoutInflater.inflate(R.layout.consolidated_inventory_report_list, viewGroup, false);
        }else if(contextName.equals("consolidated_online_balance_report")){
            view = layoutInflater.inflate(R.layout.recycler_list_report_consolidated_online_balance, viewGroup, false);
        }else if(contextName.equals("upi_payment_receipt")){
            view = layoutInflater.inflate(R.layout.recycler_list_report_upi_payment_table, viewGroup, false);
        }else if(contextName.equals("report_ledger_transaction")){
            view = layoutInflater.inflate(R.layout.table_ledger_transaction_report, viewGroup, false);
        }else if(contextName.equals("report_consolidated_ledger_balance")){
            view = layoutInflater.inflate(R.layout.table_consolidated_ledger_balance, viewGroup, false);
        }else if(contextName.equals("fetch_inventory_detailed_report")){
            view = layoutInflater.inflate(R.layout.report_inventory_detailed_list, viewGroup, false);
        }else if(contextName.equals("master_hamali_calculation")){
            view = layoutInflater.inflate(R.layout.recycler_list_hamali, viewGroup, false);
        }else if(contextName.equals("master_expense")){
            view = layoutInflater.inflate(R.layout.expense_master_list, viewGroup, false);
        }else if(contextName.equals("transaction_price_variation")){
            view = layoutInflater.inflate(R.layout.recycler_list_price_variation, viewGroup, false);
        }
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {
        //final DataInfo dataInfo = myListFiltered.get(position);
        JSONObject objData = null;
        try {
            objData = myListFiltered.getJSONObject(position);

            //Toast.makeText(context, ""+objData.toString(), Toast.LENGTH_SHORT).show();

            //Toast.makeText(context, ""+myListFiltered.getJSONObject(position), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(context, ""+objData.toString(), Toast.LENGTH_SHORT).show();

        if(contextName.equals("master_location")
                || contextName.equals("user_position")
                || contextName.equals("master_users")
        ) {
            try{
                holder.masterOne_lblName.setText(objData.getString("nsName"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("android_signedin_users")) {
            try{
                holder.masterOne_lblName.setText(objData.getString("nsName")+", "+objData.getString("nsAndroidID")+", "+objData.getString("nsDeviceName"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("transaction_ledger_opening_balance")) {
            try{
                String name = "";
                name+= objData.getString("nsName");
                if(!objData.isNull("nsArea") && !objData.getString("nsArea").equals("")){
                    name += " " + objData.getString("nsArea");
                }
                name+=" "+objData.getString("nsCity");

                holder.ledger_opening_lblName.setText(name);
                holder.ledger_opening_lblID.setText(objData.getString("nsId"));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.ledger_opening_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.ledger_opening_lblOpeningBalance.setText(objData.getString("nsAmount"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("master_modules")) {
            try{
                holder.masterOne_lblName.setText(objData.getString("nsNameAlias"));
                holder.masterOne_listMenu.setVisibility(View.GONE);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("opening_cash_balance")) {
            try{

                if (objData.getInt("nsLocationId") == 1) {
                    holder.card_view.setBackgroundColor(Color.parseColor("#e0f3ff"));
                } else if (objData.getInt("nsLocationId") == 2) {
                    holder.card_view.setBackgroundColor(Color.parseColor("#FAFAD2"));
                } else if (objData.getInt("nsLocationId") == 3) {
                    holder.card_view.setBackgroundColor(Color.parseColor("#fae6f8"));
                }

                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));

                holder.masterOne_lblName.setText(new SimpleDateFormat("yyyy-MM-dd").format(d)+", "+objData.getString("nsLocation")+", "+objData.getString("nsAmount"));
                holder.masterOne_listMenu.setVisibility(View.GONE);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("consolidated_inventory_report")) {
            try{

                if(objData.getInt("nsAccountingStock") <= objData.getInt("nsSafetyStock")){
                    holder.llTableView.setBackgroundColor(Color.parseColor("#FFCDD2"));
                }

                //Toast.makeText(context, ""+objData.toString(4), Toast.LENGTH_SHORT).show();

                holder.consolidated_inventory_report_lblSno.setText(""+(position+1));
                holder.consolidated_inventory_report_lblName.setText(objData.getString("nsBrand"));
                holder.consolidated_inventory_report_lblOpeningStock.setText(objData.getString("nsManualOpeningStock"));
                if(objData.getInt("nsSalesOrderStock")!=0) {
                    holder.consolidated_inventory_report_lblAccountingStock.setText(objData.getString("nsAccountingStock") + " (" + objData.getString("nsSalesOrderStock") + ")");
                }else{
                    holder.consolidated_inventory_report_lblAccountingStock.setText(objData.getString("nsAccountingStock"));
                }
                holder.consolidated_inventory_report_lblPhysicalStock.setText(objData.getString("nsPhysicalStock"));
                holder.consolidated_inventory_report_lblClosingManual.setText(objData.getString("nsManualClosingStock"));

                if(objData.getInt("nsDifference")>0){
                    holder.consolidated_inventory_report_lblClosingDiffrence.setBackgroundColor(Color.parseColor("#1E88E5"));
                    holder.consolidated_inventory_report_lblClosingDiffrence.setTextColor(Color.parseColor("#FFFFFF"));
                }else if(objData.getInt("nsDifference")<0){
                    holder.consolidated_inventory_report_lblClosingDiffrence.setBackgroundColor(Color.parseColor("#F44336"));
                    holder.consolidated_inventory_report_lblClosingDiffrence.setTextColor(Color.parseColor("#FFFFFF"));
                }

                if((objData.getInt("nsManualOpeningStock")-objData.getInt("nsPhysicalStock"))>0){
                    holder.consolidated_inventory_report_lblOpeningStock.setBackgroundColor(Color.parseColor("#FFD600"));
                    holder.consolidated_inventory_report_lblOpeningStock.setTextColor(Color.parseColor("#FFFFFF"));
                }else if((objData.getInt("nsManualOpeningStock")-objData.getInt("nsPhysicalStock"))<0){
                    holder.consolidated_inventory_report_lblOpeningStock.setBackgroundColor(Color.parseColor("#FF6D00"));
                    holder.consolidated_inventory_report_lblOpeningStock.setTextColor(Color.parseColor("#FFFFFF"));
                }

                holder.consolidated_inventory_report_lblClosingDiffrence.setText(objData.getString("nsDifference"));

                if(objData.getInt("nsDifference")!=0) {
                    final JSONObject finalObjData1 = objData;
                    holder.consolidated_inventory_report_lblClosingDiffrence.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, StockManagementActivity.class);
                            try {
                                i.putExtra("nsQuantity", finalObjData1.getString("nsDifference"));
                                i.putExtra("nsInventoryId", finalObjData1.getString("nsId"));
                                i.putExtra("nsInventory", finalObjData1.getString("nsBrand"));
                                i.putExtra("nsModule", "transaction_stock_management");
                                i.putExtra("nsId", "-1");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            context.startActivity(i);
                        }
                    });
                }

                final JSONObject finalObjData3 = objData;
                holder.consolidated_inventory_report_lblOpeningStock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ClosingStockActivity.class);
                        try {
                            i.putExtra("nsQuantity", ""+(finalObjData3.getInt("nsPhysicalStock")-finalObjData3.getInt("nsManualOpeningStock")));
                            i.putExtra("nsInventoryId", finalObjData3.getString("nsId"));
                            i.putExtra("nsInventory", finalObjData3.getString("nsBrand"));
                            i.putExtra("nsModule", "master_daily_stock_closing");
                            i.putExtra("nsSubModule", "returnToStockReport");
                            i.putExtra("strArrayStockType", "0");
                            i.putExtra("nsId", "-1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        context.startActivity(i);
                    }
                });

                holder.consolidated_inventory_report_lblClosingManual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ClosingStockActivity.class);
                        try {
                            int diff = (0-finalObjData3.getInt("nsDifference"));
                            i.putExtra("nsQuantity", ""+diff);
                            i.putExtra("nsInventoryId", finalObjData3.getString("nsId"));
                            i.putExtra("nsInventory", finalObjData3.getString("nsBrand"));
                            i.putExtra("nsModule", "master_daily_stock_closing");
                            i.putExtra("nsSubModule", "returnToStockReport");
                            i.putExtra("strArrayStockType", "1");
                            i.putExtra("nsId", "-1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        context.startActivity(i);
                    }
                });

            }catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(contextName.equals("cart_items")) {
            try{
                double price = 0.0;
                if(!objData.getString("nsInventoryPrice").equals("") || !objData.isNull("nsInventoryPrice")) {
                    price = objData.getDouble("nsInventoryPrice");
                }
                double quantity = 0.0;
                quantity = Double.parseDouble(objData.getString("nsInventoryQuantity"));
                double units = 0.0;
                units = Double.parseDouble(objData.getString("nsUnitSize"));

                holder.cart_lblSno.setText(String.valueOf(position+1));
                holder.cart_lblInvName.setText(objData.getString("nsInventoryName"));
                holder.cart_lblPrice.setText(objData.getString("nsInventoryPrice"));
                holder.cart_lblQty.setText(objData.getString("nsInventoryQuantity"));
                double temp1 = price*quantity*units;
                BigDecimal bd = new BigDecimal(temp1).setScale(2, RoundingMode.HALF_UP);
                double total_amount = bd.doubleValue();
                holder.cart_lblAmount.setText(String.valueOf(Math.round(total_amount)));

                if(subModule.equals("cart_item_purchase")){
                    //Toast.makeText(context, objData.getString("nsIsRestricted")+" "+subModule, Toast.LENGTH_SHORT).show();
                    if(objData.getInt("nsIsRestricted")==1) {

                        holder.cart_lblAmount.setVisibility(View.INVISIBLE);
                        holder.cart_lblPrice.setVisibility(View.INVISIBLE);
                    }else{
                        holder.cart_lblAmount.setVisibility(View.VISIBLE);
                        holder.cart_lblPrice.setVisibility(View.VISIBLE);
                    }

                }


            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(contextName.equals("cart_items_stock_transfer")) {
            try{
                holder.cart_stocktransfer_lblSno.setText(String.valueOf(position+1));
                holder.cart_stocktransfer_lblInvName.setText(objData.getString("nsInventoryName"));
                holder.cart_stocktransfer_lblQty.setText(objData.getString("nsQuantity"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("transaction_price_variation")) {
            try{
                holder.pvlblSno.setText(String.valueOf(position+1));
                holder.pvlblDate.setText(objData.getString("nsDate"));
                holder.pvlblName.setText(objData.getString("nsVoucherNumber")+", "+objData.getString("nsName"));
                holder.pvlblInventory.setText(objData.getString("nsInventory")+"\n("+objData.getDouble("nsBaseAmount")+")");
                holder.pvlblPrice.setText(""+objData.getDouble("nsPriceAmount"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("master_hamali_calculation")) {
            try{
                holder.hamali_lblSno.setText(String.valueOf(position+1));
                holder.hamali_lblName.setText(objData.getString("nsName")+" - "+objData.getString("nsPartyName"));
                holder.hamali_lblQty.setText(objData.getString("nsQuantity"));
                double total = objData.getDouble("nsQuantity")*objData.getDouble("nsHamliPrice");
                BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                holder.hamali_lblAmount.setText(""+Math.round(bd.doubleValue()));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("transaction_openingstock")) {
            try{
                holder.openingstock_lblInventory.setText(objData.getString("nsName"));
                holder.openingstock_lblLocation.setText(objData.getString("nsLocation"));
                holder.openingstock_lblOpeningStock.setText(objData.getString("nsOpeningQuantity"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("inventory_saftey_stock")) {
            try{
                holder.openingstock_lblInventory.setText(objData.getString("nsName"));
                holder.openingstock_lblLocation.setText(objData.getString("nsLocation"));
                holder.openingstock_lblOpeningStock.setText(objData.getString("nsQuantity"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("transaction_stock_management")) {
            try{
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.stockmanagement_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.stockmanagement_lblInventory.setText(objData.getString("nsName"));
                holder.stockmanagement_lblLocation.setText(objData.getString("nsLocation"));
                holder.stockmanagement_lblOpeningStock.setText(objData.getString("nsQuantity"));
                holder.stockmanagement_lblSno.setText(""+(position+1));
                holder.stockmanagement_lblVoucher.setText(objData.getString("nsId"));

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("master_inventory")){
            try{
                holder.masterOne_lblName.setText(objData.getString("nsNameDuringTransaction"));
                if(objData.getInt("nsIsActive")==2) {
                    holder.masterOne_lblName.setAlpha(0.5f);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("transactions_stock_transfer")){
            try{
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.masterOne_lblName.setText(new SimpleDateFormat("dd-MM-yyyy").format(d)+"-> "+objData.getString("nsFromLocation")+" to "+objData.getString("nsToLocation"));
                holder.masterOne_listMenu.setVisibility(View.GONE);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("cart_customer_list_receipt_payment")){
            try{
                holder.receipt_payment_list_checkboxReceiptPayment.setVisibility(View.VISIBLE);
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.receipt_payment_list_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.receipt_payment_list_lblType.setText(objData.getString("nsTransactionType"));
                holder.receipt_payment_list_lblDebit.setText(objData.getString("nsDebit"));
                holder.receipt_payment_list_lblCredit.setText(objData.getString("nsCredit"));
                holder.receipt_payment_list_lblRunningTotal.setText(objData.getString("nsRunningTotal"));

                if(array.get(position)){
                    holder.receipt_payment_list_checkboxReceiptPayment.setChecked(true);
                }else{
                    holder.receipt_payment_list_checkboxReceiptPayment.setChecked(false);
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("transactions_in_out")){
            try{
                if (objData.getInt("nsLocationId") == 1) {
                    holder.newSales_card_view_new.setBackgroundColor(Color.parseColor("#e0f3ff"));
                } else if (objData.getInt("nsLocationId") == 2) {
                    holder.newSales_card_view_new.setBackgroundColor(Color.parseColor("#FAFAD2"));
                } else if (objData.getInt("nsLocationId") == 3) {
                    holder.newSales_card_view_new.setBackgroundColor(Color.parseColor("#fae6f8"));
                }

                if((objData.getInt("nsTotalAmount")-objData.getInt("nsPaidAmount"))==0){
                    holder.newSales_lblAmount.setTextColor(Color.parseColor("#2E7D32"));
                }else{
                    holder.newSales_lblAmount.setTextColor(Color.parseColor("#C62828"));
                }

                if(objData.getInt("nsIsCC")==1){
                    holder.newSales_lblName.setTextColor(Color.parseColor("#C62828"));
                }else{
                    holder.newSales_lblName.setTextColor(Color.parseColor("#2E7D32"));
                }

                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));


                if(objData.getInt("nsTransactionStatus")==3){
                    holder.newSales_lblVoucherNumber.setText(" E.No : "+objData.getString("nsVoucherNo")+"\t\t✔");
                    holder.newSales_lblVoucherNumber.setTextColor(Color.parseColor("#1A237E"));
                }else if(objData.getInt("nsTransactionStatus")==2){
                    holder.newSales_lblVoucherNumber.setText(" E.No : "+objData.getString("nsVoucherNo"));
                    holder.newSales_lblVoucherNumber.setTextColor(Color.parseColor("#E6696C"));
                }else{
                    holder.newSales_lblVoucherNumber.setText(" E.No : "+objData.getString("nsVoucherNo"));
                    holder.newSales_lblVoucherNumber.setTextColor(Color.parseColor("#000000"));
                }

                if(subModule.equals("transaction_purchase")){
                    if(!preferenceConfig.readUserPosition().equals("1")) {
                        if (objData.getInt("nsIsRestricted") == 1) {
                            holder.newSales_lblAmount.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                if(objData.getInt("nsVerifiedStatus")==1){
                    holder.newSales_lblVoucherNumber.setText(" E.No : "+objData.getString("nsVoucherNo")+"\t\t✔");
                    holder.newSales_lblVoucherNumber.setTextColor(Color.parseColor("#1A237E"));
                }

                holder.newSales_lblDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(d));
                if(objData.getInt("nsTransactionTypeId")==300){
                    holder.newSales_lblAmount.setText("₹"+objData.getString("nsPaidAmount"));
                }else{
                    holder.newSales_lblAmount.setText("₹"+objData.getString("nsTotalAmount"));
                }


                String tempName = " ";
                tempName+=objData.getString("nsLedgerName");
                if(!objData.getString("nsLedgerAlias").equals("")) {
                    tempName+=" ("+objData.getString("nsLedgerAlias")+")";
                }
                tempName+="\n";
                if(!objData.isNull("nsArea") && !objData.getString("nsArea").equals("")) {
                    tempName+=" "+objData.getString("nsArea")+",";
                }

                tempName+=" "+objData.getString("nsCity");


                holder.newSales_lblName.setText(tempName);

                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(objData.getString("nsManulTime"));
                Calendar cal123 = Calendar.getInstance();
                cal123.setTime(date);
                /*cal123.add(Calendar.MINUTE, 750);*/
                DateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String newTime = sdf1.format(cal123.getTime());

                String state="";

                if(objData.getInt("nsTransactionStatus")==3){
                    state="Delivered";
                }else if(objData.getInt("nsTransactionStatus")==2){
                    state="Partial";
                }else{
                    state="Undelivered";
                }

                if(objData.getInt("nsTransactionTypeId")!=300) {
                    holder.newSales_lblTime.setText("[" + state + "]\n" + objData.getString("nsUser") + ", " + newTime);
                }else{
                    holder.newSales_lblTime.setText("");
                }

                if(subModule.equals("transaction_sales_purchase_reminders")){
                    if(objData.getInt("nsTransactionTypeId")==300) {
                        holder.newSales_lblTime.setText("PURCHASE");
                        holder.newSales_lblTime.setTextColor(Color.parseColor("#311B92"));
                        holder.newSales_lblTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                    }else if(objData.getInt("nsTransactionTypeId")==200) {
                        holder.newSales_lblTime.setText("SALES");
                        holder.newSales_lblTime.setTextColor(Color.parseColor("#1B5E20"));
                        holder.newSales_lblTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                    }
                }

            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "1"+e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Toast.makeText(context, "2"+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        else if(contextName.equals("master_daily_stock_closing")) {
            //Toast.makeText(context, ""+objData, Toast.LENGTH_SHORT).show();
            try{

                if(objData.getInt("nsTypeId")==1){
                    holder.daily_stock_lblType.setText("Opening");
                    holder.daily_stock_card_view.setBackgroundColor(Color.parseColor("#9CCC65"));
                }else if(objData.getInt("nsTypeId")==2){
                    holder.daily_stock_lblType.setText("Closing");
                    holder.daily_stock_card_view.setBackgroundColor(Color.parseColor("#FFCDD2"));
                }

                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));

                holder.daily_stock_lblDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(d));
                holder.daily_stock_lblStack.setText("Stack : "+objData.getString("nsStack"));
                holder.daily_stock_lblInventory.setText(objData.getString("nsBrandName"));
                holder.daily_stock_lblQuantity.setText("Qty : "+objData.getString("nsQuantity"));
                holder.daily_stock_lblLocation.setText(objData.getString("nsLocationName"));
                holder.daily_stock_lblUser.setText(objData.getString("nsName"));

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(contextName.equals("report_ledger_transaction")) {
            //Toast.makeText(context, ""+objData, Toast.LENGTH_SHORT).show();
            try{

                String receiptType= "";
                if(!objData.getString("nsReceiptType").equals("")) {
                    if (objData.getInt("nsReceiptType") == 0) {
                        receiptType = "";
                    } else if (objData.getInt("nsReceiptType") == 1) {
                        receiptType = "\n(Cash)";
                    } else if (objData.getInt("nsReceiptType") == 2) {
                        receiptType = "\n(PhonePe)";
                    } else if (objData.getInt("nsReceiptType") == 3) {
                        receiptType = "\n(Cheque)";
                    } else if (objData.getInt("nsReceiptType") == 4) {
                        receiptType = "\n(Bank)";
                    }else if (objData.getInt("nsReceiptType") == 5) {
                        receiptType = "\n(Cash)";
                    }else if (objData.getInt("nsReceiptType") == 6) {
                        receiptType = "\n(Credit)";
                    }
                }

                if(objData.getString("nsDate").equals("")) {
                    holder.ledger_transaction_reports_lblDate.setText("");

                }else {
                    Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                    holder.ledger_transaction_reports_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                }

                holder.ledger_transaction_reports_temp111.setBackgroundColor(Color.parseColor("#ECEFF1"));

                String nsTransactionType = "";

                /*if(objData.getInt("nsTransactionId")==200){
                    nsTransactionType = "<font color='#FFD600'>"+objData.getString("nsTransactionType")+"</font>";
                }else if(objData.getInt("nsTransactionId")==400){
                    nsTransactionType = "<font color='#33691E'>"+objData.getString("nsTransactionType")+"</font>";
                }else{
                    nsTransactionType = "<font color='#33691E'>"+objData.getString("nsTransactionType")+"</font>";
                }*/

                if(objData.getInt("nsTransactionId")==200){
                    holder.ledger_transaction_reports_lblType.setTextColor(Color.parseColor("#1A237E"));
                }else if(objData.getInt("nsTransactionId")==400){
                    holder.ledger_transaction_reports_lblType.setTextColor(Color.parseColor("#1B5E20"));
                }/*else{
                    holder.ledger_transaction_reports_lblType.setTextColor(Color.parseColor("#000000"));
                }*/

                holder.ledger_transaction_reports_lblLocation.setText(objData.getString("nsLocationName"));
                holder.ledger_transaction_reports_lblType.setText(objData.getString("nsTransactionType")+receiptType);
                if(!objData.isNull("nsDebit") || !objData.getString("nsDebit").equals("")) {
                    holder.ledger_transaction_reports_lblDebit.setText(objData.getString("nsDebit"));
                }else{
                    holder.ledger_transaction_reports_lblDebit.setText("");
                }

                if(!objData.isNull("nsCredit") || !objData.getString("nsCredit").equals("")) {
                    holder.ledger_transaction_reports_lblCredit.setText(objData.getString("nsCredit"));
                }else{
                    holder.ledger_transaction_reports_lblCredit.setText("");
                }


                holder.ledger_transaction_reports_lblBalance.setText(""+objData.getDouble("nsRunningTotal"));
                holder.ledger_transaction_reports_lblBalance.setTextColor(Color.parseColor("#DD2600"));

            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        else if(contextName.equals("report_consolidated_ledger_balance")) {
            //Toast.makeText(context, ""+objData, Toast.LENGTH_SHORT).show();
            try{

                Calendar cal=Calendar.getInstance();
                int day1 = cal.get(Calendar.DAY_OF_MONTH);
                int month1 = cal.get(Calendar.MONTH);
                int year1 = cal.get(Calendar.YEAR);

                String cDate = String.format("%02d", year1) + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", day1);

                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = originalFormat.parse(objData.getString("nsLastSalesDate"));
                Date d = originalFormat.parse(cDate);
                long diff = d.getTime() - date.getTime();



                holder.consolidated_ledger_balance_reports_lblSno.setText(""+(position+1));
                holder.consolidated_ledger_balance_reports_lblID.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
                holder.consolidated_ledger_balance_reports_lblName.setText(objData.getString("nsName"));
                if(!objData.isNull("nsArea")){
                    holder.consolidated_ledger_balance_reports_lblArea.setText(objData.getString("nsArea"));
                }else{
                    holder.consolidated_ledger_balance_reports_lblArea.setText("");
                }
                holder.consolidated_ledger_balance_reports_lblCity.setText(objData.getString("nsCity"));
                holder.consolidated_ledger_balance_reports_lblBalance.setText(""+objData.getDouble("nsBalance"));

            }catch (JSONException e) {
                e.printStackTrace();
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }

        else if(contextName.equals("offset_items")){
            try{
                holder.deliveryItems_lblSno.setText(String.valueOf(position+1));
                holder.deliveryItems_lblProduct.setText(objData.getString("nsNameDuringTransaction"));
                holder.deliveryItems_txtQty.setText(objData.getString("nsOriginValue"));
                holder.deliveryItems_txtDelivered.setText(objData.getString("nsBalance"));
                holder.deliveryItems_lblPending.setText(objData.getString("nsBalance"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(contextName.equals("offset_items_delivered")){
            //Toast.makeText(context, ""+contextName, Toast.LENGTH_SHORT).show();
            try{

                if(objData.getInt("nsSlot")==1){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#EF9A9A"));
                }else if(objData.getInt("nsSlot")==2){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#CE93D8"));
                }else if(objData.getInt("nsSlot")==3){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#90CAF9"));
                }else if(objData.getInt("nsSlot")==4){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#80DEEA"));
                }else if(objData.getInt("nsSlot")==5){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#9575CD"));
                }else if(objData.getInt("nsSlot")==6){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#81C784"));
                }else if(objData.getInt("nsSlot")==7){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#BCAAA4"));
                }else if(objData.getInt("nsSlot")==8){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#BDBDBD"));
                }else if(objData.getInt("nsSlot")==9){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#EF9A9A"));
                }else if(objData.getInt("nsSlot")==10){
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#EF9A9A"));
                }else{
                    holder.deliveredItems_lblLocation.setBackgroundColor(Color.parseColor("#EF9A9A"));
                }


                holder.deliveredItems_lblSno.setText(String.valueOf(position+1));
                //Toast.makeText(context, ""+objData.getString("nsInventoryName"), Toast.LENGTH_SHORT).show();
                holder.deliveredItems_lblProduct.setText(objData.getString("nsInventoryName")+" ("+objData.getString("nsSlot")+")");
                holder.deliveredItems_txtQty.setText(objData.getString("nsDeliveredQty"));
                holder.deliveredItems_lblLocation.setText(objData.getString("nsDeliveredLocation"));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDeliveryDate"));
                holder.deliveredItems_lblDeliveredDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));

                final JSONObject finalObjData2 = objData;
                holder.deliveredItems_lblDeliveredDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            //Toast.makeText(context, ""+ finalObjData2.getString("nsRemarks"), Toast.LENGTH_SHORT).show();
                            DialogUtility.showDialog((Activity) context,finalObjData2.getString("nsRemarks"),"Remarks");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }catch (JSONException e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else if(contextName.equals("transaction_receipt_payment")){
            try{
                if (objData.getInt("nsLocationId") == 1) {
                    holder.newSales_card_view_new.setBackgroundColor(Color.parseColor("#e0f3ff"));
                } else if (objData.getInt("nsLocationId") == 2) {
                    holder.newSales_card_view_new.setBackgroundColor(Color.parseColor("#FAFAD2"));
                } else if (objData.getInt("nsLocationId") == 3) {
                    holder.newSales_card_view_new.setBackgroundColor(Color.parseColor("#fae6f8"));
                }

                String tempName = " ";
                tempName+=objData.getString("nsLedgerName");
                //tempName+="\n";
                if(!objData.isNull("nsArea") && !objData.getString("nsArea").equals("")) {
                    tempName+=" "+objData.getString("nsArea")+",";
                }

                tempName+=" "+objData.getString("nsCity");


                holder.receipt_payment_lblCustomerName.setText(tempName);
                holder.receipt_payment_lblVoucherNo.setText(objData.getString("nsVoucherNo"));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.receipt_payment_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.receipt_payment_lblPaymentType.setText(objData.getString("nsReceiptName"));
                if(objData.getInt("nsTransactionId")==500){
                    holder.receipt_payment_lblTransactionType.setText("PAYMENT");
                    holder.receipt_payment_lblAmount.setText("₹"+objData.getString("nsAmount"));
                    holder.receipt_payment_lblTransactionType.setBackgroundColor(Color.parseColor("#E6696C"));
                }else{
                    holder.receipt_payment_lblTransactionType.setText("RECEIPT");
                    holder.receipt_payment_lblAmount.setText("₹"+(0-objData.getInt("nsAmount")));
                    holder.receipt_payment_lblTransactionType.setBackgroundColor(Color.parseColor("#4CAF50"));
                }



            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(contextName.equals("master_expense")){
            try{




                if (objData.getInt("nsLocationId") == 1) {
                    holder.new_expense_card_view_new.setBackgroundColor(Color.parseColor("#e0f3ff"));
                } else if (objData.getInt("nsLocationId") == 2) {
                    holder.new_expense_card_view_new.setBackgroundColor(Color.parseColor("#FAFAD2"));
                } else if (objData.getInt("nsLocationId") == 3) {
                    holder.new_expense_card_view_new.setBackgroundColor(Color.parseColor("#fae6f8"));
                }

                String tempName = "";
                tempName+=objData.getString("nsName");
                //tempName+="\n";
                /*if(!objData.isNull("nsArea") && !objData.getString("nsArea").equals("")) {
                    tempName+=" "+objData.getString("nsArea")+",";
                }

                tempName+=" "+objData.getString("nsCity");*/


                holder.expense_lblCustomerName.setText(tempName);
                holder.expense_lblVoucherNo.setText(objData.getString("nsVoucherno"));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.expense_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.expense_lblExpenseType.setText(objData.getString("nsExpenseHead"));
                holder.expense_lblAmount.setText("₹"+(objData.getInt("nsAmount")+objData.getInt("nsOtherAmount")));
                holder.expense_lblRemarks.setText(""+objData.getString("nsRemarks"));




            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(contextName.equals("master_user_role_management")){
            try{
                holder.role_lblModuleName.setText(objData.getString("nsModuleAlias"));

                if(objData.getInt("nsAccessModule")==1 ){
                    holder.role_lblAccessModule.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsAdd")==1 ){
                    holder.role_lblAccessAdd.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsEditAll")==1 ){
                    holder.role_lblAccessEditAll.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsEditSelf")==1 ){
                    holder.role_lblAccessEditSelf.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsDeleteAll")==1 ){
                    holder.role_lblAccessDeleteAll.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsDeleteSelf")==1 ){
                    holder.role_lblAccessDeleteSelf.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsViewAll")==1 ){
                    holder.role_lblAccessViewAll.setTextColor(Color.parseColor("#388E3C"));
                }
                if(objData.getInt("nsViewSelf")==1 ){
                    holder.role_lblAccessViewSelf.setTextColor(Color.parseColor("#388E3C"));
                }


            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(contextName.equals("consolidated_online_balance_report")){
            try{
                holder.consolidated_online_balance_reports_lblSno.setText(""+(position+1));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.consolidated_online_balance_reports_billDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.consolidated_online_balance_reports_lblLocation.setText(objData.getString("nsLocation"));
                holder.consolidated_online_balance_reports_lblName.setText(objData.getString("nsLedgerName")+"\t"+objData.getString("nsCity")+"\n"+objData.getString("nsPaymentType"));
                holder.consolidated_online_balance_reports_lblAmount.setText(objData.getString("nsAmount"));
                //holder.consolidated_online_balance_reports_transactionId.setText(objData.getString("nsInOutId"));
            }catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else if(contextName.equals("fetch_inventory_detailed_report")){
            try{
                holder.inv_detailed_lblSno.setText(""+(position+1));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.inv_detailed_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));

                if(objData.getInt("nsTransactionTypeId")==101){
                    holder.inv_detailed_lblNameType.setText(objData.getString("nsTransactionType"));
                }else if(objData.getInt("nsTransactionTypeId")==1000){
                    holder.inv_detailed_lblNameType.setText(objData.getString("nsTransactionType") + "\n\n"
                           +", " + objData.getString("nsLedgerName") + "\n" + objData.getString("nsLocation"));
                }else{
                    holder.inv_detailed_lblNameType.setText(objData.getString("nsTransactionType") + "\n\n"
                            + objData.getString("nsVoucherNumber")
                           +", " + objData.getString("nsLedgerName") + "\n" + objData.getString("nsLocation"));
                }

                holder.inv_detailed_lblQty.setText(objData.getString("nsQuantity"));
                holder.inv_detailed_lblClosingBal.setText(objData.getString("nsAccountingStockRunningTotal"));
                holder.inv_detailed_lblPhysicalStock.setText(objData.getString("nsPhysicalStockRunningTotal"));

            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
        else if(contextName.equals("upi_payment_receipt")){
            try{
                holder.upi_payment_receipt_lblSno.setText(""+(position+1));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.upi_payment_receipt_billDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.upi_payment_receipt_lblAmount.setText(objData.getString("nsAmount"));
                holder.upi_payment_receipt_lblRemarks.setText(objData.getString("nsRemarks"));
                holder.upi_payment_receipt_lblGrandAmount.setText(objData.getString("nsRunningTotal"));

                if(objData.getDouble("nsAmount")>0){
                    holder.upi_payment_receipt_lblAmount.setBackgroundColor(Color.parseColor("#DCEDC8"));
                }else if(objData.getDouble("nsAmount")<0){
                    holder.upi_payment_receipt_lblAmount.setBackgroundColor(Color.parseColor("#FFCCBC"));
                }

                //holder.consolidated_online_balance_reports_transactionId.setText(objData.getString("nsInOutId"));
            }catch (JSONException e) {
                e.printStackTrace();
            }


            /*try{
                holder.upi_payment_receipt_id.setText(objData.getString("nsId"));
                Date d = ConvertFunctions.convertDatabaseDateStringToDate(objData.getString("nsDate"));
                holder.upi_payment_receipt_lblDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(d));
                holder.upi_payment_receipt_lblAmount.setText(objData.getString("nsAmount"));
                holder.upi_payment_receipt_lblTransactionType.setText(objData.getString("nsUPIName"));

                if(objData.getDouble("nsAmount")>0){
                    holder.upi_payment_receipt_lblTransactionType.setBackgroundColor(Color.parseColor("#4CAF50"));
                }else if(objData.getDouble("nsAmount")<0){
                    holder.upi_payment_receipt_lblTransactionType.setBackgroundColor(Color.parseColor("#E6696C"));
                }

                //holder.consolidated_online_balance_reports_transactionId.setText(objData.getString("nsInOutId"));
            }catch (JSONException e) {
                e.printStackTrace();
            }*/


        }


        else if(contextName.equals("master_ledger")) {
            try{

                holder.bind(objData);

                if(objData.getInt("nsLedgerCategoryId")==2){
                    holder.ledger_lblName.setTextColor(Color.parseColor("#FF1565C0"));
                }
                String area="",city="";
                if(objData.isNull("nsArea")){
                    area="";
                }else{
                    area=objData.getString("nsArea");
                }

                if(objData.isNull("nsCity")){
                    city="";
                }else{
                    city=objData.getString("nsCity");
                }

                holder.ledger_lblName.setText(objData.getString("nsName"));
                holder.ledger_lblArea.setText(area);
                holder.ledger_lblCity.setText(city);

                if(objData.isNull("nsPhone") || objData.getString("nsPhone").equals("")){
                    holder.ledger_layoutPhone.setVisibility(View.INVISIBLE);
                }else {
                    holder.ledger_lblPhoneNumber.setText(objData.getString("nsPhone"));
                }


                    if (objData.getDouble("nsBalance") < 0) {
                        holder.ledger_lblOpeningBalance.setTextColor(Color.parseColor("#1B5E20"));
                    } else {
                        holder.ledger_lblOpeningBalance.setTextColor(Color.parseColor("#C62828"));
                    }


                if(objData.getDouble("nsIsCreditCustomer")==2){
                    holder.ledger_lblName.setTextColor(Color.parseColor("#1B5E20"));
                }else{
                    holder.ledger_lblName.setTextColor(Color.parseColor("#C62828"));
                }

                holder.ledger_lblOpeningBalance.setText("\u20B9 " + objData.getString("nsBalance") + "/-");

                if(Integer.parseInt(preferenceConfig.readUserPosition())>1) {
                    if (objData.getInt("nsIsRestricted") == 2) {
                        holder.ledger_lblOpeningBalance.setVisibility(View.VISIBLE);
                    } else {
                        holder.ledger_lblOpeningBalance.setVisibility(View.INVISIBLE);
                    }
                }


                if(objData.getInt("nsSalesOrder")>0){
                    holder.ledger_imgTelephone.setVisibility(View.VISIBLE);
                }else {
                    holder.ledger_imgTelephone.setVisibility(View.GONE);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(contextName.equals("master_area")
                || contextName.equals("master_city")
                ||contextName.equals("master_brand")
                ||contextName.equals("master_expense_head")
                ||contextName.equals("master_units")
                ||contextName.equals("ff_master_state_code")
                ||contextName.equals("master_inventory_category")
                ||contextName.equals("master_inventory_subcategory")){
            try{
                holder.masterOne_listMenu.setVisibility(View.VISIBLE);
                if(contextName.equals("master_inventory_subcategory")){
                    holder.masterOne_lblName.setText(objData.getString("nsName")+", "+objData.getString("nsInventoryCategory"));
                }else {
                    holder.masterOne_lblName.setText(objData.getString("nsName"));
                }

                final JSONObject finalObjData = objData;

                holder.masterOne_listMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(context, holder.masterOne_listMenu);

                        try {
                            Field[] fields = popupMenu.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                if ("mPopup".equals(field.getName())) {
                                    field.setAccessible(true);
                                    Object menuPopupHelper = field.get(popupMenu);
                                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                    setForceIcons.invoke(menuPopupHelper, true);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        popupMenu.inflate(R.menu.option_menu_for_list);

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()){
                                    case R.id.menu_option_edit:

                                        if(contextName.equals("master_area") || contextName.equals("master_city")
                                                || contextName.equals("master_inventory_category")
                                                || contextName.equals("user_position")
                                                || contextName.equals("master_units")
                                                || contextName.equals("master_expense_head")
                                                || contextName.equals("master_brand")
                                                || contextName.equals("master_inventory")){

                                            Intent iedit = new Intent(context, MasterOneActivity.class);
                                            iedit.putExtra("nsModule", contextName);
                                            try {
                                                iedit.putExtra("nsId", finalObjData.getString("nsId"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            context.startActivity(iedit);

                                        }
                                        else if(contextName.equals("master_inventory_subcategory")) {
                                            Intent iedit = new Intent(context, MasterInventorySubCategoryActivity.class);
                                            iedit.putExtra("nsModule", contextName);
                                            try {
                                                iedit.putExtra("nsId", finalObjData.getString("nsId"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            context.startActivity(iedit);
                                        }
                                        break;
                                    /*case R.id.menu_option_delete:
                                        if(contextName.equals("customer")){
                                            try {
                                                Activity activity = (Activity) context;
                                                curdModule.deleteData(activity, preferenceConfig, finalObjData.getString("nsId"), "0", "master_ledger");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else if(contextName.equals("employee")){
                                            try {
                                                Activity activity = (Activity) context;
                                                //Toast.makeText(activity, ""+finalObjData.getString("nsId"), Toast.LENGTH_SHORT).show();
                                                curdModule.deleteData(activity, preferenceConfig, finalObjData.getString("nsId"), "0", "master_worker");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case R.id.menu_option_Call:
                                        String mobileNo = holder.customer_txtPhoneNumber.getText().toString().trim();
                                        String uri = "tel:" + mobileNo.trim();
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse(uri));
                                        context.startActivity(intent);*/
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return myListFiltered.length();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }



    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView masterOne_lblName;
        TextView ledger_lblName,ledger_lblPhoneNumber,ledger_lblOpeningBalance,ledger_lblArea,ledger_lblCity;
        ImageView ledger_imgTelephone;
        TextView openingstock_lblInventory,openingstock_lblLocation,openingstock_lblOpeningStock;
        LinearLayout ledger_layoutPhone,masterOne_listMenu,llTableView;
        TextView role_lblModuleName;
        TextView role_lblAccessModule,role_lblAccessAdd,role_lblAccessEditSelf,role_lblAccessEditAll,role_lblAccessDeleteSelf,role_lblAccessDeleteAll,role_lblAccessViewSelf,role_lblAccessViewAll;
        TextView ledger_opening_lblName,ledger_opening_lblID,ledger_opening_lblDate,ledger_opening_lblOpeningBalance;
        TextView cart_lblSno,cart_lblInvName,cart_lblQty,cart_lblPrice,cart_lblAmount;
        TextView newSales_lblVoucherNumber,newSales_lblDate,newSales_lblAmount,newSales_lblName,newSales_lblTime;
        CardView newSales_card_view_new,card_view;
        TextView receipt_payment_lblCustomerName,receipt_payment_lblVoucherNo,receipt_payment_lblDate,receipt_payment_lblAmount,receipt_payment_lblPaymentType,receipt_payment_lblTransactionType;
        TextView receipt_payment_list_lblDate,receipt_payment_list_lblType,receipt_payment_list_lblDebit,receipt_payment_list_lblCredit
                ,receipt_payment_list_lblRunningTotal;
        CheckBox receipt_payment_list_checkboxReceiptPayment;
        TextView cart_stocktransfer_lblSno,cart_stocktransfer_lblInvName,cart_stocktransfer_lblQty;
        TextView deliveryItems_lblSno,deliveryItems_lblProduct,deliveryItems_txtQty,deliveryItems_lblPending;
        TextView deliveredItems_lblSno,deliveredItems_lblProduct,deliveredItems_txtQty,deliveredItems_lblPending,deliveredItems_lblLocation,deliveredItems_lblDeliveredDate;
        EditText deliveryItems_txtDelivered;
        CardView daily_stock_card_view;
        TextView daily_stock_lblDate,daily_stock_lblType,daily_stock_lblStack,daily_stock_lblInventory,
                daily_stock_lblQuantity,daily_stock_lblLocation,daily_stock_lblUser,day_inv_val_lblOpeningStock,
                day_inv_val_lblTotal,day_inv_val_lblClosing;

        TextView consolidated_inventory_report_lblSno,consolidated_inventory_report_lblName,consolidated_inventory_report_lblOpeningStock,
                consolidated_inventory_report_lblAccountingStock,consolidated_inventory_report_lblPhysicalStock,
                consolidated_inventory_report_lblClosingManual,consolidated_inventory_report_lblClosingDiffrence;

        TextView stockmanagement_lblDate,stockmanagement_lblInventory,
                stockmanagement_lblLocation,stockmanagement_lblOpeningStock,
                stockmanagement_lblSno,stockmanagement_lblVoucher;

        TextView consolidated_online_balance_reports_lblSno,consolidated_online_balance_reports_billDate,consolidated_online_balance_reports_lblLocation,
                consolidated_online_balance_reports_lblName,consolidated_online_balance_reports_transactionId,
                consolidated_online_balance_reports_lblAmount;

        //TextView upi_payment_receipt_id,upi_payment_receipt_lblDate,upi_payment_receipt_lblAmount,upi_payment_receipt_lblTransactionType;

        TextView  upi_payment_receipt_lblSno,upi_payment_receipt_billDate,upi_payment_receipt_lblRemarks,
                upi_payment_receipt_lblAmount,upi_payment_receipt_lblGrandAmount;

        TextView ledger_transaction_reports_lblDate,ledger_transaction_reports_lblLocation,
                ledger_transaction_reports_lblDebit,ledger_transaction_reports_lblType,
                ledger_transaction_reports_lblCredit,ledger_transaction_reports_lblBalance;

        LinearLayout ledger_transaction_reports_temp111;

        TextView consolidated_ledger_balance_reports_lblSno,consolidated_ledger_balance_reports_lblID,
                consolidated_ledger_balance_reports_lblName,consolidated_ledger_balance_reports_lblArea,
                consolidated_ledger_balance_reports_lblCity,consolidated_ledger_balance_reports_lblBalance;

        TextView inv_detailed_lblSno,inv_detailed_lblDate,inv_detailed_lblNameType,inv_detailed_lblQty,inv_detailed_lblClosingBal,
                inv_detailed_lblPhysicalStock;

        TextView hamali_lblSno,hamali_lblName,hamali_lblQty,hamali_lblAmount;

        TextView expense_lblCustomerName,expense_lblVoucherNo,expense_lblDate,expense_lblAmount,
                expense_lblExpenseType,expense_lblRemarks;

        TextView pvlblSno,pvlblDate,pvlblName,pvlblPrice,pvlblInventory;
        CardView new_expense_card_view_new;

        private ObjectAnimator anim;

        public myViewHolder(View itemView) {
            super(itemView);
            if(contextName.equals("master_location")
                    ||contextName.equals("user_position")
                    ||contextName.equals("master_users")
                    ||contextName.equals("master_area")
                    || contextName.equals("master_city")
                    ||contextName.equals("master_brand")
                    ||contextName.equals("master_expense_head")
                    ||contextName.equals("master_units")
                    ||contextName.equals("master_inventory_category")
                    ||contextName.equals("master_inventory_subcategory")
                    ||contextName.equals("master_modules")
                    ||contextName.equals("transactions_stock_transfer")
                    ||contextName.equals("master_inventory")
                    ||contextName.equals("ff_master_state_code")
                    ||contextName.equals("opening_cash_balance")
            ){
                masterOne_lblName = (TextView) view.findViewById(R.id.lblName);
                masterOne_listMenu = (LinearLayout) view.findViewById(R.id.listMenu);
                card_view = (CardView)view.findViewById(R.id.card_view);
            }else if(contextName.equals("android_signedin_users")){
                masterOne_lblName = (TextView) view.findViewById(R.id.lblName);
            }else if(contextName.equals("master_ledger")){
                ledger_lblName = (TextView) view.findViewById(R.id.lblName);
                ledger_lblPhoneNumber = (TextView) view.findViewById(R.id.lblPhoneNumber);
                ledger_lblOpeningBalance = (TextView) view.findViewById(R.id.lblOpeningBalance);
                ledger_lblArea = (TextView) view.findViewById(R.id.lblArea);
                ledger_lblCity = (TextView) view.findViewById(R.id.lblCity);
                ledger_imgTelephone = (ImageView) view.findViewById(R.id.imgTelephone);
                ledger_layoutPhone = (LinearLayout) view.findViewById(R.id.layoutPhone);

                anim = ObjectAnimator.ofFloat(ledger_imgTelephone, "alpha", 0.3f, 1f);
                anim.setRepeatMode(ValueAnimator.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(600);

            }else if(contextName.equals("transaction_openingstock") || contextName.equals("inventory_saftey_stock")){
                openingstock_lblInventory = (TextView) view.findViewById(R.id.lblInventory);
                openingstock_lblLocation = (TextView) view.findViewById(R.id.lblLocation);
                openingstock_lblOpeningStock = (TextView) view.findViewById(R.id.lblOpeningStock);

            }else if(contextName.equals("transaction_price_variation")){
                pvlblSno = (TextView) view.findViewById(R.id.lblSno);
                pvlblDate = (TextView) view.findViewById(R.id.lblDate);
                //pvlblVoucherNo = (TextView) view.findViewById(R.id.lblVoucherNo);
                pvlblName = (TextView) view.findViewById(R.id.lblName);
                pvlblPrice = (TextView) view.findViewById(R.id.lblPrice);
                pvlblInventory = (TextView) view.findViewById(R.id.lblInventory);

            }else if(contextName.equals("transaction_stock_management")){
                stockmanagement_lblSno = (TextView) view.findViewById(R.id.lblSno);
                stockmanagement_lblDate = (TextView) view.findViewById(R.id.lblDate);
                stockmanagement_lblInventory = (TextView) view.findViewById(R.id.lblInventory);
                stockmanagement_lblVoucher = (TextView) view.findViewById(R.id.lblVoucher);
                stockmanagement_lblLocation = (TextView) view.findViewById(R.id.lblLocation);
                stockmanagement_lblOpeningStock = (TextView) view.findViewById(R.id.lblStock);

            }else if(contextName.equals("cart_items_stock_transfer")){
                cart_stocktransfer_lblSno = (TextView) view.findViewById(R.id.lblSno);
                cart_stocktransfer_lblInvName = (TextView) view.findViewById(R.id.lblInvName);
                cart_stocktransfer_lblQty = (TextView) view.findViewById(R.id.lblQty);

            }else if(contextName.equals("master_user_role_management")){
                role_lblModuleName = (TextView) view.findViewById(R.id.lblModuleName);
                role_lblAccessModule = (TextView) view.findViewById(R.id.lblAccessModule);
                role_lblAccessAdd = (TextView) view.findViewById(R.id.lblAccessAdd);
                role_lblAccessEditSelf = (TextView) view.findViewById(R.id.lblAccessEditSelf );
                role_lblAccessEditAll = (TextView) view.findViewById(R.id.lblAccessEditAll);
                role_lblAccessDeleteSelf = (TextView) view.findViewById(R.id.lblAccessDeleteSelf);
                role_lblAccessDeleteAll = (TextView) view.findViewById(R.id.lblAccessDeleteAll);
                role_lblAccessViewSelf = (TextView) view.findViewById(R.id.lblAccessViewSelf);
                role_lblAccessViewAll = (TextView) view.findViewById(R.id.lblAccessViewAll);
            }else if(contextName.equals("transaction_ledger_opening_balance")){
                ledger_opening_lblName = (TextView) view.findViewById(R.id.lblName);
                ledger_opening_lblID = (TextView) view.findViewById(R.id.lblID);
                ledger_opening_lblDate = (TextView) view.findViewById(R.id.lblDate);
                ledger_opening_lblOpeningBalance = (TextView) view.findViewById(R.id.lblOpeningBalance );
            }else if(contextName.equals("cart_items")){
                cart_lblSno = (TextView) view.findViewById(R.id.lblSno);
                cart_lblInvName = (TextView) view.findViewById(R.id.lblInvName);
                cart_lblQty = (TextView) view.findViewById(R.id.lblQty);
                cart_lblPrice = (TextView) view.findViewById(R.id.lblPrice);
                cart_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
            }else if(contextName.equals("transactions_in_out")){
                newSales_lblVoucherNumber = (TextView) view.findViewById(R.id.lblVoucherNumber);
                newSales_lblDate = (TextView) view.findViewById(R.id.lblDate);
                newSales_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
                newSales_lblName = (TextView) view.findViewById(R.id.lblName);
                newSales_lblTime = (TextView) view.findViewById(R.id.lblTime);
                newSales_card_view_new = (CardView) view.findViewById(R.id.card_view_new);
            }
            else if(contextName.equals("offset_items")){
                deliveryItems_lblSno = (TextView) view.findViewById(R.id.lblSno);
                deliveryItems_lblProduct = (TextView) view.findViewById(R.id.lblProduct);
                deliveryItems_txtQty = (TextView) view.findViewById(R.id.txtQty);
                deliveryItems_lblPending = (TextView) view.findViewById(R.id.lblPending);
                deliveryItems_txtDelivered = (EditText) view.findViewById(R.id.txtDelivered);
            }
            else if(contextName.equals("offset_items_delivered")){
                deliveredItems_lblSno = (TextView) view.findViewById(R.id.lblSno);
                deliveredItems_lblProduct = (TextView) view.findViewById(R.id.lblProduct);
                deliveredItems_txtQty = (TextView) view.findViewById(R.id.txtQty);
                deliveredItems_lblLocation = (TextView) view.findViewById(R.id.lblLocation);
                deliveredItems_lblDeliveredDate = (TextView) view.findViewById(R.id.lblDeliveryDate);
            }else if(contextName.equals("upi_payment_receipt")){
                /*upi_payment_receipt_id = (TextView) view.findViewById(R.id.lblVoucherNo);
                upi_payment_receipt_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
                upi_payment_receipt_lblDate = (TextView) view.findViewById(R.id.lblDate);
                upi_payment_receipt_lblTransactionType = (TextView) view.findViewById(R.id.lblTransactionType);*/

                upi_payment_receipt_lblSno = (TextView) view.findViewById(R.id.lblSno);
                upi_payment_receipt_billDate = (TextView) view.findViewById(R.id.billDate);
                upi_payment_receipt_lblRemarks = (TextView) view.findViewById(R.id.lblRemarks);
                upi_payment_receipt_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
                upi_payment_receipt_lblGrandAmount = (TextView) view.findViewById(R.id.lblGrandAmount);

            }
            else if(contextName.equals("transaction_receipt_payment")){
                receipt_payment_lblCustomerName = (TextView) view.findViewById(R.id.lblCustomerName);
                receipt_payment_lblVoucherNo = (TextView) view.findViewById(R.id.lblVoucherNo);
                receipt_payment_lblDate = (TextView) view.findViewById(R.id.lblDate);
                receipt_payment_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
                receipt_payment_lblTransactionType = (TextView) view.findViewById(R.id.lblTransactionType);
                receipt_payment_lblPaymentType = (TextView) view.findViewById(R.id.lblPaymentType);
                newSales_card_view_new = (CardView) view.findViewById(R.id.card_view_new);
            }else if(contextName.equals("master_expense")){
                expense_lblCustomerName = (TextView) view.findViewById(R.id.lblName);
                expense_lblVoucherNo = (TextView) view.findViewById(R.id.lblVoucherNo);
                expense_lblDate = (TextView) view.findViewById(R.id.lblDate);
                expense_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
                expense_lblExpenseType = (TextView) view.findViewById(R.id.lblExpenseType);
                expense_lblRemarks = (TextView) view.findViewById(R.id.lblRemarks);
                new_expense_card_view_new = (CardView) view.findViewById(R.id.card_view_new);

            }else if(contextName.equals("cart_customer_list_receipt_payment")){
                receipt_payment_list_lblDate = (TextView) view.findViewById(R.id.lblDate);
                receipt_payment_list_lblType = (TextView) view.findViewById(R.id.lblType);
                receipt_payment_list_lblDebit = (TextView) view.findViewById(R.id.lblDebit);
                receipt_payment_list_lblCredit = (TextView) view.findViewById(R.id.lblCredit);
                receipt_payment_list_checkboxReceiptPayment = (CheckBox) view.findViewById(R.id.checkboxReceiptPayment);
                receipt_payment_list_lblRunningTotal = (TextView) view.findViewById(R.id.lblRunningTotal);


                receipt_payment_list_checkboxReceiptPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(array.get(getAdapterPosition())){
                            array.put(getAdapterPosition(),false);
                            try {
                                listener.recyclerCheckboxChecked(myListFiltered.getJSONObject(getAdapterPosition()),getAdapterPosition(),2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            notifyDataSetChanged();
                        }else{
                            array.put(getAdapterPosition(),true);
                            try {
                                listener.recyclerCheckboxChecked(myListFiltered.getJSONObject(getAdapterPosition()),getAdapterPosition(),1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if(contextName.equals("master_daily_stock_closing")){
                daily_stock_card_view = (CardView) view.findViewById(R.id.card_view);
                daily_stock_lblDate = (TextView) view.findViewById(R.id.lblDate);
                daily_stock_lblType = (TextView) view.findViewById(R.id.lblType);
                daily_stock_lblStack = (TextView) view.findViewById(R.id.lblStack);
                daily_stock_lblInventory = (TextView) view.findViewById(R.id.lblInventory);
                daily_stock_lblQuantity = (TextView) view.findViewById(R.id.lblQuantity);
                daily_stock_lblLocation = (TextView) view.findViewById(R.id.lblLocation);
                daily_stock_lblUser = (TextView) view.findViewById(R.id.lblUser);
            }
            else if(contextName.equals("consolidated_inventory_report")){
                consolidated_inventory_report_lblSno = (TextView) view.findViewById(R.id.lblSno);
                consolidated_inventory_report_lblName = (TextView) view.findViewById(R.id.lblName);
                consolidated_inventory_report_lblAccountingStock = (TextView) view.findViewById(R.id.lblAccountingStock);
                consolidated_inventory_report_lblClosingManual = (TextView) view.findViewById(R.id.lblClosingManual);
                consolidated_inventory_report_lblPhysicalStock = (TextView) view.findViewById(R.id.lblPhysicalStock);
                consolidated_inventory_report_lblClosingDiffrence = (TextView) view.findViewById(R.id.lblClosingDiffrence);
                consolidated_inventory_report_lblOpeningStock = (TextView) view.findViewById(R.id.lblOpeningStock);
                llTableView = (LinearLayout) view.findViewById(R.id.llTableView);

            }else if(contextName.equals("master_hamali_calculation")){
                hamali_lblSno = (TextView) view.findViewById(R.id.lblSno);
                hamali_lblName = (TextView) view.findViewById(R.id.lblName);
                hamali_lblQty = (TextView) view.findViewById(R.id.lblQty);
                hamali_lblAmount = (TextView) view.findViewById(R.id.lblAmount);


            }else if(contextName.equals("consolidated_online_balance_report")){
                consolidated_online_balance_reports_lblSno = (TextView) view.findViewById(R.id.lblSno);
                consolidated_online_balance_reports_billDate = (TextView) view.findViewById(R.id.billDate);
                consolidated_online_balance_reports_lblLocation = (TextView) view.findViewById(R.id.lblLocation);
                consolidated_online_balance_reports_lblName = (TextView) view.findViewById(R.id.lblName);
                consolidated_online_balance_reports_lblAmount = (TextView) view.findViewById(R.id.lblAmount);
                //consolidated_online_balance_reports_transactionId = (TextView) view.findViewById(R.id.tra)
            }else if(contextName.equals("report_ledger_transaction")){
                ledger_transaction_reports_lblDate = (TextView) view.findViewById(R.id.lblDate);
                ledger_transaction_reports_lblLocation = (TextView) view.findViewById(R.id.lblLocation);
                ledger_transaction_reports_lblDebit = (TextView) view.findViewById(R.id.lblDebit);
                ledger_transaction_reports_lblType = (TextView) view.findViewById(R.id.lblType);
                ledger_transaction_reports_lblCredit = (TextView) view.findViewById(R.id.lblCredit);
                ledger_transaction_reports_lblBalance = (TextView) view.findViewById(R.id.lblBalance);
                ledger_transaction_reports_temp111 = (LinearLayout) view.findViewById(R.id.temp111);
            }else if(contextName.equals("report_consolidated_ledger_balance")){
                consolidated_ledger_balance_reports_lblSno = (TextView) view.findViewById(R.id.lblSno);
                consolidated_ledger_balance_reports_lblID = (TextView) view.findViewById(R.id.lblID);
                consolidated_ledger_balance_reports_lblName = (TextView) view.findViewById(R.id.lblName);
                consolidated_ledger_balance_reports_lblArea = (TextView) view.findViewById(R.id.lblArea);
                consolidated_ledger_balance_reports_lblCity = (TextView) view.findViewById(R.id.lblCity);
                consolidated_ledger_balance_reports_lblBalance = (TextView) view.findViewById(R.id.lblBalance);
            }else if(contextName.equals("fetch_inventory_detailed_report")){
                inv_detailed_lblSno = (TextView) view.findViewById(R.id.lblSno);
                inv_detailed_lblDate = (TextView) view.findViewById(R.id.lblDate);
                inv_detailed_lblNameType = (TextView) view.findViewById(R.id.lblNameType);
                inv_detailed_lblQty = (TextView) view.findViewById(R.id.lblQty);
                inv_detailed_lblClosingBal = (TextView) view.findViewById(R.id.lblClosingBal);
                inv_detailed_lblPhysicalStock = (TextView) view.findViewById(R.id.lblPhysicalStock);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.OnItemSelected(myListFiltered.getJSONObject(getAdapterPosition()),getAdapterPosition());

                        /*Toast.makeText(context, ""+getAdapterPosition(), Toast.LENGTH_SHORT).show();*/
                        /*if(contextName.equals("receiptList")) {
                            temp111.setEnabled(false);
                            temp111.setBackgroundColor(CYAN);
                        }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            /*if(contextName.equals("receiptList")) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            listener.OnItemLongSelected(myListFiltered.getJSONObject(getAdapterPosition()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return false;
                    }
                });

            }*/


        }

        public void bind(JSONObject dataObject){
            anim.start();
        }
    }

    public interface myAdapterListener {

        void OnItemSelected(JSONObject item, int position);
        //void OnItemLongSelected(JSONObject item);
        void recyclerCheckboxChecked(JSONObject item, int position,int state);
    }

    //clears the old data on pull refresh
    public void clear() {
        myList=new JSONArray(new ArrayList<String>());
        notifyDataSetChanged();
    }

}
