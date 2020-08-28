package com.something.KingdomSafety;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchVideo extends YouTubeBaseActivity {
private final static String TAG = "WATCHVIDEO";
private ImageView whiteBackground;
private int correctAnswerIndex, youtubeVidDuration, youtubeCurrentTime;
private String userFirstName, userLastName, companyName, userID, queriedTrainingID, stringvaluevar, finalQuestionField;
private String questionString, answer1, answer2, answer3, answer4, datedTrainingID, queriedVideoURL, dateStarted, formattedDate;
private List<Object> userInfo, questionList, userTrainingList;
private ArrayList<Object> userTrainingInfo;
private ArrayList receievedList;
private Button takeTestScreenButton, backToUserScreenButton;
private long numOfQuestions;
private YouTubePlayerView youtubeView;
private FirebaseFirestore db;
private Map<String, Object> userDoc, userTrainingDoc;
private ArrayList<String> quizzes;
Button playVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        takeTestScreenButton = findViewById(R.id.gototaketestbutton);
        backToUserScreenButton = findViewById(R.id.homebutton);
        whiteBackground = findViewById(R.id.whitebehindvidbackground);
        whiteBackground.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        quizzes = new ArrayList<>();
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            userFirstName = myBundle.getString("First Name");
            userLastName = myBundle.getString("Last Name");
            companyName = myBundle.getString("Company Name");
            queriedVideoURL = myBundle.getString("Queried Video URL");
            datedTrainingID = myBundle.getString("Dated Training ID");
            dateStarted = myBundle.getString("Date Started");
            formattedDate = myBundle.getString("Today's Date");
            Log.d(TAG, "DATED TRAINING ID: " + datedTrainingID);
            userID = myBundle.getString("User ID");
            receievedList = myBundle.getParcelableArrayList("User Info List");
        }
        userInfo = (List<Object>) receievedList;
        youtubeView = findViewById(R.id.myYoutubeView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    private void initializePlayer()
    {
        takeTestScreenButton.setClickable(false);
        backToUserScreenButton.setClickable(false);
       youtubeView.initialize("AIzaSyAz3u0NhoFUmNCdjpFlSpA1kkjeskZDvf4",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(queriedVideoURL.substring(queriedVideoURL.lastIndexOf("/") + 1));
                        youtubeVidDuration = youTubePlayer.getDurationMillis();//Need to figure out a way to
                        youtubeCurrentTime = youTubePlayer.getCurrentTimeMillis();
                        youTubePlayer.play();
                        onVideoStarted();
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                    }
                });
    }

    public void onVideoStarted()
    {
        userTrainingInfo = new ArrayList<>();
        Log.d(TAG, "VIDEO HAS STARTED!!!");
        takeTestScreenButton.setVisibility(View.VISIBLE);
        userDoc = new HashMap<>();
        userInfo.set(2, true);
        userDoc.put(userID, userInfo);
        Log.d(TAG, "USER INFO MAP: " + userDoc.get(userID));
        db.collection("companies").document(companyName).set(userDoc, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO UPDATE USER INFO ARRAY WITHIN COMPANY DOC");
            }
        });
        userTrainingDoc = new HashMap<>();
        db.collection("companies").document(companyName).collection("Trainings").document(datedTrainingID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult()!=null)
                {
                    userTrainingList = (List<Object>) task.getResult().get(userID);
                    if(userTrainingList==null)
                    {
                        userTrainingInfo.add( true);
                        userTrainingInfo.add(formattedDate);
                        userTrainingInfo.add(false);
                        userTrainingInfo.add(null);
                    }
                    else
                    {
                        userTrainingInfo = (ArrayList<Object>) userTrainingList;
                        userTrainingInfo.set(1, formattedDate);
                    }
                    userTrainingDoc.put(userID, userTrainingInfo);
                    db.collection("companies").document(companyName).collection("Trainings").document(datedTrainingID)
                            .set(userTrainingDoc, SetOptions.merge());
                }
                backToUserScreenButton.setClickable(true);
                takeTestScreenButton.setClickable(true);
            }
        });
    }

    public void gotoTakeTest(View v)
    {
        takeTestScreenButton.setClickable(false);
        quizzes.clear();
        db.collection("companies").document(companyName).collection("Trainings").document(datedTrainingID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        queriedTrainingID = (String) document.get("trainingID");

                        Log.d(TAG, "QUERIED TRAINING ID: " + queriedTrainingID);
                        Log.d(TAG, "Company Name" + companyName);
                        db.collection("companies").document(companyName).collection("CustomTrainings").whereEqualTo("trainingID", queriedTrainingID).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                    {
                                        for(DocumentSnapshot myDocument:queryDocumentSnapshots)
                                        {
                                            Log.d(TAG, "DOCUMENT ID: " + myDocument.getId());
                                            numOfQuestions = (Long)myDocument.get("numberOfQuestions");
                                            for(int i = 0; i<numOfQuestions; i++) {
                                                stringvaluevar = String.valueOf(i);
                                                finalQuestionField = "question" + stringvaluevar;
                                                questionList = (List<Object>) myDocument.get(finalQuestionField);
                                                questionString = (String) questionList.get(0);
                                                answer1 = (String) questionList.get(2);
                                                answer2 = (String) questionList.get(3);
                                                answer3 = (String) questionList.get(4);
                                                answer4 = (String) questionList.get(5);
                                                quizzes.add(finalQuestionField);
                                                quizzes.add(questionString);
                                                quizzes.add(String.valueOf(questionList.get(1)));
                                                quizzes.add(answer1);
                                                quizzes.add(answer2);
                                                quizzes.add(answer3);
                                                quizzes.add(answer4);
                                            }
                                        }
                                        Intent toTest = new Intent(WatchVideo.this, TakeTest.class);
                                        toTest.putExtra("Company Name", companyName);
                                        toTest.putExtra("User ID", userID);
                                        toTest.putExtra("User First Name", userFirstName);
                                        toTest.putExtra("User Last Name", userLastName);
                                        toTest.putExtra("Dated Training ID", datedTrainingID);
                                        toTest.putExtra("Today's Date", formattedDate);
                                        toTest.putExtra("Number of Questions", numOfQuestions);
                                        toTest.putExtra("Date Started", dateStarted);
                                        toTest.putStringArrayListExtra("Quiz Arraylist", quizzes);
                                        Log.d(TAG, "QUIZ ARRAYLIST: " + quizzes);
                                        toTest.putParcelableArrayListExtra("User Info List", receievedList);
                                        startActivity(toTest);
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                takeTestScreenButton.setClickable(true);
                                Log.d(TAG, "Failed to get referenced document in quizes collection");
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        takeTestScreenButton.setClickable(true);
                        Log.d(TAG, "FAILED TO QUERY SUBCOLLECTION TRAININGS");
                    }
                });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        quizzes.clear();
    }

    public void backtoUserScreen(View v)
    {
        Intent userScreen = new Intent(WatchVideo.this, UsersScreen.class);
        userScreen.putExtra("Watched Video", true); // do a db query instead of assuming this is true
        userScreen.putExtra("User First Name", userFirstName);
        userScreen.putExtra("User Last Name", userLastName);
        userScreen.putExtra("Company Name", companyName);
        userScreen.putExtra("User ID", userID);
        userScreen.putExtra("Date Started", dateStarted);
        userScreen.putExtra("Dated Training ID", datedTrainingID);
        userScreen.putExtra("User Info List", receievedList);
        startActivity(userScreen);
    }
}
