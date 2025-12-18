package com.naufal.pocongpanic.view;

import com.naufal.pocongpanic.model.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class MenuPanel extends JPanel {
    private JTextField usernameField;
    private JTable scoreTable;
    private JSpinner levelSpinner; // Input Level
    private GameWindow gameWindow;
    private Image bgImage;

    // Tema Warna Kayu
    private Color colorTableBg = new Color(210, 180, 140);
    private Color colorText = new Color(101, 67, 33);
    private Color colorHeader = new Color(139, 69, 19);
    private Color colorButton = new Color(222, 184, 135);

    public MenuPanel(GameWindow window) {
        this.gameWindow = window;
        this.setLayout(null);

        try {
            bgImage = new ImageIcon(getClass().getResource("/assets/rumput.png")).getImage();
        } catch (Exception e) {}

        // Judul
        JLabel titleLabel = new JLabel("RETRO FOREST HUNTER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(255, 228, 196));
        titleLabel.setBounds(100, 30, 600, 50);

        JLabel shadowLabel = new JLabel("RETRO FOREST HUNTER", SwingConstants.CENTER);
        shadowLabel.setFont(new Font("Serif", Font.BOLD, 40));
        shadowLabel.setForeground(new Color(50, 20, 0));
        shadowLabel.setBounds(103, 33, 600, 50);

        add(titleLabel);
        add(shadowLabel);

        // Panel Kayu (Tengah)
        JPanel woodPanel = new JPanel();
        woodPanel.setBounds(150, 100, 500, 400);
        woodPanel.setBackground(new Color(160, 82, 45));
        woodPanel.setLayout(null);
        woodPanel.setBorder(BorderFactory.createLineBorder(new Color(101, 67, 33), 5));

        // Input Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(30, 20, 100, 30);
        woodPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(30, 50, 200, 35);
        usernameField.setBackground(colorTableBg);
        usernameField.setForeground(colorText);
        usernameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        woodPanel.add(usernameField);

        // Input Level (BARU)
        JLabel levelLabel = new JLabel("Level (1-10)");
        levelLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBounds(250, 20, 150, 30);
        woodPanel.add(levelLabel);

        levelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1)); // Default 1, Min 1, Max 10
        levelSpinner.setBounds(250, 50, 80, 35);
        levelSpinner.setFont(new Font("Monospaced", Font.BOLD, 18));
        woodPanel.add(levelSpinner);

        // Tabel Highscore
        JLabel scoreLabel = new JLabel("Highscore Table");
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(30, 100, 200, 30);
        woodPanel.add(scoreLabel);

        scoreTable = new JTable();
        scoreTable.setFont(new Font("Monospaced", Font.BOLD, 14));
        scoreTable.setRowHeight(25);
        scoreTable.setBackground(colorTableBg);
        scoreTable.setForeground(colorText);

        JTableHeader header = scoreTable.getTableHeader();
        header.setFont(new Font("Monospaced", Font.BOLD, 14));
        header.setBackground(colorHeader);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setBounds(30, 130, 300, 200);
        scrollPane.getViewport().setBackground(colorTableBg);
        woodPanel.add(scrollPane);

        // Tombol Play
        JButton playButton = createWoodButton("Play", 350, 100);
        playButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            int level = (int) levelSpinner.getValue(); // Ambil Level

            if (!username.isEmpty()) {
                gameWindow.startGame(username, level); // Kirim Level
            } else {
                JOptionPane.showMessageDialog(null, "Username required!");
            }
        });
        woodPanel.add(playButton);

        // Tombol Quit
        JButton quitButton = createWoodButton("Quit", 350, 170);
        quitButton.addActionListener(e -> System.exit(0));
        woodPanel.add(quitButton);

        add(woodPanel);
        refreshTable();
    }

    private JButton createWoodButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 100, 50);
        btn.setFont(new Font("Serif", Font.BOLD, 20));
        btn.setBackground(colorButton);
        btn.setForeground(new Color(50, 20, 0));
        btn.setBorder(BorderFactory.createBevelBorder(0));
        btn.setFocusPainted(false);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
    }

    public void refreshTable() {
        scoreTable.setModel(DBConnection.getTableData());
    }
}