package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CreateNewGroup extends AppCompatActivity {
private String companyId, groupName, userFirstName, userLastName, userId;
private final static String TAG = "CREATE NEW GROUP";
private EditText entergroupname;
private Boolean sameName, ingroup;
private Button backButton, joinExistingGroupButton, nextButton;
private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        backButton = findViewById(R.id.homebutton4);
        joinExistingGroupButton = findViewById(R.id.joinexistinggroupbutton);
        nextButton = findViewById(R.id.generatecode);
        db = FirebaseFirestore.getInstance();
        entergroupname = (EditText)findViewById(R.id.companynamefield);
        companyId = getRandNum() + "-" + getRandNum() + "-" + getRandNum();
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            userId = myBundle.getString("User ID");
            ingroup = myBundle.getBoolean("ingroup");
        }
        Log.d(TAG, "USER FIRST NAME AND LAST NAME: " + userFirstName + ", " + userLastName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sameName = false;
        nextButton.setClickable(true);
        backButton.setClickable(true);
        joinExistingGroupButton.setClickable(true);
    }


    public void genCode(View v)
    {
        nextButton.setClickable(false);
        backButton.setClickable(false);
        joinExistingGroupButton.setClickable(false);
        sameName = false;
        //if gen code is successful{
        groupName = entergroupname.getText().toString();
        if(!groupName.equals("")) {
            Log.d(TAG, "GROUPNAME: " + groupName);
            groupName = entergroupname.getText().toString();
            db.collection("companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot document : task.getResult())
                        {
                            if(document.getId().equalsIgnoreCase(groupName))
                            {
                                sameName = true;
                            }
                        }

                        if(sameName)
                        {
                            Toast toast = Toast.makeText(CreateNewGroup.this, "There's already a group with that name. Please choose a different one", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            nextButton.setClickable(true);
                            backButton.setClickable(true);
                            joinExistingGroupButton.setClickable(true);
                        }
                        else
                        {
                            Intent choosegroup = new Intent(CreateNewGroup.this, GroupCategories.class);
                            choosegroup.putExtra("Group Name", groupName); //groupName
                            choosegroup.putExtra("Company ID", companyId); //companyID
                            choosegroup.putExtra("First Name", userFirstName);
                            choosegroup.putExtra("Last Name", userLastName);
                            choosegroup.putExtra("User ID", userId);
                            choosegroup.putExtra("ingroup", ingroup);
                            startActivity(choosegroup);
                        }
                    }
                    else
                    {
                        Toast.makeText(CreateNewGroup.this, "FAILED TO ACCESS DATABASE..POSSIBLY NO INTERNET", Toast.LENGTH_LONG).show();
                        nextButton.setClickable(true);
                        backButton.setClickable(true);
                        joinExistingGroupButton.setClickable(true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    nextButton.setClickable(true);
                    backButton.setClickable(true);
                    joinExistingGroupButton.setClickable(true);
                    Log.d(TAG, "FAILED TO CHECK IF COMPANY NAME ALREADY EXISTS WITHIN COMPANIES COLLECTION");
                }
            });
        }
        else
        {
            Toast.makeText(CreateNewGroup.this, "Please enter a valid Group Name", Toast.LENGTH_SHORT).show();
            nextButton.setClickable(true);
            backButton.setClickable(true);
            joinExistingGroupButton.setClickable(true);
        }

    }

    public void joinExistingGroupPressed(View v)
    {
        nextButton.setClickable(false);
        backButton.setClickable(false);
        joinExistingGroupButton.setClickable(false);
        Intent joinExistingGroup = new Intent(CreateNewGroup.this, JoinExistingGroup.class);
        joinExistingGroup.putExtra("First Name", userFirstName);
        joinExistingGroup.putExtra("Last Name", userLastName);
        joinExistingGroup.putExtra("User ID", userId);
        joinExistingGroup.putExtra("ingroup", ingroup);
        startActivity(joinExistingGroup);
    }

    public void backbuttonpressed(View v)
    {
        nextButton.setClickable(false);
        backButton.setClickable(false);
        joinExistingGroupButton.setClickable(false);
            Intent backtoSelectGroup = new Intent(CreateNewGroup.this, SelectGroup.class);
            backtoSelectGroup.putExtra("First Name", userFirstName);
            backtoSelectGroup.putExtra("Last Name", userLastName);
            backtoSelectGroup.putExtra("User ID", userId);
            startActivity(backtoSelectGroup);
    }

    public int getRandNum()
    {
        int min = 100;
        int max = 999;
        return (int)Math.floor(Math.random()*(max-min)) + min;
    }
}
