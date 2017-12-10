package com.doenough.contestapp.search;

import android.net.Uri;

/**
 * Created by sanchit on 19/11/17.
 */

public class SearchItem {

    private String mKey;
    private String mName;
    private long mTime;
    private Uri mImageUrl;
    private String mCreatorName;

    public SearchItem(String key, String name, long time, Uri imageUrl, String creatorName) {
        mKey = key;
        mName = name;
        mTime = time;
        mImageUrl = imageUrl;
        mCreatorName = creatorName;
    }

    public SearchItem(String key, String name, long time, String creatorName) {
        mKey = key;
        mName = name;
        mTime = time;
        mCreatorName = creatorName;
    }

    public String getmKey() {
        return mKey;
    }
    public String getmName() {
        return mName;
    }
    public long getmTime() {
        return mTime;
    }
    public Uri getmImageUrl() {
        return mImageUrl;
    }
    public String getmCreatorName() {
        return mCreatorName;
    }

}
