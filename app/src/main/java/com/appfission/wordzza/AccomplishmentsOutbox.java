package com.appfission.wordzza;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Created by srikanthmannepalle on 3/22/17.
 */

public class AccomplishmentsOutbox {

    public boolean mLadyBugAchievement = false;
    public boolean mSnailAchievement = false;
    public boolean mDuckAchievement = false;
    public boolean mOwlAchievement = false;
    public boolean mPenguinAchievement = false;
    public boolean mMouseAchievement = false;
    public boolean mFrogAchievement = false;
    public boolean mTurtleAchievement = false;
    public boolean mHamsterAchievement = false;
    public boolean mSquirrelAchievement = false;
    public boolean mHedgehogAchievement = false;
    public boolean mSnakeAchievement = false;
    public boolean mSheepAchievement = false;
    public boolean mCowAchievement = false;
    public boolean mBullAchievement = false;
    public boolean mGiraffeAchievement = false;
    public boolean mBearAchievement = false;
    public boolean mWolfAchievement = false;
    public boolean mRhinoAchievement = false;
    public boolean mElephantAchievement = false;
    public boolean mLionAchievement = false;

    public static Context context;
    public static GoogleApiClient mGoogleApiClient;
    private final static String TAG = AccomplishmentsOutbox.class.getName();

    public AccomplishmentsOutbox(Context ctx, GoogleApiClient mGoogleApiClient) {
        this.context = ctx;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    //Useful if we were to save and upload data to cloud
    boolean isEmpty() {
        return (!mLadyBugAchievement && !mSnailAchievement && !mDuckAchievement && !mOwlAchievement
                && !mPenguinAchievement && !mMouseAchievement && !mFrogAchievement && !mTurtleAchievement
                && !mHamsterAchievement && !mSquirrelAchievement && !mHedgehogAchievement && !mSnakeAchievement
                && !mSheepAchievement && !mCowAchievement && !mBullAchievement && !mGiraffeAchievement
                && !mBearAchievement && !mWolfAchievement && !mRhinoAchievement && !mElephantAchievement
                && !mLionAchievement);
    }

    public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
    }

    public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
    }

    public boolean isSignedIn() {
        Log.d(TAG, "mGoogleApiClient is null = " + String.valueOf(mGoogleApiClient == null));
        Log.d(TAG, "mGoogleApiClient is connected = " + String.valueOf(mGoogleApiClient.isConnected()));
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    void pushAccomplishments() {
        Log.d(TAG, "Pushing accomplishments");
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            saveLocal(context);
            Log.d(TAG, "User was not signed in, so saving his local data");
            return;
        }
        Log.d(TAG, "User signed in, achievements unlocking begins");
        if (mLadyBugAchievement) {
            Log.d(TAG, "Ladybug unlocked = " + context.getResources().getString(R.string.achievement_ladybug));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_ladybug));
            mLadyBugAchievement = false;
        }
        if (mSnailAchievement) {
            Log.d(TAG, "Snail unlocked = " + context.getResources().getString(R.string.achievement_snail));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_snail));
            mSnailAchievement = false;
        }
        if (mDuckAchievement) {
            Log.d(TAG, "Duck unlocked = " + context.getResources().getString(R.string.achievement_duck));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_duck));
            mDuckAchievement = false;
        }
        if (mOwlAchievement) {
            Log.d(TAG, "Owl unlocked = " + context.getResources().getString(R.string.achievement_owl));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_owl));
            mOwlAchievement = false;
        }
        if (mPenguinAchievement) {
            Log.d(TAG, "Penguin unlocked = " + context.getResources().getString(R.string.achievement_penguin));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_penguin));
            mPenguinAchievement = false;
        }
        if (mMouseAchievement) {
            Log.d(TAG, "Mouse unlocked = " + context.getResources().getString(R.string.achievement_mouse));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_mouse));
            mMouseAchievement = false;
        }
        if (mFrogAchievement) {
            Log.d(TAG, "Frog unlocked = " + context.getResources().getString(R.string.achievement_frog));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_frog));
            mFrogAchievement = false;
        }
        if (mTurtleAchievement) {
            Log.d(TAG, "Turtle unlocked = " + context.getResources().getString(R.string.achievement_turtle));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_turtle));
            mTurtleAchievement = false;
        }
        if (mHamsterAchievement) {
            Log.d(TAG, "Hamster unlocked = " + context.getResources().getString(R.string.achievement_hamster));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_hamster));
            mHamsterAchievement = false;
        }
        if (mSquirrelAchievement) {
            Log.d(TAG, "Squirrel unlocked = " + context.getResources().getString(R.string.achievement_squirrel));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_squirrel));
            mSquirrelAchievement = false;
        }
        if (mHedgehogAchievement) {
            Log.d(TAG, "Hedgehog unlocked = " + context.getResources().getString(R.string.achievement_hedgehog));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_hedgehog));
            mHedgehogAchievement = false;
        }
        if (mSnakeAchievement) {
            Log.d(TAG, "Snake unlocked = " + context.getResources().getString(R.string.achievement_snake));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_snake));
            mSnakeAchievement = false;
        }
        if (mSheepAchievement) {
            Log.d(TAG, "Sheep unlocked = " + context.getResources().getString(R.string.achievement_sheep));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_sheep));
            mSheepAchievement = false;
        }
        if (mCowAchievement) {
            Log.d(TAG, "Cow unlocked = " + context.getResources().getString(R.string.achievement_cow));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_cow));
            mCowAchievement = false;
        }
        if (mBullAchievement) {
            Log.d(TAG, "Bull unlocked = " + context.getResources().getString(R.string.achievement_bull));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_bull));
            mBullAchievement = false;
        }
        if (mGiraffeAchievement) {
            Log.d(TAG, "Giraffe unlocked = " + context.getResources().getString(R.string.achievement_giraffe));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_giraffe));
            mGiraffeAchievement = false;
        }
        if (mBearAchievement) {
            Log.d(TAG, "Bear unlocked = " + context.getResources().getString(R.string.achievement_bear));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_bear));
            mBearAchievement = false;
        }
        if (mWolfAchievement) {
            Log.d(TAG, "Wolf unlocked = " + context.getResources().getString(R.string.achievement_wolf));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_wolf));
            mWolfAchievement = false;
        }
        if (mRhinoAchievement) {
            Log.d(TAG, "Rhino unlocked = " + context.getResources().getString(R.string.achievement_rhino));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_rhino));
            mRhinoAchievement = false;
        }
        if (mElephantAchievement) {
            Log.d(TAG, "Elephant unlocked = " + context.getResources().getString(R.string.achievement_elephant));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_elephant));
            mElephantAchievement = false;
        }
        if (mLionAchievement) {
            Log.d(TAG, "Lion unlocked = " + context.getResources().getString(R.string.achievement_lion));
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(R.string.achievement_lion));
            mLionAchievement = false;
        }
        saveLocal(context);
    }
}

