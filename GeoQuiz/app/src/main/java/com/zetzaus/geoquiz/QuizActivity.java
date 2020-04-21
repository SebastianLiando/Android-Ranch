package com.zetzaus.geoquiz;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    private Question[] mQuestionBank = {
            new Question(R.string.question_one, true),
            new Question(R.string.question_two, true),
            new Question(R.string.question_three, false),
            new Question(R.string.question_four, false),
            new Question(R.string.question_five, true),
    };

    private int mCurrentIndex = 0;

    // Constants
    private static final String INDEX_KEY = "index_key";

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
        }

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
    }

    /**
     * Updates the question text to the question pointed by the current index
     */
    private void updateQuestionText() {
        mQuestionText.setText(mQuestionBank[mCurrentIndex].getResId());
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
        } else {
            createToastTop(R.string.toast_incorrect).show();
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
        toast.setGravity(Gravity.TOP, 0, 0);
        return toast;
    }
}
