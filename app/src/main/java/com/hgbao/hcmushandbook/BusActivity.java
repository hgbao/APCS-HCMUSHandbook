package com.hgbao.hcmushandbook;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class BusActivity extends ActionBarActivity {

    ListView lvBus;
    ArrayList<String> list_bus;
    ArrayAdapter<String> adapter_bus;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        addControl();
        initializeData();
    }

    private void addControl(){
        //Action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Controls
        lvBus = (ListView) findViewById(R.id.lvBus);
        list_bus = new ArrayList<>();
        adapter_bus = new ArrayAdapter<String>(BusActivity.this, android.R.layout.simple_list_item_1, list_bus);
        lvBus.setAdapter(adapter_bus);
    }

    private void initializeData(){
        list_bus.add("06 - Bến xe Chợ Lớn - Đại học Nông Lâm");
        list_bus.add("11 - Bến Thành – Đầm Sen");
        list_bus.add("35 - Tuyến xe buýt Quận 1");
        list_bus.add("38 - KDC Tân Quy - Bến Thành - Đầm Sen");
        list_bus.add("45 - Bến xe Quận 8 - Bến Thành - Bến xe Miền Đông");
        list_bus.add("50 - Đại học Bách khoa - Bến Thành - Đại học Quốc gia");
        list_bus.add("56 - Bến xe Chợ Lớn - Đại học Giao thông Vận tải");
        list_bus.add("96 - Bến Thành - Chợ Bình Điền");
        list_bus.add("139 - Bến xe Miền Tây - Khu tái định cư Phú Mỹ");
        adapter_bus.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
