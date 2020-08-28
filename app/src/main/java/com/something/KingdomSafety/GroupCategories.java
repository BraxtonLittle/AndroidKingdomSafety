package com.something.KingdomSafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupCategories extends AppCompatActivity {
private ListView companycategories;
private String[] companies;
private Long numOfQuestions;
private Boolean ingroup;
private Button backButton, customButton;
private ArrayList<String> trainingNames, customTrainingsContent;
private String compID, groupName, userFirstName, userLastName, documentID, userId;
private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupcategories);
        companycategories = findViewById(R.id.companylist);
        backButton = findViewById(R.id.homebutton6);
        customButton = findViewById(R.id.customTraining);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            compID = myBundle.getString("Company ID");
            groupName = myBundle.getString("Group Name");
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            userId = myBundle.getString("User ID");
            ingroup = myBundle.getBoolean("ingroup");
            Log.d("GROUP CATEGORIES", "USER FIRST NAME AND LAST NAME: " + userFirstName + ", " + userLastName);
        }
        companies = new String[1];
        trainingNames = new ArrayList();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        companies[0] = "General Trainings";
        ArrayAdapter<String> myAdapter = new ArrayAdapter
                (GroupCategories.this, R.layout.custom_list_items, companies);
        companycategories.setAdapter(myAdapter);
        companycategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companycategories.setOnItemClickListener(null);
                backButton.setClickable(false);
                customButton.setClickable(false);
                String constructiontype = myAdapter.getItem(position);
                db.collection(constructiontype).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            documentID = doc.getId();
                            numOfQuestions = (long)doc.get("numberOfQuestions");
                            if(numOfQuestions>0) {
                                trainingNames.add(documentID);
                            }
                        }

                        Intent reviewTrainings = new Intent(GroupCategories.this, ReviewTrainings.class);
                        reviewTrainings.putExtra("Company ID", compID);
                        reviewTrainings.putExtra("Group Name", groupName);
                        reviewTrainings.putExtra("Training Names List", trainingNames);
                        reviewTrainings.putExtra("Company Type", companies[position]);
                        //reviewTrainings.putExtra("Manage Review", 2);
                        reviewTrainings.putExtra("First Name", userFirstName);
                        reviewTrainings.putExtra("Last Name", userLastName);
                        reviewTrainings.putExtra("ingroup", ingroup);
                        reviewTrainings.putExtra("User ID", userId);
                        startActivity(reviewTrainings);
                    }
                });
            }
        });
    }

    public void backtocreatenewgroupclicked(View v)
    {
        backButton.setClickable(false);
        customButton.setClickable(false);
        Intent createGroup = new Intent(GroupCategories.this, CreateNewGroup.class);
        createGroup.putExtra("First Name", userFirstName);
        createGroup.putExtra("Last Name", userLastName);
        createGroup.putExtra("User ID", userId);
        createGroup.putExtra("ingroup", ingroup);
        startActivity(createGroup);
    }

    public void customTrainingCategoryClicked(View v)
    {
        backButton.setClickable(false);
        customButton.setClickable(false);
        customTrainingsContent = new ArrayList<>();
        Intent reviewTrainings = new Intent(GroupCategories.this, ReviewTrainings.class);
        reviewTrainings.putExtra("Company ID", compID);
        reviewTrainings.putExtra("Group Name", groupName);
        reviewTrainings.putExtra("Training Names List", trainingNames);
        reviewTrainings.putExtra("Company Type", "Custom");
        reviewTrainings.putStringArrayListExtra("Custom Trainings Content", customTrainingsContent);
        //reviewTrainings.putExtra("Manage Review", 2);
        reviewTrainings.putExtra("First Name", userFirstName);
        reviewTrainings.putExtra("Last Name", userLastName);
        reviewTrainings.putExtra("ingroup", ingroup);
        reviewTrainings.putExtra("User ID", userId);
        startActivity(reviewTrainings);
    }
}
