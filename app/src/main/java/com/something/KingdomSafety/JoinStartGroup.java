package com.something.KingdomSafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class JoinStartGroup extends AppCompatActivity {
private String userfirstname, userlastname, userId;
private Boolean ingroup;
private Button backButton, joinGroupButton, startGroupButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_start_group);
        backButton = findViewById(R.id.backbutton3);
        joinGroupButton = findViewById(R.id.joingroupbutton);
        startGroupButton = findViewById(R.id.startgroupbutton);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            userfirstname = myBundle.getString("First Name");
            userlastname = myBundle.getString("Last Name");
            ingroup = myBundle.getBoolean("ingroup");
            userId = myBundle.getString("User ID");
            Log.d("JOINSTARTGROUP", "user id: " + userId);
            Log.d("JOINSTARTGROUP", "USER FIRST AND LAST NAME IN ONSTART OF JOINSTARTGROUP: " + userfirstname + ", " + userlastname);
        }
    }

    public void backtoLoginSignup(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        startGroupButton.setClickable(false);
        Intent loginSignup = new Intent(JoinStartGroup.this, LoginSignup.class);
        loginSignup.putExtra("autosignin", false);
        startActivity(loginSignup);
    }

    public void startnewgroup(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        startGroupButton.setClickable(false);
        Intent startgroup = new Intent(JoinStartGroup.this, CreateNewGroup.class);
        startgroup.putExtra("ingroup", ingroup);
        startgroup.putExtra("User ID", userId);
        startgroup.putExtra("First Name", userfirstname);
        startgroup.putExtra("Last Name", userlastname);
        startActivity(startgroup);
    }

    public void joinagroup(View v)
    {
        backButton.setClickable(false);
        joinGroupButton.setClickable(false);
        startGroupButton.setClickable(false);
        Intent joingroup = new Intent(JoinStartGroup.this, JoinExistingGroup.class);
        Bundle joingroupbundle = new Bundle();
        joingroupbundle.putString("First Name", userfirstname);
        joingroupbundle.putString("Last Name", userlastname);
        joingroupbundle.putBoolean("ingroup", ingroup);
        joingroupbundle.putString("User ID", userId);
        joingroup.putExtras(joingroupbundle);
        startActivity(joingroup);
    }
}
