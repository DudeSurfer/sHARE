package com.swagger.navneeeth99.share;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
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
                    final ArrayAdapter mAllUsersAdapter = new ArrayAdapter<>(NewGroupActivity.this, android.R.layout.simple_list_item_1, mAllSchoolmates);
                    mAllUsersLV.setAdapter(mAllUsersAdapter);
                    final ArrayAdapter mNewGrpMbrAdapter = new ArrayAdapter<>(NewGroupActivity.this, android.R.layout.simple_list_item_1, mNewGrpMembers);
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
}
