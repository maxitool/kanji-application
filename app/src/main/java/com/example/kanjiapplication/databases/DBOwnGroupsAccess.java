package com.example.kanjiapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kanjiapplication.databases.tables.GROUPS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBOwnGroupsAccess {

    private static final boolean HAVE_SAME_NAME = false, NO_HAVE_SAME_NAME = true;
    private static final int NO_ERRORS = 0, NO_GROUP = 1, HAVE_SAME_KANJI_IN_GROUP = 2;
    private DBOwnGroupsHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;

    public DBOwnGroupsAccess(Context context) {
        dbHelper = new DBOwnGroupsHelper(context);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public List<GROUPS> getGroups() {
        List<GROUPS> groups = new ArrayList<>();
        int iterator = 0;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM GROUPS ORDER BY ID", null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                groups.add(new GROUPS());
                groups.get(iterator).ID = Integer.parseInt(cursor.getString(0));
                groups.get(iterator).NAME = cursor.getString(1);
                groups.get(iterator).LIST_KANJI = cursor.getString(2);
                cursor.moveToNext();
                iterator++;
            }
        cursor.close();
        return groups;
    }

    public GROUPS getGroup(String groupName) {
        GROUPS group = new GROUPS();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM GROUPS WHERE NAME = '" + groupName + "' ORDER BY ID", null);
        if (cursor.moveToFirst()) {
            group.ID = Integer.parseInt(cursor.getString(0));
            group.NAME = cursor.getString(1);
            group.LIST_KANJI = cursor.getString(2);
        }
        cursor.close();
        return group;
    }

    public boolean addNewGroup(String nameGroup) {
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM GROUPS WHERE NAME = '" + nameGroup + "'", null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return HAVE_SAME_NAME;
        }
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", nameGroup);
        contentValues.put("LIST_KANJI", "");
        sqLiteDatabase.insert("GROUPS", null, contentValues);
        return NO_HAVE_SAME_NAME;
    }

    public int isExistKanji(String nameGroup, char kanji) {
        String listKanji = "";
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM GROUPS WHERE NAME = '" + nameGroup + "'", null);
        if (cursor.moveToFirst())
            listKanji = cursor.getString(2);
        else {
            cursor.close();
            return NO_GROUP;
        }
        cursor.close();
        char[] kanjiArray = listKanji.toCharArray();
        for (int i = 0; i < kanjiArray.length; i++)
            if (kanjiArray[i] == kanji)
                return HAVE_SAME_KANJI_IN_GROUP;
        return NO_ERRORS;
    }

    public int addKanji(String nameGroup, char kanji) {
        int isExist = isExistKanji(nameGroup, kanji);
        if (isExist != 0)
            return isExist;
        String listKanji = "";
        int id = 0;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM GROUPS WHERE NAME = '" + nameGroup + "'", null);
        if (cursor.moveToFirst()) {
            id = Integer.parseInt(cursor.getString(0));
            listKanji = cursor.getString(2);
        }
        cursor.close();
        listKanji += kanji;
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", nameGroup);
        contentValues.put("LIST_KANJI", listKanji);
        sqLiteDatabase.update("GROUPS", contentValues, "ID=" + Integer.toString(id), null);
        return NO_ERRORS;
    }
}
