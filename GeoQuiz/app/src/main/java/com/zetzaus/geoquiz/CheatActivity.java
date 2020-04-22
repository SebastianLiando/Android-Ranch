package com.zetzaus.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER = BuildConfig.APPLICATION_ID + "EXTRA_ANSWER";
    private static final String EXTRA_CHEATED = BuildConfig.APPLICATION_ID + "EXTRA_CHEATED";
    private static final String CHEATED_KEY = "cheated_key";

    private boolean mIsAnswerTrue;
    private boolean mIsCheated = false;

    private Button mShowButton;
    private TextView mTextAnswer;
    private TextView mTextVersion;

    /**
     * Returns an intent containing information to start the <code>CheatActivity</code> class.
     *
     * @param context      the package context.
     * @param isAnswerTrue true if the statement/question's answer is true
     * @return the intent containing information to start the <code>CheatActivity</code> class.
     */
    public static Intent newIntent(Context context, boolean isAnswerTrue, boolean isCheated) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER, isAnswerTrue);
        intent.putExtra(EXTRA_CHEATED, isCheated);
        return intent;
    }

    /**
     * Returns true if the answer was shown to the user.
     *
     * @param result the required information wrapped in an intent.
     * @return true if the answer was shown to the user.
     */
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_CHEATED, false);
    }

    /**
     * Sets up the activity's functionality.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        // Get intent data
        mIsAnswerTrue = getIntent().getBooleanExtra(EXTRA_ANSWER, false);
        mIsCheated = getIntent().getBooleanExtra(EXTRA_CHEATED, false);

        mTextAnswer = findViewById(R.id.text_cheat);

        // Setup show button
        mShowButton = findViewById(R.id.button_show_answer);
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAnswer();
                mIsCheated = true;

                // Animation only for SDK 21 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowButton.getWidth() / 2;
                    int cy = mShowButton.getHeight() / 2;
                    float radius = mShowButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowButton.setVisibility(View.GONE);
                        }
                    });
                    anim.start();
                }
            }
        });

        // Display the device SDK version
        mTextVersion = findViewById(R.id.text_android_version);
        mTextVersion.setText(String.format(Locale.getDefault(),
                getResources().getString(R.string.text_android_version), Build.VERSION.SDK_INT));

        // Restore state
        if (savedInstanceState != null) {
            mIsCheated = savedInstanceState.getBoolean(CHEATED_KEY);
        }

        // Check if already cheated
        if (mIsCheated) {
            displayAnswer();
            mShowButton.setVisibility(View.GONE);
        }
    }

    /**
     * Save the cheated state to prevent rotation trick to erase result code.
     *
     * @param outState the bundle containing the cheated state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHEATED_KEY, mIsCheated);
    }

    /**
     * Display the answer for the question to the user.
     */
    private void displayAnswer() {
        if (mIsAnswerTrue) mTextAnswer.setText(R.string.button_true);
        else mTextAnswer.setText(R.string.button_false);
    }

    /**
     * Creates a new intent containing the information whether the user has cheated or not, and set
     * reply code to the caller activity.
     *
     * @param isAnswerShown true if the user has cheated.
     */
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_CHEATED, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    /**
     * Sets the result code and intent before popping the activity from the back stack.
     */
    @Override
    public void onBackPressed() {
        setAnswerShownResult(mIsCheated);
        super.onBackPressed();
    }
}
