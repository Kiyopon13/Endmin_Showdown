// ============================================
// PROFILE JS - User Profile Management
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    loadProfile();
});

function loadProfile() {
    const user = UserSession.getUser();
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    const stats = GameStatsManager.getStats();

    // Load profile info
    const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(user.username)}&background=0D8ABC&color=fff&size=120`;
    document.getElementById('profileAvatar').src = avatarUrl;
    document.getElementById('profileUsername').textContent = user.username;
    document.getElementById('profileEmail').textContent = user.email || 'not.provided@example.com';
    document.getElementById('joinedDate').textContent = new Date().getFullYear();

    // Load statistics
    document.getElementById('totalScoreProfile').textContent = formatScore(stats.totalScore);
    document.getElementById('gamesPlayedProfile').textContent = stats.gamesPlayed;
    document.getElementById('logicHighScore').textContent = formatScore(stats.logic.highScore);
    document.getElementById('memoryHighScore').textContent = formatScore(stats.memory.highScore);

    // Load achievements
    loadAchievements();

    // Load settings form
    document.getElementById('settingsEmail').value = user.email || '';
}

function loadAchievements() {
    const achievementsGrid = document.getElementById('achievementsGrid');
    achievementsGrid.innerHTML = '';

    const achievements = [
        { id: 1, name: 'First Steps', description: 'Play your first game', icon: '👣', unlocked: true },
        { id: 2, name: 'Score Master', description: 'Reach 1000 points', icon: '⭐', unlocked: true },
        { id: 3, name: 'Logic Expert', description: 'Reach Level 5 in Logic Game', icon: '🧠', unlocked: false },
        { id: 4, name: 'Memory Master', description: 'Reach Level 5 in Memory Game', icon: '🧩', unlocked: false },
        { id: 5, name: 'Speedster', description: 'Complete a game in under 30 seconds', icon: '⚡', unlocked: false },
        { id: 6, name: 'Social Butterfly', description: 'Add 5 friends', icon: '🦋', unlocked: false },
        { id: 7, name: 'Top Rank', description: 'Reach top 10 in leaderboard', icon: '🏆', unlocked: false },
        { id: 8, name: 'Legendary', description: 'Score 5000+ points total', icon: '⚔️', unlocked: false }
    ];

    achievements.forEach(achievement => {
        const card = document.createElement('div');
        card.className = `achievement ${achievement.unlocked ? 'unlocked' : ''}`;
        
        card.innerHTML = `
            <div class="achievement-icon" style="opacity: ${achievement.unlocked ? 1 : 0.3}">${achievement.icon}</div>
            <p class="achievement-name">${achievement.name}</p>
            <p class="achievement-desc">${achievement.description}</p>
        `;
        
        achievementsGrid.appendChild(card);
    });
}

function saveSettings() {
    const email = document.getElementById('settingsEmail').value.trim();
    const password = document.getElementById('settingsPassword').value;
    const confirmPassword = document.getElementById('settingsConfirmPassword').value;

    if (!validateEmail(email)) {
        showToast('Invalid email format', 'error');
        return;
    }

    if (password && !validatePassword(password)) {
        showToast('Password must be at least 6 characters', 'error');
        return;
    }

    if (password && password !== confirmPassword) {
        showToast('Passwords do not match', 'error');
        return;
    }

    // Update user
    const updated = UserSession.updateUser({ email: email });
    
    // Clear password fields
    document.getElementById('settingsPassword').value = '';
    document.getElementById('settingsConfirmPassword').value = '';

    showToast('Settings saved successfully!', 'success');
}

function deleteAccount() {
    const confirmation = prompt(
        'This action cannot be undone. Type your username to confirm deletion:'
    );

    const user = UserSession.getUser();
    if (confirmation === user.username) {
        UserSession.logout();
        GameStatsManager.resetStats();
        showToast('Account deleted. Redirecting...', 'success');
        
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1000);
    } else {
        showToast('Account deletion cancelled', 'info');
    }
}
