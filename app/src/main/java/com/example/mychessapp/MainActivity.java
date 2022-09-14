package com.example.mychessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    private String sharedPrefFile = "com.example.android.mysharedprefs";
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set correct layout
        if( mPreferences.getBoolean("darkMode", false) ) setContentView(R.layout.activity_main_dark);
        else setContentView(R.layout.activity_main);
    }

    public String readFile() throws IOException {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, "savedGame.txt");

        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = new FileInputStream(file);
        try {
            in.read(bytes);
        } finally {
            in.close();
        }
        return new String(bytes);
    }

    public void openCpuGameActivity(View v){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void openOnlineGameActivity(View v){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("online", true);
        startActivity(intent);
    }

    public void resumeGameActivity(View v){

        try {
            String gameStatus = readFile();
            //Log.d(TAG, gameStatus);
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("gameStatus", gameStatus);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "No game to resume" ,Toast.LENGTH_SHORT).show();
        }

    }

    public void openSettingsActivity(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}