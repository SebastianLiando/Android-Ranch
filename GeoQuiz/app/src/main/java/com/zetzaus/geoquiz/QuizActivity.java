package com.zetzaus.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

/**
 * This activity is the main activity of the application. It displays a question in a form of a statement which
 * the user has to determine if it is true or false. It also has a next button to juggle between the questions.
 */
public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionText;
    private TextView mResultText;
    private TextView mCheatRemainingText;

    private QuizViewModel mViewModel;

    // Constants
    private static final String INDEX_KEY = "index_key";
    private static final String ANSWER_MASK_KEY = "answer_mask_key";
    private static final String CORRECT_ANSWER_KEY = "correct_answer_key";
    private static final String CHEAT_MASK_KEY = "cheat_mask_key";
    private static final int CHEAT_REQUEST_CODE = 0;
    private static final int MAX_CHEAT_COUNT = 3;

    /**
     * Sets up the activity to be functional.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Retrieve ViewModel
        mViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Restore state
        if (savedInstanceState != null) {
            mViewModel.setCurrentIndex(savedInstanceState.getInt(INDEX_KEY));
            mViewModel.setAnsweredMask(savedInstanceState.getInt(ANSWER_MASK_KEY));
            mViewModel.setCorrectAnswer(savedInstanceState.getInt(CORRECT_ANSWER_KEY));
            mViewModel.setCheatMask(savedInstanceState.getInt(CHEAT_MASK_KEY));
        }

        // Make the true button listens to click. It will display a toast.
        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        // Make the false button listens to click. It will display a toast.
        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        // Make the next button listens to click. It will replace with the next question.
        mNextButton = findViewById(R.id.button_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.nextQuestion();
                updateQuestionText();
            }
        });

        // Make the previous button listens to click. It will replace with the previous question.
        // Is a solution for Chapter 2 challenge problem
        mPrevButton = findViewById(R.id.button_prev);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.prevQuestion();
                updateQuestionText();
            }
        });

        // Make the cheat button listens to click. On click go to Cheat Activity
        mCheatButton = findViewById(R.id.button_cheat);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cheatIntent = CheatActivity.newIntent(QuizActivity.this,
                        mViewModel.getQuestionAnswer(), mViewModel.isCurrentQuestionCheated());
                startActivityForResult(cheatIntent, CHEAT_REQUEST_CODE);
            }
        });

        // Display the first question
        mQuestionText = findViewById(R.id.text_question);
        updateQuestionText();

        // Clicking the question text also cycles to the next question.
        // Is the solution for the challenge in Question 2.
        mQuestionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.nextQuestion();
                updateQuestionText();
            }
        });

        // Setup result text
        mResultText = findViewById(R.id.text_result);
        updateScoreText();
    }

    /**
     * Disables cheat button if the user has cheated more than the number of allowed times.
     */
    @Override
    protected void onResume() {
        super.onResume();

        int cheatCount = mViewModel.getCheatCount();
        // Setup remaining cheats text
        mCheatRemainingText = findViewById(R.id.text_cheats_remaining);
        mCheatRemainingText.setText(String.format(Locale.getDefault(),
                getResources().getString(R.string.text_cheats_remaining), MAX_CHEAT_COUNT - cheatCount));

        if (cheatCount >= MAX_CHEAT_COUNT) mCheatButton.setEnabled(false);
    }

    /**
     * Stores the index of the current question to the bundle.
     *
     * @param outState the bundle.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INDEX_KEY, mViewModel.getCurrentIndex());
        outState.putInt(ANSWER_MASK_KEY, mViewModel.getAnsweredMask());
        outState.putInt(CORRECT_ANSWER_KEY, mViewModel.getCorrectAnswer());
        outState.putInt(CHEAT_MASK_KEY, mViewModel.getCheatMask());
    }

    /**
     * Checks if the user really cheated in <code>CheatActivity</code>.
     *
     * @param requestCode the request code.
     * @param resultCode  the result code.
     * @param data        the extra information containing whether the user has cheated or not.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHEAT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;
                boolean isCheater = CheatActivity.wasAnswerShown(data);
                if (isCheater) mViewModel.setCurrentQuestionCheated();
            }
        }
    }

    /**
     * Updates the question text to the question pointed by the current index.
     */
    private void updateQuestionText() {
        mQuestionText.setText(mViewModel.getQuestionId());

        // Disable answer buttons if the question has been answered
        if (mViewModel.isCurrentQuestionAnswered()) {
            setAnswerButtonsState(false);
        } else {
            setAnswerButtonsState(true);
        }
    }

    /**
     * Verifies the answer when the user pressed one of the answer button and display the toast accordingly.
     *
     * @param userAnswer true if the button the user pressed is the true button.
     */
    private void checkAnswer(boolean userAnswer) {
        boolean answer = mViewModel.getQuestionAnswer();
        if (!mViewModel.isCurrentQuestionCheated()) {
            if (answer == userAnswer) {
                createToastTop(R.string.toast_correct).show();
                mViewModel.setCorrectAnswer(mViewModel.getCorrectAnswer() + 1);
            } else {
                createToastTop(R.string.toast_incorrect).show();
            }
        } else {
            createToastTop(R.string.toast_cheat).show();
        }

        // Mark as answered
        mViewModel.setCurrentQuestionAnswered();
        setAnswerButtonsState(false);

        updateScoreText();
    }

    private void updateScoreText() {
        // Display score if all has been answered
        if (mViewModel.isAllAnswered()) {
            mResultText.setText(mViewModel.getResult());
        }
    }

    /**
     * Returns a Toast object located at the top center of the screen.
     * This is also a solution for the Challenge question in chapter 1.
     *
     * @param stringId the string resource to be used as the message.
     * @return a Toast object
     */
    private Toast createToastTop(int stringId) {
        Toast toast = Toast.makeText(this, stringId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 200);
        return toast;
    }

    /**
     * Sets the true and false buttons to be enabled or disabled.
     *
     * @param state the state.
     */
    private void setAnswerButtonsState(boolean state) {
        mFalseButton.setEnabled(state);
        mTrueButton.setEnabled(state);
    }
}
