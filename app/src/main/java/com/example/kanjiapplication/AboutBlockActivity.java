package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kanjiapplication.R;
import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.DBOwnGroupsAccess;
import com.example.kanjiapplication.databases.tables.GROUPS;
import com.example.kanjiapplication.fragments.AboutBlockFragment;

public class AboutBlockActivity extends AppCompatActivity {

    private LinearLayout linearLayoutAboutBlocks;
    private Button buttonLearn, buttonAddKanjiAboutBlock;
    private TextView textLevelAboutBlock, textGroupAboutBlock;
    private String level, group;
    private GROUPS groups;
    private boolean isOwnGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_block);
        level = (String)this.getIntent().getSerializableExtra("level");
        group = (String)this.getIntent().getSerializableExtra("group");
        linearLayoutAboutBlocks = findViewById(R.id.linearLayoutAboutBlocks);
        textLevelAboutBlock = findViewById(R.id.textLevelAboutBlock);
        textGroupAboutBlock = findViewById(R.id.textGroupAboutBlock);
        buttonLearn = findViewById(R.id.buttonLearn);
        buttonAddKanjiAboutBlock = findViewById(R.id.buttonAddKanjiAboutBlock);
        isOwnGroup = isOwnGroupFunc();
        if (isOwnGroup) {
            textLevelAboutBlock.setText("Моя группа");
            buttonAddKanjiAboutBlock.setOnClickListener(view -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }
        else {
            textLevelAboutBlock.setText(level);
            buttonAddKanjiAboutBlock.setVisibility(View.GONE);
        }
        textGroupAboutBlock.setText(group);
        if (isOwnGroup) {
            DBOwnGroupsAccess dbOwnGroupsAccess = new DBOwnGroupsAccess(this);
            groups = dbOwnGroupsAccess.getGroup(group);
        }
        else {
            DBKanjiInfoAccess dbKanjiInfoAccess = new DBKanjiInfoAccess(this);
            groups = dbKanjiInfoAccess.getGroup(level, group);
        }
        if (groups.LIST_KANJI.length() == 0)
            buttonLearn.setEnabled(false);
        setKanji();
    }


    public void letsLearn(View view) {
        Intent intent = new Intent(view.getContext(), TaskActivity.class);
        intent.putExtra("listKanji", groups.LIST_KANJI);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void setKanji() {
        if (groups.LIST_KANJI.length() > 0) {
            char[] kanjiArray = groups.LIST_KANJI.toCharArray();
            FrameLayout flPointer;
            FragmentManager fragmentManager = getSupportFragmentManager();
            AboutBlockFragment abfPointer;
            for (int i = 0; i < kanjiArray.length; i++) {
                flPointer = new FrameLayout(this);
                flPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                flPointer.setId(i + 1);
                linearLayoutAboutBlocks.addView(flPointer);
                abfPointer = new AboutBlockFragment(kanjiArray[i]);
                fragmentManager.beginTransaction().replace(i + 1, abfPointer).commit();
            }
        }
    }

    private boolean isOwnGroupFunc() {
        char[] _level = level.toCharArray();
        if (_level.length == 3)
            if (_level[0] == 'O' && _level[1] == 'w' && _level[2] == 'n')
                return true;
        return false;
    }
}