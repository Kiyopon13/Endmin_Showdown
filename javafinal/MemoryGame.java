import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryGame extends JFrame {

    private IUserDatabase db;
    private String username;
    private JFrame parentFrame;
    private Player player;

    private JButton[] buttons = new JButton[9];
    private List<Integer> pattern = new ArrayList<>();
    private int level = 1;
    private int score = 0;
    private int index = 0;
    private boolean userTurn = false;

    private JLabel levelLabel;
    private JLabel scoreLabel;

    public MemoryGame(IUserDatabase db, String username) {
        this(db, username, null);
    }

    public MemoryGame(IUserDatabase db, String username, JFrame parentFrame) {

        this.db = db;
        this.username = username;
        this.parentFrame = parentFrame;
        this.player = db.getPlayer(username);

        setTitle("Pattern Memory Challenge");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Premium animated background with gaming theme
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Top Panel (Score & Level)
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.setOpaque(false);
        levelLabel = new JLabel("Level: 1", SwingConstants.CENTER);
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);

        levelLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        levelLabel.setForeground(Color.WHITE);
        scoreLabel.setForeground(Color.WHITE);

        topPanel.add(levelLabel);
        topPanel.add(scoreLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Hide parent frame while game is open
        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }

        // Grid Panel
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Get background color which handles both normal and highlight states
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    
                    // Glass highlight effect
                    g2d.setColor(new Color(255, 255, 255, 60));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
                    
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            // Semi-transparent base state
            buttons[i].setBackground(new Color(255, 255, 255, 40)); 
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 24));
            buttons[i].setFocusable(false);
            buttons[i].setContentAreaFilled(false);
            buttons[i].setOpaque(false);
            buttons[i].setBorderPainted(false);
            buttons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            final int btnIndex = i;

            buttons[i].addActionListener(e -> {
                if (userTurn) {
                    highlightButton(btnIndex, new Color(0, 255, 200, 180)); // Cyan highlight

                    if (pattern.get(index) != btnIndex) {
                        gameOver();
                        return;
                    }

                    index++;

                    if (index == pattern.size()) {
                        nextLevel();
                    }
                }
            });

            gridPanel.add(buttons[i]);
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Bottom Panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JButton leaderboardBtn = new JButton("View Leaderboard");
        styleMemoryButton(leaderboardBtn);
        leaderboardBtn.addActionListener(e -> {
            if (player != null) {
                new GameLeaderboardWindow(player, "Memory", this);
                setVisible(false);
            }
        });
        bottomPanel.add(leaderboardBtn);
        
        JButton backBtn = new JButton("Back");
        styleMemoryButton(backBtn);
        backBtn.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        bottomPanel.add(backBtn);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // If parent frame is maximized, maximize this frame too
        if (parentFrame != null && (parentFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setVisible(true);

        showStartMessage();
    }

    private void showStartMessage() {
        JOptionPane.showMessageDialog(this,
                "Memorize the pattern and repeat it!\nGood Luck!");

        startGame();
    }

    private void startGame() {
        pattern.clear();
        level = 1;
        score = 0;
        updateLabels();
        generateNext();
    }

    private void generateNext() {
        Random rand = new Random();
        pattern.add(rand.nextInt(9));
        showPattern();
    }

    private void showPattern() {
        userTurn = false;
        index = 0;

        javax.swing.Timer timer = new javax.swing.Timer(600, null);
        final int[] i = {0};

        timer.addActionListener(e -> {
            if (i[0] < pattern.size()) {
                int btnIndex = pattern.get(i[0]);
                highlightButton(btnIndex, new Color(255, 255, 0, 180)); // Yellow highlight
                i[0]++;
            } else {
                timer.stop();
                userTurn = true;
            }
        });

        timer.start();
    }

    private void highlightButton(int index, Color color) {
        buttons[index].setBackground(color);

        javax.swing.Timer timer = new javax.swing.Timer(300, e -> {
            buttons[index].setBackground(new Color(255, 255, 255, 40)); // Back to glass
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void nextLevel() {
        level++;
        score += 20;
        updateLabels();
        generateNext();
    }

    private void updateLabels() {
        levelLabel.setText("Level: " + level);
        scoreLabel.setText("Score: " + score);
    }

    private void gameOver() {

        db.updateScore(username, "Memory", score);

        JOptionPane.showMessageDialog(this,
                "Game Over!\nFinal Level: " + level +
                        "\nFinal Score: " + score);

        dispose();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
    }

    private void styleMemoryButton(JButton button) {
        button.setBackground(new Color(147, 51, 219)); // Purple
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 100, 255), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 100, 255)); // Lighter purple
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(147, 51, 219)); // Purple
            }
        });
    }
}
