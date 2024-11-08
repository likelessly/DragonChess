package com.dragonchess;

public class moving {
    public Piece piece;
    public int targetCol;
    public int targetRow;

    public moving(Piece piece, int targetCol, int targetRow) {
        this.piece = piece;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }
    public void executeMove(){
        piece.col = targetCol;
        piece.row = targetRow;
    }
    public void undoMove(){
        piece.col = piece.preCol;
        piece.row = piece.preRow;
    }
    public Piece getPiece() {
        return piece;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public int getTargetRow() {
        return targetRow;
    }
}
