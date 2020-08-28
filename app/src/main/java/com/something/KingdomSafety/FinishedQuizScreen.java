package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinishedQuizScreen extends AppCompatActivity {
private TextView scoreTextView;
private final static String TAG = "FINISHEDQUIZSCREEN";
private EditText passwordEditText;
private ArrayList receivedList;
private Button confirmPassword, confirmButton, backToHomeButton;
private List<Object> userInfo, userTrainingList;
private ArrayList<Object> userTrainingInfo;
private String companyName, userID, enteredPassword, userEmail, dateStarted, datedTrainingID, userFirstName, userLastName, formattedDate;
private Map<String, Object> userDoc, userTrainingDoc;
private FirebaseFirestore db;
private int numOfQuestions, correctAnswers, score;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_quiz_screen);
        scoreTextView = findViewById(R.id.scoremessage);
        passwordEditText = findViewById(R.id.passwordedittext);
        confirmPassword = findViewById(R.id.confirmpasswordbutton);
        confirmButton = findViewById(R.id.confirmbutton);
        backToHomeButton = findViewById(R.id.backtohomebutton);
        db = FirebaseFirestore.getInstance();
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            userFirstName = myBundle.getString("User First Name");
            userLastName = myBundle.getString("User Last Name");
            correctAnswers = myBundle.getInt("Correct Answers");
            numOfQuestions = myBundle.getInt("Number of Questions");
            companyName = myBundle.getString("Company Name");
            userID = myBundle.getString("User ID");
            formattedDate = myBundle.getString("Today's Date");
            Log.d(TAG, "FORMATTED DATE IN FINISHEDQUIZSCREEN: " + formattedDate);
            datedTrainingID = myBundle.getString("Dated Training ID");
            dateStarted = myBundle.getString("Date Started");
            receivedList = myBundle.getParcelableArrayList("User Info List");
        }
        Log.d(TAG, "RECIEVED USER INFO LIST: " + receivedList);
        userInfo = (List<Object>) receivedList;
        userInfo.set(3, true);
        score = (correctAnswers/numOfQuestions) * 100;
        userInfo.set(5, score);
        mAuth = FirebaseAuth.getInstance();
        userEmail = mAuth.getCurrentUser().getEmail();
        db.collection("companies").document(companyName).update(userID, userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "SUCCESSFULLY UPDATED USER INFO WITH TAKEN TEST");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO UPDATE USER INFO WITH TAKEN TEST");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        scoreTextView.setText("Your Score is : " + correctAnswers + " / " + numOfQuestions);
        passwordEditText.setVisibility(View.INVISIBLE);
        confirmPassword.setVisibility(View.INVISIBLE);
    }

    public void confirmpressed(View v)
    {
        passwordEditText.setVisibility(View.VISIBLE);
        confirmPassword.setVisibility(View.VISIBLE);
        confirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPassword.setClickable(false);
                confirmButton.setClickable(false);
                backToHomeButton.setClickable(false);
                enteredPassword = passwordEditText.getText().toString();
                if(enteredPassword=="" || enteredPassword==null || enteredPassword.isEmpty())
                {
                    Toast toast = Toast.makeText(FinishedQuizScreen.this, "Please enter a valid Password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    confirmPassword.setClickable(true);
                    confirmButton.setClickable(true);
                    backToHomeButton.setClickable(true);
                }
                else {
                    userTrainingDoc = new HashMap<>();
                    mAuth.signInWithEmailAndPassword(userEmail, enteredPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if ((float)correctAnswers/(float)numOfQuestions>=.70) {
                                userDoc = new HashMap<>();
                                userInfo.set(4, true);
                                userDoc.put(userID, userInfo);
                                db.collection("companies").document(companyName).set(userDoc, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                db.collection("companies").document(companyName).collection("Trainings").document(datedTrainingID)
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.getResult()!=null)
                                                        {
                                                            userTrainingList = (List<Object>) task.getResult().get(userID);
                                                            if(userTrainingList!=null)
                                                            {
                                                                userTrainingInfo = (ArrayList<Object>) userTrainingList;
                                                                userTrainingInfo.set(2, true);
                                                                userTrainingInfo.set(3, formattedDate);
                                                            }
                                                            userTrainingDoc.put(userID, userTrainingInfo);
                                                            db.collection("companies").document(companyName).collection("Trainings").document(datedTrainingID)
                                                                    .set(userTrainingDoc, SetOptions.merge());
                                                        }
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        confirmPassword.setClickable(true);
                                        confirmButton.setClickable(true);
                                        backToHomeButton.setClickable(true);
                                        Log.d(TAG, "FAILED TO UPDATE USER INFO LIST WITH PASSED TEST IN COMPANY COLLECTION");
                                    }
                                });
                            }
                            else
                            {
                                userDoc = new HashMap<>();
                                userInfo.set(4, false);
                                userDoc.put(userID, userInfo);
                                db.collection("companies").document(companyName).set(userDoc, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "SUCCESSFULLY UPDATED USER INFO LIST IN COMPANY COLLECTION");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        confirmPassword.setClickable(true);
                                        Log.d(TAG, "FAILED TO UPDATE USER INFO LIST WITH PASSED TEST IN COMPANY COLLECTION");
                                    }
                                });
                            }
                            passwordEditText.setVisibility(View.INVISIBLE);
                            confirmPassword.setVisibility(View.INVISIBLE);
                            confirmButton.setVisibility(View.INVISIBLE);
                            Toast toast = Toast.makeText(FinishedQuizScreen.this, "Results Successfully Confirmed!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            confirmPassword.setClickable(true);
                            confirmButton.setClickable(true);
                            backToHomeButton.setClickable(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toast.makeText(FinishedQuizScreen.this, "Incorrect Password", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            confirmPassword.setClickable(true);
                            confirmButton.setClickable(true);
                            backToHomeButton.setClickable(true);
                        }
                    });
                }
            }
        });
    }

    public void backhomepressed(View v)
    {
        backToHomeButton.setClickable(false);
        confirmPassword.setClickable(false);
        confirmButton.setClickable(false);
        Intent userScreen = new Intent(FinishedQuizScreen.this, UsersScreen.class);
        userScreen.putExtra("Company Name", companyName);
        userScreen.putExtra("User ID", userID);
        userScreen.putExtra("Dated Training ID", datedTrainingID);
        userScreen.putExtra("User First Name", userFirstName);
        userScreen.putExtra("User Last Name", userLastName);
        Log.d(TAG, "USER INFO LIST ABOUT TO BE SENT TO USER SCREEN: " + receivedList);
        userScreen.putParcelableArrayListExtra("User Info List", receivedList);
        userScreen.putExtra("Taken Test", true);
        userScreen.putExtra("Date Started", dateStarted);
        Log.d(TAG, "Correct answers: " + correctAnswers);
        startActivity(userScreen);
    }
}