package com.swagger.navneeeth99.share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
    private TextView mTopicTV;
    private TextView mFiletypeTV;
    private TextView mUpvoteTV;
    private TextView mDownvoteTV;
    private ImageButton mPreviewButton;
    private ImageButton mDownloadButton;
    private ImageButton mUpvoteBT;
    private ImageButton mDownvoteBT;
    private ListView mCommentsLV;
    private EditText mNewCommentET;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        super.onCreateDrawer();

        mTopicTV = (TextView)findViewById(R.id.TopicTV);
        mFiletypeTV = (TextView)findViewById(R.id.FiletypeTV);
        mPreviewButton = (ImageButton)findViewById(R.id.previewButton);
        mDownloadButton = (ImageButton)findViewById(R.id.downloadButton);
        mUpvoteBT = (ImageButton)findViewById(R.id.UpvoteBT);
        mUpvoteTV = (TextView)findViewById(R.id.UpvoteTV);
        mDownvoteBT = (ImageButton)findViewById(R.id.DownvoteBT);
        mDownvoteTV = (TextView)findViewById(R.id.DownvoteTV);
        mCommentsLV = (ListView)findViewById(R.id.CommentsLV);
        mNewCommentET = (EditText)findViewById(R.id.NewCommentET);

        ParseQuery<Notes> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("objectId", getIntent().getStringExtra(NotesActivity.DISPLAY_DETAIL));
        query.getFirstInBackground(new GetCallback<Notes>() {
            @Override
            public void done(Notes notes, ParseException e) {
                mChosenNote = notes;
                mTopicTV.setText(mChosenNote.getTopic());
                mFiletypeTV.setText(notes.getNotesType());
                mPreviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                mDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                });
            }
        });
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
