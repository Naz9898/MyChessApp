package com.example.mychessapp.gameImplementation;

import com.example.mychessapp.R;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Game game, boolean isWhite, int row, int column) {
        super(game, isWhite, row, column);
    }

    @Override
    public ArrayList<Position> getMenacingCells() {
        Position p = getCurrentPosition();
        int row = p.getRow();
        int column = p.getColumn();

        ArrayList<Position> possibleMoves = new ArrayList<Position>();
        //Upper
        for(int i=1; isTileAvailable(row-i, column) > -1; i++){
            possibleMoves.add(new Position(row-i, column));
            if(isTileAvailable(row-i, column) == 1) break;
        }
        //Right
        for(int i=1; isTileAvailable(row, column+i) > -1; i++){
            possibleMoves.add(new Position(row, column+i));
            if(isTileAvailable(row, column+i) == 1) break;
        }
        //Lower
        for(int i=1; isTileAvailable(row+i, column) > -1; i++){
            possibleMoves.add(new Position(row+i, column));
            if(isTileAvailable(row+i, column) == 1) break;
        }
        //Left
        for(int i=1; isTileAvailable(row, column-i) > -1; i++){
            possibleMoves.add(new Position(row, column-i));
            if(isTileAvailable(row, column-i) == 1) break;
        }

        return possibleMoves;
    }

    @Override
    public int getIconId() {
        if( isWhite() ) return R.drawable.rookw;
        else return R.drawable.rookb;
    }
}
