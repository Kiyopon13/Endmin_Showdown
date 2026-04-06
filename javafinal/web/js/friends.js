// ============================================
// FRIENDS JS - Friends Management
// ============================================

const samplePlayers = [
    { username: 'ProGamer', status: 'online' },
    { username: 'MasterMind', status: 'offline' },
    { username: 'ScoreKing', status: 'online' },
    { username: 'ElitePlayer', status: 'offline' },
    { username: 'TopRanker', status: 'online' },
    { username: 'SkillMaster', status: 'offline' },
    { username: 'GameWizard', status: 'online' },
    { username: 'LegendPlayer', status: 'offline' }
];

document.addEventListener('DOMContentLoaded', () => {
    loadFriendsList();
    loadSuggestedFriends();
});

function loadFriendsList() {
    const friendsList = document.getElementById('friendsList');
    friendsList.innerHTML = '';

    const friends = FriendsManager.getFriends();

    if (friends.length === 0) {
        friendsList.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 2rem; color: var(--text-secondary);">No friends yet. Add some friends to get started!</div>';
        return;
    }

    friends.forEach(friendName => {
        const friend = samplePlayers.find(p => p.username === friendName) || { username: friendName, status: 'offline' };
        const card = createFriendCard(friend, true);
        friendsList.appendChild(card);
    });
}

function loadSuggestedFriends() {
    const suggestedFriends = document.getElementById('suggestedFriends');
    suggestedFriends.innerHTML = '';

    const myFriends = FriendsManager.getFriends();
    const availableFriends = samplePlayers.filter(p => !myFriends.includes(p.username));

    if (availableFriends.length === 0) {
        suggestedFriends.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 2rem; color: var(--text-secondary);">You are friends with everyone!</div>';
        return;
    }

    availableFriends.forEach(friend => {
        const card = createFriendCard(friend, false);
        suggestedFriends.appendChild(card);
    });
}

function createFriendCard(friend, isFriend) {
    const card = document.createElement('div');
    card.className = 'friend-card';
    
    const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(friend.username)}&background=0D8ABC&color=fff&size=80`;
    const statusClass = friend.status === 'online' ? 'status-online' : 'status-offline';
    
    card.innerHTML = `
        <img src="${avatarUrl}" alt="${friend.username}" class="friend-avatar">
        <p class="friend-name">${friend.username}</p>
        <div class="friend-status">
            <span class="status-indicator ${statusClass}"></span>
            <span>${friend.status === 'online' ? 'Online' : 'Offline'}</span>
        </div>
        <div class="friend-actions">
            ${isFriend ? `
                <button class="btn btn-remove" onclick="removeFriend('${friend.username}')">
                    <i class="fas fa-user-minus"></i>
                    Remove
                </button>
            ` : `
                <button class="btn btn-add" onclick="addFriend('${friend.username}')">
                    <i class="fas fa-user-plus"></i>
                    Add
                </button>
            `}
        </div>
    `;
    
    return card;
}

function addFriend(username) {
    if (FriendsManager.addFriend(username)) {
        showToast(`Added ${username} as a friend!`, 'success');
        loadFriendsList();
        loadSuggestedFriends();
    }
}

function removeFriend(username) {
    if (confirm(`Remove ${username} from friends?`)) {
        FriendsManager.removeFriend(username);
        showToast(`Removed ${username} from friends`, 'success');
        loadFriendsList();
        loadSuggestedFriends();
    }
}

function searchFriends() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase().trim();
    const friendsList = document.getElementById('friendsList');
    const cards = friendsList.querySelectorAll('.friend-card');
    
    cards.forEach(card => {
        const username = card.querySelector('.friend-name').textContent.toLowerCase();
        if (username.includes(searchInput)) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}
