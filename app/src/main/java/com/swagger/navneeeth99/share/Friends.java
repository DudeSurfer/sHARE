package com.swagger.navneeeth99.share;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 28/6/15.
 */
@ParseClassName("Friends")
public class Friends extends ParseObject {
    public Friends() {
        super();
    }

    public String getUser(){
        return getString("User");
    }

    public void  setUser(String mUser){
        put("User",mUser);
    }

    public ArrayList<String> getFriendsWith(){
        List<String> mFriendsWith = getList("FriendsWith");
        return new ArrayList<>(mFriendsWith);
    }

    public void setFriendsWith(ArrayList<String> mFriendsWith){
        put("FriendsWith", mFriendsWith);
    }

    public void addNotesDownvoter(String mNewFriend) {
        add("FriendsWith", mNewFriend);
    }

    public void removeNotesDownvoter(String mByeFriend) {
        ArrayList<String> toRemove = new ArrayList<>();
        toRemove.add(mByeFriend);
        removeAll("FriendsWith", toRemove);
    }

}
