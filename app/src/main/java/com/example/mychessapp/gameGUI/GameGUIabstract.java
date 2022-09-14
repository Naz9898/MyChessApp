package com.example.mychessapp.gameGUI;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mychessapp.R;
import com.example.mychessapp.gameImplementation.Game;
import com.example.mychessapp.gameImplementation.Piece;
import com.example.mychessapp.gameImplementation.Position;

import java.util.ArrayList;

public abstract class GameGUIabstract {
    String TAG  = getClass().getSimpleName();

    private Game game;
    private ConstraintLayout layout;
    private TableLayout chessboard;
    //True = Selection mode, False = Move mode
    private boolean isSelecting;
    private Piece selectedPiece;

    private Player opponent;
    private boolean isWhite;    //Player color

    public GameGUIabstract(ConstraintLayout layout){
        this.layout = layout;

        this.chessboard = layout.findViewById(R.id.chessBoard);
        this.isSelecting = true;
        this.selectedPiece = null;
    }

    //Initialization methods
    private ImageView getImageView(int row, int column){
        return (ImageView) ((TableRow)getChessboard().getChildAt(row)).getChildAt(column);
    }

    protected void setNames() {
        if(isWhite()){
            ((TextView)getLayout().findViewById(R.id.textViewUsernameBlack)).setText( getOpponent().getName());
        }else{
            TextView whiteName = getLayout().findViewById(R.id.textViewUsernameWhite);
            TextView blackName = getLayout().findViewById(R.id.textViewUsernameBlack);
            String username = whiteName.getText().toString();
            whiteName.setText( getOpponent().getName() );
            blackName.setText( username );
        }
    }

    protected void setListeners() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ImageView imageView = getImageView(i, j);
                imageView.setOnClickListener( new OnTileClickListener(i,j,this) );
            }
        }
    }

    //Update single piece
    protected void drawPiece(ImageView imageView, Piece piece){
        if(piece == null){
            imageView.setForeground(null);
            return;
        }
        int resourceId = piece.getIconId();
        imageView.setForeground(getChessboard().getContext().getDrawable(resourceId));
    }

    //Render all pieces
    protected void drawAll(){
        ArrayList<Piece> toDraw = new ArrayList<Piece>();
        toDraw.addAll( game.getWhitePieces() ); //Add white pieces
        toDraw.addAll( game.getBlackPieces() ); //Add black pieces
        //Draw white
        for(Piece piece: toDraw){
            int row = piece.getCurrentPosition().getRow();
            int column = piece.getCurrentPosition().getColumn();
            ImageView imageView = getImageView(row, column);
            //Set image
            drawPiece(imageView, piece);
        }

    }

    protected void drawCaptured(Drawable d){
        ImageView imageView = new ImageView( getChessboard().getContext() );
        imageView.setImageDrawable(d);

        if(getGame().isWhiteMove()) ((LinearLayout)layout.findViewById(R.id.blackCapturedGUI)).addView(imageView);
        else ((LinearLayout)layout.findViewById(R.id.whiteCapturedGUI)).addView(imageView);
    }

    //Select piece to move
    public void select(int row, int column){
        if(getGame() == null) return;
        //If game is over don't do anything
        if( getGame().getGamestatus() != 0 ) return;
        Piece currentPiece = getGame().getGameBoard()[row][column];
        //Empty tile
        if(currentPiece == null) return;
        //Not your move
        if(currentPiece.isWhite() != getGame().isWhiteMove()) return;
        //Not you piece
        if(currentPiece.isWhite() != isWhite()) return;
        ArrayList<Position> possibleMoves = currentPiece.getPossibleMoves();
        //If no moves, don't go to move mode
        if( possibleMoves.isEmpty() ) return;
        //Highlight selectable cells and go to Move mode
        highlightPossibleMoves(possibleMoves);
        setSelecting(false);
        setSelectedPiece( currentPiece );
    }

    //Check if move is possible and then move and return to selection
    public void move(int nextRow, int nextColumn){
        Position nextPosition = new Position(nextRow, nextColumn);
        ArrayList<Position> selectedPossibleMoves = getSelectedPiece().getPossibleMoves();
        for(Position p: selectedPossibleMoves){
            if(p.equals(nextPosition)){
                Position oldPosition = getSelectedPiece().getCurrentPosition();
                directMove(getSelectedPiece(), nextPosition);
                notifyOpponentMove(oldPosition, nextPosition);
                break;
            }
        }

        //Return to select mode and update GUI
        clearSelectedCells(selectedPossibleMoves);
        setSelecting(true);
        setSelectedPiece(null);

    }

    protected abstract void notifyOpponentMove(Position oldPos, Position newPos);

    //Move logic
    public void directMove(Piece piece, Position pos){
        //Remove piece  from old position (GUI)
        Position oldPos = piece.getCurrentPosition();
        int oldRow = oldPos.getRow();
        int oldColumn = oldPos.getColumn();
        ImageView imageView = getImageView(oldRow, oldColumn);
        imageView.setForeground(null);
        //Add to capture GUI if needed
        imageView = getImageView(pos.getRow(), pos.getColumn());
        Drawable drawable = imageView.getForeground();
        if(drawable != null) drawCaptured(drawable);
        //Move
        getGame().move(piece, pos);
        notifyGameStatus();
        drawPiece(imageView, piece);
    }


    public void notifyGameStatus(){
        //Notify normal check
        int gameStatus = getGame().getGamestatus();
        switch (gameStatus) {
            case 1:
                Toast.makeText(getChessboard().getContext(), "White wins!", Toast.LENGTH_LONG).show();
                return;
            case -1:
                Toast.makeText(getChessboard().getContext(), "Black wins!", Toast.LENGTH_LONG).show();
                return;
            case -2:
                Toast.makeText(getChessboard().getContext(), "Draw! Stalemate", Toast.LENGTH_LONG).show();
                return;
        }
    }

    //Highlight selected moves
    protected void highlightPossibleMoves(ArrayList<Position> possibleMoves) {
        int color = getChessboard().getContext().getResources().getColor(R.color.purple_200);
        for(Position p: possibleMoves){
            ImageView imageView = getImageView(p.getRow(), p.getColumn());
            imageView.setColorFilter( color );
        }
    }
    //Remove highlight from selected cells
    protected void clearSelectedCells(ArrayList<Position> cells){
        for(Position p: cells){
            ImageView imageView = getImageView(p.getRow(), p.getColumn());
            imageView.clearColorFilter();
        }
    }


    //Getter
    public Game getGame() {
        return game;
    }
    public TableLayout getChessboard() {
        return chessboard;
    }
    public boolean isSelecting() {
        return isSelecting;
    }
    public Piece getSelectedPiece() {
        return selectedPiece;
    }
    public boolean isWhite() {
        return isWhite;
    }
    public ConstraintLayout getLayout() {
        return layout;
    }
    public Player getOpponent() {
        return opponent;
    }

    //Setter
    public void setGame(Game game) {
        this.game = game;
    }
    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }
    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }
    public void setWhite(boolean white) {
        isWhite = white;
    }
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }
}
