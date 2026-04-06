import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;

// gradient utility reused from LoginWindow
import java.awt.GradientPaint;

public class MainGameWindow implements ActionListener {

    private JFrame frame;
    private Player player;
    private JFrame parentFrame;
    private JButton chessButton;
    private JButton logicButton;
    private JButton memoryButton;
    private JButton globalLeaderboardButton;
    private JButton logoutButton;
    private JButton friendsButton;
    private JButton profileButton;

    public MainGameWindow(Player player) {
        this(player, null);
    }

    public MainGameWindow(Player player, JFrame parentFrame) {
        UIUtils.initLookAndFeel();
        this.player = player;
        this.parentFrame = parentFrame;

        frame = new JFrame("🎮 Endmin Showdown - Main Menu");
        frame.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Premium animated background with gaming theme
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(null);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        frame.setContentPane(mainPanel);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome to Endmin Showdown! 🎮");
        Font welcomeFont = new Font("Segoe UI Emoji", Font.BOLD, 32);
        if (welcomeFont.getName().equals("Dialog")) {
            welcomeFont = new Font("DejaVu Sans", Font.BOLD, 32);
        }
        welcomeLabel.setFont(welcomeFont);
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);
        
        mainPanel.add(headerPanel);

        // Games Panel (Cards) — slides in from LEFT
        JPanel gamesPanel = UIUtils.createSlideInPanel(new GridLayout(3, 1, 15, 15), "LEFT", 200);
        gamesPanel.setOpaque(false);
        gamesPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        chessButton = createGameCard("♟ Chess Online", "Play online chess", new Color(34, 139, 87));
        logicButton = createGameCard("🧩 Logic Game", "Solve puzzles", new Color(0, 150, 136));
        memoryButton = createGameCard("🧠 Memory Game", "Test your memory", new Color(77, 182, 172));

        chessButton.addActionListener(this);
        logicButton.addActionListener(this);
        memoryButton.addActionListener(this);

        gamesPanel.add(chessButton);
        gamesPanel.add(logicButton);
        gamesPanel.add(memoryButton);

        mainPanel.add(gamesPanel);

        // Options Panel (Cards) — slides in from RIGHT
        JPanel optionsPanel = UIUtils.createSlideInPanel(new GridLayout(3, 1, 15, 15), "RIGHT", 400);
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        globalLeaderboardButton = createGameCard("🏆 Global Leaderboard", "View top players", new Color(76, 175, 80));
        friendsButton = createGameCard("👥 Friends", "Manage friends", new Color(104, 211, 145));
        profileButton = createGameCard("👤 My Profile", "Stats, achievements & more", new Color(80, 120, 220));

        globalLeaderboardButton.addActionListener(this);
        friendsButton.addActionListener(this);
        profileButton.addActionListener(this);

        optionsPanel.add(globalLeaderboardButton);
        optionsPanel.add(friendsButton);
        optionsPanel.add(profileButton);

        mainPanel.add(optionsPanel);

        // Logout button with premium styling
        JPanel logoutPanel = new JPanel();
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 50, 0));

        logoutButton = new JButton("🚪 Logout");
        Font logoutFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
        if (logoutFont.getName().equals("Dialog")) {
            logoutFont = new Font("DejaVu Sans", Font.BOLD, 14);
        }
        logoutButton.setFont(logoutFont);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(220, 60, 60));
        logoutButton.setPreferredSize(new Dimension(140, 45));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setOpaque(true);
        logoutButton.addActionListener(this);
        
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(250, 80, 80));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 60, 60));
            }
        });

        logoutPanel.add(logoutButton);
        mainPanel.add(logoutPanel);

        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);

        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }

        frame.setVisible(true);

        // Fade-in animation
        try {
            frame.setOpacity(0f);
            javax.swing.Timer fadeIn = new javax.swing.Timer(16, null);
            float[] op = {0f};
            fadeIn.addActionListener(ev -> {
                op[0] = Math.min(op[0] + 0.05f, 1f);
                try { frame.setOpacity(op[0]); } catch (Exception ignored) {}
                if (op[0] >= 1f) fadeIn.stop();
            });
            fadeIn.start();
        } catch (Exception ignored) {}
    }

    private JButton createGameCard(String title, String subtitle, Color bgColor) {
        // Determine specific theme based on game type
        Color primaryColor = bgColor;
        Color accentColor;
        
        if (title.contains("Chess")) {
            primaryColor = new Color(139, 69, 19); // Saddle brown
            accentColor = new Color(184, 134, 11); // Dark goldenrod
        } else if (title.contains("Logic")) {
            primaryColor = new Color(25, 103, 210); // Deep blue
            accentColor = new Color(100, 200, 255); // Light blue
        } else if (title.contains("Memory")) {
            primaryColor = new Color(128, 29, 196); // Purple
            accentColor = new Color(220, 100, 255); // Light purple
        } else if (title.contains("Global")) {
            primaryColor = new Color(34, 139, 87); // Green
            accentColor = new Color(0, 200, 150); // Teal
        } else {
            primaryColor = new Color(0, 150, 136); // Teal
            accentColor = new Color(100, 255, 200); // Light cyan
        }
        
        final Color finalPrimaryColor = primaryColor;
        final Color finalAccentColor = accentColor;
        
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Premium gradient background
                GradientPaint gradient = new GradientPaint(0, 0, finalPrimaryColor,
                        getWidth(), getHeight(), finalPrimaryColor.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Accent line on the left
                g2d.setColor(finalAccentColor);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawLine(0, 0, 0, getHeight());
                
                // Multiple shadow layers for depth
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 20, 20);
                
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                
                super.paintComponent(g);
            }
        };

        button.setLayout(new BorderLayout(10, 5));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 20));
        button.setPreferredSize(new Dimension(600, 80));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title with enhanced styling
        JLabel titleLabel = new JLabel(title);
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 20);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 20);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);

        // Subtitle with styling
        JLabel subtitleLabel = new JLabel(subtitle);
        Font subtitleFont = new Font("Segoe UI Emoji", Font.PLAIN, 13);
        if (subtitleFont.getName().equals("Dialog")) {
            subtitleFont = new Font("DejaVu Sans", Font.PLAIN, 13);
        }
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        // Add an icon panel for visual interest
        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(50, 50));
        
        button.add(textPanel, BorderLayout.CENTER);

        // Premium hover effect with size and color change
        button.addMouseListener(new MouseAdapter() {
            private Color originalColor = finalPrimaryColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setPreferredSize(new Dimension(610, 85));
                button.getParent().revalidate();
                // Visual feedback by updating button appearance
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setPreferredSize(new Dimension(600, 80));
                button.getParent().revalidate();
                button.repaint();
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == chessButton) {

            // Show premium dialog to choose between Play or Leaderboard
            String[] options = {"Play Game", "View Leaderboard"};
                int choice = showCustomOptionDialog("Chess Online", "What would you like to do?", options);

            if (choice == 0) {
                // Check if Lichess account is linked
                if (player.getLichessUsername() == null || player.getLichessUsername().isEmpty()) {
                    String[] linkOptions = {"Yes, Link Now", "Not Now"};
                    int result = showCustomOptionDialog("Link Lichess Account",
                            "You need to link your Lichess account first.<br><br>Would you like to link it now?",
                            linkOptions);
                    
                    if (result == 0) {
                        openLichessLinkDialog();
                    }
                } else {
                    // Open Lichess with linked account
                    try {
                        Runtime.getRuntime().exec(
                                "rundll32 url.dll,FileProtocolHandler https://lichess.org/@/" + player.getLichessUsername()
                        );
                    } catch (Exception ex) {
                        showCustomOptionDialog("Error", "Failed to open Chess website.", new String[]{"OK"});
                    }
                }
            } else if (choice == 1) {
                // View Leaderboard - Show Lichess leaderboard
                new LichessLeaderboardWindow(frame);
            }

        } else if (e.getSource() == logicButton) {

            String[] options = {"Play Game", "View Leaderboard"};
            int choice = showCustomOptionDialog("🧩 Logic Game", "What would you like to do?", options);
            if (choice == 0) {
                new LogicGame(DatabaseFactory.getDatabase(), player.getUsername(), frame);
            } else if (choice == 1) {
                new GameLeaderboardWindow(player, "Logic", frame);
            }

        } else if (e.getSource() == memoryButton) {

            String[] options = {"Play Game", "View Leaderboard"};
            int choice = showCustomOptionDialog("🧠 Memory Game", "What would you like to do?", options);
            if (choice == 0) {
                new MemoryGame(DatabaseFactory.getDatabase(), player.getUsername(), frame);
            } else if (choice == 1) {
                new GameLeaderboardWindow(player, "Memory", frame);
            }

        } else if (e.getSource() == globalLeaderboardButton) {

            new GlobalLeaderboardWindow(player, frame);

        } else if (e.getSource() == friendsButton) {
            new FriendsWindow(player, frame);

        } else if (e.getSource() == profileButton) {
            new ProfileWindow(player, frame);

        } else if (e.getSource() == logoutButton) {
            frame.dispose();
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            new LoginWindow();
        }
    }

    private int showCustomOptionDialog(String title, String message, String[] options) {
        JDialog dialog = new JDialog(frame, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(480, 280);
        dialog.setLocationRelativeTo(frame);
        
        // Premium background panel
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 136), 3),
            BorderFactory.createEmptyBorder(20, 25, 25, 25)
        ));
        
        // Header with title and close button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 200, 150));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton closeBtn = new JButton("×");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeBtn.setForeground(new Color(255, 100, 100));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(Color.RED); }
            public void mouseExited(MouseEvent e) { closeBtn.setForeground(new Color(255, 100, 100)); }
        });
        
        headerPanel.add(closeBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Message
        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(Color.WHITE);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, options.length, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        final int[] choice = {-1};
        for (int i = 0; i < options.length; i++) {
            JButton button = new JButton(options[i]);
            Color bgColor = (i == 0) ? new Color(0, 150, 136) : new Color(100, 110, 140);
            Color hoverColor = (i == 0) ? new Color(0, 200, 170) : new Color(130, 140, 170);
            
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(bgColor);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(150, 45));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.brighter(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            
            final int index = i;
            button.addActionListener(e -> {
                choice[0] = index;
                dialog.dispose();
            });
            
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
                public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
            });
            
            buttonPanel.add(button);
        }
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
        
        return choice[0];
    }

    private void openLichessLinkDialog() {
        JDialog dialog = new JDialog(frame, "Link Lichess Account", true);
        dialog.setUndecorated(true);
        dialog.setSize(500, 320);
        dialog.setLocationRelativeTo(frame);

        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 136), 3),
            BorderFactory.createEmptyBorder(20, 25, 25, 25)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Link Lichess Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 200, 150));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton closeBtn = new JButton("×");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeBtn.setForeground(new Color(255, 100, 100));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(Color.RED); }
            public void mouseExited(MouseEvent e) { closeBtn.setForeground(new Color(255, 100, 100)); }
        });
        headerPanel.add(closeBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel instructionLabel = new JLabel("<html>Enter your Lichess username to link it.<br/>You only need to do this once!</html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        instructionLabel.setForeground(Color.WHITE);
        centerPanel.add(instructionLabel, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel usernameLabel = new JLabel("Lichess Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(0, 200, 150));
        centerPanel.add(usernameLabel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBackground(new Color(20, 30, 40));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 136), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        centerPanel.add(usernameField, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton saveButton = new JButton("Save & Open");
        Color saveBg = new Color(0, 150, 136);
        Color saveHover = new Color(0, 200, 170);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(saveBg);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(150, 45));
        saveButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(saveBg.brighter(), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setOpaque(true);
        saveButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { saveButton.setBackground(saveHover); }
            public void mouseExited(MouseEvent e) { saveButton.setBackground(saveBg); }
        });

        saveButton.addActionListener(e -> {
            String lichessUsername = usernameField.getText().trim();
            if (lichessUsername.isEmpty()) {
                showCustomOptionDialog("Error", "Please enter a Lichess username.", new String[]{"OK"});
                return;
            }
            
            player.setLichessUsername(lichessUsername);
            DatabaseFactory.getDatabase().save();
            dialog.dispose();
            
            // Now open Lichess
            try {
                Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler https://lichess.org/@/" + lichessUsername
                );
            } catch (Exception ex) {
                showCustomOptionDialog("Error", "Failed to open Chess website.", new String[]{"OK"});
            }
        });

        JButton cancelButton = new JButton("Cancel");
        Color cancelBg = new Color(100, 110, 140);
        Color cancelHover = new Color(130, 140, 170);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(cancelBg);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cancelBg.brighter(), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setOpaque(true);
        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { cancelButton.setBackground(cancelHover); }
            public void mouseExited(MouseEvent e) { cancelButton.setBackground(cancelBg); }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}

// Custom rounded border class
class RoundedBorder2 extends AbstractBorder {
    private int radius;
    private Color color;

    public RoundedBorder2(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }
}