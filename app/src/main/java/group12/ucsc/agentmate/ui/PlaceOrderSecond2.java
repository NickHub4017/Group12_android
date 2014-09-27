package group12.ucsc.agentmate.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

/**
 * Created by NRV on 9/27/2014.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import group12.ucsc.agentmate.R;
import group12.ucsc.agentmate.bll.Order;
import group12.ucsc.agentmate.bll.Representative;
import group12.ucsc.agentmate.bll.SellItem;
import group12.ucsc.agentmate.bll.UnitMap;
import group12.ucsc.agentmate.bll.Vendor;
import group12.ucsc.agentmate.dbc.DatabaseControl;
import group12.ucsc.agentmate.bll.mapper;
import group12.ucsc.agentmate.ui.DialogGetQty.GetQtyCommunicator;
/**
 * Created by NRV on 9/27/2014.
 */
public class PlaceOrderSecond2 extends Activity implements GetQtyCommunicator{
    Order new_order=new Order(); //Create new Order
    Order dmnd_new_order=new Order();
    DatabaseControl dbc = new DatabaseControl(this);
    UnitMap[] map;
    mapper mpUnitnames=null;
    AutoCompleteTextView itemID_edit_auto;
    AutoCompleteTextView itemName_edit_auto;
    SellItem currentItem,currentdemanditem;
    boolean select_exsist,demand_exsist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_order_item_add);

        itemID_edit_auto = (AutoCompleteTextView) findViewById(R.id.auto_comp_item_id);
        itemName_edit_auto = (AutoCompleteTextView) findViewById(R.id.auto_comp_item_name);

        final Representative logged_rep = (Representative) getIntent().getExtras().getSerializable("logged_user");
        final Vendor sel_vendor = (Vendor) getIntent().getExtras().getSerializable("vendor");

        TextView logged_vendor_tv = (TextView) findViewById(R.id.txt_vname_order_b);
        logged_vendor_tv.setText("Selected Vendor is :- " + sel_vendor.getShopName());

        table_hdr();///Draw headers of the tables
        demand_table_hdr();

        Cursor itm_cur = dbc.getAllItemByName();

        final String[] str_arry_item_id = new String[itm_cur.getCount()];
        final String[] str_arry_item_name = new String[itm_cur.getCount()];
        int j = 0;
        if (itm_cur.moveToFirst() && itm_cur.getCount() != 0) {
            do {
                str_arry_item_id[j] = itm_cur.getString(itm_cur.getColumnIndex("ItemID"));
                str_arry_item_name[j] = itm_cur.getString(itm_cur.getColumnIndex("ItemName"));
                j++;
            } while (itm_cur.moveToNext());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, str_arry_item_id);
        itemID_edit_auto.setAdapter(adapter);
        itemID_edit_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selection = (String) adapterView.getItemAtPosition(position);//
                mpUnitnames=new mapper(getApplicationContext(),selection);//GET the unit maps
                int pos = new_order.findById(selection);
                int pos_dmnd=dmnd_new_order.findById(selection);
                Toast.makeText(getApplicationContext(),dmnd_new_order.list.size()+"**",Toast.LENGTH_SHORT).show();

                if (pos != -1) {
                    Toast.makeText(getApplicationContext(),"it is in list",Toast.LENGTH_SHORT).show();
                    currentItem = new_order.findByIdObj(pos);
                    select_exsist=true;
                }
                else{
                    Toast.makeText(getApplicationContext(),"it is not in list",Toast.LENGTH_SHORT).show();
                    select_exsist=false;
                    currentItem = new SellItem(selection, PlaceOrderSecond2.this);
                }
                ///to demand item
                if (pos_dmnd != -1) {
                    Toast.makeText(getApplicationContext(),"it is in demand",Toast.LENGTH_SHORT).show();
                    currentdemanditem = dmnd_new_order.findByIdObj(pos_dmnd);
                    demand_exsist=true;
                }
                else{
                    Toast.makeText(getApplicationContext(),"it is not in demand",Toast.LENGTH_SHORT).show();
                    demand_exsist=false;
                    currentdemanditem = new SellItem(selection, PlaceOrderSecond2.this);
                }

                FragmentManager fm = getFragmentManager();
                DialogGetQty md = new DialogGetQty();
                Bundle args = new Bundle();

                args.putInt("qty", currentItem.getStoreQty());
                args.putString("itemid", currentItem.getItemID());
                args.putSerializable("umapname", mpUnitnames);
                md.setArguments(args);
                md.show(fm, "dialog2");



            }

        });

        Button btset=(Button)findViewById(R.id.button_testing);
        btset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dbc.k();
            }
        });
    }

    public void table_hdr() {
        TableLayout tl = (TableLayout) findViewById(R.id.selected_table1);
        final TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
        tr_head.setBackgroundColor(Color.BLACK);
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView label_Item_ID = new TextView(this);
        label_Item_ID.setId(20);
        label_Item_ID.setText("Item ID");
        label_Item_ID.setTextColor(Color.WHITE);
        label_Item_ID.setPadding(5, 5, 5, 5);
        tr_head.addView(label_Item_ID);// add the column to the table row here

        TextView label_Item_Name = new TextView(this);
        label_Item_Name.setId(21);// define id that must be unique
        label_Item_Name.setText("Item Name"); // set the text for the header
        label_Item_Name.setTextColor(Color.WHITE); // set the color
        label_Item_Name.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Item_Name); // add the column to the table row here

        TextView label_Qty = new TextView(this);
        label_Qty.setId(22);// define id that must be unique
        label_Qty.setText("Qty"); // set the text for the header
        label_Qty.setTextColor(Color.WHITE); // set the color
        label_Qty.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Qty); // add the column to the table row here

        TextView label_Discount = new TextView(this);
        label_Discount.setId(23);// define id that must be unique
        label_Discount.setText("Discount"); // set the text for the header
        label_Discount.setTextColor(Color.WHITE); // set the color
        label_Discount.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Discount); // add the column to the table row here

        TextView label_Price = new TextView(this);
        label_Price.setId(24);// define id that must be unique
        label_Price.setText("Price"); // set the text for the header
        label_Price.setTextColor(Color.WHITE); // set the color
        label_Price.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Price); // add the column to the table row here

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

    public void demand_table_hdr() {
        TableLayout tl = (TableLayout) findViewById(R.id.demanded_table);
        final TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
        tr_head.setBackgroundColor(Color.BLACK);
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView label_Item_ID = new TextView(this);
        label_Item_ID.setId(30);
        label_Item_ID.setText("Item ID");
        label_Item_ID.setTextColor(Color.WHITE);
        label_Item_ID.setPadding(5, 5, 5, 5);
        tr_head.addView(label_Item_ID);// add the column to the table row here

        TextView label_Item_Name = new TextView(this);
        label_Item_Name.setId(31);// define id that must be unique
        label_Item_Name.setText("Item Name"); // set the text for the header
        label_Item_Name.setTextColor(Color.WHITE); // set the color
        label_Item_Name.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Item_Name); // add the column to the table row here

        TextView label_Qty = new TextView(this);
        label_Qty.setId(32);// define id that must be unique
        label_Qty.setText("Qty"); // set the text for the header
        label_Qty.setTextColor(Color.WHITE); // set the color
        label_Qty.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Qty); // add the column to the table row here

        TextView label_Discount = new TextView(this);
        label_Discount.setId(33);// define id that must be unique
        label_Discount.setText("Date"); // set the text for the header
        label_Discount.setTextColor(Color.WHITE); // set the color
        label_Discount.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_Discount); // add the column to the table row here


        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

    public void DrawTable(ArrayList<SellItem> arls){
        try {
            TableLayout ttt = (TableLayout) findViewById(R.id.selected_table1);
            //for (int i = 1; i < new_order.list.size(); i++) {
            ttt.removeAllViewsInLayout();
            //}
        }
        catch (Exception e){}
        table_hdr();
        for (int i=0;i<arls.size();i++){
            RowCreator(arls.get(i), R.id.selected_table1,i);
            Log.d("PlaceOrderArray",arls.get(i).getItemID()+"**"+arls.get(i).getQty());
        }
    }

    public void RowCreator(SellItem item, int layout,int rw) {

        TableLayout tl = (TableLayout) findViewById(layout);

// Create the table row
        final TableRow tr = new TableRow(this);


        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getFragmentManager();
                DialogEditQty md = new DialogEditQty();
                md.show(fm, "edit");

            }
        });

        if (rw % 2 != 0) tr.setBackgroundColor(Color.GRAY);
        tr.setId(100 + rw);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
///TODO must write a method to get qty
//Create two columns to add as table data
        // Create a TextView to add date
        TextView labelID = new TextView(this);
        labelID.setId(200 + rw);
        labelID.setText(item.getItemID());
        labelID.setPadding(2, 0, 5, 0);
        labelID.setTextColor(Color.BLACK);
        tr.addView(labelID);

        TextView labelName = new TextView(this);
        labelName.setId(300 + rw);
        labelName.setText(String.valueOf(item.getItemName()));
        labelName.setTextColor(Color.BLACK);
        tr.addView(labelName);

        TextView labelQty = new TextView(this);
        labelQty.setId(400 + rw);
//        labelQty.setText(String.valueOf(item.getQty())+" "+item.getSelectedUnit());
        labelQty.setTextColor(Color.BLACK);
        //tr.addView(labelQty);

        TextView labelDiscount = new TextView(this);
        labelDiscount.setId(500 + rw);
        //labelDiscount.setText(String.valueOf(item.getRelavantDiscount(item.getQty())));
        labelDiscount.setTextColor(Color.BLACK);


            labelQty.setText(String.valueOf(item.getQty()) + " " + item.getSelectedUnit());
            tr.addView(labelQty);
            labelDiscount.setText(String.valueOf(item.getRelavantDiscount(item.getQty())));
            tr.addView(labelDiscount);
            TextView labelPrice = new TextView(this);
            labelPrice.setId(600 + rw);
            double value = (100 - item.getRelavantDiscount(item.getQty())) * item.getPrice() * item.getQty();
            labelPrice.setText(String.valueOf(value / 100));
            labelPrice.setTextColor(Color.BLACK);
            tr.addView(labelPrice);


// finally add this to the table row
        tl.addView(tr, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));


    }

    public void DemandDrawTable(ArrayList<SellItem> arls){
        try {
            TableLayout ttt = (TableLayout) findViewById(R.id.demanded_table);
            //for (int i = 1; i < new_order.list.size(); i++) {
            ttt.removeAllViewsInLayout();
            //}
        }
        catch (Exception e){}
        demand_table_hdr();
        for (int i=0;i<arls.size();i++){
            DemandRowCreator(arls.get(i), R.id.demanded_table,i);
            Log.d("PlaceOrderArray",arls.get(i).getItemID()+"**"+arls.get(i).getQty());
        }
    }

    public void DemandRowCreator(SellItem item, int layout,int rw) {

        TableLayout tl = (TableLayout) findViewById(layout);

// Create the table row
        final TableRow tr = new TableRow(this);


        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getFragmentManager();
                DialogEditQty md = new DialogEditQty();
                md.show(fm, "edit");

            }
        });

        if (rw % 2 != 0) tr.setBackgroundColor(Color.GRAY);
        tr.setId(100 + rw);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
///TODO must write a method to get qty
//Create two columns to add as table data
        // Create a TextView to add date
        TextView labelID = new TextView(this);
        labelID.setId(200 + rw);
        labelID.setText(item.getItemID());
        labelID.setPadding(2, 0, 5, 0);
        labelID.setTextColor(Color.BLACK);
        tr.addView(labelID);

        TextView labelName = new TextView(this);
        labelName.setId(300 + rw);
        labelName.setText(String.valueOf(item.getItemName()));
        labelName.setTextColor(Color.BLACK);
        tr.addView(labelName);

        TextView labelQty = new TextView(this);
        labelQty.setId(400 + rw);
        labelQty.setText(String.valueOf(item.getQty())+" "+item.getSelectedUnit());
        labelQty.setTextColor(Color.BLACK);
        tr.addView(labelQty);


        TextView labelDate = new TextView(this);
        labelDate.setId(600 + rw);
        labelDate.setText("****************");
        labelDate.setTextColor(Color.BLACK);
        tr.addView(labelDate);


// finally add this to the table row
        tl.addView(tr, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));


    }

    @Override
    public void onGetData(int qty, int demandQty) {
        Toast.makeText(getApplicationContext(),"//////////////"+qty+" "+demandQty+" "+" " ,Toast.LENGTH_SHORT).show();
        //to select item
        if((!select_exsist) &&(qty!=0)){
            currentItem.setQty(qty);
            currentItem.setStoreQty(currentItem.getStoreQty()-qty);
            new_order.addItem(currentItem);
        }
        else{
            int temp=currentItem.getQty();
            currentItem.resetStoreQty();
            currentItem.setQty(temp+qty);
            currentItem.setStoreQty(currentItem.getStoreQty()-currentItem.getQty());
        }
        //to demand item

        if((!demand_exsist)&&(demandQty!=0)){
            currentdemanditem.setQty(demandQty);
            dmnd_new_order.addItem(currentdemanditem);
        }
        else{
            currentdemanditem.setQty(demandQty);
        }

        for (int i=0;i<new_order.list.size();i++){
            String c=new_order.list.get(i).getItemID()+" "+new_order.list.get(i).getItemName()+" "+new_order.list.get(i).getStoreQty();
            Log.d("new order",c);
            Toast.makeText(getApplicationContext(),c,Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(),"***********",Toast.LENGTH_SHORT).show();
        for (int i=0;i<dmnd_new_order.list.size();i++){
            String c=dmnd_new_order.list.get(i).getItemID()+" "+dmnd_new_order.list.get(i).getItemName()+" "+dmnd_new_order.list.get(i).getStoreQty();
            Log.d("dmnd_new_order",c);
            Toast.makeText(getApplicationContext(),c,Toast.LENGTH_SHORT).show();
        }

        DrawTable(new_order.list);
        DemandDrawTable(dmnd_new_order.list);
    }
}



