package com.doenough.contestapp.event;

import android.net.Uri;

/**
 * Created by sanchit on 18/11/17.
 */

public class Event {


    private String mName;
    private long mTime;
    private String mKey;
    private String mUserKey;
    private Uri mImageUrl;
    private String mNameOfCreator;
    private String mUidOfCreator;

    public Event(String name, long time, String key, String userKey, Uri imageUrl, String nameOfCreator, String uidOfCreator) {
        mName = name;
        mTime = time;
        mKey = key;
        mUserKey = userKey;
        mImageUrl = imageUrl;
        mNameOfCreator = nameOfCreator;
        mUidOfCreator = uidOfCreator;
    }

    public Event(String name, long time, String key, String userKey, String nameOfCreator, String uidOfCreator) {
        mName = name;
        mTime = time;
        mKey = key;
        mUserKey = userKey;
        mNameOfCreator = nameOfCreator;
        mUidOfCreator = uidOfCreator;
    }

    public String getmName() {
        return mName;
    }

    public long getmTime() {
        return mTime;
    }

    public String getmKey() {
        return  mKey;
    }

    public String getmUserKey() {
        return mUserKey;
    }

    public Uri getmImageUrl() {
        return mImageUrl;
    }

    public  String getmNameOfCreator() {
        return mNameOfCreator;
    }

    public  String getmUidOfCreator() {
        return mUidOfCreator;
    }
}

