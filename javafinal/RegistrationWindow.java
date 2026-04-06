import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;

public class RegistrationWindow implements ActionListener {
    private JFrame frame;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;

    public RegistrationWindow() {
        UIUtils.initLookAndFeel();

        frame = new JFrame("🎮 Endmin Showdown - Register");
        frame.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create modern gradient background with gaming theme
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(new GridBagLayout());
        frame.setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("🎮 Create Account");
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 32);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 32);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(40, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join Endmin Showdown");
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

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridx = 0; fbgc.gridy = 0; fbgc.weightx = 1.0;
        formPanel.add(usernameLabel, fbgc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 35));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 1;
        formPanel.add(usernameField, fbgc);

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 2; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(emailLabel, fbgc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 35));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 3; fbgc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(emailField, fbgc);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 4; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(passwordLabel, fbgc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 5; fbgc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(passwordField, fbgc);

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmPasswordLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 6; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(confirmPasswordLabel, fbgc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(250, 35));
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 7; fbgc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(confirmPasswordField, fbgc);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        messageLabel.setForeground(new Color(200, 50, 50));
        fbgc.gridy = 8; fbgc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(messageLabel, fbgc);

        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonsPanel.setOpaque(false);

        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(34, 139, 87));
        registerButton.addActionListener(this);
        buttonsPanel.add(registerButton);

        backButton = new JButton("Back to Login");
        styleButton(backButton, new Color(0, 150, 136));
        backButton.addActionListener(this);
        buttonsPanel.add(backButton);

        fbgc.gridy = 9; fbgc.insets = new Insets(20, 0, 0, 0);
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
        if (e.getSource() == registerButton) {
            handleRegister();
        } else if (e.getSource() == backButton) {
            frame.dispose();
            new LoginWindow();
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("⚠ Please fill all fields!");
            return;
        }

        if (!email.contains("@")) {
            messageLabel.setText("❌ Please enter a valid email address!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("❌ Passwords do not match!");
            passwordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        if (password.length() < 3) {
            messageLabel.setText("❌ Password must be at least 3 characters!");
            return;
        }

        // Check if username already exists
        IUserDatabase db = DatabaseFactory.getDatabase();
        if (db.getPlayer(username) != null) {
            messageLabel.setText("❌ Username already exists!");
            return;
        }

        // Check if email already exists
        if (db.getPlayerByEmail(email) != null) {
            messageLabel.setText("❌ Email already registered!");
            return;
        }

        // Register the user
        db.registerUser(username, password, email);
        
        // Welcome email
        new Thread(() -> {
            EmailService.sendWelcomeEmail(email, username);
        }).start();
        messageLabel.setForeground(new Color(34, 139, 87));
        messageLabel.setText("✓ Registration successful! Redirecting...");
        
        // Redirect to login after a short delay
        Timer timer = new Timer(1500, e1 -> {
            frame.dispose();
            new LoginWindow();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
