public class Player {
    private String username;
    private String password;
    private String email;
    private String mobileNumber;
    private String lichessUsername; // Optional Lichess account linked to this player
    private GameStats chessStats;
    private GameStats logicStats;
    private GameStats memoryStats;

    // new features
    private java.util.Set<String> achievements;
    private java.util.Set<String> friends; // usernames of friends

    public Player(String username, String password) {
        this(username, password, "", "");
    }

    public Player(String username, String password, String email) {
        this(username, password, email, "");
    }

    public Player(String username, String password, String email, String mobileNumber) {
        this.username = username;
        this.password = password;
        this.email = email != null ? email : "";
        this.mobileNumber = mobileNumber != null ? mobileNumber : "";
        this.lichessUsername = null;
        this.chessStats = new GameStats("Chess");
        this.logicStats = new GameStats("Logic");
        this.memoryStats = new GameStats("Memory");
        this.achievements = new java.util.HashSet<>();
        this.friends = new java.util.HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber != null ? mobileNumber : "";
    }

    public GameStats getChessStats() {
        return chessStats;
    }

    public GameStats getLogicStats() {
        return logicStats;
    }

    public GameStats getMemoryStats() {
        return memoryStats;
    }

    public String getLichessUsername() {
        return lichessUsername;
    }

    public void setLichessUsername(String lichessUsername) {
        this.lichessUsername = lichessUsername;
    }

    // achievements convenience
    public java.util.Set<String> getAchievements() {
        return achievements;
    }
    public void addAchievement(String ach) {
        achievements.add(ach);
    }

    // friends
    public java.util.Set<String> getFriends() {
        return friends;
    }
    public void addFriend(String username) {
        if (!username.equals(this.username)) {
            friends.add(username);
        }
    }
    public void removeFriend(String username) {
        friends.remove(username);
    }

    public GameStats getGameStats(String gameName) {
        switch(gameName) {
            case "Chess":
                return chessStats;
            case "Logic":
                return logicStats;
            case "Memory":
                return memoryStats;
            default:
                return null;
        }
    }

    public int getTotalGamesPlayed() {
        return chessStats.getTotalGames() + logicStats.getTotalGames() + memoryStats.getTotalGames();
    }

    public int getTotalGamesWon() {
        return chessStats.getTotalWins() + logicStats.getTotalWins() + memoryStats.getTotalWins();
    }

    public int getTotalTimeSpent() {
        return chessStats.getTotalTimeSpent() + logicStats.getTotalTimeSpent() + memoryStats.getTotalTimeSpent();
    }
}
