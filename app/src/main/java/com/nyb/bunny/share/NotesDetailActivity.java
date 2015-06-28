package com.nyb.bunny.share;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
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
    private TextView mContributorTV;
    private TextView mVotesTV;
    private ImageButton mPreviewButton;
    private ImageButton mDownloadButton;
    private ImageButton mUpvoteBT;
    private ImageButton mDownvoteBT;
    private ListView mCommentsLV;
    private Button mNewCommentBT;
    private ImageButton mReportButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        super.onCreateDrawer();

        setTitle("Notes");
        mTopicTV = (TextView)findViewById(R.id.TopicTV);
        mFiletypeTV = (TextView)findViewById(R.id.FiletypeTV);
        mContributorTV = (TextView) findViewById(R.id.ContributorTV);
        mPreviewButton = (ImageButton)findViewById(R.id.previewButton);
        mDownloadButton = (ImageButton)findViewById(R.id.downloadButton);
        mUpvoteBT = (ImageButton)findViewById(R.id.UpvoteBT);
        mDownvoteBT = (ImageButton)findViewById(R.id.DownvoteBT);
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
                mContributorTV.setText(mChosenNote.getContributorName());
                mTopicTV.setText(mChosenNote.getTopic());
                mVotesTV.setText("" + (mChosenNote.getNotesUpvoters().size()-mChosenNote.getNotesDownvoters().size()));
                mCommentsLV.setEmptyView(findViewById(R.id.empty_list_item));

                if (mChosenNote.getContributor().equals(ParseUser.getCurrentUser().getUsername())){
                    mReportButton.setBackgroundResource(R.drawable.ic_delete);
                }

                switch(mChosenNote.getNotesType()){
                    case "image/png":{
                        mFiletypeTV.setText("Picture (.png)");
                        break;
                    }
                    case "application/msword":{
                        mFiletypeTV.setText("Document (.doc)");
                        break;
                    }
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":{
                        mFiletypeTV.setText("Document (.docx)");
                        break;
                    }
                    case "image/jpeg":{
                        mFiletypeTV.setText("Image (.jpg)");
                        break;
                    }
                    case "video/x-msvideo":{
                        mFiletypeTV.setText("Video (.avi)");
                        break;
                    }
                    case "video/mp4":{
                        mFiletypeTV.setText("Video (.mp4)");
                        break;
                    }
                    case "video/mpeg":{
                        mFiletypeTV.setText("Video (.mpg)");
                        break;
                    }
                    case "audio/mp4":{
                        mFiletypeTV.setText("Audio (.mp4a)");
                        break;
                    }
                    case "audio/mpeg":{
                        mFiletypeTV.setText("Audio (.mpga)");
                        break;
                    }
                    case "application/pdf":{
                        mFiletypeTV.setText("Document (.pdf)");
                        break;
                    }
                }

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
                    mCommentsLV.setAdapter(new CommentAdapter(NotesDetailActivity.this, R.layout.comment_item, mChosenNoteComm));
                } else{
//                    Toast.makeText(NotesDetailActivity.this, "mChosenNoteComm is null", Toast.LENGTH_SHORT).show();
                }

                if (mChosenNote.getNotesDownvoters().contains(ParseUser.getCurrentUser().getUsername())){
                    mDownvoteBT.setBackgroundResource(R.drawable.light_red_bg);
                }
                if (mChosenNote.getNotesUpvoters().contains(ParseUser.getCurrentUser().getUsername())){
                    mUpvoteBT.setBackgroundResource(R.drawable.light_red_bg);
                }

                mUpvoteBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChosenNote.getNotesUpvoters().contains(ParseUser.getCurrentUser().getUsername())) {
                            mChosenNote.removeNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mUpvoteBT.setBackgroundResource(R.drawable.red_bg);
                            mChosenNote.saveInBackground();
                            mVotesTV.setText(""+(mChosenNote.getNotesUpvoters().size()-mChosenNote.getNotesDownvoters().size()));
                        } else if (mChosenNote.getNotesDownvoters().contains(ParseUser.getCurrentUser().getUsername())){
                            mChosenNote.removeNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mChosenNote.addNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mDownvoteBT.setBackgroundResource(R.drawable.red_bg);
                            mUpvoteBT.setBackgroundResource(R.drawable.light_red_bg);
                            mChosenNote.saveInBackground();
                            mVotesTV.setText(""+(mChosenNote.getNotesUpvoters().size()-mChosenNote.getNotesDownvoters().size()));
                        } else {
                            mChosenNote.addNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mUpvoteBT.setBackgroundResource(R.drawable.light_red_bg);
                            mChosenNote.saveInBackground();
                            mVotesTV.setText(""+(mChosenNote.getNotesUpvoters().size()-mChosenNote.getNotesDownvoters().size()));
                        }
                    }
                });

                mDownvoteBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChosenNote.getNotesDownvoters().contains(ParseUser.getCurrentUser().getUsername())) {
                            mChosenNote.removeNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mDownvoteBT.setBackgroundResource(R.drawable.red_bg);
                            mChosenNote.saveInBackground();
                            mVotesTV.setText(""+(mChosenNote.getNotesDownvoters().size()-mChosenNote.getNotesUpvoters().size()));
                        } else if (mChosenNote.getNotesUpvoters().contains(ParseUser.getCurrentUser().getUsername())){
                            mChosenNote.removeNotesUpvoter(ParseUser.getCurrentUser().getUsername());
                            mChosenNote.addNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mUpvoteBT.setBackgroundResource(R.drawable.red_bg);
                            mDownvoteBT.setBackgroundResource(R.drawable.light_red_bg);
                            mChosenNote.saveInBackground();
                            mVotesTV.setText(""+(mChosenNote.getNotesUpvoters().size()-mChosenNote.getNotesDownvoters().size()));
                        } else {
                            mChosenNote.addNotesDownvoter(ParseUser.getCurrentUser().getUsername());
                            mDownvoteBT.setBackgroundResource(R.drawable.light_red_bg);
                            mChosenNote.saveInBackground();
                            mVotesTV.setText(""+(mChosenNote.getNotesUpvoters().size()-mChosenNote.getNotesDownvoters().size()));
                        }
                    }
                });

                mReportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChosenNote.getContributor().equals(ParseUser.getCurrentUser().getUsername())){
                            new CustomDialog.Builder(NotesDetailActivity.this)
                                    .setTitle("Delete your app?")
                                    .setMessage("Deleting is permanenet")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            mChosenNote.deleteInBackground();
                                            Toast.makeText(NotesDetailActivity.this, "Note deleted", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(NotesDetailActivity.this, NotesActivity.class);
                                            startActivity(intent);
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                                    .create()
                                    .show();
                        } else {
                            new CustomDialog.Builder(NotesDetailActivity.this)
                                    .setTitle("Report this note as poor?")
                                    .setMessage("Reporting is serious.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            if (!mChosenNote.getReporters().contains(ParseUser.getCurrentUser().getUsername())){
                                                mChosenNote.addReporters(ParseUser.getCurrentUser().getUsername());
                                                Toast.makeText(NotesDetailActivity.this,"Report sent!\nAdmins will review this note.",Toast.LENGTH_LONG).show();
                                                mChosenNote.saveInBackground();
                                            } else {
                                                Toast.makeText(NotesDetailActivity.this,"Report has already been sent!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                                    .create()
                                    .show();
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

    private class CommentAdapter extends ArrayAdapter<Comments> {
        private int mResource;
        private List<Comments> mComments;

        public CommentAdapter(Context context, int resource, List<Comments> comments) {
            super(context, resource, comments);
            mResource = resource;
            mComments = comments;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            if (row == null) {
                row = getLayoutInflater().inflate(mResource, parent, false);
            }

            ParseUser mParseUser = null;
            Comments mThisComment = mComments.get(position);
            final TextView titleTV = (TextView) row.findViewById(R.id.firstLine);
            final TextView descrTV = (TextView) row.findViewById(R.id.secondLine);
            final RatingBar mRB = (RatingBar) row.findViewById(R.id.mRB);
            final ImageView profilePic = (ImageView) row.findViewById(R.id.icon);


            mRB.setRating(mThisComment.getCStars());
            LayerDrawable stars = (LayerDrawable) mRB.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(0xffd13d25, PorterDuff.Mode.SRC_ATOP);
            titleTV.setText(mThisComment.getCTitle());
            descrTV.setText(mThisComment.getCContent());
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", mThisComment.getCContributor());
            try {
                mParseUser = query.getFirst();
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
            } catch (Exception e){
            }



            return row;
        }
    }
}
