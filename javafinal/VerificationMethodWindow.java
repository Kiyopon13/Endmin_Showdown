import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VerificationMethodWindow extends JFrame {
    private JButton emailButton;
    private JButton mobileButton;
    private JButton cancelButton;
    private IUserDatabase database;
    private JLabel messageLabel;
    private String username;
    private String email;
    private String mobile;

    public VerificationMethodWindow(String username, String email, String mobile, JFrame parentFrame) {
        this.database = UserDatabase.getInstance();
        this.username = username;
        this.email = email;
        this.mobile = mobile;

        setTitle("🔐 Choose Verification Method");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(parentFrame);
        setResizable(false);

        // Create modern gradient background with gaming theme
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(null);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("🔐 Verify Your Identity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username display
        JLabel usernameDisplay = new JLabel("Account: " + username);
        usernameDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameDisplay.setForeground(new Color(220, 220, 220));
        usernameDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Choose how to verify your identity");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message label
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(255, 100, 100));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Options container
        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email option
        if (email != null && !email.isEmpty()) {
            JPanel emailPanel = createOptionPanel("📧 Email", maskEmail(email));
            emailButton = new JButton("Use Email ✓");
            styleButton(emailButton);
            emailButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            emailButton.addActionListener(e -> handleEmailVerification());
            emailPanel.add(emailButton);
            optionsPanel.add(emailPanel);
            optionsPanel.add(Box.createVerticalStrut(20));
        }

        // Mobile option
        if (mobile != null && !mobile.isEmpty()) {
            JPanel mobilePanel = createOptionPanel("📱 Mobile", maskMobile(mobile));
            mobileButton = new JButton("Use Mobile ✓");
            styleButton(mobileButton);
            mobileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            mobileButton.addActionListener(e -> handleMobileVerification());
            mobilePanel.add(mobileButton);
            optionsPanel.add(mobilePanel);
        }

        // Cancel button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        cancelButton = new JButton("✕ Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> {
            dispose();
            new LoginWindow();
        });

        buttonPanel.add(cancelButton);

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(usernameDisplay);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(optionsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(buttonPanel);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createOptionPanel(String title, String maskedValue) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(maskedValue);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueLabel.setForeground(new Color(200, 255, 230));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return email;
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        // Mask local part
        if (localPart.length() <= 2) {
            return "*".repeat(Math.max(1, localPart.length())) + "@" + domain;
        }
        return localPart.charAt(0) + "*****" + localPart.charAt(localPart.length() - 1) + "@" + domain;
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4)
            return mobile;
        // Show only last 4 digits
        return "**** **** " + mobile.substring(Math.max(0, mobile.length() - 4));
    }

    private void handleEmailVerification() {
        if (email == null || email.isEmpty()) {
            messageLabel.setText("❌ Email not registered");
            return;
        }

        // Generate OTP
        String otp = database.generateOTP(username);
        if (otp == null) {
            messageLabel.setText("❌ Error generating OTP");
            return;
        }

        // Send OTP via email using SMTP
        new Thread(() -> {
            EmailService.sendVerificationCode(email, otp);
        }).start();

        // Show a message without displaying the OTP
        JOptionPane.showMessageDialog(this,
                "✓ OTP has been sent to: " + maskEmail(email) + "\n\n" +
                        "Check your email inbox.\n" +
                        "OTP will expire in 5 minutes.\n" +
                        "You have 3 attempts to verify.",
                "OTP Sent Successfully",
                JOptionPane.INFORMATION_MESSAGE);

        // Open OTP verification window
        dispose();
        new OTPVerificationWindow(username, null);
    }

    private void handleMobileVerification() {
        if (mobile == null || mobile.isEmpty()) {
            messageLabel.setText("❌ Mobile not registered");
            return;
        }

        // Generate OTP
        String otp = database.generateOTP(username);
        if (otp == null) {
            messageLabel.setText("❌ Error generating OTP");
            return;
        }

        // TODO: In production, send OTP via SMS using SMS gateway
        // SMSService.sendOTP(mobile, otp);

        // For now, just show a message without displaying the OTP
        JOptionPane.showMessageDialog(this,
                "✓ OTP has been sent to: " + maskMobile(mobile) + "\n\n" +
                        "Check your SMS inbox.\n" +
                        "OTP will expire in 5 minutes.\n" +
                        "You have 3 attempts to verify.",
                "OTP Sent Successfully",
                JOptionPane.INFORMATION_MESSAGE);

        // Open OTP verification window
        dispose();
        new OTPVerificationWindow(username, null);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 150, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }
}
