package com.nyb.bunny.share;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class NewGroupActivity extends ActionBarActivity {
    private ArrayList<String> mNewGrpMembers = new ArrayList<>();
    private ArrayList<String> mAllSchoolmates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        final ListView mAllUsersLV = (ListView)findViewById(R.id.allUserLV);
        final ListView mNewMembersLV = (ListView)findViewById(R.id.grpMembersLV);

        ParseQuery query = ParseQuery.getQuery("SchoolItem");
        query.whereEqualTo("name", ParseUser.getCurrentUser().getString("school"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.e("ERROR", ParseUser.getCurrentUser().getString("school") + " does not exist.");
                } else {
                    mAllSchoolmates.addAll((List)object.getList("members"));
                    mAllSchoolmates.remove(ParseUser.getCurrentUser().getUsername());
                    final SimpleUserAdapter mAllUsersAdapter = new SimpleUserAdapter(NewGroupActivity.this, R.layout.simplest_user_list_item, mAllSchoolmates);
                    mAllUsersLV.setAdapter(mAllUsersAdapter);
                    final SimpleUserAdapter mNewGrpMbrAdapter = new SimpleUserAdapter(NewGroupActivity.this, R.layout.simplest_user_list_item, mNewGrpMembers);
                    mNewMembersLV.setAdapter(mNewGrpMbrAdapter);
                    mAllUsersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mNewGrpMembers.add((String)mAllUsersLV.getItemAtPosition(position));
                            mAllSchoolmates.remove(mAllUsersLV.getItemAtPosition(position));
                            mAllUsersAdapter.notifyDataSetChanged();
                            mNewGrpMbrAdapter.notifyDataSetChanged();
                        }
                    });
                    mNewMembersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mAllSchoolmates.add((String)mNewMembersLV.getItemAtPosition(position));
                            mNewGrpMembers.remove(mNewMembersLV.getItemAtPosition(position));
                            mAllUsersAdapter.notifyDataSetChanged();
                            mNewGrpMbrAdapter.notifyDataSetChanged();
                        }
                    });

                    Button mSaveButton = (Button)findViewById(R.id.saveGroup);
                    mSaveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText mGrpNameET = (EditText)findViewById(R.id.grpNameET);
                            mNewGrpMembers.add(ParseUser.getCurrentUser().getUsername());

                            ParseObject newGroupChat = new ParseObject("GroupChat");
                            newGroupChat.put("title", mGrpNameET.getText().toString());
                            newGroupChat.put("members", mNewGrpMembers);
                            newGroupChat.saveInBackground();
                            Toast.makeText(NewGroupActivity.this, "Created new group " + mGrpNameET.getText().toString(), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(NewGroupActivity.this, DiscussionActivity.class);
                            startActivity(intent);
                        }
                    });

                    Button mCancelButton = (Button)findViewById(R.id.cancelGroup);
                    mCancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cancelIntent = new Intent(NewGroupActivity.this, DiscussionActivity.class);
                            startActivity(cancelIntent);
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
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

    private class SimpleUserAdapter extends ArrayAdapter<String> {
        private int mResource;
        private ArrayList<String> mUsers;

        public SimpleUserAdapter(Context context, int resource, ArrayList<String> users) {
            super(context, resource, users);
            mResource = resource;
            mUsers = users;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            if (row == null) {
                row = getLayoutInflater().inflate(mResource, parent, false);
            }

            final TextView usernameTV = (TextView) row.findViewById(R.id.simple_usrname);
            final ImageView profilePic = (ImageView) row.findViewById(R.id.simple_icon);

            ParseUser mParseUser;
            String mParseUserName = mUsers.get(position);
            ParseQuery currUserQuery = ParseUser.getQuery();
            currUserQuery.whereEqualTo("username", mParseUserName);
            try{
                mParseUser = (ParseUser)currUserQuery.getFirst();
                usernameTV.setText(mParseUserName);
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
            } catch (ParseException pE){
                pE.printStackTrace();
                Toast.makeText(NewGroupActivity.this, "Something went wrong! Data could not be loaded", Toast.LENGTH_LONG).show();
                usernameTV.setText(mParseUserName);
                Resources res = getResources();
                Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                profilePic.setImageBitmap(bitmap);
            }

            return row;
        }
    }
}
