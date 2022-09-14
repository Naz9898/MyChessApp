package com.example.mychessapp.gameGUI;

import android.view.View;

public class OnTileClickListener implements View.OnClickListener {
    private int row;
    private int column;

    private GameGUIabstract gameGUI;

    public OnTileClickListener(int row, int column, GameGUIabstract gameGUI) {
        this.row = row;
        this.column = column;
        this.gameGUI = gameGUI;
    }

    @Override
    public void onClick(View view) {
        //Select piece, highlight possible moves
        if( gameGUI.isSelecting() ) gameGUI.select(row, column);
        //Move
        else gameGUI.move(row, column);
        return;
    }
}
