package com.something.KingdomSafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddTrainingFirstScreen extends AppCompatActivity {
private String trainingName, youtubeLink, compID, groupName, companyType, userFirstName, userLastName, trainingNametoRemove, userId, datedTrainingID;
private int questionNum;
private Button backButton, nextButton;
private boolean ingroup, fromManageTrainings;
private final String TAG = "ADDTRAININGFIRSTSCREEN";
private ArrayList<String> trainingNames, customTrainingsContent, customDocIDList, customTrainingIDList, uidList, userNameList;
private EditText trainingNameEditText, youtubeLinkEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training_first_screen);
        trainingNameEditText = findViewById(R.id.trainingNameEditText);
        youtubeLinkEditText = findViewById(R.id.youtubeLinkEditText);
        backButton = findViewById(R.id.homebutton8);
        nextButton = findViewById(R.id.addTrainingScreenOneNextButton);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            compID = myBundle.getString("Company ID");
            groupName = myBundle.getString("Group Name");
            datedTrainingID = myBundle.getString("Dated Training ID");
            companyType = myBundle.getString("Company Type");
            userId = myBundle.getString("User ID");
            fromManageTrainings = myBundle.getBoolean("From Manage Trainings");
            customDocIDList = myBundle.getStringArrayList("Custom Doc ID List");
            customTrainingIDList = myBundle.getStringArrayList("Custom Training ID List");
            userNameList = myBundle.getStringArrayList("User Name List");
            uidList = myBundle.getStringArrayList("UID List");
            ingroup = myBundle.getBoolean("ingroup");
            trainingNametoRemove = myBundle.getString("Remaining Training Name to Remove");
            questionNum = myBundle.getInt("Question Number");
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            customTrainingsContent = myBundle.getStringArrayList("Custom Trainings Content");
            trainingNames = myBundle.getStringArrayList("Training Names List");
            trainingName = myBundle.getString("User Training Name");
            youtubeLink = myBundle.getString("Youtube Link");
        }
        if(trainingName != null)
        {
            trainingNameEditText.setText(trainingName);
        }
        if(youtubeLink != null)
        {
            youtubeLinkEditText.setText(youtubeLink);
        }
        if(customTrainingsContent!=null && trainingNametoRemove!=null & customTrainingsContent.contains(trainingNametoRemove)) {
            customTrainingsContent.remove(customTrainingsContent.indexOf(trainingNametoRemove));
            Log.d(TAG, "CUSTOM TRAININGS CONTENT IN ADDTRAININGFIRST SCREEN AFTER REMOVING REMAINING TRAINING NAME: " + customTrainingsContent);
        }
        Log.d(TAG, "CUSTOM TRAINING CONTENT" + customTrainingsContent);
    }

    public void addTrainingScreenOneNextPressed(View v)
    {
        nextButton.setClickable(false);
        backButton.setClickable(false);
        trainingName = trainingNameEditText.getText().toString();
        if(trainingNames.contains(trainingName))
        {
            Toast.makeText(AddTrainingFirstScreen.this, "There's already a training with this name", Toast.LENGTH_SHORT).show();
            nextButton.setClickable(true);
            backButton.setClickable(true);
        }
        else {
            youtubeLink = youtubeLinkEditText.getText().toString();
            Intent generateQuestions = new Intent(AddTrainingFirstScreen.this, QuestionGeneratingScreen.class);
            generateQuestions.putExtra("Training Name", trainingName);
            generateQuestions.putExtra("Youtube Link", youtubeLink);
            if (questionNum == -1) {
                questionNum = 0;
            }
            generateQuestions.putExtra("Question Number", questionNum);
            generateQuestions.putExtra("Company ID", compID);
            generateQuestions.putExtra("ingroup", ingroup);
            generateQuestions.putExtra("User ID", userId);
            generateQuestions.putExtra("Dated Training ID", datedTrainingID);
            generateQuestions.putExtra("Group Name", groupName);
            generateQuestions.putStringArrayListExtra("User Name List", userNameList);
            generateQuestions.putStringArrayListExtra("UID List", uidList);
            generateQuestions.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
            generateQuestions.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
            generateQuestions.putExtra("From Manage Trainings", fromManageTrainings);
            generateQuestions.putExtra("Company Type", companyType);
            generateQuestions.putExtra("First Name", userFirstName);
            generateQuestions.putExtra("Last Name", userLastName);
            generateQuestions.putExtra("Training Names List", trainingNames);
            generateQuestions.putExtra("Custom Trainings Content", customTrainingsContent);
            startActivity(generateQuestions);
        }
    }

    public void backbuttonToReviewTrainingsPressed(View v)
    {
        backButton.setClickable(false);
        nextButton.setClickable(false);
        if(fromManageTrainings)
        {
            Intent backtoReviewTrainings = new Intent(AddTrainingFirstScreen.this, ManageTrainings.class);
            backtoReviewTrainings.putExtra("Company ID", compID);
            backtoReviewTrainings.putExtra("ingroup", ingroup);
            backtoReviewTrainings.putExtra("User ID", userId);
            backtoReviewTrainings.putExtra("Group Name", groupName);
            backtoReviewTrainings.putExtra("Dated Training ID", datedTrainingID);
            backtoReviewTrainings.putExtra("Company Type", companyType);
            backtoReviewTrainings.putExtra("First Name", userFirstName);
            backtoReviewTrainings.putStringArrayListExtra("User Name List", userNameList);
            backtoReviewTrainings.putStringArrayListExtra("UID List", uidList);
            backtoReviewTrainings.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
            backtoReviewTrainings.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
            backtoReviewTrainings.putExtra("Last Name", userLastName);
            backtoReviewTrainings.putExtra("Training Names List", trainingNames);
            backtoReviewTrainings.putExtra("Custom Trainings Content", customTrainingsContent);
            startActivity(backtoReviewTrainings);
        }
        else {
            Intent backtoReviewTrainings = new Intent(AddTrainingFirstScreen.this, ReviewTrainings.class);
            backtoReviewTrainings.putExtra("Company ID", compID);
            backtoReviewTrainings.putExtra("ingroup", ingroup);
            backtoReviewTrainings.putExtra("User ID", userId);
            backtoReviewTrainings.putExtra("Group Name", groupName);
            backtoReviewTrainings.putExtra("Company Type", companyType);
            backtoReviewTrainings.putExtra("First Name", userFirstName);
            backtoReviewTrainings.putExtra("Last Name", userLastName);
            backtoReviewTrainings.putExtra("Training Names List", trainingNames);
            backtoReviewTrainings.putExtra("Custom Trainings Content", customTrainingsContent);
            startActivity(backtoReviewTrainings);
        }
    }
}
