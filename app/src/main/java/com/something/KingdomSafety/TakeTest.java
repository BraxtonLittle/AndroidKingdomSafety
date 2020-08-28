package com.something.KingdomSafety;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TakeTest extends AppCompatActivity {
    private final static String TAG = "TAKETEST";
    private FirebaseFirestore db;
    private String companyName, userID, questionString, datedTrainingID;
    private ArrayList<String> quizzes;
    private ArrayList receivedList;
    private TextView questionTextView;
    private AlertDialog.Builder builder;
    private AlertDialog alert;
    private int questionSet, correctAnswers, numberOfQuestions, indexOfQuestionNum;
    private Handler myHandler;
    private String dateStarted, userFirstName, userLastName, formattedDate;
    private long recievedQuestionNum;
    private Button answer1Button, answer2Button, answer3Button, answer4Button;
    private String[] questionList, ans1List, ans2List, ans3List, ans4List;
    private int[] rightAnswerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_test);
        db = FirebaseFirestore.getInstance();
        questionTextView = findViewById(R.id.testQuestiontextview);
        answer1Button = findViewById(R.id.taketestanswer1);
        answer2Button = findViewById(R.id.taketestanswer2);
        answer3Button = findViewById(R.id.taketestanswer3);
        answer4Button = findViewById(R.id.taketestanswer4);
        myHandler = new Handler();
        builder = new AlertDialog.Builder(TakeTest.this, R.style.MyDialogTheme);
        alert = builder.create();
        questionSet = 0;
        correctAnswers = 0;
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            companyName = myBundle.getString("Company Name");
            userID = myBundle.getString("User ID");
            formattedDate = myBundle.getString("Today's Date");
            userFirstName = myBundle.getString("User First Name");
            userLastName = myBundle.getString("User Last Name");
            recievedQuestionNum = myBundle.getLong("Number of Questions");
            quizzes = myBundle.getStringArrayList("Quiz Arraylist");
            Log.d(TAG, "QUIZ ARRAYLIST: " + quizzes);
            datedTrainingID = myBundle.getString("Dated Training ID");
            receivedList = myBundle.getParcelableArrayList("User Info List");
            Log.d(TAG, "USER INFO LIST FROM TAKETEST BUNDLE: " + receivedList);
            dateStarted = myBundle.getString("Date Started");
        }
        numberOfQuestions = (int)recievedQuestionNum;
        Log.d(TAG, "NUMBER OF QUESTIONS: " + numberOfQuestions);
        questionList = new String[numberOfQuestions];
        ans1List = new String[numberOfQuestions];
        ans2List = new String[numberOfQuestions];
        ans3List = new String[numberOfQuestions];
        ans4List = new String[numberOfQuestions];
        rightAnswerList = new int[numberOfQuestions];
        Log.d(TAG, "RECEIVED USER INFO LIST: " + receivedList);
        Log.d(TAG, "Company Name: " + companyName);
        Log.d(TAG, "QUIZ ARRAYLIST" + quizzes);
    }


    public void readQuizzes()
    {
        for(int i = 0; i<numberOfQuestions; i++)
        {
            questionString = "question" + i;
            indexOfQuestionNum = quizzes.indexOf(questionString);
            questionList[i] = quizzes.get(indexOfQuestionNum+1);
            rightAnswerList[i] = Integer.valueOf(quizzes.get(indexOfQuestionNum+2));
            ans1List[i] = quizzes.get(indexOfQuestionNum+3);
            ans2List[i] = quizzes.get(indexOfQuestionNum+4);
            ans3List[i] = quizzes.get(indexOfQuestionNum+5);
            ans4List[i] = quizzes.get(indexOfQuestionNum+6);
        }

    }

    public void setData(int j)
    {
        if(j>numberOfQuestions-1)
        {
            Intent doneQuiz = new Intent(TakeTest.this, FinishedQuizScreen.class);
            doneQuiz.putExtra("Correct Answers", correctAnswers);
            Log.d(TAG, "CORRECT ANSWERS: " + correctAnswers);
            doneQuiz.putExtra("Company Name", companyName);
            doneQuiz.putExtra("Number of Questions", numberOfQuestions);
            doneQuiz.putExtra("Today's Date", formattedDate);
            doneQuiz.putExtra("Dated Training ID", datedTrainingID);
            doneQuiz.putExtra("User ID", userID);
            doneQuiz.putExtra("User Last Name", userLastName);
            doneQuiz.putExtra("User First Name", userFirstName);
            doneQuiz.putExtra("Date Started", dateStarted);
            Log.d(TAG, "PUSHED USER INFO LIST FROM TAKETESTSCREEN: " + receivedList);
            doneQuiz.putParcelableArrayListExtra("User Info List", receivedList);
            startActivity(doneQuiz);
        }
        else {
            questionTextView.setText(questionList[j]);
            answer1Button.setText(ans1List[j]);
            answer2Button.setText(ans2List[j]);
            answer3Button.setText(ans3List[j]);
            answer4Button.setText(ans4List[j]);
            answer1Button.setEnabled(true);
            answer2Button.setEnabled(true);
            answer3Button.setEnabled(true);
            answer4Button.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
                builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        questionSet++;
                        setData(questionSet);
                    }
                });
        questionSet = 0;
        correctAnswers = 0;
        readQuizzes();
        Log.d(TAG, "STARTING CORRECTANSWERS VARIABLE VALUE: " + correctAnswers);
        setData(questionSet);
    }

    public void answer1buttonpressed(View v)
    {
        Log.d(TAG, "BUTTON 1 HAS BEEN PRESSED");
        if(rightAnswerList[questionSet] == 1)
        {
            correctAnswers++;
            builder.setMessage("Correct! You got it right");
        }
        else
        {
            builder.setMessage("Incorrect! That's not the right answer");
        }
        Log.d(TAG, "CORRECT ANSWERS SINCE SCREEN: " + questionSet + ", num: " + correctAnswers);
        alert = builder.create();
        alert.show();
    }

    public void answer2buttonpressed(View v)
    {
        Log.d(TAG, "BUTTON 2 HAS BEEN PRESSED");
        if(rightAnswerList[questionSet] == 2)
        {
            correctAnswers++;
            builder.setMessage("Correct! You got it right");
        }
        else
        {
            builder.setMessage("Incorrect! That's not the right answer");
        }
        alert = builder.create();
        alert.show();
    }

    public void answer3buttonpressed(View v)
    {
        Log.d(TAG, "BUTTON 3 HAS BEEN PRESSED");
        if(rightAnswerList[questionSet] == 3)
        {
            correctAnswers++;
            builder.setMessage("Correct! You got it right");
        }
        else
        {
            builder.setMessage("Incorrect! That's not the right answer");
        }
        alert = builder.create();
        alert.show();
    }

    public void answer4buttonpressed(View v)
    {
        Log.d(TAG, "BUTTON 4 HAS BEEN PRESSED");
        if(rightAnswerList[questionSet] == 4)
        {
            correctAnswers++;
            builder.setMessage("Correct! You got it right");
        }
        else
        {
            builder.setMessage("Incorrect! That's not the right answer");
        }
        alert = builder.create();
        alert.show();
    }

    public void backbuttonpressed(View v)
    {
        Intent backtoUserScreen = new Intent(TakeTest.this, UsersScreen.class);
        backtoUserScreen.putExtra("Company Name", companyName);
        backtoUserScreen.putExtra("Watched Video", true);
        backtoUserScreen.putExtra("User ID", userID);
        backtoUserScreen.putExtra("User First Name", userFirstName);
        backtoUserScreen.putExtra("User Last Name", userLastName);
        backtoUserScreen.putExtra("Dated Training ID", datedTrainingID);
        backtoUserScreen.putExtra("User Info List", receivedList);
        Log.d(TAG, "RECEIVED USER INFO LIST IN TAKETEST: " + receivedList);
        backtoUserScreen.putExtra("Date Started", dateStarted);
        startActivity(backtoUserScreen);
    }
}
