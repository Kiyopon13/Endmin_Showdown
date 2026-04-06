// ============================================
// GAME LEADERBOARD JS - Game-Specific Leaderboards
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    loadGameLeaderboards();
});

function loadGameLeaderboards() {
    loadLogicLeaderboard();
    loadMemoryLeaderboard();
}

function loadLogicLeaderboard() {
    const leaderboardBody = document.getElementById('logicLeaderboard');
    leaderboardBody.innerHTML = '';

    let leaderboard = LeaderboardManager.getLeaderboardByGame('logic');

    if (leaderboard.length === 0) {
        leaderboardBody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem;">No leaderboard data yet</td></tr>';
        return;
    }

    leaderboard.forEach((entry, index) => {
        const row = document.createElement('tr');
        const rankClass = index < 3 ? ['rank-gold', 'rank-silver', 'rank-bronze'][index] : '';
        
        row.innerHTML = `
            <td class="${rankClass}">
                ${index < 3 ? ['🥇', '🥈', '🥉'][index] : '#' + (index + 1)}
            </td>
            <td><strong>${entry.username}</strong></td>
            <td><strong>${formatScore(entry.score)}</strong></td>
            <td>${entry.level}</td>
            <td>--</td>
        `;
        
        leaderboardBody.appendChild(row);
    });
}

function loadMemoryLeaderboard() {
    const leaderboardBody = document.getElementById('memoryLeaderboard');
    leaderboardBody.innerHTML = '';

    let leaderboard = LeaderboardManager.getLeaderboardByGame('memory');

    if (leaderboard.length === 0) {
        leaderboardBody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem;">No leaderboard data yet</td></tr>';
        return;
    }

    leaderboard.forEach((entry, index) => {
        const row = document.createElement('tr');
        const rankClass = index < 3 ? ['rank-gold', 'rank-silver', 'rank-bronze'][index] : '';
        
        row.innerHTML = `
            <td class="${rankClass}">
                ${index < 3 ? ['🥇', '🥈', '🥉'][index] : '#' + (index + 1)}
            </td>
            <td><strong>${entry.username}</strong></td>
            <td><strong>${formatScore(entry.score)}</strong></td>
            <td>${entry.level}</td>
            <td>--</td>
        `;
        
        leaderboardBody.appendChild(row);
    });
}
