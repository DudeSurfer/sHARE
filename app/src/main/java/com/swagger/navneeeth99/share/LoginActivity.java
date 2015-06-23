package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.parse.LogInCallback;
//import com.parse.ParseException;
//import com.parse.ParseUser;


public class LoginActivity extends Activity {

//    private EditText mNameET;
//    private EditText mPasswordET;
//    private Button mLoginButton;
//    private Button mSignUpButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        mNameET = (EditText) findViewById(R.id.usernameEditText);
//        mPasswordET = (EditText) findViewById(R.id.passwordEditText);
//        mLoginButton = (Button) findViewById(R.id.loginButton);
//        mSignUpButton = (Button) findViewById(R.id.signUpButton);
//        mLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!mNameET.getText().toString().equals("") && !mPasswordET.getText().toString().equals("")) {
//                    ParseUser.logInInBackground(mNameET.getText().toString(), mPasswordET.getText().toString(), new LogInCallback() {
//                        @Override
//                        public void done(ParseUser parseUser, ParseException e) {
//                            setProgressBarIndeterminateVisibility(false);
//                            if (e == null) {
//                                // Success!
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                            } else {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                builder.setMessage(e.getMessage())
//                                        .setTitle("Sorry.")
//                                        .setPositiveButton(android.R.string.ok, null);
//                                AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
//                        }
//                    });
//                } else {
//                    Toast.makeText(LoginActivity.this, "Ensure all fields are filled in.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        mSignUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    public void onBackPressed() {
    }
}
