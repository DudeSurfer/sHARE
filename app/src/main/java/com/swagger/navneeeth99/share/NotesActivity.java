package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileOutputStream;


public class NotesActivity extends BaseActivity {
    private Button mNewNotesButton;
    private String mChosenSubject;
    private String mChosenLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        super.onCreateDrawer();

        if (ParseUser.getCurrentUser() != null) {
            mNewNotesButton = (Button) findViewById(R.id.addNewNotesBT);
            mNewNotesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddNewNotesDialogFrag postDF = new AddNewNotesDialogFrag();
                    postDF.show(NotesActivity.this.getFragmentManager(), "save data");
                }
            });

            final ListView mNotesDisplay = (ListView)findViewById(R.id.notesListView);
            ParseQueryAdapter mNotesAdapter = new CustomNotesAdapter(this, mChosenSubject, mChosenLevel);
            mNotesDisplay.setAdapter(mNotesAdapter);

            mNotesDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    ParseFile mCurrentNotesPF = ((Notes)mNotesDisplay.getItemAtPosition(position)).getNotesData();
                    Uri uri = Uri.parse(mCurrentNotesPF.getUrl());
                    Intent Newintent = new Intent(Intent.ACTION_VIEW);
                    Newintent.setDataAndType(uri,((Notes)mNotesDisplay.getItemAtPosition(position)).getNotesType());
                    Newintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(Newintent);
                    }
                    catch (ActivityNotFoundException e){
                        Toast.makeText(NotesActivity.this, "No application available to view file", Toast.LENGTH_LONG).show();
                    }

                }
            });


            Spinner mLevelSpinner = (Spinner)findViewById(R.id.levelSpinner);
            ArrayAdapter<CharSequence> mLevelAdapter = ArrayAdapter.createFromResource(this, R.array.level_list, android.R.layout.simple_spinner_item);
            mLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mLevelSpinner.setAdapter(mLevelAdapter);
            mLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mChosenLevel = getResources().getStringArray(R.array.level_list)[position];
                    mNotesDisplay.setAdapter(new CustomNotesAdapter(NotesActivity.this, mChosenSubject, mChosenLevel));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Spinner mSubjectSpinner = (Spinner)findViewById(R.id.subjectSpinner);
            ArrayAdapter<CharSequence> mSubjectAdapter = ArrayAdapter.createFromResource(this, R.array.subject_list, android.R.layout.simple_spinner_item);
            mSubjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubjectSpinner.setAdapter(mSubjectAdapter);
            mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mChosenSubject = getResources().getStringArray(R.array.subject_list)[position];
                    mNotesDisplay.setAdapter(new CustomNotesAdapter(NotesActivity.this, mChosenSubject, mChosenLevel));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }



    public static class CustomNotesAdapter extends ParseQueryAdapter<Notes> {

        public CustomNotesAdapter(Context context, final String mChosenSubject, final String mChosenLevel) {
            super(context, new ParseQueryAdapter.QueryFactory<Notes>() {
                public ParseQuery<Notes> create() {
                    ParseQuery<Notes> query = new ParseQuery<>("Notes");
                    if (ParseUser.getCurrentUser() != null) {
                        query.whereEqualTo("subject", mChosenSubject);
                        query.whereEqualTo("level", mChosenLevel);
                    }
                    return query;
                }
            });
        }

        // Customize the layout by overriding getItemView
        @Override
        public View getItemView(Notes notesObject, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.list_notes, null);
            }

            super.getItemView(notesObject, v, parent);

            TextView mNotesTitle = (TextView)v.findViewById(R.id.titleTV);
            mNotesTitle.setText(notesObject.getTopic());

            TextView mNotesContributor = (TextView)v.findViewById(R.id.contributorTV);
            mNotesContributor.setText(notesObject.getContributor());

            ImageView mNotesTypeIndicator = (ImageView)v.findViewById(R.id.typeIndicatorIV);
            if (notesObject.getNotesType().contains("Image")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_collections_black_48dp);
            } else if (notesObject.getNotesType().contains("Document")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_document_icon);
            } else if (notesObject.getNotesType().contains("Audio")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_audio);
            } else if (notesObject.getNotesType().contains("Video")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_video);
            }

            return v;
        }

    }
}
