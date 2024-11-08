package com.dragonchess;

public class Queen extends Piece{

    public Queen(int color, int col, int row) {
        super(color, col, row);
        type = Type.QUEEN;
        if(color == Panel.WHITE){
            image = getImage("Queen_W.svg");

        }
        else {
            image = getImage("Queen_B.svg");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            if((targetCol == preCol) || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    
}
