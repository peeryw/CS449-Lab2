package com.peery.android.cs449_lab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;


public class UmpireBuddy extends AppCompatActivity {

    // declare variables
    private int mCounterStrike = 0;
    private int mCounterBall = 0;
    private int mCounterOuts = 0;

    private TextView mStrikeCount;
    private TextView mBallCount;
    private TextView mOutCount;

    private ActionMode mActionModeCountChange;

    //private static final String TAG = "UmpireBuddy";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "text";

    private String textPersistent;
    private String mWalk = "Walk";
    private String mOut = "Out";

    private TextToSpeech mTTS;

    private Boolean mOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umpire_buddy);

        //set text views of activity viewable
        mStrikeCount = findViewById(R.id.textViewStrikeCount);
        mStrikeCount.setText(String.valueOf(mCounterStrike));
        mBallCount = findViewById(R.id.textViewBallCount);
        mBallCount.setText(String.valueOf(mCounterBall));
        mOutCount = findViewById(R.id.textViewTotalOutsCount);
        mOutCount.setText(String.valueOf(mCounterOuts));

        //need to set this text view to the background of the umpire activity or
        //      divided between the strike text and the ball text.
        //add a hint for the long press function?
        TextView mChangeCountMenu = findViewById(R.id.textViewStrike);
        mChangeCountMenu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionModeCountChange != null) {
                    return false;
                }

                mActionModeCountChange = startSupportActionMode(mActionModeCallbackCountChange);
                return true;
            }
        });

        loadData();
        updatePersistentViews();

    }

    // onStop override to save data to persistent storage
    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop() called");
        saveData();
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    //onDestroy override to save data to persistent storage
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy() called");
        saveData();
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    private ActionMode.Callback mActionModeCallbackCountChange = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_change_count, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        //fix this to separate the long press to be split between strike and ball text field
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.increment_ball_count:
                    if (mCounterBall < 3) {
                        mCounterBall++;
                        mBallCount.setText(String.valueOf(mCounterBall));
                        actionMode.finish();
                        return true;
                    } else {
                        if (mOn == true) {
                            mTTS.speak(mWalk, TextToSpeech.QUEUE_FLUSH, null);
                        }
                        AlertDialog.Builder mWalkBuilder = new AlertDialog.Builder(UmpireBuddy.this);

                        mWalkBuilder.setMessage("WALK!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mCounterBall = 0;
                                mCounterStrike = 0;
                                mBallCount.setText(String.valueOf(mCounterBall));
                                mStrikeCount.setText(String.valueOf(mCounterStrike));
                            }
                        }).setNegativeButton("", null).setCancelable(false);

                        AlertDialog alert = mWalkBuilder.create();
                        alert.show();
                        actionMode.finish();
                        return true;
                    }

                case R.id.increment_strike_count:
                    if (mCounterStrike < 2) {
                        mCounterStrike++;
                        mStrikeCount.setText(String.valueOf(mCounterStrike));
                        actionMode.finish();
                        return true;
                    } else {
                        if (mOn == true) {
                            mTTS.speak(mOut, TextToSpeech.QUEUE_FLUSH, null);
                        }
                        AlertDialog.Builder mStrikeBuilder = new AlertDialog.Builder(UmpireBuddy.this);

                        mStrikeBuilder.setMessage("OUT!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mCounterStrike = 0;
                                mCounterBall = 0;
                                mStrikeCount.setText(String.valueOf(mCounterStrike));
                                mBallCount.setText(String.valueOf(mCounterBall));
                            }
                        }).setNegativeButton("", null).setCancelable(false);

                        AlertDialog alert = mStrikeBuilder.create();
                        alert.show();
                        actionMode.finish();
                        return true;
                    }

                case R.id.decrement_ball_count:
                    if (mCounterBall == 0) {
                        actionMode.finish();
                        return true;
                    }
                    mCounterBall--;
                    mBallCount.setText(String.valueOf(mCounterBall));
                    actionMode.finish();
                    return true;

                case R.id.decrement_strike_count:
                    if (mCounterStrike == 0) {
                        actionMode.finish();
                        return true;
                    }
                    mCounterStrike--;
                    mStrikeCount.setText(String.valueOf(mCounterStrike));
                    actionMode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionModeCountChange = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_umpire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Reset:
                // reset all text fields
                mCounterStrike = 0;
                mCounterBall = 0;
                mCounterOuts = 0;
                mStrikeCount.setText(String.valueOf(mCounterStrike));
                mBallCount.setText(String.valueOf(mCounterBall));
                mOutCount.setText(String.valueOf(mCounterOuts));
                return true;

            case R.id.About:
                //open new activity to display info about app
                saveData();
                Intent intent = new Intent(UmpireBuddy.this, About.class);
                startActivity(intent);
                return true;

            case R.id.Settings:
                //open dialog to turn on/off text-to-speech
                AlertDialog.Builder mTextToSpeech = new AlertDialog.Builder(UmpireBuddy.this);

                mTextToSpeech.setMessage("Turn on text-to-speech").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //turn on text to speech
                        turnOnSpeech();
                        mOn = true;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mTTS != null) {
                            mTTS.stop();
                            mTTS.shutdown();
                        }
                    }
                }).setCancelable(false);

                AlertDialog alert = mTextToSpeech.create();
                alert.show();
                return true;

            default:

        }
        return super.onOptionsItemSelected(item);
    }

    // save the current state of the activity screen (nonpersistent state)
    // wont save the current state if a new activity is displayed - need
    //      to fix this error
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("textViewBallCount", mCounterBall);
        outState.putInt("textViewStrikeCount", mCounterStrike);
        outState.putInt("textViewOutCount", mCounterOuts);
    }

    // restore the saved state to the new activity screen
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCounterBall = savedInstanceState.getInt("textViewBallCount");
        mBallCount.setText(String.valueOf(mCounterBall));
        mCounterStrike = savedInstanceState.getInt("textViewStrikeCount");
        mStrikeCount.setText(String.valueOf(mCounterStrike));
        mCounterOuts = savedInstanceState.getInt("textViewOutCount");
        mOutCount.setText(String.valueOf(mCounterOuts));
    }

    public void ball_count_up(View view) {
        if (mCounterBall < 3) {
            mCounterBall++;
            mBallCount.setText(String.valueOf(mCounterBall));
        } else {
            if (mOn == true) {
                mTTS.speak(mWalk, TextToSpeech.QUEUE_FLUSH, null);
            }
            AlertDialog.Builder mWalkBuilder = new AlertDialog.Builder(UmpireBuddy.this);

            mWalkBuilder.setMessage("WALK!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mCounterBall = 0;
                    mCounterStrike = 0;
                    mBallCount.setText(String.valueOf(mCounterBall));
                    mStrikeCount.setText(String.valueOf(mCounterStrike));
                }
            }).setNegativeButton("", null).setCancelable(false);

            AlertDialog alert = mWalkBuilder.create();
            alert.show();
        }
    }

    public void strike_count_up(View view) {
        if (mCounterStrike < 2) {
            mCounterStrike++;
            mStrikeCount.setText(String.valueOf(mCounterStrike));
        } else {
            if (mOn == true) {
                mTTS.speak(mOut, TextToSpeech.QUEUE_FLUSH, null);
            }
            AlertDialog.Builder mStrikeBuilder = new AlertDialog.Builder(UmpireBuddy.this);

            mStrikeBuilder.setMessage("OUT!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mCounterStrike = 0;
                    mCounterBall = 0;
                    mCounterOuts++;
                    mStrikeCount.setText(String.valueOf(mCounterStrike));
                    mBallCount.setText(String.valueOf(mCounterBall));
                    mOutCount.setText(String.valueOf(mCounterOuts));
                }
            }).setNegativeButton("", null).setCancelable(false);

            AlertDialog alert = mStrikeBuilder.create();
            alert.show();
        }
    }

    // persistent data storage function
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, mOutCount.getText().toString());

        editor.apply();
    }

    // persistent data loaded function
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        textPersistent = sharedPreferences.getString(TEXT, "");
    }

    // persistent data update for text view field
    private void updatePersistentViews() {
        mOutCount.setText(textPersistent);
    }

    private void turnOnSpeech() {
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.UK);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        AlertDialog.Builder error = new AlertDialog.Builder(UmpireBuddy.this);
                        error.setMessage("TextToSpeech is not working").setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNegativeButton("", null).setCancelable(false);

                        AlertDialog alert = error.create();
                        alert.show();
                    }
                } else {
                    AlertDialog.Builder error = new AlertDialog.Builder(UmpireBuddy.this);
                    error.setMessage("TextToSpeech is not working").setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNegativeButton("", null).setCancelable(false);

                    AlertDialog alert = error.create();
                    alert.show();
                }

            }
        });
    }

}
