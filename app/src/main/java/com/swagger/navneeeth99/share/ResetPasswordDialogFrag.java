package com.swagger.navneeeth99.share;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by Benjamin on 26/6/15.
 */
public class ResetPasswordDialogFrag extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout mLL;
        LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
        mLL = (LinearLayout)mLayoutInflater.inflate(R.layout.fragment_resetpassword, null);
        final EditText mResetEmailET = (EditText)mLL.findViewById(R.id.resetEmailET);

        final Context mContext = getActivity();

        // Use the Builder class for convenient dialog construction
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("Reset Password")
                .setContentView(mLL)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String mResetEmail = mResetEmailET.getText().toString();
                        ParseUser.requestPasswordResetInBackground(mResetEmail, new RequestPasswordResetCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    Toast.makeText(mContext,"Email sent!", Toast.LENGTH_LONG).show();
                                } else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS){
                                    Toast.makeText(mContext,"This email is invalid.", Toast.LENGTH_LONG).show();
                                } else if (e.getCode() == ParseException.EMAIL_NOT_FOUND){
                                    Toast.makeText(mContext,"This email is not in our database.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ResetPasswordDialogFrag.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }
}