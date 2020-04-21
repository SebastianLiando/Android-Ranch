package com.zetzaus.geoquiz;

/**
 * The <code>Question</code> class is an entity class that holds information of a question.
 * The information includes the id of the String resource and whether the question is a true statement.
 */
public class Question {

    private int mResId;
    private boolean mAnswerTrue;

    /**
     * Constructor for the class <code>Question</code>.
     *
     * @param resId        the String resource id.
     * @param isAnswerTrue true if the answer is true, otherwise false.
     */
    public Question(int resId, boolean isAnswerTrue) {
        mResId = resId;
        mAnswerTrue = isAnswerTrue;
    }

    /**
     * Returns the String resource id.
     *
     * @return the String resource id.
     */
    public int getResId() {
        return mResId;
    }

    /**
     * Sets the String resource id of a question.
     *
     * @param resId the new String resource id.
     */
    public void setResId(int resId) {
        mResId = resId;
    }

    /**
     * Returns true if the question is a true statement.
     *
     * @return true if the question is a true statement.
     */
    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    /**
     * Sets whether the question is a true statement.
     *
     * @param answerTrue whether the question is a true statement.
     */
    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
