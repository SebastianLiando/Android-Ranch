package com.zetzaus.geoquiz;

import androidx.lifecycle.ViewModel;

/**
 * A {@link ViewModel} for {@link CheatActivity}.
 */
public class CheatViewModel extends ViewModel {

    private boolean mIsCheated = false;
    private boolean mIsAnswerTrue;

    /**
     * True if the question is cheated.
     *
     * @return True if the question is cheated.
     */
    public boolean isCheated() {
        return mIsCheated;
    }

    /**
     * Sets if the current question is cheated.
     *
     * @param cheated true if the current question is cheated.
     */
    public void setCheated(boolean cheated) {
        mIsCheated = cheated;
    }

    /**
     * True if the question's answer is true.
     *
     * @return True if the question's answer is true.
     */
    public boolean isAnswerTrue() {
        return mIsAnswerTrue;
    }

    /**
     * Sets the current question's answer.
     *
     * @param answerTrue true if the question's answer is true.
     */
    public void setAnswerTrue(boolean answerTrue) {
        mIsAnswerTrue = answerTrue;
    }
}
