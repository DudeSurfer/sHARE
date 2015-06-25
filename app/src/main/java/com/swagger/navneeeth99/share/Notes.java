package com.swagger.navneeeth99.share;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by navneeeth99 on 24/6/15.
 */
@ParseClassName("Notes")
public class Notes extends ParseObject {
    public Notes() {
        super();
    }

    public String getSubject() {
        return getString("subject");
    }

    public void setSubject(String mSubject) {
        put("subject", mSubject);
    }

    public String getLevel() {
        return getString("level");
    }

    public void setLevel(String mLevel) {
        put("level", mLevel);
    }

    public String getTopic() {
        return getString("topic");
    }

    public void setTopic(String mTopic) {
        put("topic", mTopic);
    }

    public ParseFile getNotesData() {
        return getParseFile("notesData");
    }

    public void setNotesData(ParseFile mTopic) {
        put("notesData", mTopic);
    }

    public String getContributor(){
        return getString("poster");
    }

    public void setContributor(String mPoster){
        put("poster", mPoster);
    }

    public String getNotesType(){
        return getString("type");
    }

    public void setNotesType(String mNotesType){
        put("type", mNotesType);
    }
}
