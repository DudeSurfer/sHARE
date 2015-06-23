package com.swagger.navneeeth99.share;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
    private String[] mNavChoices;
    private DrawerLayout mLeftNavDrawer;
    private android.support.v4.app.ActionBarDrawerToggle mNavToggle;
    private ListView mLeftNavList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mNavChoices = getResources().getStringArray(R.array.navdrawer_items);
        mLeftNavDrawer = (DrawerLayout)findViewById(R.id.side_nav);
        mLeftNavList = (ListView)findViewById(R.id.drawer_list);

        mLeftNavList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mNavChoices));
        mLeftNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mSelectedDest = (String)mLeftNavList.getItemAtPosition(position);
                if (mSelectedDest.equals("Home")){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);

                } else if (mSelectedDest.equals("Profile")){
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    startActivity(intent);

                } else if (mSelectedDest.equals("Notes")){

                } else if (mSelectedDest.equals("Settings")){

                } else if (mSelectedDest.equals("Chat")) {
                    Intent intent = new Intent(mContext, IndivChatActivity.class);
                    startActivity(intent);


                }
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mLeftNavDrawer, R.drawable.ic_drawer, R.string.open, R.string.close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mLeftNavDrawer.setDrawerListener(mNavToggle);
        mNavToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (mNavToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mNavToggle.onConfigurationChanged(newConfig);
    }

}
