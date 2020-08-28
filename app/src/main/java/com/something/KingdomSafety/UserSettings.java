package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserSettings extends AppCompatActivity {
private String userName, userID, companyName, compID, datedTrainingID, formattedName, selectedUserID;
private List<Object> userInfo;
private ArrayList<String> hasWatchedVid, hasPassedTest;
private FirebaseUser removeUserRef;
private static final String TAG = "USERSETTINGS";
private int currentItem, selectedPosition;
private ImageView watchedVidImageView, playButtonImageView;
private TextView userNameTextView;
private LottieAnimationView loadingView;
private ArrayList<String> completedTrainings, customTrainingNames, customTrainingIDs, userNameList, uidList, trainingIDs, watchedVidDate, passedTestDate, passedTestList, watchedVidList;
private Button deleteUserButton, userReportButton;
private Drawable playButtonImageRed, playButtonImageGreen, helpButtonImageRed, helpButtonImageGreen;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        userNameTextView = findViewById(R.id.usernameTextView);
        deleteUserButton = findViewById(R.id.removeuserbutton);
        userReportButton = findViewById(R.id.userreportbutton);
        watchedVidImageView = findViewById(R.id.watchedVidImageView);
        playButtonImageView = findViewById(R.id.playButtonImageView);
        loadingView = findViewById(R.id.userSettingsAnimationView);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            userName = myBundle.getString("User Name");
            currentItem = myBundle.getInt("Current Item");
            Log.d(TAG, "USER NAME IN USER SETTINGS: " +  userName);
            selectedPosition = myBundle.getInt("Selected Position");
            companyName = myBundle.getString("Company Name");
            userNameList = myBundle.getStringArrayList("User Name List");
            Log.d(TAG, "USER NAME LIST IN USERSETTINGS: " + userNameList);
            uidList = myBundle.getStringArrayList("UID List");
            selectedUserID = myBundle.getString("Selected User ID");
            userID = myBundle.getString("User ID");
            hasWatchedVid = myBundle.getStringArrayList("Has Watched Video");
            hasPassedTest = myBundle.getStringArrayList("Has Passed Test");
            datedTrainingID = myBundle.getString("Dated Training ID");
            compID = myBundle.getString("Company ID");
            Log.d(TAG, "USER SETTINGS RECEIVED USER ID: " + userID);
        }
        else
        {
            Log.d(TAG, "USER SETTINGS BUNDLE WAS NULL");
        }
        db = FirebaseFirestore.getInstance();
        getUserID();
        playButtonImageRed = getResources().getDrawable(R.drawable.ic_play_circle_outline_24px_red);
        playButtonImageGreen = getResources().getDrawable(R.drawable.ic_play_circle_outline_24px_green);
        helpButtonImageGreen = getResources().getDrawable(R.drawable.ic_help_outline_24px_green);
        helpButtonImageRed = getResources().getDrawable(R.drawable.ic_help_outline_24px_red);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        userNameTextView.setText(userName);
        loadingView.setVisibility(View.INVISIBLE);
        loadingView.cancelAnimation();
        Log.d(TAG, "HAS WATCHED VID AND PASSED TEST LISTS: " + hasWatchedVid + " ||| " + hasPassedTest);
        Log.d(TAG, "SELECTED POSITION: " + selectedPosition);
        Log.d(TAG, "UID LIST IN USER SETTINGS " + uidList);
        Log.d(TAG, "ABOUT TO PUSH USER NAME, AND UID: " + userName + ", " + userID);
        Log.d(TAG, "ABOUT TO PUSH COMPANY NAME AND ID: " + companyName + ", " + compID);
        Log.d(TAG, "ABOUT TO PUSH USERNAME LIST AND UID LIST: " + userNameList + ", " + uidList);
    }

    public void getUserID()
    {

                                        if(hasWatchedVid.get(selectedPosition).equals("true"))
                                        {
                                            watchedVidImageView.setBackground(getResources().getDrawable(R.drawable.ic_help_outline_24px_green));
                                        }
                                        else if(hasWatchedVid.get(selectedPosition).equals("false"))
                                        {
                                            watchedVidImageView.setBackground(getResources().getDrawable(R.drawable.ic_help_outline_24px_red));
                                        }

                                        if(hasPassedTest.get(selectedPosition).equals("true"))
                                        {
                                            playButtonImageView.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_24px_green));
                                        }
                                        else if(hasPassedTest.get(selectedPosition).equals("false"))
                                        {
                                            playButtonImageView.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_24px_red));
                                        }
                                        Log.d(TAG, "USER INFO LIST: " + userInfo);

    }

    public void deleteUserPressed(View v)
    {
        deleteUserButton.setClickable(false);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.playAnimation();
        Log.d(TAG, "ABOUT TO PUSH USER NAME, AND UID: " + userName + ", " + userID);
        Log.d(TAG, "ABOUT TO PUSH COMPANY NAME AND ID: " + companyName + ", " + compID);
        Log.d(TAG, "ABOUT TO PUSH USERNAME LIST AND UID LIST: " + userNameList + ", " + uidList);
        HashMap userMap = new HashMap();
        Log.d(TAG, "FORMATTED NAME: " + formattedName);
                        userMap.put(selectedUserID, FieldValue.delete());
                        db.collection("companies").document(companyName).set(userMap, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent backAdminScreen = new Intent(UserSettings.this, AdminScreen.class);
                                backAdminScreen.putExtra("Company ID", compID);
                                uidList.remove(selectedUserID);
                                userNameList.remove(userName);
                                backAdminScreen.putExtra("User ID", userID);
                                backAdminScreen.putExtra("Dated Training ID", datedTrainingID);
                                backAdminScreen.putStringArrayListExtra("User Name List", userNameList);
                                backAdminScreen.putStringArrayListExtra("UID List", uidList);
                                backAdminScreen.putExtra("Current Item", currentItem);
                                backAdminScreen.putExtra("Company Name", companyName);
                                backAdminScreen.putExtra("User First Name", userName.substring(0, userName.indexOf(" ")));
                                backAdminScreen.putExtra("User Last Name", userName.substring(userName.indexOf(" ") +1));
                                startActivity(backAdminScreen);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                deleteUserButton.setClickable(true);
                            }
                        });
    }

    public void generateUserReportClicked(View v)
    {
        userReportButton.setClickable(false);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.playAnimation();
        completedTrainings = new ArrayList<>();
        customTrainingNames = new ArrayList<>();
        customTrainingIDs = new ArrayList<>();
        trainingIDs = new ArrayList<>();
        watchedVidDate = new ArrayList<>();
        watchedVidList = new ArrayList<>();
        passedTestDate = new ArrayList<>();
        passedTestList = new ArrayList<>();
        db.collection("companies").document(companyName).collection("CustomTrainings").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult()!=null) {
                            for (DocumentSnapshot doc : task.getResult())
                            {
                                customTrainingNames.add(doc.getId());
                                customTrainingIDs.add((String)doc.get("trainingID"));
                            }
                            db.collection("companies").document(companyName).collection("Trainings")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.getResult()!=null)
                                    {
                                        for(DocumentSnapshot myDoc : task.getResult())
                                        {
                                            if(myDoc.get(selectedUserID)!=null)
                                            {
                                                trainingIDs.add((String)myDoc.get("trainingID"));
                                                completedTrainings.add((String)myDoc.getId());
                                                List<Object> userData = (List<Object>) myDoc.get(selectedUserID);
                                                if((boolean)userData.get(0)==true)
                                                {
                                                    watchedVidList.add("true");
                                                }
                                                else
                                                {
                                                    watchedVidList.add("false");
                                                }
                                                watchedVidDate.add((String)userData.get(1));

                                                if((boolean)userData.get(2)==true)
                                                {
                                                    passedTestList.add("true");
                                                }
                                                else
                                                {
                                                    passedTestList.add("false");
                                                }
                                                passedTestDate.add((String)userData.get(3));
                                            }
                                        }
                                    }
                                    Intent userReport = new Intent(UserSettings.this, UserReport.class);
                                    userReport.putExtra("User Name", userName);
                                    userReport.putExtra("User ID", userID);
                                    userReport.putExtra("Company Name", companyName);
                                    userReport.putExtra("Dated Training ID", datedTrainingID);
                                    userReport.putExtra("Selected User ID", selectedUserID);
                                    userReport.putExtra("Company ID", compID);
                                    userReport.putStringArrayListExtra("User Name List", userNameList);
                                    userReport.putStringArrayListExtra("UID List", uidList);
                                    userReport.putStringArrayListExtra("Completed Trainings", completedTrainings);
                                    userReport.putStringArrayListExtra("Custom Training Names", customTrainingNames);
                                    userReport.putStringArrayListExtra("Has Watched Video", hasWatchedVid);
                                    userReport.putStringArrayListExtra("Has Passed Test", hasPassedTest);
                                    userReport.putExtra("Selected Position", selectedPosition);
                                    userReport.putStringArrayListExtra("Training IDs", trainingIDs);
                                    userReport.putStringArrayListExtra("Custom Training IDs", customTrainingIDs);
                                    userReport.putStringArrayListExtra("Watched Vid List", watchedVidList);
                                    userReport.putStringArrayListExtra("Passed Test List", passedTestList);
                                    userReport.putStringArrayListExtra("Watched Vid Date", watchedVidDate);
                                    userReport.putStringArrayListExtra("Passed Test Date", passedTestDate);
                                    startActivity(userReport);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userReportButton.setClickable(true);
                                    Log.d(TAG, "FAILED TO RETRIEVE COMPLETED TRAININGS");
                                }
                            });
                        }
                        else
                        {
                            userReportButton.setClickable(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userReportButton.setClickable(true);
                Log.d(TAG, "FAILED TO RETRIEVE DOCUMENTS THAT CONTAIN USER ID TO DETERMINE WHETHER USER HAS COMPLETED TRAINING");
            }
        });
    }

    public String formatName(String name)
    {
        int indexOfSpace;
        indexOfSpace=name.indexOf(" ");
        name = name.substring((indexOfSpace+1)) + "." + name.substring(0, indexOfSpace);
        return name;
    }

    public void backtoAdminScreen(View v)
    {
        Intent adminScreen = new Intent(UserSettings.this, AdminScreen.class);
        adminScreen.putExtra("Company ID", compID);
        adminScreen.putExtra("User ID", userID);
        adminScreen.putExtra("Dated Training ID", datedTrainingID);
        adminScreen.putExtra("Company Name", companyName);
        adminScreen.putExtra("Current Item", currentItem);
        adminScreen.putExtra("Selected User ID", selectedUserID);
        adminScreen.putExtra("User First Name", userName.substring(0, userName.indexOf(" ")));
        adminScreen.putExtra("User Last Name", userName.substring(userName.indexOf(" ") +1));
        adminScreen.putStringArrayListExtra("UID List", uidList);
        adminScreen.putStringArrayListExtra("User Name List", userNameList);
        startActivity(adminScreen);
    }
}