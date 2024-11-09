package com.dragonchess;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, MouseListener {
    public static int WIDTH = 1100;
    public static int HEIGHT = 800;
    public final int FPS = 60;
    public boolean running = true;
    Thread gameThread;
    ChessBoard Board = new ChessBoard();
    Mouse mouse = new Mouse();
    // pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;
    // color set
    public static int WHITE = 0;
    public static int BLACK = 1;
    int currentColor = WHITE;
    // checker
    boolean canMove;
    boolean validSquare;
    boolean promotion, gameOver, stalemate;

    protected JFrame parentFrame;
    JButton exitToMenu;

    public Panel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        setLayout(null);

        setPieces();
        copyPieces(pieces, simPieces);

        // Exit to Menu button
        exitToMenu = new JButton("Exit to Menu");
        exitToMenu.setBounds(WIDTH - 150, 10, 130, 40);
        exitToMenu.setBackground(Color.BLACK);
        exitToMenu.setForeground(Color.WHITE);
        exitToMenu.setFocusPainted(false); // Removes the focus outline
        exitToMenu.setContentAreaFilled(true);
        exitToMenu.setOpaque(true);
        exitToMenu.addMouseListener(this);
        exitToMenu.addActionListener(e -> {
            resetPieces();
            new Menu();
            parentFrame.dispose();

        });
        add(exitToMenu);

    }

    public void launchGame() {

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        // white
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(WHITE, i, 6));
        }
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));
        pieces.add(new Dragon(WHITE, 8, 7));

        // black
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(BLACK, i, 1));
        }
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
        pieces.add(new Dragon(BLACK, 8, 0));
        //test
        // pieces.add(new King(WHITE, 4, 7));
        // pieces.add(new King(BLACK, 4, 0));
        // pieces.add(new Pawn(BLACK, 1, 6));

    }

    public void resetPieces() {
        pieces.clear();
        simPieces.clear();
    }

    public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    public void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    @Override
    public void run() {
        // Game Loop

        while (running) {
            update();
            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
            }

        }
    }

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
    public void simulate() {
        canMove = false;
        validSquare = false;
        copyPieces(pieces, simPieces);
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
        activeP.x = mouse.x - Board.square_size / 2;
        activeP.y = mouse.y - Board.square_size / 2;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;
            if (activeP.isPieceThere != null) {
                simPieces.remove(activeP.isPieceThere.getIndex());
            }
            checkCastling();
            if (isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
                validSquare = true;
            }

        }
    }

    public boolean isIllegal(Piece King) {
        if (King.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece.color != King.color && piece != King && piece.canMove(King.col, King.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isKinginCheck() {
        Piece king = getKing(true);
        if (king == null)
            return false;

        // Check if any opponent piece can move to the king's position
        if (activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            System.out.println("Checking piece: " + checkingP.type);
            return true;
        } else {
            checkingP = null;
        }
        // No check if no piece can capture the king
        return false;
    }

    public boolean opponentCanCaptureKing() {
        Piece king = getKing(false);
        if (king == null)
            return false;
        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    public Piece getKing(boolean opponent) {

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    return piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    return piece;
                }
            }

        }
        return null;
    }

    public boolean isCheckmate() {
        Piece king = getKing(true);
        if (kingCanMove(king)) {
            return false;
        } else { // king can't move but there is a check

            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);
            if (colDiff == 0) {
                if (checkingP.row < king.row) {
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                if (checkingP.col < king.col) {
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) {
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                if (checkingP.row < king.row) {
                    if (checkingP.col < king.col) {
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    if (checkingP.col < king.col) {
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

        }
        return true;
    }

    public boolean isStalemate() {
        int count = 0;
        // Count the number of piece
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }
        // If only one piece (the King) is left
        if (count == 1)
            return !kingCanMove(getKing(true));

        return false;
    }

    public boolean kingCanMove(Piece king) {
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }
        return false;
    }

    public boolean isValidMove(Piece king, int colPlus, int rowPlus) {
        boolean isValidMove = false;

        // Update the king's position
        king.col += colPlus;
        king.row += rowPlus;
        if (king.canMove(king.col, king.row)) {
            if (king.isPieceThere != null) {
                simPieces.remove(king.isPieceThere.getIndex());
            }
            if (isIllegal(king) == false) {
                isValidMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return isValidMove;
    }

    public void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.col == mouse.x / Board.square_size && piece.row == mouse.y / Board.square_size) {
                    switch (piece.type) {
                        case QUEEN:
                            simPieces.add(new Queen(activeP.color, activeP.col, activeP.row));
                            break;
                        case ROOK:
                            simPieces.add(new Rook(activeP.color, activeP.col, activeP.row));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(activeP.color, activeP.col, activeP.row));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(activeP.color, activeP.col, activeP.row));
                            break;
                        case DRAGON:
                            simPieces.add(new Dragon(activeP.color, activeP.col, activeP.row));
                            piece.moved = true;
                            break;
                        default:
                            break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    

    public void changePlayer() {
        // white's turn
        if (currentColor == WHITE) {
            currentColor = BLACK;
            // reset two square move for black pawns
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoSquareMove = false;
                }
            }
        } else { // black's turn
            currentColor = WHITE;
            // reset two square move for white pawns
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoSquareMove = false;
                }
            }
        }
        activeP = null;
    }

    public boolean canPromote() {
        if (activeP.type == Type.PAWN) {
            if (activeP.color == WHITE && activeP.row == 0 || activeP.color == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Queen(activeP.color, 9, 2));
                promoPieces.add(new Rook(activeP.color, 9, 3));
                promoPieces.add(new Knight(activeP.color, 9, 4));
                promoPieces.add(new Bishop(activeP.color, 9, 5));
                promoPieces.add(new Dragon(activeP.color, 9, 6));
                return true;
            }

        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw the board
        Board.draw(g2);

        // Draw pieces
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        // Highlight the active piece
        if (activeP != null) {
            if (canMove) {
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.RED);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.square_size, activeP.row * Board.square_size, Board.square_size,
                            Board.square_size);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.square_size, activeP.row * Board.square_size, Board.square_size,
                            Board.square_size);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                }
            }
            activeP.draw(g2);
        }

        // Display promotion options or current player's turn
        g2.setFont(new Font("Posterama", Font.PLAIN, 40));
        g2.setColor(Color.WHITE);
        if (promotion) {
            
            g2.drawString("Promote to", 800, 150);
            for (Piece piece : promoPieces) {
                g2.setColor(Color.GRAY); // Semi-transparent black background
                g2.fillRect(piece.getX(piece.col), piece.getY(piece.row), Board.square_size, Board.square_size); // x, y, width, height for promotion area
            
                // Draw border
                g2.setColor(Color.WHITE);
                g2.drawRect(piece.getX(piece.col), piece.getY(piece.row), Board.square_size, Board.square_size);
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.square_size,
                        Board.square_size, null);
                
            }
        } else {
            if (currentColor == WHITE) {

                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.RED);

                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.drawString("White's turn", 840, 550);
            } else {

                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.RED);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.drawString("Black's turn", 840, 250);
            }
        }
        if (gameOver) {
            String s = "";
            if (currentColor == WHITE) {
                s = "WHITE WINS";
            } else {
                s = "BLACK WINS";
            }
            g2.setFont(new Font("Posterama", Font.PLAIN, 60));
            g2.setColor(Color.GREEN);
            g2.drawString(s, 200, 400);
        }
        if (stalemate) {
            g2.setFont(new Font("Posterama", Font.PLAIN, 60));
            g2.setColor(Color.GREEN);
            g2.drawString("STALEMATE", 200, 400);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == exitToMenu) {
            exitToMenu.setBackground(Color.GRAY); // Hover color for 'start'
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == exitToMenu) {
            exitToMenu.setBackground(Color.BLACK);
        }
    }

}
