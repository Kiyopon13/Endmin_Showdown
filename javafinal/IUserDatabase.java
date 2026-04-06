import java.util.Map;

public interface IUserDatabase {
    Player authenticateUser(String username, String password);
    void registerUser(String username, String password);
    void registerUser(String username, String password, String email);
    Player getPlayer(String username);
    Map<String, Player> getAllUsers();
    void updateScore(String username, String gameCode, int score);
    void save();
    void resetPassword(String username, String newPassword);
    String generateOTP(String username);
    boolean validateOTP(String username, String otp);
    int getOTPAttempts(String username);
    Player getPlayerByEmail(String email);
    boolean verifyCode(String email, String code);
}
