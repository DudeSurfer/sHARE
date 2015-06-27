package com.swagger.navneeeth99.share;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;


public class ProfileActivity extends BaseActivity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private Button mLogoutButton;
    private ImageButton mStatusButton;
    private ImageButton mCameraButton;
    private ImageButton mGalleryButton;
    private ImageView mPreview;
    private TextView mStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        super.onCreateDrawer();

        setTitle(ParseUser.getCurrentUser().getUsername());
//        mLogoutButton = (Button) findViewById(R.id.logOutButton);
        mCameraButton = (ImageButton) findViewById(R.id.cameraButton);
        mGalleryButton = (ImageButton) findViewById(R.id.galleryButton);
        mStatusButton = (ImageButton) findViewById(R.id.statusButton);
        mStatusTV = (TextView) findViewById(R.id.statusTV);
        mPreview = (ImageView) findViewById(R.id.profilePic);

        String st = ParseUser.getCurrentUser().getString("status");
        if (st != "") {
            mStatusTV.setText(st);
        }

        ParseFile pf = ParseUser.getCurrentUser().getParseFile("profilepic");
        pf.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) {
                        mPreview.setImageBitmap(bitmap);
                    }
                }
                else {
                    // something went wrong
                }
            }});


//        mLogoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new CustomDialog.Builder(ProfileActivity.this)
//                        .setTitle("Are you sure?")
//                        .setMessage("You are logging out.")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                ParseUser.logOut();
//                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                            }
//                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                })
//                        .create()
//                        .show();
//            }
//        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ProfileActivity.this);
                input.setText(mStatusTV.getText().toString());
                new CustomDialog.Builder(ProfileActivity.this)
                        .setTitle("Update Status")
                        .setContentView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();
                                mStatusTV.setText(value);
                                ParseUser.getCurrentUser().put("status", value);
                                ParseUser.getCurrentUser().saveInBackground();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).create().show();
            }
        });


        mCameraButton.setOnClickListener(new View.OnClickListener(){
                                             @Override
                                             public void onClick (View v){
                                                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                 intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                         MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
                                                 intent.putExtra("crop", "true");
                                                 intent.putExtra("aspectX", 1);
                                                 intent.putExtra("aspectY", 1);
                                                 intent.putExtra("outputX", 300);
                                                 intent.putExtra("outputY", 300);
                                                 intent.putExtra("scale", true);
                                                 try {
                                                     intent.putExtra("return-data", true);
                                                     startActivityForResult(intent, PICK_FROM_CAMERA);

                                                 } catch (ActivityNotFoundException e) {
                                                 }
                                             }
                                         }

        );

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("scale", true);
                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), PICK_FROM_GALLERY);
                } catch (ActivityNotFoundException e) {
                }


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                mPreview.setImageBitmap(photo);
                savePicture(photo);
            }

        }

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable("data");
                mPreview.setImageBitmap(photo);
                savePicture(photo);
            }

        }


    }

    public void savePicture(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        // get byte array here
        byte[] bytearray = stream.toByteArray();
        if (bytearray != null) {
            ParseFile file = new ParseFile("profilepic.jpg", bytearray);
            ParseUser.getCurrentUser().put("profilepic", file);
            ParseUser.getCurrentUser().saveInBackground();
        }
    }


}
