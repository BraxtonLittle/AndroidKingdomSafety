package com.something.KingdomSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
private static final String TAG = "SignUpActivity";
    private EditText useremailEditText, userpasswordEditText,
            confirmPasswordEditText, firstNameEditText, lastNameEditText;
    private String userEmail, userPassword, userFirstName,
            userLastName, confirmPassword, docName, userID;
    private FirebaseAuth mAuth;
    private LottieAnimationView signupAnimation;
    private VideoView signupVV;
    private int myVidPosition, pushedRandNum;
    private Uri myUri;
    MediaPlayer myPlayer;
    private Button signUpButton, haveAccount, homeButton;
    private String[] videosArray;
    private FirebaseFirestore db;
    private DocumentReference userDocReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        useremailEditText = findViewById(R.id.signupemail);
        signupAnimation = findViewById(R.id.signuplottieanimationview);
        userpasswordEditText = findViewById(R.id.signuppassword);
        homeButton = findViewById(R.id.homebutton);
        signUpButton = findViewById(R.id.signUpButton);
        haveAccount = findViewById(R.id.alreadyhaveaccount);
        firstNameEditText = findViewById(R.id.firstname);
        lastNameEditText = findViewById(R.id.lastname);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "The onCreate method works");
        signupVV = findViewById(R.id.signupVideoView);
        videosArray = new String[5];
        videosArray[0] = "android.resource://" + getPackageName() + "/" + R.raw.blurredjackhammervideo;
        videosArray[1] = "android.resource://" + getPackageName() + "/" + R.raw.blurredconstructiontimelapse;
        videosArray[2] = "android.resource://" + getPackageName() + "/" + R.raw.blurredlayingbricks;
        videosArray[3] = "android.resource://" + getPackageName() + "/" + R.raw.blurredcranetimelapse;
        videosArray[4] = "android.resource://" + getPackageName() + "/" + R.raw.blurredcuttingwood;
        db = FirebaseFirestore.getInstance();
    }

    protected void onStart()
    {
        super.onStart();
        signupAnimation.setVisibility(View.INVISIBLE);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            pushedRandNum = myBundle.getInt("RandNum");
            myVidPosition = myBundle.getInt("vidTime") + 1750;
        }
        myUri = Uri.parse(videosArray[pushedRandNum]);
        signupVV.setVideoURI(myUri);
        signupVV.start();
        signupVV.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                myPlayer = mediaPlayer;
                myPlayer.seekTo(myVidPosition);
                myPlayer.setLooping(true);
                if(myVidPosition !=0)
                {
                    myPlayer.seekTo(myVidPosition);
                    myPlayer.start();
                }
            }
        }
        );
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        myVidPosition = myPlayer.getCurrentPosition();
        signupVV.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        signupVV.start();
    }

    public void backtoHome(View v)
    {
        homeButton.setClickable(false);
        myVidPosition = myPlayer.getCurrentPosition();
        Intent homeScreen = new Intent(SignUpActivity.this, LoginSignup.class);
        homeScreen.putExtra("RandNum", pushedRandNum);
        homeScreen.putExtra("vidTime", myVidPosition);
        startActivity(homeScreen);
    }

    public void signuptologin(View v)
    {
        haveAccount.setClickable(false);
        myVidPosition = myPlayer.getCurrentPosition();
        Intent backtoLogin = new Intent(SignUpActivity.this, LoginActivity.class);
        backtoLogin.putExtra("RandNum", pushedRandNum);
        backtoLogin.putExtra("vidTime", myVidPosition);
        startActivity(backtoLogin);
    }

    public void signupnewuser(View v)
    {
        signUpButton.setClickable(false);
        userEmail = useremailEditText.getText().toString();
        userPassword = userpasswordEditText.getText().toString();
        userFirstName = firstNameEditText.getText().toString();
        userLastName = lastNameEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();
        docName = userLastName + "." + userFirstName;
        if(!(userEmail.equals("") && userPassword.equals("") && userFirstName.equals("") && userLastName.equals(""))) {
            if(userPassword.equals(confirmPassword)) {
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    signupAnimation.setVisibility(View.VISIBLE);
                                    signupAnimation.playAnimation();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userID = user.getUid();
                                    Map<String, Object> userInfoHash = new HashMap<>();
                                    userInfoHash.put("uid", userID);
                                    userInfoHash.put("email", userEmail);
                                    userInfoHash.put("firstname", userFirstName);
                                    userInfoHash.put("lastname", userLastName);
                                    db.collection("users").document(docName)
                                            .set(userInfoHash)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    userDocReference = db.collection("users").document(docName);
                                                    if(mAuth.getCurrentUser()!=null) {
                                                        userDocReference.update
                                                                ("uid", mAuth.getCurrentUser().getUid())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        signUpButton.setClickable(true);
                                                                        Log.w(TAG, "Error updating document", e);
                                                                    }
                                                                });

                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(SignUpActivity.this,
                                                                "User could not be found", Toast.LENGTH_SHORT).show();
                                                        signUpButton.setClickable(true);
                                                    }
                                                    Intent intoApp = new Intent(SignUpActivity.this, JoinStartGroup.class);
                                                    intoApp.putExtra("First Name", userFirstName);
                                                    intoApp.putExtra("Last Name", userLastName);
                                                    intoApp.putExtra("User ID", userID);
                                                    startActivity(intoApp);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    signUpButton.setClickable(true);
                                                    Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                }
                                else {
                                    signUpButton.setClickable(true);
                                    // If sign in fails, display a message to the user.
                                    if(task.getException().toString().contains("The given password is invalid"))
                                    {
                                        Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
                                    }
                                    else if(task.getException().toString().contains("email address is already in use"))
                                    {
                                        Toast.makeText(SignUpActivity.this, "Oops! This email address is already in use by another account ", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "EXCEPTION" + task.getException());
                                    }
                                    else
                                    {
                                        Toast.makeText(SignUpActivity.this, "Oops! Failed signing up user", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "EXCEPTION" + task.getException());
                                    }
                                }
                            }
                        });
            }
            else
            {
                signUpButton.setClickable(true);
                Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            signUpButton.setClickable(true);
            if(userFirstName.equals(""))
            {
                Toast.makeText(SignUpActivity.this, "Please enter a valid First Name", Toast.LENGTH_SHORT).show();
            }
            else if(userLastName.equals(""))
            {
                Toast.makeText(SignUpActivity.this, "Please enter a valid Last Name", Toast.LENGTH_SHORT).show();
            }
            else if(userEmail.equals(""))
            {
                Toast.makeText(SignUpActivity.this, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
            }
            else if(userPassword.equals(""))
            {
                Toast.makeText(SignUpActivity.this, "Please enter a valid Password", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
