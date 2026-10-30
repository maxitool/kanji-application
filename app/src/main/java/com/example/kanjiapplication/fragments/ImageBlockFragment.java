package com.example.kanjiapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kanjiapplication.AboutBlockActivity;
import com.example.kanjiapplication.R;
import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.DBOwnGroupsAccess;
import com.example.kanjiapplication.databases.tables.GROUPS;

public class ImageBlockFragment extends Fragment {

    private static final int COUNT_IMAGES = 4;
    private ImageView[] images;
    private TextView textNameGroup, textKanjiExistsImageBlock;
    private RelativeLayout relativeLayoutImages;
    private GROUPS group;
    private String level;
    private boolean isAddKanji;
    private boolean isExistsKanji;
    private char kanji;

    public ImageBlockFragment(String level, GROUPS group, boolean isAddKanji) {
        this.level = level;
        this.group = group;
        this.isAddKanji = isAddKanji;
        images = new ImageView[COUNT_IMAGES];
    }

    public void setKanji(char kanji) {
        this.kanji = kanji;
        char[] listKanji = group.LIST_KANJI.toCharArray();
        for (int i = 0; i < listKanji.length; i++)
            if (kanji == listKanji[i]) {
                isExistsKanji = true;
                break;
            }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textNameGroup = view.findViewById(R.id.textNameGroup);
        textNameGroup.setText(group.NAME);
        DBKanjiInfoAccess dbKanjiInfoAccess = new DBKanjiInfoAccess(view.getContext());
        char[] listKanji = group.LIST_KANJI.toCharArray();
        int[] ids = dbKanjiInfoAccess.getIDsKanji(listKanji, COUNT_IMAGES);
        //set resources of images
        images[0] = view.findViewById(R.id.imageKanji1);
        images[1] = view.findViewById(R.id.imageKanji2);
        images[2] = view.findViewById(R.id.imageKanji3);
        images[3] = view.findViewById(R.id.imageKanji4);
        for (int i = 0; i < ids.length; i++)
            images[i].setImageResource(this.getResources().getIdentifier('i' + Integer.toString(ids[i]), "drawable", getActivity().getPackageName()));
        if (ids.length < COUNT_IMAGES)
            for (int i = ids.length; i < COUNT_IMAGES; i++)
                images[i].setVisibility(View.GONE);
        textKanjiExistsImageBlock = view.findViewById(R.id.textKanjiExistsImageBlock);
        if (!isAddKanji)
            textKanjiExistsImageBlock.setVisibility(View.GONE);
        if (isAddKanji)
            if (!isExistsKanji)
                textKanjiExistsImageBlock.setVisibility(View.GONE);
        relativeLayoutImages = view.findViewById(R.id.relativeLayoutImages);
        if (isAddKanji && !isExistsKanji && kanji != 0)
            relativeLayoutImages.setOnClickListener(view12 -> {
                DBOwnGroupsAccess dbOwnGroupsAccess = new DBOwnGroupsAccess(view.getContext());
                if (dbOwnGroupsAccess.addKanji(group.NAME, kanji) != 0)
                    Toast.makeText(view.getContext(), "Kanji cant add to the group", Toast.LENGTH_LONG).show();
                getActivity().finish();
                startActivity(getActivity().getIntent());
            });
        if (!isAddKanji)
            relativeLayoutImages.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), AboutBlockActivity.class);
                intent.putExtra("level", level);
                intent.putExtra("group", group.NAME);
                startActivity(intent);
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_block, container, false);
    }
}