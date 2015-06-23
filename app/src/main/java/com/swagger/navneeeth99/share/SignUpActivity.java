package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class SignUpActivity extends Activity {

    private EditText mNameET;
    private EditText mEmailET;
    private EditText mPasswordET;
    private EditText mConfirmPasswordET;
    private Button mSubmitButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mNameET = (EditText) findViewById(R.id.usernameEditText);
        mEmailET = (EditText) findViewById(R.id.emailEditText);
        mPasswordET = (EditText) findViewById(R.id.passwordEditText);
        mConfirmPasswordET = (EditText) findViewById(R.id.confirmPasswordEditText);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(cancelIntent);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mNameET.getText().toString().equals("")&& !mEmailET.getText().toString().equals("") && !mPasswordET.getText().toString().equals("") && !mConfirmPasswordET.getText().toString().equals("")) {
                    if (mPasswordET.getText().toString().equals(mConfirmPasswordET.getText().toString())) {
                        createUser(mNameET.getText().toString(), mEmailET.getText().toString(), mPasswordET.getText().toString());
                    } else {
                        Toast.makeText(SignUpActivity.this, getString(R.string.pw_not_same), Toast.LENGTH_LONG).show();
                        mPasswordET.setText("");
                        mConfirmPasswordET.setText("");
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.full_fields), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createUser(String username, String email, String password) {
        final ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.put("lowerUsername", username.toLowerCase());
        newUser.put("status", "Hi! I'm a newt.");
        newUser.put("friends", new ArrayList<String>());
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
                    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    // get byte array here
                    byte[] bytearray = stream.toByteArray();
                    ParseFile file = new ParseFile("profilepic.jpg", bytearray);
                    newUser.put("profilepic", file);
                    newUser.saveInBackground();
                    // Success!
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String errorMsg;
                    switch(e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            errorMsg = "This username is taken.";
                            break;
                        case ParseException.INVALID_EMAIL_ADDRESS:
                            errorMsg = "This email is invalid.";
                            break;
                        case ParseException.EMAIL_TAKEN:
                            errorMsg = "This email is taken.";
                            break;
                        default:
                            errorMsg = e.getMessage();
                            break;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("Oops")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
    }


}
