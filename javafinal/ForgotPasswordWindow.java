import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForgotPasswordWindow extends JFrame {
    private JTextField usernameField;
    private JButton nextButton;
    private JButton cancelButton;
    private IUserDatabase database;
    private JLabel messageLabel;

    public ForgotPasswordWindow(JFrame parentFrame) {
        this.database = UserDatabase.getInstance();

        setTitle("🔐 Forgot Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 380);
        setLocationRelativeTo(parentFrame);
        setResizable(false);

        // Create modern gradient background with gaming theme
        JPanel mainPanel = UIUtils.createPremiumBackgroundPanel(new BoxLayout(null, BoxLayout.Y_AXIS));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("🔐 Forgot Password?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Verify your identity to reset password");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username input
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(Color.WHITE);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setBackground(new Color(240, 248, 255));
        usernameField.setCaretColor(Color.BLACK);

        // Message label for errors
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(255, 100, 100));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Security info
        JLabel infoLabel = new JLabel("ℹ We'll verify using your registered email or mobile number");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(200, 255, 230));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        nextButton = new JButton("↻ Next");
        cancelButton = new JButton("✕ Cancel");

        styleButton(nextButton);
        styleButton(cancelButton);

        nextButton.addActionListener(e -> handleNext());
        cancelButton.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });

        // Allow Enter key to submit
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleNext();
                }
            }
        });

        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setVisible(true);
    }

    private void handleNext() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            messageLabel.setText("❌ Please enter your username");
            return;
        }

        // Check if user exists
        Player player = database.getPlayer(username);
        if (player == null) {
            messageLabel.setText("❌ Username not found!");
            usernameField.setText("");
            return;
        }

        // Check if user has email or mobile registered
        String email = player.getEmail();
        String mobile = player.getMobileNumber();

        if ((email == null || email.isEmpty()) && (mobile == null || mobile.isEmpty())) {
            messageLabel.setText("❌ No email or mobile registered for this account");
            return;
        }

        // Open verification method selection window
        dispose();
        new VerificationMethodWindow(username, email, mobile, null);
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
