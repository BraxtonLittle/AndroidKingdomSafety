package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupCreatedView extends AppCompatActivity {
TextView congratsMessage;
private String compID, trainingID;
private FirebaseFirestore db;
private Object[] userInfo;
private DateFormat df;
private Button adminScreen, shareCode, backButton;
private ArrayList<String> trainingsContents, trainingNames, customTrainingsContent;
private ArrayList <String> trainingDocIds, uidList, nameList;
private LottieAnimationView myLoadingView;
private Date dateClass;
private HashMap<String, String> myMap;
private ArrayList questionField;
private int indexOfTrainingStart, numOfQuestions, indexOfQuestionStart, numTrainingsAdded;
private String userId, groupName, currentDate, companyType, userFirstName, userLastName, videoURL, questionBlank;
private final String TAG = "GROUPCREATEDVIEW";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_created_view);
        trainingDocIds = new ArrayList<>();
        congratsMessage = findViewById(R.id.congratsmessage);
        shareCode = findViewById(R.id.shareCodeButton);
        shareCode.setVisibility(View.INVISIBLE);
        backButton = findViewById(R.id.groupcreatedbackbutton);
        myLoadingView = findViewById(R.id.groupCreatedLottieAnimationView);
        myLoadingView.setVisibility(View.INVISIBLE);
        adminScreen = findViewById(R.id.togrouplist);
        adminScreen.setVisibility(View.INVISIBLE);
        df = new SimpleDateFormat("MM-dd-yyyy");
        dateClass = new Date();
        currentDate = df.format(dateClass);
        Log.d(TAG, "CURRENT DATE: " + currentDate);
        Bundle myBundle = getIntent().getExtras();
        if (myBundle != null) {
            compID = myBundle.getString("Company ID");
            trainingsContents = myBundle.getStringArrayList("Trainings Content");
            customTrainingsContent = myBundle.getStringArrayList("Custom Trainings Content");
            Log.d(TAG, "TRAININGS CONTENT: " + trainingsContents);
            groupName = myBundle.getString("Group Name");
            companyType = myBundle.getString("Company Type");
            trainingNames = myBundle.getStringArrayList("Training Names List");
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            userId = myBundle.getString("User ID");
            Log.d(TAG, "USER FIRST NAME AND LAST NAME: " + userFirstName + ", " + userLastName);
        }
        congratsMessage.setText("Your group code is: " + compID + " Save this and share it with your employees so they can join your group and you can get started staying SAFE!");
        db = FirebaseFirestore.getInstance();
        userInfo = new Object[6];
        userInfo[0] = userId;
        userInfo[1] = true; //isAdmin
        userInfo[2] = false; //watchedVideo
        userInfo[3] = false; //taken test
        userInfo[4] = false; //passed test
        userInfo[5] = 0; //score
        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put(userId, Arrays.asList(userInfo));
        userDoc.put("Company ID", compID);
        userDoc.put("dateStarted", currentDate);
        userDoc.put("trade", companyType);
        Log.d(TAG, "TRAINING NAMES: " + trainingNames);
        numTrainingsAdded = 0;
        for(int i = 0; i<trainingNames.size(); i++)
        {
            Map<String, Object> trainingDoc = new HashMap<>();
            indexOfTrainingStart = trainingsContents.indexOf(trainingNames.get(i));
            videoURL = trainingsContents.get(indexOfTrainingStart+1);
            numOfQuestions = Integer.valueOf(trainingsContents.get(indexOfTrainingStart+2));
            trainingDoc.put("numberOfQuestions", numOfQuestions);
            for(int j = 0; j<numOfQuestions; j++)
            {
                questionField = new ArrayList(6);
                questionBlank = "question" + j;
                indexOfQuestionStart = trainingsContents.indexOf(trainingNames.get(i)) + 3 + (j*7);
                Log.d(TAG, "INDEX OF QUESTION START: " + indexOfQuestionStart);
                //indexOfQuestionStart = trainingsContents.indexOf(trainingNames.get(i)+3);
                questionField.add(trainingsContents.get(indexOfQuestionStart+1));
                questionField.add(Integer.valueOf(trainingsContents.get(indexOfQuestionStart+2)));
                questionField.add(trainingsContents.get(indexOfQuestionStart+3));
                questionField.add(trainingsContents.get(indexOfQuestionStart+4));
                questionField.add(trainingsContents.get(indexOfQuestionStart+5));
                questionField.add(trainingsContents.get(indexOfQuestionStart+6));
                Log.d(TAG, "QUESTION FIELD LIST VALUE: " + questionField);
                trainingDoc.put(questionBlank, (List<Object>)questionField);
            }
            trainingDoc.put("videoURL", videoURL);
            trainingDoc.put("trainingID", compID + "-" + i);
            //Log.d(TAG, "TRAINING: " + trainingNames.get(i) + " TRAINING DOC: " + trainingDoc);
            myLoadingView.playAnimation();
            myLoadingView.setVisibility(View.VISIBLE);
            db.collection("companies").document(groupName).collection("CustomTrainings").document(trainingNames.get(i)).set(trainingDoc, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "SUCCESSFULLY CREATED CUSTOM TRAININGS SUBCOLLECTION");
                            db.collection("companies").document(groupName)
                                    .set(userDoc, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User Info Successfully Uploaded to Document");
                                                Log.d(TAG, "INITIAL NUMTRAININGSADDED: " + numTrainingsAdded);
                                                while(numTrainingsAdded<51)
                                                {
                                                    for(int c = 0; c<trainingNames.size(); c++)
                                                    {
                                                        if(numTrainingsAdded<52)
                                                        {
                                                            Log.d(TAG, "NUMTRAININGS ADDED AT BEGINNING OF FOR LOOP: " + numTrainingsAdded);
                                                            trainingID = compID + "-" + c;
                                                            Log.d(TAG, "TRAINING ID AT VALUE " + numTrainingsAdded + ": " + trainingID);
                                                            myMap = new HashMap<>();
                                                            myMap.put("trainingID", trainingID);
                                                            db.collection("companies").document(groupName).collection("Trainings")
                                                                    .document(String.valueOf(numTrainingsAdded)).set(myMap, SetOptions.merge());
                                                            Log.d(TAG, "NUMTRAININGS ADDED WITHIN FOR LOOP: " + numTrainingsAdded);
                                                            numTrainingsAdded++;
                                                        }
                                                    }
                                                }
                                                Log.d(TAG, "FINAL NUMOFTRAININGSADDED: " + numTrainingsAdded);
                                                myLoadingView.cancelAnimation();
                                                myLoadingView.setVisibility(View.INVISIBLE);
                                                shareCode.setVisibility(View.VISIBLE);
                                                adminScreen.setVisibility(View.VISIBLE);
                                            }
                                            else {
                                                myLoadingView.cancelAnimation();
                                                myLoadingView.setVisibility(View.INVISIBLE);
                                                Toast.makeText(GroupCreatedView.this, "Failed to add User Info to Firestore", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    myLoadingView.cancelAnimation();
                    myLoadingView.setVisibility(View.INVISIBLE);
                    Toast.makeText(GroupCreatedView.this, "Failed to add User Info to Firestore", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adminScreen.setClickable(true);
        shareCode.setClickable(true);
        backButton.setClickable(true);
    }

    public void toGroupList(View v)
    {
        adminScreen.setClickable(false);
        shareCode.setClickable(false);
        backButton.setClickable(false);
        uidList = new ArrayList<>();
        uidList.add(userId);
        nameList = new ArrayList<>();
        nameList.add(userFirstName + " " + userLastName);
        Intent adminScreen = new Intent(GroupCreatedView.this, AdminScreen.class);
        adminScreen.putExtra("Company Name", groupName);
        adminScreen.putExtra("Company ID", compID);
        adminScreen.putExtra("User Name List", nameList);
        adminScreen.putExtra("User First Name", userFirstName);
        adminScreen.putExtra("User Last Name", userLastName);
        adminScreen.putExtra("UID List", uidList);
        adminScreen.putExtra("User ID", userId);
        adminScreen.putExtra("Dated Training ID", String.valueOf(0));
        startActivity(adminScreen);
    }

    public void startShareCode(View v)
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "Share Using";
        String shareBody = "You have been invited to a Kingdom Safety training group! Download the " +
                "Kingdom Safety app on the iOS App Store and Google Play, then use this code to" +
                " join the group: " + compID + "\n" + "\n" +
                "iOS: https://apps.apple.com/us/app/kingdom-safety/id1483537269" + "\n" + "\n" +
                "Google Play: https://play.google.com/store/apps/details?id=com.something.welcomescreen";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
        startActivity(Intent.createChooser(shareIntent, shareText));
    }

    public void backtoReviewTrainings(View v)
    {
        adminScreen.setClickable(false);
        shareCode.setClickable(false);
        backButton.setClickable(false);
        Intent reviewTrainings = new Intent(GroupCreatedView.this, ReviewTrainings.class);
        reviewTrainings.putExtra("User ID", userId);
        reviewTrainings.putExtra("Group Name", groupName);
        reviewTrainings.putExtra("Company ID", compID);
        reviewTrainings.putStringArrayListExtra("Custom Trainings Content", customTrainingsContent);
        reviewTrainings.putStringArrayListExtra("Training Names List", trainingNames);
        reviewTrainings.putExtra("Company Type", companyType);
        reviewTrainings.putExtra("First Name", userFirstName);
        reviewTrainings.putExtra("Last Name", userLastName);
        startActivity(reviewTrainings);
    }
}
