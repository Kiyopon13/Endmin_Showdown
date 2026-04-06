public class DatabaseFactory {
    private static IUserDatabase instance;
    public static IUserDatabase getDatabase() {
        if (instance == null) {
            // decide which implementation to use; for now prefer JDBC if available
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = JdbcUserDatabase.getInstance();
            } catch (ClassNotFoundException e) {
                instance = UserDatabase.getInstance();
            }
        }
        return instance;
    }
}
