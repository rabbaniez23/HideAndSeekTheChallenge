package com.naufal.hideandseek.model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Player {
    private int x, y;

    // --- MOVEMENT ---
    private int normalSpeed = 5;
    public boolean isDashing = false;
    private int dashTimer = 0;
    private int dashCooldown = 0;

    // --- SKILLS ---
    public int cdSkill1 = 0;
    public final int MAX_CD_1 = 300;

    public int cdSkill2 = 0;
    public int durationSkill2 = 0;
    public final int MAX_CD_2 = 480;

    public int cdSkill3 = 0;
    public int durationSkill3 = 0;
    public final int MAX_CD_3 = 600;

    // --- ANIMASI ---
    public int direction = 0;
    public boolean isMoving = false;
    public boolean facingLeft = false;
    public int spriteCounter = 0;
    public int spriteNum = 0;

    // --- GAMEPLAY ---
    private int ammo = 0;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    public static final int SIZE = 64;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    // UPDATE: Menerima list obstacle untuk cek tabrakan
    public void update(ArrayList<Obstacle> obstacles) {
        // 1. Update Timer Skills
        if (cdSkill1 > 0) cdSkill1--;
        if (cdSkill2 > 0) cdSkill2--;
        if (durationSkill2 > 0) durationSkill2--;
        if (cdSkill3 > 0) cdSkill3--;
        if (durationSkill3 > 0) durationSkill3--;

        // 2. Dash Logic
        int currentSpeed = normalSpeed;
        if (isDashing) {
            currentSpeed = 15;
            dashTimer++;
            if (dashTimer > 10) {
                isDashing = false;
                dashTimer = 0;
                dashCooldown = 60;
            }
        }
        if (dashCooldown > 0) dashCooldown--;

        // 3. Logic Gerak dengan Tabrakan (Collision Detection)
        // Kita gerakkan sumbu X dan Y secara terpisah agar bisa "sliding" (meluncur di tembok)

        isMoving = false;
        int dx = 0;
        int dy = 0;

        if (upPressed) { dy -= currentSpeed; direction = 2; isMoving = true; facingLeft = false; }
        else if (downPressed) { dy += currentSpeed; direction = 0; isMoving = true; facingLeft = false; }
        else if (leftPressed) { dx -= currentSpeed; direction = 1; facingLeft = true; isMoving = true; }
        else if (rightPressed) { dx += currentSpeed; direction = 1; facingLeft = false; isMoving = true; }

        // --- CEK SUMBU X ---
        x += dx;
        // Cek Batas Layar X
        if (x < 0) x = 0;
        if (x > 800 - SIZE) x = 800 - SIZE;

        // Cek Tabrakan Obstacle X
        Rectangle myBoundsX = new Rectangle(x + 15, y + 20, SIZE - 30, SIZE - 30); // Hitbox sedikit lebih kecil dari gambar
        for (Obstacle obs : obstacles) {
            if (myBoundsX.intersects(obs.getBounds())) {
                x -= dx; // Batalkan gerakan X jika nabrak
                break;
            }
        }

        // --- CEK SUMBU Y ---
        y += dy;
        // Cek Batas Layar Y
        if (y < 0) y = 0;
        if (y > 600 - SIZE - 40) y = 600 - SIZE - 40;

        // Cek Tabrakan Obstacle Y
        Rectangle myBoundsY = new Rectangle(x + 15, y + 20, SIZE - 30, SIZE - 30);
        for (Obstacle obs : obstacles) {
            if (myBoundsY.intersects(obs.getBounds())) {
                y -= dy; // Batalkan gerakan Y jika nabrak
                break;
            }
        }

        // 4. Animasi
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum++;
                if (spriteNum >= 6) spriteNum = 0;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0;
        }
    }

    // Method overload lama (untuk kompatibilitas jika dipanggil tanpa param, walau jarang)
    public void update() {
        update(new ArrayList<>());
    }

    public void activateDash() {
        if (dashCooldown == 0 && !isDashing) isDashing = true;
    }

    // --- GETTERS & SETTERS ---
    public void addAmmo(int amount) { this.ammo += amount; }
    public void useAmmo() { if (ammo > 0) ammo--; }
    public int getAmmo() { return ammo; }
    public int getX() { return x; }
    public int getY() { return y; }

    public int getDashCooldown() { return dashCooldown; }
    public boolean isMultishotActive() { return durationSkill2 > 0; }
    public boolean isInvincible() { return durationSkill3 > 0; }
}