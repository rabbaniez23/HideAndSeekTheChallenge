package com.naufal.hideandseek.model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Enemy {
    private int x, y;
    private int speed = 1; // Kecepatan musuh (pelan biar zombie-like)

    public int shootTimer = 0;

    // Animasi
    public int spriteCounter = 0;
    public int spriteNum = 0;

    public Enemy(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.shootTimer = (int)(Math.random() * 100);
    }

    // UPDATE: Menerima list obstacle untuk cek tabrakan
    public void update(int playerX, int playerY, ArrayList<Obstacle> obstacles) {
        int dx = 0;
        int dy = 0;

        // Tentukan arah gerak (Mengejar Player)
        if (x < playerX) dx = speed;
        else if (x > playerX) dx = -speed;

        if (y < playerY) dy = speed;
        else if (y > playerY) dy = -speed;

        // --- GERAK SUMBU X ---
        x += dx;
        // Cek apakah nabrak obstacle?
        Rectangle myBoundsX = getBounds();
        boolean hitX = false;
        for (Obstacle obs : obstacles) {
            if (myBoundsX.intersects(obs.getBounds())) {
                hitX = true;
                break;
            }
        }
        if (hitX) x -= dx; // Kalau nabrak, batalkan gerak X

        // --- GERAK SUMBU Y ---
        y += dy;
        // Cek apakah nabrak obstacle?
        Rectangle myBoundsY = getBounds();
        boolean hitY = false;
        for (Obstacle obs : obstacles) {
            if (myBoundsY.intersects(obs.getBounds())) {
                hitY = true;
                break;
            }
        }
        if (hitY) y -= dy; // Kalau nabrak, batalkan gerak Y


        // --- LOGIKA SHOOT & ANIMASI ---
        shootTimer++;

        spriteCounter++;
        if(spriteCounter > 12) {
            spriteNum++;
            if(spriteNum > 3) spriteNum = 0;
            spriteCounter = 0;
        }
    }

    public boolean readyToShoot() {
        if (shootTimer >= 180) {
            shootTimer = 0;
            return true;
        }
        return false;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // Hitbox Musuh (Sedikit lebih kecil dari gambar agar tidak kaku)
    public Rectangle getBounds() {
        return new Rectangle(x + 10, y + 10, 60, 60); // Asumsi gambar 84x84
    }
}