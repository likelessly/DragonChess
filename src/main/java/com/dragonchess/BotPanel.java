package com.dragonchess;

import java.util.ArrayList;

import javax.swing.JFrame;

public class BotPanel extends Panel {
    private int depth = 2; // Depth for the Minimax algorithm
    private boolean player = true;

    public BotPanel(JFrame parentFrame) {
        super(parentFrame);
    }

    @Override
    public void changePlayer() {
        if (currentColor == WHITE) {
            System.out.println("Bot's turn");
            player = true;
            currentColor = BLACK;
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoSquareMove = false;
                }
            }
            makeBotMove(); // Trigger Minimax bot move
            // System.out.println("minimax is = " + minimax(depth, false));
            // makeBestBlackMove(depth); // Trigger Minimax bot move when it’s black’s turn
        } else {
            System.out.println("White's turn");

            currentColor = WHITE;
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoSquareMove = false;
                }
            }

            System.out.println("Current eval = " + evaluateBoard(simPieces));
        }

        activeP = null;
    }

    @Override
    public void update() {

        if (promotion) {
            promoting();
        } else if (!gameOver && !stalemate) {
            
            // mouse pressed
            if (mouse.pressed) {
                if (activeP == null) {
                    for (Piece piece : simPieces) {
                        if (piece.color == currentColor && piece.col == mouse.x / Board.square_size
                                && piece.row == mouse.y / Board.square_size) {
                            activeP = piece;
                        }
                    }
                } else {
                    simulate();
                }
            }
            // mouse released
            if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {
                        // piece is moved / Captured / Confirm
                        copyPieces(simPieces, pieces); // update the piece in the list if piece is captured
                        activeP.updatePosition();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }

                        if (isKinginCheck() && isCheckmate()) {
                            System.out.println("Checkmate");
                            gameOver = true;
                        } else if (!isKinginCheck() && isStalemate()) {
                            System.out.println("Stalemate");
                            stalemate = true;
                        } else {
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                                
                            }
                        }

                    } else {
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }

    }

    // Minimax algorithm for move decision-making
    private int minimax(int depth, boolean isMaximizingPlayer) {
        // System.out.println("Minimax depth: " + depth);
        // ArrayList<Piece> mPieces = new ArrayList<>(simPieces);
        if (gameOver || depth == 0) {
            return evaluateBoard(simPieces);
        }
        Piece capturedP = null;
        if (isMaximizingPlayer) { // White's turn
            int bestScore = Integer.MIN_VALUE;
            for (Move posMove : getPossibleMoves(simPieces)) { // Create a copy of the list to iterate over
                if (posMove.piece.color == WHITE) {
                    capturedP = posMove.piece.isPieceThere(posMove.targetCol, posMove.targetRow);
                    if (capturedP != null) {
                        simPieces.remove(capturedP);
                    }
                    posMove.executeMove(); // Simulate move
                    int score = minimax(depth - 1, false);
                    posMove.undoMove(); // Undo move
                    if (capturedP != null) {
                        simPieces.add(capturedP);
                        capturedP = null;
                    }
                    bestScore = Math.max(score, bestScore); // Maximizing player's choice
                }

            }
            return bestScore;
        } else { // Minimize player's turn
            int bestScore = Integer.MAX_VALUE;
            for (Move posMove : getPossibleMoves(simPieces)) { // Create a copy of the list to iterate over
                if (posMove.piece.color == BLACK) {
                    capturedP = posMove.piece.isPieceThere(posMove.targetCol, posMove.targetRow);
                    if (capturedP != null) {
                        simPieces.remove(capturedP);
                    }
                    posMove.executeMove(); // Simulate move

                    // piece.updatePosition();
                    int score = minimax(depth - 1, true); // Alternate to maximizing player
                    posMove.undoMove(); // Undo move
                    if (capturedP != null) {
                        simPieces.add(capturedP);
                        capturedP = null;
                    }
                    bestScore = Math.min(score, bestScore); // Minimizing player's choice
                }
            }
            return bestScore;
        }
    }

    // Execute the best move for black determined by Minimax
    private Move findBestMoveForBlack() {
        int bestScore = Integer.MAX_VALUE; // Initialize to maximum value since Black is minimizing
        Move bestMove = null;

        // Iterate through all possible moves for Black
        for (Move posMove : getPossibleMoves(simPieces)) {
            if (posMove.piece.color == BLACK) {
                Piece capturedP = posMove.piece.isPieceThere(posMove.targetCol, posMove.targetRow);
                if (capturedP != null) {
                    simPieces.remove(capturedP);
                }
                posMove.executeMove(); // Simulate move

                // Evaluate the move using minimax
                int score = minimax(depth - 1, true); // Alternate to maximizing player

                posMove.undoMove(); // Undo move
                if (capturedP != null) {
                    simPieces.add(capturedP);
                }

                // Update best move if the current move has a lower score
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = posMove;
                }
            }
        }

        return bestMove;
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
    private int evaluateBoard(ArrayList<Piece> evalPieces) {
        int score = 0;
        if (evalPieces == null) {
            System.err.println("simPieces is null");
            return score;
        }
        for (Piece piece : evalPieces) {
            if (piece == null) {
                System.err.println("Null piece found in simPieces");
                continue;
            }
            if (piece.color == BLACK) {
                score -= getPieceValue(piece);
            } else {
                score += getPieceValue(piece);
            }
        }
        return score;
    }

    private int getPieceValue(Piece piece) {
        if (piece.type == Type.PAWN)
            return 10;
        if (piece.type == Type.KNIGHT || piece.type == Type.BISHOP)
            return 30;
        if (piece.type == Type.ROOK)
            return 50;
        if (piece.type == Type.QUEEN)
            return 90;
        if (piece.type == Type.DRAGON)
            return 70;
        if (piece.type == Type.KING)
            return 900;
        else
            return 0;
    }

    public ArrayList<Move> getPossibleMoves(ArrayList<Piece> restPieces) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (Piece piece : restPieces) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (piece.canMove(i, j)) {
                        possibleMoves.add(new Move(piece, i, j));
                    }
                }
            }
        }
        return possibleMoves;
    }
    public void makeBotMove() {
        Move bestMove = findBestMoveForBlack();
        if (bestMove != null) {
            System.out.println("Best move for Black: " + bestMove.piece.type + " to (" + bestMove.targetCol + ", "
                    + bestMove.targetRow + ")");
            Piece isPieceThere = bestMove.piece.isPieceThere(bestMove.targetCol, bestMove.targetRow);
            if (isPieceThere != null) {
                simPieces.remove(isPieceThere.getIndex());
            }
            bestMove.executeMove();
            copyPieces(simPieces, pieces);
            // bestMove.piece.col = bestMove.targetCol;
            // bestMove.piece.row = bestMove.targetRow;
            changePlayer(); // Switch to the other player's turn
        } else {
            System.out.println("No valid moves found for Black.");
        }
    }
}
