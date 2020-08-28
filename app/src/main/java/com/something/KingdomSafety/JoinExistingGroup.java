package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JoinExistingGroup extends AppCompatActivity {
private EditText field1, field2, field3;
private String concatgroupcode, compId, companyName, userId, userFirstName, userLastName, userDocName, dateStarted;
private final static String TAG = "JOINEXISTINGGROUP";
private FirebaseFirestore db;
private Boolean ingroup, alreadyInGroup;
private Object[] userInfo;
private Button backButton, checkCodeButton, startGroupButton;
private ArrayList userInfoList;
private Bundle joingroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_existing_group);
        field1 = (EditText)findViewById(R.id.firstgroupcodefield);
        field2 = (EditText)findViewById(R.id.secondgroupcodefield);
        field3 = (EditText)findViewById(R.id.thirdgroupcodefield);
        backButton = findViewById(R.id.homebutton5);
        checkCodeButton = findViewById(R.id.checkcodebutton);
        startGroupButton = findViewById(R.id.startnewgroupbutton);
        db = FirebaseFirestore.getInstance();
        joingroup = getIntent().getExtras();
        userId = joingroup.getString("User ID");
        userInfoList = new ArrayList<>();
        userInfo = new Object[6];
        userInfo[0] = userId;
        userInfo[1] = false; //isAdmin
        userInfo[2] = false; //watchedVideo
        userInfo[3] = false; //taken test
        userInfo[4] = false; //passed test
        userInfo[5] = 0; //score
        Log.d(TAG, "USER ID: " + userId);
        ingroup = joingroup.getBoolean("ingroup");
        for(int i = 0; i<userInfo.length; i++)
        {
            userInfoList.add(userInfo[i]);
        }
        alreadyInGroup = false;
        userFirstName = joingroup.getString("First Name");
        userLastName = joingroup.getString("Last Name");
        Log.d(TAG, "USER FIRST AND LAST NAME IN ONSTART OF JOINEXISTINGGROUP: " + userFirstName + ", " + userLastName);
        userDocName = userLastName + "." + userFirstName;
        Log.d(TAG, "USER DOC NAME " + userDocName);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        autotab(field1, field2);
        autotab(field2, field3);
    }

    public void checkcode(View v)
    {
        checkCodeButton.setClickable(false);
        backButton.setClickable(false);
        startGroupButton.setClickable(false);
        concatgroupcode = field1.getText().toString()+field2.getText().toString()+ field3.getText().toString();
        compId = field1.getText().toString()+"-"+field2.getText().toString()+"-"+field3.getText().toString();
        if(concatgroupcode.length()==9)
        {
            Log.d(TAG,"GROUP CODE: " + concatgroupcode);
            Log.d(TAG,"COMPANY ID: " + compId);
            db.collection("companies")
                    .whereEqualTo("Company ID", compId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "TASK IS SUCCESSFUL");
                                if(task.getResult()!=null) {
                                    Log.d(TAG, "THE TASK RESULT ISN'T NULL");
                                    for (DocumentSnapshot document : task.getResult()) {
                                        companyName = document.getId();
                                        Log.d(TAG, "THIS COMPANY'S NAME: " + companyName);
                                        if(document.get(userId)!=null)
                                        {
                                            alreadyInGroup=true;
                                        }
                                        dateStarted = (String)document.get("dateStarted");
                                    }
                                        Map<String, Object> userDoc = new HashMap<>();
                                    if(!alreadyInGroup) {
                                        userDoc.put(userId, Arrays.asList(userInfo));
                                    }
                                    if(companyName!=null) {
                                        if(!alreadyInGroup)
                                        {
                                            db.collection("companies").document(companyName).set(userDoc, SetOptions.merge())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "USER INFO SUCCESSFULLY ADDED TO COMPANY DOC");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                              @Override
                                                                                              public void onFailure(@NonNull Exception e) {
                                                                                                  checkCodeButton.setClickable(true);
                                                                                                  backButton.setClickable(true);
                                                                                                  startGroupButton.setClickable(true);
                                                                                                  Log.d(TAG, "FAILED ADDING USER INFO TO COMPANY DOC");
                                                                                              }
                                                                                          }
                                                                    );
                                                            Intent grouplist = new Intent(JoinExistingGroup.this, UsersScreen.class);
                                                            grouplist.putExtra("Company ID", compId);
                                                            Log.d(TAG, "COMPANY ID IN JOINEXISTINGGROUP: " + compId);
                                                            grouplist.putExtra("Dated Training ID", String.valueOf(0));
                                                            grouplist.putExtra("Company Name", companyName);
                                                            Log.d(TAG, "COMPANY NAME FROM JOINEXISTINGGROUP: " + companyName);
                                                            grouplist.putExtra("User ID", userId);
                                                            grouplist.putExtra("User First Name", userFirstName);
                                                            grouplist.putExtra("User Last Name", userLastName);
                                                            Log.d(TAG, "PUSHED USER ID: " + userId);
                                                            grouplist.putExtra("Date Started", dateStarted);
                                                            grouplist.putParcelableArrayListExtra("User Info List", userInfoList);
                                                            grouplist.putExtra("Watched Video", false);
                                                            grouplist.putExtra("Passed Test", false);
                                                            grouplist.putExtra("Taken Test", false);
                                                            startActivity(grouplist);
                                                        }
                                                        else
                                                        {
                                                            backButton.setClickable(true);
                                                            startGroupButton.setClickable(true);
                                                            checkCodeButton.setClickable(true);
                                                            Toast.makeText(JoinExistingGroup.this, "You've already joined this group!", Toast.LENGTH_SHORT).show();
                                                        }
                                    }
                                    else
                                    {
                                        checkCodeButton.setClickable(true);
                                        backButton.setClickable(true);
                                        startGroupButton.setClickable(true);
                                        Toast.makeText(JoinExistingGroup.this, "The provided Group Code is invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    backButton.setClickable(true);
                                    startGroupButton.setClickable(true);
                                    checkCodeButton.setClickable(true);
                                    Log.d(TAG, "TASK RESULT WAS NULL" + task.getResult());
                                }
                            }
                            }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "FAILED TO USER INPUTTED COMPANY ID CODE TO JOIN THEM IN A GROUP");
                    checkCodeButton.setClickable(true);
                    backButton.setClickable(true);
                    startGroupButton.setClickable(true);
                }
            });

            db.collection("users").document(userDocName).update("ingroup", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        checkCodeButton.setClickable(true);
                        backButton.setClickable(true);
                        startGroupButton.setClickable(true);
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        }
        else
        {
            checkCodeButton.setClickable(true);
            backButton.setClickable(true);
            startGroupButton.setClickable(true);
            Toast.makeText(JoinExistingGroup.this, "Please enter a valid group code", Toast.LENGTH_SHORT).show();
        }
    }

    public void backbuttonpressed(View v)
    {
        backButton.setClickable(false);
        checkCodeButton.setClickable(false);
        startGroupButton.setClickable(false);
        if(!ingroup)
        {
            Intent joinStartGroup = new Intent(JoinExistingGroup.this, JoinStartGroup.class);
            joinStartGroup.putExtra("First Name", userFirstName);
            joinStartGroup.putExtra("Last Name", userLastName);
            joinStartGroup.putExtra("ingroup", false);
            joinStartGroup.putExtra("User ID", userId);
            startActivity(joinStartGroup);
        }
        else {
            Intent backtoSelectGroup = new Intent(JoinExistingGroup.this, SelectGroup.class);
            backtoSelectGroup.putExtra("First Name", userFirstName);
            backtoSelectGroup.putExtra("Last Name", userLastName);
            backtoSelectGroup.putExtra("User ID", userId);
            startActivity(backtoSelectGroup);
        }
    }

    public void startgroupclicked(View v)
    {
        checkCodeButton.setClickable(false);
        backButton.setClickable(false);
        startGroupButton.setClickable(false);
        Intent creategroup = new Intent(JoinExistingGroup.this, CreateNewGroup.class);
        creategroup.putExtra("First Name", userFirstName);
        creategroup.putExtra("Last Name", userLastName);
        creategroup.putExtra("User ID", userId);
        creategroup.putExtra("ingroup", ingroup);
        startActivity(creategroup);
    }

    public void autotab(EditText one, EditText two)
    {
        one.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                if(one.getText().toString().length()==3)//size as per your requirement
                {
                    two.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                //auto-generated method stub
            }

        });
    }
}
