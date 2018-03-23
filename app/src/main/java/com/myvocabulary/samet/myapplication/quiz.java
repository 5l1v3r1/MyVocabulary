package com.myvocabulary.samet.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class quiz extends AppCompatActivity {

    List<String> selected = new ArrayList<>();
    Random random = new Random();
    Database db;
    int sonSecimIndex;
    int points = 0;
    LinearLayout ll;
    RelativeLayout rl;
    Handler handler;


    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            Word answer = db.retrieveData(selected.get(selected.size()-1));

            if (b.getText().equals(answer.getEquivalent())){
                points += 10;

                for (int i = 0; i < ll.getChildCount(); i++){
                    if (ll.getChildAt(i) instanceof Button){
                        b = (Button) ll.getChildAt(i);
                        b.setEnabled(false);
                    }
                }

                dogruCevap();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextQuestion();
                    }
                },1500);


            }
            else{
                points -= 10;
                b.setEnabled(false);
                yanlisCevap();
            }
            writePoint();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        rl = (RelativeLayout) findViewById(R.id.rl);
        ll = (LinearLayout) findViewById(R.id.mainContainer);
        handler = new Handler();


        for (int i = 0; i < ll.getChildCount(); i++){
            if (ll.getChildAt(i) instanceof Button){
                Button b = (Button) ll.getChildAt(i);
                b.setOnClickListener(buttonListener);
            }
        }

        writePoint();
        yaziAnimasyonu();
        enableControlAnimation();
        choiceQuestion();
        fillChoices();


    }
    public void enableControlAnimation(){
        for (int i = 0; i < ll.getChildCount(); i++){
            if (ll.getChildAt(i) instanceof Button){
                Button b = (Button) ll.getChildAt(i);
                b.setEnabled(false);
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enableAllButton();
            }
        },500);
    }


    public void writePoint(){
        TextView pointTV = (TextView) findViewById(R.id.point_tv);
        pointTV.setText(" Points: " + points);
    }
    public void writeQuestionNumber(){
        TextView progressTV = (TextView) findViewById(R.id.progress_tv);
        progressTV.setText(" Question: " + selected.size() +"/"+MainActivity.wordList.size());
    }

    public void enableAllButton(){
        for (int i = 0; i < ll.getChildCount(); i++){
            if (ll.getChildAt(i) instanceof Button){
                Button b = (Button) ll.getChildAt(i);
                b.setEnabled(true);
            }
        }
    }


    public void nextQuestion(){

        if (selected.size() != MainActivity.wordList.size()){
            choiceQuestion();
            fillChoices();
            enableAllButton();
            yaziAnimasyonu();
            enableControlAnimation();
        }
        else {
            result();
        }

    }


    public void result(){
        double successRate = 0;
        if (points > 0){
            successRate = 100 * points / (MainActivity.wordList.size() * 10);
        }
        new AlertDialog.Builder(quiz.this)
                .setTitle("Result of Test")
                .setMessage("Your success's ratio: " + successRate)
                .setPositiveButton("Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Back To Main Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(quiz.this, MainActivity.class));
                        dialog.cancel();
                    }
                }).show();

    }


    public void resetQuiz(){
        selected.clear();
        points = 0;
        enableAllButton();
        yaziAnimasyonu();
        writePoint();
        choiceQuestion();
        fillChoices();
    }

    public boolean kelimeTekrari(String selectedWord){
        for (int i = 0; i < selected.size(); i++){
            if (selected.get(i).equals(selectedWord)){
                return true;
            }
        }
        return false;
    }
    public void choiceQuestion(){

        TextView question_tv = (TextView) findViewById(R.id.question_tv);
        question_tv.setTextColor(Color.BLACK);
        question_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        sonSecimIndex = random.nextInt(MainActivity.wordList.size());
        String selectedWord = MainActivity.wordList.get(sonSecimIndex);

        while (kelimeTekrari(selectedWord)){
            sonSecimIndex = random.nextInt(MainActivity.wordList.size());
            selectedWord = MainActivity.wordList.get(sonSecimIndex);
        }

        question_tv.setText(selectedWord);
        selected.add(selectedWord);
        writeQuestionNumber();

    }
    public void fillChoices(){

        String selectedIndexs = "";
        LinearLayout quizActivity = (LinearLayout) findViewById(R.id.mainContainer);

        int dogruCevapIndex = random.nextInt(4)+3;

        selectedIndexs += sonSecimIndex;

        for (int i = 0; i < quizActivity.getChildCount(); i++){

            if (quizActivity.getChildAt(i) instanceof Button){
                Button b = (Button) quizActivity.getChildAt(i);
                db = new Database(getApplicationContext());

                if (dogruCevapIndex == i){
                    Word answer = db.retrieveData(selected.get(selected.size()-1));
                    b.setText(answer.getEquivalent());
                }
                else {

                    int newIndex = random.nextInt(MainActivity.wordList.size());

                    while (selectedIndexs.indexOf(String.valueOf(newIndex)) != -1){
                        newIndex = random.nextInt(MainActivity.wordList.size());
                    }

                    selectedIndexs += newIndex;
                    b.setText(db.retrieveData(MainActivity.wordList.get(newIndex)).getEquivalent());

                }
            }
        }
    }

    public void yanlisCevap(){
        TextView question_tv = (TextView) findViewById(R.id.question_tv);
        YoYo.with(Techniques.Shake)
                .duration(1500)
                .playOn(question_tv);
    }
    public void dogruCevap(){
        TextView question_tv = (TextView) findViewById(R.id.question_tv);
        question_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.colorCorret));
        question_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
        question_tv.setText("CORRECT!");
        YoYo.with(Techniques.BounceInDown)
                .duration(1000)
                .playOn(question_tv);

    }

    public void yaziAnimasyonu(){
        TextView question_tv = (TextView) findViewById(R.id.question_tv);
        YoYo.with(Techniques.ZoomIn)
                .duration(1000)
                .playOn(question_tv);
    }

    public void buttonControl(View v){
        switch (v.getId()){
            case R.id.backToMainMenu:
                new AlertDialog.Builder(quiz.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(quiz.this, MainActivity.class));
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        }).show();
                break;
            case R.id.resetQuiz:
                resetQuiz();
                break;
            case R.id.finishTest:
                result();
                break;
        }
    }

}