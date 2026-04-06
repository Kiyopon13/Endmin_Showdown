// ============================================
// LEADERBOARD JS - Global Leaderboard
// ============================================

let currentFilter = 'all';

document.addEventListener('DOMContentLoaded', () => {
    loadLeaderboard('all');
});

function filterLeaderboard(type) {
    currentFilter = type;
    
    // Update button states
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.closest('.filter-btn').classList.add('active');

    // Load filtered data
    if (type === 'all') {
        loadLeaderboard('all');
    } else {
        loadLeaderboard(type);
    }
}

function loadLeaderboard(gameType) {
    const leaderboardBody = document.getElementById('leaderboardBody');
    leaderboardBody.innerHTML = '';

    let leaderboard = LeaderboardManager.getLeaderboard();

    if (gameType !== 'all') {
        leaderboard = leaderboard.filter(entry => entry.gameType === gameType);
    }

    // Sort by score descending
    leaderboard.sort((a, b) => b.score - a.score);
    leaderboard = leaderboard.slice(0, 50);

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
            <td>${entry.gameType === 'all' ? '-' : entry.gameType.toUpperCase()}</td>
        `;
        
        leaderboardBody.appendChild(row);
    });

    displayUserRank(gameType);
}

function displayUserRank(gameType) {
    const user = UserSession.getUser();
    if (!user) return;

    let leaderboard = LeaderboardManager.getLeaderboard();
    
    if (gameType !== 'all') {
        leaderboard = leaderboard.filter(entry => entry.gameType === gameType);
    }

    leaderboard.sort((a, b) => b.score - a.score);
    
    const userRank = leaderboard.findIndex(e => e.username === user.username) + 1;
    const userEntry = leaderboard.find(e => e.username === user.username);

    const highlightSection = document.getElementById('userHighlight');
    
    if (userRank > 0) {
        highlightSection.style.display = 'block';
        document.getElementById('userRankNum').textContent = '#' + userRank;
        document.getElementById('userLeaderboardName').textContent = user.username;
        document.getElementById('userLeaderboardScore').textContent = formatScore(userEntry.score);
    } else {
        highlightSection.style.display = 'none';
    }
}
