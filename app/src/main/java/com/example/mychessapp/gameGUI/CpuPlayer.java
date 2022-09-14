package com.example.mychessapp.gameGUI;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mychessapp.gameImplementation.Piece;
import com.example.mychessapp.gameImplementation.Position;

import java.util.ArrayList;
import java.util.Random;

public class CpuPlayer extends Player {
    String TAG  = CpuPlayer.class.getSimpleName();
    private GameGUI gameGUI;


    public CpuPlayer(GameGUI gameGUI, boolean isWhite) {
        super(isWhite);
        this.gameGUI = gameGUI;
    }

    public void move(){
        MakeMoveTask task = new MakeMoveTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public String getName() {
        return "CPU";
    }

    public ArrayList<Piece> getPieceSet(){
        if(isWhite()) return getGameGUI().getGame().getWhitePieces();
        else return getGameGUI().getGame().getBlackPieces();
    }

    //Getters
    public GameGUI getGameGUI() {
        return gameGUI;
    }
}

//Async task
 class MakeMoveTask extends AsyncTask<Void, Void, Void> {
    String TAG  = CpuPlayer.class.getSimpleName();

    public CpuPlayer player;

    public MakeMoveTask(CpuPlayer player){
        super();
        this.player = player;
    }

     @Override
     protected Void doInBackground(Void... voids) {
         try {
             Thread.sleep(5000);
             if(player.getGameGUI().getGame().getGamestatus() != 0) cancel(true);
         } catch (InterruptedException e) {
             Log.d(TAG, "Game over, no move");
         }
         return null;
     }

     @Override
     protected void onPostExecute(Void aVoid) {
         //Make move
         Piece piece;
         Position pos;
         ArrayList<Piece> pieceSet = player.getPieceSet();
         ArrayList<Piece> possiblePiece = new ArrayList<Piece>();
         for(Piece p: pieceSet){
             if(p.getPossibleMoves().size() > 0)
                 possiblePiece.add(p);
         }
         //Get random piece and position
         int randomIdx = new Random().nextInt(possiblePiece.size());
         piece = possiblePiece.get(randomIdx);
         randomIdx = new Random().nextInt(piece.getPossibleMoves().size());
         pos = piece.getPossibleMoves().get(randomIdx);

         player.getGameGUI().directMove(piece, pos);
     }
 }

