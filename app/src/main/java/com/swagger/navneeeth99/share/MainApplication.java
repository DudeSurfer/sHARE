package com.swagger.navneeeth99.share;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by navneeeth99 on 23/6/15.
 */
public class MainApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Notes.class);
        ParseObject.registerSubclass(Comments.class);
        ParseObject.registerSubclass(Friends.class);
        Parse.initialize(this, "ofVu7m2pAWlmIoxq55A2J2t9jG2FPRgQbqBjy5ku", "GT192HrXbktHSExlfHdtwvboSmV1o9ZPJqaNVih3");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
