package com.swagger.navneeeth99.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class DiscussionActivity extends BaseActivity {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SimpleDateFormat df;
    ArrayList<ParseUser> mFriends;
    List<String> mMessages;
    List<String> mDates;
    ListView FriendsLV;
    ProgressBar mPB;
    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        super.onCreateDrawer();

        setTitle("Discussion");

        mFriends = new ArrayList<>();
        mMessages = new ArrayList<>();
        mDates = new ArrayList<>();
        df = new SimpleDateFormat("MMMM dd HH:mm");
        FriendsLV = (ListView) findViewById(R.id.FriendsLV);
        mPB = (ProgressBar) findViewById(R.id.ProgressBar);
        FriendsLV.setEmptyView(findViewById(R.id.empty_list_item));
        adapter = new UsersAdapter(DiscussionActivity.this, R.layout.user_list_item, mFriends);
        FriendsLV.setAdapter(adapter);
        FriendsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiscussionActivity.this, IndivChatActivity.class);
                intent.putExtra(IndivChatActivity.SEND_PARSE_USERNAME, mFriends.get(position).getUsername());
                startActivity(intent);
            }
        });
        mPB.setVisibility(View.VISIBLE);
        fillFriendList();

        Button mNewGrpButton = (Button)findViewById(R.id.newGrpBT);
        mNewGrpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void fillFriendList(){
        final List<String> mCurrentFriendNames = ParseUser.getCurrentUser().getList("friends");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(350);
        query.orderByAscending("lowerUsername");
        query.whereContainedIn("username", mCurrentFriendNames);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> results, ParseException e) {
                if (e == null) {
                    try {
                        Toast.makeText(DiscussionActivity.this, results.toString(), Toast.LENGTH_SHORT).show();
                        mFriends.clear();
                        mFriends.addAll(results);
                        Toast.makeText(DiscussionActivity.this, "List loaded.", Toast.LENGTH_SHORT).show();
                        fillMessageList();
                    } catch (NullPointerException e2) {
                    }
                } else {
                    Toast.makeText(DiscussionActivity.this, "Error. Try reloading.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fillMessageList(){
        mMessages = new ArrayList<>();
        mDates = new ArrayList<>();
        for (ParseUser pu: mFriends){
            String message = "";
            String date = "";
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatMessage");
            query.whereMatches("fromName", pu.getUsername());
            query.whereMatches("toName", ParseUser.getCurrentUser().getUsername());

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ChatMessage");
            query1.whereMatches("toName", pu.getUsername());
            query1.whereMatches("fromName", ParseUser.getCurrentUser().getUsername());

            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(query);
            queries.add(query1);

            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.orderByDescending("createdAt");
            try {
                if (mainQuery.getFirst().getString("fromName").equals(pu.getUsername())) {
                    String test = mainQuery.getFirst().getString("message");
                    if (test.length() < 18) {
                        message = ">" + pu.getUsername() + ": " + test;
                    } else {
                        message = ">" + pu.getUsername() + ": " + test.substring(0, 15) + "...";
                    }
                } else {
                    String test = mainQuery.getFirst().getString("message");
                    if (test.length() < 25) {
                        message = test;
                    } else {
                        message = test.substring(0, 22) + "...";
                    }
                }
                date = df.format(mainQuery.getFirst().getCreatedAt());
            } catch (ParseException e){
            }

            mMessages.add(message);
            mDates.add(date);
        }
        adapter.notifyDataSetChanged();
        mPB.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discussion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

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
            final TextView messageTV = (TextView) row.findViewById(R.id.secondLine);
            final TextView dateTV = (TextView) row.findViewById(R.id.dateTV);
            final ImageView profilePic = (ImageView) row.findViewById(R.id.icon);

            usernameTV.setText(mParseUser.getUsername());
            try {
                messageTV.setText(mMessages.get(position));
            } catch (IndexOutOfBoundsException e) {
                messageTV.setText("");
            }
            try {
                dateTV.setText(mDates.get(position));
            } catch (IndexOutOfBoundsException e) {
                dateTV.setText("");
            }
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
