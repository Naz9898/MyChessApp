package com.example.mychessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.mychessapp.gameGUI.GameGUI;
import com.example.mychessapp.gameGUI.GameGUIabstract;
import com.example.mychessapp.gameGUI.GameGUIonline;
import com.example.mychessapp.gameImplementation.Game;
import com.example.mychessapp.gameImplementation.Piece;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private String TAG = GameActivity.class.getSimpleName();
    private String sharedPrefFile = "com.example.android.mysharedprefs";
    private SharedPreferences mPreferences;

    private GameGUIabstract gameGUI;

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set user preferences
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        setUserPreferences();

        //Create or restore game
        ConstraintLayout layout = findViewById(R.id.constraintLayout);

        Bundle extras = getIntent().getExtras();
        //Setup game depending: New, Resume, or Online
        if( extras != null ){
            if(extras.containsKey("online")){
                //Start online game
                gameGUI = new GameGUIonline(layout);
                return;
            }
            else{
                //Resume offline game
                Log.d(TAG, "Restoring last game");
                //Restoring game
                String gameStatus = extras.getString("gameStatus");
                String[] params = gameStatus.split("\n", 4);
                //game = new Game( params[3] );
                int whiteTime = Integer.parseInt(params[0]);
                int blackTime = Integer.parseInt(params[1]);
                boolean isWhite = params[2].equals("true");

                gameGUI = new GameGUI(params[3], layout, whiteTime, blackTime, isWhite);
            }

        }
        else {
            Log.d(TAG, "Creating new game");
            //new game
            gameGUI = new GameGUI(layout);
        }

    }

    private void setUserPreferences() {
        if( mPreferences.getBoolean("darkMode", false) ) setContentView(R.layout.activity_game_dark);
        else setContentView(R.layout.activity_game);
        //Initialize variables
        tableLayout = findViewById(R.id.chessBoard);
        //Set preferences
        boolean alternativeColor = mPreferences.getInt("grid", 0) == 1;
        if(alternativeColor) setGridColor();
        //Set username
        String username = mPreferences.getString("username", "Guest");
        ((TextView) findViewById(R.id.textViewUsernameWhite)).setText(username);
    }

    private ImageView getImageView(int row, int column){
        return (ImageView) ((TableRow) tableLayout.getChildAt(row)).getChildAt(column);
    }

    private void setGridColor(){
        int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
        for(int i = 0; i<8; ++i)
            for(int j = 0; j<8; ++j)
                if((i + j) % 2 == 1) getImageView(i,j).setImageDrawable(new ColorDrawable(color));
    }

    private void saveGameToFile() throws IOException {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, "savedGame.txt");
        FileOutputStream stream = new FileOutputStream(file);
        try {
            //White time
            String time = ((GameGUI)gameGUI).getWhiteTimer().getSeconds() + "\n";
            stream.write(time.getBytes());
            //Black time
            time = ((GameGUI)gameGUI).getBlackTimer().getSeconds() + "\n";
            stream.write(time.getBytes());
            //Player color
            if(gameGUI.isWhite()) stream.write("true\n".getBytes());
            else stream.write("false\n".getBytes());
            //Turn
            if(gameGUI.getGame().isWhiteMove()) stream.write("true\n".getBytes());
            else stream.write("false\n".getBytes());
            //Write white pieces
            ArrayList<Piece> pieceSet = gameGUI.getGame().getWhitePieces();
            for(Piece p: pieceSet)
                stream.write(p.toString().getBytes());
            //Write black pieces
            stream.write("Black\n".getBytes());
            pieceSet = gameGUI.getGame().getBlackPieces();
            for(Piece p: pieceSet)
                stream.write(p.toString().getBytes());
        } finally {
            stream.close();
        }
    }

    private void deleteGameFile() throws IOException {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, "savedGame.txt");
        file.delete();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //If online resigned if there is game, delete otherwise
        if(gameGUI instanceof  GameGUIonline ){
            Log.d(TAG, "closing online game");
            if(gameGUI.getGame() == null){ //Nobody joined, delete game
                ((GameGUIonline) gameGUI).deleteGame();
                return;
            }
            Log.d(TAG, "closing online game");
            if(gameGUI.getGame().getGamestatus() == 0) resignClick(null);
            return;
        }
        //If CPU game save if it's ongoing
        try {
            if(gameGUI.getGame().getGamestatus() == 0) saveGameToFile();
            else deleteGameFile();
            //Log.d(TAG, "File saved!");
        } catch (IOException e) {
            Log.d(TAG, "Couldn't save file");
            e.printStackTrace();
        }

        ((GameGUI)gameGUI).getWhiteTimer().cancel(true);
        ((GameGUI)gameGUI).getBlackTimer().cancel(true);
    }

    public void resignClick(View v){
        if(gameGUI instanceof  GameGUIonline && gameGUI.getGame() == null) return;
        if(gameGUI.getGame().getGamestatus() == 0) {

            if (gameGUI.isWhite()) gameGUI.getGame().setGamestatus(-1);
            else gameGUI.getGame().setGamestatus(1);
            if(gameGUI instanceof GameGUIonline) ((GameGUIonline) gameGUI).notifyResigned();
            gameGUI.notifyGameStatus();
        }
    }

}