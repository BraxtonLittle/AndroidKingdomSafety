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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
private EditText useremailEditText, userpasswordEditText;
private String useremail, userpassword, docName, companyID, userFirstName, userLastName, companyName;
private Boolean ingroup;
private LottieAnimationView loginAnimation;
private final static String TAG = "LOGINACTIVITY";
private FirebaseAuth mAuth;
private FirebaseFirestore db;
    private VideoView loginVV;
    private String[] videosArray;
    private int myVidPosition, pushedRandNum, userlastscore;
    private Uri myUri;
    private MediaPlayer myPlayer;
    private List<Object> userInfo;
    private Button loginButton, donthaveaccountButton, homeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        useremailEditText = findViewById(R.id.loginemail);
        userpasswordEditText = findViewById(R.id.signuppassword);
        loginAnimation = findViewById(R.id.loginlottieanimationview);
        loginButton = findViewById(R.id.loginbutton);
        donthaveaccountButton = findViewById(R.id.donthaveaccount);
        homeButton = findViewById(R.id.homebutton);
        useremailEditText.setVisibility(View.VISIBLE);
        loginAnimation.setVisibility(View.INVISIBLE);
        userpasswordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        donthaveaccountButton.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "The onCreate method works");
        loginVV = findViewById(R.id.loginVideoView);
        videosArray = new String[5];
        videosArray[0] = "android.resource://" + getPackageName() + "/" + R.raw.blurredjackhammervideo;
        videosArray[1] = "android.resource://" + getPackageName() + "/" + R.raw.blurredconstructiontimelapse;
        videosArray[2] = "android.resource://" + getPackageName() + "/" + R.raw.blurredlayingbricks;
        videosArray[3] = "android.resource://" + getPackageName() + "/" + R.raw.blurredcranetimelapse;
        videosArray[4] = "android.resource://" + getPackageName() + "/" + R.raw.blurredcuttingwood;
    }
    @Override
    public void onStart() {
        super.onStart();
        loginAnimation.setVisibility(View.INVISIBLE);
        useremailEditText.setVisibility(View.VISIBLE);
        userpasswordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        donthaveaccountButton.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);
        if(mAuth.getCurrentUser()!=null)
        {
            useremailEditText.setText(mAuth.getCurrentUser().getEmail());
        }
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            pushedRandNum = myBundle.getInt("RandNum");
            myVidPosition = myBundle.getInt("vidTime") + 1500;
            Log.d("RECIEVED VID TIME:", "LOGIN VID POS: " + myVidPosition);
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        Log.d(TAG, "Firebase user has been initialized");

        myUri = Uri.parse(videosArray[pushedRandNum]);
        loginVV.setVideoURI(myUri);
        loginVV.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                myPlayer = mediaPlayer;
                if(myVidPosition!=0) {
                    if(myPlayer.getDuration()>myVidPosition) {
                        myPlayer.seekTo(myVidPosition);
                    }
                    Log.d("PLAYER SEEKING", "SEEKING TO: " + myVidPosition);
                    myPlayer.start();
                    myPlayer.setLooping(true);
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
        loginVV.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loginVV.seekTo(myVidPosition);
        loginVV.start();
        useremailEditText.setVisibility(View.VISIBLE);
        loginAnimation.setVisibility(View.INVISIBLE);
        userpasswordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        donthaveaccountButton.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);

    }

    public void logintosignup(View v)
    {
        donthaveaccountButton.setClickable(false);
        homeButton.setClickable(false);
        loginButton.setClickable(false);
        myVidPosition = myPlayer.getCurrentPosition();
        Intent signupScreen = new Intent(LoginActivity.this, SignUpActivity.class);
        signupScreen.putExtra("RandNum", pushedRandNum);
        signupScreen.putExtra("vidTime", myVidPosition);
        startActivity(signupScreen);
    }

    public void backtoHome(View v)
    {
        donthaveaccountButton.setClickable(false);
        homeButton.setClickable(false);
        loginButton.setClickable(false);
        myVidPosition = myPlayer.getCurrentPosition();
        Intent homeScreen = new Intent(LoginActivity.this, LoginSignup.class);
        homeScreen.putExtra("RandNum", pushedRandNum);
        homeScreen.putExtra("vidTime", myVidPosition);
        homeScreen.putExtra("autosignin", false);
        startActivity(homeScreen);
    }

    public void loginuser(View v)
    {
        donthaveaccountButton.setClickable(false);
        homeButton.setClickable(false);
        loginButton.setClickable(false);
        useremail = useremailEditText.getText().toString();
        userpassword = userpasswordEditText.getText().toString();
        if(!useremail.equals("") && !userpassword.equals("")) {
            useremailEditText.setVisibility(View.INVISIBLE);
            userpasswordEditText.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            donthaveaccountButton.setVisibility(View.INVISIBLE);
            homeButton.setVisibility(View.INVISIBLE);
            loginAnimation.setVisibility(View.VISIBLE);
            loginAnimation.playAnimation();
            mAuth.signInWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                docName = mAuth.getCurrentUser().getUid();
                                // Sign in success, update UI with the signed-in user's information
                                db.collection("users")
                                        .whereEqualTo("uid", docName)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "UID HAS BEEN FOUND");
                                                    if(task.getResult()!=null) {
                                                        Log.d(TAG, "TASK RESULT WASN'T NULL");
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                userFirstName = document.getString("firstname");
                                                                userLastName = document.getString("lastname");
                                                                db.collection("companies").whereArrayContains(docName, docName)
                                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "Company query was successful");
                                                                            if (task.getResult()!=null && task.getResult().size()>0) {
                                                                                Log.d(TAG, "Company query result wasn't null, should start for loop");
                                                                                for (DocumentSnapshot document : task.getResult()) {
                                                                                    companyName = document.getId();
                                                                                    companyID = document.getString("Company ID");
                                                                                    Log.d(TAG, "Company name of user: " + companyName);
                                                                                }
                                                                                Intent adminScreen = new Intent(LoginActivity.this, SelectGroup.class);
                                                                                adminScreen.putExtra("Company Name", companyName);
                                                                                adminScreen.putExtra("Company ID", companyID);
                                                                                adminScreen.putExtra("First Name", userFirstName);
                                                                                adminScreen.putExtra("Last Name", userLastName);
                                                                                adminScreen.putExtra("User ID", docName);
                                                                                adminScreen.putExtra("RandNum", pushedRandNum);
                                                                                Log.d(TAG, "ACTIVITY STARTED");
                                                                                startActivity(adminScreen);
                                                                            }
                                                                            else {
                                                                                Log.d(TAG, "Company query result wasn't null, should start for loop");
                                                                                Intent joinstartgroup = new Intent(LoginActivity.this, JoinStartGroup.class);
                                                                                Bundle joinstartbundle = new Bundle();
                                                                                joinstartbundle.putString("First Name", userFirstName);
                                                                                joinstartbundle.putString("Last Name", userLastName);
                                                                                Log.d(TAG, "DOCNAME FROM LOGIN: " + docName);
                                                                                joinstartbundle.putString("User ID", docName);
                                                                                joinstartgroup.putExtras(joinstartbundle);
                                                                                Log.d(TAG, "INTENT STARTED");
                                                                                startActivity(joinstartgroup);
                                                                            }
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        donthaveaccountButton.setClickable(true);
                                                                        homeButton.setClickable(true);
                                                                        loginButton.setClickable(true);
                                                                        useremailEditText.setVisibility(View.VISIBLE);
                                                                        userpasswordEditText.setVisibility(View.VISIBLE);
                                                                        loginButton.setVisibility(View.VISIBLE);
                                                                        donthaveaccountButton.setVisibility(View.VISIBLE);
                                                                        homeButton.setVisibility(View.VISIBLE);
                                                                        loginAnimation.setVisibility(View.INVISIBLE);
                                                                        loginAnimation.cancelAnimation();
                                                                    }
                                                                });
                                                        }
                                                    }
                                                    else
                                                    {
                                                        donthaveaccountButton.setClickable(true);
                                                        homeButton.setClickable(true);
                                                        loginButton.setClickable(true);
                                                        useremailEditText.setVisibility(View.VISIBLE);
                                                        userpasswordEditText.setVisibility(View.VISIBLE);
                                                        loginButton.setVisibility(View.VISIBLE);
                                                        donthaveaccountButton.setVisibility(View.VISIBLE);
                                                        homeButton.setVisibility(View.VISIBLE);
                                                        loginAnimation.setVisibility(View.INVISIBLE);
                                                        loginAnimation.cancelAnimation();
                                                    }
                                                }
                                                else {
                                                    donthaveaccountButton.setClickable(true);
                                                    homeButton.setClickable(true);
                                                    loginButton.setClickable(true);
                                                    useremailEditText.setVisibility(View.VISIBLE);
                                                    loginAnimation.setVisibility(View.INVISIBLE);
                                                    userpasswordEditText.setVisibility(View.VISIBLE);
                                                    loginButton.setVisibility(View.VISIBLE);
                                                    donthaveaccountButton.setVisibility(View.VISIBLE);
                                                    homeButton.setVisibility(View.VISIBLE);
                                                    Toast.makeText(LoginActivity.this, "Login Failed, unable to access database. Please check" +
                                                            "internet connection and try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        donthaveaccountButton.setClickable(true);
                                        homeButton.setClickable(true);
                                        loginButton.setClickable(true);
                                        useremailEditText.setVisibility(View.VISIBLE);
                                        userpasswordEditText.setVisibility(View.VISIBLE);
                                        loginButton.setVisibility(View.VISIBLE);
                                        donthaveaccountButton.setVisibility(View.VISIBLE);
                                        homeButton.setVisibility(View.VISIBLE);
                                        loginAnimation.setVisibility(View.INVISIBLE);
                                        loginAnimation.cancelAnimation();
                                    }
                                });
                                Log.d(TAG, "signInWithEmail:success");
                            }
                            else { //if task is unsuccessful
                                // If sign in fails, display a message to the user.
                                donthaveaccountButton.setClickable(true);
                                homeButton.setClickable(true);
                                loginButton.setClickable(true);
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                useremailEditText.setVisibility(View.VISIBLE);
                                userpasswordEditText.setVisibility(View.VISIBLE);
                                loginButton.setVisibility(View.VISIBLE);
                                donthaveaccountButton.setVisibility(View.VISIBLE);
                                homeButton.setVisibility(View.VISIBLE);
                                loginAnimation.setVisibility(View.INVISIBLE);
                                loginAnimation.cancelAnimation();
                                Toast.makeText(LoginActivity.this, "Oops! Authentication failed. Email or password may be incorrect",
                            Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    donthaveaccountButton.setClickable(true);
                    homeButton.setClickable(true);
                    loginButton.setClickable(true);
                    useremailEditText.setVisibility(View.VISIBLE);
                    userpasswordEditText.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    donthaveaccountButton.setVisibility(View.VISIBLE);
                    homeButton.setVisibility(View.VISIBLE);
                    loginAnimation.setVisibility(View.INVISIBLE);
                    loginAnimation.cancelAnimation();
                }
            });
        }
        else
        {
            donthaveaccountButton.setClickable(true);
            homeButton.setClickable(true);
            loginButton.setClickable(true);
            Toast.makeText(LoginActivity.this, "Please enter a valid username or password", Toast.LENGTH_LONG).show();
            useremailEditText.setVisibility(View.VISIBLE);
            userpasswordEditText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            donthaveaccountButton.setVisibility(View.VISIBLE);
            homeButton.setVisibility(View.VISIBLE);
            loginAnimation.setVisibility(View.INVISIBLE);
            loginAnimation.cancelAnimation();
        }
    }
}