package com.swagger.navneeeth99.share;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.parse.ParseUser;


public class HelpActivity extends BaseActivity {

    boolean isHide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser!=null){
            setTitle("Help");
            super.onCreateDrawer();
        }
        final LinearLayout mFirstText = (LinearLayout) findViewById(R.id.firstHelp);
        final Button mHideButton = (Button) findViewById(R.id.hideButton);
        final Button mContactButton = (Button) findViewById(R.id.contactButton);
        mHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHide){
                    // Prepare the View for the animation
                    mFirstText.setVisibility(View.VISIBLE);
                    mFirstText.setAlpha(0.0f);
                    // Start the animation
                    mFirstText.animate()
                            .scaleY(1)
                            .translationY(0)
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mFirstText.setAlpha(1.0f);
                                    mFirstText.setVisibility(View.VISIBLE);
                                }
                            });
                    isHide = false;
                    mHideButton.setText("Hide");
                } else {
                    mFirstText.animate()
                            .scaleY(0)
                            .translationY(-mFirstText.getHeight()/2)
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mFirstText.setAlpha(0.0f);
                                    mFirstText.setVisibility(View.GONE);
                                }
                            });
                    isHide = true;
                    mHideButton.setText("Unhide");
                }
            }
        });
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "navneeth.k50@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "sHARE Question: ");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
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
}
