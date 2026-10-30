package com.example.kanjiapplication.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kanjiapplication.DrawDataKanji;
import com.example.kanjiapplication.R;
import com.example.kanjiapplication.threads.MyThreads;
import com.example.kanjiapplication.threads.ThreadDraw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SequentialDrawFragment extends Fragment {
    private static final int SIZE_SIDE_PANEL = 256,
            COUNT_DASH_LINES_IN_SIDE = 3;
    private static final int COUNT_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private GifImageView gifView;
    private GifImageView gifViewHalf;
    private GifImageView gifViewLayer;
    private GifDrawable gifDrawable;
    private GifDrawable gifDrawableHalf;
    private GifDrawable gifDrawableLayer;
    private ImageView imageViewDraw;
    private boolean isDrawable;
    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;
    private List<MyThreads> threadsList;
    private int height, width;
    private float[] points;
    private int[] locationViewDraw;
    private int[] globalLocation;
    private int widthBrush;
    private int countSteps;
    private float[] steps;
    private float[] pointSteps;
    private int commonCountPartitionStreams;
    private int remainder;
    private float scaleSizeSide;

    private double[] firstPoint = new double[2];
    private Bitmap previousBitmap;
    private int currentFeature = 0, currentLine = 0;
    private boolean[] isLinesDrawn = new boolean[0];
    private DrawDataKanji.InfoFeaturesKanji infoFeaturesKanji;
    private float[] lengthCurrentLineXY = new float[2];
    private boolean isLengthTooLong = false;
    private ImageView imageViewBack;
    private ImageView imageViewClear;
    private ImageView imageViewSetLayer;
    private ImageView imageViewPlay;
    private ThreadControlAnim tControlAnim;
    private boolean isShowBackKanji = false;
    private char kanji;
    private DrawDataKanji ddKanji;

    public SequentialDrawFragment(char kanji) {
        this.kanji = kanji;
        ddKanji = new DrawDataKanji();
        infoFeaturesKanji = ddKanji.getInfoFeaturesKanji(String.valueOf(kanji));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        points = new float[4];
        locationViewDraw = new int[2];
        steps = new float[2];
        pointSteps = new float[2];
        paint.setColor(Color.BLACK);
        View view = inflater.inflate(R.layout.fragment_draw_sequential, container, false);
        view.setOnTouchListener((view1, event) -> eventListener(view1, event));
        isDrawable = true;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageViewDraw = view.findViewById(R.id.drawView);
        imageViewBack = view.findViewById(R.id.imageViewBack);
        imageViewClear = view.findViewById(R.id.imageViewClear);
        imageViewSetLayer = view.findViewById(R.id.imageViewSetLayer);
        imageViewPlay = view.findViewById(R.id.imageViewPlay);
        gifView = view.findViewById(R.id.gifView);
        gifViewHalf = view.findViewById(R.id.gifViewHalf);
        gifViewLayer = view.findViewById(R.id.gifViewLayer);
        gifViewLayer.setVisibility(View.INVISIBLE);

        int idGifRes = view.getResources().getIdentifier(
                ddKanji.getRomajiKanji(String.valueOf(kanji)) + "_anim",
                "drawable",
                view.getContext().getPackageName()
        );
        if (idGifRes == 0)
            getActivity().finish();
        try {
            gifDrawable = new GifDrawable(view.getResources(), idGifRes);
            gifView.setImageDrawable(gifDrawable);
            gifDrawableHalf = new GifDrawable(view.getResources(), idGifRes);
            gifViewHalf.setImageDrawable(gifDrawableHalf);
            gifDrawableLayer = new GifDrawable(view.getResources(), idGifRes);
            gifViewLayer.setImageDrawable(gifDrawableLayer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageViewBack.setOnClickListener(view1 -> {
            if (currentFeature > 0) {
                currentFeature--;
                if (currentFeature == 0)
                    imageViewBack.setActivated(false);
                changeGifsSeek();
            }
        });
        imageViewClear.setOnClickListener(view12 -> {
            currentFeature = 0;
            imageViewBack.setActivated(false);
            changeGifsSeek();
        });
        imageViewSetLayer.setOnClickListener(view13 -> {
            if (isShowBackKanji) {
                isShowBackKanji = false;
                gifViewLayer.setVisibility(View.INVISIBLE);
                imageViewSetLayer.setImageResource(R.drawable.baseline_layers_24);
            } else {
                isShowBackKanji = true;
                gifViewLayer.setVisibility(View.VISIBLE);
                imageViewSetLayer.setImageResource(R.drawable.baseline_layers_clear_24);
            }
        });
        imageViewPlay.setOnClickListener(view14 -> {
            if (currentFeature < infoFeaturesKanji.countFeatures) {
                gifDrawableHalf.stop();
                if (tControlAnim.isAlive()) {
                    tControlAnim.interrupt();
                }
                Thread.State d = tControlAnim.getState();
                if (tControlAnim.getState() == Thread.State.TERMINATED ||
                        tControlAnim.getState() == Thread.State.BLOCKED)
                    tControlAnim = new ThreadControlAnim();
                gifDrawableHalf.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature][0]);
                tControlAnim.start();
                gifDrawableHalf.start();
            }
        });
    }

    public void setSettings(int[] globalLocation) {
        this.globalLocation = globalLocation;
        imageViewDraw.post(() -> {
            height = imageViewDraw.getHeight();
            width = imageViewDraw.getWidth();

            imageViewDraw.getLocationOnScreen(locationViewDraw);

            scaleSizeSide = (float) width / (float) SIZE_SIDE_PANEL;
            imageViewDraw.getLayoutParams().height = width;
            gifView.getLayoutParams().height = width;
            height = width;
            widthBrush = width / 32;

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            canvas = new Canvas(bitmap);
            Paint h = new Paint();
            //draw dash lines
            for (int i = 0; i < COUNT_DASH_LINES_IN_SIDE; i++)
                if (i == COUNT_DASH_LINES_IN_SIDE / 2)
                    drawDashLine(true, SIZE_SIDE_PANEL / (COUNT_DASH_LINES_IN_SIDE + 1) * (i + 1), 127);
                else
                    drawDashLine(true, SIZE_SIDE_PANEL / (COUNT_DASH_LINES_IN_SIDE + 1) * (i + 1), 63);
            for (int i = 0; i < COUNT_DASH_LINES_IN_SIDE; i++)
                if (i == COUNT_DASH_LINES_IN_SIDE / 2)
                    drawDashLine(false, SIZE_SIDE_PANEL / (COUNT_DASH_LINES_IN_SIDE + 1) * (i + 1), 127);
                else
                    drawDashLine(false, SIZE_SIDE_PANEL / (COUNT_DASH_LINES_IN_SIDE + 1) * (i + 1), 63);
            gifView.getLayoutParams().height = height;
            gifViewHalf.getLayoutParams().height = height;
            gifViewLayer.getLayoutParams().height = height;
            gifDrawable.stop();
            gifDrawable.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature][0]);
            gifDrawableHalf.stop();
            gifDrawableHalf.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature][0]);
            gifDrawableLayer.stop();
            threadsList = new ArrayList<>();
            if (COUNT_PROCESSORS > 2)
                for (int i = 0; i < COUNT_PROCESSORS - 1; i++)
                    threadsList.add(new ThreadDraw(canvas, paint, widthBrush, COUNT_PROCESSORS - 1, i));
            imageViewDraw.setImageBitmap(bitmap);
            tControlAnim = new ThreadControlAnim();
        });
    }

    public boolean isKanjiDrawn() {
        if (currentFeature >= infoFeaturesKanji.countFeatures)
            return true;
        else
            return false;
    }

    public int getCountFeatures() {
        return infoFeaturesKanji.countFeatures;
    }

    private void changeGifsSeek() {
        if (currentFeature < infoFeaturesKanji.countFeatures) {
            gifDrawable.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature][0]);
            gifDrawableHalf.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature][0]);
        }
    }

    private void drawDashLine(boolean type, int padding, int opacity) {
        if (opacity < 0 || opacity > 255)
            return;
        if (padding > SIZE_SIDE_PANEL * scaleSizeSide)
            return;
        Paint mpaint = new Paint();
        mpaint.setARGB(opacity, 0, 0, 0);
        int LENGTH_LINE = 6,
                INDENTATION = 2,
                WIDTH_LINE = 2;
        int countLines = SIZE_SIDE_PANEL / (LENGTH_LINE + INDENTATION);
        if (type) { // false - horizontal line, true - vertical line
            for (int i = 0; i < countLines / 2; i++) {

                canvas.drawRect((padding - WIDTH_LINE / 2) * scaleSizeSide, i * (LENGTH_LINE + INDENTATION) * scaleSizeSide,
                        (padding + WIDTH_LINE / 2) * scaleSizeSide,
                        (i * (LENGTH_LINE + INDENTATION) + LENGTH_LINE) * scaleSizeSide, mpaint);

            }

            for (int i = 0; i < countLines / 2; i++) {

                canvas.drawRect((padding - WIDTH_LINE / 2) * scaleSizeSide, (SIZE_SIDE_PANEL - i * (LENGTH_LINE + INDENTATION)) * scaleSizeSide,
                        (padding + WIDTH_LINE / 2) * scaleSizeSide,
                        (SIZE_SIDE_PANEL - i * (LENGTH_LINE + INDENTATION) - LENGTH_LINE) * scaleSizeSide, mpaint);

            }

            canvas.drawRect((padding - WIDTH_LINE / 2) * scaleSizeSide, (SIZE_SIDE_PANEL / 2 - WIDTH_LINE / 2) * scaleSizeSide,
                    (padding + WIDTH_LINE / 2) * scaleSizeSide,
                    (SIZE_SIDE_PANEL / 2 + WIDTH_LINE / 2) * scaleSizeSide, mpaint);
        } else {
            for (int i = 0; i < countLines / 2; i++) {

                canvas.drawRect(i * (LENGTH_LINE + INDENTATION) * scaleSizeSide, (padding - WIDTH_LINE / 2) * scaleSizeSide,
                        (i * (LENGTH_LINE + INDENTATION) + LENGTH_LINE) * scaleSizeSide,
                        (padding + WIDTH_LINE / 2) * scaleSizeSide, mpaint);

            }

            for (int i = 0; i < countLines / 2; i++) {

                canvas.drawRect((SIZE_SIDE_PANEL - i * (LENGTH_LINE + INDENTATION)) * scaleSizeSide, (padding - WIDTH_LINE / 2) * scaleSizeSide,
                        (SIZE_SIDE_PANEL - i * (LENGTH_LINE + INDENTATION) - LENGTH_LINE) * scaleSizeSide,
                        (padding + WIDTH_LINE / 2) * scaleSizeSide, mpaint);

            }

            canvas.drawRect((SIZE_SIDE_PANEL / 2 - WIDTH_LINE / 2) * scaleSizeSide, (padding + WIDTH_LINE / 2) * scaleSizeSide,
                    (SIZE_SIDE_PANEL / 2 + WIDTH_LINE / 2) * scaleSizeSide,
                    (padding + WIDTH_LINE / 2) * scaleSizeSide, mpaint);

        }

    }

    private boolean eventListener(View view, MotionEvent event) {
        if (currentFeature < infoFeaturesKanji.countFeatures) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isLinesDrawn = new boolean[infoFeaturesKanji.countPointsInFeatures[currentFeature]];
                    previousBitmap = bitmap.copy(bitmap.getConfig(), true);
                    points[0] = event.getX();
                    points[1] = event.getY();
                    BitmapChangeDown();
                    firstPoint[0] = points[0];
                    firstPoint[1] = points[1];
                    return true;
                case MotionEvent.ACTION_MOVE:
                    try {
                        points[2] = event.getX();
                        points[3] = event.getY();
                        if (!isLengthTooLong) {
                            lengthCurrentLineXY[0] += Math.abs(points[2] - locationViewDraw[0] + globalLocation[0] - points[0]) / scaleSizeSide;
                            lengthCurrentLineXY[1] += Math.abs(points[3] - locationViewDraw[1] + globalLocation[1] - points[1]) / scaleSizeSide;
                        }
                        BitmapChangeMove();
                        if (!isLengthTooLong) {
                            if (currentLine < infoFeaturesKanji.countPointsInFeatures[currentFeature]) {
                                if (checkIsCorrectPoint() && checkLengthLine(getLengthFromLengthXY())) {
                                    currentLine++;
                                    if (currentLine < infoFeaturesKanji.countPointsInFeatures[currentFeature]) {
                                        lengthCurrentLineXY[0] = 0f;
                                        lengthCurrentLineXY[1] = 0f;
                                    }
                                }
                            } else if (!checkLengthLine(getLengthFromLengthXY()))
                                isLengthTooLong = true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (!isLengthTooLong && checkIsAllLinesDrawn()) {
                        currentFeature++;
                        gifDrawable.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature - 1][1]);
                    }
                    currentLine = 0;
                    lengthCurrentLineXY[0] = 0f;
                    lengthCurrentLineXY[1] = 0f;
                    isLengthTooLong = false;
                    bitmap = previousBitmap.copy(previousBitmap.getConfig(), true);
                    canvas = new Canvas(bitmap);
                    imageViewDraw.setImageBitmap(bitmap);
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void BitmapChangeDown() {
        if ((int) points[0] >= (locationViewDraw[0] - globalLocation[0]) && points[1] >= (locationViewDraw[1] - globalLocation[1]) && (int) points[0] < (locationViewDraw[0] - globalLocation[0] + width) && (int) points[1] < (locationViewDraw[1] - globalLocation[1] + height)) {
            points[0] -= (float) locationViewDraw[0] - (float) globalLocation[0];
            points[1] -= (float) locationViewDraw[1] - (float) globalLocation[1];
            canvas.drawCircle(points[0], points[1], widthBrush, paint);
            imageViewDraw.setImageBitmap(bitmap);
        } else {
            points[0] = -1.0f;
            points[1] = -1.0f;
        }
    }

    private void BitmapChangeMove() {
        if ((int) points[2] >= (locationViewDraw[0] - globalLocation[0]) && points[3] >= (locationViewDraw[1] - globalLocation[1]) && (int) points[2] < (locationViewDraw[0] - globalLocation[0] + width) && (int) points[3] < (locationViewDraw[1] - globalLocation[1] + height)) {
            points[2] -= (float) locationViewDraw[0] - (float) globalLocation[0];
            points[3] -= (float) locationViewDraw[1] - (float) globalLocation[1];
            drawCircles();
            imageViewDraw.setImageBitmap(bitmap);
            points[0] = points[2];
            points[1] = points[3];
        } else {
            points[0] = points[2];
            points[1] = points[3];
        }
    }

    private void drawCircles() {
        countSteps = (int) Math.sqrt((points[0] - points[2]) * (points[0] - points[2]) + (points[1] - points[3]) * (points[1] - points[3]));
        if (countSteps > 1) {
            steps[0] = Math.abs(points[0] - points[2]) / (float) countSteps;
            steps[1] = Math.abs(points[1] - points[3]) / (float) countSteps;
            if (countSteps >= COUNT_PROCESSORS - 1 && COUNT_PROCESSORS > 2) {
                commonCountPartitionStreams = countSteps / (COUNT_PROCESSORS - 1);
                remainder = countSteps - commonCountPartitionStreams * (COUNT_PROCESSORS - 1);
                for (int i = 0; i < remainder; i++)
                    threadsList.get(i).setSettings(commonCountPartitionStreams + 1, points, steps);
                if (commonCountPartitionStreams > 0)
                    for (int i = remainder; i < COUNT_PROCESSORS - 1; i++)
                        threadsList.get(i).setSettings(commonCountPartitionStreams, points, steps);
                for (int i = 0; i < threadsList.size(); i++)
                    threadsList.get(i).run();
            } else {
                for (int i = 0; i < countSteps; i++) {
                    if (points[0] == points[2])
                        pointSteps[0] = points[0];
                    else {
                        if (points[0] > points[2])
                            pointSteps[0] = points[0] - steps[0] * i;
                        else
                            pointSteps[0] = points[0] + steps[0] * i;
                    }
                    if (points[1] == points[3])
                        pointSteps[1] = points[1];
                    else {
                        if (points[1] > points[3])
                            pointSteps[1] = points[1] - steps[1] * i;
                        else
                            pointSteps[1] = points[1] + steps[1] * i;
                    }
                    canvas.drawCircle(pointSteps[0], pointSteps[1], widthBrush, paint);
                }
            }
        } else
            canvas.drawCircle(points[2], points[3], widthBrush, paint);
    }

    private boolean checkIsCorrectPoint() {
        double lengthLine = getLengthFromLengthXY();
        if ((points[2] - firstPoint[0]) / scaleSizeSide >= infoFeaturesKanji.endPointsOfFeatures[currentLine][0] - lengthLine * infoFeaturesKanji.percentErrorsEndPoints[currentLine][0][0] / 100 &&
                (points[2] - firstPoint[0]) / scaleSizeSide <= infoFeaturesKanji.endPointsOfFeatures[currentLine][0] + lengthLine * infoFeaturesKanji.percentErrorsEndPoints[currentLine][0][1] / 100)
            if ((points[3] - firstPoint[1]) / scaleSizeSide >= infoFeaturesKanji.endPointsOfFeatures[currentLine][1] - lengthLine * infoFeaturesKanji.percentErrorsEndPoints[currentLine][1][0] / 100 &&
                    (points[3] - firstPoint[1]) / scaleSizeSide <= infoFeaturesKanji.endPointsOfFeatures[currentLine][1] + lengthLine * infoFeaturesKanji.percentErrorsEndPoints[currentLine][1][1] / 100) {
                isLinesDrawn[currentLine] = true;
                return true;
            }
        return false;
    }

    private double getLengthFromLengthXY() {
        int thisCurrentLine = currentLine;
        if (currentLine >= infoFeaturesKanji.countPointsInFeatures[currentFeature])
            thisCurrentLine--;
        return Math.sqrt(infoFeaturesKanji.endPointsOfFeatures[thisCurrentLine][0] * infoFeaturesKanji.endPointsOfFeatures[thisCurrentLine][0] +
                infoFeaturesKanji.endPointsOfFeatures[thisCurrentLine][1] * infoFeaturesKanji.endPointsOfFeatures[thisCurrentLine][1]);
    }

    private boolean checkLengthLine(double lengthLine) {
        int thisCurrentLine = currentLine;
        if (currentLine >= infoFeaturesKanji.countPointsInFeatures[currentFeature])
            thisCurrentLine--;
        if (lengthCurrentLineXY[0] <= Math.abs(infoFeaturesKanji.endPointsOfFeatures[thisCurrentLine][0]) + lengthLine * infoFeaturesKanji.percentErrorsEndPoints[thisCurrentLine][0][1] / 100 &&
                lengthCurrentLineXY[1] <= Math.abs(infoFeaturesKanji.endPointsOfFeatures[thisCurrentLine][1]) + lengthLine * infoFeaturesKanji.percentErrorsEndPoints[thisCurrentLine][1][1] / 100)
            return true;
        return false;
    }

    private boolean checkIsAllLinesDrawn() {
        for (int i = 0; i < isLinesDrawn.length; i++)
            if (isLinesDrawn[i] == false)
                return false;
        return true;
    }

    private class ThreadControlAnim extends Thread {
        private final int AREA_ANIM_TIME = 101; //milliseconds

        @Override
        public void run() {
            super.run();
            if (currentFeature < infoFeaturesKanji.countFeatures) {
                while (gifDrawableHalf.getCurrentPosition() < infoFeaturesKanji.timesOfAnim[currentFeature][1] - AREA_ANIM_TIME ||
                        gifDrawableHalf.getCurrentPosition() > infoFeaturesKanji.timesOfAnim[currentFeature][1] + AREA_ANIM_TIME) {
                    try {
                        this.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                gifDrawableHalf.stop();
                gifDrawableHalf.seekTo(infoFeaturesKanji.timesOfAnim[currentFeature][0]);
            }
        }
    }
}