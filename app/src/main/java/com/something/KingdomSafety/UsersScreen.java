package com.something.KingdomSafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersScreen extends AppCompatActivity {
private FirebaseFirestore db;
private SimpleDateFormat currentFormatter = new SimpleDateFormat("yyyy-MM-dd");
private Date todaysdate;
private LinearLayout topViewLinearLayout, bottomViewLinearLayout;
private ImageView watchVidIV, takeTestIV, completedVidBanner, completedTestBanner;
private List<Object> userInfo, questionList;
private ArrayList<String> quizzes;
private ArrayList receivedUserInfo;
private Boolean hasTakenTest, hasWatchedVideo, hasPassedTest;
private String TAG, userFirstName, userLastName, companyName, userID, datedTrainingID, companyID, stringvaluevar, dateStarted, formattedDate;
private String finalQuestionField, queriedTrainingID, questionString, answer1, answer2, answer3, answer4, queriedVideoURL;
private long numOfQuestions;
private TextView welcomeName, passedTestMessageView, watchedVidMessageView;
private Button takeTestButton, watchVideoButton;
private Map<String, Object> userDoc;
private int queriedStartDay, queriedStartMonth, queriedStartYear, calcCurrentDay, calcCurrentMonth, calcCurrentYear, startingIntValue, endingIntValue, hourDifference, weeksBetween,
    sharedPrefWeeks;
private Context context;
private SharedPreferences sharedPref;
private SharedPreferences.Editor prefEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_screen);
        context = getApplicationContext();
        sharedPref  = context.getSharedPreferences("WeekValues", MODE_PRIVATE);
        prefEditor = sharedPref.edit();
        completedTestBanner = findViewById(R.id.completedTestTakenBanner);
        completedVidBanner = findViewById(R.id.completedVideoWatchedBanner);
        welcomeName = findViewById(R.id.welcomeName);
        watchVidIV = findViewById(R.id.watchvideoimageview);
        watchedVidMessageView = findViewById(R.id.haventwatchedvidmessage);
        takeTestIV = findViewById(R.id.taketestimageview);
        passedTestMessageView = findViewById(R.id.haventtakenquizmessage);
        takeTestButton = findViewById(R.id.taketestbutton);
        watchVideoButton = findViewById(R.id.watchvidbutton);
        todaysdate = new Date(System.currentTimeMillis());
        quizzes = new ArrayList<>();
        receivedUserInfo = new ArrayList<>();
        TAG = "USERSSCREEN";
        db = FirebaseFirestore.getInstance();
        formattedDate = currentFormatter.format(todaysdate);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Bundle myBundle = getIntent().getExtras();
        if (myBundle != null) {
            userFirstName = myBundle.getString("User First Name");
            userLastName = myBundle.getString("User Last Name");
            companyID = myBundle.getString("Company ID");
            datedTrainingID = myBundle.getString("Dated Training ID");
            Log.d(TAG, "DATED TRAINING ID IN CREATE METHOD: " + datedTrainingID);
            hasWatchedVideo = myBundle.getBoolean("Watched Video");
            hasTakenTest = myBundle.getBoolean("Taken Test");
            dateStarted = myBundle.getString("Date Started");
            hasPassedTest = myBundle.getBoolean("Passed Test");
            companyName = myBundle.getString("Company Name");
            Log.d(TAG, "RECIEVED COMPANY NAME IN USERSCREEN: " + companyName);
            userID = myBundle.getString("User ID");
            Log.d(TAG, "USER ID: " + userID);
            receivedUserInfo = myBundle.getParcelableArrayList("User Info List");
            Log.d(TAG, "USER FIRST NAME AND LAST NAME: " + userFirstName + ", " + userLastName);
        }
        welcomeName.setText("Welcome, " + userFirstName);
        welcomeName.setVisibility(View.VISIBLE);
        completedTestBanner.setVisibility(View.INVISIBLE);
        completedVidBanner.setVisibility(View.INVISIBLE);
        startingIntValue = 0;
        endingIntValue = 0;
        hourDifference = 0;
        Log.d(TAG, "RECIEVED COMPANY NAME: " + companyName);
        Log.d(TAG, "TODAYS FORMATTED DATE VALUE: " + formattedDate);
                queriedStartMonth = Integer.parseInt(dateStarted.substring(0, 2));
                queriedStartYear = Integer.parseInt(dateStarted.substring(dateStarted.lastIndexOf("-") + 1));
                queriedStartDay = Integer.parseInt(dateStarted.substring(3, 5));
                startingIntValue += (queriedStartDay*24);
                startingIntValue+=(queriedStartMonth*730);
                startingIntValue+=(queriedStartYear*8760);

                calcCurrentYear = Integer.parseInt(formattedDate.substring(0, 4));
                calcCurrentMonth = Integer.parseInt(formattedDate.substring(5, 7));
                calcCurrentDay = Integer.parseInt(formattedDate.substring(8, 10));
                endingIntValue+= (calcCurrentDay*24);
                endingIntValue+= (calcCurrentMonth*730);
                endingIntValue+= (calcCurrentYear*8760);
                weeksBetween = (endingIntValue-startingIntValue)/168;
                Log.d(TAG, "RECEIVED USER INFO ARRAYLIST: " + receivedUserInfo);
                sharedPrefWeeks = sharedPref.getInt(companyName + "weekValue", -1);
                Log.d(TAG, "SHAREDPREFWEEKS: " + sharedPrefWeeks + "WEEKS BETWEEN: " + weeksBetween);
                if(sharedPrefWeeks!=weeksBetween) //&& weeksBetween!=0
                {
                    userInfo = (List<Object>) receivedUserInfo;
                    userInfo.set(2, false);
                    userInfo.set(3, false);
                    userInfo.set(4, false);
                    userDoc = new HashMap<>();
                    userDoc.put(userID, userInfo);
                    db.collection("companies").document(companyName).set(userDoc, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "USER INFO WITHIN COMPANY DOCUMENT SUCCESSFULLY UPDATED WITH WATCHVIDEO SET TO FALSE");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "FAILED TO UPDATE USER INFO WITHIN COMPANY DOCUMENT AND SET WATCHVIDEO TO FALSE");
                        }
                    });
                }
        prefEditor.putInt(companyName + "weekValue", weeksBetween);
        prefEditor.apply();
                Log.d(TAG, "UPDATED SHAREDPREFWEEKS: " + sharedPref.getInt(companyName + "weekValue", -1));
        checkforWatchedVideo();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startingIntValue = 0;
        endingIntValue = 0;
        hourDifference = 0;
        weeksBetween = 0;
        Intent i = getIntent();
        quizzes.clear();
        checkforWatchedVideo();
        checkforWatchedVideo();
    }

    public void watchvideopressed(View v)
    {
        watchVideoButton.setClickable(false);
        Intent toVideo = new Intent(UsersScreen.this, WatchVideo.class);
        toVideo.putExtra("First Name", userFirstName);
        toVideo.putExtra("Last Name", userLastName);
        toVideo.putExtra("Dated Training ID", datedTrainingID);
        toVideo.putExtra("Company Name", companyName);
        toVideo.putExtra("User ID", userID);
        toVideo.putExtra("Today's Date", formattedDate.substring(5) + "-" + formattedDate.substring(0, 4));
                toVideo.putExtra("Dated TrainingID", datedTrainingID);
                Log.d(TAG, "USERSCREEN DATED TRAINING ID IS: " + datedTrainingID);

                Log.d(TAG, "NEW USER INFO: " + receivedUserInfo);
                toVideo.putExtra("Date Started", dateStarted); // not sure if this line of code is needed, may delete later since watchvideo doesnt get it from bundle
                toVideo.putParcelableArrayListExtra("User Info List", (ArrayList) receivedUserInfo);

        db.collection("companies").document(companyName).collection("Trainings")
                .document(datedTrainingID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                queriedTrainingID = (String)document.get("trainingID");
                Log.d(TAG, "QUERIED TRAINING ID BEFORE QUERY: " + queriedTrainingID);
                db.collection("companies").document(companyName).collection("CustomTrainings").whereEqualTo("trainingID", queriedTrainingID).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size()==0)
                                {
                                    Log.d(TAG, "There aren't any queried documents");
                                }
                                else
                                {
                                    Log.d(TAG, "There are at least one queried documents");
                                }
                                for(DocumentSnapshot myDocument : queryDocumentSnapshots)
                                {
                                    queriedVideoURL = (String) myDocument.get("videoURL");
                                }
                                toVideo.putExtra("Queried Video URL", queriedVideoURL);
                                Log.d(TAG, "QUERIED VIDEO URL: " + queriedVideoURL);
                                startActivity(toVideo);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        watchVideoButton.setClickable(true);
                        Log.d(TAG, "FAILED TO GET DOC WITH QUERIEDTRAININGID IN GENERAL TRAININGS");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                watchVideoButton.setClickable(true);
                Log.d(TAG, "FAILED TO RETRIEVE DATED DOCUMENT IN TRAININGS SUBCOLLECTION");
            }
        });
    }

    public void taketestpressed (View v)
    {
        generateQuizList();
    }

    public void generateQuizList()
    {
        takeTestButton.setClickable(false);
        Log.d(TAG, "DATED TRAINING ID: " + datedTrainingID);
        db.collection("companies").document(companyName).collection("Trainings").document(datedTrainingID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                queriedTrainingID = (String) doc.get("trainingID");
                Log.d(TAG, "QUERIED TRAINING ID: " + queriedTrainingID);
                        db.collection("companies").document(companyName).collection("CustomTrainings").whereEqualTo("trainingID", queriedTrainingID).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                    {
                                        quizzes = new ArrayList<>();
                                        for(DocumentSnapshot myDocument:queryDocumentSnapshots)
                                        {
                                            Log.d(TAG, "DOCUMENT ID: " + myDocument.getId());
                                            numOfQuestions = (Long)myDocument.get("numberOfQuestions");
                                            for(int i = 0; i<numOfQuestions; i++){
                                                stringvaluevar = String.valueOf(i);
                                                finalQuestionField = "question" + stringvaluevar;
                                                questionList = (List<Object>) myDocument.get(finalQuestionField);
                                                questionString = (String) questionList.get(0);
                                                answer1 = (String) questionList.get(2);
                                                answer2 = (String) questionList.get(3);
                                                answer3 = (String) questionList.get(4);
                                                answer4 = (String) questionList.get(5);
                                                quizzes.add(finalQuestionField);
                                                quizzes.add(questionString);
                                                quizzes.add(String.valueOf(questionList.get(1)));
                                                quizzes.add(answer1);
                                                quizzes.add(answer2);
                                                quizzes.add(answer3);
                                                quizzes.add(answer4);
                                            }
                                        }
                                        Intent toTest = new Intent(UsersScreen.this, TakeTest.class);
                                        toTest.putExtra("Company Name", companyName);
                                        toTest.putExtra("User ID", userID);
                                        toTest.putExtra("User First Name", userFirstName);
                                        toTest.putExtra("User First Name", userFirstName);
                                        toTest.putExtra("Today's Date", formattedDate.substring(5) + "-" + formattedDate.substring(0, 4));
                                        toTest.putExtra("User Last Name", userLastName);
                                        toTest.putExtra("Number of Questions", numOfQuestions);
                                        toTest.putExtra("Date Started", dateStarted);
                                        toTest.putExtra("Dated Training ID", datedTrainingID);
                                        toTest.putExtra("User Info List", receivedUserInfo);
                                        toTest.putStringArrayListExtra("Quiz Arraylist", quizzes);
                                        Log.d(TAG, "ABOUT TO PUSH QUIZ ARRAYLIST: " + quizzes);
                                        startActivity(toTest);
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                takeTestButton.setClickable(true);
                                Log.d(TAG, "Failed to get referenced document in quizes collection");
                            }
                        });
                    } //end of oncomplete method
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        takeTestButton.setClickable(true);
                        Log.d(TAG, "FAILED TO QUERY SUBCOLLECTION TRAININGS");
                    }
                });
    }

    public void backtoSelectGroup(View v)
    {
        Intent selectGroup = new Intent(UsersScreen.this, SelectGroup.class);
        selectGroup.putExtra("User ID", userID);
        selectGroup.putExtra("First Name", userFirstName);
        selectGroup.putExtra("Last Name", userLastName);
        startActivity(selectGroup);
    }

    public void checkforWatchedVideo()
    {
        db.collection("companies").document(companyName).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        userInfo = (List<Object>) document.get(userID);
                        if((Boolean)userInfo.get(2))
                        {
                            hasWatchedVideo = true;
                        }
                        else
                        {
                            hasWatchedVideo = false;
                        }
                        if((Boolean)userInfo.get(4))
                        {
                            hasPassedTest = true;
                        }
                        else
                        {
                            hasPassedTest = false;
                        }
                        if (hasWatchedVideo) {
                            watchVidIV.setBackgroundResource(R.drawable.groups_borders_green);
                            completedVidBanner.setVisibility(View.VISIBLE);
                            takeTestButton.setVisibility(View.VISIBLE);
                            //watchedVidMessageView.setTextColor();
                            watchedVidMessageView.setText("YOU'VE WATCHED THIS WEEK'S VIDEO");
                            if(hasPassedTest)
                            {
                                takeTestIV.setBackgroundResource(R.drawable.groups_borders_green);
                                completedTestBanner.setVisibility(View.VISIBLE);
                                passedTestMessageView.setText("YOU'VE PASSED THIS WEEK'S TEST");

                            }
                            else
                            {
                                takeTestIV.setBackgroundResource(R.drawable.groups_borders_red);
                                completedTestBanner.setVisibility(View.INVISIBLE);
                                //passedTestMessageView.setTextColor();
                                passedTestMessageView.setText("YOU HAVE NOT PASSED THIS WEEK'S TEST");
                            }
                        }
                        else
                        {
                            watchVidIV.setBackgroundResource(R.drawable.groups_borders_red);
                            completedVidBanner.setVisibility(View.INVISIBLE);
                            watchedVidMessageView.setText("YOU HAVE NOT WATCHED THIS WEEK'S VIDEO");
                            passedTestMessageView.setText("WATCH THE VIDEO TO UNLOCK THE TEST");
                            takeTestButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO QUERY COMPANY COLLECTION TO FIND USERS WATCH VIDEO BOOLEAN");
            }
        });
    }
}
