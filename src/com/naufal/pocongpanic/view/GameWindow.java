package com.naufal.pocongpanic.view;

import javax.swing.*;

public class GameWindow extends JFrame {

    // Objek Musik Background
    Sound music = new Sound();

    public GameWindow() {
        this.setTitle("Retro Forest Hunter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(800, 600);

        // Putar lagu index 0 (Looping)
        playMusic(0);

        showMenu();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void showMenu() {
        this.getContentPane().removeAll();
        MenuPanel menu = new MenuPanel(this);
        this.add(menu);
        this.revalidate();
        this.repaint();
    }

    // UPDATE: Menerima parameter Level
    public void startGame(String username, int level) {
        this.getContentPane().removeAll();
        // Oper Level ke GamePanel
        GamePanel game = new GamePanel(this, username, level);
        this.add(game);
        game.requestFocusInWindow(); // Agar keyboard terbaca
        this.revalidate();
        this.repaint();
    }
}