package com.dragonchess;

import javax.swing.JFrame;

public class BotGame {
    private JFrame gameWindow;
    public BotPanel botGame;

    public BotGame() {
        gameWindow = new JFrame("Dragon Chess with Bot");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setSize(1000, 800);
        gameWindow.setResizable(true);
        gameWindow.setLocationRelativeTo(null);

        botGame = new BotPanel(gameWindow); // Use BotPanel instead of Panel
        gameWindow.add(botGame);

        gameWindow.pack();
        gameWindow.setVisible(true);
        botGame.launchGame();
    }

    
}
