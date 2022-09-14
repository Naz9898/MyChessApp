package com.example.mychessapp.gameGUI;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mychessapp.R;
import com.example.mychessapp.gameImplementation.Game;
import com.example.mychessapp.gameImplementation.Position;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GameGUIonline extends GameGUIabstract {

    String TAG = GameGUIonline.class.getSimpleName();

    private DatabaseReference mDatabase;
    private String gameKey;

    public GameGUIonline(ConstraintLayout layout) {
        super(layout);
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        String username = ((TextView)layout.findViewById(R.id.textViewUsernameWhite)).getText().toString();
        ((TextView)layout.findViewById(R.id.textViewTimerWhite)).setVisibility(View.INVISIBLE);
        ((TextView)layout.findViewById(R.id.textViewTimerBlack)).setVisibility(View.INVISIBLE);

        findGame(username);
    }

    @Override
    protected void notifyOpponentMove(Position oldPos, Position newPos) {
        //write move
        DatabaseReference  moves = mDatabase.child("Games").child( getGameKey() ).child("Moves");
        String key = moves.push().getKey();
        moves.child(key).setValue( oldPos.toString() + " " + newPos.toString() );
        return;
    }

    public void startGame(){
        setGame(new Game());
        setListeners();
        drawAll();

        Toast.makeText(getLayout().getContext(), "Game started", Toast.LENGTH_SHORT).show();
    }

    private void createOnlineGame(String username){
        Log.d(TAG, "Creating online game");
        gameKey = mDatabase.child("Games").push().getKey();
        DatabaseReference gameRef = mDatabase.child("Games").child(gameKey);
        gameRef.child("white").setValue(username);

        setWhite(true);
        setOpponent( new OnlinePlayer(false, gameKey, this ) );
    }

    private void joinOnlineGame(String username){
        Log.d(TAG, "Joining game");
        mDatabase.child("Games").child( getGameKey() ).child("black").setValue(username);

        setWhite(false);
        setOpponent(new OnlinePlayer(true, gameKey, this));
    }

    private void findGame(String username){
        Log.d(TAG, "Check if we have to create game or not");
        Query availableGames = mDatabase.child("Games").orderByChild("black").equalTo(null);
        availableGames.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG,  dataSnapshot.getKey() +"reading " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount() == 0) createOnlineGame(username);
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    setGameKey(d.getKey());
                    joinOnlineGame(username);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Can't find game: databaseError", databaseError.toException());
            }
        });
    }

    public void notifyResigned(){
        DatabaseReference  moves = mDatabase.child("Games").child( getGameKey() ).child("Moves");
        String key = moves.push().getKey();
        int gameStatus = isWhite()?-1:1;
        moves.child(key).setValue("" + gameStatus);
        return;
    }

    public void deleteGame(){
        mDatabase.child("Games").child( getGameKey() ).setValue(null);
        return;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }
}
