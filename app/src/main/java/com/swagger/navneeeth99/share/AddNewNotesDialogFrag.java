package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by navneeeth99 on 24/6/15.
 */
public class AddNewNotesDialogFrag extends DialogFragment{
    public static int PICKFILE_REQUEST_CODE = 1;
    private ParseFile notesData;
    private String mFileType;
    private Button mUploadNotesBT;
    private String mLevelSelected;
    private String mSubjectSelected;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout mLL;
        LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
        mLL = (LinearLayout)mLayoutInflater.inflate(R.layout.fragment_newnotes, null);

        mUploadNotesBT = (Button)mLL.findViewById(R.id.uploadNotesBT);
        mUploadNotesBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        Spinner mLevelSpinner = (Spinner)mLL.findViewById(R.id.levelNote);
        ArrayAdapter<CharSequence> mLevelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.level_list, android.R.layout.simple_spinner_item);
        mLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLevelSpinner.setAdapter(mLevelAdapter);

        mLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLevelSelected = getResources().getStringArray(R.array.level_list)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner mSubjectSpinner = (Spinner)mLL.findViewById(R.id.subjectNote);
        ArrayAdapter<CharSequence> mSubjectAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subject_list, android.R.layout.simple_spinner_item);
        mSubjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSubjectSpinner.setAdapter(mSubjectAdapter);

        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubjectSelected = getResources().getStringArray(R.array.subject_list)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText mTopicET = (EditText)mLL.findViewById(R.id.topicNote);



        // Use the Builder class for convenient dialog construction
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("Add new notes!")
                .setContentView(mLL)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (notesData != null) {
                            Toast.makeText(getActivity(), "A PNG/doc/docx file was picked", Toast.LENGTH_SHORT).show();
                            Notes mNewNote = new Notes();
                            mNewNote.setContributor(ParseUser.getCurrentUser().getObjectId());
                            mNewNote.setContributorName(ParseUser.getCurrentUser().getUsername());
                            mNewNote.setSubject(mSubjectSelected);
                            mNewNote.setLevel(mLevelSelected);
                            mNewNote.setTopic(mTopicET.getText().toString());
                            mNewNote.setNotesData(notesData);
                            mNewNote.setNotesType(mFileType);
                            mNewNote.setNotesUpvoters(new ArrayList<String>());
                            mNewNote.setNotesDownvoters(new ArrayList<String>());
                            mNewNote.setReporters(new ArrayList<String>());
                            mNewNote.saveInBackground();
                        } else {
                            Toast.makeText(getActivity(), "No file was picked!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddNewNotesDialogFrag.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri objectUri = data.getData();
            InputStream iStream = null;
            byte[] inputData = null;
            try {
                iStream = getActivity().getContentResolver().openInputStream(objectUri);
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                inputData = getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputData != null){
                String mimeType = getActivity().getContentResolver().getType(objectUri);
                if (mimeType.equals("image/png")) {
                    notesData = new ParseFile("notes.png", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("image - png");
                } else if (mimeType.equals("application/msword")) {
                    notesData = new ParseFile("notes.doc", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("word doc");
                } else if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    notesData = new ParseFile("notes.docx", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("word docx");
                } else if (mimeType.equals("image/jpeg")) {
                    notesData = new ParseFile("notes.jpg", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("image - jpg");
                } else if (mimeType.equals("video/x-msvideo")) {
                    notesData = new ParseFile("notes.avi", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("avi video");
                } else if (mimeType.equals("video/mp4")) {
                    notesData = new ParseFile("notes.mp4", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("mp4 video");
                } else if (mimeType.equals("video/mpeg")) {
                    notesData = new ParseFile("notes.mpg", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("mpeg video");
                } else if (mimeType.equals("audio/mp4")) {
                    notesData = new ParseFile("notes.mp4a", inputData);
                    mUploadNotesBT.setText("audio - mp4a");
                    mFileType = mimeType;
                } else if (mimeType.equals("audio/mpeg")) {
                    notesData = new ParseFile("notes.mpga", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("audio - mpeg");
                } else if (mimeType.equals("application/pdf")) {
                    notesData = new ParseFile("notes.pdf", inputData);
                    mFileType = mimeType;
                    mUploadNotesBT.setText("pdf");
                } else {
                    Toast.makeText(getActivity(), "This filetype is not supported :(", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
