package com.example.mychessapp.gameGUI;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.mychessapp.gameImplementation.Game;

public class TimerAsyncTask extends AsyncTask<Void, Void, Void> {
    private static String TAG = Game.class.getSimpleName();

    private int seconds;
    private boolean isWhite;
    private GameGUI gameGUI;
    private TextView textView;

    public TimerAsyncTask(boolean isWhite, GameGUI gameGUI, TextView textView, int seconds){
        this.seconds = seconds;
        this.isWhite = isWhite;
        this.gameGUI = gameGUI;
        this.textView = textView;
        if(isWhite);
    }
    @Override
    protected Void doInBackground(Void... voids) {
        while(seconds > 0 && gameGUI.getGame().getGamestatus() == 0){
            if ( isCancelled() ) return null;

            if(isWhite == gameGUI.getGame().isWhiteMove()){
                seconds--;
                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d(TAG, "Clock interrupted");
                }
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(seconds == 0) {
            if (isWhite) gameGUI.getGame().setGamestatus(-1);
            else gameGUI.getGame().setGamestatus(1);
            gameGUI.notifyGameStatus();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        textView.setText( toTime() );
    }

    private String toTime(){
        int minutes = seconds/60;
        return "" + minutes +  ":" + seconds%60;
    }

    public int getSeconds() {
        return seconds;
    }
}
