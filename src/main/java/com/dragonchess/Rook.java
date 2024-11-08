package com.dragonchess;

public class Rook extends Piece{

    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = Type.ROOK;
        if(color == Panel.WHITE){
            image = getImage("Rook_W.svg");

        }
        else {
            image = getImage("Rook_B.svg");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            if((targetCol == preCol) || targetRow == preRow){ // move 
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
    
}
