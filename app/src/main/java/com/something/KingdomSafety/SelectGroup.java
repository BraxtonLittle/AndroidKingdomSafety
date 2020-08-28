package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SelectGroup extends AppCompatActivity {
private ArrayList<String> groups, uidList, userNameList, userUIDList, userPosition;
private int pushedRandNum, myVidPosition, queriedStartMonth, queriedStartYear, queriedStartDay, calcCurrentYear, calcCurrentMonth, calcCurrentDay;
private int startingIntValue, endingIntValue, hourDifference, weeksBetween, indexOfEqualSign;
private String formattedDate, datedTrainingID, mapArrayString, beforeEqualString, afterEqualString, tempUid, rearrangeUID, userDocID;
    private SimpleDateFormat currentFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private Date todaysdate;
private static final String TAG = "SelectGroup";
private SwipeMenuListView listView;
private Map documentInfo;
private Button backButton, joinnewgroupButton;
private ArrayList<Boolean> hasPassedTestArray;
private Object [] mapArray;
private FirebaseFirestore db;
private Map<String, Object> docMap, myMap;
private BaseArrayAdapter customAdapter;
private TextView listCellItem;
private LottieAnimationView myAnimationView;
private boolean watchedVid, takenTest, passedTest, ingroup;
private String groupName, companyId, isAdminString, userID, userFirstName, userLastName, companyName, dateStarted, trainingName, trainingToDeleteName;
private List<Object> userInfo, adminUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        Bundle myBundle = getIntent().getExtras();
        backButton = findViewById(R.id.backbutton2);
        joinnewgroupButton = findViewById(R.id.joinnewgroup);
        if(myBundle!=null) {
            userFirstName = myBundle.getString("First Name", userFirstName);
            userLastName = myBundle.getString("Last Name", userLastName);
            userID = myBundle.getString("User ID");
        }

        Log.d(TAG, "USER FIRST NAME AND LAST NAME: " + userFirstName + ", " + userLastName);
        groups = new ArrayList<String>();
        userPosition = new ArrayList<>();
        listView = (SwipeMenuListView)findViewById(R.id.grouplist);
        myAnimationView = findViewById(R.id.selectgrouplottieanimationview);
        myAnimationView.setVisibility(View.INVISIBLE);
        ingroup = false;
        todaysdate = new Date(System.currentTimeMillis());
        formattedDate = currentFormatter.format(todaysdate);
        db = FirebaseFirestore.getInstance();
        startingIntValue = endingIntValue = hourDifference = weeksBetween = queriedStartDay = queriedStartMonth
                = queriedStartMonth = calcCurrentDay = calcCurrentMonth = calcCurrentYear = 0;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //temp displaymetrics data
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels/displayMetrics.density;
        Log.d(TAG, "DISPLAY METRICS HEIGHT: " + dpHeight);
        myAnimationView.setVisibility(View.VISIBLE);
        myAnimationView.playAnimation();
        db.collection("companies").whereArrayContains(userID, userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size()==0) {
                                Intent joinstartgroup = new Intent(SelectGroup.this, JoinStartGroup.class);
                                joinstartgroup.putExtra("User ID", userID);
                                joinstartgroup.putExtra("First Name", userFirstName);
                                joinstartgroup.putExtra("Last Name", userLastName);
                                startActivity(joinstartgroup);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    groupName = document.getId();
                                    if(!groups.contains(groupName)) {
                                        groups.add(groupName);
                                    }
                                }
                            }
                            db.collection("companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.getResult()!=null)
                                    {
                                        for(DocumentSnapshot compDoc : task.getResult())
                                        {
                                            for(int i = 0; i<groups.size(); i++)
                                            {
                                                if(groups.get(i).equalsIgnoreCase((String)compDoc.getId()))
                                                {
                                                    adminUserInfo = (List<Object>)compDoc.get(userID);
                                                    if((boolean) adminUserInfo.get(1)==true)
                                                    {
                                                        userPosition.add("Admin");
                                                    }
                                                    else if((boolean)adminUserInfo.get(1)==false)
                                                    {
                                                        userPosition.add("User");
                                                    }
                                                }
                                            }
                                        }
                                        Log.d(TAG, "GROUPS LIST: " + groups);
                                    }
                                    Log.d(TAG, "USER POSITION LIST: " + userPosition);
                                    customAdapter = new BaseArrayAdapter(SelectGroup.this, R.layout.trainingnameslistitemsmanage, groups, userPosition);
                                    listView.setAdapter(customAdapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    listView.setOnItemClickListener(null);
                                    listCellItem = view.findViewById(R.id.list_content);
                                    trainingName = groups.get(position);
                                    uidList = new ArrayList<String>();
                                    userNameList = new ArrayList<String>();
                                    userUIDList = new ArrayList<String>();
                                    myAnimationView.setVisibility(View.VISIBLE);
                                    myAnimationView.playAnimation();
                                    db.collection("companies").document(groups.get(position)).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult()!=null) {
                                                        DocumentSnapshot document = task.getResult();
                                                        userInfo = (List<Object>) document.get(userID);
                                                        isAdminString = userInfo.get(1).toString();
                                                        dateStarted = document.getString("dateStarted");
                                                        companyId = document.get("Company ID").toString();
                                                        companyName = document.getId();
                                                        watchedVid = (Boolean) userInfo.get(2);
                                                        takenTest = (Boolean) userInfo.get(3);
                                                        passedTest = (Boolean) userInfo.get(4);
                                                        Log.d(TAG, "DATE STARTED: "  + dateStarted);
                                                        queriedStartMonth = Integer.parseInt(dateStarted.substring(0, 2));
                                                        Log.d(TAG, "QUERIED MONTH: " + queriedStartMonth);
                                                        queriedStartYear = Integer.parseInt(dateStarted.substring(dateStarted.lastIndexOf("-") + 1));
                                                        Log.d(TAG, "QUERIED YEAR: " + queriedStartYear);
                                                        queriedStartDay = Integer.parseInt(dateStarted.substring(3, 5));
                                                        Log.d(TAG, "QUERIED DAY: " + queriedStartDay);
                                                        startingIntValue += (queriedStartDay*24);
                                                        startingIntValue+=(queriedStartMonth*730);
                                                        startingIntValue+=(queriedStartYear*8760);

                                                        formattedDate = currentFormatter.format(todaysdate);
                                                        calcCurrentYear = Integer.parseInt(formattedDate.substring(0, 4));
                                                        calcCurrentMonth = Integer.parseInt(formattedDate.substring(5, 7));
                                                        calcCurrentDay = Integer.parseInt(formattedDate.substring(8, 10));
                                                        endingIntValue+= (calcCurrentDay*24);
                                                        endingIntValue+= (calcCurrentMonth*730);
                                                        endingIntValue+= (calcCurrentYear*8760);
                                                        Log.d(TAG, "TODAY'S YEAR: " + calcCurrentYear);
                                                        Log.d(TAG, "TODAY'S MONTH: " + calcCurrentMonth);
                                                        Log.d(TAG, "TODAY'S DAY: " + calcCurrentDay);

                                                        hourDifference = endingIntValue-startingIntValue;
                                                        weeksBetween = hourDifference/168;
                                                        Log.d(TAG, "WEEKS BETWEEN: " + weeksBetween);
                                                        datedTrainingID = String.valueOf(weeksBetween);
                                                        Log.d(TAG, "DATED TRAINING ID: " + datedTrainingID);
                                                        if(task.getResult()!=null) {
                                                            if (isAdminString.equals("true")) {
                                                                db.collection("companies").document(companyName).get() //.document(companyName)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                if (documentSnapshot != null) {
                                                                                    documentInfo = documentSnapshot.getData();
                                                                                    Log.d(TAG, "MAP ARRAY: " + documentInfo);
                                                                                    mapArray = documentInfo.entrySet().toArray();
                                                                                    for (int i = 0; i < mapArray.length; i++) {
                                                                                        mapArrayString = mapArray[i].toString();
                                                                                        if (mapArrayString.contains("=")) {
                                                                                            indexOfEqualSign = mapArrayString.indexOf("=");
                                                                                            beforeEqualString = mapArrayString.substring(0, indexOfEqualSign);
                                                                                            afterEqualString = mapArrayString.substring((indexOfEqualSign + 1));
                                                                                            if (afterEqualString.contains(beforeEqualString)) {
                                                                                                uidList.add(beforeEqualString);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            if(task.getResult()!=null) {
                                                                                                for(DocumentSnapshot userDoc : task.getResult()) {
                                                                                                    userDocID = (String)userDoc.get("uid");
                                                                                                    for(int j = 0; j<uidList.size(); j++)
                                                                                                    {
                                                                                                        if(uidList.get(j).toString().equals(userDocID))
                                                                                                        {
                                                                                                            userUIDList.add(userDocID);
                                                                                                            rearrangeUID = userDoc.getId();
                                                                                                            rearrangeUID = rearrangeUID.substring(rearrangeUID.indexOf(".") + 1) +
                                                                                                                    " " + rearrangeUID.substring(0, rearrangeUID.indexOf("."));
                                                                                                            userNameList.add(rearrangeUID);
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                Log.d(TAG, "SELECT GROUP TO PUSH UID LIST: " + uidList);
                                                                                                int uidIndex, newIndex;
                                                                                                String tempString;
                                                                                                for(int c = 0; c<userUIDList.size(); c++)
                                                                                                {
                                                                                                    uidIndex = uidList.indexOf(userUIDList.get(c));
                                                                                                    Collections.swap(uidList, c, uidIndex);
                                                                                                }
                                                                                                Intent adminScreen = new Intent(SelectGroup.this, AdminScreen.class);
                                                                                                adminScreen.putExtra("Company Name", companyName);
                                                                                                adminScreen.putExtra("Dated Training ID", datedTrainingID);
                                                                                                adminScreen.putExtra("Training Name", trainingName);
                                                                                                adminScreen.putStringArrayListExtra("UID List", uidList);
                                                                                                adminScreen.putStringArrayListExtra("User Name List", userNameList);
                                                                                                Log.d(TAG,"SELECT GROUP TO PUSH AFTER SWAP: " + uidList);
                                                                                                Log.d(TAG, "SELECT GROUP TO PUSH USER NAME LIST: " + userNameList);
                                                                                                Log.d(TAG, "SELECT GROUP TO PUSH USERUID LIST: " + userUIDList);
                                                                                                adminScreen.putExtra("Company ID", companyId);
                                                                                                adminScreen.putExtra("User ID", userID);
                                                                                                adminScreen.putExtra("User First Name", userFirstName);
                                                                                                adminScreen.putExtra("User Last Name", userLastName);
                                                                                                startActivity(adminScreen);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                            } else
                                                                {
                                                                Intent userScreen = new Intent(SelectGroup.this, UsersScreen.class);
                                                                userScreen.putExtra("Company Name", companyName);
                                                                userScreen.putExtra("User ID", userID);
                                                                userScreen.putExtra("Training Name", trainingName);
                                                                userScreen.putExtra("Dated Training ID", datedTrainingID);
                                                                userScreen.putExtra("Date Started", dateStarted);
                                                                userScreen.putExtra("Watched Video", watchedVid);
                                                                userScreen.putExtra("User First Name", userFirstName);
                                                                userScreen.putExtra("User Last Name", userLastName);
                                                                userScreen.putExtra("Passed Test", passedTest);
                                                                userScreen.putExtra("Taken Test", takenTest);
                                                                userScreen.putExtra("Watched Video", watchedVid);
                                                                Log.d(TAG, "USER INFO LIST FROM SELECTGROUP: " + userInfo);
                                                                userScreen.putParcelableArrayListExtra("User Info List", (ArrayList) userInfo);
                                                                startActivity(userScreen);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(SelectGroup.this, "The selected group doesn't exist!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(SelectGroup.this, "Failed to retrieve Group Data", Toast.LENGTH_SHORT).show();
                                                        myAnimationView.setVisibility(View.INVISIBLE);
                                                        myAnimationView.cancelAnimation();
                                                    }
                                                }
                                            });
                                }
                            });

                            SwipeMenuCreator creator = new SwipeMenuCreator() {
                                @Override
                                public void create(SwipeMenu menu) {
                                    // create "delete" item
                                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                                            getApplicationContext());
                                    // set item background
                                    deleteItem.setBackground(new ColorDrawable(Color.RED));
                                    // set item width
                                    deleteItem.setWidth(300);
                                    deleteItem.setTitle("Leave Group");
                                    deleteItem.setTitleColor(Color.WHITE);
                                    deleteItem.setTitleSize(15);
                                    // set a icon
                                    //deleteItem.setIcon(R.drawable.ic_delete);
                                    // add to menu
                                    menu.addMenuItem(deleteItem);
                                }
                            };
                            listView.setMenuCreator(creator);
                            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                    switch (index) {
                                        case 0:
                                            for(int i = 0; i<listView.getChildCount(); i++)
                                            {
                                                listView.getChildAt(i).setEnabled(false);
                                            }
                                            trainingToDeleteName = groups.get(position);
                                            myMap = new HashMap<>();
                                            myMap.put(userID, FieldValue.delete());
                                            myAnimationView.setVisibility(View.VISIBLE);
                                            myAnimationView.playAnimation();
                                            db.collection("companies").document(groups.get(position))
                                                    .update(myMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    docMap = new HashMap<>();
                                                    db.collection("companies").document(groups.get(position)).get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    docMap = documentSnapshot.getData();
                                                                    if(docMap.size()==3)
                                                                    {
                                                                        db.collection("companies").document(trainingToDeleteName).collection("CustomTrainings")
                                                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                for(DocumentSnapshot doc:queryDocumentSnapshots)
                                                                                {
                                                                                    db.collection("companies").document(trainingToDeleteName)
                                                                                            .collection("CustomTrainings").document(doc.getId()).delete()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    db.collection("companies").document(trainingToDeleteName).collection("Trainings")
                                                                                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                            for(DocumentSnapshot trainingDoc: queryDocumentSnapshots) {
                                                                                                                db.collection("companies").document(trainingToDeleteName)
                                                                                                                        .collection("Trainings").document(trainingDoc.getId()).delete()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if(Integer.parseInt(trainingDoc.getId())==51) {
                                                                                                                                    db.collection("companies").document(trainingToDeleteName).delete();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                }
                                                                                groups.remove(position);
                                                                                userPosition.remove(position);
                                                                                menu.removeMenuItem(menu.getMenuItem(index));
                                                                                customAdapter.notifyDataSetChanged();
                                                                                myAnimationView.setVisibility(View.INVISIBLE);
                                                                                myAnimationView.cancelAnimation();
                                                                                if(groups.size()>0) {
                                                                                    listView.setAdapter(customAdapter);
                                                                                    for(int d = 0; d<listView.getChildCount(); d++)
                                                                                    {
                                                                                        listView.getChildAt(d).setEnabled(true);
                                                                                    }
                                                                                }
                                                                                else
                                                                                {
                                                                                    Intent joinstartgroup = new Intent(SelectGroup.this, JoinStartGroup.class);
                                                                                    joinstartgroup.putExtra("User ID", userID);
                                                                                    Log.d(TAG, "ABOUT TO PUSH FIRST AND LAST NAME: " + userFirstName + ", " + userLastName);
                                                                                    joinstartgroup.putExtra("First Name", userFirstName);
                                                                                    joinstartgroup.putExtra("Last Name", userLastName);
                                                                                    startActivity(joinstartgroup);
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.d(TAG, "THERE IS MORE THAN 3 FIELDS IN GROUP DOC APPARENTLY");
                                                                        groups.remove(position);
                                                                        userPosition.remove(position);
                                                                        menu.removeMenuItem(menu.getMenuItem(index));
                                                                        customAdapter.notifyDataSetChanged();
                                                                        myAnimationView.setVisibility(View.INVISIBLE);
                                                                        myAnimationView.cancelAnimation();
                                                                        if(groups.size()>0) {
                                                                            listView.setAdapter(customAdapter);
                                                                        }
                                                                        else
                                                                        {
                                                                            Intent joinstartgroup = new Intent(SelectGroup.this, JoinStartGroup.class);
                                                                            joinstartgroup.putExtra("First Name", userFirstName);
                                                                            joinstartgroup.putExtra("Last Name", userLastName);
                                                                            joinstartgroup.putExtra("User ID", userID);
                                                                            startActivity(joinstartgroup);
                                                                        }
                                                                    }
                                                                    //Toast.makeText(SelectGroup.this, "docMap size: " + docMap.size(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Intent selectGroup = new Intent(SelectGroup.this, SelectGroup.class);
                                                            selectGroup.putExtra("First Name", userFirstName);
                                                            selectGroup.putExtra("Last Name", userLastName);
                                                            selectGroup.putExtra("User ID", userID);
                                                            Toast.makeText(SelectGroup.this, "Failed to delete company document due to no users", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                            break;
                                    }
                                    // false : close the menu; true : not close the menu
                                    return false;
                                }
                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SelectGroup.this, "FAILED TO RETRIEVE ADMIN AND USER VALUES FROM DATABASE, PLEASE RELOAD AND TRY AGAIN", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else
                            {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            myAnimationView.setVisibility(View.INVISIBLE);
                            myAnimationView.cancelAnimation();
                        }
                        myAnimationView.cancelAnimation();
                        myAnimationView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    public void joinnewgroup(View v)
    {
        joinnewgroupButton.setClickable(false);
        Intent joingroup = new Intent(SelectGroup.this, JoinExistingGroup.class);
        db.collection("companies").whereEqualTo(userID, userID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult()!=null)
                        {
                            ingroup = true;
                        }
                        else
                        {
                            ingroup = false;
                        }

                        joingroup.putExtra("First Name", userFirstName);
                        joingroup.putExtra("ingroup", ingroup);
                        joingroup.putExtra("Last Name", userLastName);
                        joingroup.putExtra("User ID", userID);
                        startActivity(joingroup);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                joinnewgroupButton.setClickable(true);
                Log.d(TAG, "FAILED TO CHECK IF USER IS IN A GROUP");
            }
        });
    }

    public void backtoLogin(View v)
    {
        backButton.setClickable(false);
        Intent toLogin = new Intent(SelectGroup.this, LoginActivity.class);
        toLogin.putExtra("RandNum", pushedRandNum);
        toLogin.putExtra("vidTime", myVidPosition);
        startActivity(toLogin);
    }

    public class BaseArrayAdapter extends ArrayAdapter<String>
    {
        private ArrayList<String> trainings, userPosition;
        private LayoutInflater mInflater;
        private int mLayout;

        public BaseArrayAdapter(Context context, int textViewResourceId, ArrayList<String> trainingNames, ArrayList<String> userPositionList) {
            super(context, textViewResourceId, trainingNames);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
            trainings = trainingNames;
            userPosition = userPositionList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(this.mLayout, null);
                assert view != null;
                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.trainingscustomtextview);
                holder.userPosition = (TextView) view.findViewById(R.id.weeksTextView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            String compName = trainings.get(position);
            holder.title.setText(compName);
            String userPositionText = userPosition.get(position);
            holder.userPosition.setTextSize(16);
            holder.userPosition.setText(userPositionText);
            return view;
        }
    }

    static class ViewHolder {
        TextView title;
        TextView userPosition;
    }
}


