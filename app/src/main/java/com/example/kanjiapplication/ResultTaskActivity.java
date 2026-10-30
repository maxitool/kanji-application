package com.example.kanjiapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultTaskActivity extends AppCompatActivity {

    private final static int COUNT_STARS = 5;
    private RatingBar ratingBarResultTask;
    private TextView textPercentResultTask, textStatisticsResultTask;
    private int countTasks, correctTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_task);
        Intent intent = getIntent();
        countTasks = Integer.parseInt((String) intent.getSerializableExtra("countTasks"));
        correctTasks = Integer.parseInt((String) intent.getSerializableExtra("correctTasks"));
        textPercentResultTask = findViewById(R.id.textPercentResultTask);
        textPercentResultTask.setText(String.format("%.1f", (float) correctTasks / (float) countTasks * 100) + '%');
        textStatisticsResultTask = findViewById(R.id.textStatisticsResultTask);
        textStatisticsResultTask.setText(Integer.toString(correctTasks) + " из " + Integer.toString(countTasks));
        ratingBarResultTask = findViewById(R.id.ratingBarResultTask);
        ratingBarResultTask.setRating((float) correctTasks / (float) countTasks * (float) COUNT_STARS);
    }

    public void back(View view) {
        this.finish();
    }
}