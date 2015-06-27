package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class UserProfileActivity extends Activity {

    public static String ARG_USER_NAME = "nyanch.newtwork.username.send";
    private ImageView mProfilePic;
    private Button mFriendButton;
    private ImageButton mChatButton;
    private TextView mNameTV;
    private TextView mStatusTV;
    private ParseUser mParseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mStatusTV = (TextView) findViewById(R.id.statusTV);
        mNameTV = (TextView) findViewById(R.id.usernameTV);
        mProfilePic = (ImageView) findViewById(R.id.profilePic);
        mFriendButton = (Button) findViewById(R.id.friendButton);
        mChatButton = (ImageButton) findViewById(R.id.chatButton);
        mParseUser = new ParseUser();
        Intent intent = getIntent();
        final String username = intent.getStringExtra(ARG_USER_NAME);
        mNameTV.setText(username);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mParseUser = parseUser;
                String st = mParseUser.getString("status");
                if (st != "") {
                    mStatusTV.setText(st);
                }
                ParseFile pf = mParseUser.getParseFile("profilepic");
                pf.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            if (bitmap != null) {
                                mProfilePic.setImageBitmap(bitmap);
                            }
                        }
                        else {
                            // something went wrong
                        }
                    }});
            }
        });

        final List<String> mCurrentFriends = ParseUser.getCurrentUser().getList("friends");
        if (mCurrentFriends.contains(username)) { //if friends, unfriend.
            mChatButton.setVisibility(View.VISIBLE);
            mChatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfileActivity.this, IndivChatActivity.class);
                    intent.putExtra(IndivChatActivity.SEND_PARSE_USERNAME, username);
                    startActivity(intent);
                }
            });
            mFriendButton.setText("unfriend me!");
            mFriendButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     new CustomDialog.Builder(UserProfileActivity.this)
                                                             .setTitle("You sure?")
                                                             .setMessage("You are unfriending.")
                                                             .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                 public void onClick(DialogInterface dialog, int whichButton) {
                                                                     try {
                                                                         if (mCurrentFriends.contains(username)) {
                                                                             mCurrentFriends.remove(username);
                                                                             ParseUser.getCurrentUser().put("friends", mCurrentFriends);
                                                                             removeFriendForOther();
                                                                             ParseUser.getCurrentUser().saveInBackground();
                                                                         } else {
                                                                             Toast.makeText(UserProfileActivity.this, "You are not friends. Oops!", Toast.LENGTH_SHORT).show();
                                                                         }
                                                                     } catch (NullPointerException e2) {
                                                                     }
                                                                 }
                                                             }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                         public void onClick(DialogInterface dialog, int whichButton) {
                                                             // Do nothing.
                                                         }
                                                     }).create().show();
                                                 }
                                             }
            );
        } else { //if not friends, allow friending.
            mChatButton.setVisibility(View.GONE);
            mFriendButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     new CustomDialog.Builder(UserProfileActivity.this)
                                                             .setTitle("You sure?")
                                                             .setMessage("You are friending.")
                                                             .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                 public void onClick(DialogInterface dialog, int whichButton) {

                                                                     try {
                                                                         List<String> mCurrentFriends = ParseUser.getCurrentUser().getList("friends");
                                                                         if (!mCurrentFriends.contains(username)) {
                                                                             mCurrentFriends.add(username);
                                                                             ParseUser.getCurrentUser().put("friends", mCurrentFriends);
//                                                                         Toast.makeText(UserProfileActivity.this, "adding friends for other", Toast.LENGTH_SHORT).show();
                                                                             addFriendForOther();
                                                                             ParseUser.getCurrentUser().saveInBackground();
                                                                         } else {
                                                                             Toast.makeText(UserProfileActivity.this, "You are already friends!", Toast.LENGTH_LONG).show();
                                                                         }
                                                                     } catch (NullPointerException e2) {
                                                                         List<String> mCurrentFriends = new ArrayList<>();
                                                                         mCurrentFriends.add(username);
                                                                         ParseUser.getCurrentUser().put("friends", mCurrentFriends);
                                                                         ParseUser.getCurrentUser().saveInBackground();
                                                                         addFriendForOther();
                                                                     }

                                                                 }
                                                             }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                         public void onClick(DialogInterface dialog, int whichButton) {
                                                             // Do nothing.
                                                         }
                                                     }).create().show();
                                                 }
                                             }
            );
        }
    }

    private void addFriendForOther (){
        try {
//            Toast.makeText(UserProfileActivity.this, mParseUser.getUsername(), Toast.LENGTH_SHORT).show();
            List<String> mOtherFriends = mParseUser.getList("friends");
            mOtherFriends.add(ParseUser.getCurrentUser().getUsername());
            mParseUser.put("friends", mOtherFriends);
            mParseUser.saveInBackground();
//            Toast.makeText(UserProfileActivity.this, "saving now", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e2) {
            List<String> mOtherFriends = new ArrayList<>();
            mOtherFriends.add(ParseUser.getCurrentUser().getUsername());
            mParseUser.put("friends", mOtherFriends);
            mParseUser.saveInBackground();
        }
    }

    private void removeFriendForOther (){
        try {
//            Toast.makeText(UserProfileActivity.this, mParseUser.getUsername(), Toast.LENGTH_SHORT).show();
            List<String> mOtherFriends = mParseUser.getList("friends");
            mOtherFriends.remove(ParseUser.getCurrentUser().getUsername());
            mParseUser.put("friends", mOtherFriends);
            mParseUser.saveInBackground();
//            Toast.makeText(UserProfileActivity.this, "saving now", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e2) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
