package com.swagger.navneeeth99.share;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class BaseActivity extends ActionBarActivity {
    private String[] mNavChoices;
    private DrawerLayout mLeftNavDrawer;
    private android.support.v4.app.ActionBarDrawerToggle mNavToggle;
    private ListView mLeftNavList;
    private Context mContext;

    protected void onCreateDrawer () {
        mContext = this;
        mNavChoices = getResources().getStringArray(R.array.navdrawer_items);
        mLeftNavDrawer = (DrawerLayout)findViewById(R.id.side_nav);
        mLeftNavList = (ListView)findViewById(R.id.drawer_list);
        final View header = getLayoutInflater().inflate(R.layout.sidenav_header, null);
        mLeftNavList.addHeaderView(header, "profile summary", false);
        mLeftNavList.setAdapter(new MySimpleArrayAdapter(this, mNavChoices));
        mLeftNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mSelectedDest = (String) mLeftNavList.getItemAtPosition(position);
                switch (mSelectedDest) {
                    case "Home": {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Profile": {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Notes": {
                        Intent intent = new Intent(mContext, NotesActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Discussion": {
                        Intent intent = new Intent(mContext, DiscussionActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "School": {
                        Intent intent = new Intent(mContext, SchoolActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Settings": {
                        Intent intent = new Intent(mContext, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Chat": {
                        Intent intent = new Intent(mContext, IndivChatActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Help": {
                        Intent intent = new Intent(mContext, HelpActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Logout": {
                        new CustomDialog.Builder(BaseActivity.this)
                                .setTitle("Are you sure?")
                                .setMessage("You are logging out.")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        ParseUser.logOut();
                                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                                .create()
                                .show();
                    }
                }
            }
        });
        TypefacedTextView nameTV = (TypefacedTextView)header.findViewById(R.id.nameTextView);
        if (nameTV != null) {
            nameTV.setText(ParseUser.getCurrentUser().getUsername());
        }
        ParseFile pf = ParseUser.getCurrentUser().getParseFile("profilepic");
        pf.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) {
                        ((RoundedImageView)header.findViewById(R.id.profilePicImageView)).setImageBitmap(bitmap);
                    }
                }
                else {
                    // something went wrong
                }
            }});

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mLeftNavDrawer, R.drawable.hare, R.string.open, R.string.close) {
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
        if (ParseUser.getCurrentUser() != null) {
            mNavToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mNavToggle.onConfigurationChanged(newConfig);
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            String mSelectedDest = values[position];
            textView.setText(values[position]);
            switch (mSelectedDest) {
                case "Home":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_white_24dp));
                    break;
                case "Profile":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_white_24dp));
                    break;
                case "Notes":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_description_white_24dp));
                    break;
                case "Discussion":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_white_24dp));
                    break;
                case "School":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_school_white));
                    break;
                case "Help":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_white_24dp));
                    break;
                case "Settings":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_white_24dp));
                    break;
                case "Logout":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_exit_to_app_white_24dp));
                    break;
                default:
                    imageView.setVisibility(View.GONE);
                    break;
            }

            return rowView;
        }
    }

}
