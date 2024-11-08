package com.dragonchess;

import java.util.List;
import javax.swing.JFrame;

public class BotPanel extends Panel {
    private int depth = 3;  // Depth for the Minimax algorithm

    public BotPanel(JFrame parentFrame) {
        super(parentFrame);
    }

    @Override
    public void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoSquareMove = false;
                }
            }
            makeBestBlackMove(depth);  // Trigger Minimax bot move when it’s black’s turn
        } else {
            currentColor = WHITE;
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoSquareMove = false;
                }
            }
        }
        activeP = null;
    }

    // Minimax algorithm for move decision-making 
    public int minimax(int depth, boolean isMaximizingPlayer) {
        if (depth == 0 || gameOver) {
            return evaluateBoard();  // Evaluate the board at the limit depth or endgame
        }

        int bestValue = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Piece piece : pieces) {
            if ((isMaximizingPlayer && piece.color == BLACK) || (!isMaximizingPlayer && piece.color == WHITE)) {
                List<int[]> moves = piece.getPossibleMoves(simPieces);

                for (int[] move : moves) {
                    int originalCol = piece.col;
                    int originalRow = piece.row;
                    Piece capturedPiece = piece.isPieceThere(move[0], move[1]);

                    // Simulate move
                    piece.col = move[0];
                    piece.row = move[1];
                    if (capturedPiece != null) simPieces.remove(capturedPiece);

                    // Skip moves that leave the bot's king in check
                    if (isKingInCheck(BLACK)) {
                        // Undo move and continue to next move
                        piece.col = originalCol;
                        piece.row = originalRow;
                        if (capturedPiece != null) simPieces.add(capturedPiece);
                        continue;
                    }

                    int value = minimax(depth - 1, !isMaximizingPlayer);  // Recursively evaluate move

                    // Undo move
                    piece.col = originalCol;
                    piece.row = originalRow;
                    if (capturedPiece != null) simPieces.add(capturedPiece);

                    // Select best move based on player
                    if (isMaximizingPlayer) {
                        bestValue = Math.max(bestValue, value);
                    } else {
                        bestValue = Math.min(bestValue, value);
                    }
                }
            }
        }
        return bestValue;
    }

    // Execute the best move for black determined by Minimax
    public void makeBestBlackMove(int depth) {
        Piece bestPiece = null;
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Piece piece : pieces) {
            if (piece.color == BLACK) {
                List<int[]> moves = piece.getPossibleMoves(simPieces);

                for (int[] move : moves) {
                    int originalCol = piece.col;
                    int originalRow = piece.row;
                    Piece capturedPiece = piece.isPieceThere(move[0], move[1]);

                    // Simulate move
                    piece.col = move[0];
                    piece.row = move[1];
                    if (capturedPiece != null) simPieces.remove(capturedPiece);

                    int score = minimax(depth - 1, false);  // Evaluate move with Minimax

                    // Undo move
                    piece.col = originalCol;
                    piece.row = originalRow;
                    if (capturedPiece != null) simPieces.add(capturedPiece);

                    // Track best scoring move
                    if (score > bestScore) {
                        bestScore = score;
                        bestPiece = piece;
                        bestMove = move;
                    }
                }
            }
        }

        // Perform the best move and remove the captured piece permanently if any
        if (bestPiece != null && bestMove != null) {
            Piece capturedPiece = bestPiece.isPieceThere(bestMove[0], bestMove[1]);
            bestPiece.col = bestMove[0];
            bestPiece.row = bestMove[1];
            bestPiece.updatePosition();

            // Remove the captured piece permanently from both lists
            if (capturedPiece != null) {
                pieces.remove(capturedPiece);
                simPieces.remove(capturedPiece);
            }

            changePlayer();  // Change turn to white
        }
    }

    // Check if the king of the specified color is in check
    private boolean isKingInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        // Check if any opponent piece can move to the king's position
        for (Piece piece : pieces) {
            if (piece.color != color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    // Find the king piece for the specified color
    private Piece findKing(int color) {
        for (Piece piece : pieces) {
            if (piece.color == color && piece.type == Type.KING) {
                return piece;
            }
        }
        return null;
    }

    // Evaluation of the board based on material and positional value
    private int evaluateBoard() {
        int score = 0;

        for (Piece piece : pieces) {
            int pieceValue = 0;

            // Assign basic material values
            switch (piece.type) {
                case QUEEN: pieceValue = 90; break;
                case ROOK: pieceValue = 50; break;
                case BISHOP: pieceValue = 30; break;
                case KNIGHT: pieceValue = 30; break;
                case PAWN: pieceValue = 10; break;
                case DRAGON: pieceValue = 70; break;
                case KING: pieceValue = 900; break;  // High value to prevent sacrificing the king
            }

            // Additional positional evaluation can be added here

            // Score is positive for black pieces, negative for white pieces
            if (piece.color == BLACK) score += pieceValue;
            else score -= pieceValue;
        }

        return score;
    }
}
