package com.example.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CheatActivity : AppCompatActivity() {

    private val  EXTRA_ANSWER_IS_TRUE : String = "com.example.geoquiz.answer_is_true"
    private val  EXTRA_SHOWN_ANSWER : String = "show_answer"

    private var mAnswerIsTrue : Boolean = false
    private  lateinit var mAnswerTextView: TextView
    private lateinit var mShowAnswerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        //Получаем объект который был передан посредством метода startActivity()
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        mAnswerTextView = findViewById(R.id.answer_text_view)
        mShowAnswerButton = findViewById(R.id.show_answer_button)

        mShowAnswerButton.setOnClickListener{

            if (mAnswerIsTrue) mAnswerTextView.setText(R.string.true_button)
            else mAnswerTextView.setText(R.string.true_button)

            setAnswerShownResult(true)
        }

    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val intent = Intent()
        intent.putExtra(EXTRA_SHOWN_ANSWER, isAnswerShown)
        //Когда пользователь нажимает кнопку назад
        //вызывается onActivityResult, которому мы передаем код запроса и сам интент
        setResult(RESULT_OK,intent)
    }

    fun wasAnswerShown(intent : Intent) : Boolean{
        return intent.getBooleanExtra(EXTRA_SHOWN_ANSWER, false)
    }

    //Функция возвращающая интент с состоянием нажатия кнопки по ключу
    fun newIntent(packageContext: Context?, answerIsTrue: Boolean): Intent? {

        return Intent(packageContext, CheatActivity::class.java).putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
    }
}