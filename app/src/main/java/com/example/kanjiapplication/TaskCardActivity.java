package com.example.kanjiapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.INFO;
import com.example.kanjiapplication.fragments.SequentialDrawFragment;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;

public class TaskCardActivity extends AppCompatActivity {
    private SequentialDrawFragment drawFragment;
    private FrameLayout frameLayout;
    private NavigationView navigationMenu;
    private Intent intent;
    private TextView textViewKanji;
    private TextView textViewReadings;
    private TextView textViewTranslate;
    private TextView textViewFeatures;
    private INFO kanjiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_card);
        intent = getIntent();
        String kanji = intent.getExtras().getString("kanji");
        if (kanji == null || kanji.length() == 0)
            finish();
        navigationMenu = findViewById(R.id.navigationMenu);
        navigationMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuLevel1:
                    intent = new Intent(TaskCardActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Уровень 1");
                    startActivity(intent);
                    return true;
                case R.id.menuLevel2:
                    intent = new Intent(TaskCardActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Уровень 2");
                    startActivity(intent);
                    return true;
                case R.id.menuLevel3:
                    intent = new Intent(TaskCardActivity.this, ChooseBlockActivity.class);
                    intent.putExtra("level", "Уровень 3");
                    startActivity(intent);
                    return true;
                case R.id.menuOwnGroups:
                    intent = new Intent(TaskCardActivity.this, OwnGroupsActivity.class);
                    intent.putExtra("kanji", "");
                    startActivity(intent);
                    return true;
            }
            return false;
        });
        DBKanjiInfoAccess dbKanjiInfoAccess = new DBKanjiInfoAccess(this);
        kanjiInfo = dbKanjiInfoAccess.getInfoKanji(kanji.charAt(0));
        textViewKanji = findViewById(R.id.textViewKanji);
        textViewKanji.setText(kanji);
        textViewReadings = findViewById(R.id.textViewReadings);
        textViewReadings.setText(kanjiInfo.READING);
        textViewTranslate = findViewById(R.id.textViewTranslate);
        textViewTranslate.setText(kanjiInfo.MEANING);
        textViewFeatures = findViewById(R.id.textViewFeatures);
        drawFragment = new SequentialDrawFragment(kanji.charAt(0));
        textViewFeatures.setText(String.valueOf(drawFragment.getCountFeatures()));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, drawFragment);
        fragmentTransaction.commit();
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.post(() -> {
            int[] globalLocation = new int[2];
            frameLayout.getLocationOnScreen(globalLocation);
            drawFragment.setSettings(globalLocation);
        });
    }
}