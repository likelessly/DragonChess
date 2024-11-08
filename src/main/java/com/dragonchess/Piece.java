package com.dragonchess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class Piece {
    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public boolean moved, twoSquareMove;
    public Piece isPieceThere;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        this.x = getX(col);
        this.y = getY(row);
        this.preCol = col;
        this.preRow = row;
    }

    public BufferedImage getImage(String path) {
        BufferedImage image = null;
        try (InputStream svgFile = getClass().getResourceAsStream("/" + path)) {
            if (svgFile == null) {
                System.err.println("File not found: " + path);
                return null;
            }
            TranscoderInput input = new TranscoderInput(svgFile);
            // Create an instance of the subclassed PNGTranscoder
            ImageTranscoder transcoder = new ImageTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) ChessBoard.square_size);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) ChessBoard.square_size);

            // Perform the transcoding
            transcoder.transcode(input, null);
            image = transcoder.getBufferedImage(); // Get the resulting BufferedImage
        } catch (Exception e) {
            System.err.println("Error loading SVG: " + path);
            e.printStackTrace();
        }
        return image;
    }

    // Subclass of PNGTranscoder
    private class ImageTranscoder extends PNGTranscoder {
        private BufferedImage bufferedImage;

        @Override
        public BufferedImage createImage(int width, int height) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            return bufferedImage;
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            // You can save the image or handle it as needed
        }

        public BufferedImage getBufferedImage() {
            return bufferedImage; // Return the created image
        }
    }

    public int getX(int col) {
        return col * ChessBoard.square_size;
    }

    public int getY(int row) {
        return row * ChessBoard.square_size;
    }

    public int getCol(int x) {
        return (x + ChessBoard.square_size / 2) / ChessBoard.square_size;
    }

    public int getRow(int y) {
        return (y + ChessBoard.square_size / 2) / ChessBoard.square_size;
    }

    public void updatePosition() {
        // check en passant
        if (type == Type.PAWN) {
            twoSquareMove = Math.abs(row - preRow) == 2;
        }
        x = getX(col);
        y = getY(row);
        // debug move
        System.out.println((color == Panel.WHITE ? "White" : "Black") + " Moved " +
                type + " from (" + preRow + "," + preCol +
                ") to (" + row + "," + col + ")");
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;

    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        return preCol == targetCol && preRow == targetRow;
    }

    public Piece isPieceThere(int targetCol, int targetRow) {
        for (Piece piece : Panel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public int getIndex() {
        for (int index = 0; index < Panel.simPieces.size(); index++) {
            if (Panel.simPieces.get(index) == this) {
                return index;
            }
        }
        return 0;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        isPieceThere = isPieceThere(targetCol, targetRow);
        if (isPieceThere == null) { // no piece
            return true;
        } else {
            if (isPieceThere.color != this.color) {
                return true;
            } else {
                isPieceThere = null;
            }
        }
        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        // moving left
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece piece : Panel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    isPieceThere = piece;
                    return true;
                }
            }
        }
        // right
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece piece : Panel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    isPieceThere = piece;
                    return true;
                }
            }
        }
        // up
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece piece : Panel.simPieces) {
                if (piece.row == r && piece.col == targetCol) {
                    isPieceThere = piece;
                    return true;
                }
            }
        }
        // down
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece piece : Panel.simPieces) {
                if (piece.row == r && piece.col == targetCol) {
                    isPieceThere = piece;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        int colStep, rowStep;

        // Determine the direction of the diagonal
        if (targetCol > preCol && targetRow > preRow) { // Down-right
            colStep = 1;
            rowStep = 1;
        } else if (targetCol > preCol && targetRow < preRow) { // Up-right
            colStep = 1;
            rowStep = -1;
        } else if (targetCol < preCol && targetRow > preRow) { // Down-left
            colStep = -1;
            rowStep = 1;
        } else if (targetCol < preCol && targetRow < preRow) { // Up-left
            colStep = -1;
            rowStep = -1;
        } else {
            return false; // Not a diagonal move
        }

        int col = preCol + colStep;
        int row = preRow + rowStep;

        // Check each square along the diagonal path
        while (col != targetCol && row != targetRow) {
            for (Piece piece : Panel.simPieces) {
                if (piece.col == col && piece.row == row) {
                    isPieceThere = piece;
                    return true;
                }
            }
            col += colStep;
            row += rowStep;
        }

        return false; // No piece found along the diagonal
    }

    public List<int[]> getPossibleMoves(List<Piece> simPieces) {
        List<int[]> possibleMoves = new ArrayList<>();

        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                if (canMove(col, row)) { // assumes canMove is implemented for each piece type
                    possibleMoves.add(new int[] { col, row });
                }
            }
        }
        return possibleMoves;
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, ChessBoard.square_size, ChessBoard.square_size, null);
        } else {
            System.err.println("Image not loaded for piece at (" + col + ", " + row + ")");
        }
    }
}
