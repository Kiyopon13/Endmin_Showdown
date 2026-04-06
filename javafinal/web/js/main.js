// ============================================
// MAIN JS - Shared Utilities & Navigation
// ============================================

// Local storage keys
const STORAGE_KEYS = {
    user: 'endmin_user',
    gameStats: 'endmin_gameStats',
    friends: 'endmin_friends',
    leaderboard: 'endmin_leaderboard'
};

// Notification/Toast System
function showToast(message, type = 'info', duration = 3000) {
    const toast = document.getElementById('toast') || createToastElement();
    toast.textContent = message;
    toast.className = `toast ${type}`;
    toast.classList.remove('hidden');
    
    setTimeout(() => {
        toast.classList.add('hidden');
    }, duration);
}

function createToastElement() {
    const toast = document.createElement('div');
    toast.id = 'toast';
    toast.className = 'toast hidden';
    document.body.appendChild(toast);
    return toast;
}

// Message box for forms
function showMessage(message, type = 'info') {
    const messageBox = document.getElementById('messageBox');
    if (messageBox) {
        messageBox.innerHTML = message;
        messageBox.className = `message-box ${type}`;
    }
}

// Navigation Functions
function navigateToGame(page) {
    window.location.href = page;
}

function goBack() {
    window.history.back();
}

function goToDashboard() {
    window.location.href = 'dashboard.html';
}

function goToProfile() {
    window.location.href = 'profile.html';
}

function logout() {
    if (confirm('Are you sure you want to logout?')) {
        localStorage.removeItem(STORAGE_KEYS.user);
        showToast('Logged out successfully', 'success');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 500);
    }
}

// User Session Manager
class UserSession {
    static getUser() {
        const user = localStorage.getItem(STORAGE_KEYS.user);
        return user ? JSON.parse(user) : null;
    }

    static setUser(user) {
        localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
    }

    static isLoggedIn() {
        return this.getUser() !== null;
    }

    static logout() {
        localStorage.removeItem(STORAGE_KEYS.user);
    }

    static updateUser(updates) {
        const user = this.getUser();
        if (user) {
            const updated = { ...user, ...updates };
            this.setUser(updated);
            return updated;
        }
        return null;
    }
}

// Check authentication on page load
function checkAuth() {
    if (!UserSession.isLoggedIn()) {
        // Redirect to login if not authenticated (except on auth pages)
        const currentPage = window.location.pathname.split('/').pop();
        const publicPages = ['index.html', 'register.html', 'forgot-password.html', ''];
        
        if (!publicPages.includes(currentPage)) {
            window.location.href = 'index.html';
        }
    }
}

// Game Stats Manager
class GameStatsManager {
    static getStats() {
        const stats = localStorage.getItem(STORAGE_KEYS.gameStats);
        return stats ? JSON.parse(stats) : this.getDefaultStats();
    }

    static getDefaultStats() {
        return {
            totalScore: 0,
            gamesPlayed: 0,
            logic: {
                highScore: 0,
                timesPlayed: 0,
                levelReached: 1
            },
            memory: {
                highScore: 0,
                timesPlayed: 0,
                levelReached: 1
            },
            winStreak: 0,
            achievements: []
        };
    }

    static updateStats(gameType, score, level) {
        const stats = this.getStats();
        stats.totalScore += score;
        stats.gamesPlayed += 1;

        if (gameType === 'logic') {
            stats.logic.timesPlayed += 1;
            if (score > stats.logic.highScore) {
                stats.logic.highScore = score;
            }
            if (level > stats.logic.levelReached) {
                stats.logic.levelReached = level;
            }
        } else if (gameType === 'memory') {
            stats.memory.timesPlayed += 1;
            if (score > stats.memory.highScore) {
                stats.memory.highScore = score;
            }
            if (level > stats.memory.levelReached) {
                stats.memory.levelReached = level;
            }
        }

        localStorage.setItem(STORAGE_KEYS.gameStats, JSON.stringify(stats));
        return stats;
    }

    static resetStats() {
        localStorage.setItem(STORAGE_KEYS.gameStats, JSON.stringify(this.getDefaultStats()));
    }
}

// Leaderboard Manager
class LeaderboardManager {
    static getLeaderboard() {
        const lb = localStorage.getItem(STORAGE_KEYS.leaderboard);
        return lb ? JSON.parse(lb) : [];
    }

    static getLeaderboardByGame(gameType) {
        const lb = this.getLeaderboard();
        return lb.filter(entry => !gameType || entry.gameType === gameType)
                 .sort((a, b) => b.score - a.score)
                 .slice(0, 50);
    }

    static addToLeaderboard(username, score, level, gameType) {
        const lb = this.getLeaderboard();
        const existingIndex = lb.findIndex(e => e.username === username && e.gameType === gameType);
        
        if (existingIndex !== -1) {
            if (score > lb[existingIndex].score) {
                lb[existingIndex] = { username, score, level, gameType, date: new Date().toISOString() };
            }
        } else {
            lb.push({ username, score, level, gameType, date: new Date().toISOString() });
        }

        localStorage.setItem(STORAGE_KEYS.leaderboard, JSON.stringify(lb));
        return this.getLeaderboardByGame(gameType);
    }

    static getUserRank(username, gameType) {
        const lb = this.getLeaderboardByGame(gameType);
        return lb.findIndex(e => e.username === username) + 1 || -1;
    }
}

// Friends Manager
class FriendsManager {
    static getFriends() {
        const friends = localStorage.getItem(STORAGE_KEYS.friends);
        return friends ? JSON.parse(friends) : [];
    }

    static addFriend(username) {
        const friends = this.getFriends();
        if (!friends.includes(username)) {
            friends.push(username);
            localStorage.setItem(STORAGE_KEYS.friends, JSON.stringify(friends));
            return true;
        }
        return false;
    }

    static removeFriend(username) {
        let friends = this.getFriends();
        friends = friends.filter(f => f !== username);
        localStorage.setItem(STORAGE_KEYS.friends, JSON.stringify(friends));
        return true;
    }

    static isFriend(username) {
        return this.getFriends().includes(username);
    }
}

// Format utilities
function formatScore(score) {
    return score.toLocaleString();
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function getRankBadge(rank) {
    if (rank === 1) return '<span class="rank-gold"><i class="fas fa-crown"></i> Gold</span>';
    if (rank === 2) return '<span class="rank-silver"><i class="fas fa-crown"></i> Silver</span>';
    if (rank === 3) return '<span class="rank-bronze"><i class="fas fa-crown"></i> Bronze</span>';
    return `<span>#${rank}</span>`;
}

// Animation utility
function playClickAnimation(element) {
    element.style.transform = 'scale(0.95)';
    setTimeout(() => {
        element.style.transform = 'scale(1)';
    }, 100);
}

// Password toggle
function togglePassword(field = 'password') {
    const input = document.getElementById(field);
    if (input) {
        input.type = input.type === 'password' ? 'text' : 'password';
    }
}

// Tab switching
function switchTab(tabName) {
    // Hide all tabs
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Remove active class from buttons
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(btn => btn.classList.remove('active'));

    // Show selected tab
    const selectedTab = document.getElementById(tabName);
    if (selectedTab) {
        selectedTab.classList.add('active');
    }

    // Mark button as active
    event.target.classList.add('active');
}

function switchFriendsTab(tabName) {
    switchTab(tabName);
}

function switchProfileTab(tabName) {
    switchTab(tabName);
}

// Initialize page on load
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    initializeToastElement();
});

function initializeToastElement() {
    if (!document.getElementById('toast')) {
        createToastElement();
    }
}

// Form validation
function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function validatePassword(password) {
    return password.length >= 6;
}

function validateUsername(username) {
    return username.length >= 3 && username.length <= 20 && /^[a-zA-Z0-9_.-]+$/.test(username);
}

// Debounce helper
function debounce(func, delay) {
    let timeoutId;
    return function(...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func(...args), delay);
    };
}
