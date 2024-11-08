package com.dragonchess;

public class Pawn extends Piece{
    
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;
        if(color == Panel.WHITE){
            image = getImage("Pawn_W.svg");
        } else {
            image = getImage("Pawn_B.svg");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            int moveValue;
            if(color == Panel.WHITE){
                moveValue = -1;
            }
            else {
                moveValue = 1;
            }
            isPieceThere = isPieceThere(targetCol, targetRow);
            // 1 square move
            if(targetCol == preCol && targetRow == preRow + moveValue && isPieceThere == null){
                return true;
            }
            // 2 sqaure move
            if(targetCol == preCol && targetRow == preRow + moveValue*2 && isPieceThere == null && moved == false && pieceIsOnStraightLine(targetCol, targetRow) == false){
                return true;
            }
            //diagonal capture, move
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && isPieceThere != null && isPieceThere.color != color){
                return true;
            }
            //en passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue){
                for(Piece piece : Panel.simPieces){
                    if(piece.col == targetCol && piece.row == preRow && piece.twoSquareMove == true){
                        isPieceThere = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
