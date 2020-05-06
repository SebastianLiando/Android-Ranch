package com.zetzaus.geoquiz;

import java.util.Locale;

import androidx.lifecycle.ViewModel;

/**
 * A {@link ViewModel} for {@link QuizActivity}
 */
public class QuizViewModel extends ViewModel {
    private final Question[] mQuestionBank = {
            new Question(R.string.question_one, true),
            new Question(R.string.question_two, true),
            new Question(R.string.question_three, false),
            new Question(R.string.question_four, false),
            new Question(R.string.question_five, true),
    };

    private int mCurrentIndex = 0;
    private int mAnsweredMask = 0;
    private int mCheatMask = 0;
    private int mCorrectAnswer = 0;

    /**
     * Sets current question index to the next question.
     */
    public void nextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
    }

    /**
     * Sets current question index to the previous question.
     */
    public void prevQuestion() {
        if (mCurrentIndex == 0) mCurrentIndex = mQuestionBank.length - 1;
        else mCurrentIndex--;
    }

    /**
     * Returns current index.
     *
     * @return the current question index.
     */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * Sets current index.
     *
     * @param currentIndex the current index.
     */
    public void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
    }


    /**
     * Returns the String resource id for the current question.
     *
     * @return the String resource id.
     */
    public int getQuestionId() {
        return mQuestionBank[mCurrentIndex].getResId();
    }

    /**
     * Returns the current question's answer.
     *
     * @return the current question's answer.
     */
    public boolean getQuestionAnswer() {
        return mQuestionBank[mCurrentIndex].isAnswerTrue();
    }

    /**
     * Returns the bitmask for answered question.
     *
     * @return the bitmask for answered question.
     */
    public int getAnsweredMask() {
        return mAnsweredMask;
    }

    /**
     * Sets the bitmask for answered question.
     *
     * @param answeredMask the bitmask for answered question.
     */
    public void setAnsweredMask(int answeredMask) {
        mAnsweredMask = answeredMask;
    }

    /**
     * Returns the bitmask for cheated questions.
     *
     * @return the bitmask for cheated questions.
     */
    public int getCheatMask() {
        return mCheatMask;
    }

    /**
     * Sets the bitmask for cheated questions.
     *
     * @param cheatMask the bitmask for cheated questions.
     */
    public void setCheatMask(int cheatMask) {
        mCheatMask = cheatMask;
    }

    /**
     * Returns the number of correct answer.
     *
     * @return the number of correct answer.
     */
    public int getCorrectAnswer() {
        return mCorrectAnswer;
    }

    /**
     * Sets the number of correct answer.
     *
     * @param correctAnswer the number of correct answer.
     */
    public void setCorrectAnswer(int correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }

    /**
     * Returns true if all questions has been answered.
     *
     * @return true if all questions has been answered.
     */
    public boolean isAllAnswered() {
        // Checks if all bits are set
        int mask = (1 << (mQuestionBank.length)) - 1;
        return mask == mAnsweredMask;
    }

    /**
     * Returns the text for displaying the score.
     *
     * @return the text for displaying the score.
     */
    public String getResult() {
        return String.format(Locale.getDefault(), "%d / %d", mCorrectAnswer, mQuestionBank.length);
    }

    /**
     * Returns true if the current question has been cheated.
     *
     * @return true if the current question has been cheated.
     */
    public boolean isCurrentQuestionCheated() {
        return isCurrentBitSet(mCheatMask);
    }

    /**
     * Returns true if the current question has been answered.
     *
     * @return true if the current question has been answered.
     */
    public boolean isCurrentQuestionAnswered() {
        return isCurrentBitSet(mAnsweredMask);
    }

    /**
     * True if the bit positioned at the current index is 1.
     *
     * @param toCheck the bitmask to be checked.
     * @return True if the bit positioned at the current index is 1.
     */
    private boolean isCurrentBitSet(int toCheck) {
        int mask = 1 << mCurrentIndex;
        return (mask & toCheck) != 0;
    }

    /**
     * Marks the current question as answered by setting the bitmask for answered question.
     */
    public void setCurrentQuestionAnswered() {
        mAnsweredMask = setCurrentBit(mAnsweredMask);
    }

    /**
     * Marks the current question as cheated by setting the bitmask for cheated question.
     */
    public void setCurrentQuestionCheated() {
        mCheatMask = setCurrentBit(mCheatMask);
    }

    /**
     * Sets the current bit of the bitmask.
     *
     * @param toSet the bitmask to set.
     * @return the set bitmask.
     */
    private int setCurrentBit(int toSet) {
        return toSet | (1 << mCurrentIndex);
    }

    /**
     * Returns the number of cheats the user has used. This functions uses the Brian Kernighan's algorithm.
     *
     * @return the number of cheats the user has used
     */
    public int getCheatCount() {
        int count = 0;
        int tmpCheatMask = mCheatMask;

        while (tmpCheatMask != 0) {
            tmpCheatMask = tmpCheatMask & (tmpCheatMask - 1);
            count++;
        }

        return count;
    }

}
