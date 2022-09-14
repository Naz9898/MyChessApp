package com.example.mychessapp.gameImplementation;

import com.example.mychessapp.R;

import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(Game game, boolean isWhite, int row, int column) {
        super(game, isWhite, row, column);
    }

    @Override
    public ArrayList<Position> getMenacingCells() {
        Position p = getCurrentPosition();
        int row = p.getRow();
        int column = p.getColumn();

        ArrayList<Position> possibleMoves = new ArrayList<Position>();
        //Up-right
        if( isTileAvailable(row-2, column+1) > -1)
            possibleMoves.add(new Position(row-2, column+1));
        //Up-left
        if( isTileAvailable(row-2, column-1) > -1)
            possibleMoves.add( new Position(row-2, column-1) );
        //Right-up
        if( isTileAvailable(row-1, column+2) > -1)
            possibleMoves.add( new Position(row-1, column+2) );
        //Right-down
        if( isTileAvailable(row+1, column+2) > -1)
            possibleMoves.add( new Position(row+1, column+2) );
        //Lower-right
        if( isTileAvailable(row+2, column+1) > -1)
            possibleMoves.add( new Position(row+2, column+1) );
        //Lower-left
        if( isTileAvailable(row+2, column-1) > -1)
            possibleMoves.add( new Position(row+2, column-1) );
        //Left-down
        if( isTileAvailable(row+1, column-2) > -1)
            possibleMoves.add( new Position(row+1, column-2) );
        //Left-up
        if( isTileAvailable(row-1, column-2) > -1)
            possibleMoves.add( new Position(row-1, column-2) );

        return possibleMoves;
    }

    @Override
    public int getIconId() {
        if( isWhite() ) return R.drawable.knightw;
        else return R.drawable.knightb;
    }
}
