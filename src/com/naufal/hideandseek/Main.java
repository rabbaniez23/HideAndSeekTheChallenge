package com.naufal.hideandseek;

/*
 * Saya Naufal Rizki Rabbani dengan NIM 2410946 mengerjakan Evaluasi Tugas Masa Depan
 * dalam mata kuliah Desain Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
 * tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
 */

// Import package view untuk memanggil jendela utama
import com.naufal.hideandseek.view.GameWindow;
import javax.swing.*;
import java.awt.*;

// ---------------------------------------------------------------------------------------------
// Kelas Main merupakan kelas utama (Entry Point) yang bertanggung jawab untuk menjalankan
// aplikasi permainan "Hide and Seek The Challenge".
// ---------------------------------------------------------------------------------------------
//
// Fungsionalitas utama dari kelas ini meliputi:
// - Menjadi titik awal eksekusi program Java (public static void main).
// - Menginisialisasi objek GameWindow yang mengatur seluruh tampilan GUI (Menu & Game).
// - Memastikan aplikasi berjalan dengan thread yang aman (secara implisit via Swing).
//
// Kelas ini sengaja dibuat sederhana untuk memisahkan logika inisialisasi (bootstrapping)
// dengan logika tampilan (View) dan logika permainan (Presenter/Model).
// ---------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------
// Credit Assets (Sumber Aset):
// - Gambar Player            : [itch.io]
// - Gambar Enemy (Slime)     : [itch.io]
// - Gambar Background        : [generate chatgpt]
// - Gambar Obstacle          : [generate chatgpt]
// - Gambar skill             : [generate chatgpt]
// - Musik Latar (BGM)        : [pixebay.com]
// - Sound Effect (SFX)       : [pixebay.com]
// ---------------------------------------------------------------------------------------------

public class Main {

    public static void main(String[] args) {
        // Memanggil Jendela Game (GameWindow) supaya muncul di layar.
        // Constructor GameWindow akan otomatis memuat MenuPanel sebagai tampilan awal.
        new GameWindow();
    }
}