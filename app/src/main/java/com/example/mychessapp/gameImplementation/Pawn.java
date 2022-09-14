package com.example.mychessapp.gameImplementation;

import com.example.mychessapp.R;

import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(Game game, boolean isWhite, int row, int column) {
        super(game, isWhite, row, column);
    }

    @Override
    public ArrayList<Position> getPossibleMoves() {
        ArrayList<Position> potentialMoves = new ArrayList<Position>();
        int direction = isWhite()? -1 : 1;
        int row = getCurrentPosition().getRow();
        int column = getCurrentPosition().getColumn();

        //Move single
        if( isTileAvailable(row + direction, column) == 0) {
            potentialMoves.add(new Position(row + direction, column));
            //Initial double move
            if( (isWhite() && getCurrentPosition().getRow() == 6) || (!isWhite() && getCurrentPosition().getRow() == 1))
                if(isTileAvailable(row + direction*2, column) == 0)
                potentialMoves.add(new Position(row + direction*2, column));
        }
        //Capture moves
        if( isTileAvailable(row + direction, column+1) == 1 )
            potentialMoves.add( new Position(row + direction, column+1) );

        if( isTileAvailable(row + direction, column-1) == 1 ){
            potentialMoves.add( new Position(row + direction, column-1) );
        }

        ArrayList<Position> possibleMoves = new ArrayList<Position>();
        for(Position p: potentialMoves){
            if( getGame().isMovePossible(this, p) )
                possibleMoves.add(p);
        }
        return possibleMoves;
    }

    @Override
    public ArrayList<Position> getMenacingCells() {
        ArrayList<Position> menacingCells = new ArrayList<Position>();
        int direction = isWhite()? -1 : 1;
        int row = getCurrentPosition().getRow();
        int column = getCurrentPosition().getColumn();
        if( isTileAvailable(row + direction, column+1) == 1 )
            menacingCells.add( new Position(row + direction, column+1) );

        if( isTileAvailable(row + direction, column-1) == 1 )
            menacingCells.add( new Position(row + direction, column-1) );
        return menacingCells;
    }

    @Override
    public int getIconId() {
        if( isWhite() ) return R.drawable.pawnw;
        else return R.drawable.pawnb;
    }

}
