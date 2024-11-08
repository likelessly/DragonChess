package com.dragonchess;

public class Dragon extends Piece{

    public Dragon(int color, int col, int row) {
        super(color, col, row);
        type = Type.DRAGON;
        if(color == Panel.WHITE){
            image = getImage("Dragon_W2.svg");

        }
        else {
            image = getImage("Dragon_B.svg");
        }
    }
    @Override
    public int getX(int col) {
        return col * ChessBoard.square_size;
    }
    @Override
    public int getY(int row) {
        return color == 1 ? row * ChessBoard.square_size: row *ChessBoard.square_size;
    }
    @Override
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            if(moved == false){
                if(targetRow == preRow && isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }
            else {
                if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow) || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2){
                    if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
