package com.naufal.pocongpanic.model;

public class Player {
    private int x, y;

    // --- SKILL & MOVEMENT VARIABLES ---
    private int normalSpeed = 5;
    public boolean isDashing = false;
    private int dashTimer = 0;
    private int dashCooldown = 0;

    // --- ANIMASI ---
    public int direction = 0;
    public boolean isMoving = false;
    public boolean facingLeft = false;
    public int spriteCounter = 0;
    public int spriteNum = 0;

    // --- GAMEPLAY ---
    private int ammo = 0;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    // Config
    public static final int SIZE = 64; // Sesuaikan ukuran hero (64/80)

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        // 1. Tentukan Kecepatan (Normal vs Dash)
        int currentSpeed = normalSpeed;

        if (isDashing) {
            currentSpeed = 15; // Ngebut!
            dashTimer++;
            if (dashTimer > 10) { // Dash cuma 10 frame (sebentar)
                isDashing = false;
                dashTimer = 0;
                dashCooldown = 60; // Cooldown 1 detik (60 frame)
            }
        }

        // Kurangi Cooldown
        if (dashCooldown > 0) dashCooldown--;

        // 2. Logika Gerak
        isMoving = false;
        if (upPressed) {
            y -= currentSpeed;
            direction = 2; // Atas
            isMoving = true;
            facingLeft = false;
        }
        else if (downPressed) {
            y += currentSpeed;
            direction = 0; // Bawah
            isMoving = true;
            facingLeft = false;
        }
        else if (leftPressed) {
            x -= currentSpeed;
            direction = 1; // Kiri
            facingLeft = true;
            isMoving = true;
        }
        else if (rightPressed) {
            x += currentSpeed;
            direction = 1; // Kanan
            facingLeft = false;
            isMoving = true;
        }

        // 3. Pembatas Layar (Biar gak kabur)
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > 800 - SIZE) x = 800 - SIZE;
        if (y > 600 - SIZE - 40) y = 600 - SIZE - 40;

        // 4. Animasi Sprite
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) { // Ganti frame tiap 10 tick
                spriteNum++;
                if (spriteNum >= 6) spriteNum = 0; // Asumsi ada 6 frame jalan
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0;
        }
    }

    // --- SKILL ACTIVATION ---
    public void activateSkill() {
        if (dashCooldown == 0 && !isDashing) {
            isDashing = true;
            // System.out.println("SKILL: DASH!");
        }
    }

    // --- GETTERS & SETTERS ---
    public void addAmmo(int amount) { this.ammo += amount; }
    public void useAmmo() { if (ammo > 0) ammo--; }
    public int getAmmo() { return ammo; }
    public int getX() { return x; }
    public int getY() { return y; }
}