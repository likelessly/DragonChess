package com.dragonchess;
import javax.swing.JFrame;
public class Game {
    public JFrame gameWindow;
    public Panel game;
    
    public Game(){
        gameWindow = new JFrame("Dragon Chess");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setSize(1000, 800);
        gameWindow.setResizable(true);
        gameWindow.setLocationRelativeTo(null);
        
        game = new Panel(gameWindow);
        gameWindow.add(game);
        
        gameWindow.pack();
        gameWindow.setVisible(true);
        game.launchGame();
    }
}