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
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginSignup extends AppCompatActivity {
    private VideoView videoBG;
    private int myVidPosition, randNum;
    private FirebaseAuth mAuth;
    private Button signInButton, signUpButton;
    private LottieAnimationView animationView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    public String[] videosArray;
    private Boolean autosignin;
    private String companyName, docName, companyID, userFirstName, userLastName;
    private final static String TAG = "LoginSignup";
    private Uri myUri;
    MediaPlayer myPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        autosignin = true;
        setContentView(R.layout.activity_login_signup);
        signInButton = findViewById(R.id.signinbutton);
        signUpButton = findViewById(R.id.signupbutton);
        animationView = findViewById(R.id.lottieanimationview);
        myVidPosition = 0;
        videoBG = findViewById(R.id.videoView);
        videosArray = new String[5];
        videosArray[0] = "android.resource://" + getPackageName() + "/" + R.raw.jackhammervideo;
        videosArray[1] = "android.resource://" + getPackageName() + "/" + R.raw.constructiontimelapse;
        videosArray[2] = "android.resource://" + getPackageName() + "/" + R.raw.layingbricks;
        videosArray[3] = "android.resource://" + getPackageName() + "/" + R.raw.cranetimelapse;
        videosArray[4] = "android.resource://" + getPackageName() + "/" + R.raw.cuttingwood;
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!=null)
        {
            randNum = myBundle.getInt("RandNum");
            myVidPosition = myBundle.getInt("vidTime") + 1500;
            autosignin = myBundle.getBoolean("autosignin");
        }
        else {
            randNum = getRandNum();
        }

        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null && autosignin)
        {
            docName = currentUser.getUid();
            // Sign in success, update UI with the signed-in user's information
            animationView.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            db.collection("users")
                    .whereEqualTo("uid", docName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "UID HAS BEEN FOUND");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document!=null) {
                                        userFirstName = document.getString("firstname");
                                        userLastName = document.getString("lastname");
                                            db.collection("companies").whereArrayContains(docName, docName)
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    Log.d(TAG, "DB. WHERE ARRAY CONTAINS: " + task.getResult().getDocuments());
                                                    if(task.isSuccessful()) {
                                                        if(task.getResult().getDocuments().size()==0){
                                                            Log.d(TAG, "DOCUMENT IN COMPANY QUERY DID RETURN NULL, SHOULD GO TO JOINSTARTGROUP");
                                                            Intent joinstartgroup = new Intent(LoginSignup.this, JoinStartGroup.class);
                                                            joinstartgroup.putExtra("First Name", userFirstName);
                                                            joinstartgroup.putExtra("Last Name", userLastName);
                                                            joinstartgroup.putExtra("ingroup", false);
                                                            joinstartgroup.putExtra("User ID", docName);
                                                            startActivity(joinstartgroup);
                                                        }
                                                        else
                                                            {
                                                            for (DocumentSnapshot document : task.getResult()) {
                                                                    companyName = document.getId();
                                                                    companyID = document.getString("Company ID");
                                                                    Intent adminScreen = new Intent(LoginSignup.this, SelectGroup.class);
                                                                    adminScreen.putExtra("Company Name", companyName);
                                                                    adminScreen.putExtra("Company ID", companyID);
                                                                    adminScreen.putExtra("First Name", userFirstName);
                                                                    adminScreen.putExtra("Last Name", userLastName);
                                                                    adminScreen.putExtra("User ID", docName);
                                                                    adminScreen.putExtra("RandNum", randNum);
                                                                    startActivity(adminScreen);
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        signInButton.setVisibility(View.VISIBLE);
                                                        signUpButton.setVisibility(View.VISIBLE);
                                                        signInButton.setClickable(true);
                                                        signUpButton.setClickable(true);
                                                        animationView.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(LoginSignup.this, "Failed to query user's companies", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    }
                                }
                            }
                            else {
                                signInButton.setVisibility(View.VISIBLE);
                                signUpButton.setVisibility(View.VISIBLE);
                                signInButton.setClickable(true);
                                signUpButton.setClickable(true);
                                animationView.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginSignup.this, "Failed to find user info within database", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            Log.d(TAG, "signInWithEmail:success");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        animationView.setVisibility(View.INVISIBLE);
        myUri = Uri.parse(videosArray[randNum]);
        videoBG.setVideoURI(myUri);
        videoBG.start();
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                myPlayer = mediaPlayer;
                myPlayer.setLooping(true);
                if(myVidPosition !=0)
                {
                    myPlayer.seekTo(myVidPosition);
                    myPlayer.start();
                }
            }
        }
        );
        animationView.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
    }

    public int getRandNum()
    {
        randNum = (int)Math.floor(Math.random() * 5);
        return randNum;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        videoBG.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getRandNum();
        myUri = Uri.parse(videosArray[randNum]);
        videoBG.setVideoURI(myUri);
        videoBG.seekTo(myVidPosition);
        videoBG.start();
        animationView.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
    }


    public void loginpressed(View v)
    {
        signInButton.setClickable(false);
        signUpButton.setClickable(false);
        myVidPosition = myPlayer.getCurrentPosition();
        Intent loginScreen = new Intent(LoginSignup.this, LoginActivity.class);
        loginScreen.putExtra("RandNum",randNum);
        loginScreen.putExtra("vidTime", myVidPosition);
        Log.d("LS SENT VID POS", "INITIAL VIDPOS: " + myVidPosition);
        startActivity(loginScreen);
    }

    public void signuppressed(View v)
    {
        signUpButton.setClickable(false);
        signInButton.setClickable(false);
        myVidPosition = myPlayer.getCurrentPosition();
        Intent signupScreen = new Intent(LoginSignup.this, SignUpActivity.class);
        signupScreen.putExtra("RandNum", randNum);
        signupScreen.putExtra("vidTime", myVidPosition);
        startActivity(signupScreen);
    }

}
