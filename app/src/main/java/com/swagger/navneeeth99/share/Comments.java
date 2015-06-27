package com.swagger.navneeeth99.share;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by navneeth99 on 27/6/2015.
 */
@ParseClassName("Comments")
public class Comments extends ParseObject{
    public Comments() {
        super();
    }

    public String getCTitle() {
        return getString("ctitle");
    }

    public void setCTitle(String mCTitle) {
        put("ctitle", mCTitle);
    }

    public int getCStars() {
        return getInt("stars");
    }

    public void setCStars(int mStars) {
        put("stars", mStars);
    }

    public String getCContent() {
        return getString("ccontent");
    }

    public void setCContent(String mContent) {
        put("ccontent", mContent);
    }

    public String getCContributor() {
        return getString("cposter");
    }

    public void setCContributor(String mCContributor) {
        put("cposter", mCContributor);
    }
}
