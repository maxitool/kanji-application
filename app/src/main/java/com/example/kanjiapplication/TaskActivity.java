package com.example.kanjiapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kanjiapplication.databases.DBKanjiInfoAccess;
import com.example.kanjiapplication.databases.tables.INFO;
import com.example.kanjiapplication.databases.tables.TASKS;
import com.example.kanjiapplication.fragments.DrawFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class TaskActivity extends AppCompatActivity {

    private String result;
    private TASKS task;
    private String answer;
    private TextView textFuriganaTask, textWordTask, textRomanjiTask, textTranslationTask;
    private Button buttonNextTask, buttonCheckTask;
    private FrameLayout frameLayoutTask;
    private DrawFragment drawFragment;
    private char[] listKanji;
    private int pointerKanji;
    private Dialog dialog;
    private int[] ids;
    private DBKanjiInfoAccess dbKanjiInfoAccess;
    private int positionKanji, startPosFurigana, endPosFurigana;
    private int countTasks = 0, correctTasks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Intent intent = getIntent();
        String _listKanji = (String)intent.getSerializableExtra("listKanji");
        listKanji = _listKanji.toCharArray();
        textFuriganaTask = findViewById(R.id.textFuriganaTask);
        textWordTask = findViewById(R.id.textWordTask);
        textRomanjiTask = findViewById(R.id.textRomanjiTask);
        textTranslationTask = findViewById(R.id.textTranslationTask);
        buttonNextTask = findViewById(R.id.buttonNextTask);
        buttonNextTask.setEnabled(false);
        buttonCheckTask = findViewById(R.id.buttonCheckTask);
        frameLayoutTask = findViewById(R.id.frameLayoutTask);
        drawFragment = new DrawFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutTask, drawFragment);
        fragmentTransaction.commit();
        frameLayoutTask.post(() -> {
            int[] globalLocation = new int[2];
            frameLayoutTask.getLocationOnScreen(globalLocation);
            drawFragment.setSettings(globalLocation);
        });
        dbKanjiInfoAccess = new DBKanjiInfoAccess(this);
        randomKanji();
        ids = dbKanjiInfoAccess.getIDsKanji(listKanji, listKanji.length);
        reDraw();
        dialog = new Dialog(this);
    }

    public void checkAction(View view) throws IOException, InterruptedException {
        result = "Unknown";
        String image = drawFragment.getImage();
        if (image== "") {
            Toast.makeText(this, "Paint panel is empty", Toast.LENGTH_LONG).show();
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SendToServer sendToServer = new SendToServer(countDownLatch, image);
        countDownLatch.await();
        answer = sendToServer.getKanjiList();
        if (answer == "") {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }
        if (answer == "?") {
            Toast.makeText(this, "The drawn hieroglyph was not found", Toast.LENGTH_LONG).show();
            return;
        }
        buttonCheckTask.setEnabled(false);
        char[] answerArray = answer.toCharArray();
        countTasks++;
        for (int i = 0; i < answerArray.length; i++)
            if (answerArray[i] == listKanji[pointerKanji]) {
                result = "Правильно";
                correctTasks++;
                break;
            }
        if (result != "Правильно")
            result = "Неправильно";
        dialog.setContentView(R.layout.check_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textAnswer = dialog.findViewById(R.id.textAnswer);
        textAnswer.setText(result);
        ImageView imageCurrentDialog = dialog.findViewById(R.id.imageCurrentDialog);
        imageCurrentDialog.setImageBitmap(drawFragment.getBitmap());
        ImageView imageCorrectDialog = dialog.findViewById(R.id.imageCorrectDialog);
        imageCorrectDialog.setImageResource(this.getResources().getIdentifier('i' + Integer.toString(ids[pointerKanji]), "drawable", getPackageName()));
        ImageButton buttonExitDialog = dialog.findViewById(R.id.buttonExitDialog);
        buttonExitDialog.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        dialog.show();
        buttonNextTask.setEnabled(true);
    }

    public void nextTask(View view) {
        buttonNextTask.setEnabled(false);
        buttonCheckTask.setEnabled(true);
        pointerKanji++;
        if (pointerKanji == listKanji.length)
            randomKanji();
        reDraw();
        drawFragment.clearPanel();
    }

    public void quit(View view) {
        if (countTasks == 0)
            this.finish();
        else {
            Intent intent = new Intent(this, ResultTaskActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("countTasks", Integer.toString(countTasks));
            intent.putExtra("correctTasks", Integer.toString(correctTasks));
            startActivity(intent);
        }
    }

    private void reDraw() {
        randomTask();;
        setPositionKanji();
        Spannable spannable = new SpannableString(task.FURIGANA.replaceAll("\\s+",""));
        spannable.setSpan(new ForegroundColorSpan(Color.RED), startPosFurigana, endPosFurigana, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textFuriganaTask.setText(spannable, TextView.BufferType.SPANNABLE);
        spannable = new SpannableString(replaceWord());
        spannable.setSpan(new ForegroundColorSpan(Color.RED), startPosFurigana, endPosFurigana, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textWordTask.setText(spannable, TextView.BufferType.SPANNABLE);
        textRomanjiTask.setText(task.ROMAJI);
        textTranslationTask.setText(getOneLineTranslation());
    }

    private String replaceWord() {
        char[] word = task.WORD.toCharArray();
        char[] furigana = task.FURIGANA.replaceAll("\\s+","").toCharArray();
        String result = "";
        int pointerBegin = 0, pointerEnd = 0;
        for (int i = 0; i < positionKanji; i++) {
            result += word[i];
            pointerBegin ++;
        }
        pointerEnd = pointerBegin;
        for (int i = startPosFurigana; i < endPosFurigana; i++) {
            result += furigana[i];
            pointerEnd++;
        }
        for (int i = positionKanji + 1; i < word.length; i++)
            result += word[i];
        startPosFurigana = pointerBegin;
        endPosFurigana = pointerEnd;
        return result;
    }

    private String getOneLineTranslation() {
        char[] translation = task.TRANSLATION.toCharArray();
        String result = "";
        for (int i = 0; i < translation.length; i++) {
            if (translation[i] == '\n' || translation[i] == ';')
                break;
            result += translation[i];
        }
        return result;
    }

    private void setPositionKanji() {
        char[] word = task.WORD.toCharArray();
        for (int i = 0; i < word.length; i++)
            if (word[i] == listKanji[pointerKanji]) {
                positionKanji = i;
                break;
            }
        int spaces = 0;
        word = task.FURIGANA.toCharArray();
        endPosFurigana = -1;
        boolean isFoundStart = false;
        for (int i = 0; i < word.length; i++) {
            if (spaces == positionKanji) {
                if (!isFoundStart) {
                    startPosFurigana = i - spaces;
                    isFoundStart = true;
                } else if (word[i] == ' ') {
                    endPosFurigana = i - spaces;
                    break;
                }
            } else if (word[i] == ' ')
                spaces++;
        }
        if (endPosFurigana == -1)
            endPosFurigana = task.FURIGANA.replaceAll("\\s+", "").length();
    }

    private void randomKanji() {
        if (listKanji.length > 1) {
            char[] listKanjiTemp = new char[listKanji.length];
            int pointer;
            for (int i = 0; i < listKanjiTemp.length/2; i++) {
                do {
                    pointer = (int)(Math.random() * listKanjiTemp.length);
                } while (listKanjiTemp[pointer] != 0);
                listKanjiTemp[pointer] = listKanji[i];
            }
            pointer = 0;
            for (int i = listKanjiTemp.length/2; i < listKanjiTemp.length; i++)
                for (int j = pointer; j < listKanjiTemp.length; j++)
                    if (listKanjiTemp[j] == 0) {
                        listKanjiTemp[j] = listKanji[i];
                        pointer = j + 1;
                        break;
                    }
            for (int i = 0; i < listKanjiTemp.length; i++)
                listKanji[i] = listKanjiTemp[i];
        }
        pointerKanji = 0;
    }

    private void randomTask() {
        List<TASKS> tasks = dbKanjiInfoAccess.getTasksKanji(listKanji[pointerKanji]);
        int pointer = (int)(Math.random() * tasks.size());
        task = tasks.get(pointer);
    }
}