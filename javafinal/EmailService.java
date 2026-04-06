import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.io.UnsupportedEncodingException;

public class EmailService {
    private static final String SENDER_EMAIL = "5462009pr@gmail.com";
    private static final String APP_PASSWORD = "nybt msms yfuj clup";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static boolean sendVerificationCode(String recipientEmail, String code) {
        try {
            // Set up properties for Gmail SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                }
            });

            // Create the email message
            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(SENDER_EMAIL, "Endmin Showdown"));
            } catch (UnsupportedEncodingException e) {
                message.setFrom(new InternetAddress(SENDER_EMAIL));
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Verification Code - Endmin Showdown");

            // Email body
            String emailBody = "Hello,\n\n" +
                    "You requested to reset your password for Endmin Showdown.\n\n" +
                    "Your verification code is: " + code + "\n\n" +
                    "This code will expire in 10 minutes.\n" +
                    "If you didn't request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Endmin Showdown Team";

            message.setText(emailBody);

            // Send the email
            Transport.send(message);
            System.out.println("✓ Verification code sent to: " + recipientEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendWelcomeEmail(String recipientEmail, String username) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(SENDER_EMAIL, "Endmin Showdown"));
            } catch (UnsupportedEncodingException e) {
                message.setFrom(new InternetAddress(SENDER_EMAIL));
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Welcome to Endmin Showdown!");

            String emailBody = "Hello " + username + ",\n\n" +
                    "Welcome to Endmin Showdown!\n\n" +
                    "Your account has been successfully created.\n" +
                    "You can now login with your username and password.\n\n" +
                    "Master Your Games!\n\n" +
                    "Best regards,\n" +
                    "Endmin Showdown Team";

            message.setText(emailBody);
            Transport.send(message);
            System.out.println("✓ Welcome email sent to: " + recipientEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
            return false;
        }
    }
}
