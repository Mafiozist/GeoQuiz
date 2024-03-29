package com.example.geoquiz

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //last page 135
    private lateinit var mTrueButton : Button
    private lateinit var mFalseButton : Button
    private lateinit var mCheatButton : Button

    //Старая вариация кнопок с текстом
    //private lateinit var mNextButton : Button
    //private lateinit var mPrevButton : Button
    private lateinit var mQuestionTextView : TextView

    //Новые кнопки, чисто с использованием изображения
    //Насл от imageview
    private lateinit var mPrevButton : ImageButton
    private lateinit var mNextButton : ImageButton

    private lateinit var mAnswerTextView: TextView
    private  lateinit var mImageView: ImageView

    private val REQUEST_CODE_CHEAT : Int  = 0

    private val TAG : String = "MainActivity"
    private val KEY_INDEX : String = "index"
    private val ANSWERS_INDEX : String = "answers"
    private val HINTS_INDEX = "hints"
    private val PERSON_ANSWERS = "person_answers"

    private var mIsCheater : Boolean = false
    private var mHintsCount  = 3

    private var  mQuestionBank : MutableList<Question> = mutableListOf<Question>(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    );

    //Создается массив в ячейках которого значение false, т.е. на конкретные вопросы
    //не был дан ответ
    private var AnsweredBoolArray = BooleanArray(mQuestionBank.size)

    //Массив пользовательских ответов на вопрос
    private var mQuestionAnswers = mutableMapOf<Int,Boolean>()

    private var mCurrentIndex : Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null) mCurrentIndex = savedInstanceState.getInt(KEY_INDEX)

        //В коде java тут приводят к типу button, но по видемому
        // в kotlin это не требуется(может новая версия)
        mTrueButton = findViewById(R.id.true_button)
        mTrueButton.setOnClickListener {
            checkAnswer(true)
            isHasAnswerDo()
        }

        mFalseButton = findViewById(R.id.false_button)
        mFalseButton.setOnClickListener {
            checkAnswer(false)
            isHasAnswerDo()
        }


        mQuestionTextView = findViewById(R.id.question_text_view)

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

        //Добавление новой кнопки и activity посредством intent, которому сообщаем
        // о той activity которую нужно открыть
        mCheatButton = findViewById(R.id.cheat_button)
        mCheatButton.setOnClickListener{

            if(mHintsCount > 0) {
                //В данном случае используется явный explicit конструктор
                //Данная активность должна возвращать данные о том, подсмотрел ответ пользователь или нет
                //По видемому данные можно передовать вместе с Intent
                val answerIsTrue  : Boolean = mQuestionBank[mCurrentIndex].isAnswerTrue

                val intent : Intent? = CheatActivity().newIntent(this, answerIsTrue)

                //Метод позволяющий получить результат от дочерней активности
                startActivityForResult(intent, REQUEST_CODE_CHEAT)

            } else  Toast.makeText(this@MainActivity, R.string.hints_toast,Toast.LENGTH_SHORT).show()
        }
    }

    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK  ) return
        if(requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) return
            mIsCheater = CheatActivity().wasAnswerShown(data)
        }

        if(mIsCheater){
            mIsCheater = false
            --mHintsCount
        }

    }

    private fun updateQuestion() {
        //Log.d(TAG,"Updating question text", Exception())

        val question : Int = mQuestionBank[mCurrentIndex].textResId
        mQuestionTextView.setText(question)
    }

    private fun calculateSumTrueAnswer() : Int{
        var sum : Int = 0

        for((index,answer) in mQuestionAnswers) {
            //Проверка ответа и то, что ответ был принят(кнопка нажата)
            if (answer && AnsweredBoolArray[index])
                sum++
        }
        return sum
    }

    private fun showMessage(){
        var count = 0
        for (isAnswered in AnsweredBoolArray)
            if(isAnswered) count++

        if( (mQuestionAnswers.size == mQuestionBank.size) && (count == mQuestionBank.size) ) {

            Toast.makeText(this@MainActivity,"You completed " + calculateSumTrueAnswer() + " task(s) from " + mQuestionBank.size,Toast.LENGTH_LONG).show()
        }
    }


    private fun isHasAnswerDo(){
        if(mQuestionAnswers.containsKey(mCurrentIndex) && AnsweredBoolArray[mCurrentIndex])
        {
            mTrueButton.visibility = View.INVISIBLE
            mFalseButton.visibility = View.INVISIBLE

            //Необходимо добавить этот текст в strings и чтобы при этом он
            // динамически изменялся
            val res : Resources = resources

            mAnswerTextView.text =  String.format(
                res.getString(
                    R.string.answer_output,
                    mQuestionAnswers[mCurrentIndex].toString() ))

        }
        else{
            mTrueButton.visibility = View.VISIBLE
            mFalseButton.visibility = View.VISIBLE
            mAnswerTextView.setText("")
        }

    }

    //Получаем значение true или false  из массива Question
    //и по результату выдаем уведомление
    private fun checkAnswer(userPressedTrue: Boolean){
        val answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue
        val messageResId: Int

            if(userPressedTrue == answerIsTrue) {

                messageResId = R.string.correct_toast
                mQuestionAnswers[mCurrentIndex] = true
            }
            else{

                messageResId = R.string.incorrect_toast
                mQuestionAnswers[mCurrentIndex] = false
            }

        //Т.е. ответ на конкретный вопрос был дан
        AnsweredBoolArray[mCurrentIndex] = true
        Toast.makeText(this@MainActivity, messageResId, Toast.LENGTH_SHORT).show()
    }


    //Переопределение методов жизненного цикла
    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()

        isHasAnswerDo()
        Log.d(TAG, "onResume() called")

        //При продолжении работы необходимо проверить есть ли данные о
        //вводе пользователя
        //это необходимо потому что происходит проверка состояния введенных данных -
        // только по нажатию на кнопку
        //а при смене жизни активности, кнопка не может быть нажата т.к. программа только заработала
        isHasAnswerDo()
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

    //Фунция необходимая для сохранения данных при изменениях жизни activity
    private fun getBoolArrayFromMap(answers : MutableMap<Int,Boolean>?) : BooleanArray{

        //индексы важны т.к. они отвечают за id вопроса
        val tempArray : BooleanArray = BooleanArray(mQuestionBank.size)

        if(!answers.isNullOrEmpty())
            for( (index, answer) in answers ){
                tempArray[index] = answer
            }

        return tempArray
    }

    private fun getMapFromBoolArray(boolArray : BooleanArray?) : MutableMap<Int,Boolean>{
        val tempMap = mutableMapOf<Int,Boolean>()
        if(boolArray != null)
            for ( i in boolArray.indices){
                tempMap[i] = boolArray[i]
            }

        return tempMap
    }

    //Метод используется не только при поворотах, но и других изменениях
    // конфигурации времени выполнения
    //Переопределяем метод, который сохраняет жизнь переменным после вызова stop
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putBooleanArray(ANSWERS_INDEX,getBoolArrayFromMap(mQuestionAnswers))
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex)
        savedInstanceState.putInt(HINTS_INDEX, mHintsCount)
        savedInstanceState.putBooleanArray(PERSON_ANSWERS, AnsweredBoolArray)

        Log.d(TAG,"Сохранил данные(onSaveInstanceState)")+
                Log.i(TAG,"Введенные данные сохраненные программой ${getBoolArrayFromMap(mQuestionAnswers).size.toString()}")
    }

    //Если массив данных был сохранен не с начала, то программа выдает ошибку
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        Log.d(TAG,"Получил данные onRestoreInstanceState")
        mCurrentIndex = savedInstanceState!!.getInt(KEY_INDEX, 0)
        mQuestionAnswers = getMapFromBoolArray(savedInstanceState.getBooleanArray(ANSWERS_INDEX))
        mHintsCount = savedInstanceState.getInt(HINTS_INDEX)

        AnsweredBoolArray = savedInstanceState.getBooleanArray(PERSON_ANSWERS)!!

        Log.i(TAG,"Введенные данные сохраненные программой ${savedInstanceState.getBooleanArray(ANSWERS_INDEX)?.size.toString()}")
    }
}
