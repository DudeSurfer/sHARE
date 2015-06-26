package com.swagger.navneeeth99.share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by Benjamin on 26/6/15.
 */
public class NotesDetailActivity extends BaseActivity {
    private Notes mChosenNote;
    private String mNotesTopic;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        super.onCreateDrawer();

        ParseQuery<Notes> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("objectId", getIntent().getStringExtra(NotesActivity.DISPLAY_DETAIL));
        query.getFirstInBackground(new GetCallback<Notes>() {
            @Override
            public void done(Notes notes, ParseException e) {
                mChosenNote = notes;
                mNotesTopic = mChosenNote.getTopic();

                //PUT ALL CODE HERE
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NotesActivity.DISPLAY_NOTES_DETAILS && resultCode == RESULT_OK){
            data.getData();
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
