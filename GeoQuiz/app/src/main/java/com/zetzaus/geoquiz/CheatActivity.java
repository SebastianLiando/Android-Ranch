package com.zetzaus.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    /**
     * Returns an intent containing information to start the <code>CheatActivity</code> class.
     *
     * @param context      the package context.
     * @param isAnswerTrue true if the statement/question's answer is true
     * @return the intent containing information to start the <code>CheatActivity</code> class.
     */
    public static Intent newIntent(Context context, boolean isAnswerTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER, isAnswerTrue);
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

        mTextAnswer = findViewById(R.id.text_cheat);

        if (savedInstanceState != null) {
            mIsCheated = savedInstanceState.getBoolean(CHEATED_KEY);
            if(mIsCheated){
                displayAnswer();
            }
        }

        // Setup show button
        mShowButton = findViewById(R.id.button_show_answer);
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAnswer();
                mIsCheated = true;
            }
        });
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

    private void displayAnswer(){
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

    @Override
    public void onBackPressed() {
        setAnswerShownResult(mIsCheated);
        super.onBackPressed();
    }
}
