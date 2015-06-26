package com.swagger.navneeeth99.share;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.io.File;

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
                mDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseFile mCurrentNotesPF = (mChosenNote.getNotesData());
                        Uri uri = Uri.parse(mCurrentNotesPF.getUrl());
                        final long myDownloadReference;
                        BroadcastReceiver receiverDownloadComplete;
                        final DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setVisibleInDownloadsUi(true);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mCurrentNotesPF.getName());
                        request.allowScanningByMediaScanner();
                        myDownloadReference = downloadManager.enqueue(request);
                        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        receiverDownloadComplete = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                if (myDownloadReference == reference){
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(reference);
                                    Cursor cursor = downloadManager.query(query);
                                    cursor.moveToFirst();
                                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                    Uri downloadedUri2 = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                                    Log.d("java", downloadedUri2.toString());
                                    Log.d("java", (mChosenNote.getNotesType()));
                                    switch (status){
                                        case DownloadManager.STATUS_SUCCESSFUL:
                                            File file = new File(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));

                                            if (file.exists()) {
                                                Uri path = Uri.fromFile(file);
                                                Log.d("java", path.toString());
                                                Log.d("java", NotesActivity.getMimeType(path.toString()));
                                                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                                                newIntent.setDataAndType(path, NotesActivity.getMimeType(path.toString()));
                                                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                try {
                                                    startActivity(newIntent);
                                                }
                                                catch (ActivityNotFoundException e) {
                                                    Toast.makeText(NotesDetailActivity.this, "No application available to view file!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            break;
                                        case DownloadManager.STATUS_FAILED:
                                            Toast.makeText(NotesDetailActivity.this, "Download failed", Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                    cursor.close();
                                }
                            }
                        };
                        registerReceiver(receiverDownloadComplete, intentFilter);
                    }
                });

                mPreviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseFile mCurrentNotesPF = (mChosenNote.getNotesData());
                        Uri PreviewUri = Uri.parse(mCurrentNotesPF.getUrl());
                        Intent PreviewIntent = new Intent(Intent.ACTION_VIEW);
                        PreviewIntent.setDataAndType(PreviewUri, mChosenNote.getNotesType());
                        PreviewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {
                            startActivity(PreviewIntent);
                        }
                        catch (ActivityNotFoundException e) {
                            Toast.makeText(NotesDetailActivity.this, "No application available to view file!", Toast.LENGTH_SHORT).show();
                        }
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
