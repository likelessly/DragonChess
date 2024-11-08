package com.dragonchess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class ChessBoard extends JPanel{
    private int column = 8;
    private int row = 8;
    int extra = 1;
    public static int square_size = 100;
    public ChessBoard(){
        this.setPreferredSize(new Dimension((column + extra) * square_size, row * square_size));
        System.out.println("ChessBoard created");
    }
    public void draw(Graphics2D g){
        for(int r = 0 ; r < row; r++){
            for(int c = 0; c < column; c++){
                if((c+r) % 2 == 0){
                    g.setColor(new Color(210, 165, 125));
                }
                else g.setColor(new Color(175, 115, 70));
                g.fillRect(c * square_size, r * square_size, square_size, square_size);
            }
            
        }
        g.setColor(new Color(255, 215, 0)); // Choose a color for the extra square
        g.fillRect((column) * square_size, 7 * square_size , square_size, square_size); // position the extra square at the bottom-right corner
         
        g.fillRect((column)* square_size, 0 * square_size, square_size, square_size);
        // Optional: Draw a border around the extra square
        g.setColor(Color.BLACK);
        g.drawRect(column * square_size, row * square_size, square_size, square_size);
    }
}