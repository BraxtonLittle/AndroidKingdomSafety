package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewTrainings extends AppCompatActivity {
    private String compID, groupName, companyType, documentID, userFirstName, userLastName, questionField, cellText;
    private ArrayList<String> trainingNames, customTrainingsContent, trainingscontent, previewTrainingsContent, questionSet;
    private FirebaseFirestore db;
    private boolean ingroup;
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
    private String mDragString, customQuestionNumString, userId, itemName, switchName;
    private long numberOfQuestions;
    private int customNumOfQuestions, previewUsableQuestionNum, questionBoxDynamicHeight;
    private int mPosition = -1;
    private int firstVisiblePos=-1;
    private boolean hasScrolled = false;
    private int lastVisiblePos=-1;
    private int previousPosition=-1;
    private int startPosition=-1;
    private int customTextViewID, customTrainingStart, previousBoxID, manageReview;
    private final String TAG = "REVIEWTRAININGS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_trainings);
        addButton = findViewById(R.id.addButton);
        addButton.setVisibility(View.INVISIBLE);
        nextButton = findViewById(R.id.nextButton);
        trainingListContainer = findViewById(R.id.trainingListContainer);
        backButton = findViewById(R.id.backtoGroupType);
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
        trainingscontent = new ArrayList();
        customTrainingsContent = new ArrayList();
        previewTrainingsContent = new ArrayList();
        Bundle myBundle = getIntent().getExtras();
        if (myBundle != null) {
            compID = myBundle.getString("Company ID");
            groupName = myBundle.getString("Group Name");
            companyType = myBundle.getString("Company Type");
            ingroup = myBundle.getBoolean("ingroup");
            userId = myBundle.getString("User ID");
            Log.d(TAG, "REVIEW TRAININGS USER ID: " + userId);
            //manageReview = myBundle.getInt("Manage Review");
            Log.d(TAG, "COMPANY TYPE: " +companyType);
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            customTrainingsContent = myBundle.getStringArrayList("Custom Trainings Content");
            trainingNames = myBundle.getStringArrayList("Training Names List");
        }
        Log.d(TAG, "TRAINING NAMES LIST: " + trainingNames);
        Log.d(TAG, "CUSTOM TRAININGS CONTENT IN CREATE METHOD: " + customTrainingsContent);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListView.setVisibility(View.INVISIBLE);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.trainingnameslistitems, trainingNames);
        regularNamesList.setAdapter(adapter);
        //if(!companyType.equalsIgnoreCase("Custom")) {
        Log.d(TAG, "TRAININGS CONTENT IN ONSTART: " + trainingscontent);
        Log.d(TAG, "CUSTOM TRAININGS CONTENT IN ONSTART: " + customTrainingsContent);
        Log.d(TAG, "TRAININGS CONTENT NAME: " + trainingNames);
            db.collection(companyType).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().size()>0) {
                        Log.d(TAG, "TASK.GETRESULT() SIZE: " + task.getResult().size());
                        for (DocumentSnapshot doc : task.getResult()) {
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
                                    Log.d(TAG, "ELSE EVENT INITIATED");
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
                    else
                    {
                        for(int c = 0; c<trainingNames.size(); c++){
                        if (customTrainingsContent != null) {
                            if (customTrainingsContent.contains(trainingNames.get(c))) {
                                Log.d(TAG, "THERE IS A CUSTOM TRAINING WITHIN THE TRAINING NAMES LIST");
                                customTrainingStart = customTrainingsContent.indexOf(trainingNames.get(c));
                                if (!trainingscontent.contains(customTrainingsContent.get(customTrainingStart + 1))) {
                                    trainingscontent.add(trainingNames.get(c));
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
        regularNamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemView = (TextView) view;
                questionSet = new ArrayList<>();
                cellText = listItemView.getText().toString();
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
                MyAdapter previewAdapter = new MyAdapter(ReviewTrainings.this, R.layout.preview_training_question_box, questionSet);
                previewAdapter.setTrainingsContent(trainingscontent);
                previewTrainingsListView.setAdapter(previewAdapter);
                previewTrainingsListView.setVisibility(View.VISIBLE);
            }
                else
                {
                    Toast.makeText(ReviewTrainings.this, "This Training doesn't have any questions to preview", Toast.LENGTH_SHORT).show();
                }
        }
        });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "FAILED TO DOWNLOAD TRAININGS FROM FIREBASE AND ADD THEM TO TRAININGS CONTENT ARRAYLIST");
                }
            });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(editButton.getText()=="DONE")
        {
            mAdapter = new MyArrayAdapter(this, R.layout.trainingnameslistitemsedited, trainingNames);
            mListView.setAdapter(mAdapter);
            mListView.setVisibility(View.VISIBLE);
        }
        else if(editButton.getText()=="EDIT");
        {
            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.trainingnameslistitems, trainingNames);
            regularNamesList.setAdapter(adapter);
        }
    }

    public void nextButtonPressed(View v) {
        nextButton.setClickable(false);
        addButton.setClickable(false);
        editButton.setClickable(false);
        backButton.setClickable(false);
        cancelWindowButton.setClickable(false);
        Intent createdView = new Intent(ReviewTrainings.this, GroupCreatedView.class);
        createdView.putExtra("Company ID", compID);
        createdView.putExtra("Group Name", groupName);
        createdView.putStringArrayListExtra("Trainings Content", trainingscontent);
        createdView.putStringArrayListExtra("Custom Trainings Content", customTrainingsContent);
        createdView.putExtra("Company Type", companyType);
        createdView.putExtra("Training Names List", trainingNames);
        createdView.putExtra("Training List Size", trainingNames.size());
        createdView.putExtra("First Name", userFirstName);
        createdView.putExtra("Last Name", userLastName);
        createdView.putExtra("User ID", userId);
        startActivity(createdView);
    }

    public void editButtonPressed(View v) {
        if (editButton.getText().equals("EDIT")) {
            addButton.setVisibility(View.VISIBLE);
            editButton.setText("DONE");
            regularNamesList.setVisibility(View.INVISIBLE);
            mAdapter = new MyArrayAdapter(this, R.layout.trainingnameslistitemsedited, trainingNames); //rearrangeablelistviewlayout
            Log.d(TAG, "TRAINING NAMES: " + trainingNames);
            mListView.setAdapter(mAdapter);
            mListView.setVisibility(View.VISIBLE);
            mListView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch (View view, MotionEvent event){
                    if (!mSortable) {
                        return false;
                    }
                    int myViewHeight = view.getHeight();
                    firstVisiblePos = mListView.getFirstVisiblePosition();
                    lastVisiblePos = mListView.getLastVisiblePosition();
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
                                mPosition = position;
                                itemName = getItemName();
                                switchName = mAdapter.getItem(mPosition);
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
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_OUTSIDE: {
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
            trainingNames = mAdapter.getUpdatedTrainingList();
            mListView.setVisibility(View.INVISIBLE);
            regularNamesList.setVisibility(View.VISIBLE);
            ArrayAdapter myAdapter = new ArrayAdapter
                    (ReviewTrainings.this, R.layout.trainingnameslistitems, trainingNames);
            regularNamesList.setAdapter(myAdapter);
        }
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
        mPosition = -1;
        mSortable = true;
        mDragString = string;
        mAdapter.notifyDataSetChanged();
        if(startPosition==-1)
        {
            startPosition = position;
            setItemName(mAdapter.getItem(startPosition));
        }
    }

    public void stopDrag() {
        mPosition = -1;
        mSortable = false;
        firstVisiblePos=-1;
        lastVisiblePos=-1;
        previousPosition=-1;
        startPosition=-1;
        mDragString = null;
        mAdapter.notifyDataSetChanged();
    }

    public void setPreviousPosition(int positionToSet)
    {
        previousPosition = positionToSet;
    }

    public int getPreviousPosition()
    {
        return previousPosition;
    }

    static class ViewHolder {
        TextView title;
        Button handle;
        Button deleteButton;
        TextView questionTitle, ans1, ans2, ans3, ans4;
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {

        private ArrayList<String> mStrings = new ArrayList<String>();
        private LayoutInflater mInflater;
        private int mLayout;

        public MyArrayAdapter(Context context, int textViewResourceId, ArrayList<String> trainings) {
            super(context, textViewResourceId, trainings);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
            mStrings = trainings;
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


            holder.handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        startDrag(string, position);
                        return true;
                    }
                    return  false;
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
        Intent addTrainingFirstScreen = new Intent(ReviewTrainings.this, AddTrainingFirstScreen.class);
        addTrainingFirstScreen.putExtra("Company ID", compID);
        addTrainingFirstScreen.putExtra("Group Name", groupName);
        addTrainingFirstScreen.putExtra("Company Type", companyType);
        addTrainingFirstScreen.putExtra("First Name", userFirstName);
        addTrainingFirstScreen.putExtra("Manage Review Intent", 1);
        addTrainingFirstScreen.putExtra("ingroup", ingroup);
        addTrainingFirstScreen.putExtra("User ID", userId);
        addTrainingFirstScreen.putExtra("Last Name", userLastName);
        addTrainingFirstScreen.putExtra("Training Names List", trainingNames);
        addTrainingFirstScreen.putExtra("Custom Trainings Content", customTrainingsContent);
        startActivity(addTrainingFirstScreen);
    }

    public void backtoGroupTypeClicked(View v) {
        backButton.setClickable(false);
        Intent groupType = new Intent(ReviewTrainings.this, GroupCategories.class);
        groupType.putExtra("Company ID", compID);
        groupType.putExtra("Group Name", groupName);
        groupType.putExtra("First Name", userFirstName);
        groupType.putExtra("Last Name", userLastName);
        groupType.putExtra("ingroup", ingroup);
        groupType.putExtra("User ID", userId);
        startActivity(groupType);
    }
}
