package com.example.geoquiz

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //last page 97
    private lateinit var mTrueButton : Button;
    private lateinit var mFalseButton : Button;

    //Старая вариация кнопок с текстом
    //private lateinit var mNextButton : Button;
    //private lateinit var mPrevButton : Button;
    private lateinit var mQuestionTextView : TextView;

    //Новые кнопки, чисто с использованием изображения
    //Насл от imageview
    private lateinit var mPrevButton : ImageButton;
    private lateinit var mNextButton : ImageButton;

    private lateinit var mAnswerTextView: TextView;

    val TAG : String = "MainActivity"
    val KEY_INDEX : String = "index"


    private var  mQuestionBank : MutableList<Question> = mutableListOf<Question>(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    );

    //Массив пользовательских ответов на вопрос
    private var mQuestionAnswers = mutableMapOf<Int,Boolean>()

    private var mCurrentIndex : Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        //Проверка на то, что данные сохранены и их можно забрать
        if(savedInstanceState!=null) {
            Log.d(TAG,"Получил данные")
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
        }

        //В коде java тут приводят к типу button, но по видимому
        // в kotlin это не требуется(может новая версия)
        mTrueButton = findViewById(R.id.true_button)
        mTrueButton.setOnClickListener { checkAnswer(true) }

        mFalseButton = findViewById(R.id.false_button)
        mFalseButton.setOnClickListener {
            checkAnswer(false)
        }


        mQuestionTextView = findViewById(R.id.question_text_view);

        //Получил ссылку на кнопку
        mNextButton = findViewById(R.id.next_button)
        mNextButton.setOnClickListener {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
            updateQuestion()
            isHasAnswerDo()
            showMessage()
        }

        updateQuestion()

        mPrevButton = findViewById(R.id.prev_button)
        mPrevButton.setOnClickListener {
            mCurrentIndex =
                if (mCurrentIndex == 0) mQuestionBank.size - 1 else (mCurrentIndex - 1)
            
            updateQuestion()
            isHasAnswerDo()
            showMessage()
        }

        mAnswerTextView = findViewById(R.id.answer_text_view)
    }

    private fun updateQuestion() {
        var question : Int = mQuestionBank[mCurrentIndex].textResId
        mQuestionTextView.setText(question)
    }

    private fun calculateSumTrueAnswer() : Int{
        var sum : Int = 0;
        for((index,answer) in mQuestionAnswers) {
            if (mQuestionBank[index].isAnswerTrue == answer) sum++;
        }
        return sum
    }

    private fun showMessage(){
        if(mQuestionAnswers.size == mQuestionBank.size)
        {
            Toast.makeText(this@MainActivity,"You completed " + calculateSumTrueAnswer() + " task(s) from " + mQuestionBank.size,Toast.LENGTH_LONG).show()
        }
    }

    private fun isHasAnswerDo() : Boolean{
        if(mQuestionAnswers.containsKey(mCurrentIndex))
        {
            mTrueButton.visibility = View.INVISIBLE
            mFalseButton.visibility = View.INVISIBLE
            mAnswerTextView.setText("Your answer is " + mQuestionAnswers[mCurrentIndex])
            return true
        }
        else{
            mTrueButton.visibility = View.VISIBLE
            mFalseButton.visibility = View.VISIBLE
            mAnswerTextView.setText("")
            return false
        }

    }


    //Получаем значение true или false  из массива Question
    //и по результату выдаем уведомление
    private fun checkAnswer(userPressedTrue: Boolean){
        var answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue
        var messageResId = 0

        if(userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast
            mQuestionAnswers[mCurrentIndex] = true
        }
        else  {
            messageResId = R.string.incorrect_toast
            mQuestionAnswers[mCurrentIndex] = false
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }


    //Переопределение методов жизненного цикла
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    //Метод используется не только при поворотах, но и других изменениях
    // конфигурации времени выполнения
    //Переопределяем метод, который сохраняет жизнь переменным после вызова stop
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(TAG,"Сохранил данные(onSaveInstanceState)")
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex)
    }
}
