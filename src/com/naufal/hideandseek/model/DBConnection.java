package com.naufal.pocongpanic.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

/**
 * Class DBConnection (Model)
 * Bertugas menangani segala komunikasi dengan Database MySQL.
 * Menggunakan JDBC untuk menyimpan dan mengambil skor pemain.
 */
public class DBConnection {
    // Konfigurasi Database (Sesuaikan jika ada perubahan user/pass)
    private static final String URL = "jdbc:mysql://localhost:3306/db_game_pbo";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Membuat koneksi ke database.
     * @return Objek Connection jika berhasil, null jika gagal.
     */
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Memuat driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Membuka koneksi
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Koneksi Database Gagal: " + e.getMessage());
        }
        return con;
    }

    /**
     * Mengambil data pemain spesifik untuk fitur "Lanjut Main" (Akumulasi).
     * @param username Nama pemain yang dicari.
     * @return Array int [skor, peluru_meleset, sisa_peluru] atau null jika user baru.
     */
    public static int[] loadPlayerData(String username) {
        int[] data = null;
        try {
            Connection con = getConnection();
            // Query untuk mengambil data berdasarkan username
            String sql = "SELECT skor, peluru_meleset, sisa_peluru FROM tbenefit WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            // Jika data ditemukan, simpan ke dalam array
            if (rs.next()) {
                data = new int[3];
                data[0] = rs.getInt("skor");
                data[1] = rs.getInt("peluru_meleset");
                data[2] = rs.getInt("sisa_peluru");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data; // Mengembalikan null jika pemain belum terdaftar
    }

    /**
     * Menyimpan atau Memperbarui data pemain ke database.
     * Logikanya: Jika username ada -> Update. Jika tidak ada -> Insert.
     *
     * @param username Nama pemain.
     * @param currentScore Skor total saat ini.
     * @param currentMissed Jumlah peluru meleset total.
     * @param currentAmmo Sisa peluru yang dimiliki.
     */
    public static void saveScore(String username, int currentScore, int currentMissed, int currentAmmo) {
        try {
            Connection con = getConnection();

            // 1. Cek apakah username sudah ada di tabel?
            String checkSql = "SELECT * FROM tbenefit WHERE username = ?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // --- USER LAMA: Lakukan UPDATE ---
                String updateSql = "UPDATE tbenefit SET skor = ?, peluru_meleset = ?, sisa_peluru = ? WHERE username = ?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);
                updatePs.setInt(1, currentScore);
                updatePs.setInt(2, currentMissed);
                updatePs.setInt(3, currentAmmo);
                updatePs.setString(4, username);
                updatePs.executeUpdate();
                System.out.println("Data Progress Disimpan untuk: " + username);

            } else {
                // --- USER BARU: Lakukan INSERT ---
                String insertSql = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES (?, ?, ?, ?)";
                PreparedStatement insertPs = con.prepareStatement(insertSql);
                insertPs.setString(1, username);
                insertPs.setInt(2, currentScore);
                insertPs.setInt(3, currentMissed);
                insertPs.setInt(4, currentAmmo);
                insertPs.executeUpdate();
                System.out.println("User Baru Dibuat & Disimpan!");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengambil seluruh data Highscore untuk ditampilkan di Tabel Menu.
     * @return DefaultTableModel yang siap dimasukkan ke JTable.
     */
    public static DefaultTableModel getTableData() {
        // Nama-nama kolom tabel
        String[] columnNames = {"Username", "Skor", "Peluru Meleset", "Sisa Peluru"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();
            // Mengambil semua data diurutkan dari SKOR TERTINGGI (DESC)
            ResultSet rs = st.executeQuery("SELECT * FROM tbenefit ORDER BY skor DESC");

            while (rs.next()) {
                String user = rs.getString("username");
                int score = rs.getInt("skor");
                int missed = rs.getInt("peluru_meleset");
                int remain = rs.getInt("sisa_peluru");
                // Masukkan baris data ke model tabel
                model.addRow(new Object[]{user, score, missed, remain});
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
}