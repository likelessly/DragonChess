package com.dragonchess;

public class King extends Piece{

    public King(int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;
        if(color == Panel.WHITE){
            image = getImage("King_W.svg");

        }
        else {
            image = getImage("King_B.svg");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){ // within board
            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1
            || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1){
                if(isValidSquare(targetCol, targetRow)){ // right square
                    return true;
                }
            }
        }
        //castling
        if(moved == false){
            //right castling
            if(targetCol == preCol + 2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                for(Piece piece : Panel.simPieces){
                    if(piece.col == preCol+3 && piece.row == preRow && piece.moved == false){
                        Panel.castlingP = piece;
                        return true;
                    }
                }
            }
            //left castling
            if(targetCol == preCol - 2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                Piece p[] = new Piece[2];
                for(Piece piece : Panel.simPieces){
                    if(piece.col == preCol-3 && piece.row == targetRow){
                        p[0] = piece;
                    }
                    if(piece.col == preCol-4 && piece.row == targetRow){
                        p[1] = piece;
                    }
                    if(p[0] == null && p[1] != null && p[1].moved == false){
                        Panel.castlingP = p[1];
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
