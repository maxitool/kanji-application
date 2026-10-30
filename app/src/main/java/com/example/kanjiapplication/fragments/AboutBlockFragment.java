package com.example.kanjiapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kanjiapplication.InformationActivity;
import com.example.kanjiapplication.R;
import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.INFO;

public class AboutBlockFragment extends Fragment {

    private ImageView imageKanjiAboutBlock;
    private TextView textReadingAboutBlock, textTranslationAboutBlock;
    private LinearLayout linearLayoutAboutBlock;
    private INFO info;
    private char kanji;

    public AboutBlockFragment(char kanji) {
        this.kanji = kanji;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DBKanjiInfoAccess dbKanjiInfoAccess = new DBKanjiInfoAccess(view.getContext());
        info = dbKanjiInfoAccess.getInfoKanji(kanji);
        imageKanjiAboutBlock = view.findViewById(R.id.imageKanjiAboutBlock);
        imageKanjiAboutBlock.setImageResource(this.getResources().getIdentifier('i' + Integer.toString(info.ID), "drawable", getActivity().getPackageName()));
        textReadingAboutBlock = view.findViewById(R.id.textReadingAboutBlock);
        textReadingAboutBlock.setText(info.READING);
        textTranslationAboutBlock = view.findViewById(R.id.textTranslationAboutBlock);
        textTranslationAboutBlock.setText(info.MEANING);
        linearLayoutAboutBlock = view.findViewById(R.id.linearLayoutAboutBlock);
        linearLayoutAboutBlock.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), InformationActivity.class);
            intent.putExtra("listKanji", Character.toString(kanji));
            startActivity(intent);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_block, container, false);
    }
}