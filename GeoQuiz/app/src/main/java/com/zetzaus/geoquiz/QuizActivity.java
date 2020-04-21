package com.zetzaus.geoquiz;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity is the main activity of the application. It displays a question in a form of a statement which
 * the user has to determine if it is true or false. It also has a next button to juggle between the questions.
 */
public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionText;
    private TextView mResultText;

    private final Question[] mQuestionBank = {
            new Question(R.string.question_one, true),
            new Question(R.string.question_two, true),
            new Question(R.string.question_three, false),
            new Question(R.string.question_four, false),
            new Question(R.string.question_five, true),
    };

    private int mCurrentIndex = 0;
    private int mAnsweredMask = 0;
    private int mCorrectAnswer = 0;

    // Constants
    private static final String INDEX_KEY = "index_key";
    private static final String ANSWER_MASK_KEY = "answer_mask_key";
    private static final String CORRECT_ANSWER_KEY = "correct_answer_key";

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

        // Restore state
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(INDEX_KEY);
            mAnsweredMask = savedInstanceState.getInt(ANSWER_MASK_KEY);
            mCorrectAnswer = savedInstanceState.getInt(CORRECT_ANSWER_KEY);
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
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestionText();
            }
        });

        // Make the previous button listens to click. It will replace with the previous question.
        // Is a solution for Chapter 2 challenge problem
        mPrevButton = findViewById(R.id.button_prev);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 == -1) ? (mQuestionBank.length - 1) : (mCurrentIndex - 1);
                updateQuestionText();
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
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestionText();
            }
        });

        // Setup result text
        mResultText = findViewById(R.id.text_result);
        if (isAllAnswered()){
            mResultText.setText(String.format(Locale.getDefault(), "%d / %d", mCorrectAnswer, mQuestionBank.length));
        }
    }

    /**
     * Stores the index of the current question to the bundle.
     *
     * @param outState the bundle.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INDEX_KEY, mCurrentIndex);
        outState.putInt(ANSWER_MASK_KEY, mAnsweredMask);
        outState.putInt(CORRECT_ANSWER_KEY, mCorrectAnswer);
    }

    /**
     * Updates the question text to the question pointed by the current index.
     */
    private void updateQuestionText() {
        mQuestionText.setText(mQuestionBank[mCurrentIndex].getResId());

        // Disable answer buttons if the question has been answered
        if (isCurrentQuestionAnswered()) {
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
        boolean answer = mQuestionBank[mCurrentIndex].isAnswerTrue();
        if (answer == userAnswer) {
            createToastTop(R.string.toast_correct).show();
            mCorrectAnswer++;
        } else {
            createToastTop(R.string.toast_incorrect).show();
        }

        // Mark as answered
        mAnsweredMask = mAnsweredMask | (1 << mCurrentIndex);
        setAnswerButtonsState(false);

        // Display score if all has been answered
        if (isAllAnswered()){
            mResultText.setText(String.format(Locale.getDefault(), "%d / %d", mCorrectAnswer, mQuestionBank.length));
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
     * Returns true if the current question has been answered.
     *
     * @return true if the current question has been answered.
     */
    private boolean isCurrentQuestionAnswered() {
        int mask = 1 << mCurrentIndex;

        return (mAnsweredMask & mask) != 0;
    }

    /**
     * Returns true if all questions has been answered.
     *
     * @return true if all questions has been answered.
     */
    private boolean isAllAnswered() {
        int mask = (1 << (mQuestionBank.length)) - 1;
        return mask == mAnsweredMask;
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
