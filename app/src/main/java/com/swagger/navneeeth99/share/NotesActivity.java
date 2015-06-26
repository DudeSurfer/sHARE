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
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
    public static final String DISPLAY_DETAIL = ".display";
    public static String WIDGET_ADD_NOTES = "nyb.add_notes.widget";
    private Button mNewNotesButton;
    private String mChosenSubject;
    private String mChosenLevel;
    private boolean isAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        if (ParseUser.getCurrentUser() != null) {
            super.onCreateDrawer();

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
                    Notes mCurrentNote = (Notes)mNotesDisplay.getItemAtPosition(position);
                    Intent detailIntent = new Intent(NotesActivity.this, NotesDetailActivity.class);
                    detailIntent.putExtra(DISPLAY_DETAIL, mCurrentNote.getObjectId());
                    startActivity(detailIntent);


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

            Intent intent = getIntent();
            isAdd = intent.getBooleanExtra(WIDGET_ADD_NOTES, false);
            if (isAdd) {
                AddNewNotesDialogFrag postDF = new AddNewNotesDialogFrag();
                postDF.show(NotesActivity.this.getFragmentManager(), "save data");
            }
        } else {
            Intent intent = new Intent(NotesActivity.this, LoginActivity.class);
            startActivity(intent);
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
            if (notesObject.getNotesType().contains("image")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_collections_black_48dp);
            } else if (notesObject.getNotesType().contains("application")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_document_icon);
            } else if (notesObject.getNotesType().contains("audio")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_audio_icon);
            } else if (notesObject.getNotesType().contains("video")){
                mNotesTypeIndicator.setImageResource(R.drawable.ic_video_icon);
            }

            return v;
        }

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
