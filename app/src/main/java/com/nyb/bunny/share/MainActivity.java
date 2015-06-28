package com.nyb.bunny.share;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("sHARE");

        final ListView mPrivateLV = (ListView)findViewById(R.id.privateLV);
        mPrivateLV.setEmptyView(findViewById(R.id.private_empty_list_item));
        final ListView mGroupLV = (ListView)findViewById(R.id.groupLV);
        mGroupLV.setEmptyView(findViewById(R.id.group_empty_list_item));

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            super.onCreateDrawer();
            ImageButton mAddButton = (ImageButton) findViewById(R.id.addButton);
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                    intent.putExtra(NotesActivity.WIDGET_ADD_NOTES, true);
                    startActivity(intent);
                }
            });

            ParseQuery mPMsgUnread = new ParseQuery("ChatMessage");
            mPMsgUnread.whereEqualTo("read", false);
            mPMsgUnread.whereNotEqualTo("fromName", ParseUser.getCurrentUser().getUsername());
            mPMsgUnread.whereEqualTo("toName", ParseUser.getCurrentUser().getUsername());
            mPMsgUnread.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Log.d("arrays", list.toString());
                    ArrayList<String> mUnreadUsers = new ArrayList<>();
                    for (ParseObject pmsgObj : list) {
                        mUnreadUsers.add(pmsgObj.getString("fromName"));
                    }
                    mUnreadUsers = new ArrayList<String>(new LinkedHashSet<String>(mUnreadUsers));
                    UnreadUserAdapter mPrivateAdapter = new UnreadUserAdapter(MainActivity.this, R.layout.simplest_user_list_item, mUnreadUsers);
                    mPrivateLV.setAdapter(mPrivateAdapter);
                }
            });

            final ParseQuery<ParseObject> mGMsgUnread = new ParseQuery<>("GroupChat");
            mGMsgUnread.whereEqualTo("members", ParseUser.getCurrentUser().getUsername());
            mGMsgUnread.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    final ArrayList<String> mUnreadGroups = new ArrayList<>();
                    if (list != null) {
                        Log.d("list", list.toString());
                        for (final ParseObject gmsgObj : list) {
                            ParseQuery<ParseObject> mGMsg = new ParseQuery<>("GrpChatMessage");
                            mGMsg.whereEqualTo("toGroup", gmsgObj.getString("title"));
                            mGMsg.whereNotEqualTo("readBy", ParseUser.getCurrentUser().getUsername());
                            mGMsg.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list2, ParseException e) {
                                    Log.d("list", list2.toString());
                                    for (ParseObject msgObj : list2) {
                                        if (!msgObj.getList("readBy").contains(ParseUser.getCurrentUser().getUsername())) {
                                            mUnreadGroups.add(gmsgObj.getString("title"));
                                        }
                                        ArrayList<String> mNoRepeatUnreadGroups = new ArrayList<>(new LinkedHashSet<>(mUnreadGroups));
                                        Log.d("list", mNoRepeatUnreadGroups.toString() + " list size: " + mNoRepeatUnreadGroups.size());
                                        UnreadGroupAdapter mGroupAdapter = new UnreadGroupAdapter(MainActivity.this, R.layout.simplest_user_list_item, mNoRepeatUnreadGroups);
                                        mGroupLV.setAdapter(mGroupAdapter);
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d("list", "List is null!!!!!");
                    }
                }
            });

            mPrivateLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, IndivChatActivity.class);
                    intent.putExtra(IndivChatActivity.SEND_PARSE_USERNAME, (String) mPrivateLV.getItemAtPosition(position));
                    startActivity(intent);
                }
            });

            mGroupLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, IndivGrpChatActivity.class);
                    intent.putExtra("title", (String) mGroupLV.getItemAtPosition(position));
                    startActivity(intent);
                }
            });
        }
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

        return super.onOptionsItemSelected(item);
    }

    private class UnreadUserAdapter extends ArrayAdapter<String> {
        private int mResource;
        private ArrayList<String> mUsers;

        public UnreadUserAdapter(Context context, int resource, ArrayList<String> users) {
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
                Toast.makeText(MainActivity.this, "Something went wrong! Data could not be loaded", Toast.LENGTH_LONG).show();
                usernameTV.setText(mParseUserName);
                Resources res = getResources();
                Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                profilePic.setImageBitmap(bitmap);
            }

            return row;
        }
    }

    private class UnreadGroupAdapter extends ArrayAdapter<String> {
        private int mResource;
        private ArrayList<String> mGroups;

        public UnreadGroupAdapter(Context context, int resource, ArrayList<String> groups) {
            super(context, resource, groups);
            mResource = resource;
            mGroups = groups;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            if (row == null) {
                row = getLayoutInflater().inflate(mResource, parent, false);
            }

            final TextView usernameTV = (TextView) row.findViewById(R.id.simple_usrname);
            usernameTV.setText(mGroups.get(position));

            final ImageView profilePic = (ImageView) row.findViewById(R.id.simple_icon);
            profilePic.setVisibility(View.GONE);

            return row;
        }
    }

}
