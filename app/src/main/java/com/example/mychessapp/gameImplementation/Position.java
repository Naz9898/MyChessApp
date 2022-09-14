package com.example.mychessapp.gameImplementation;

public class Position {
    private int row;
    private int column;

    //Constructor
    public Position(int row, int column){
        this.row = row;
        this.column = column;
    }

    public boolean equals(Position p){
        return (getRow() == p.getRow()) && (getColumn() == p.getColumn());
    }

    public String toString(){
        return getRow()+" "+getColumn();
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }
}
