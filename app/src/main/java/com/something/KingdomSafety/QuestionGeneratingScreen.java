package com.something.KingdomSafety;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.LogDescriptor;

import java.util.ArrayList;

public class QuestionGeneratingScreen extends AppCompatActivity {
private String trainingName, youtubeLink, questionTitleText, answer1, answer2, answer3, answer4, appendedQuestion, datedTrainingID;
private String compID, companyType, groupName, userFirstName, userLastName, setAppendedQuestion, userId, finalQuestionString, questionSetString;
private int questionNum, rightAnswer, indexOfTrainingName, questionStartIndex, questionNumTitle, finalQuestionSize, questionSize, yesClicked;
private Switch q1S, q2S, q3S, q4S;
private AlertDialog.Builder builder;
private AlertDialog alert;
private boolean ingroup, previousQuestionIntent, fromManageTrainings;
private Button previousQuestionButton, nextQuestionButton, doneButton;
private final String TAG = "QUESTIONGENERATINGSCREE";
private TextView questionBlankof5Title;
private ArrayList<String> customTrainingIDList, customDocIDList, userNameList, uidList;
private EditText questionTitle, option1, option2, option3, option4;
private ArrayList customQuizQuestions, pushedCustomQuizQuestions, trainingNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_generating_screen);
        questionBlankof5Title = findViewById(R.id.questionBlankof5Title);
        doneButton = findViewById(R.id.CustomQuizDoneButton);
        questionTitle = findViewById(R.id.questionTitleEditText);
        option1 = findViewById(R.id.option1EditText);
        option2 = findViewById(R.id.option2EditText);
        option3 = findViewById(R.id.option3EditText);
        option4 = findViewById(R.id.option4EditText);
        previousQuestionButton = findViewById(R.id.previousQuestionButton);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        q1S = findViewById(R.id.switch1);
        q2S = findViewById(R.id.switch2);
        q3S = findViewById(R.id.switch3);
        q4S = findViewById(R.id.switch4);
        customQuizQuestions = new ArrayList();
        builder = new AlertDialog.Builder(QuestionGeneratingScreen.this, R.style.MyDialogTheme);
        alert = builder.create();

        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            trainingName = myBundle.getString("Training Name");
            youtubeLink = myBundle.getString("Youtube Link");
            questionNum = myBundle.getInt("Question Number");
            questionNumTitle = myBundle.getInt("Question Num Title");
            ingroup = myBundle.getBoolean("ingroup");
            questionSize = myBundle.getInt("Question Size");
            userId = myBundle.getString("User ID");
            uidList = myBundle.getStringArrayList("UID List");
            userNameList = myBundle.getStringArrayList("User Name List");
            customDocIDList = myBundle.getStringArrayList("Custom Doc ID List");
            customTrainingIDList = myBundle.getStringArrayList("Custom Training ID List");
            fromManageTrainings = myBundle.getBoolean("From Manage Trainings");
            datedTrainingID = myBundle.getString("Dated Training ID");
            previousQuestionIntent = myBundle.getBoolean("From Previous Question");
            pushedCustomQuizQuestions = myBundle.getParcelableArrayList("Custom Trainings Content");
            compID = myBundle.getString("Company ID");
            groupName = myBundle.getString("Group Name");
            companyType = myBundle.getString("Company Type");
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            trainingNames = myBundle.getStringArrayList("Training Names List");
        }
        if(pushedCustomQuizQuestions!=null)
        {
            customQuizQuestions = pushedCustomQuizQuestions;
        }
        Log.d(TAG, "CUSTOM TRAININGS CONTENT: " + customQuizQuestions);
    }

    protected void onStart()
    {
        super.onStart();
        previousQuestionButton.setVisibility(View.VISIBLE);
        nextQuestionButton.setVisibility(View.VISIBLE);
        if(questionNum==-1 || questionNum==0)
        {
            questionNum=0;
            previousQuestionButton.setVisibility(View.INVISIBLE);
        }
        if(questionNum==-1)
        {
            questionSize = 0;
        }
        questionNumTitle = questionNum+1;
        Log.d(TAG, "Custom quiz QUESTION NUM: " + questionNum);
        //Log.d(TAG, "QUESTION NUM TITLE: " + questionNumTitle);
        questionBlankof5Title.setText("Question  " + questionNumTitle);
        Log.d(TAG, "Custom quiz question size: " + questionSize);
        Log.d(TAG, "Custom quiz content: " + customQuizQuestions);
        appendedQuestion = "question" + questionNum;
        indexOfTrainingName = customQuizQuestions.indexOf(trainingName);
            //Log.d(TAG, "CUSTOM TRAININGS CONTENT CONTAINS TITLETEXT, ANSWER 1 AND ANSWER 2..SHOULD AUTOFILL TEXT BOXES");
            if(customQuizQuestions.size() > (indexOfTrainingName+3+(questionNum*7))) {
                setAppendedQuestion = String.valueOf(customQuizQuestions.get(indexOfTrainingName + 3 + (questionNum * 7)));
                if (setAppendedQuestion.equals(appendedQuestion)) {
                    //Log.d(TAG, "THE SPECIFIED INDEX DOES EQUAL APPENDED QUESTION!!");
                    questionStartIndex = indexOfTrainingName + 3 + (questionNum * 7);
                    questionTitle.setText((String) customQuizQuestions.get(questionStartIndex + 1));
                    option1.setText((String) customQuizQuestions.get(questionStartIndex + 3));
                    option2.setText((String) customQuizQuestions.get(questionStartIndex + 4));
                    option3.setText((String) customQuizQuestions.get(questionStartIndex + 5));
                    option4.setText((String) customQuizQuestions.get(questionStartIndex + 6));
                    rightAnswer = (int)customQuizQuestions.get(questionStartIndex + 2);
                    switch (rightAnswer) {
                        case 1:
                            q1S.setChecked(true);
                            break;
                        case 2:
                            q2S.setChecked(true);
                            break;
                        case 3:
                            q3S.setChecked(true);
                            break;
                        case 4:
                            q4S.setChecked(true);
                            break;
                    }
                }
            }

            q1S.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    q2S.setChecked(false);
                    q3S.setChecked(false);
                    q4S.setChecked(false);
                }
            });
        q2S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q1S.setChecked(false);
                q3S.setChecked(false);
                q4S.setChecked(false);
            }
        });
        q3S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q1S.setChecked(false);
                q2S.setChecked(false);
                q4S.setChecked(false);
            }
        });
        q4S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q1S.setChecked(false);
                q2S.setChecked(false);
                q3S.setChecked(false);
            }
        });
    }

    public boolean updateCustomTrainings(int intent)
    {
        if(getAllContent(intent) & intent==1) {
            getAllContent(intent);
            if (!customQuizQuestions.contains(trainingName)) {
                customQuizQuestions.add(trainingName);
                customQuizQuestions.add(youtubeLink);
                customQuizQuestions.add(questionNumTitle);
                indexOfTrainingName = customQuizQuestions.indexOf(trainingName);
            }
            else
                {
                indexOfTrainingName = customQuizQuestions.indexOf(trainingName);
                customQuizQuestions.set(indexOfTrainingName + 2, String.valueOf(questionSize));
            }

            if(!(customQuizQuestions.contains(questionTitleText) && customQuizQuestions.contains(answer1) &&
                    customQuizQuestions.contains(answer2) && customQuizQuestions.contains(answer3) && customQuizQuestions.contains(answer4))) {
                        Log.d(TAG, "THIS IS A NEW QUESTION, SHOULD UPDATE QUESTION SIZE");
                        questionSize++;
                        //if(questionSize>=1)
                        //{
                            customQuizQuestions.set(indexOfTrainingName + 2, String.valueOf(questionSize));
                        //}
                        customQuizQuestions.add(appendedQuestion);
                        customQuizQuestions.add(questionTitleText);
                        customQuizQuestions.add(rightAnswer);
                        customQuizQuestions.add(answer1);
                        customQuizQuestions.add(answer2);
                        customQuizQuestions.add(answer3);
                        customQuizQuestions.add(answer4);
            }
            else {
                questionStartIndex = indexOfTrainingName + 3 + (questionNum * 7);
                if (customQuizQuestions.get(questionStartIndex + 1).equals(questionTitleText) && customQuizQuestions.get(questionStartIndex + 3).equals(answer1)
                        && customQuizQuestions.get(questionStartIndex + 4).equals(answer2) && customQuizQuestions.get(questionStartIndex + 5).equals(answer3)
                        && customQuizQuestions.get(questionStartIndex + 6).equals(answer4)) {
                    setAppendedQuestion = (String) customQuizQuestions.get(indexOfTrainingName + 3 + (questionNum * 7));
                    if (setAppendedQuestion.equals(appendedQuestion)) {
                        Log.d(TAG, "THE SPECIFIED INDEX DOES EQUAL APPENDED QUESTION!!");
                        customQuizQuestions.set(questionStartIndex + 1, questionTitleText);
                        customQuizQuestions.set(questionStartIndex + 2, rightAnswer);
                        customQuizQuestions.set(questionStartIndex + 3, answer1);
                        customQuizQuestions.set(questionStartIndex + 4, answer2);
                        customQuizQuestions.set(questionStartIndex + 5, answer3);
                        customQuizQuestions.set(questionStartIndex + 6, answer4);
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public void previousquestionbuttonpressed(View v)
    {
        previousQuestionButton.setClickable(false);
        yesClicked = 0;
        getAllContent(2);
        Log.d(TAG, "PREVIOUS QUESTION TITLE TEXT: " + questionTitleText);
        Log.d(TAG, "PREVIOUS QUESTION ANSWER 1: " + answer1);
        Log.d(TAG, "PREVIOUS QUESTION ANSWER 2: " + answer2);
        Log.d(TAG, "PREVIOUS QUESTION ANSWER 3: " + answer3);
        Log.d(TAG, "PREVIOUS QUESTION ANSWER 4: " + answer4);
        Log.d(TAG, "PREVIOUS QUESTION RIGHT ANSWER: " + rightAnswer);

        if(!questionTitleText.isEmpty() && !answer1.isEmpty() && !answer2.isEmpty() && !answer3.isEmpty() && !answer4.isEmpty() && rightAnswer!=0)
        {
            questionStartIndex = indexOfTrainingName + 3 + (questionNum * 7);
            if(!(customQuizQuestions.contains(questionTitleText) && customQuizQuestions.contains(answer1) &&
                    customQuizQuestions.contains(answer2) && customQuizQuestions.contains(answer3) && customQuizQuestions.contains(answer4))) {
                Log.d(TAG, "BLIP: ALL FIELDS ARE FILLED OUT, THIS SHOULD BE ADDED TO CUSTOM TRAININGS LIST");
                Log.d(TAG, "THIS IS A NEW QUESTION, SHOULD UPDATE QUESTION SIZE");
                questionSize++;
                customQuizQuestions.set(indexOfTrainingName + 2, String.valueOf(questionSize));
                customQuizQuestions.add(appendedQuestion);
                customQuizQuestions.add(questionTitleText);
                customQuizQuestions.add(rightAnswer);
                customQuizQuestions.add(answer1);
                customQuizQuestions.add(answer2);
                customQuizQuestions.add(answer3);
                customQuizQuestions.add(answer4);
            }
            previousQuestionButton.setVisibility(View.INVISIBLE);
            nextQuestionButton.setVisibility(View.INVISIBLE);
            Intent previousQuestion = new Intent(QuestionGeneratingScreen.this, QuestionGeneratingScreen.class);
            previousQuestion.putExtra("Company ID", compID);
            previousQuestion.putExtra("Training Name", trainingName);
            previousQuestion.putExtra("Youtube Link", youtubeLink);
            previousQuestion.putExtra("Question Size", questionSize);
            previousQuestion.putExtra("Dated Training ID", datedTrainingID);
            previousQuestion.putExtra("Question Number", questionNum - 1);
            previousQuestion.putExtra("Group Name", groupName);
            previousQuestion.putExtra("From Manage Trainings", fromManageTrainings);
            previousQuestion.putStringArrayListExtra("UID List", uidList);
            previousQuestion.putStringArrayListExtra("User Name List", userNameList);
            previousQuestion.putExtra("Company Type", companyType);
            previousQuestion.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
            previousQuestion.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
            previousQuestion.putExtra("From Previous Question", true);
            previousQuestion.putExtra("ingroup", ingroup);
            previousQuestion.putExtra("Question Num Title", questionNumTitle);
            previousQuestion.putExtra("User ID", userId);
            previousQuestion.putExtra("First Name", userFirstName);
            previousQuestion.putExtra("Last Name", userLastName);
            previousQuestion.putExtra("Training Names List", trainingNames);
            previousQuestion.putExtra("Custom Trainings Content", customQuizQuestions);
            startActivity(previousQuestion);
        }
        else if(!questionTitleText.isEmpty() || !answer1.isEmpty() || !answer2.isEmpty() || !answer3.isEmpty() || !answer4.isEmpty() || rightAnswer!=0)
        {
            previousQuestionButton.setClickable(true);
            Log.d(TAG, "BLIP: THERE'S AT LEAST ONE FIELD FILLED OUT");
            if(questionTitleText.isEmpty())
            {
                builder.setMessage("Returning to the previous question will erase the current question's entered information " +
                        "because the question title isn't filled out. Would you still like to continue?");
            }
            else if(answer1.isEmpty())
            {
                builder.setMessage("Returning to the previous question will erase the current question's entered information " +
                        "because the first answer choice isn't filled out. Would you still like to continue?");
            }
            else if(answer2.isEmpty())
            {
                builder.setMessage("Returning to the previous question will erase the current question's entered information " +
                        "because the second answer isn't filled out. Would you still like to continue?");
            }
            else if(answer3.isEmpty())
            {
                builder.setMessage("Returning to the previous question will erase the current question's entered information " +
                        "because the third answer choice isn't filled out. Would you still like to continue?");
            }
            else if(answer4.isEmpty())
            {
                builder.setMessage("Returning to the previous question will erase the current question's entered information " +
                        "because the fourth answer choice isn't filled out. Would you still like to continue?");
            }
            else if(rightAnswer==0)
            {
                builder.setMessage("Returning to the previous question will erase the current question's entered information " +
                        "because no answer choice was marked as correct. Would you still like to continue?");
            }
            builder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    previousQuestionButton.setVisibility(View.INVISIBLE);
                    nextQuestionButton.setVisibility(View.INVISIBLE);
                    Intent previousQuestion = new Intent(QuestionGeneratingScreen.this, QuestionGeneratingScreen.class);
                    previousQuestion.putExtra("Company ID", compID);
                    previousQuestion.putExtra("Training Name", trainingName);
                    previousQuestion.putExtra("Youtube Link", youtubeLink);
                    previousQuestion.putExtra("Question Size", questionSize);
                    previousQuestion.putExtra("Dated Training ID", datedTrainingID);
                    previousQuestion.putExtra("Question Number", questionNum - 1);
                    previousQuestion.putExtra("Group Name", groupName);
                    previousQuestion.putExtra("From Manage Trainings", fromManageTrainings);
                    previousQuestion.putStringArrayListExtra("UID List", uidList);
                    previousQuestion.putStringArrayListExtra("User Name List", userNameList);
                    previousQuestion.putExtra("Company Type", companyType);
                    previousQuestion.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
                    previousQuestion.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
                    previousQuestion.putExtra("From Previous Question", true);
                    previousQuestion.putExtra("ingroup", ingroup);
                    previousQuestion.putExtra("Question Num Title", questionNumTitle);
                    previousQuestion.putExtra("User ID", userId);
                    previousQuestion.putExtra("First Name", userFirstName);
                    previousQuestion.putExtra("Last Name", userLastName);
                    previousQuestion.putExtra("Training Names List", trainingNames);
                    previousQuestion.putExtra("Custom Trainings Content", customQuizQuestions);
                    startActivity(previousQuestion);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.hide();
                }
            });
            alert = builder.create();
            alert.show();
        }
        else
        {
            previousQuestionButton.setVisibility(View.INVISIBLE);
            nextQuestionButton.setVisibility(View.INVISIBLE);
            Intent previousQuestion = new Intent(QuestionGeneratingScreen.this, QuestionGeneratingScreen.class);
            previousQuestion.putExtra("Company ID", compID);
            previousQuestion.putExtra("Training Name", trainingName);
            previousQuestion.putExtra("Youtube Link", youtubeLink);
            previousQuestion.putExtra("Question Size", questionSize);
            previousQuestion.putExtra("Dated Training ID", datedTrainingID);
            previousQuestion.putExtra("Question Number", questionNum - 1);
            previousQuestion.putExtra("Group Name", groupName);
            previousQuestion.putExtra("From Manage Trainings", fromManageTrainings);
            previousQuestion.putStringArrayListExtra("UID List", uidList);
            previousQuestion.putStringArrayListExtra("User Name List", userNameList);
            previousQuestion.putExtra("Company Type", companyType);
            previousQuestion.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
            previousQuestion.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
            previousQuestion.putExtra("From Previous Question", true);
            previousQuestion.putExtra("ingroup", ingroup);
            previousQuestion.putExtra("Question Num Title", questionNumTitle);
            previousQuestion.putExtra("User ID", userId);
            previousQuestion.putExtra("First Name", userFirstName);
            previousQuestion.putExtra("Last Name", userLastName);
            previousQuestion.putExtra("Training Names List", trainingNames);
            previousQuestion.putExtra("Custom Trainings Content", customQuizQuestions);
            startActivity(previousQuestion);
        }

    }

    public void backtFirstTrainingScreenPressed(View v)
    {
        builder.setCancelable(false)
                .setMessage("Returning to the previous screen will delete any entered information for current custom training. " +
                        "Are you sure you want to return to the previous screen?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "TRAINING CONTENT: " + customQuizQuestions);
                        Log.d(TAG, "TRAINING NAME: " + trainingName);
                        if(customQuizQuestions.size()>0 && customQuizQuestions.contains(trainingName)) {
                            indexOfTrainingName = customQuizQuestions.indexOf(trainingName);
                            finalQuestionString = String.valueOf(customQuizQuestions.get(indexOfTrainingName + 2));
                            finalQuestionSize = Integer.parseInt(finalQuestionString);
                            Log.d(TAG, "CUSTOM TRAININGS CONTENT ON BACKTOFIRSTRAININGSCREENPRESSED: " + customQuizQuestions);
                            for (int i = 1; i < (finalQuestionSize * 7) + 3; i++) {
                                customQuizQuestions.remove(indexOfTrainingName + 1);
                            }
                        }
                        Log.d(TAG, "CUSTOM TRAININGS CONTENT AFTER FOR LOOP HAS COMPLETED: " + customQuizQuestions);
                        Intent firstTrainingScreen = new Intent(QuestionGeneratingScreen.this, AddTrainingFirstScreen.class);
                        firstTrainingScreen.putExtra("Company ID", compID);
                        firstTrainingScreen.putExtra("Question Number", 0);
                        firstTrainingScreen.putExtra("Group Name", groupName);
                        firstTrainingScreen.putExtra("Dated Training ID", datedTrainingID);
                        firstTrainingScreen.putExtra("ingroup", ingroup);
                        firstTrainingScreen.putExtra("User Training Name", trainingName);
                        firstTrainingScreen.putStringArrayListExtra("UID List", uidList);
                        firstTrainingScreen.putStringArrayListExtra("User Name List", userNameList);
                        firstTrainingScreen.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
                        firstTrainingScreen.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
                        firstTrainingScreen.putExtra("Youtube Link", youtubeLink);
                        firstTrainingScreen.putExtra("User ID", userId);
                        firstTrainingScreen.putExtra("From Manage Trainings", fromManageTrainings);
                        firstTrainingScreen.putExtra("Company Type", companyType);
                        firstTrainingScreen.putExtra("First Name", userFirstName);
                        firstTrainingScreen.putExtra("Last Name", userLastName);
                        firstTrainingScreen.putExtra("Remaining Training Name to Remove", trainingName);
                        firstTrainingScreen.putExtra("Training Names List", trainingNames);
                        firstTrainingScreen.putExtra("Custom Trainings Content", customQuizQuestions);
                        startActivity(firstTrainingScreen);
                    }
                })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.hide();
                }
            });
        alert = builder.create();
        alert.show();
    }

    public void generateCustomQuizPressed(View v)
    {
        doneButton.setClickable(false);
        nextQuestionButton.setClickable(false);
        if(updateCustomTrainings(1)) {
            trainingNames.add(0, trainingName);
            if(fromManageTrainings)
            {
                Intent reviewTrainings = new Intent(QuestionGeneratingScreen.this, ManageTrainings.class);
                reviewTrainings.putExtra("Company ID", compID);
                reviewTrainings.putExtra("Group Name", groupName);
                reviewTrainings.putExtra("Company Type", companyType);
                reviewTrainings.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
                reviewTrainings.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
                reviewTrainings.putExtra("First Name", userFirstName);
                reviewTrainings.putStringArrayListExtra("UID List", uidList);
                reviewTrainings.putStringArrayListExtra("User Name List", userNameList);
                reviewTrainings.putExtra("Last Name", userLastName);
                reviewTrainings.putExtra("Dated Training ID", datedTrainingID);
                reviewTrainings.putExtra("User ID", userId);
                reviewTrainings.putExtra("ingroup", ingroup);
                reviewTrainings.putStringArrayListExtra("Custom Trainings Content", customQuizQuestions);
                Log.d(TAG, "ABOUT TO PUSH TO MANAGE TRAININGS NAMES LIST: " + trainingNames);
                reviewTrainings.putStringArrayListExtra("Training Names List", trainingNames);
                startActivity(reviewTrainings);
            }
            else {
                Intent reviewTrainings = new Intent(QuestionGeneratingScreen.this, ReviewTrainings.class);
                reviewTrainings.putExtra("Company ID", compID);
                reviewTrainings.putExtra("Group Name", groupName);
                reviewTrainings.putExtra("Company Type", companyType);
                reviewTrainings.putExtra("First Name", userFirstName);
                reviewTrainings.putExtra("Last Name", userLastName);
                reviewTrainings.putExtra("User ID", userId);
                reviewTrainings.putExtra("ingroup", ingroup);
                reviewTrainings.putStringArrayListExtra("Custom Trainings Content", customQuizQuestions);
                Log.d(TAG, "ABOUT TO PUSH FROM QUESTIONGENERATING CUSTOM TRAININGS CONTENT: " + customQuizQuestions);
                reviewTrainings.putStringArrayListExtra("Training Names List", trainingNames);
                startActivity(reviewTrainings);
            }
        }
    }

    public void nextQuestionButtonPressed(View v)
    {
        nextQuestionButton.setClickable(false);
            if(updateCustomTrainings(1)) {
                previousQuestionButton.setVisibility(View.INVISIBLE);
                nextQuestionButton.setVisibility(View.INVISIBLE);
                Intent nextQuestion = new Intent(QuestionGeneratingScreen.this, QuestionGeneratingScreen.class);
                nextQuestion.putExtra("Company ID", compID);
                nextQuestion.putExtra("Training Name", trainingName);
                nextQuestion.putExtra("Question Size", questionSize);
                nextQuestion.putExtra("Youtube Link", youtubeLink);
                nextQuestion.putExtra("Question Number", questionNum + 1);
                nextQuestion.putExtra("Group Name", groupName);
                nextQuestion.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
                nextQuestion.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
                nextQuestion.putStringArrayListExtra("UID List", uidList);
                nextQuestion.putStringArrayListExtra("User Name List", userNameList);
                nextQuestion.putExtra("Dated Training ID", datedTrainingID);
                nextQuestion.putExtra("Company Type", companyType);
                nextQuestion.putExtra("ingroup", ingroup);
                nextQuestion.putExtra("From Manage Trainings", fromManageTrainings);
                nextQuestion.putExtra("User ID", userId);
                nextQuestion.putExtra("First Name", userFirstName);
                nextQuestion.putExtra("Last Name", userLastName);
                nextQuestion.putExtra("Training Names List", trainingNames);
                nextQuestion.putExtra("Custom Trainings Content", customQuizQuestions);
                startActivity(nextQuestion);
            }
    }

    public boolean getAllContent(int intent) //1 is done pressed or next question, 2 is previous question
    {
        if(intent==1) {
            appendedQuestion = "question" + questionNum;
            questionTitleText = (String) questionTitle.getText().toString().trim();
            answer1 = (String) option1.getText().toString().trim();
            answer2 = (String) option2.getText().toString().trim();
            answer3 = (String) option3.getText().toString().trim();
            answer4 = (String) option4.getText().toString().trim();
            if (q1S.isChecked()) {
                rightAnswer = 1;
            } else if (q2S.isChecked()) {
                rightAnswer = 2;
            } else if (q3S.isChecked()) {
                rightAnswer = 3;
            } else if (q4S.isChecked()) {
                rightAnswer = 4;
            }
            else
            {
                Toast.makeText(QuestionGeneratingScreen.this, "Please identify which answer is correct using toggle button", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (questionTitleText.length() == 0 || questionTitleText.isEmpty()) {
                Toast.makeText(QuestionGeneratingScreen.this, "Please enter a valid Question Title", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (answer1.length() == 0 || answer1.isEmpty()) {
                Toast.makeText(QuestionGeneratingScreen.this, "Please enter a valid first answer choice", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (answer2.length() == 0 || answer2.isEmpty()) {
                Toast.makeText(QuestionGeneratingScreen.this, "Please enter a valid second answer choice", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (answer3.length() == 0 || answer3.isEmpty()) {
                Toast.makeText(QuestionGeneratingScreen.this, "Please enter a valid third answer choice", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (answer4.length() == 0 || answer4.isEmpty()) {
                Toast.makeText(QuestionGeneratingScreen.this, "Please enter a valid fourth answer choice", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
        else if(intent==2)
        {
            appendedQuestion = "question" + questionNum;
            questionTitleText = (String) questionTitle.getText().toString().trim();
            answer1 = (String) option1.getText().toString().trim();
            answer2 = (String) option2.getText().toString().trim();
            answer3 = (String) option3.getText().toString().trim();
            answer4 = (String) option4.getText().toString().trim();
            if (q1S.isChecked()) {
                rightAnswer = 1;
            } else if (q2S.isChecked()) {
                rightAnswer = 2;
            } else if (q3S.isChecked()) {
                rightAnswer = 3;
            } else if (q4S.isChecked()) {
                rightAnswer = 4;
            }

            return true;
        }
        else
        {
            return true;
        }
    }
     //training contents order: Training name, yt link, # of questions, question0, questionTitle, rightAnswer, option 1, option 2...option 4,
    // question1, questionTitle, rightAnswer, option1, option2, option3, option4
}
