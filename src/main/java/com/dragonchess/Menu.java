package com.dragonchess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Menu implements MouseListener, ActionListener {
    JFrame window;
    JPanel titlePanel, startPanel;
    JLabel GameName = new JLabel("Dragon Chess");
    JButton start = new JButton("Play with Player");
    JButton startBot = new JButton("Play with Bot");
    JButton exit = new JButton("Exit");
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    public Menu() {

        window = new JFrame("Main Menu");
        window.setSize(WIDTH, HEIGHT);
        window.setBackground(new Color(153, 76, 0));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.getContentPane().setBackground(Color.BLACK);
        window.setLocationRelativeTo(null);

        titlePanel = new JPanel();
        startPanel = new JPanel();

        titlePanel.setBounds(100, 100, 600, 100);
        // titlePanel.setBackground(Color.BLACK);
        // set text title

        GameName.setForeground(Color.WHITE);
        Border border = BorderFactory.createLineBorder(Color.WHITE);
        GameName.setFont(new Font("Posterama", Font.BOLD, 50));
        GameName.setVerticalAlignment(JLabel.CENTER);
        GameName.setHorizontalAlignment(JLabel.CENTER);

        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(border);
        titlePanel.setBackground(Color.BLACK);
        titlePanel.add(GameName);

        startPanel.setBackground(Color.BLACK);
        startPanel.setBounds(300, 800 / 2 - 100, 200, 200);
        startPanel.setBorder(BorderFactory.createEmptyBorder());
        startPanel.setLayout(new GridLayout(3, 1, 0, 25));

        start.setBackground(Color.BLACK);
        start.setForeground(Color.WHITE);
        start.setFocusPainted(false); // Removes the focus outline
        start.setBorder(border); // Removes the border
        start.setContentAreaFilled(true);
        start.setOpaque(true);
        startPanel.add(start);
        start.addMouseListener(this);
        start.addActionListener(e -> {
            Game game = new Game();
            window.dispose();});

        
        startBot.setBackground(Color.BLACK);
        startBot.setForeground(Color.WHITE);
        startBot.setFocusPainted(false); // Removes the focus outline
        startBot.setBorder(border); // Removes the border
        startBot.setContentAreaFilled(true);
        startBot.setOpaque(true);
        startBot.addMouseListener(this);
        startBot.addActionListener(e -> {
            BotGame botGame = new BotGame();
            window.dispose();});
        startPanel.add(startBot);

        
        // start.setFont(new Font("Posterama", Font.BOLD, 20));
        exit.setBackground(Color.BLACK);
        exit.setForeground(Color.WHITE);
        exit.setFocusPainted(false); // Removes the focus outline
        exit.setBorder(border); // Removes the border
        exit.setContentAreaFilled(true);
        exit.setOpaque(true);
        exit.addMouseListener(this);
        exit.addActionListener(e -> System.exit(0));
        startPanel.add(exit);

        window.add(titlePanel);
        window.add(startPanel);
        window.setVisible(true);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == start) {
            start.setBackground(Color.GRAY);  // Hover color for 'start'
        } else if (e.getSource() == startBot) {
            startBot.setBackground(Color.GRAY);  // Hover color for 'startBot'
        } else if (e.getSource() == exit) {
            exit.setBackground(Color.GRAY);  // Hover color for 'exit'
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == start) {
            start.setBackground(Color.BLACK);
        } else if (e.getSource() == startBot) {
            startBot.setBackground(Color.BLACK);
        } else if (e.getSource() == exit) {
            exit.setBackground(Color.BLACK);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    public static void main(String[] args) {
        Menu menu = new Menu();
    }
}
