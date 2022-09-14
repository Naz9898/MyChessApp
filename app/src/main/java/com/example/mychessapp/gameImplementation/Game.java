package com.example.mychessapp.gameImplementation;

import android.util.Log;

import java.util.ArrayList;

public class Game {
    String TAG  = Game.class.getSimpleName();
    private Piece[][] gameBoard;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Piece> whiteCaptured;
    private ArrayList<Piece> blackCaptured;

    private Piece whiteKing;
    private Piece blackKing;
    private boolean isWhiteMove;
    //0 = ongoing, 1 = white wins, -1 = black wins, -2 = stalemate, 2 = check
    private int gamestatus;

    public Game() {
        Log.d(TAG, "Game new");
        initializeVariables();
        initializePieces();
        isWhiteMove = true;

        gamestatus = 0;
    }

    public Game(String gameStatus){
        Log.d(TAG, "Game resume");
        initializeVariables();
        restoreGame(gameStatus);

        gamestatus = 0;
    }

    public void initializeVariables(){
        gameBoard = new Piece[8][8];
        whitePieces = new ArrayList<Piece>();
        whiteCaptured = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        blackCaptured = new ArrayList<Piece>();
    }
    public void initializePieces(){
        //Pawns
        for(int i = 0; i < 8; ++i)
            new Pawn(this, false, 1, i);
        for(int i = 0; i < 8; ++i)
            new Pawn(this, true, 6, i);
        //Rooks
        new Rook(this, false, 0, 0);
        new Rook(this, false, 0, 7);
        new Rook(this, true,  7, 0);
        new Rook(this, true,  7, 7);
        //Bishops
        new Bishop(this, false, 0, 2);
        new Bishop(this, false, 0, 5);
        new Bishop(this, true,  7, 2);
        new Bishop(this, true,  7, 5);
        //Knights
        new Knight(this, false, 0, 1);
        new Knight(this, false, 0, 6);
        new Knight(this, true,  7, 1);
        new Knight(this, true,  7, 6);
        //Queens
        new Queen(this, true, 7, 3);
        new Queen(this, false, 0, 3);
        //Kings
        new King(this, true, 7, 4);
        whiteKing = gameBoard[7][4];
        new King(this, false, 0, 4);
        blackKing = gameBoard[0][4];
    }
    //Read game from file
    public void restoreGame(String gameStatus){
        Log.d(TAG, gameStatus);
        String[] lines = gameStatus.split("\n");
        isWhiteMove = Boolean.parseBoolean(lines[0]);
        Log.d(TAG, "read isWhiteMove " + lines[0]);
        boolean isWhite = true;
        for(int i= 1; i< lines.length; ++i){
            String s =  lines[i];
            if(s.equals("Black") ) isWhite = false;
            else pieceFromString(s, isWhite);
        }
    }

    public void pieceFromString(String line, boolean isWhite){
        String[] splitStr = line.split("\\s+");
        String name = splitStr[0];

        int row = Integer.parseInt(splitStr[1]);
        int column = Integer.parseInt(splitStr[2]);
        switch(name) {
            case "Pawn":  new Pawn(this, isWhite, row, column);
                return;
            case "Rook":  new Rook(this, isWhite, row, column);
                return;
            case "Knight":  new Knight(this, isWhite, row, column);
                return;
            case "Bishop":  new Bishop(this, isWhite, row, column);
                return;
            case "Queen":  new Queen(this, isWhite, row, column);
                return;
            case "King":
                Piece p = new King(this, isWhite, row, column);
                if(isWhite) whiteKing = gameBoard[row][column];
                else blackKing = gameBoard[row][column];
                return;
        };
    }

    //Add piece to the game sets
    public void add(Piece piece, boolean white){
        ArrayList<Piece> pieceSet = white? getWhitePieces():getBlackPieces();
        pieceSet.add(piece);
    }

    public boolean move(Piece piece, Position nextPosition){
        if(getGamestatus() != 0) return false;
        //Not your turn to move
        if(piece.isWhite() && !isWhiteMove()) return false;
        if(!piece.isWhite() && isWhiteMove()) return false;
        ArrayList<Position> possibleMoves = piece.getPossibleMoves();
        for (Position position: possibleMoves){
            if( position.equals(nextPosition) ){
                //Remove piece from current position
                setTile(null, piece.getCurrentPosition());
                //Set piece on next position
                Piece capturedPiece = setTile(piece, nextPosition);
                //If piece is captured, remove it from game
                if( capturedPiece != null ) capture( capturedPiece );
                piece.setCurrentPosition(nextPosition);
                //Pawn doble move or promotion check
                if(piece instanceof Pawn){
                    if(nextPosition.getRow() == 0 || nextPosition.getRow() == 7)
                        promote(nextPosition);
                }
                setWhiteMove( !isWhiteMove() );
                //Controls if game is over
                if( checkControl() ){
                    if(noMoves()){
                        //Check mate
                        if(isWhiteMove) setGamestatus(-1);
                        else setGamestatus(1);
                    }
                    else Log.d(TAG, "Check!"); //Check
                } else if(noMoves())
                    setGamestatus(-2); //Stale mate
                return true;
            }
        }
        return false;
    }

    public boolean isMovePossible(Piece piece, Position nextPosition){
        //Try move
        Position oldPosition = piece.getCurrentPosition();
        setTile(null, oldPosition);
        Piece capturedPiece = setTile(piece, nextPosition);
        piece.setCurrentPosition(nextPosition);
        ArrayList<Piece> pieceSet = isWhiteMove?getBlackPieces():getWhitePieces();
        if(capturedPiece != null)pieceSet.remove(capturedPiece);
        //If it's check move it's no good, if it's not check the move is possible
        boolean result = !checkControl();
        //Restore status
        setTile(piece, oldPosition);
        setTile(capturedPiece, nextPosition);
        piece.setCurrentPosition(oldPosition);
        if(capturedPiece != null) pieceSet.add(capturedPiece);
        return result;
    }

    //Return the captured piece, null if tile was free
    public Piece setTile(Piece piece, Position position){
        Piece capturedPiece = getGameBoard()[position.getRow()][position.getColumn()];
        //Move the piece
        getGameBoard()[position.getRow()][position.getColumn()] = piece;
        return capturedPiece;
    }

    //Add piece to captured set and remove it from game
    private void capture(Piece piece){
        if(piece.isWhite()){
            getWhiteCaptured().add(piece);
            getWhitePieces().remove(piece);
        }
        else{
            getBlackCaptured().add(piece);
            getBlackPieces().remove(piece);
        }
        return;
    }

    //Return 0 if the tile is free, 1 if it's a white piece, -1 if it's black
    public int checkTile(int row, int column){
        Piece tileStatus = getGameBoard()[row][column];
        if(getWhitePieces().contains(tileStatus)) return 1;
        if(getBlackPieces().contains(tileStatus)) return -1;
        return 0;
    }

    //True if it's check, false otherwise
    public boolean checkControl(){
        ArrayList<Piece> toCheck = isWhiteMove?getBlackPieces():getWhitePieces();
        for(Piece p: toCheck){
            Piece king = isWhiteMove? getWhiteKing():getBlackKing();
            ArrayList<Position> movesToCheck = p.getMenacingCells();
            for(Position pos: movesToCheck)
                if( king.getCurrentPosition().equals(pos) )
                    return true;
        }
        return false;
    }

    //For checkmate and stalemate control
    public boolean noMoves(){
        ArrayList<Piece> toCheck = isWhiteMove?getWhitePieces():getBlackPieces();
        for(Piece p: toCheck)
            if(p.getPossibleMoves().size() > 0)
                return false;
        return true;
    }

    //Promote pawns into queens
    public boolean promote(Position pos){
        Piece toPromote = getGameBoard()[pos.getRow()][pos.getColumn()];
        if( !(toPromote instanceof Pawn) )
            return false;
        ArrayList<Piece> toUpdate = isWhiteMove()?getWhitePieces():getBlackPieces();
        toUpdate.remove(toPromote);
        toPromote = new Queen(this, isWhiteMove(), pos.getRow(), pos.getColumn());
        setTile(toPromote, pos);
        gameBoard[pos.getRow()][pos.getColumn()] = toPromote;
        toUpdate.add(toPromote);
        return true;
    }

    //Getters methods
    public Piece[][] getGameBoard() {
        return gameBoard;
    }
    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }
    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }
    public ArrayList<Piece> getWhiteCaptured() {
        return whiteCaptured;
    }
    public ArrayList<Piece> getBlackCaptured() {
        return blackCaptured;
    }

    public Piece getWhiteKing() {
        return whiteKing;
    }
    public Piece getBlackKing() {
        return blackKing;
    }
    public boolean isWhiteMove() {
        return isWhiteMove;
    }
    public int getGamestatus() {
        return gamestatus;
    }

    //Setters
    public void setWhiteMove(boolean whiteMove) {
        isWhiteMove = whiteMove;
    }
    public void setGamestatus(int gamestatus) {
        this.gamestatus = gamestatus;
    }
}
