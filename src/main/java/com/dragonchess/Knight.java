package com.dragonchess;

public class Knight extends Piece{

    public Knight(int color, int col, int row) {
        super(color, col, row);
        type = Type.KNIGHT;
        if(color == Panel.WHITE){
            image = getImage("Horse_W.svg");

        }
        else {
            image = getImage("Horse_B.svg");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
    
}
