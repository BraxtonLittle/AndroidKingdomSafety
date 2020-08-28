package com.something.KingdomSafety;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mStrings = new ArrayList<String>();
    private ArrayList<String> trainingsContent = new ArrayList<String>();
    private LayoutInflater mInflater;
    private String rightAnswer;
    private int mLayout, tempNum;
    private TextView questionTitle, ans1, ans2, ans3, ans4;

    public MyAdapter(Context context, int questionBoxResourceId, ArrayList<String> trainings) {
        super(context, questionBoxResourceId, trainings);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLayout = questionBoxResourceId;
        mStrings = trainings;
    }

    public void setTrainingsContent(ArrayList<String> content)
    {
        trainingsContent = content;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ReviewTrainings.ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(this.mLayout, null);
            assert view != null;
            holder = new ReviewTrainings.ViewHolder();
            holder.questionTitle = (TextView)view.findViewById(R.id.questionSetTitle);
            holder.ans1 = (TextView)view.findViewById(R.id.questionSetAns1);
            holder.ans2 = (TextView)view.findViewById(R.id.questionSetAns2);
            holder.ans3 = (TextView)view.findViewById(R.id.questionSetAns3);
            holder.ans4 = (TextView)view.findViewById(R.id.questionSetAns4);
            view.setTag(holder);
        }
        else {
            holder = (ReviewTrainings.ViewHolder) view.getTag();
        }
        holder.questionTitle.setTextColor(Color.BLACK);
        holder.ans1.setTextColor(Color.BLACK);
        holder.ans2.setTextColor(Color.BLACK);
        holder.ans3.setTextColor(Color.BLACK);
        holder.ans4.setTextColor(Color.BLACK);
        tempNum = trainingsContent.indexOf(mStrings.get(0)) + 3;
        tempNum+=(position*7);
        Log.d("MYADAPTER", "TEMPNUM: " + tempNum);
        holder.questionTitle.setText(trainingsContent.get(tempNum+1));
        rightAnswer = trainingsContent.get(tempNum+2);
        switch(rightAnswer)
        {
            case "1":
                holder.ans1.setTextColor(Color.GREEN);
                break;
            case "2":
                holder.ans2.setTextColor(Color.GREEN);
                break;
            case "3":
                holder.ans3.setTextColor(Color.GREEN);
                break;
            case "4":
                holder.ans4.setTextColor(Color.GREEN);
                break;
        }
        holder.ans1.setText(trainingsContent.get(tempNum+3));
        holder.ans2.setText(trainingsContent.get(tempNum+4));
        holder.ans3.setText(trainingsContent.get(tempNum+5));
        holder.ans4.setText(trainingsContent.get(tempNum+6));
        return view;
    }
}
