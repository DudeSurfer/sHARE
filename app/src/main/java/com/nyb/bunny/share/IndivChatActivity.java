package com.nyb.bunny.share;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class IndivChatActivity extends ActionBarActivity {

    public static String SEND_PARSE_USERNAME = "newtwork.nyanch.parseuser.send";
    private EditText mMessageET;
    private ListView mChatLV;
    private ProgressBar mProgressBar;
    private Button mSubmitButton;
    private ParseUser mParseUser;
    private ParseUser mCurrentUser;
    private String mCurrentName;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indiv_chat);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mChatLV = (ListView) findViewById(R.id.ChatLV);
        mMessageET = (EditText) findViewById(R.id.messageET);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentName = ParseUser.getCurrentUser().getUsername();
        Intent intent = getIntent();
        String mUsername = intent.getStringExtra(SEND_PARSE_USERNAME);
        setTitle(mUsername);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", mUsername);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mParseUser = parseUser;
                try {
                    ParseFile pf = mParseUser.getParseFile("profilepic");
                    pf.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                if (bitmap != null) {
                                    Resources res = getResources();
                                    BitmapDrawable icon = new BitmapDrawable(res,
                                            makeCircle(bitmap));
                                    getSupportActionBar().setIcon(icon);
                                }
                            } else {
                                // something went wrong
                            }
                        }
                    });
                    refresh();
                }
                catch (NullPointerException e2) {

                }
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        final List<ParseObject> mMessages = new ArrayList<>();
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ChatMessage");
        query1.setLimit(10000);
        query1.orderByAscending("createdAt");
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        try {
                            if (((object.get("fromName").equals(mCurrentName)) &&
                                    (object.get("toName").equals(mParseUser.getUsername()))) ||
                                    ((object.get("fromName").equals(mParseUser.getUsername())) &&
                                            (object.get("toName").equals(mCurrentName)))) {
                                mMessages.add(object);
                            }
                        } catch (NullPointerException e2){}
                    }
                    try {
                        mSubmitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!mMessageET.getText().toString().equals("")){
                                    ParseObject chatMessage= new ParseObject("ChatMessage");
                                    chatMessage.put("toName", mParseUser.getUsername());
                                    chatMessage.put("fromName", mCurrentName);
                                    chatMessage.put("message", mMessageET.getText().toString());
                                    chatMessage.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(IndivChatActivity.this, "Sent.", Toast.LENGTH_SHORT).show();
                                            refresh();
                                        }
                                    });
                                    mMessageET.setText("");
                                }
                            }
                        });
                        adapter = new ChatAdapter(IndivChatActivity.this, R.layout.message_to_item, mMessages);
                        mChatLV.setAdapter(adapter);

                        mProgressBar.setVisibility(View.GONE);
                    } catch (NullPointerException e2) {
                    }
                } else{
                    Toast.makeText(IndivChatActivity.this, "There's an error. Try reloading.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });



    }
    private void refresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        final List<ParseObject> mMessages = new ArrayList<>();
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ChatMessage");
        query1.setLimit(10000);
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        if (((object.get("fromName").equals(mCurrentName)) &&
                                (object.get("toName").equals(mParseUser.getUsername()))) ||
                                ((object.get("fromName").equals(mParseUser.getUsername())) &&
                                        (object.get("toName").equals(mCurrentName)))) {
                            mMessages.add(object);
                        }
                    }
                    try {
                        adapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.GONE);
                    } catch (NullPointerException e2) {
                        Toast.makeText(IndivChatActivity.this,"There's an error. Try reloading.", Toast.LENGTH_SHORT).show();
                        e2.printStackTrace();
                    }
                } else {
                    Toast.makeText(IndivChatActivity.this,"There's an error. Try reloading.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_indiv_chat, menu);
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

    private class ChatAdapter extends ArraySwipeAdapter<ParseObject> {
        private int mResource;
        private List<ParseObject> mParseObjects;

        public ChatAdapter(Context context, int resource, List<ParseObject> messages){
            super(context, resource, messages);
            mResource = resource;
            mParseObjects = messages;
        }

        public int getSwipeLayoutResourceId(int position){
            return mResource;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent){
            SimpleDateFormat df = new SimpleDateFormat("MMMM dd HH:mm");
            final ParseObject currentParseObject = mParseObjects.get(position);
            if(currentParseObject.get("fromName").equals(mCurrentUser.getUsername())) {
                row = getLayoutInflater().inflate(R.layout.message_to_item, parent, false);
                ImageButton deleteButton = (ImageButton) row.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParseObjects.remove(currentParseObject);
                        currentParseObject.deleteInBackground();
                        notifyDataSetChanged();
                    }
                });
            }
            else{
                row = getLayoutInflater().inflate(R.layout.message_from_item, parent, false);
            }
            TextView dateTV = (TextView) row.findViewById(R.id.dateTV);
            dateTV.setText(df.format(currentParseObject.getCreatedAt()));
            TextView messageItem= (TextView) row.findViewById(R.id.messageTV);
            messageItem.setText(currentParseObject.getString("message"));


            return row;
        }
    }

    private Bitmap makeCircle(Bitmap bitmapimg){
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }
}
