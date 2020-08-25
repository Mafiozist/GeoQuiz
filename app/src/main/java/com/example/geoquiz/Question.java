package com.example.geoquiz;

public class Question {
    //Тип инт потому что в нем будет содержаться
    //идентификатор ресурса
    private  int mTextResId;
    private boolean mAnswerTrue;

    Question(int textResId, boolean answerTrue){
        this.mTextResId = textResId;
        this.mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }
    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }
    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }
    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
