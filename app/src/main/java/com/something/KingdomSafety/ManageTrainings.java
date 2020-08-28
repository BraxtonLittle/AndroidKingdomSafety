package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTrainings extends AppCompatActivity {
    private String compID, groupName, companyType, userFirstName, userLastName, questionField, cellText, trainingID, datedTrainingID;
    private ArrayList<String> trainingNames, customTrainingsContent, trainingscontent, previewTrainingsContent, questionSet,
            customDocIDList, customTrainingIDList, noDuplicateTrainingsList, userNameList, uidList;
    private ArrayList questionFieldList;
    private HashMap<String, String> myMap;
    private FirebaseFirestore db;
    private boolean ingroup;
    private String videoURL, itemName, switchName;
    private int numOfQuestions, indexOfTrainingStart, indexOfQuestionStart, numCustomTraining, idTrick;
    private ImageView questionContainer, trainingListContainer, dummyView;
    private TextView listItemView, questionTitleTextView, ans1TextView, ans2TextView, ans3TextView, ans4TextView, reviewTrainingsTitleTextView;
    private TextView titleView, ans1View, ans2View, ans3View, ans4View, previewTrainingName;
    private ConstraintLayout previewWindow, reviewTrainingsContainer, tempLayout, myLayout;
    private ViewGroup.MarginLayoutParams layoutParams;
    private ConstraintSet previewSet, questionBoxSet;
    private ListView regularNamesList, mListView, previewTrainingsListView;
    private List<Object> queriedQuestionData;
    private Button addButton, editButton, nextButton, backButton, cancelWindowButton;
    private MyArrayAdapter mAdapter;
    private boolean mSortable = false;
    private String mDragString, customQuestionNumString, userId, questionBlank;
    private long numberOfQuestions;
    private int customNumOfQuestions, previewUsableQuestionNum, questionBoxDynamicHeight, weeksBetween;
    private int previousPosition = -1;
    private int mPosition = -1;
    private int startPosition = -1;
    private int firstVisiblePos=-1;
    private boolean hasScrolled = false;
    private int lastVisiblePos=-1;
    private int customTextViewID, customTrainingStart, previousBoxID, manageReview;
    private final String TAG = "REVIEWTRAININGS";
    private Rect rect;
    private LottieAnimationView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trainings);
        addButton = findViewById(R.id.addButton);
        addButton.setVisibility(View.INVISIBLE);
        nextButton = findViewById(R.id.nextButton);
        trainingListContainer = findViewById(R.id.trainingListContainer);
        backButton = findViewById(R.id.backtoGroupType);
        loadingView = findViewById(R.id.manageTrainingsLottieView);
        previewWindow = findViewById(R.id.previewtrainingcontainer);
        previewWindow.setVisibility(View.INVISIBLE);
        cancelWindowButton =  findViewById(R.id.cancelWindowButton);
        cancelWindowButton.setVisibility(View.INVISIBLE);
        reviewTrainingsContainer = findViewById(R.id.reviewTrainingsConstraintContainer);
        reviewTrainingsTitleTextView = findViewById(R.id.ReviewTrainingsTitle);
        regularNamesList = (ListView) findViewById(R.id.regulartrainingnameslist);
        customTextViewID = (int) R.id.trainingscustomtextview;
        dummyView = findViewById(R.id.blankdummyview);
        editButton = findViewById(R.id.editButton);
        mListView = findViewById(R.id.rearrangeabletrainingnameslist);
        previewTrainingName = findViewById(R.id.previewtrainingname);
        previewTrainingName.setVisibility(View.INVISIBLE);
        previewTrainingsListView = findViewById(R.id.previewtraininglistview);
        previewTrainingsListView.setVisibility(View.INVISIBLE);
        trainingNames = new ArrayList<>();
        trainingscontent = new ArrayList<>();
        customTrainingsContent = new ArrayList<>();
        previewTrainingsContent = new ArrayList<>();
        Bundle myBundle = getIntent().getExtras();
        if (myBundle != null) {
            compID = myBundle.getString("Company ID");
            groupName = myBundle.getString("Group Name");
            companyType = myBundle.getString("Company Type");
            datedTrainingID = myBundle.getString("Dated Training ID");
            weeksBetween = Integer.parseInt(datedTrainingID);
            ingroup = myBundle.getBoolean("ingroup");
            userId = myBundle.getString("User ID");
            userNameList = myBundle.getStringArrayList("User Name List");
            uidList = myBundle.getStringArrayList("UID List");
            customDocIDList = myBundle.getStringArrayList("Custom Doc ID List");
            Log.d(TAG, "RECIEVED CUSTOM DOC ID LIST: " + customDocIDList);
            customTrainingIDList = myBundle.getStringArrayList("Custom Training ID List");
            Log.d(TAG, "CUSTOM TRAINING ID LIST IN BUNDLE: " + customTrainingIDList);
            Log.d(TAG, "REVIEW TRAININGS USER ID: " + userId);
            //manageReview = myBundle.getInt("Manage Review");
            Log.d(TAG, "COMPANY TYPE: " +companyType);
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            customTrainingsContent = myBundle.getStringArrayList("Custom Trainings Content");
            trainingNames = myBundle.getStringArrayList("Training Names List");
        }
        Log.d(TAG, "USER FIRST AND LAST NAME IN MANAGE TRAININGS: " + userFirstName + ", " + userLastName);
        Log.d(TAG, "TRAINING NAMES LIST SIZE: " + trainingNames.size());
        Log.d(TAG, "COMPANY ID: " + compID);
        Log.d(TAG, "CUSTOM TRAININGS CONTENT IN CREATE METHOD: " + customTrainingsContent);
        db = FirebaseFirestore.getInstance();
        rect = new Rect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        Log.d(TAG, "EVENT. SCREEN HEIGHT: " + height);
        loadingView.setVisibility(View.INVISIBLE);
        loadingView.cancelAnimation();
        noDuplicateTrainingsList = new ArrayList<>();
        for(int j = 0; j<trainingNames.size(); j++)
        {
            if(!noDuplicateTrainingsList.contains(trainingNames.get(j)))
            {
                noDuplicateTrainingsList.add(trainingNames.get(j));
            }
        }
        Log.d(TAG, "NO DUPLICATE TRAININGS LIST: " + noDuplicateTrainingsList);
        mListView.setVisibility(View.INVISIBLE);
        BaseArrayAdapter adapter = new BaseArrayAdapter(getApplicationContext(), R.layout.trainingnameslistitemsmanage, trainingNames, weeksBetween);
        regularNamesList.setAdapter(adapter);
        idTrick=0;
        db.collection("companies").document(groupName).collection("CustomTrainings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    for (int b = 0; b < trainingNames.size(); b++) {
                        if (trainingNames.get(b).equalsIgnoreCase(doc.getId())) {
                            if (!trainingscontent.contains(doc.getId())) {
                                //Log.d(TAG, "DOC ID: " + doc.getId());
                                numberOfQuestions = (Long) doc.get("numberOfQuestions");
                                //Log.d(TAG, "NUMBER OF QUESTIONS: " + numberOfQuestions);
                                trainingscontent.add(doc.getId());
                                trainingscontent.add((String) doc.get("videoURL"));
                                trainingscontent.add(String.valueOf(numberOfQuestions));
                                for (int i = 0; i < numberOfQuestions; i++) {
                                    questionField = "question" + String.valueOf(i);
                                    queriedQuestionData = (List<Object>) doc.get(questionField);
                                    //Log.d(TAG, "QUERIED QUESTION DATA LIST: " + queriedQuestionData);
                                    trainingscontent.add(questionField);
                                    trainingscontent.add((String) queriedQuestionData.get(0)); //actual question title
                                    trainingscontent.add((String.valueOf(queriedQuestionData.get(1)))); //Right answer
                                    trainingscontent.add((String) queriedQuestionData.get(2)); //Answer option 1
                                    trainingscontent.add((String) queriedQuestionData.get(3)); //Answer option 2
                                    trainingscontent.add((String) queriedQuestionData.get(4)); //Answer option 3
                                    trainingscontent.add((String) queriedQuestionData.get(5)); //Answer option 4
                                }
                            }
                        } else {
                            if (customTrainingsContent != null) {
                                if (customTrainingsContent.contains(trainingNames.get(b))) {
                                    Log.d(TAG, "THERE IS A CUSTOM TRAINING WITHIN THE TRAINING NAMES LIST");
                                    customTrainingStart = customTrainingsContent.indexOf(trainingNames.get(b));
                                    if (!trainingscontent.contains(customTrainingsContent.get(customTrainingStart + 1))) {
                                        trainingscontent.add(trainingNames.get(b));
                                        trainingscontent.add(customTrainingsContent.get(customTrainingStart + 1)); //video URL
                                        customQuestionNumString = String.valueOf(customTrainingsContent.get(customTrainingStart + 2));
                                        Log.d(TAG, "CUSTOM QUESTION NUM STRING: " + customQuestionNumString);
                                        customNumOfQuestions = Integer.parseInt(customQuestionNumString);// integer form of question number
                                        trainingscontent.add(String.valueOf(customNumOfQuestions));
                                        for (int i = 0; i < customNumOfQuestions; i++) {
                                            questionField = "question" + String.valueOf(i);
                                            trainingscontent.add(questionField);
                                            trainingscontent.add((String) customTrainingsContent.get(customTrainingStart + (i * 7) + 4)); //actual question title
                                            trainingscontent.add(String.valueOf(customTrainingsContent.get(customTrainingStart + (i * 7) + 5))); //Right answer
                                            trainingscontent.add(customTrainingsContent.get(customTrainingStart + (i * 7) + 6)); //Answer option 1
                                            trainingscontent.add(customTrainingsContent.get(customTrainingStart + (i * 7) + 7)); //Answer option 2
                                            trainingscontent.add(customTrainingsContent.get(customTrainingStart + (i * 7) + 8)); //Answer option 3
                                            trainingscontent.add(customTrainingsContent.get(customTrainingStart + (i * 7) + 9)); //Answer option 4
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO DOWNLOAD TRAININGS FROM FIREBASE AND ADD THEM TO TRAININGS CONTENT ARRAYLIST");
            }
        });
        Log.d(TAG, "TRAININGS CONTENT: " + trainingscontent);
        regularNamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //listItemView = (TextView) view;
                questionSet = new ArrayList<>();
                //cellText = listItemView.getText().toString();
                cellText = adapter.getItem(position);
                previewUsableQuestionNum = Integer.parseInt((String) trainingscontent.get(trainingscontent.indexOf(cellText) + 2));
                if (previewUsableQuestionNum > 0)
                {
                    Log.d(TAG, "CELL TEXT: " + cellText);
                    previewWindow.setVisibility(View.VISIBLE);
                    previewTrainingName.setText(cellText);
                    previewTrainingName.setVisibility(View.VISIBLE);
                    cancelWindowButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.INVISIBLE);
                    editButton.setVisibility(View.INVISIBLE);
                    reviewTrainingsTitleTextView.setVisibility(View.INVISIBLE);
                    backButton.setVisibility(View.INVISIBLE);
                    trainingListContainer.setVisibility(View.INVISIBLE);
                    regularNamesList.setVisibility(View.INVISIBLE);
                    dummyView.setVisibility(View.VISIBLE);
                    dummyView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            previewWindow.setVisibility(View.INVISIBLE);
                            previewWindow.setVisibility(View.INVISIBLE);
                            nextButton.setVisibility(View.VISIBLE);
                            reviewTrainingsTitleTextView.setVisibility(View.VISIBLE);
                            editButton.setVisibility(View.VISIBLE);
                            backButton.setVisibility(View.VISIBLE);
                            regularNamesList.setVisibility(View.VISIBLE);
                            trainingListContainer.setVisibility(View.VISIBLE);
                            dummyView.setVisibility(View.INVISIBLE);
                        }
                    });
                    cancelWindowButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            previewWindow.setVisibility(View.INVISIBLE);
                            previewWindow.setVisibility(View.INVISIBLE);
                            nextButton.setVisibility(View.VISIBLE);
                            reviewTrainingsTitleTextView.setVisibility(View.VISIBLE);
                            editButton.setVisibility(View.VISIBLE);
                            backButton.setVisibility(View.VISIBLE);
                            regularNamesList.setVisibility(View.VISIBLE);
                            trainingListContainer.setVisibility(View.VISIBLE);
                            dummyView.setVisibility(View.INVISIBLE);
                        }
                    });
                    Log.d(TAG, "trainings content: " + trainingscontent);
                    Log.d(TAG, "custom trainings content: " + customTrainingsContent);
                    Log.d(TAG, "PREVIEW USABLE QUESTION NUM VALUE: " + cellText + " " + previewUsableQuestionNum);
                    for (int i = 0; i < previewUsableQuestionNum; i++) {
                        questionSet.add(cellText);
                    }
                    MyAdapter previewAdapter = new MyAdapter(ManageTrainings.this, R.layout.preview_training_question_box, questionSet);
                    previewAdapter.setTrainingsContent(trainingscontent);
                    previewTrainingsListView.setAdapter(previewAdapter);
                    previewTrainingsListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(ManageTrainings.this, "This Training doesn't have any questions to preview", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(editButton.getText()=="DONE")
        {
            mAdapter = new MyArrayAdapter(this, R.layout.trainingnameslistitemseditedmanage, trainingNames, weeksBetween);
            mListView.setAdapter(mAdapter);
            mListView.setVisibility(View.VISIBLE);
        }
        else if(editButton.getText()=="EDIT");
        {
            BaseArrayAdapter adapter = new BaseArrayAdapter(getApplicationContext(), R.layout.trainingnameslistitemsmanage, trainingNames, weeksBetween);
            regularNamesList.setAdapter(adapter);
        }
    }
    public void nextButtonPressed(View v) {
        nextButton.setClickable(false);
        addButton.setClickable(false);
        editButton.setClickable(false);
        backButton.setClickable(false);
        cancelWindowButton.setClickable(false);
        //main method that reassigns all trainings in collection to rearranged training list
        loadingView.setVisibility(View.VISIBLE);
        loadingView.playAnimation();
        numCustomTraining = 0;
        int i = 0;
        int k = 0;
        while(k<52)
        {
            //Same method that gets custom training info from list and updates CustomTrainings collection,
            //in case a new custom training is added to manage trainings list
            Map<String, Object> trainingDoc = new HashMap<>();
            indexOfTrainingStart = trainingscontent.indexOf(trainingNames.get(i));
            videoURL = trainingscontent.get(indexOfTrainingStart+1);
            numOfQuestions = Integer.valueOf(trainingscontent.get(indexOfTrainingStart+2));
            trainingDoc.put("numberOfQuestions", numOfQuestions);
            for(int j = 0; j<numOfQuestions; j++)
            {
                questionFieldList = new ArrayList(6);
                questionBlank = "question" + j;
                indexOfQuestionStart = trainingscontent.indexOf(trainingNames.get(i)) + 3 + (j*7);
                Log.d(TAG, "INDEX OF QUESTION START: " + indexOfQuestionStart);
                questionFieldList.add(trainingscontent.get(indexOfQuestionStart+1));
                questionFieldList.add(Integer.valueOf(trainingscontent.get(indexOfQuestionStart+2)));
                questionFieldList.add(trainingscontent.get(indexOfQuestionStart+3));
                questionFieldList.add(trainingscontent.get(indexOfQuestionStart+4));
                questionFieldList.add(trainingscontent.get(indexOfQuestionStart+5));
                questionFieldList.add(trainingscontent.get(indexOfQuestionStart+6));
                Log.d(TAG, "QUESTION FIELD LIST VALUE: " + questionField);
                trainingDoc.put(questionBlank, (List<Object>)questionFieldList);
            }
            trainingDoc.put("videoURL", videoURL);
            Log.d(TAG, "TRAINING NAMES LIST: " + trainingNames);

            //if there is a new custom training in list, add it to arraylist and generate training id
            if (!customDocIDList.contains(trainingNames.get(i)))
            {
                customDocIDList.add(trainingNames.get(i));
                trainingID = compID + "-" + customTrainingIDList.size();
                customTrainingIDList.add(trainingID);
            }

            //this is the main part of rearranging the training id's through parallel arrays
            else
            {
                trainingID = customTrainingIDList.get(customDocIDList.indexOf(trainingNames.get(i)));
            }
            Log.d(TAG, "TRAINING ID AT POSITION " + k + ": " + trainingID);
            trainingDoc.put("trainingID", trainingID);
            db.collection("companies").document(groupName).collection("CustomTrainings").document(trainingNames.get(i)).set(trainingDoc, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "SUCCESSFULLY CREATED CUSTOM TRAININGS SUBCOLLECTION");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "FAILED TO CREATE CUSTOM TRAININGS SUBCOLLECTION");
                }
            });
            Log.d(TAG, "VALUE OF I AT END OF TRAININGNAMES FOR LOOP: " + i);
            Map<String, Object> trainingIDMap = new HashMap<>();
            Log.d(TAG, "CUSTOM LIST OF DOCS AT " + k + ": " + customDocIDList);
            Log.d(TAG, "CUSTOM LIST OF TRAININGS AT " + k + ": " + customTrainingIDList);
            trainingIDMap.put("trainingID", trainingID);
            if(k+weeksBetween<=50) {
                db.collection("companies").document(groupName).collection("Trainings").document(String.valueOf(k + weeksBetween)).
                        set(trainingIDMap, SetOptions.merge());
            }
            //wait until the last document in collection to be updated, then start new intent
            if(k+weeksBetween==51) {
                db.collection("companies").document(groupName).collection("Trainings").document(String.valueOf(k + weeksBetween)).
                        set(trainingIDMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent createdView = new Intent(ManageTrainings.this, AdminSettings.class);
                        createdView.putExtra("Company ID", compID);
                        Log.d(TAG, "ABOUT TO PUSH GROUP NAME: " + groupName);
                        createdView.putExtra("Company Name", groupName);
                        createdView.putStringArrayListExtra("Trainings Content", trainingscontent);
                        createdView.putStringArrayListExtra("Custom Trainings Content", customTrainingsContent);
                        createdView.putExtra("Company Type", companyType);
                        createdView.putExtra("Training Names List", trainingNames);
                        createdView.putStringArrayListExtra("User Name List", userNameList);
                        createdView.putStringArrayListExtra("UID List", uidList);
                        createdView.putExtra("Dated Training ID", datedTrainingID);
                        createdView.putExtra("Dated Training ID", datedTrainingID);
                        createdView.putExtra("Training List Size", trainingNames.size());
                        createdView.putExtra("User First Name", userFirstName);
                        createdView.putExtra("User Last Name", userLastName);
                        createdView.putExtra("User ID", userId);
                        startActivity(createdView);
                    }
                });
            }
            i++;
            //if i is at the end of the list, start again at the beginning of the list and keep going through
            //until k reached 52
            if(i==trainingNames.size())
            {
                i=0;
            }
            k++;
            Log.d(TAG, "VALUE OF K: " + k);
        }
    }

    public void editButtonPressed(View v) { ;
        MyArrayAdapter stableAdapter = new MyArrayAdapter(this, R.layout.trainingnameslistitemseditedmanage, trainingNames, weeksBetween);
        if (editButton.getText().equals("EDIT")) {
            addButton.setVisibility(View.VISIBLE);
            editButton.setText("DONE");
            regularNamesList.setVisibility(View.INVISIBLE);
            mAdapter = new MyArrayAdapter(this, R.layout.trainingnameslistitemseditedmanage, trainingNames, weeksBetween); //rearrangeablelistviewlayout
            Log.d(TAG, "TRAINING NAMES: " + trainingNames);
            mListView.setAdapter(mAdapter);
            mListView.setVisibility(View.VISIBLE);
            Log.d(TAG, "EVENT. LISTVIEW BOTTOM: " + mListView.getBottom() + ", LISTVIEW TOP: " + mListView.getTop());
            mListView.getGlobalVisibleRect(rect);
            Log.d(TAG, "RECTANGLE TOP AND BOTTOM: " + rect.top + ", " + rect.bottom);
            mListView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch (View view, MotionEvent event){
                    if (!mSortable) {
                        return false;
                    }
                    int myViewHeight = view.getHeight();
                    firstVisiblePos=mListView.getFirstVisiblePosition();
                    lastVisiblePos= mListView.getLastVisiblePosition();
                    Log.d(TAG, "FIRST VISIBLE POSITION: " + firstVisiblePos + ", LAST VISIBLE POSITION: " + lastVisiblePos);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            int position = mListView.pointToPosition((int) event.getX(), (int) event.getY());
                            if (position < 0) {
                                break;
                            }
                            if (position != mPosition) {
                                hasScrolled=false;
                                Log.d(TAG, "TOUCH POSITION: " + position);
                                mPosition = position;
                                itemName = getItemName();
                                switchName = mAdapter.getItem(mPosition);
                                mListView.smoothScrollToPosition(mPosition);
                                Log.d(TAG, "EVENT.GETY(): " + event.getY());
                                Log.d(TAG, "MPOSITION: " + mPosition + ", PREVIOUSPOSITION: " + previousPosition);
                                if(previousPosition!=-1) {
                                    trainingNames.set(mPosition, itemName);
                                    trainingNames.set(previousPosition, switchName);
                                }

                                mAdapter.notifyDataSetChanged();
                                setPreviousPosition(mPosition);
                            }
                            if(position==firstVisiblePos && !hasScrolled)
                            {
                                //mListView.scrollTo((int)event.getX(), -(view.getHeight()+(int)event.getY()));
                                mListView.smoothScrollBy(-(myViewHeight/2), 5000);
                                hasScrolled=true;
                            }
                            if(position==lastVisiblePos && !hasScrolled)
                            {
                                //mListView.scrollTo((int)event.getX(), view.getHeight()+(int)event.getY());
                                //mListView.smoothScrollByOffset(view.getHeight());
                                mListView.smoothScrollBy((myViewHeight/2), 5000);
                                hasScrolled=true;
                            }
                            return true;
                        }
                        case MotionEvent.ACTION_UP:
                            //mListView.smoothScrollBy(50,2500);
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_OUTSIDE: {
                            // mListView.smoothScrollBy(50,2500);
                            stopDrag();
                            return true;
                        }
                    }
                    return false;
                }
            });

        } else if (editButton.getText().equals("DONE")) {
            addButton.setVisibility(View.INVISIBLE);
            editButton.setText("EDIT");
            trainingNames = stableAdapter.getUpdatedTrainingList();
            mListView.setVisibility(View.INVISIBLE);
            regularNamesList.setVisibility(View.VISIBLE);
            BaseArrayAdapter myAdapter = new BaseArrayAdapter
                    (ManageTrainings.this, R.layout.trainingnameslistitemsmanage, trainingNames, weeksBetween);
            regularNamesList.setAdapter(myAdapter);
        }
    }

    public void setPreviousPosition(int positionToSet)
    {
        previousPosition = positionToSet;
    }

    public int getPreviousPosition()
    {
        return previousPosition;
    }

    public void setItemName(String startName)
    {
        itemName = startName;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void startDrag(String string, int position) {
        if(startPosition==-1)
        {
            startPosition = position;
            setItemName(mAdapter.getItem(startPosition));
        }
        Log.d(TAG, "STARTPOSITION: " + startPosition + ", ITEM AT START POSITION: " + mAdapter.getItem(startPosition));
        mPosition = -1;
        mSortable = true;
        mDragString = string;
        mAdapter.notifyDataSetChanged();
    }

    public void stopDrag() {
        mPosition = -1;
        startPosition = -1;
        previousPosition=-1;
        firstVisiblePos=-1;
        hasScrolled = false;
        lastVisiblePos=-1;
        mSortable = false;
        mDragString = null;
        mAdapter.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView title;
        Button handle;
        Button deleteButton;
        TextView weekNumber;
    }

    public class BaseArrayAdapter extends ArrayAdapter<String>
    {
        private ArrayList<String> trainings;
        private LayoutInflater mInflater;
        private int mLayout, weeks;

        public BaseArrayAdapter(Context context, int textViewResourceId, ArrayList<String> trainingNames, int date) {
            super(context, textViewResourceId, trainingNames);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
            trainings = trainingNames;
            weeks = date;
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
                holder.weekNumber = (TextView) view.findViewById(R.id.weeksTextView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            String string = trainings.get(position);
            holder.title.setText(string);
            holder.weekNumber.setText("Week: " + (position+1+weeks));

            return view;
        }
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {

        private ArrayList<String> mStrings;
        private LayoutInflater mInflater;
        private int mLayout, weekNumber;

        public MyArrayAdapter(Context context, int textViewResourceId, ArrayList<String> trainings, int date) {
            super(context, textViewResourceId, trainings);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
            mStrings = trainings;
            weekNumber = date;
        }

        @Override
        public void add(String row) {
            super.add(row);
            mStrings.add(row);
        }

        @Override
        public void insert(String row, int position) {
            super.insert(row, position);
            mStrings.set(position, row); //add
        }

        @Override
        public void remove(String row) {
            super.remove(row);
            mStrings.remove(row);
        }

        @Override
        public void clear() {
            super.clear();
            mStrings.clear();
        }

        public ArrayList getUpdatedTrainingList()
        {
            return mStrings;
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
                    holder.handle = view.findViewById(R.id.rearrangeitemsbutton);
                    holder.deleteButton = view.findViewById(R.id.deleteTrainingButton);
                    holder.weekNumber = view.findViewById(R.id.weeksTextView);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }

                String string = mStrings.get(position);

                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trainingNames.remove(position);
                        notifyDataSetChanged();
                    }
                });
                holder.title.setText(string);
                holder.weekNumber.setText("Week: " + (position+1+weekNumber));
                holder.handle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            startDrag(string, position);
                            return true;
                        }
                        return false;
                    }
                  });

                    if(mPosition==position) {
                        view.setBackgroundColor(Color.parseColor("#9933b5e5"));}
                else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            return view;
        }
    }

    public void addTrainingButtonPressed(View v) {
        addButton.setClickable(false);
        if(trainingNames.size()==52)
        {
            Toast myToast = Toast.makeText(ManageTrainings.this, "You can only have a maximum of 52 Trainings, remove a training to add one", Toast.LENGTH_LONG);
            myToast.show();
            addButton.setClickable(true);
        }
        else {
            Intent addTrainingFirstScreen = new Intent(ManageTrainings.this, AddTrainingFirstScreen.class);
            addTrainingFirstScreen.putExtra("Company ID", compID);
            addTrainingFirstScreen.putExtra("Group Name", groupName);
            addTrainingFirstScreen.putExtra("Company Type", companyType);
            addTrainingFirstScreen.putExtra("First Name", userFirstName);
            addTrainingFirstScreen.putExtra("From Manage Trainings", true);
            addTrainingFirstScreen.putExtra("ingroup", ingroup);
            addTrainingFirstScreen.putExtra("Dated Training ID", datedTrainingID);
            addTrainingFirstScreen.putExtra("User ID", userId);
            addTrainingFirstScreen.putStringArrayListExtra("User Name List", userNameList);
            addTrainingFirstScreen.putStringArrayListExtra("UID List", uidList);
            addTrainingFirstScreen.putStringArrayListExtra("Custom Training ID List", customTrainingIDList);
            addTrainingFirstScreen.putStringArrayListExtra("Custom Doc ID List", customDocIDList);
            addTrainingFirstScreen.putExtra("Last Name", userLastName);
            addTrainingFirstScreen.putExtra("Training Names List", trainingNames);
            addTrainingFirstScreen.putExtra("Custom Trainings Content", customTrainingsContent);
            startActivity(addTrainingFirstScreen);
        }
    }

    public void backToAdminSettingsClicked(View v) {
        backButton.setClickable(false);
        Intent groupType = new Intent(ManageTrainings.this, AdminSettings.class);
        groupType.putExtra("Company ID", compID);
        groupType.putExtra("Company Name", groupName);
        groupType.putExtra("User First Name", userFirstName);
        groupType.putExtra("User Last Name", userLastName);
        groupType.putStringArrayListExtra("User Name List", userNameList);
        groupType.putStringArrayListExtra("UID List", uidList);
        groupType.putExtra("Dated Training ID", datedTrainingID);
        groupType.putExtra("ingroup", ingroup);
        groupType.putExtra("User ID", userId);
        startActivity(groupType);
    }
}
