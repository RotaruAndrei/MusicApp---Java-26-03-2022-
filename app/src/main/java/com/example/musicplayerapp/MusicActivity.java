package com.example.musicplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;


public class MusicActivity extends AppCompatActivity {

    private MaterialButton skip_previous, skip_next, pause;
    private TextView songTitle, startTime, progressTime;
    private SeekBar topBar, bottomBar;

    private String title, filePath;
    private int  filePosition;
    private ArrayList<String> songsList;
    private MediaPlayer mediaPlayer;

    private Runnable runnable;
    private Handler handler;
    private int totalTime;

    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initUI();

        // receive the intent information from the adapter
        Intent intent = getIntent();
        if (intent != null){

            title = intent.getStringExtra("songTitle");
            filePath = intent.getStringExtra("filePath");
            filePosition = intent.getIntExtra("position",-1);
            songsList = intent.getStringArrayListExtra("songsList");

        }

        songTitle.setText(title);
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        animation = AnimationUtils.loadAnimation(this,R.anim.animation_song_title);
        songTitle.setAnimation(animation);


        skip_previous.setOnClickListener(onClick ->{
            // reset the media player so the app will not crash
            mediaPlayer.reset();
            //if the user tries to change the song and is the first song
            // last song of the list will be played
            // otherwise will go to previous song
            if (filePosition == 0){
                filePosition = songsList.size() - 1;
            }else {
                filePosition--;
            }

            // the previous song position
            String newPath = songsList.get(filePosition);

            try {

                mediaPlayer.setDataSource(newPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                // change the play icon to pause
                // the song will start playing when previous button has been click
                pause.setBackgroundResource(R.drawable.ic_pause_ic);

                // change the song title
                songTitle.setText(newPath.substring(newPath.lastIndexOf("/") + 1));
                songTitle.clearAnimation();
                songTitle.startAnimation(animation);

            } catch (IOException e) {
                e.printStackTrace();
            }



        });

        pause.setOnClickListener(onClick ->{

            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                pause.setBackgroundResource(R.drawable.ic_play_icon);
            }else {
                mediaPlayer.start();
                pause.setBackgroundResource(R.drawable.ic_pause_ic);
            }
        });

        skip_next.setOnClickListener(onClick ->{

            // reset the media player so the app will not crash
            mediaPlayer.reset();
            //if the user tries to change the song and is the last in the list
            // first song will be played
            // otherwise will go to previous song
            if (filePosition == songsList.size() - 1){
                filePosition = 0;
            }else {
                filePosition++;
            }

            // the previous song position
            String newPath = songsList.get(filePosition);

            try {

                mediaPlayer.setDataSource(newPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                // change the play icon to pause
                // the song will start playing when previous button has been click
                pause.setBackgroundResource(R.drawable.ic_pause_ic);

                // change the song title
                songTitle.setText(newPath.substring(newPath.lastIndexOf("/") + 1));
                songTitle.clearAnimation();
                songTitle.startAnimation(animation);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //volume seek bar
        topBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //if the user made some changes to the seek bar
                if (b){
                    //then set the progress to the seek bar
                    //the location of the seek bar will be updated
                    seekBar.setProgress(i);
                    //divide the progress of the seek bar of 100%
                    //and use it to mediaplayer volume level
                    float volumeLevel = i /100f;
                    //then set the volume from media player
                    mediaPlayer.setVolume(volumeLevel,volumeLevel);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //song bar progress
        bottomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //if the user made changes update the seek bar
                // on media player and the seek bar object
                if (b){

                    mediaPlayer.seekTo(i);
                    bottomBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // runnable that will handle the media player when the user does not make any changes
        handler  = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                totalTime = mediaPlayer.getDuration();
                bottomBar.setMax(totalTime);

                int currentPosition  = mediaPlayer.getCurrentPosition();
                bottomBar.setProgress(currentPosition);
                handler.postDelayed(runnable,1000);

                String elapseTime = setTimer(currentPosition);
                String endTime = setTimer(totalTime);

                startTime.setText(elapseTime);
                progressTime.setText(endTime);

                if (elapseTime.equals(endTime)){
                    // reset the media player so the app will not crash
                    mediaPlayer.reset();
                    //if the user tries to change the song and is the last in the list
                    // first song will be played
                    // otherwise will go to previous song
                    if (filePosition == songsList.size() - 1){
                        filePosition = 0;
                    }else {
                        filePosition++;
                    }

                    // the previous song position
                    String newPath = songsList.get(filePosition);

                    try {

                        mediaPlayer.setDataSource(newPath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        // change the play icon to pause
                        // the song will start playing when previous button has been click
                        pause.setBackgroundResource(R.drawable.ic_pause_ic);

                        // change the song title
                        songTitle.setText(newPath.substring(newPath.lastIndexOf("/") + 1));
                        songTitle.clearAnimation();
                        songTitle.startAnimation(animation);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        };

        handler.post(runnable);
    }

    private String setTimer (int currentPosition) {

        // 1m = 60 sec
        // 1sec = 1000 ms;

        String timeLabel;
        int min, sec;

        // current times comes in milisec
        // divide by sec then by min
        min = currentPosition / 1000/ 60;
        sec = currentPosition /1000 % 60;

         if (sec < 10){
             timeLabel = min + ":0"+ sec;
         }else {
             timeLabel = min + ":" + sec;
         }

         return timeLabel;

    }

    //init ui elements
    private void initUI(){
        skip_previous = findViewById(R.id.music_skip_previous_btn);
        skip_next     = findViewById(R.id.music_forward_skip_btn);
        pause         = findViewById(R.id.music_pause_btn);
        songTitle     = findViewById(R.id.music_song_title);
        startTime     = findViewById(R.id.music_start_time_tv);
        progressTime  = findViewById(R.id.music_end_time_tv);
        topBar        = findViewById(R.id.music_seekBar_top);
        bottomBar     = findViewById(R.id.music_seekBar_bottom);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }
}