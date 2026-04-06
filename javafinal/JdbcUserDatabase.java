import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JdbcUserDatabase implements IUserDatabase {
    private static JdbcUserDatabase instance;

    private JdbcUserDatabase() {
        // ensure tables exist (you could run external SQL script instead)
    }

    public static synchronized JdbcUserDatabase getInstance() {
        if (instance == null) {
            instance = new JdbcUserDatabase();
        }
        return instance;
    }

    @Override
    public Player authenticateUser(String username, String password) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String pw = rs.getString(1);
                if (pw.equals(password)) {
                    return loadPlayer(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void registerUser(String username, String password) {
        registerUser(username, password, null);
    }

    @Override
    public void registerUser(String username, String password, String email) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,password,email) VALUES(?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
            // also create empty stats rows
            for (String game : new String[]{"Chess","Logic","Memory"}) {
                try (PreparedStatement ps2 = conn.prepareStatement("INSERT INTO stats(username,game) VALUES(?,?)")) {
                    ps2.setString(1, username);
                    ps2.setString(2, game);
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Player loadPlayer(String username) {
        Player p = new Player(username, "");
        try (Connection conn = DatabaseHelper.getConnection()) {
            // lichess
            try (PreparedStatement ps = conn.prepareStatement("SELECT lichess FROM users WHERE username=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) p.setLichessUsername(rs.getString(1));
            }
            // stats
            try (PreparedStatement ps = conn.prepareStatement("SELECT game,games,wins,time FROM stats WHERE username=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String game = rs.getString(1);
                    int games = rs.getInt(2);
                    int wins = rs.getInt(3);
                    int time = rs.getInt(4);
                    GameStats gs = p.getGameStats(game);
                    if (gs != null) gs.setTotals(games, wins, time);
                }
            }
            // achievements
            try (PreparedStatement ps = conn.prepareStatement("SELECT achievement FROM achievements WHERE username=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    p.addAchievement(rs.getString(1));
                }
            }
            // friends
            try (PreparedStatement ps = conn.prepareStatement("SELECT user2 FROM friends WHERE user1=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    p.addFriend(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public Player getPlayer(String username) {
        return loadPlayer(username);
    }

    @Override
    public Map<String, Player> getAllUsers() {
        Map<String, Player> map = new HashMap<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users")) {
            while (rs.next()) {
                String u = rs.getString(1);
                map.put(u, loadPlayer(u));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void updateScore(String username, String gameCode, int score) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            // update stats table
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE stats SET games = games+1, wins = wins + ?, time = time + ? WHERE username=? AND game=?")) {
                ps.setInt(1, score>0?1:0);
                ps.setInt(2, score);
                ps.setString(3, username);
                ps.setString(4, mapGameCode(gameCode));
                ps.executeUpdate();
            }
            // record game history
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO games(username,game_type,won,time_spent) VALUES(?,?,?,?)")) {
                ps.setString(1, username);
                ps.setString(2, mapGameCode(gameCode));
                ps.setBoolean(3, score>0);
                ps.setInt(4, score);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // optionally check achievements via SQL or Java
    }

    private String mapGameCode(String code) {
        switch(code) {
            case "LOGIC": return "Logic";
            case "MEMORY": return "Memory";
            case "CHESS": return "Chess";
            default: return code;
        }
    }

    @Override
    public void save() {
        // nothing to do, all operations hit DB immediately
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateOTP(String username) {
        // Stub method for JDBC implementation
        return null;
    }

    @Override
    public boolean validateOTP(String username, String otp) {
        // Stub method for JDBC implementation
        return false;
    }

    @Override
    public int getOTPAttempts(String username) {
        // Stub method for JDBC implementation
        return 0;
    }

    @Override
    public Player getPlayerByEmail(String email) {
        return null;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        return false;
    }
}
