package com.hgbao.hcmushandbook;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hgbao.adapter.AdapterAct;
import com.hgbao.adapter.AdapterEntertainment;
import com.hgbao.adapter.AdapterScholarship;
import com.hgbao.model.Act;
import com.hgbao.model.Entertainment;
import com.hgbao.model.Scholarship;
import com.hgbao.provider.DataProvider;
import com.hgbao.provider.SupportProvider;

import java.util.ArrayList;


public class OptionListActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    ActionBar actionBar;
    SearchView searchView;
    DrawerLayout layoutDrawer;
    NavigationView navigationView;
    MenuItem menu_search;

    ListView lvOptionList;
    boolean filter1, filter2, filter3, filter4, filter5;

    //Data
    int current_type;//1: activity, 2: scholarship, 3: entertainment, 0: other

    ArrayList<Scholarship> list_scholarship;
    AdapterScholarship adapter_scholarship;

    ArrayList<Entertainment> list_entertainment;
    AdapterEntertainment adapter_entertainment;

    ArrayList<Act> list_activity;
    AdapterAct adapter_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_list);

        switch (getIntent().getStringExtra(DataProvider.EXTRA_OPTION_TYPE)){
            case DataProvider.EXTRA_ACTIVITY:
                current_type = 1;
                break;
            case DataProvider.EXTRA_SCHOLARSHIP:
                current_type = 2;
                break;
            case DataProvider.EXTRA_ENTERTAINMENT:
                current_type = 3;
                break;
            default:
                current_type = 0;
        }

        addControl();
        setControlData();
    }

    private void addControl(){
        filter1 = true;
        filter2 = true;
        filter3 = true;
        filter4 = true;
        filter5 = true;
        //Action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Navigation drawer
        layoutDrawer = (DrawerLayout) findViewById(R.id.list_nav_drawer);
        navigationView = (NavigationView) findViewById(R.id.list_nav_view);
        //Controls
        lvOptionList = (ListView) findViewById(R.id.lvOptionList);
        lvOptionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openItem(position);
            }
        });
    }

    private void setListScholarship(){
        int n = DataProvider.list_scholarship.size();
        list_scholarship = new ArrayList<>();
        for (int i = 0; i < n; i++){
            Scholarship scholarship = DataProvider.list_scholarship.get(i);
            if (!scholarship.isExpired() || filter5) {
                if (scholarship.getType() == 1 && filter1)
                    list_scholarship.add(scholarship);
                if (scholarship.getType() == 2 && filter2)
                    list_scholarship.add(scholarship);
                if (scholarship.getType() == 3 && filter3)
                    list_scholarship.add(scholarship);
                if (scholarship.getType() == 4 && filter4)
                    list_scholarship.add(scholarship);
            }
        }
        adapter_scholarship = new AdapterScholarship(OptionListActivity.this, R.layout.custom_row_scholarship, list_scholarship);
        lvOptionList.setAdapter(adapter_scholarship);
        adapter_scholarship.notifyDataSetChanged();
    }

    private void setListEntertainment(){
        int n = DataProvider.list_entertainment.size();
        list_entertainment = new ArrayList<>();
        for (int i = 0; i < n; i++){
            Entertainment entertainment = DataProvider.list_entertainment.get(i);
                if (entertainment.getType() == 1 && filter1)
                    list_entertainment.add(entertainment);
                if (entertainment.getType() == 2 && filter2)
                    list_entertainment.add(entertainment);
                if (entertainment.getType() == 3 && filter3)
                    list_entertainment.add(entertainment);
        }
        adapter_entertainment = new AdapterEntertainment(OptionListActivity.this, R.layout.custom_row_entertainment, list_entertainment);
        lvOptionList.setAdapter(adapter_entertainment);
        adapter_entertainment.notifyDataSetChanged();
    }

    private void setListActivity(){
        int n = DataProvider.list_activity.size();
        list_activity = new ArrayList<>();
        for (int i = 0; i < n; i++){
            Act activity = DataProvider.list_activity.get(i);
            if (activity.getType() == 1 && filter1)
                list_activity.add(activity);
            if (activity.getType() == 2 && filter2)
                list_activity.add(activity);
            if (activity.getType() == 3 && filter3)
                list_activity.add(activity);
            if (activity.getType() == 4 && filter4)
                list_activity.add(activity);
        }
        adapter_activity = new AdapterAct(OptionListActivity.this, R.layout.custom_row_activity, list_activity);
        lvOptionList.setAdapter(adapter_activity);
        adapter_activity.notifyDataSetChanged();
    }

    private void setControlData(){
        switch (current_type) {
            case 1:{
                setListActivity();//Initilize the list
                navigationView.inflateMenu(R.menu.navigation_activity);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actType1:
                                filter1 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.actType2:
                                filter2 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.actType3:
                                filter3 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.actResult:
                                filter4 = SupportProvider.checkItem(menuItem);
                                break;
                        }
                        setListActivity();
                        return false;
                    }
                });
                break;
            }
            case 2: {
                setListScholarship();//Initilize the list
                navigationView.inflateMenu(R.menu.navigation_scholarship);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.schType1:
                                filter1 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.schType2:
                                filter2 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.schType3:
                                filter3 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.schResult:
                                filter4 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.schExpired:
                                filter5 = SupportProvider.checkItem(menuItem);
                        }
                        setListScholarship();
                        return false;
                    }
                });
                break;
            }
            case 3: {
                setListEntertainment();//Initilize the list
                navigationView.inflateMenu(R.menu.navigation_entertainment);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.entType1:
                                filter1 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.entType2:
                                filter2 = SupportProvider.checkItem(menuItem);
                                break;
                            case R.id.entType3:
                                filter3 = SupportProvider.checkItem(menuItem);
                                break;
                        }
                        setListEntertainment();
                        return false;
                    }
                });
                break;
            }
        }
    }

    private void openItem(int position) {
        switch (current_type) {
            case 1: {
                Intent i = new Intent(OptionListActivity.this, ActActivity.class);
                i.putExtra(DataProvider.EXTRA_ITEM, adapter_activity.getItem(position));
                startActivity(i);
                break;
            }
            case 2: {
                Intent i = new Intent(OptionListActivity.this, ScholarshipActivity.class);
                i.putExtra(DataProvider.EXTRA_ITEM, adapter_scholarship.getItem(position));
                startActivity(i);
                break;
            }
            case 3: {
                Intent i = new Intent(OptionListActivity.this, EntertainmentActivity.class);
                //Since the extra data put to another intent is limited in size,
                //entertainments with huge list_photo cannot be put. So I put entertainment's ID then search for it.
                i.putExtra(DataProvider.EXTRA_ITEM, adapter_entertainment.getItem(position).getId());
                startActivity(i);
                break;
            }
        }
        overridePendingTransition(R.anim.activity_slide_left_1, R.anim.activity_slide_left_2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option_list, menu);
        this.menu_search = menu.findItem(R.id.action_search);

        //Search view
        lvOptionList.setTextFilterEnabled(true);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(OptionListActivity.this);
        searchView.setIconified(false);
        searchView.clearFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_menu:
                if (layoutDrawer.isDrawerOpen(GravityCompat.END))
                    layoutDrawer.closeDrawer(GravityCompat.END);
                else
                    layoutDrawer.openDrawer(GravityCompat.END);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        if (TextUtils.isEmpty(query))
            lvOptionList.clearTextFilter();
        else
            lvOptionList.setFilterText(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText))
            lvOptionList.clearTextFilter();
        else
            lvOptionList.setFilterText(newText);
        return false;
    }
}
