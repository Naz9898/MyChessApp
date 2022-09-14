package com.example.mychessapp.gameGUI;

public abstract class Player {
    private boolean isWhite;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public abstract void move();
    public abstract String getName();

    public boolean isWhite() {
        return isWhite;
    }
}
