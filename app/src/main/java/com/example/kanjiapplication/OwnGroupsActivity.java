package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.DBOwnGroupsAccess;
import com.example.kanjiapplication.databases.tables.GROUPS;
import com.example.kanjiapplication.fragments.ImageBlockFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class OwnGroupsActivity extends AppCompatActivity {

    private static final int COUNT_GROUPS_IN_ROW = 3;
    private LinearLayout linearLayoutOwnGroups;
    private DBOwnGroupsAccess dbOwnGroupsAccess;
    private LinearLayout llPointer;
    private FrameLayout flPointer;
    private ImageBlockFragment imageBlockFragment;
    private char kanji;
    private List<GROUPS> listGroups;
    private FragmentManager fragmentManager;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_groups);
        char[] _kanji = ((String)this.getIntent().getSerializableExtra("kanji")).toCharArray();
        if (_kanji.length != 0)
            kanji = _kanji[0];
        dbOwnGroupsAccess = new DBOwnGroupsAccess(this);
        listGroups = dbOwnGroupsAccess.getGroups();
        fragmentManager = getSupportFragmentManager();
        linearLayoutOwnGroups = findViewById(R.id.linearLayoutOwnGroups);
        dialog = new Dialog(this);
        setImagesFragments();
    }

    public void addGroup(View view) {
        dialog.setContentView(R.layout.add_group_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editTextTextPersonName = dialog.findViewById(R.id.editTextTextPersonName);
        ImageButton buttonExitDialog = dialog.findViewById(R.id.buttonExitDialog2);
        buttonExitDialog.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        Button buttonAddGroup = dialog.findViewById(R.id.buttonAddGroup);
        buttonAddGroup.setOnClickListener(view12 -> {
            if (editTextTextPersonName.getText().length() > 0) {
                if (dbOwnGroupsAccess.addNewGroup(editTextTextPersonName.getText().toString())) {
                    dialog.dismiss();
                    this.finish();
                    startActivity(getIntent());
                } else
                    Toast.makeText(this, "Cant add group to database (this name is already exists)", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "Text field is empty", Toast.LENGTH_LONG).show();
        });
        dialog.show();
    }

    private void setImagesFragments() {
        int countRows = (int)Math.ceil((double)listGroups.size() / (double)COUNT_GROUPS_IN_ROW);
        int countGroups = listGroups.size();
        int count;
        for (int i = 0; i < countRows; i++) {
            llPointer = new LinearLayout(this);
            llPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.VERTICAL));
            linearLayoutOwnGroups.addView(llPointer);
            if (countGroups > COUNT_GROUPS_IN_ROW)
                count = COUNT_GROUPS_IN_ROW;
            else
                count = countGroups;
            for (int j = 0; j < count; j++) {
                flPointer = new FrameLayout(this);
                flPointer.setId(i * COUNT_GROUPS_IN_ROW + j + 1);
                flPointer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                llPointer.addView(flPointer);
                if (kanji != 0) {
                    imageBlockFragment = new ImageBlockFragment("Own", listGroups.get(i * COUNT_GROUPS_IN_ROW + j), true);
                    imageBlockFragment.setKanji(kanji);
                }
                else
                    imageBlockFragment = new ImageBlockFragment("Own", listGroups.get(i * COUNT_GROUPS_IN_ROW + j), false);
                FragmentTransaction fm = fragmentManager.beginTransaction();
                fm.replace(i * COUNT_GROUPS_IN_ROW + j + 1, imageBlockFragment);
                fm.commit();
            }
            countGroups  -= COUNT_GROUPS_IN_ROW;
        }
    }
}