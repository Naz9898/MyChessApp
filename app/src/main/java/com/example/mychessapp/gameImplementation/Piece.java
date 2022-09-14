package com.example.mychessapp.gameImplementation;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public abstract class Piece {
    private Position currentPosition;
    private Game game;
    private boolean isWhite;

    public Piece(Game game, boolean isWhite, int row, int column) {
        this.game = game;
        this.isWhite = isWhite;
        currentPosition = new Position(row, column);
        game.setTile(this, currentPosition);
        game.add(this, isWhite);
    }

    //Return all the cells a piece can reach
    public abstract ArrayList<Position> getMenacingCells();

    //Sub-set of previous list (ex. pinned piece  can't move)
    public  ArrayList<Position> getPossibleMoves(){
        ArrayList<Position> potentialMoves = getMenacingCells();
        ArrayList<Position> possibleMoves = new ArrayList<Position>();
        for(Position p: potentialMoves){
            if( getGame().isMovePossible(this, p) )
                possibleMoves.add(p);
        }
        return possibleMoves;

    }

    protected int isTileAvailable(int row, int column){
        //Out of bounds
        if(row < 0 || row >= 8 || column < 0 || column >= 8)
            return -1;
        //Check if it' own player tile
        if(isWhite() && getGame().checkTile(row, column) == 1) {
            return -1;
        }
        if(!isWhite() && getGame().checkTile(row, column) == -1)
            return -1;
        //Check if it's opponent piece
        if(isWhite() && getGame().checkTile(row, column) == -1) {
            return 1;
        }
        if(!isWhite() && getGame().checkTile(row, column) == 1)
            return 1;
        //Free tile
        return 0;
    }

    public abstract int getIconId();

    //Getters
    public Position getCurrentPosition() {
        return currentPosition;
    }
    public Game getGame() {
        return game;
    }
    public boolean isWhite() {
        return isWhite;
    }
    public String toString(){
        return getClass().getSimpleName() + " " + getCurrentPosition().toString() + "\n";
    }

    //Setters
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }
}
