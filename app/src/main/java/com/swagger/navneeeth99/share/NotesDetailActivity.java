package com.swagger.navneeeth99.share;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
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
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

/**
 * Created by Benjamin on 26/6/15.
 */
public class NotesDetailActivity extends BaseActivity {
    private Notes mChosenNote;
    private TextView mTopicTV;
    private TextView mFiletypeTV;
    private TextView mVotesTV;
    private ImageButton mPreviewButton;
    private ImageButton mDownloadButton;
    private Button mUpvoteBT;
    private Button mDownvoteBT;
    private ListView mCommentsLV;
    private Button mNewCommentBT;
    private ImageButton mReportButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        super.onCreateDrawer();

        mTopicTV = (TextView)findViewById(R.id.TopicTV);
        mFiletypeTV = (TextView)findViewById(R.id.FiletypeTV);
        mPreviewButton = (ImageButton)findViewById(R.id.previewButton);
        mDownloadButton = (ImageButton)findViewById(R.id.downloadButton);
        mUpvoteBT = (Button)findViewById(R.id.UpvoteBT);
        mDownvoteBT = (Button)findViewById(R.id.DownvoteBT);
        mCommentsLV = (ListView)findViewById(R.id.CommentsLV);
        mNewCommentBT = (Button)findViewById(R.id.NewCommentBT);
        mVotesTV = (TextView)findViewById(R.id.votesTV);
        mReportButton = (ImageButton)findViewById(R.id.reportBT);


        ParseQuery<Notes> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("objectId", getIntent().getStringExtra(NotesActivity.DISPLAY_DETAIL));
        query.getFirstInBackground(new GetCallback<Notes>() {
            @Override
            public void done(Notes notes, ParseException e) {
                mChosenNote = notes;
                mTopicTV.setText(mChosenNote.getTopic());
                mFiletypeTV.setText(notes.getNotesType());
                mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                mDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseFile mCurrentNotesPF = (mChosenNote.getNotesData());
                        Uri uri = Uri.parse(mCurrentNotesPF.getUrl());
                        final long myDownloadReference;
                        BroadcastReceiver receiverDownloadComplete;
                        final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
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
                                if (myDownloadReference == reference) {
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(reference);
                                    Cursor cursor = downloadManager.query(query);
                                    cursor.moveToFirst();
                                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                    Uri downloadedUri2 = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                                    Log.d("java", downloadedUri2.toString());
                                    Log.d("java", (mChosenNote.getNotesType()));
                                    switch (status) {
                                        case DownloadManager.STATUS_SUCCESSFUL:
                                            File file = new File(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));

                                            if (file.exists()) {
                                                Uri path = Uri.fromFile(file);
                                                Log.d("java", path.toString());
                                                Log.d("java", NotesDetailActivity.getMimeType(path.toString()));
                                                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                                                newIntent.setDataAndType(path, NotesDetailActivity.getMimeType(path.toString()));
                                                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                try {
                                                    startActivity(newIntent);
                                                } catch (ActivityNotFoundException e) {
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

                mNewCommentBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddNewCommentDialogFrag newCommentDialogFrag = new AddNewCommentDialogFrag();
                        Bundle args = new Bundle();
                        args.putString("id", mChosenNote.getObjectId());
                        newCommentDialogFrag.setArguments(args);
                        newCommentDialogFrag.show(NotesDetailActivity.this.getFragmentManager(), "add comment");
                    }
                });

                List<Comments> mChosenNoteComm = mChosenNote.getList("comments");
                if (mChosenNoteComm != null) {
                    mCommentsLV.setAdapter(new ArrayAdapter<Comments>(NotesDetailActivity.this, android.R.layout.simple_list_item_1, mChosenNoteComm));
                } else{
                    Toast.makeText(NotesDetailActivity.this, "mChosenNoteComm is null", Toast.LENGTH_SHORT).show();
                }

                if (mChosenNote.getNotesDownvoters().contains(ParseUser.getCurrentUser().getUsername())){
                    mDownvoteBT.setTextColor(Color.parseColor("#FFD15099"));
                }
                if (mChosenNote.getNotesUpvoters().contains(ParseUser.getCurrentUser().getUsername())){
                    mUpvoteBT.setTextColor(Color.parseColor("#FFD15099"));
                }



                mUpvoteBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChosenNote.getNotesUpvoters().contains(ParseUser.getCurrentUser().getUsername())) {
                            mChosenNote.removeNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mUpvoteBT.setTextColor(Color.parseColor("#d13d25"));
                            mChosenNote.saveInBackground();
                            mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        } else if (mChosenNote.getNotesDownvoters().contains(ParseUser.getCurrentUser().getUsername())){
                            mChosenNote.removeNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mChosenNote.addNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mDownvoteBT.setTextColor(Color.parseColor("#d13d25"));
                            mUpvoteBT.setTextColor(Color.parseColor("#FF38B1"));
                            mChosenNote.saveInBackground();
                            mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        } else {
                            mChosenNote.addNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mUpvoteBT.setTextColor(Color.parseColor("#FF38B1"));
                            mChosenNote.saveInBackground();
                            mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        }
                    }
                });

                mDownvoteBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChosenNote.getNotesDownvoters().contains(ParseUser.getCurrentUser().getUsername())) {
                            mChosenNote.removeNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mDownvoteBT.setTextColor(Color.parseColor("#d13d25"));
                            mChosenNote.saveInBackground();
                            mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        } else if (mChosenNote.getNotesUpvoters().contains(ParseUser.getCurrentUser().getUsername())){
                            mChosenNote.removeNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mChosenNote.addNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mUpvoteBT.setTextColor(Color.parseColor("#d13d25"));
                            mDownvoteBT.setTextColor(Color.parseColor("#FF38B1"));
                            mChosenNote.saveInBackground();
                            mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        } else {
                            mChosenNote.addNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mDownvoteBT.setTextColor(Color.parseColor("#FF38B1"));
                            mChosenNote.saveInBackground();
                            mVotesTV.setText("Votes: "+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        }
                    }
                });

                mReportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mChosenNote.getReporters().contains(ParseUser.getCurrentUser().getUsername())){
                            mChosenNote.addReporters(ParseUser.getCurrentUser().getUsername());
                            Toast.makeText(NotesDetailActivity.this,"Report sent!\nAdmins will review this note.",Toast.LENGTH_LONG).show();
                            mChosenNote.saveInBackground();
                        } else {
                            Toast.makeText(NotesDetailActivity.this,"Report has already been sent!",Toast.LENGTH_LONG).show();
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
