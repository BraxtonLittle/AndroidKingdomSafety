package com.something.KingdomSafety;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.fasterxml.jackson.databind.ser.Serializers;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserReport extends AppCompatActivity {
    private final String TAG  = "USERREPORT";
private String userName, userID, companyName, datedTrainingID, companyID, selectedUserID;
private ListView listView;
private int selectedPosition;
private ConstraintLayout previewWindow;
private TextView vidBool, vidDate, passedTestBool, passTestDateTextView;
private ImageView dummyView;
private LottieAnimationView myAnimationView;
private ArrayList<String> completedTrainings, customTrainingNames, customTrainingIDs, userNameList, uidList, trainingIDs,
        watchedVidDate, passedTestDate, passedTestList, watchedVidList, trainingNames, hasWatchedVid, hasPassedTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);
        listView = findViewById(R.id.completedTrainingsListView);
        myAnimationView = findViewById(R.id.userReportAnimationView);
        previewWindow = findViewById(R.id.userReportUserData);
        dummyView = findViewById(R.id.uerReportDummyView);
        vidBool = findViewById(R.id.watchedVidBooleanText);
        vidDate = findViewById(R.id.watchedVidDateText);
        passedTestBool = findViewById(R.id.passedTestBooleanText);
        passTestDateTextView = findViewById(R.id.passedTestDateText);
        previewWindow.setVisibility(View.INVISIBLE);
        Bundle reportBundle = getIntent().getExtras();
        userName = reportBundle.getString("User Name");
        companyName = reportBundle.getString("Company Name");
        companyID = reportBundle.getString("Company ID");
        selectedUserID = reportBundle.getString("Selected User ID");
        datedTrainingID = reportBundle.getString("Dated Training ID");
        userID = reportBundle.getString("User ID");
        completedTrainings = reportBundle.getStringArrayList("Completed Trainings");
        customTrainingNames = reportBundle.getStringArrayList("Custom Training Names");
        customTrainingIDs = reportBundle.getStringArrayList("Custom Training IDs");
        hasPassedTest = reportBundle.getStringArrayList("Has Passed Test");
        hasWatchedVid = reportBundle.getStringArrayList("Has Watched Video");
        selectedPosition = reportBundle.getInt("Selected Position");
        userNameList = reportBundle.getStringArrayList("User Name List");
        uidList = reportBundle.getStringArrayList("UID List");
        trainingIDs = reportBundle.getStringArrayList("Training IDs");
        watchedVidDate = reportBundle.getStringArrayList("Watched Vid Date");
        passedTestDate = reportBundle.getStringArrayList("Passed Test Date");
        passedTestList = reportBundle.getStringArrayList("Passed Test List");
        watchedVidList = reportBundle.getStringArrayList("Watched Vid List");
        myAnimationView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        myAnimationView.playAnimation();
        myAnimationView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        previewWindow.setVisibility(View.INVISIBLE);
        trainingNames = new ArrayList<>();
        for(int i = 0; i<trainingIDs.size(); i++)
        {
            trainingNames.add(customTrainingNames.get(customTrainingIDs.indexOf(trainingIDs.get(i))));
        }
        Log.d(TAG, "TRAINING NAMES IN USERREPORT: " + trainingNames);

        BaseArrayAdapter myAdapter = new BaseArrayAdapter
                (UserReport.this, R.layout.trainingnameslistitemsmanage, trainingNames, completedTrainings);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setVisibility(View.INVISIBLE);
                previewWindow.setVisibility(View.VISIBLE);
                vidBool.setText("Watched Video: " + watchedVidList.get(position));
                vidDate.setText("Date watched video: " + watchedVidDate.get(position));
                passedTestBool.setText("Passed Test: " + passedTestList.get(position));
                if(passedTestDate.get(position)!=null) {
                    passTestDateTextView.setText("Date Passed Test: " + passedTestDate.get(position));
                }
                else {
                    passTestDateTextView.setText("Incomplete");
                }

                dummyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previewWindow.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        myAnimationView.cancelAnimation();
        myAnimationView.setVisibility(View.INVISIBLE);
    }

    public void backToUserScreenClicked(View v)
    {
        Intent userScreen = new Intent(UserReport.this, UserSettings.class);
        userScreen.putExtra("User Name", userName);
        userScreen.putExtra("User ID", userID);
        userScreen.putExtra("Company Name", companyName);
        userScreen.putStringArrayListExtra("Has Watched Video", hasWatchedVid);
        userScreen.putStringArrayListExtra("Has Passed Test", hasPassedTest);
        userScreen.putExtra("Selected Position", selectedPosition);
        userScreen.putExtra("Dated Training ID", datedTrainingID);
        userScreen.putExtra("Company ID", companyID);
        userScreen.putStringArrayListExtra("User Name List", userNameList);
        userScreen.putStringArrayListExtra("UID List", uidList);
        userScreen.putExtra("Selected User ID", selectedUserID);
        startActivity(userScreen);
    }

    public void refreshUserReportClicked(View v)
    {
        myAnimationView.setVisibility(View.VISIBLE);
        myAnimationView.playAnimation();
        onStart();
    }

    static class ViewHolder
    {
        TextView title;
        TextView weekNumber;
    }

    public class BaseArrayAdapter extends ArrayAdapter<String>
    {
        private ArrayList<String> trainings, weeks;
        private LayoutInflater mInflater;
        private int mLayout;

        public BaseArrayAdapter(Context context, int textViewResourceId, ArrayList<String> trainingNames, ArrayList<String> weekList) {
            super(context, textViewResourceId, trainingNames);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
            trainings = trainingNames;
            weeks = weekList;
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
            holder.weekNumber.setTextSize(16);
            int showWeek = Integer.valueOf(weeks.get(position))+1;
            holder.weekNumber.setText("Week: " + showWeek);

            return view;
        }
    }

    public void cancelUserReportClicked(View v)
    {
        previewWindow.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }
}
