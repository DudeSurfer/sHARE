package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseFile;
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
    private Button mUploadNotesBT;

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

        // Use the Builder class for convenient dialog construction
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("Add new notes!")
                .setContentView(mLL)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (notesData != null) {
                            Toast.makeText(getActivity(), "A PNG file was picked", Toast.LENGTH_SHORT).show();
                            Notes mNewNote = new Notes();
                            mNewNote.setContributor(ParseUser.getCurrentUser().getObjectId());
                            mNewNote.setSubject("Physics");
                            mNewNote.setLevel("Secondary 3");
                            mNewNote.setTopic("Kinematics");
                            mNewNote.setNotesData(notesData);
                            mNewNote.saveInBackground();
                        } else {
                            Toast.makeText(getActivity(), "A non-PNG file was picked/no file was picked", Toast.LENGTH_SHORT).show();
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
                mUploadNotesBT.setText(objectUri.toString());
                String mimeType = getActivity().getContentResolver().getType(objectUri);
                if (mimeType.equals("image/png")) {
                    notesData = new ParseFile("notes.png", inputData);
                } else {
                    Toast.makeText(getActivity(), "This is NOT an image :(", Toast.LENGTH_SHORT).show();
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
