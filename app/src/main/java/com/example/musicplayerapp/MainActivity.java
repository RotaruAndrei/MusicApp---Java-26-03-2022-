package com.example.musicplayerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath()+"/Ringtones";
    private static final String NEW_PATH = "/storage/emulated/0/";
    private ArrayList<String> songList = new ArrayList<>();
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new MusicAdapter(this);
        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {

            getAudioFiles();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getAudioFiles();
        }
    }

    private void getAudioFiles() {

        if (NEW_PATH != null){

            File mainFile   = new File(NEW_PATH);
            File[] fileList = mainFile.listFiles();

            for (File s: fileList){

                if (s.isDirectory()){

                    scanFolder(s);

                }else {

                    String path = s.getAbsolutePath();
                    if (path.endsWith(".wav")){
                        songList.add(path);
                        adapter.notifyDataSetChanged();

                    }
                }
            }

        }else {
            Toast.makeText(this, "Path is empty", Toast.LENGTH_SHORT).show();
        }
        adapter.setMusicList(songList);
        recyclerView.setAdapter(adapter);
    }

    private void scanFolder(File folder) {

        if (folder != null){

            File[] directoryFiles = folder.listFiles();

            if (directoryFiles != null) {

                for (File sj: directoryFiles){

                    if (sj.isDirectory()){

                        scanFolder(sj);

                    }else {

                        String path = sj.getAbsolutePath();

                        if (path.endsWith(".wav")){

                            songList.add(path);
                            adapter.notifyDataSetChanged();

                        }

                    }
                }
            }

        }
    }

}