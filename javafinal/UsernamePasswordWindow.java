import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;

public class UsernamePasswordWindow implements ActionListener {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;
    private JLabel messageLabel;
    private String email;

    public UsernamePasswordWindow(String email) {
        this.email = email;
        UIUtils.initLookAndFeel();

        frame = new JFrame("🎮 Endmin Showdown - Login Step 2");
        frame.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create modern gradient background (Green to Teal)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(34, 139, 87),
                        getWidth(), getHeight(), new Color(0, 150, 136));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        frame.setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("🎮 Endmin Showdown");
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 32);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 32);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(40, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Step 2: Enter Your Credentials");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 255, 230));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 40, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Form Panel (Card)
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints fbgc = new GridBagConstraints();
        fbgc.insets = new Insets(10, 0, 10, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;

        // Email display
        JLabel emailDisplayLabel = new JLabel("Email: " + email);
        emailDisplayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailDisplayLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridx = 0; fbgc.gridy = 0; fbgc.weightx = 1.0;
        formPanel.add(emailDisplayLabel, fbgc);

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 1; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(usernameLabel, fbgc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 35));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 2;
        formPanel.add(usernameField, fbgc);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 3; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(passwordLabel, fbgc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 4; fbgc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(passwordField, fbgc);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        messageLabel.setForeground(new Color(200, 50, 50));
        fbgc.gridy = 5; fbgc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(messageLabel, fbgc);

        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonsPanel.setOpaque(false);

        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(34, 139, 87));
        loginButton.addActionListener(this);
        buttonsPanel.add(loginButton);

        backButton = new JButton("Back");
        styleButton(backButton, new Color(0, 150, 136));
        backButton.addActionListener(this);
        buttonsPanel.add(backButton);

        fbgc.gridy = 6; fbgc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonsPanel, fbgc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 30, 100, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(formPanel, gbc);

        frame.setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, bgColor),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == backButton) {
            frame.dispose();
            new EmailLoginWindow();
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠ Please fill all fields!");
            return;
        }

        IUserDatabase db = DatabaseFactory.getDatabase();
        Player player = db.authenticateUser(username, password);

        if (player == null) {
            messageLabel.setText("❌ Invalid credentials! Try again.");
            passwordField.setText("");
            return;
        }

        // Verify that the email matches
        if (!player.getEmail().equalsIgnoreCase(email)) {
            messageLabel.setText("❌ Email doesn't match account!");
            return;
        }

        // Successful login
        frame.dispose();
        new MainGameWindow(player);
    }
}
