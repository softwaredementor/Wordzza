package com.appfission.wordzza;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;

import java.util.ArrayList;

import static com.appfission.wordzza.AccomplishmentsOutbox.mGoogleApiClient;

/**
 * Created by srikanthmannepalle on 3/17/17.
 */

public class GameScreen extends QuestionGenerator {

    private ProgressBar progressBar;
    private static long totalScore;


    private static String TAG;

    private boolean isSoundEnabled;
    private static boolean isActive = false;

    private MediaPlayer correctSoundFilePlayer;
    private MediaPlayer incorrectSoundFilePlayer;
    private Animation anim;

    private  String correctAnswer;
    private  long highScoreTextValue;
    private  float countDowntimerValue = 6000f;

    private TextView questionTextView;
    private TextView totalScoreView;
    private TextView answerTextView;
    private TextView newScoreTextView;
    private TextView highScoreTextView;
    private TextView dialogFakeTitle;

    private ImageView soundIconView;
    private Button firstChoiceButton;
    private Button secondChoiceButton;
    private Button thirdChoiceButton;
    private Button fourthChoiceButton;
    private Button playButton;
    private Button menuButton;
    private CountDownTimer countDownTimer;
    private CountDownTimer dialogCountDownTimer;
    private static ArrayList<ArrayList<String>> output;
    private static ArrayList<String> stringCombinations;
    private static String HIGH_SCORE;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private InterstitialAd interstitialAd;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamescreen);
        sharedPreferences =  getApplicationContext().getSharedPreferences("MY_PREFERENCE_WORDZZA", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //to resolve a bug, when dialogue pop-up comes
        // and user presses back button immediately
        if(mGoogleApiClient.isConnected()) {
            Games.setViewForPopups(mGoogleApiClient, getWindow().getDecorView().findViewById(android.R.id.content));
        }

        //Ad section
        interstitialAd = new InterstitialAd(this);
        //Test ad
//        interstitialAd.setAdUnitId(getResources().getString(R.string.testAd));
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_adunit));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        highScoreTextValue = sharedPreferences.getLong(HIGH_SCORE, 0);
        mOutbox = new AccomplishmentsOutbox(AccomplishmentsOutbox.context, mGoogleApiClient);

        TAG = GameScreen.class.getName();
        isSoundEnabled = true;
        totalScore = 0;
        correctSoundFilePlayer = MediaPlayer.create(this, R.raw.correctsound);
        incorrectSoundFilePlayer = MediaPlayer.create(this, R.raw.incorrectsound);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        totalScoreView = (TextView) findViewById(R.id.totalScoreView);
        totalScoreView.setText(String.valueOf(totalScore));
        soundIconView = (ImageView) findViewById(R.id.soundIconView);
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
        firstChoiceButton = (Button) findViewById(R.id.firstChoice);
        secondChoiceButton = (Button) findViewById(R.id.secondChoice);
        thirdChoiceButton = (Button) findViewById(R.id.thirdChoice);
        fourthChoiceButton = (Button) findViewById(R.id.fourthChoice);

        //initialize dictionary
        try {
            readWordsFromFile();
        } catch (Exception e) {
           Log.d(TAG, "Unknown exception occurred in reading words file in onCreate " + e);
        }

        //initialize Button Controls
        initializeButtonControls();

        //initialize the wordlist
        //GC issue is happening here !
        questionGenerator();

    }

    @Override
    protected void onPause() {
        //logic for high score
        highScoreTextValue = highScoreTextValue > totalScore ? highScoreTextValue : totalScore;
        editor.putLong(HIGH_SCORE, highScoreTextValue);
        editor.commit();
        countDownTimer.cancel();
        super.onPause();
    }

    private void requestNewInterstitial() {
        Log.d(TAG, "Device id = " + Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
                .build();

        interstitialAd.loadAd(adRequest);
        Log.d(TAG, "interstitial ad loaded");
    }

    private void runTimer() {
        if ( null != countDownTimer) {
            countDownTimer.cancel();
        }
        if (totalScore >= 50 && totalScore < 150) {
            countDowntimerValue = 5000f;
        } else if (totalScore >= 150 && totalScore < 500) {
            countDowntimerValue = 4000f;
        } else if (totalScore >= 500 && totalScore < 1000) {
            countDowntimerValue = 3000f;
        } else if (totalScore >= 1000) {
            countDowntimerValue = 2000f;
        }

        countDownTimer = new CountDownTimer((long) countDowntimerValue, 25) {

            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) ((millisUntilFinished / countDowntimerValue)*100));
            }

            public void onFinish() {
                progressBar.setProgress(0);
                enableDisableAllButtons(false);
                Toast.makeText(getApplicationContext(), "Time up", Toast.LENGTH_SHORT).show();
                if(isSoundEnabled) {
                    answerTextView.setText(correctAnswer.toUpperCase());
                    answerTextView.startAnimation(anim);
                    incorrectSoundFilePlayer.start();
                }
                bringContextualDialogue("Time Up");
            }
        }.start();
    }

    private void initializeButtonControls() {
        soundIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundEnabled) {
                    isSoundEnabled = false;
                    soundIconView.setImageResource(R.drawable.ic_volume_off_black_24dp);
                } else {
                    isSoundEnabled = true;
                    soundIconView.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }
            }
        });
        firstChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer("" + firstChoiceButton.getText().toString(), correctAnswer);
            }
        });

        secondChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer("" + secondChoiceButton.getText().toString(), correctAnswer);
            }
        });

        thirdChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer("" + thirdChoiceButton.getText().toString(), correctAnswer);
            }
        });

        fourthChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer("" + fourthChoiceButton.getText().toString(), correctAnswer.toUpperCase());
            }
        });

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the blinking time with this parameter
        anim.setStartOffset(30);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
    }

    private void questionGenerator() {
        try {
            output = generateQuestionAndAnswers();
            correctAnswer = output.get(0).get(0).toUpperCase();
            stringCombinations = output.get(1);
            Log.d(TAG, "Actual word = " + correctAnswer);
            int randomChoice = (int) Math.round(Math.random()*3);
            switch (randomChoice) {
                case 0 : questionTextView.setText(stringCombinations.get(0).toUpperCase());
                    firstChoiceButton.setText(correctAnswer.toUpperCase());
                    Log.d(TAG, "first choice was = " + stringCombinations.get(0).toUpperCase());
                    secondChoiceButton.setText(stringCombinations.get(1).toUpperCase());
                    thirdChoiceButton.setText(getObfuscatedActualWord().toUpperCase());
                    fourthChoiceButton.setText(stringCombinations.get(3).toUpperCase());
                    break;
                case 1 : questionTextView.setText(stringCombinations.get(1).toUpperCase());
                    secondChoiceButton.setText(correctAnswer.toUpperCase());
                    Log.d(TAG, "second choice was = " + stringCombinations.get(1).toUpperCase());
                    firstChoiceButton.setText(stringCombinations.get(0).toUpperCase());
                    thirdChoiceButton.setText(stringCombinations.get(2).toUpperCase());
                    fourthChoiceButton.setText(getObfuscatedActualWord().toUpperCase());
                    break;
                case 2 : questionTextView.setText(stringCombinations.get(2).toUpperCase());
                    thirdChoiceButton.setText(correctAnswer.toUpperCase());
                    Log.d(TAG, "third choice was = " + stringCombinations.get(2).toUpperCase());
                    firstChoiceButton.setText(getObfuscatedActualWord().toUpperCase());
                    secondChoiceButton.setText(stringCombinations.get(1).toUpperCase());
                    fourthChoiceButton.setText(stringCombinations.get(3).toUpperCase());
                    break;
                case 3 : questionTextView.setText(stringCombinations.get(3).toUpperCase());
                    fourthChoiceButton.setText(correctAnswer.toUpperCase());
                    Log.d(TAG, "fourth choice was = " + stringCombinations.get(3).toUpperCase());
                    firstChoiceButton.setText(stringCombinations.get(0).toUpperCase());
                    secondChoiceButton.setText(getObfuscatedActualWord().toUpperCase());
                    thirdChoiceButton.setText(stringCombinations.get(2).toUpperCase());
                    break;

            }
            //Start the timer after displaying options
            runTimer();
        } catch (Exception e) {
            Log.d(TAG, "Exception occurred in setting options" + e.getMessage());
        }
    }

    private void enableDisableAllButtons(boolean isEnabled) {
        firstChoiceButton.setEnabled(isEnabled);
        secondChoiceButton.setEnabled(isEnabled);
        thirdChoiceButton.setEnabled(isEnabled);
        fourthChoiceButton.setEnabled(isEnabled);
    }

    private void bringContextualDialogue(final String title) {
        //submit the score to the leaderboard
        if(AccomplishmentsOutbox.mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Submitting score to leaderboard");
            Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_leaderboard), totalScore);
        } else {
            Log.d(TAG, "Not submitting score to leaderboard since user is not signed in");
        }

        //display timer first
        dialogCountDownTimer = new CountDownTimer(2000, 500) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Log.d(TAG, "Interstitial ad was not loaded properly");
                }
                setDialogSettings(title);
            }
        }.start();
    }

    private void setDialogSettings(String title) {
        if (isActive) {
            try {
                final Dialog dialog = new Dialog(GameScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialoguebox);
                newScoreTextView = (TextView) dialog.findViewById(R.id.newValueTextView);

                //validate high score
                highScoreTextView = (TextView) dialog.findViewById(R.id.highValueTextView);
                dialogFakeTitle = (TextView) dialog.findViewById(R.id.dialogFakeTitle);

                playButton = (Button) dialog.findViewById(R.id.dialogButtonPlay);
                menuButton = (Button) dialog.findViewById(R.id.dialogButtonMenu);

                newScoreTextView.setText(String.valueOf(totalScore));
                highScoreTextView.setText(String.valueOf(highScoreTextValue));
                highScoreTextValue = highScoreTextValue > totalScore ? highScoreTextValue : totalScore;
                dialogFakeTitle.setText(title);

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalScore = 0;
                        answerTextView.setText("");
                        anim.cancel();
                        totalScoreView.setText("0");
                        enableDisableAllButtons(true);
                        dialog.dismiss();
                        questionGenerator();
                    }
                });

                menuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mainMenuIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainMenuIntent);
                    }
                });

                dialog.getWindow().setBackgroundDrawable(null);
                dialog.show();
            } catch (Exception ex) {
                Log.d(TAG, "Game screen window was closed unexpectedly " + ex.getMessage());
            }
        }
        editor.putLong(HIGH_SCORE, highScoreTextValue);
        editor.commit();

        // check for achievements
        checkForAchievements();

        // push those accomplishments to the cloud, if signed in
        mOutbox.pushAccomplishments();

    }

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     */
    void checkForAchievements() {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (totalScore >= 10) {
            mOutbox.mLadyBugAchievement = true;
            unsignedUserAchievementLoggings("LadyBug");
        }
        if (totalScore >= 25) {
            mOutbox.mSnailAchievement = true;
            unsignedUserAchievementLoggings("Snail");
        }
        if (totalScore >= 50) {
            mOutbox.mDuckAchievement= true;
            unsignedUserAchievementLoggings("Duck");
        }
        if (totalScore >= 75) {
            mOutbox.mOwlAchievement = true;
            unsignedUserAchievementLoggings("Owl");
        }
        if (totalScore >= 100) {
            mOutbox.mPenguinAchievement = true;
            unsignedUserAchievementLoggings("Penguin");
        }
        if (totalScore >= 120) {
            mOutbox.mMouseAchievement = true;
            unsignedUserAchievementLoggings("Mouse");
        }
        if (totalScore >= 140) {
            mOutbox.mFrogAchievement = true;
            unsignedUserAchievementLoggings("Frog");
        }
        if (totalScore >= 165) {
            mOutbox.mTurtleAchievement = true;
            unsignedUserAchievementLoggings("Turtle");
        }
        if (totalScore >= 185) {
            mOutbox.mHamsterAchievement = true;
            unsignedUserAchievementLoggings("Hamster");
        }
        if (totalScore >= 200) {
            mOutbox.mSquirrelAchievement = true;
            unsignedUserAchievementLoggings("Squirrel");
        }
        if (totalScore >= 225) {
            mOutbox.mHedgehogAchievement = true;
            unsignedUserAchievementLoggings("Hedgehog");
        }
        if (totalScore >= 250) {
            mOutbox.mSnakeAchievement = true;
            unsignedUserAchievementLoggings("Snake");
        }
        if (totalScore >= 275) {
            mOutbox.mSheepAchievement = true;
            unsignedUserAchievementLoggings("Sheep");
        }
        if (totalScore >= 300) {
            mOutbox.mCowAchievement = true;
            unsignedUserAchievementLoggings("Cow");
        }
        if (totalScore >= 325) {
            mOutbox.mBullAchievement = true;
            unsignedUserAchievementLoggings("Bull");
        }
        if (totalScore >= 350) {
            mOutbox.mGiraffeAchievement = true;
            unsignedUserAchievementLoggings("Giraffe");
        }
        if (totalScore >= 375) {
            mOutbox.mBearAchievement = true;
            unsignedUserAchievementLoggings("Bear");
        }
        if (totalScore >= 400) {
            mOutbox.mWolfAchievement = true;
            unsignedUserAchievementLoggings("Wolf");
        }
        if (totalScore >= 425) {
            mOutbox.mRhinoAchievement = true;
            unsignedUserAchievementLoggings("Rhino");
        }
        if (totalScore >= 450) {
            mOutbox.mElephantAchievement = true;
            unsignedUserAchievementLoggings("Elephant");
        }
        if (totalScore >= 500) {
            mOutbox.mLionAchievement = true;
            unsignedUserAchievementLoggings("Lion");
        }
    }

    void unsignedUserAchievementLoggings(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!mOutbox.isSignedIn()) {
            Log.d(TAG, getString(R.string.achievement) + ": " + achievement + " unlocked");
        } else {
            Log.d(TAG, "User is signed in, Toast not required!");
        }
    }

    private void validateAnswer(String userAnswer, String correctAnswer) {
        Log.d(TAG, "User answer = " + userAnswer + " and correct answer = " + correctAnswer);
        if (userAnswer.equals(correctAnswer) || (isAnagram(userAnswer, correctAnswer) && totaldWordList.contains(userAnswer))) {
            totalScoreView.setText(String.valueOf(++totalScore));

            questionGenerator();
            if(isSoundEnabled) {
                correctSoundFilePlayer.start();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Game over", Toast.LENGTH_SHORT).show();
            //disable buttons
            enableDisableAllButtons(false);
            countDownTimer.cancel();
            //show correct answer
            answerTextView.setText(correctAnswer.toUpperCase());
            answerTextView.startAnimation(anim);
            if(isSoundEnabled) {
                incorrectSoundFilePlayer.start();
            }
            //show the dialogue window to start or to go to main menu
            bringContextualDialogue("Game over");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }
}
