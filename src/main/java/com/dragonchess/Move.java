package com.dragonchess;

public class Move {
    public Piece piece;
    public int targetCol;
    public int targetRow;
    private int startCol;
    private int startRow;

    public Move(Piece piece, int targetCol, int targetRow) {
        this.piece = piece;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
        this.startCol = piece.col;
        this.startRow = piece.row;
    }

    public void executeMove() {
        piece.preCol = piece.col;
        piece.preRow = piece.row;
        
        piece.col = targetCol;
        piece.row = targetRow;
        if (piece.type == Type.PAWN) {
            piece.twoSquareMove = Math.abs(piece.row - piece.preRow) == 2;
        }
        piece.updatePosition();
    }

    public void undoMove() {
        piece.col = startCol;
        piece.row = startRow;
        piece.updatePosition();
    }
}