// ============================================
// DASHBOARD JS - Main Game Dashboard
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    loadUserProfile();
    loadStats();
    generateSampleLeaderboardData();
});

function loadUserProfile() {
    const user = UserSession.getUser();
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    const username = user.username;
    document.getElementById('username').textContent = username;
    document.getElementById('greetUser').textContent = username;
    
    // Update avatar
    const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(username)}&background=0D8ABC&color=fff&size=40`;
    const avatarImg = document.querySelector('.user-avatar');
    if (avatarImg) {
        avatarImg.src = avatarUrl;
    }
}

function loadStats() {
    const stats = GameStatsManager.getStats();
    const user = UserSession.getUser();

    // Update dashboard stats
    document.getElementById('totalScore').textContent = formatScore(stats.totalScore);
    document.getElementById('gamesPlayed').textContent = stats.gamesPlayed;
    document.getElementById('winStreak').textContent = stats.winStreak;
    
    // Get ranking
    const rankings = LeaderboardManager.getLeaderboard()
        .filter(e => e.username === user.username)
        .sort((a, b) => b.score - a.score);
    document.getElementById('userRank').textContent = rankings.length > 0 ? '#' + rankings.length : '--';

    // Update game stats on cards
    document.getElementById('logicHighScore').textContent = formatScore(stats.logic.highScore);
    document.getElementById('logicPlayed').textContent = stats.logic.timesPlayed;
    document.getElementById('memoryHighScore').textContent = formatScore(stats.memory.highScore);
    document.getElementById('memoryPlayed').textContent = stats.memory.timesPlayed;
    document.getElementById('friendCount').textContent = FriendsManager.getFriends().length;
}

function generateSampleLeaderboardData() {
    // Generate sample data if leaderboard is empty
    if (LeaderboardManager.getLeaderboard().length === 0) {
        const games = ['logic', 'memory'];
        const samplePlayers = [
            { name: 'ProGamer', scores: [2500, 1800] },
            { name: 'MasterMind', scores: [2400, 1950] },
            { name: 'ScoreKing', scores: [2300, 1750] },
            { name: 'ElitePlayer', scores: [2200, 1900] },
            { name: 'TopRanker', scores: [2100, 1850] }
        ];

        samplePlayers.forEach(player => {
            games.forEach((game, index) => {
                LeaderboardManager.addToLeaderboard(
                    player.name,
                    player.scores[index],
                    Math.floor(Math.random() * 10) + 1,
                    game
                );
            });
        });
    }
}
