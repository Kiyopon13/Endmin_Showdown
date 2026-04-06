import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LogicGame extends JFrame {

    private IUserDatabase db;
    private String username;
    private JFrame parentFrame;

    private String secretCode;
    private int attemptsLeft = 8;

    private JLabel attemptsLabel;
    private JTextField guessField;
    // show feedback about the last guess
    private JLabel statusLabel;
    private JPanel historyPanel;
    private JLabel[] digitLabels;
    private Player player;

    public LogicGame(IUserDatabase db, String username) {
        this(db, username, null);
    }

    public LogicGame(IUserDatabase db, String username, JFrame parentFrame) {

        this.db = db;
        this.username = username;
        this.parentFrame = parentFrame;
        this.player = db.getPlayer(username);

        setTitle("Code Breaker Logic Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Logic-themed main panel with dark blue to purple gradient
        JPanel mainPanel = UIUtils.createLogicThemedPanel(new BorderLayout());
        setContentPane(mainPanel);

        secretCode = generateSecretCode();

        // Top Panel with title and instructions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Code Breaker - Enter a 4-digit number", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        attemptsLabel = new JLabel("Attempts Left: 8", SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        attemptsLabel.setForeground(new Color(100, 200, 255)); // Light blue
        topPanel.add(attemptsLabel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Hide parent frame while game is open
        if (parentFrame != null) {
            parentFrame.setVisible(false);
        }

        // Center Panel - show history and current digits
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // History panel (previous guesses)
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setOpaque(false);
        centerPanel.add(historyPanel, BorderLayout.NORTH);

        // Digit display (replaces the old submit button area)
        JPanel digitsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 30));
        digitsPanel.setOpaque(false);
        digitLabels = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            JLabel d = new JLabel("_");
            d.setOpaque(true);
            d.setBackground(new Color(25, 103, 210)); // Deep blue
            d.setForeground(Color.WHITE);
            d.setPreferredSize(new Dimension(100, 80));
            d.setHorizontalAlignment(SwingConstants.CENTER);
            d.setFont(new Font("Arial", Font.BOLD, 36));
            d.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255), 3));
            digitLabels[i] = d;
            digitsPanel.add(d);
        }

        // result area removed, using status label instead
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 255, 200)); // Cyan for feedback

        centerPanel.add(digitsPanel, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel with buttons and small submit
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JButton leaderboardBtn = new JButton("View Leaderboard");
        styleLogicButton(leaderboardBtn);
        leaderboardBtn.addActionListener(e -> {
            if (player != null) {
                new GameLeaderboardWindow(player, "Logic", this);
                setVisible(false);
            }
        });
        bottomPanel.add(leaderboardBtn);

        // small input and submit at bottom
        guessField = new JTextField(6);
        guessField.setFont(new Font("Arial", Font.BOLD, 18));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setBackground(new Color(50, 100, 180));
        guessField.setForeground(Color.WHITE);
        bottomPanel.add(guessField);

        JButton submitBtn = new JButton("Submit");
        styleLogicButton(submitBtn);
        submitBtn.setPreferredSize(new Dimension(100, 30));
        bottomPanel.add(submitBtn);
        
        JButton backBtn = new JButton("Back");
        styleLogicButton(backBtn);
        backBtn.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        bottomPanel.add(backBtn);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // update digit display as user types
        guessField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String txt = guessField.getText();
                for (int i = 0; i < 4; i++) {
                    if (i < txt.length()) {
                        digitLabels[i].setText(String.valueOf(txt.charAt(i)));
                        digitLabels[i].setBackground(new Color(230,230,250));
                    } else {
                        digitLabels[i].setText("_");
                        digitLabels[i].setBackground(new Color(230,230,250));
                    }
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        submitBtn.addActionListener(e -> checkGuess());

        // If parent frame is maximized, maximize this frame too
        if (parentFrame != null && (parentFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setVisible(true);
    }

    private String generateSecretCode() {

        Random rand = new Random();
        Set<Integer> digits = new HashSet<>();
        StringBuilder code = new StringBuilder();

        while (digits.size() < 4) {
            int digit = rand.nextInt(10);
            if (!digits.contains(digit)) {
                digits.add(digit);
                code.append(digit);
            }
        }

        return code.toString();
    }

    private void checkGuess() {

        String guess = guessField.getText();

        if (guess.length() != 4 || !guess.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid 4-digit number!");
            return;
        }

        int correctPosition = 0;
        int correctDigitWrongPlace = 0;

        // determine colors and update current digit labels
        Color[] colors = new Color[4];
        for (int i = 0; i < 4; i++) {

            if (guess.charAt(i) == secretCode.charAt(i)) {
                correctPosition++;
                colors[i] = Color.GREEN;
            } else if (secretCode.contains("" + guess.charAt(i))) {
                correctDigitWrongPlace++;
                colors[i] = Color.YELLOW;
            } else {
                colors[i] = new Color(230,230,250);
            }
            digitLabels[i].setBackground(colors[i]);
        }

        // add a new history row with colored digits
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        for (int i = 0; i < 4; i++) {
            JLabel d = new JLabel(String.valueOf(guess.charAt(i)));
            d.setOpaque(true);
            d.setBackground(colors[i]);
            d.setPreferredSize(new Dimension(60, 50));
            d.setHorizontalAlignment(SwingConstants.CENTER);
            d.setFont(new Font("Arial", Font.BOLD, 24));
            d.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            row.add(d);
        }
        historyPanel.add(row);
        historyPanel.revalidate();
        historyPanel.repaint();

        attemptsLeft--;
        attemptsLabel.setText("Attempts Left: " + attemptsLeft);

        // set status label text centered
        statusLabel.setText("Guess: " + guess + "  |  ✔ Correct Position: " + correctPosition +
                "  |  🔁 Correct Digit Wrong Position: " + correctDigitWrongPlace);

        // clear input for next guess
        guessField.setText("");
        // reset digit labels underscores
        for (int i = 0; i < 4; i++) {
            digitLabels[i].setText("_");
        }

        if (correctPosition == 4) {
            winGame();
        } else if (attemptsLeft == 0) {
            loseGame();
        }


    }

    private void winGame() {

        int score = attemptsLeft * 25;
        db.updateScore(username, "Logic", score);

        JOptionPane.showMessageDialog(this,
                "🎉 You cracked the code!\nScore: " + score);

        dispose();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
    }

    private void loseGame() {

        JOptionPane.showMessageDialog(this,
                "Game Over!\nThe correct code was: " + secretCode);

        dispose();
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
    }

    private void styleLogicButton(JButton button) {
        button.setBackground(new Color(25, 103, 210)); // Deep blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 255), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 150, 255)); // Lighter blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 103, 210)); // Deep blue
            }
        });
    }
}
