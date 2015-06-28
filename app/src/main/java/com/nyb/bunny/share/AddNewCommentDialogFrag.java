package com.nyb.bunny.share;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Sangeetha on 27/6/2015.
 */
public class AddNewCommentDialogFrag extends DialogFragment {
    private Notes mChosenNote;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout mLL;
        LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
        mLL = (LinearLayout) mLayoutInflater.inflate(R.layout.fragment_newcomment, null);

        final EditText mCommentTitleET = (EditText)mLL.findViewById(R.id.commentTitleET);
        final RatingBar mCommentStarsRB = (RatingBar)mLL.findViewById(R.id.commentStarsRB);
        final EditText mCommentBodyET = (EditText)mLL.findViewById(R.id.commentBodyET);
        final String mCommentContributor = ParseUser.getCurrentUser().getUsername();

        String mChosenNoteID = getArguments().getString("id");
        ParseQuery<Notes> noteQuery = new ParseQuery<>("Notes");
        noteQuery.whereEqualTo("objectId", mChosenNoteID);
        noteQuery.getFirstInBackground(new GetCallback<Notes>() {
            @Override
            public void done(Notes notes, ParseException e) {
                mChosenNote = notes;
            }
        });

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("Add a new comment")
                .setContentView(mLL)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Comments newComment = new Comments();
                        newComment.setCContributor(mCommentContributor);
                        newComment.setCTitle(mCommentTitleET.getText().toString());
                        newComment.setCContent(mCommentBodyET.getText().toString());
                        newComment.setCStars(mCommentStarsRB.getRating());
                        newComment.saveInBackground();
                        mChosenNote.addNotesComments(newComment);
                        mChosenNote.saveInBackground();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
