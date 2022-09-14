package com.example.mychessapp.gameGUI;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mychessapp.R;
import com.example.mychessapp.gameImplementation.Piece;
import com.example.mychessapp.gameImplementation.Position;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OnlinePlayer extends Player{
    String TAG = OnlinePlayer.class.getSimpleName();

    String name;
    String gameKey;
    GameGUIonline gameGUI;
    boolean isWhiteMove;    //Needed since variables are not synchonized
    private DatabaseReference mDatabase;

    public OnlinePlayer(boolean isWhite, String gameKey, GameGUIonline gameGUI) {
        super(isWhite);
        this.name = "Guest";
        this.gameKey = gameKey;
        this.gameGUI = gameGUI;
        this.isWhiteMove = true;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(!isWhite() ) waitOpponent();
        else getOpponent();
    }

    private void waitOpponent(){
        Log.d(TAG, "Waiting opponent");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if(dataSnapshot.getKey().equals("black")){
                    name =  dataSnapshot.getValue(String.class);
                    gameGUI.setNames();
                    gameGUI.startGame();
                    move();
                    Log.d(TAG, name + " joined - previous: " + previousChildName);
                }

            }
            //Not supposed to happen
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        mDatabase.child("Games").child(gameKey).addChildEventListener(childEventListener);
    }

    private void getOpponent(){
        Log.d(TAG, "Getting opponent");
        DatabaseReference gameRef = mDatabase.child("Games").child(gameKey);
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG,  dataSnapshot.getKey() +"reading " + dataSnapshot.getChildrenCount());
                DataSnapshot whiteRef = dataSnapshot.child("white");
                name = whiteRef.getValue(String.class);
                gameGUI.setNames();
                gameGUI.startGame();
                move();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Can't find game: databaseError", databaseError.toException());
            }
        });

    }

    @Override
    public void move() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String move =  dataSnapshot.getValue(String.class);
                String[] indexes = move.split("\\s");
                if(indexes.length == 1){
                    int gameStatus = Integer.parseInt(indexes[0]);
                    gameGUI.getGame().setGamestatus(gameStatus);
                    gameGUI.notifyGameStatus();
                    return;
                }
                if(isWhite() == isWhiteMove){
                    //Move
                    Log.d(TAG, "Opponent move (me), moving ");
                    int fromRow = Integer.parseInt(indexes[0]);
                    int fromCol = Integer.parseInt(indexes[1]);
                    Log.d(TAG, fromRow + " " + fromCol);
                    Piece pieceToMove = gameGUI.getGame().getGameBoard()[fromRow][fromCol];
                    int toRow = Integer.parseInt(indexes[2]);
                    int toCol = Integer.parseInt(indexes[3]);
                    gameGUI.directMove(pieceToMove, new Position(toRow, toCol));
                } else Log.d(TAG, "Player move, ignore ");

                isWhiteMove = !isWhiteMove;
            }
            //Not supposed to happen
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        mDatabase.child("Games").child(gameKey).child("Moves").addChildEventListener(childEventListener);

    }

    @Override
    public String getName() {
        return name;
    }
}
