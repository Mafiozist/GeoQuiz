package com.example.geoquiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
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

            //т.к.  круговая анимация была добавлена в sdk 21, а унас 19
            //Сделаем так, чтобы все модели меньше 21 sdk не могли проигрывать данный код,
            // а просто скрывали кнопку
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val cx: Int = mShowAnswerButton.width / 2
                val cy: Int = mShowAnswerButton.height / 2

                val radius: Float = mShowAnswerButton.width.toFloat()

                //Первый объект будет скрываться или отображаться в ходе анимации
                //Затем указывается центральная позиция, начальный и конечный радиус анимации
                val anim = ViewAnimationUtils
                    .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0f)

                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mShowAnswerButton.visibility = View.INVISIBLE
                    }
                })
                anim.start()
            }else{
                mShowAnswerButton.visibility = View.INVISIBLE
            }

        }

    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val intent = Intent()
        intent.putExtra(EXTRA_SHOWN_ANSWER, isAnswerShown)
        //Когда пользователь нажимает кнопку назад
        //вызывается onActivityResult, которому мы передаем код запроса и сам интент
        setResult(RESULT_OK, intent)
    }

    fun wasAnswerShown(intent: Intent) : Boolean{
        return intent.getBooleanExtra(EXTRA_SHOWN_ANSWER, false)
    }

    //Функция возвращающая интент с состоянием нажатия кнопки по ключу
    fun newIntent(packageContext: Context?, answerIsTrue: Boolean): Intent? {

        return Intent(packageContext, CheatActivity::class.java).putExtra(
            EXTRA_ANSWER_IS_TRUE,
            answerIsTrue
        )
    }
}