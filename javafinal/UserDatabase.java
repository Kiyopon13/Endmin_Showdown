import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase implements IUserDatabase {
    private static UserDatabase instance = null;
    private Map<String, Player> users;
    private final File storageFile;
    private Map<String, OTPData> otpStorage;

    // Inner class to store OTP and metadata
    private static class OTPData {
        String otp;
        long expiryTime;
        int attempts;

        OTPData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
            this.attempts = 0;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

        boolean isValid(String inputOtp) {
            return !isExpired() && this.otp.equals(inputOtp);
        }
    }

    private UserDatabase() {
        users = new HashMap<>();
        otpStorage = new ConcurrentHashMap<>();
        storageFile = new File("users.txt");

        if (storageFile.exists()) {
            loadFromFile();
        } else {
            // Initialize with sample users when no storage exists
            initializeSampleUsers();
            saveToFile();
        }
        
        // Populate stats for default users if they have no games
        populateDefaultUserStats();
    }

    private void initializeSampleUsers() {
        Player p1 = new Player("user1", "pass123", "user1@gmail.com", "9876543210");
        Player p2 = new Player("user2", "pass456", "user2@gmail.com", "9876543211");
        Player p3 = new Player("john", "john123", "john@gmail.com", "9876543212");
        Player p4 = new Player("admin", "admin", "admin@gmail.com", "9876543213");
        Player p5 = new Player("alex_player", "alex123", "alex@gmail.com", "9876543214");
        Player p6 = new Player("sarah_gamer", "sarah456", "sarah@gmail.com", "9876543215");
        Player p7 = new Player("mike_chess", "mike789", "mike@gmail.com", "9876543216");

        // Link Lichess usernames for some players
        p1.setLichessUsername("hikaru");
        p3.setLichessUsername("magnuscarlsen");
        p7.setLichessUsername("gothamchess");

        // Populate chess + simple sample results
        p1.getChessStats().addGameResult(true, 450);
        p1.getChessStats().addGameResult(false, 380);
        p1.getChessStats().addGameResult(true, 520);

        p2.getChessStats().addGameResult(true, 480);
        p2.getChessStats().addGameResult(true, 500);

        p3.getChessStats().addGameResult(false, 420);
        p3.getChessStats().addGameResult(true, 460);
        p3.getChessStats().addGameResult(true, 490);
        p3.getChessStats().addGameResult(true, 510);

        p4.getChessStats().addGameResult(true, 500);

        p5.getChessStats().addGameResult(true, 470);
        p5.getChessStats().addGameResult(true, 490);

        p6.getChessStats().addGameResult(false, 440);
        p6.getChessStats().addGameResult(true, 480);

        p7.getChessStats().addGameResult(true, 520);
        p7.getChessStats().addGameResult(true, 540);
        p7.getChessStats().addGameResult(true, 500);
        // sample logic/memory entries (optional)
        p1.getLogicStats().addGameResult(true, 200);
        p2.getMemoryStats().addGameResult(true, 120);

        users.put("user1", p1);
        users.put("user2", p2);
        users.put("john", p3);
        users.put("admin", p4);
        users.put("alex_player", p5);
        users.put("sarah_gamer", p6);
        users.put("mike_chess", p7);
    }

    private void populateDefaultUserStats() {
        // Add stats to default sample users if they have none
        if (users.containsKey("user1") && users.get("user1").getTotalGamesPlayed() == 0) {
            Player p1 = users.get("user1");
            p1.setLichessUsername("hikaru");
            p1.getChessStats().addGameResult(true, 450);
            p1.getChessStats().addGameResult(false, 380);
            p1.getChessStats().addGameResult(true, 520);
            p1.getLogicStats().addGameResult(true, 200);
        }

        if (users.containsKey("user2") && users.get("user2").getTotalGamesPlayed() == 0) {
            Player p2 = users.get("user2");
            p2.getChessStats().addGameResult(true, 480);
            p2.getChessStats().addGameResult(true, 500);
            p2.getMemoryStats().addGameResult(true, 120);
        }

        if (users.containsKey("john") && users.get("john").getTotalGamesPlayed() == 0) {
            Player p3 = users.get("john");
            p3.setLichessUsername("magnuscarlsen");
            p3.getChessStats().addGameResult(false, 420);
            p3.getChessStats().addGameResult(true, 460);
            p3.getChessStats().addGameResult(true, 490);
            p3.getChessStats().addGameResult(true, 510);
        }

        if (users.containsKey("admin") && users.get("admin").getTotalGamesPlayed() == 0) {
            Player p4 = users.get("admin");
            p4.getChessStats().addGameResult(true, 500);
        }

        if (users.containsKey("alex_player") && users.get("alex_player").getTotalGamesPlayed() == 0) {
            Player p5 = users.get("alex_player");
            p5.getChessStats().addGameResult(true, 470);
            p5.getChessStats().addGameResult(true, 490);
        }

        if (users.containsKey("sarah_gamer") && users.get("sarah_gamer").getTotalGamesPlayed() == 0) {
            Player p6 = users.get("sarah_gamer");
            p6.getChessStats().addGameResult(false, 440);
            p6.getChessStats().addGameResult(true, 480);
        }

        if (users.containsKey("mike_chess") && users.get("mike_chess").getTotalGamesPlayed() == 0) {
            Player p7 = users.get("mike_chess");
            p7.setLichessUsername("gothamchess");
            p7.getChessStats().addGameResult(true, 520);
            p7.getChessStats().addGameResult(true, 540);
            p7.getChessStats().addGameResult(true, 500);
        }
        
        // Save updated stats
        saveToFile();
    }

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                // Format: username|password|email|chGames,chWins,chTime|logicGames,logicWins,logicTime|memoryGames,memoryWins,memoryTime|ach1,ach2|friend1,friend2
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String username = parts[0];
                    String password = parts[1];
                    String email = parts.length >= 3 ? parts[2] : "";
                    Player p = new Player(username, password, email);

                    try {
                        if (parts.length >= 4) {
                            String[] chess = parts[3].split(",");
                            if (chess.length == 3) p.getChessStats().setTotals(Integer.parseInt(chess[0]), Integer.parseInt(chess[1]), Integer.parseInt(chess[2]));
                        }
                        if (parts.length >= 5) {
                            String[] logic = parts[4].split(",");
                            if (logic.length == 3) p.getLogicStats().setTotals(Integer.parseInt(logic[0]), Integer.parseInt(logic[1]), Integer.parseInt(logic[2]));
                        }
                        if (parts.length >= 6) {
                            String[] memory = parts[5].split(",");
                            if (memory.length == 3) p.getMemoryStats().setTotals(Integer.parseInt(memory[0]), Integer.parseInt(memory[1]), Integer.parseInt(memory[2]));
                        }
                        if (parts.length >= 7) {
                            String[] achs = parts[6].split(",");
                            for (String a : achs) if (!a.isEmpty()) p.addAchievement(a);
                        }
                        if (parts.length >= 8) {
                            String[] frs = parts[7].split(",");
                            for (String f : frs) if (!f.isEmpty()) p.addFriend(f);
                        }
                    } catch (NumberFormatException ex) {
                        // ignore malformed stats for this user
                    }

                    users.put(username, p);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users from file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(storageFile))) {
            // format: username|password|email|chGames,chWins,chTime|logicGames,logicWins,logicTime|memoryGames,memoryWins,memoryTime|ach1,ach2|friend1,friend2
            for (Map.Entry<String, Player> entry : users.entrySet()) {
                String username = entry.getKey();
                Player p = entry.getValue();
                String password = p.getPassword();
                String email = p.getEmail();

                String chessPart = String.format("%d,%d,%d", p.getChessStats().getTotalGames(), p.getChessStats().getTotalWins(), p.getChessStats().getTotalTimeSpent());
                String logicPart = String.format("%d,%d,%d", p.getLogicStats().getTotalGames(), p.getLogicStats().getTotalWins(), p.getLogicStats().getTotalTimeSpent());
                String memoryPart = String.format("%d,%d,%d", p.getMemoryStats().getTotalGames(), p.getMemoryStats().getTotalWins(), p.getMemoryStats().getTotalTimeSpent());
                String achPart = String.join(",", p.getAchievements());
                String friendPart = String.join(",", p.getFriends());

                bw.write(username + "|" + password + "|" + email + "|" + chessPart + "|" + logicPart + "|" + memoryPart + "|" + achPart + "|" + friendPart);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save users to file: " + e.getMessage());
        }
    }

    // Allow games (external windows) to update scores by game code
    @Override
    public void updateScore(String username, String gameCode, int score) {
        Player p = users.get(username);
        if (p == null) return;

        // normalize game code to uppercase for comparison
        String code = gameCode == null ? "" : gameCode.trim().toUpperCase();
        switch (code) {
            case "LOGIC":
                p.getLogicStats().addGameResult(score > 0, score);
                break;
            case "MEMORY":
                p.getMemoryStats().addGameResult(score > 0, score);
                break;
            case "CHESS":
                p.getChessStats().addGameResult(score > 0, score);
                break;
            default:
                // unknown game code
                return;
        }

        // after updating stats, evaluate achievements
        checkAchievements(p);
        saveToFile();
    }

    /**
     * Evaluate achievements for a player after stats change.
     */
    private void checkAchievements(Player p) {
        int total = p.getTotalGamesPlayed();
        if (total >= 100 && !p.getAchievements().contains("100 Games")) {
            p.addAchievement("100 Games");
        }
        if (p.getTotalGamesWon() >= 10 && !p.getAchievements().contains("10 Wins")) {
            p.addAchievement("10 Wins");
        }
        if (p.getChessStats().getTotalGames() >= 50 && !p.getAchievements().contains("50 Chess Games")) {
            p.addAchievement("50 Chess Games");
        }
    }

    public static UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    @Override
    public Player authenticateUser(String username, String password) {
        Player player = users.get(username);
        if (player != null && player.getPassword().equals(password)) {
            return player;
        }
        return null;
    }

    @Override
    public Player getPlayer(String username) {
        return users.get(username);
    }

    @Override
    public void registerUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, new Player(username, password));
            saveToFile();
        }
    }

    @Override
    public void registerUser(String username, String password, String email) {
        if (!users.containsKey(username)) {
            users.put(username, new Player(username, password, email));
            saveToFile();
        }
    }

    // Persist current in-memory users and their stats to storage
    @Override
    public void save() {
        saveToFile();
    }

    @Override
    public Map<String, Player> getAllUsers() {
        return users;
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        Player player = users.get(username);
        if (player != null) {
            player.setPassword(newPassword);
            saveToFile();
            // Clear OTP after successful password reset
            otpStorage.remove(username);
        }
    }

    @Override
    public String generateOTP(String username) {
        // Check if user exists
        if (!users.containsKey(username)) {
            return null;
        }

        // Generate 4-digit OTP
        Random random = new Random();
        String otp = String.format("%04d", random.nextInt(10000));

        // Store OTP with 5 minutes expiry (300000 ms)
        long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000);
        otpStorage.put(username, new OTPData(otp, expiryTime));

        return otp;
    }

    @Override
    public boolean validateOTP(String username, String otp) {
        OTPData otpData = otpStorage.get(username);

        if (otpData == null) {
            return false;
        }

        // Increment attempt counter
        otpData.attempts++;

        // Check if max attempts exceeded (3 tries)
        if (otpData.attempts > 3) {
            otpStorage.remove(username);
            return false;
        }

        // Validate OTP
        if (otpData.isValid(otp)) {
            otpStorage.remove(username);
            return true;
        }

        return false;
    }

    @Override
    public int getOTPAttempts(String username) {
        OTPData otpData = otpStorage.get(username);
        if (otpData == null) {
            return 0;
        }
        return otpData.attempts;
    }

    @Override
    public Player getPlayerByEmail(String email) {
        for (Player p : users.values()) {
            if (email.equalsIgnoreCase(p.getEmail())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        Player p = getPlayerByEmail(email);
        if (p != null) {
            return validateOTP(p.getUsername(), code);
        }
        return false;
    }
}
