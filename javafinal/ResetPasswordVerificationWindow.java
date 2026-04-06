import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;

public class ResetPasswordVerificationWindow implements ActionListener {
    private JFrame frame;
    private JTextField codeField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton;
    private JButton backButton;
    private JLabel messageLabel;
    private String email;

    public ResetPasswordVerificationWindow(String email) {
        this.email = email;
        UIUtils.initLookAndFeel();

        frame = new JFrame("🎮 Endmin Showdown - Verify & Reset Password");
        frame.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        JLabel titleLabel = new JLabel("🔐 Reset Password");
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 32);
        if (titleFont.getName().equals("Dialog")) {
            titleFont = new Font("DejaVu Sans", Font.BOLD, 32);
        }
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(40, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Verify Code & Set New Password");
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

        // Verification Code field
        JLabel codeLabel = new JLabel("Verification Code");
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        codeLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 1; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(codeLabel, fbgc);

        codeField = new JTextField();
        codeField.setPreferredSize(new Dimension(250, 35));
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 2;
        formPanel.add(codeField, fbgc);

        // New Password field
        JLabel newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        newPasswordLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 3; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(newPasswordLabel, fbgc);

        newPasswordField = new JPasswordField();
        newPasswordField.setPreferredSize(new Dimension(250, 35));
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 4;
        formPanel.add(newPasswordField, fbgc);

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmPasswordLabel.setForeground(new Color(34, 139, 87));
        fbgc.gridy = 5; fbgc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(confirmPasswordLabel, fbgc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(250, 35));
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(200, 220, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        fbgc.gridy = 6;
        formPanel.add(confirmPasswordField, fbgc);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        messageLabel.setForeground(new Color(200, 50, 50));
        fbgc.gridy = 7; fbgc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(messageLabel, fbgc);

        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonsPanel.setOpaque(false);

        resetButton = new JButton("Reset Password");
        styleButton(resetButton, new Color(34, 139, 87));
        resetButton.addActionListener(this);
        buttonsPanel.add(resetButton);

        backButton = new JButton("Back");
        styleButton(backButton, new Color(0, 150, 136));
        backButton.addActionListener(this);
        buttonsPanel.add(backButton);

        fbgc.gridy = 8; fbgc.insets = new Insets(20, 0, 0, 0);
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
        if (e.getSource() == resetButton) {
            handleReset();
        } else if (e.getSource() == backButton) {
            frame.dispose();
            new ForgotPasswordWindow(null);
        }
    }

    private void handleReset() {
        String code = codeField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("⚠ Please fill all fields!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("❌ Passwords do not match!");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        if (newPassword.length() < 3) {
            messageLabel.setText("❌ Password must be at least 3 characters!");
            return;
        }

        IUserDatabase db = DatabaseFactory.getDatabase();

        // Verify the code
        if (!db.verifyCode(email, code)) {
            messageLabel.setText("❌ Invalid verification code!");
            codeField.setText("");
            return;
        }

        // Get the player and reset password
        Player player = db.getPlayerByEmail(email);
        if (player != null) {
            db.resetPassword(player.getUsername(), newPassword);
            messageLabel.setForeground(new Color(34, 139, 87));
            messageLabel.setText("✓ Password reset successful! Redirecting...");
            
            // Redirect to login after a short delay
            Timer timer = new Timer(1500, e1 -> {
                frame.dispose();
                new EmailLoginWindow();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            messageLabel.setText("❌ User not found!");
        }
    }
}
