package com.example.mychessapp.gameGUI;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mychessapp.R;
import com.example.mychessapp.gameImplementation.Game;
import com.example.mychessapp.gameImplementation.Piece;
import com.example.mychessapp.gameImplementation.Position;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameGUI extends GameGUIabstract {

    private TimerAsyncTask whiteTimer;
    private TimerAsyncTask blackTimer;

    private FileOutputStream stream;
    //New game
    public GameGUI(ConstraintLayout layout) {
        //Log.d(TAG, "New game constructor");
        super(layout);
        setGame( new Game() );
        setTimer(layout, 60, 60);
        //Set player color
        setWhite(new Random().nextInt(2) == 0);
        openMovesFile();
        prepareCPU();
    }

    //Resume game
    public GameGUI(String gameString, ConstraintLayout layout, int whiteTime, int blackTime, boolean isWhite) {
        //Log.d(TAG, "Restore constructor");
        super(layout);
        setGame(new Game(gameString));
        setTimer(layout, whiteTime, blackTime);
        //Set player color
        setWhite(isWhite);
        prepareCPU();
    }

    private void prepareCPU() {
        setOpponent(new CpuPlayer(this, !isWhite()));
        setNames();
        setListeners();
        drawAll();
        if (getGame().isWhiteMove() == getOpponent().isWhite())
            getOpponent().move();
    }

    private void setTimer(ConstraintLayout layout, int whiteTime, int blackTime) {
        TextView timerWhite = layout.findViewById(R.id.textViewTimerWhite);
        TextView timerBlack = layout.findViewById(R.id.textViewTimerBlack);
        this.whiteTimer = new TimerAsyncTask(true,  this, timerWhite, whiteTime);
        this.blackTimer = new TimerAsyncTask(false, this, timerBlack, blackTime);
        whiteTimer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        blackTimer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public TimerAsyncTask getWhiteTimer() {
        return whiteTimer;
    }

    public TimerAsyncTask getBlackTimer() {
        return blackTimer;
    }

    @Override
    protected void notifyOpponentMove(Position oldPos, Position newPos) {
        if(getGame().getGamestatus() == 0) getOpponent().move();
    }

    private void openMovesFile(){
        File path = getChessboard().getContext().getFilesDir();
        File file = new File(path, "moves.txt");
        try {
            stream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "No saved game");
        }
    }

    @Override
    public void directMove(Piece piece, Position pos) {
        Piece captured = getGame().getGameBoard()[pos.getRow()][pos.getColumn()];
        super.directMove(piece, pos);
    }
}
