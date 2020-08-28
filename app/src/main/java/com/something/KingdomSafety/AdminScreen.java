package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminScreen extends AppCompatActivity {
    private TextView compNameTextView, completedTrainings;
    private Button groupCodeView, backButton, settingsButton;
    private String companyName, companyID, mapArrayString, beforeEqualString, afterEqualString, retrievedName, newUserName, userName,
            docuidField, changeableDocUid, userID, tempUid, userFirstName, userLastName, datedTrainingID, formattedName, selectedUserID;
    private int indexOfEqualSign, indexOfPeriod, currentItem;
    private final static String TAG = "ADMINSCREEN";
    private ListView userlist;
    private ViewGroup myViewGroup;
    private FirebaseFirestore db;
    private Map documentInfo;
    private LottieAnimationView myAnimation;
    private Object [] mapArray;
    private List<Object> userInfo;
    private ArrayList uidList, finalNameList, passedTestList, notPassedTestList, hasWatchedVid, hasPassedTest;
    private Boolean [] hasPassedTestArray;
    private Toolbar myToolBar;
    private TabLayout myTabLayout;
    private TabItem allUsersTabItem, completeTabItem, incompleteTabItem;
    private PageAdapter myPageAdapter;
    private ViewPager myViewPager;
    private ArrayAdapter<String> myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        db = FirebaseFirestore.getInstance();
        compNameTextView = findViewById(R.id.companynamefield);
        completedTrainings = findViewById(R.id.completedTrainingsTextView);
        groupCodeView = findViewById(R.id.groupcodeTextView);
        myAnimation = findViewById(R.id.adminscreenanimationview);
        userlist = findViewById(R.id.userlistview);
        myToolBar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backbutton);
        settingsButton = findViewById(R.id.settingsbutton);
        myTabLayout = findViewById(R.id.tabLayoutID);
        allUsersTabItem = findViewById(R.id.allUsersTab);
        completeTabItem = findViewById(R.id.completeUsersTab);
        incompleteTabItem = findViewById(R.id.incompleteUsersTab);
        myViewPager = findViewById(R.id.viewPager);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null) {
            companyName = myBundle.getString("Company Name");
            uidList = myBundle.getStringArrayList("UID List");
            companyID = myBundle.getString("Company ID");
            currentItem = myBundle.getInt("Current Item");
            userID = myBundle.getString("User ID");
            finalNameList = myBundle.getStringArrayList("User Name List");
            datedTrainingID = myBundle.getString("Dated Training ID");
            Log.d(TAG, "ADMIN SCREEN DATED TRAINING ID: " + datedTrainingID);
            userFirstName = myBundle.getString("User First Name");
            userLastName = myBundle.getString("User Last Name");
            Log.d(TAG, "FINAL NAME LIST IN ADMIN SCREEN: " + finalNameList);
        }
        myPageAdapter = new PageAdapter(getSupportFragmentManager(), myTabLayout.getTabCount());
        myViewPager.setAdapter(myPageAdapter);
        //saves the tab user was on before pressing button, but it's buggy and could just be added later
        /*if(currentItem!=-1)
        {
            myViewPager.setCurrentItem(currentItem);
        }*/
        myViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(myTabLayout));
        Log.d(TAG, "ADMIN SCREEN CURRENT ITEM: " + currentItem);
        compNameTextView.setText(companyName);
        myViewGroup = (ViewGroup) findViewById(android.R.id.content);
        groupCodeView.setText("Group Code: " + companyID);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        groupCodeView.setClickable(true);
        settingsButton.setClickable(true);
        backButton.setClickable(true);
        myAnimation.cancelAnimation();
        myAnimation.setVisibility(View.INVISIBLE);
        passedTestList = new ArrayList<String>();
        notPassedTestList = new ArrayList<String>();
        hasWatchedVid = new ArrayList();
        hasPassedTest = new ArrayList();
        Log.d(TAG, "ADMIN SCREEN UID LIST: " + uidList);
        Log.d(TAG, "ADMIN SCREEN FINAL NAME LIST: " + finalNameList);
        db.collection("companies").document(companyName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult()!=null) {
                    for (int i = 0; i < uidList.size(); i++) {
                        userInfo = (List<Object>)task.getResult().get((String)uidList.get(i));
                        if((boolean)userInfo.get(4) == true)
                        {
                            passedTestList.add(finalNameList.get(i));
                        }
                        else
                        {
                            notPassedTestList.add(finalNameList.get(i));
                        }
                        hasWatchedVid.add(userInfo.get(2).toString());
                        hasPassedTest.add(userInfo.get(4).toString());
                    }
                    Log.d(TAG, "ADMIN SCREEN PASSED TEST LIST: " + passedTestList);
                    Log.d(TAG, "ADMIN SCREEN NOT PASSED TEST LIST: " + notPassedTestList);
                    userlist.setOnItemClickListener(mOnItemClickListener);
                    myTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            myViewPager.setCurrentItem(tab.getPosition());
                            if(tab.getPosition()==0)
                            {
                                Log.d(TAG, "TAB POSITION IS 1");
                                userlist.setAdapter(myAdapter);
                                userlist.setOnItemClickListener(mOnItemClickListener);
                            }
                            else if(tab.getPosition()==1)
                            {
                                Log.d(TAG, "TAB POSITION IS 2");
                                final ArrayAdapter<String> completeListAdapter = new ArrayAdapter(AdminScreen.this, R.layout.adminscreen_user_list_items_green, passedTestList);
                                userlist.setAdapter(completeListAdapter);
                                userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        userlist.setOnItemClickListener(null);
                                        backButton.setClickable(false);
                                        settingsButton.setClickable(false);
                                        groupCodeView.setClickable(false);
                                        myAnimation.setVisibility(View.VISIBLE);
                                        myAnimation.playAnimation();
                                        userName = (String)passedTestList.get(position);
                                        formattedName = userName.substring(userName.indexOf(" ")+1)
                                                + "." + userName.substring(0, userName.indexOf(" "));
                                        Log.d(TAG, "FORMATTED NAME: " + formattedName);
                                        db.collection("users").document(formattedName).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.getResult()!=null)
                                                        {
                                                            selectedUserID = (String)task.getResult().get("uid");
                                                        }
                                                        Intent gotouserscreen = new Intent(AdminScreen.this, UserSettings.class);
                                                        Bundle userscreen = new Bundle();
                                                        userscreen.putString("Selected User ID", selectedUserID);
                                                        Log.d(TAG, "SELECTED USER ID IS: " + selectedUserID);
                                                        userscreen.putString("User Name", userName);
                                                        userscreen.putInt("Current Item", myViewPager.getCurrentItem());
                                                        userscreen.putString("Company Name", companyName);
                                                        userscreen.putStringArrayList("Has Watched Video", hasWatchedVid);
                                                        Log.d(TAG, "ABOUT TO PUSH SELECTED POSITION: " + position);
                                                        userscreen.putInt("Selected Position", position);
                                                        userscreen.putStringArrayList("Has Passed Test", hasPassedTest);
                                                        userscreen.putStringArrayList("UID List", uidList);
                                                        userscreen.putStringArrayList("User Name List", finalNameList);
                                                        userscreen.putString("Company ID", companyID);
                                                        userscreen.putString("Dated Training ID", datedTrainingID);
                                                        userscreen.putString("User ID", userID);
                                                        gotouserscreen.putExtras(userscreen);
                                                        startActivity(gotouserscreen);
                                                    }
                                                });
                                    }
                                });
                            }
                            else
                            {
                                Log.d(TAG, "TAB POSITION IS 3");
                                final ArrayAdapter<String> incompleteListAdapter = new ArrayAdapter(AdminScreen.this, R.layout.adminscreen_user_list_items_red, notPassedTestList);
                                userlist.setAdapter(incompleteListAdapter);
                                userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        userlist.setOnItemClickListener(null);
                                        backButton.setClickable(false);
                                        settingsButton.setClickable(false);
                                        groupCodeView.setClickable(false);
                                        myAnimation.setVisibility(View.VISIBLE);
                                        myAnimation.playAnimation();
                                        userName = (String)notPassedTestList.get(position);
                                        formattedName = userName.substring(userName.indexOf(" ")+1)
                                                + "." + userName.substring(0, userName.indexOf(" "));
                                        Log.d(TAG, "FORMATTED NAME: " + formattedName);
                                        db.collection("users").document(formattedName).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.getResult()!=null)
                                                        {
                                                            selectedUserID = (String)task.getResult().get("uid");
                                                        }
                                                        Intent gotouserscreen = new Intent(AdminScreen.this, UserSettings.class);
                                                        Bundle userscreen = new Bundle();
                                                        userscreen.putString("Selected User ID", selectedUserID);
                                                        Log.d(TAG, "SELECTED USER ID IS: " + selectedUserID);
                                                        userscreen.putString("User Name", userName);
                                                        userscreen.putString("Company Name", companyName);
                                                        userscreen.putStringArrayList("Has Watched Video", hasWatchedVid);
                                                        userscreen.putStringArrayList("Has Passed Test", hasPassedTest);
                                                        Log.d(TAG, "ABOUT TO PUSH SELECTED POSITION: " + position);
                                                        userscreen.putInt("Selected Position", position);
                                                        userscreen.putInt("Current Item", myViewPager.getCurrentItem());
                                                        userscreen.putStringArrayList("UID List", uidList);
                                                        userscreen.putStringArrayList("User Name List", finalNameList);
                                                        userscreen.putString("Company ID", companyID);
                                                        userscreen.putString("Dated Training ID", datedTrainingID);
                                                        userscreen.putString("User ID", userID);
                                                        gotouserscreen.putExtras(userscreen);
                                                        startActivity(gotouserscreen);
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) { }
                        @Override
                        public void onTabReselected(TabLayout.Tab tab) { }
                    });
                    completedTrainings.setText(" Completed Trainings : " + passedTestList.size() + " / " + finalNameList.size());
                    myAdapter = new ArrayAdapter
                            (AdminScreen.this, R.layout.adminscreen_user_list_items_transparent, finalNameList){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent){
                            // Get the current item from ListView
                            View view = super.getView(position,convertView,parent);

                            if(passedTestList.contains(finalNameList.get(position)))
                            {
                                view.setBackgroundResource(R.drawable.adminscreen_userlistview_style_green);
                            }
                            else if(notPassedTestList.contains(finalNameList.get(position)))
                            {
                                view.setBackgroundResource(R.drawable.adminscreen_userlistview_style_red);
                            }
                            else
                            {
                                Log.d(TAG, "hasPassedTestArray[listiew position] doesn't equal true or false");
                            }
                            return view;
                        }
                    };
                    userlist.setAdapter(myAdapter);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                backButton.setClickable(true);
                settingsButton.setClickable(true);
                groupCodeView.setClickable(true);
                Log.d(TAG, "FAILED TO FIND ALL USER ID LISTS IN COMPANY DOCUMENT");
            }
        });
    }

    public void backtoSelectGroup(View v)
    {
        backButton.setClickable(false);
        settingsButton.setClickable(false);
        groupCodeView.setClickable(false);
        Intent selectGroup = new Intent(AdminScreen.this, SelectGroup.class);
        selectGroup.putExtra("User ID", userID);
        selectGroup.putExtra("First Name", userFirstName);
        selectGroup.putExtra("Last Name", userLastName);
        startActivity(selectGroup);
    }

    public void toAdminSettingsClicked(View v)
    {
        backButton.setClickable(false);
        settingsButton.setClickable(false);
        groupCodeView.setClickable(false);
        Intent adminSettings = new Intent(AdminScreen.this, AdminSettings.class);
        adminSettings.putExtra("Company Name", companyName);
        adminSettings.putExtra("Company ID", companyID);
        adminSettings.putExtra("Current Item", myViewPager.getCurrentItem());
        adminSettings.putExtra("Dated Training ID", datedTrainingID);
        adminSettings.putExtra("User ID", userID);
        adminSettings.putExtra("User First Name", userFirstName);
        adminSettings.putExtra("User Last Name", userLastName);
        adminSettings.putStringArrayListExtra("UID List", uidList);
        adminSettings.putStringArrayListExtra("User Name List", finalNameList);
        adminSettings.putExtra("User Name", userName);
        startActivity(adminSettings);
    }

    public void shareCodeFromAdmin(View v)
    {
        Intent shareCode = new Intent(Intent.ACTION_SEND);
        shareCode.setType("text/plain");
        String shareBody = "You have been invited to a Kingdom Safety training group! Download the " +
                "Kingdom Safety app on the iOS App Store and Google Play, then use this code to" +
                " join the group: " + companyID + "\n" + "\n" +
                "iOS: https://apps.apple.com/us/app/kingdom-safety/id1483537269" + "\n" + "\n" +
                "Google Play: https://play.google.com/store/apps/details?id=com.something.welcomescreen";
        shareCode.putExtra(Intent.EXTRA_SUBJECT, shareBody);
        startActivity(Intent.createChooser(shareCode, "Share Using"));
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            userlist.setOnItemClickListener(null);
            backButton.setClickable(false);
            settingsButton.setClickable(false);
            groupCodeView.setClickable(false);
            myAnimation.setVisibility(View.VISIBLE);
            myAnimation.playAnimation();
            userName = (String)finalNameList.get(position);
            formattedName = userName.substring(userName.indexOf(" ")+1)
                    + "." + userName.substring(0, userName.indexOf(" "));
            Log.d(TAG, "FORMATTED NAME: " + formattedName);
            db.collection("users").document(formattedName).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null)
                            {
                                selectedUserID = (String)task.getResult().get("uid");
                            }
                            Intent gotouserscreen = new Intent(AdminScreen.this, UserSettings.class);
                            Bundle userscreen = new Bundle();
                            userscreen.putString("Selected User ID", selectedUserID);
                            Log.d(TAG, "SELECTED USER ID IS: " + selectedUserID);
                            userscreen.putString("User Name", userName);
                            userscreen.putString("Company Name", companyName);
                            userscreen.putStringArrayList("UID List", uidList);
                            userscreen.putStringArrayList("Has Watched Video", hasWatchedVid);
                            userscreen.putStringArrayList("Has Passed Test", hasPassedTest);
                            Log.d(TAG, "ABOUT TO PUSH SELECTED POSITION: " + position);
                            userscreen.putInt("Selected Position", position);
                            userscreen.putInt("Current Item", myViewPager.getCurrentItem());
                            userscreen.putStringArrayList("User Name List", finalNameList);
                            userscreen.putString("Company ID", companyID);
                            userscreen.putString("Dated Training ID", datedTrainingID);
                            userscreen.putString("User ID", userID);
                            gotouserscreen.putExtras(userscreen);
                            startActivity(gotouserscreen);
                        }
                    });
        }
    };
}