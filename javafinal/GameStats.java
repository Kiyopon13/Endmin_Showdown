public class GameStats {
    private String gameName;
    private int totalGames;
    private int totalWins;
    private int totalTimeSpent; // in seconds

    public GameStats(String gameName) {
        this.gameName = gameName;
        this.totalGames = 0;
        this.totalWins = 0;
        this.totalTimeSpent = 0;
    }

    public void addGameResult(boolean won, int timeSpent) {
        this.totalGames++;
        if (won) {
            this.totalWins++;
        }
        this.totalTimeSpent += timeSpent;
    }

    public String getGameName() {
        return gameName;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public double getWinPercentage() {
        if (totalGames == 0) return 0;
        return (totalWins * 100.0) / totalGames;
    }

    public String getTimeSpentFormatted() {
        int hours = totalTimeSpent / 3600;
        int minutes = (totalTimeSpent % 3600) / 60;
        int seconds = totalTimeSpent % 60;
        return hours + "h " + minutes + "m " + seconds + "s";
    }

    // Set totals directly (used when loading persisted data)
    public void setTotals(int totalGames, int totalWins, int totalTimeSpent) {
        this.totalGames = totalGames;
        this.totalWins = totalWins;
        this.totalTimeSpent = totalTimeSpent;
    }
}
