package com.naufal.pocongpanic.view;

import com.naufal.pocongpanic.model.*;
import com.naufal.pocongpanic.presenter.GamePresenter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    private GamePresenter presenter;
    private Timer timer;
    private GameWindow gameWindow;
    private Image playerSheet, enemyImage, obstacleImage, bgImage;

    // Config Ukuran (Samakan dgn Presenter)
    private final int PLAYER_SIZE = 64;
    private final int ENEMY_SIZE = 64;

    public GamePanel(GameWindow window, String user, int level) {
        this.gameWindow = window;

        // Init Presenter dengan Level
        this.presenter = new GamePresenter(user, level);

        this.setFocusable(true);
        this.setBackground(new Color(20, 20, 30));

        loadImages();

        // KEYBOARD (Gerak)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_SPACE) {
                    if (presenter.isGameOver()) gameWindow.showMenu();
                    else { timer.stop(); gameWindow.showMenu(); }
                }

                if (!presenter.isGameOver()) {
                    Player p = presenter.getPlayer();
                    if (key == KeyEvent.VK_W) p.upPressed = true;
                    if (key == KeyEvent.VK_S) p.downPressed = true;
                    if (key == KeyEvent.VK_A) p.leftPressed = true;
                    if (key == KeyEvent.VK_D) p.rightPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!presenter.isGameOver()) {
                    int key = e.getKeyCode();
                    Player p = presenter.getPlayer();
                    if (key == KeyEvent.VK_W) p.upPressed = false;
                    if (key == KeyEvent.VK_S) p.downPressed = false;
                    if (key == KeyEvent.VK_A) p.leftPressed = false;
                    if (key == KeyEvent.VK_D) p.rightPressed = false;
                }
            }
        });

        // MOUSE (Action)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!presenter.isGameOver()) {
                    // KLIK KANAN -> TEMBAK
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        presenter.shootPlayer();
                    }
                    // KLIK KIRI -> SKILL DASH
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        presenter.useHeroSkill();
                    }
                }
            }
        });

        timer = new Timer(16, this);
        timer.start();
    }

    private void loadImages() {
        try {
            bgImage = new ImageIcon(getClass().getResource("/assets/rumput.png")).getImage();
            obstacleImage = new ImageIcon(getClass().getResource("/assets/pohon.png")).getImage();
            playerSheet = new ImageIcon(getClass().getResource("/assets/pocong.png")).getImage();
            enemyImage = new ImageIcon(getClass().getResource("/assets/slime.png")).getImage();
        } catch (Exception e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.update();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        Player player = presenter.getPlayer();
        ArrayList<Enemy> enemies = presenter.getEnemies();
        ArrayList<Obstacle> obstacles = presenter.getObstacles();
        ArrayList<Bullet> bullets = presenter.getBullets();
        ArrayList<Particle> particles = presenter.getParticles();

        if (bgImage != null) g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);

        for (Obstacle obs : obstacles) {
            if (obstacleImage != null) g2.drawImage(obstacleImage, obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight(), null);
        }

        int floatY = (int) (Math.sin(System.currentTimeMillis() * 0.005) * 5);

        for (Enemy en : enemies) {
            if (enemyImage != null) {
                int tileSize = 32; // Sesuaikan (32/48)
                int esx1 = en.spriteNum * tileSize;
                g2.drawImage(enemyImage, en.getX(), en.getY() + floatY, en.getX() + ENEMY_SIZE, en.getY() + ENEMY_SIZE + floatY, esx1, 0, esx1 + tileSize, tileSize, null);
            }
        }

        for (Bullet b : bullets) {
            if (b.isEnemyBullet) g2.setColor(Color.RED); else g2.setColor(Color.CYAN);
            g2.fillOval(b.getX(), b.getY(), 12, 12);
        }
        for (Particle p : particles) p.draw(g2);

        if (playerSheet != null) {
            int tileSize = 48; // Sesuaikan
            int row = player.direction;

            int sx1 = player.spriteNum * tileSize;
            int sy1 = row * tileSize;

            // Efek Visual Dash (Bayangan)
            if (player.isDashing) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // Transparan
                if (!player.facingLeft) g2.drawImage(playerSheet, player.getX()-10, player.getY()+floatY, player.getX()+PLAYER_SIZE-10, player.getY()+PLAYER_SIZE+floatY, sx1, sy1, sx1+tileSize, sy1+tileSize, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Normal lagi
            }

            if (!player.facingLeft) {
                g2.drawImage(playerSheet, player.getX(), player.getY() + floatY, player.getX() + PLAYER_SIZE, player.getY() + PLAYER_SIZE + floatY, sx1, sy1, sx1 + tileSize, sy1 + tileSize, null);
            } else {
                g2.drawImage(playerSheet, player.getX() + PLAYER_SIZE, player.getY() + floatY, player.getX(), player.getY() + PLAYER_SIZE + floatY, sx1, sy1, sx1 + tileSize, sy1 + tileSize, null);
            }
        }

        // Vignette
        float centerX = player.getX() + PLAYER_SIZE/2;
        float centerY = player.getY() + PLAYER_SIZE/2;
        if (centerX > 0 && centerY > 0) {
            float[] dist = {0.0f, 0.7f, 1.0f};
            Color[] colors = { new Color(0,0,0,0), new Color(0,0,0,50), new Color(0,0,0,200) };
            int radius = Math.max(1, Math.max(getWidth(), getHeight()));
            RadialGradientPaint p = new RadialGradientPaint(new java.awt.geom.Point2D.Float(centerX, centerY), radius, dist, colors);
            g2.setPaint(p);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // UI
        g2.setFont(new Font("Monospaced", Font.BOLD, 22));
        g2.setColor(Color.BLACK);
        g2.drawString("HUNTER: " + presenter.getUsername(), 22, 32);
        g2.drawString("AMMO  : " + player.getAmmo(), 22, 57);
        g2.drawString("SCORE : " + presenter.getScore(), 22, 82);

        g2.setColor(Color.WHITE);
        g2.drawString("HUNTER: " + presenter.getUsername(), 20, 30);
        g2.setColor(Color.CYAN);
        g2.drawString("AMMO  : " + player.getAmmo(), 20, 55);
        g2.setColor(Color.GREEN);
        g2.drawString("SCORE : " + presenter.getScore(), 20, 80);

        if (presenter.isGameOver()) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("MISSION FAILED", 200, 250);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Data saved to Archives.", 280, 300);
            g2.drawString("Press SPACE to Return", 290, 350);
        }
    }
}