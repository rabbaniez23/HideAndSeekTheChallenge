package com.naufal.pocongpanic.presenter;

import com.naufal.pocongpanic.model.*;
import com.naufal.pocongpanic.view.Sound;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class GamePresenter {
    // Models
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Particle> particles = new ArrayList<>();

    // Game Data
    private String username;
    private int difficultyLevel; // Level 1-10

    private boolean isGameOver = false;
    private int scoreKill = 0;
    private int bulletsMissed = 0;
    private int spawnTimer = 0;

    // SFX
    private Sound soundEffect = new Sound();

    // Config (Sesuaikan dgn model)
    public final int PLAYER_SIZE = 64;
    public final int ENEMY_SIZE = 64;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;

    // Constructor Terima Level
    public GamePresenter(String username, int level) {
        this.username = username;
        this.difficultyLevel = level;
        setupGame();
    }

    public void setupGame() {
        player = new Player(360, 260);
        enemies.clear();
        bullets.clear();
        obstacles.clear();
        particles.clear();

        isGameOver = false;
        scoreKill = 0;
        bulletsMissed = 0;

        // Random Map
        Random rand = new Random();
        int obstacleCount = 10 + rand.nextInt(6);
        for (int i = 0; i < obstacleCount; i++) {
            int ox = rand.nextInt(750);
            int oy = rand.nextInt(550);
            if (Math.abs(ox - 360) > 120 || Math.abs(oy - 260) > 120) {
                obstacles.add(new Obstacle(ox, oy, 80, 90));
            } else { i--; }
        }
    }

    public void update() {
        if (isGameOver) return;

        player.update();

        // Spawn Logic (Makin kill banyak, makin cepat)
        int spawnRate = Math.max(40, 120 - (scoreKill * 2));
        spawnTimer++;
        if (spawnTimer > spawnRate) {
            int randomX = (int)(Math.random() * 750);
            enemies.add(new Enemy(randomX, 600));
            spawnTimer = 0;
        }

        Rectangle playerHitbox = new Rectangle(player.getX()+20, player.getY()+20, PLAYER_SIZE-40, PLAYER_SIZE-40);

        // Update Enemies
        for (int i = 0; i < enemies.size(); i++) {
            Enemy en = enemies.get(i);
            en.update(player.getX(), player.getY());

            if (en.readyToShoot()) shootEnemyBullet(en);

            if (en.getBounds().intersects(playerHitbox)) triggerGameOver();
        }

        // Update Bullets
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.update();
            boolean removeBullet = false;

            if (b.getX() < -50 || b.getX() > WIDTH+50 || b.getY() < -50 || b.getY() > HEIGHT+50) {
                removeBullet = true;
                if (b.isEnemyBullet) { player.addAmmo(1); bulletsMissed++; }
            }

            for (Obstacle obs : obstacles) {
                if (b.getBounds().intersects(obs.getBounds())) {
                    removeBullet = true;
                    if (b.isEnemyBullet) { player.addAmmo(1); bulletsMissed++; }
                    break;
                }
            }

            if (!removeBullet) {
                if (b.isEnemyBullet) {
                    if (b.getBounds().intersects(playerHitbox)) triggerGameOver();
                } else {
                    for (int j = 0; j < enemies.size(); j++) {
                        Rectangle enemyHitbox = new Rectangle(enemies.get(j).getX(), enemies.get(j).getY(), ENEMY_SIZE, ENEMY_SIZE);
                        if (b.getBounds().intersects(enemyHitbox)) {
                            soundEffect.playSE(2); // SFX Ledakan
                            spawnExplosion(enemies.get(j).getX(), enemies.get(j).getY());
                            enemies.remove(j);
                            scoreKill++;
                            removeBullet = true;
                            break;
                        }
                    }
                }
            }
            if (removeBullet) { bullets.remove(i); i--; }
        }

        // Update Particles
        for (int i = 0; i < particles.size(); i++) {
            if (particles.get(i).update()) { particles.remove(i); i--; }
        }
    }

    // --- PLAYER ACTION ---
    public void shootPlayer() {
        if (player.getAmmo() > 0) {
            int dir = player.direction;
            if (player.facingLeft) dir = 3;
            if (!player.facingLeft && dir == 1) dir = 1;

            soundEffect.playSE(1); // SFX Tembak
            bullets.add(new Bullet(player.getX() + PLAYER_SIZE/2, player.getY() + PLAYER_SIZE/2, dir, false));
            player.useAmmo();
        }
    }

    // Skill Dash
    public void useHeroSkill() {
        player.activateSkill();
    }

    // --- ENEMY AI (LEVELING) ---
    private void shootEnemyBullet(Enemy e) {
        int targetX = player.getX() + PLAYER_SIZE/2;
        int targetY = player.getY() + PLAYER_SIZE/2;

        // Level 1: Error Margin Besar (300px)
        // Level 10: Error Margin 0 (Sniper)
        int errorMargin = (10 - difficultyLevel) * 30;

        int jitterX = (int)((Math.random() * errorMargin * 2) - errorMargin);
        int jitterY = (int)((Math.random() * errorMargin * 2) - errorMargin);

        bullets.add(new Bullet(e.getX() + ENEMY_SIZE/2, e.getY() + ENEMY_SIZE/2, targetX + jitterX, targetY + jitterY));
    }

    private void spawnExplosion(int x, int y) {
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(x + ENEMY_SIZE/2, y + ENEMY_SIZE/2, new java.awt.Color(0, 100 + (int)(Math.random()*155), 255)));
        }
    }

    private void triggerGameOver() {
        isGameOver = true;
        DBConnection.saveScore(username, scoreKill, bulletsMissed, player.getAmmo());
    }

    // Getters
    public Player getPlayer() { return player; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public ArrayList<Obstacle> getObstacles() { return obstacles; }
    public ArrayList<Bullet> getBullets() { return bullets; }
    public ArrayList<Particle> getParticles() { return particles; }
    public boolean isGameOver() { return isGameOver; }
    public int getScore() { return scoreKill; }
    public String getUsername() { return username; }
}