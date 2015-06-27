package com.swagger.navneeeth99.share;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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
        put("uctopic", mTopic.toLowerCase());
    }

    public String getUCTopic(){ return getString("uctopic");}

    public ParseFile getNotesData() {
        return getParseFile("notesData");
    }

    public void setNotesData(ParseFile mTopic) {
        put("notesData", mTopic);
    }

    public String getContributor() {
        return getString("poster");
    }

    public void setContributor(String mPoster) {
        put("poster", mPoster);
    }

    public String getContributorName() { return getString("posterName");}

    public void setContributorName (String mPosterName) { put ("posterName", mPosterName);}

    public String getNotesType() {
        return getString("type");
    }

    public void setNotesType(String mNotesType) {
        put("type", mNotesType);
    }

    public ArrayList<Comments> getNotesComments() {
        List<Comments> mComments = getList("comments");
        return new ArrayList<>(mComments);
    }

    public void setNotesComments(ArrayList<Comments> mNoteComments) {
        put("comments", mNoteComments);
    }

    public void addNotesComments(Comments mComment) {
        add("comments", mComment);
    }

    public ArrayList<String> getNotesDownvoters() {
        List<String> mNotesDownvoters = getList("NotesDownvoters");
        return new ArrayList<>(mNotesDownvoters);
    }

    public void setNotesDownvoters(ArrayList<String> mNotesDownvoters){
        put("NotesDownvoters", mNotesDownvoters);
    }

    public void addNotesDownvoter(String mNotesDownvoter) {
        add("NotesDownvoters", mNotesDownvoter);
    }

    public void removeNotesDownvoter(String mNotesDownvoter) {
        ArrayList<String> toRemove = new ArrayList<>();
        toRemove.add(mNotesDownvoter);
        removeAll("NotesDownvoters", toRemove);
    }


    public ArrayList<String> getNotesUpvoters() {
        List<String> mNotesUpvoters = getList("NotesUpvoters");
        return new ArrayList<>(mNotesUpvoters);
    }

    public void setNotesUpvoters(ArrayList<String> mNotesUpvoters){
        put("NotesDownvoters", mNotesUpvoters);
    }

    public void addNotesUpvoter(String mNotesUpvoter) {
        add("NotesUpvoters", mNotesUpvoter);
    }

    public void removeNotesUpvoter(String mNotesUpvoter) {
        ArrayList<String> toRemove = new ArrayList<>();
        toRemove.add(mNotesUpvoter);
        removeAll("NotesUpvoters", toRemove);
    }

}