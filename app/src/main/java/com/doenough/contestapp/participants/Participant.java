package com.doenough.contestapp.participants;


/**
 * Created by sanchit on 18/11/17.
 */

public class Participant {

    private String mName;
    private long mVote;
    private String mKey;
    private String mImageUrl;

    public Participant(String name, long vote, String key, String imageUrl) {
        mName = name;
        mVote = vote;
        mKey = key;
        mImageUrl = imageUrl;
    }

    public String getmName() {
        return mName;
    }
    public long getmVote() {
        return mVote;
    }
    public String getmKey() {
        return mKey;
    }
    public String getmImageUrl() {
        return mImageUrl;
    }
}
