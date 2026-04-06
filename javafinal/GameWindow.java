import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.awt.Desktop;

public class GameWindow extends JFrame {

    private String username;
    private JFrame parentFrame;

    public GameWindow(String username) {
        this(username, null);
    }

    public GameWindow(String username, JFrame parentFrame) {
        this.username = username;
        this.parentFrame = parentFrame;

        setTitle("🎮 Endmin Showdown - Chess Online");
        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Chess-themed main panel with elegant brown gradient
        JPanel mainPanel = UIUtils.createChessThemedPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Title
        JLabel titleLabel = new JLabel("♟ Chess Online", JLabel.CENTER);
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 26);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 26);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JButton playButton = new JButton("▶ Play Chess Online");
        JButton leaderboardButton = new JButton("🏆 Global Leaderboard");
        JButton backButton = new JButton("⬅ Back");

        Font buttonFont = new Font("Segoe UI Emoji", Font.BOLD, 16);
        if (buttonFont.getName().equals("Dialog")) {
            buttonFont = new Font("DejaVu Sans", Font.BOLD, 16);
        }
        playButton.setFont(buttonFont);
        leaderboardButton.setFont(buttonFont);
        backButton.setFont(buttonFont);
        
        // Style buttons for chess theme (gold/bronze accents)
        styleChessButton(playButton);
        styleChessButton(leaderboardButton);
        styleChessButton(backButton);

        panel.add(playButton);
        panel.add(leaderboardButton);
        panel.add(backButton);

        mainPanel.add(panel, BorderLayout.CENTER);

        // Button Actions
        playButton.addActionListener(this::openRealTimeChess);
        leaderboardButton.addActionListener(this::openLeaderboard);
        backButton.addActionListener(e -> {
            dispose();
            Player p = DatabaseFactory.getDatabase().getPlayer(username);
            if (p != null) {
                new MainGameWindow(p, parentFrame);
            }
        });
        // If parent frame is maximized, maximize this frame too
        if (parentFrame != null && (parentFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        setVisible(true);
    }

    // ===== OPEN REAL CHESS GAME =====
    private void openRealTimeChess(ActionEvent e) {
        Player player = DatabaseFactory.getDatabase().getPlayer(username);
        
        // Check if Lichess account is linked
        if (player.getLichessUsername() == null || player.getLichessUsername().isEmpty()) {
            int result = JOptionPane.showConfirmDialog(this,
                    "You need to link your Lichess account first.\n\nWould you like to link it now?",
                    "Link Lichess Account",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                openLichessLinkDialog(player);
            }
            return;
        }
        
        // Open Lichess with the linked account
        try {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", 
                    "https://lichess.org/@/" + player.getLichessUsername()).start();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to open Lichess.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openLichessLinkDialog(Player player) {
        JDialog dialog = new JDialog(this, "Link Lichess Account", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel instructionLabel = new JLabel("<html>Enter your Lichess username to link it to your account.<br/>You only need to do this once!</html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(instructionLabel);

        JLabel usernameLabel = new JLabel("Lichess Username:");
        mainPanel.add(usernameLabel);

        JTextField usernameField = new JTextField();
        mainPanel.add(usernameField);

        dialog.add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton saveButton = new JButton("Save & Open");
        saveButton.addActionListener(e -> {
            String lichessUsername = usernameField.getText().trim();
            if (lichessUsername.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a Lichess username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            player.setLichessUsername(lichessUsername);
            DatabaseFactory.getDatabase().save();
            dialog.dispose();
            
            // Now open Lichess
            try {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", 
                        "https://lichess.org/@/" + lichessUsername).start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to open Lichess.\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ===== OPEN GLOBAL LEADERBOARD =====
    private void openLeaderboard(ActionEvent e) {
        try {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", "https://lichess.org/player/top/200/rapid").start();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to open Leaderboard.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleChessButton(JButton button) {
        button.setBackground(new Color(184, 134, 11)); // Dark goldenrod
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(218, 165, 32)); // Goldenrod (lighter)
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(184, 134, 11)); // Dark goldenrod
            }
        });
    }
}