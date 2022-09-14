package com.example.mychessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychessapp.gameImplementation.Game;

public class SettingsActivity extends AppCompatActivity {
    private static String TAG = Game.class.getSimpleName();
    private String sharedPrefFile = "com.example.android.mysharedprefs";
    private SharedPreferences mPreferences;

    //#181E42
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        initializeState();
    }

    public void initializeState(){
        //Set correct layout
        boolean darkMode = mPreferences.getBoolean("darkMode", false);
        if(darkMode) setContentView(R.layout.activity_settings_dark);
        else setContentView(R.layout.activity_settings);
        //Set dark mode switch
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        //Set username
        String username = mPreferences.getString("username", "");
        if(username.length() > 0)
            ((TextView)findViewById(R.id.textViewUsername)).setText(username);
        switchDarkMode.setChecked(darkMode);
        //Set grid color
        if(mPreferences.getInt("grid", 0) == 1)
            ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radioButton_lightblue);
        else ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radioButton_default);
    }

    public void applyChanges(View v){
        //Save changes if possible
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        //Username
        TextView usernameTextView = findViewById(R.id.textViewUsername);
        String username = usernameTextView.getText().toString();
        if(username.length() == 0) preferencesEditor.remove("username");
        else preferencesEditor.putString("username", username);
        //Dark mode
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        boolean darkMode = switchDarkMode.isChecked();
        preferencesEditor.putBoolean("darkMode", darkMode);
        //Grid color
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int index = radioGroup.indexOfChild( findViewById(radioGroup.getCheckedRadioButtonId() ) );
        preferencesEditor.putInt("grid", index);
        preferencesEditor.apply();
        initializeState();
        Toast.makeText(getApplicationContext(), "Changes applied!", Toast.LENGTH_SHORT).show();
    }

}