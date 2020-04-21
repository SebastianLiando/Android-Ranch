package com.zetzaus.geoquiz;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Make the true button listens to click. It will display a toast
        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastTop(R.string.toast_correct).show();
            }
        });

        // Make the false button listens to click. It will display a toast
        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastTop(R.string.toast_incorrect).show();
            }
        });
    }

    /**
     * Returns a Toast object located at the top center of the screen.
     * @param stringId the string resource to be used as the message.
     * @return a Toast object
     */
    private Toast createToastTop(int stringId){
        Toast toast = Toast.makeText(this, stringId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        return toast;
    }
}
