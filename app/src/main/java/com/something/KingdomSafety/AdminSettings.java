package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminSettings extends AppCompatActivity {
private String compID, compName, userId, userFirstName, userLastName, datedTrainingID, documentTrainingID, customTrainingID;
private LottieAnimationView myAnimationView;
private int numericalDocID, docCounter, currentItem;
private ArrayList<String> trainingNames, customTrainingIDList, customDocIDList, trainingIDList, uidList, userNameList;
private ArrayList<Integer> trainingDocIDList;
private FirebaseFirestore db;
private Button backButton, joinGroupButton, groupListButton, manageTrainingsButton, signOutButton;
private final String TAG = "ADMINSETTINGS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);
        myAnimationView = findViewById(R.id.adminsettingsanimationview);
        myAnimationView.setVisibility(View.INVISIBLE);
        backButton = findViewById(R.id.backbutton);
        joinGroupButton = findViewById(R.id.joinGroupButton);
        groupListButton = findViewById(R.id.groupListButton);
        manageTrainingsButton = findViewById(R.id.manageTrainingsButton);
        signOutButton = findViewById(R.id.signOutButton);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            compID = myBundle.getString("Company ID");
            compName = myBundle.getString("Company Name");
            userNameList = myBundle.getStringArrayList("User Name List");
            uidList = myBundle.getStringArrayList("UID List");
            datedTrainingID = myBundle.getString("Dated Training ID");
            userId = myBundle.getString("User ID");
            currentItem = myBundle.getInt("Current Item");
            userFirstName = myBundle.getString("User First Name");
            userLastName = myBundle.getString("User Last Name");
        }
        Log.d(TAG, "USER FIRST AND LAST NAME: " + userFirstName + ", " + userLastName);
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "COMPANY ID: " + compID);
    }

    public void fromSettingstoAdminScreen(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        groupListButton.setClickable(false);
        manageTrainingsButton.setClickable(false);
        signOutButton.setClickable(false);
        Intent toAdminScreen = new Intent(AdminSettings.this, AdminScreen.class);
        toAdminScreen.putExtra("Company ID", compID);
        toAdminScreen.putStringArrayListExtra("User Name List", userNameList);
        toAdminScreen.putStringArrayListExtra("UID List", uidList);
        toAdminScreen.putExtra("Dated Training ID", datedTrainingID);
        toAdminScreen.putExtra("Company Name", compName);
        toAdminScreen.putExtra("Current Item", currentItem);
        toAdminScreen.putExtra("User First Name", userFirstName);
        toAdminScreen.putExtra("User Last Name", userLastName);
        toAdminScreen.putExtra("User ID", userId);
        startActivity(toAdminScreen);
    }

    public void fromSettingstoJoinGroup(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        groupListButton.setClickable(false);
        manageTrainingsButton.setClickable(false);
        signOutButton.setClickable(false);
        Intent toJoinGroup = new Intent(AdminSettings.this, JoinExistingGroup.class);
        toJoinGroup.putExtra("User ID", userId);
        toJoinGroup.putExtra("ingroup", true);
        toJoinGroup.putExtra("First Name", userFirstName);
        toJoinGroup.putExtra("Last Name", userLastName);
        startActivity(toJoinGroup);
    }

    public void signOutButtonPressed(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        groupListButton.setClickable(false);
        manageTrainingsButton.setClickable(false);
        signOutButton.setClickable(false);
        FirebaseAuth.getInstance().signOut();
        Intent loginScreen = new Intent(AdminSettings.this, LoginSignup.class);
        startActivity(loginScreen);
    }

    public void fromSettingstoSelectGroup(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        groupListButton.setClickable(false);
        manageTrainingsButton.setClickable(false);
        signOutButton.setClickable(false);
        Intent selectGroup = new Intent(AdminSettings.this, SelectGroup.class);
        selectGroup.putExtra("User ID", userId);
        selectGroup.putExtra("First Name", userFirstName);
        selectGroup.putExtra("Last Name", userLastName);
        startActivity(selectGroup);
    }

    public void toManageTrainings(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        groupListButton.setClickable(false);
        manageTrainingsButton.setClickable(false);
        signOutButton.setClickable(false);
        trainingNames = new ArrayList<>();
        customTrainingIDList = new ArrayList<>();
        customDocIDList = new ArrayList<>();
        trainingIDList = new ArrayList<>();
        trainingDocIDList = new ArrayList<>();
        myAnimationView.playAnimation();
        docCounter = 0;
        myAnimationView.setVisibility(View.VISIBLE);
        Log.d(TAG, "DATED TRAINING ID: " + datedTrainingID);

        //customTrainingIDList and customDocIDList are parallel to retrieve training name using training id
        db.collection("companies").document(compName).collection("CustomTrainings").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot myDoc : queryDocumentSnapshots)
                        {
                            customTrainingID = (String)myDoc.get("trainingID");
                            if(!customTrainingIDList.contains(customTrainingID))
                            {
                                customTrainingIDList.add(customTrainingID);
                                customDocIDList.add(myDoc.getId());
                            }
                        }

                        //retrieve all documents from training collection and make two parallel arrays for the doc id
                        // and associated training id field within that doc
                        db.collection("companies").document(compName).collection("Trainings")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                Log.d(TAG, "TRAINING ID LIST: " + customTrainingIDList);
                                for(DocumentSnapshot doc: queryDocumentSnapshots) {
                                    numericalDocID = Integer.parseInt(doc.getId());
                                        trainingDocIDList.add(numericalDocID);
                                    trainingIDList.add((String) doc.get("trainingID"));
                                }

                                //get all documents from the current week onward
                                for(int i = Integer.parseInt(datedTrainingID); i<queryDocumentSnapshots.size(); i++)
                                {
                                    documentTrainingID = trainingIDList.get(trainingDocIDList.indexOf(i));
                                    Log.d(TAG, "DOCUMENT TRAINING ID: " + documentTrainingID + ", VALUE OF I: " + i);
                                     trainingNames.add(customDocIDList.get(customTrainingIDList.indexOf(documentTrainingID)));
                                }
                                Log.d(TAG, "DOC COUNTER: " + docCounter);
                                Intent manageTrainings = new Intent(AdminSettings.this, ManageTrainings.class);
                                manageTrainings.putExtra("User ID", userId);
                                manageTrainings.putExtra("Company ID", compID);
                                manageTrainings.putExtra("Group Name", compName);
                                manageTrainings.putExtra("First Name", userFirstName);
                                manageTrainings.putExtra("Custom Doc ID List", customDocIDList);
                                manageTrainings.putExtra("Custom Training ID List", customTrainingIDList);
                                manageTrainings.putExtra("Dated Training ID", datedTrainingID);
                                manageTrainings.putExtra("Last Name", userLastName);
                                manageTrainings.putExtra("Manage Review", 1);
                                manageTrainings.putStringArrayListExtra("User Name List", userNameList);
                                manageTrainings.putStringArrayListExtra("UID List", uidList);
                                manageTrainings.putExtra("Training Names List", trainingNames);
                                Log.d(TAG, "TRAINING NAMES LIST BEFORE PUSHING: " + trainingNames);
                                Log.d(TAG, "TRAINING NAMES LIST SIZE: " + trainingNames.size());
                                startActivity(manageTrainings);
                                myAnimationView.cancelAnimation();
                                myAnimationView.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myAnimationView.cancelAnimation();
                myAnimationView.setVisibility(View.INVISIBLE);
                backButton.setClickable(true);
                joinGroupButton.setClickable(true);
                groupListButton.setClickable(true);
                manageTrainingsButton.setClickable(true);
                signOutButton.setClickable(true);
                Toast.makeText(AdminSettings.this, "Failed to retrieve current training content from database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
