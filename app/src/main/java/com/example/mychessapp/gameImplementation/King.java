package com.example.mychessapp.gameImplementation;

import com.example.mychessapp.R;

import java.util.ArrayList;

public class King extends Piece {
    public King(Game game, boolean isWhite, int row, int column) {
        super(game, isWhite, row, column);
    }

    @Override
    public ArrayList<Position> getMenacingCells() {
        Position p = getCurrentPosition();
        int row = p.getRow();
        int column = p.getColumn();

        ArrayList<Position> possibleMoves = new ArrayList<Position>();
        //Upper
        if( isTileAvailable(row-1, column) > -1)
            possibleMoves.add(new Position(row -1, column));
        //Upper right
        if( isTileAvailable(row-1, column+1) > -1)
            possibleMoves.add( new Position(row -1, column + 1) );
        //Right
        if( isTileAvailable(row, column+1) > -1)
            possibleMoves.add( new Position(row, column+1) );
        //Lower right
        if( isTileAvailable(row+1, column+1) > -1)
            possibleMoves.add( new Position(row+1, column+1) );
        //Lower
        if( isTileAvailable(row+1, column) > -1)
            possibleMoves.add( new Position(row+1, column) );
        //Lower left
        if( isTileAvailable(row+1, column-1) > -1)
            possibleMoves.add( new Position(row+1, column-1) );
        //Left
        if( isTileAvailable(row, column-1) > -1)
            possibleMoves.add( new Position(row, column-1) );
        //Upper left
        if( isTileAvailable(row-1, column-1) > -1)
            possibleMoves.add( new Position(row-1, column-1) );

        return possibleMoves;
    }

    @Override
    public int getIconId() {
        if( isWhite() ) return R.drawable.kingw;
        else return R.drawable.kingb;
    }
}
