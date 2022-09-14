package com.example.mychessapp.gameImplementation;

import com.example.mychessapp.R;

import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(Game game, boolean isWhite, int row, int column) {
        super(game, isWhite, row, column);
    }

    @Override
    public ArrayList<Position> getMenacingCells() {
        Position p = getCurrentPosition();
        int row = p.getRow();
        int column = p.getColumn();

        ArrayList<Position> possibleMoves = new ArrayList<Position>();
        //Upper right
        for(int i=1; isTileAvailable(row-i, column+i) > -1; i++){
            possibleMoves.add(new Position(row-i, column+i));
            if(isTileAvailable(row-i, column+i) == 1) break;
        }
        //Lower right
        for(int i=1; isTileAvailable(row+i, column+i) > -1; i++){
            possibleMoves.add(new Position(row+i, column+i));
            if(isTileAvailable(row+i, column+i) == 1) break;
        }
        //Lower left
        for(int i=1; isTileAvailable(row+i, column-i) > -1; i++){
            possibleMoves.add(new Position(row+i, column-i));
            if(isTileAvailable(row+i, column-i) == 1) break;
        }
        //Upper left
        for(int i=1; isTileAvailable(row-i, column-i) > -1; i++){
            possibleMoves.add(new Position(row-i, column-i));
            if(isTileAvailable(row-i, column-i) == 1) break;
        }

        return possibleMoves;
    }

    @Override
    public int getIconId() {
        if( isWhite() ) return R.drawable.bishopw;
        else return R.drawable.bishopb;
    }
}
