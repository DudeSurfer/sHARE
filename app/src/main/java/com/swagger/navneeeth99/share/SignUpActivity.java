package com.swagger.navneeeth99.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends Activity {

    private EditText mNameET;
    private EditText mEmailET;
    private EditText mPasswordET;
    private EditText mConfirmPasswordET;
    private Button mSubmitButton;
    private Button mCancelButton;
    private Spinner mSchoolSpinner;
    private ArrayList<String> mSchools;
    private String mChosenSchool;
    private int mAddSchoolInt;
    private ParseObject mSchoolObject;

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
        mSchoolSpinner = (Spinner) findViewById(R.id.schoolSpinner);
        mSchools = new ArrayList<>();
        mChosenSchool = "";
        mAddSchoolInt = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("School");
        query.getInBackground("ZAoy56XiZ7", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    mSchoolObject = parseObject;
                    mSchools.addAll((List) parseObject.getList("List"));
                    final ArrayAdapter<String> mSchoolAdapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_item, mSchools);
                    mSchoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSchoolSpinner.setAdapter(mSchoolAdapter);
                    mSchoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == mSchools.size()-1) {
                                if (mAddSchoolInt < 2) {
                                    final EditText input = new EditText(SignUpActivity.this);
                                    input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    new CustomDialog.Builder(SignUpActivity.this)
                                            .setTitle("Add School")
                                            .setContentView(input)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    String value = input.getText().toString();
                                                    mSchools.add(mSchools.size() - 1, value);
                                                    mSchoolAdapter.notifyDataSetChanged();
                                                    mSchoolSpinner.setSelection(mSchools.size() - 2);
                                                    mAddSchoolInt++;
                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("School");
                                                    query.getInBackground("ZAoy56XiZ7", new GetCallback<ParseObject>() {
                                                        public void done(ParseObject schoolList, ParseException e) {
                                                            if (e == null) {
                                                                schoolList.put("List", mSchools);
                                                                schoolList.saveEventually();
                                                            }
                                                        }
                                                    });
                                                    ParseObject object = new ParseObject("SchoolItem");
                                                    object.put("name", value);
                                                    object.put("members", new ArrayList<String>());
                                                    try {
                                                        object.save();
                                                    } catch (ParseException e){

                                                    }
                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            // Do nothing.
                                        }
                                    }).create().show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Don't create unnecessary schools.", Toast.LENGTH_LONG).show();
                                }
                            } else if (position == 0) {
                                mChosenSchool = "";
                            } else {
                                mChosenSchool = mSchools.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            mChosenSchool = "";
                        }
                    });
                } else {
                    Log.e("OMG", "ParseException: " + e.toString());
                }
            }
        });

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
                if (!mNameET.getText().toString().equals("")&& !mEmailET.getText().toString().equals("") && !mChosenSchool.equals("") && !mPasswordET.getText().toString().equals("") && !mConfirmPasswordET.getText().toString().equals("")) {
                    if (mNameET.getText().toString().length() < 13) {
                        if (mPasswordET.getText().toString().equals(mConfirmPasswordET.getText().toString())) {
                            createUser(mNameET.getText().toString(), mEmailET.getText().toString(), mChosenSchool ,mPasswordET.getText().toString());
                        } else {
                            Toast.makeText(SignUpActivity.this, getString(R.string.pw_not_same), Toast.LENGTH_LONG).show();
                            mPasswordET.setText("");
                            mConfirmPasswordET.setText("");
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, getString(R.string.username_under_12), Toast.LENGTH_LONG).show();
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


        return super.onOptionsItemSelected(item);
    }

    private void createUser(final String username, String email, final String school, String password) {
        final ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.put("school", school);
        newUser.put("lowerUsername", username.toLowerCase());
        newUser.put("status", "This is my status.");
        newUser.put("friends", new ArrayList<String>());
        ParseQuery query = ParseQuery.getQuery("SchoolItem");
        query.whereEqualTo("name", school);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.e("ERROR", school + " does not exist.");
                } else {
                    object.put("members", object.getList("members").add(username));
                }
            }
        });
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    // get byte array here
                    byte[] bytearray = stream.toByteArray();
                    ParseFile file = new ParseFile("profilepic.jpg", bytearray);
                    newUser.put("profilepic", file);
                    newUser.saveInBackground();
                    // Success!
                    Intent intent = new Intent(SignUpActivity.this, HelpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String errorMsg;
                    switch (e.getCode()) {
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
