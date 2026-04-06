import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple utility to obtain a JDBC connection to the MySQL database.
 * Adjust URL/user/password as appropriate for your setup.
 */
public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/game_leaderboard?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "password";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
