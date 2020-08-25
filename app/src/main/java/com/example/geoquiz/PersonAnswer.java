package com.example.geoquiz;

public class PersonAnswer {
    private Question question;
    private boolean personAnswer;

    PersonAnswer(Question question, boolean isPersonAnswer){
        this.question = question;
        personAnswer = isPersonAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public boolean isPersonAnswer() {
        return personAnswer;
    }

    public void setPersonAnswer(boolean personAnswer) {
        this.personAnswer = personAnswer;
    }
}
