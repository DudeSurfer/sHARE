package com.nyb.bunny.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SchoolActivity extends BaseActivity {

    private ArrayList<ParseUser> mUsers;
    ListView UserLV;
    ProgressBar mPB;
    UsersAdapter adapter;
    ArrayList<String> mSchoolMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        super.onCreateDrawer();

        setTitle(ParseUser.getCurrentUser().getString("school"));

        mUsers = new ArrayList<>();

        UserLV = (ListView) findViewById(R.id.UsersLV);
        mPB = (ProgressBar) findViewById(R.id.ProgressBar);
        UserLV.setEmptyView(findViewById(R.id.empty_list_item));
        adapter = new UsersAdapter(SchoolActivity.this, R.layout.simple_user_list_item, mUsers);
        UserLV.setAdapter(adapter);
        UserLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SchoolActivity.this, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.ARG_USER_NAME, mUsers.get(position).getUsername());
                startActivity(intent);
            }
        });

        refresh();

    }

    public void refresh(){
        mSchoolMembers = new ArrayList<>();
        mPB.setVisibility(View.VISIBLE);
        ParseQuery query = ParseQuery.getQuery("SchoolItem");
        query.whereEqualTo("name", ParseUser.getCurrentUser().getString("school"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.e("ERROR", ParseUser.getCurrentUser().getString("school") + " does not exist.");
                } else {
                    mSchoolMembers.addAll((List) object.getList("members"));
                    ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                    query1.setLimit(1000);
                    query1.orderByAscending("lowerUsername");
                    query1.whereContainedIn("username", mSchoolMembers);
                    query1.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> results, ParseException e) {
                            if (e == null) {
                                try {
                                    mUsers.clear();
                                    mUsers.addAll(results);
                                    mUsers.remove(ParseUser.getCurrentUser());
                                    adapter.notifyDataSetChanged();
                                    mPB.setVisibility(View.GONE);
                                } catch (NullPointerException e2) {
                                    Toast.makeText(SchoolActivity.this, "Error with list 1", Toast.LENGTH_SHORT).show();
                                    mPB.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(SchoolActivity.this, "Our servers are busy. Or you have no internet connection.", Toast.LENGTH_SHORT).show();
                                mPB.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });


//            final String mQuery = MainActivity.mSearchQuery;
//            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
//            parseQuery.setLimit(1000);
//            parseQuery.orderByAscending("lowerUsername");
//            parseQuery.findInBackground(new FindCallback<ParseUser>() {
//                public void done(List<ParseUser> results, ParseException e) {
//                    if (e==null) {
//                        try {
//                            mUsers.clear();
//                            results.remove(ParseUser.getCurrentUser());
////                            Toast.makeText(getActivity(), "Hey! it's a search of " + MainActivity.mSearchQuery, Toast.LENGTH_SHORT).show();
////                            Toast.makeText(getActivity(), "this is query: " + mQuery.toLowerCase(Locale.ENGLISH), Toast.LENGTH_SHORT).show();
//                            for (int i = 0; i < results.size(); i++) {
//                                if ((results.get(i).getUsername().toLowerCase(Locale.ENGLISH)).startsWith(mQuery.toLowerCase(Locale.ENGLISH))) {
////                                    Toast.makeText(getActivity(), "lowercase is: " + results.get(i).getUsername().toLowerCase(Locale.ENGLISH), Toast.LENGTH_SHORT).show();
//                                    mUsers.add(results.get(i));
//                                }
//                            }
//                            adapter.notifyDataSetChanged();
//                            mPB.setVisibility(View.GONE);
//                        } catch (NullPointerException e2){
//                            Toast.makeText(getActivity(), "Error with list 1", Toast.LENGTH_SHORT).show();
//                            mPB.setVisibility(View.GONE);
//                        }
//                    } else{
//                        Toast.makeText(getActivity(), "Our servers are busy. Or you have no internet connection.", Toast.LENGTH_SHORT).show();
//                        mPB.setVisibility(View.GONE);
//                    }
//                }
//            });
//            MainActivity.mSearching = false;
//            MainActivity.mSearchQuery = "";


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_school, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private class UsersAdapter extends ArrayAdapter<ParseUser> {
        private int mResource;
        private ArrayList<ParseUser> mUsers;

        public UsersAdapter(Context context, int resource, ArrayList<ParseUser> users) {
            super(context, resource, users);
            mResource = resource;
            mUsers = users;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            if (row == null) {
                row = getLayoutInflater().inflate(mResource, parent, false);
            }

            ParseUser mParseUser = mUsers.get(position);
            final TextView usernameTV = (TextView) row.findViewById(R.id.firstLine);
            final TextView statusTV = (TextView) row.findViewById(R.id.secondLine);
            final ImageView profilePic = (ImageView) row.findViewById(R.id.icon);

            usernameTV.setText(mParseUser.getUsername());
            statusTV.setText(mParseUser.getString("status"));
            ParseFile pf = mParseUser.getParseFile("profilepic");
            pf.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap != null) {
                            profilePic.setImageBitmap(bitmap);
                        }
                    }
                }
            });


            return row;
        }
    }
}

