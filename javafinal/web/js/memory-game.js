// ============================================
// MEMORY GAME JS - Memory Game Implementation
// ============================================

let memoryGameState = {
    score: 0,
    level: 1,
    timeLeft: 60,
    gameActive: true,
    moves: 0,
    pairsFound: 0,
    cards: [],
    flippedCards: [],
    matchedPairs: 0,
    gameStartTime: Date.now()
};

const memoryConfig = {
    baseTime: 60,
    scorePerMatch: 150,
    pairsPerLevel: 8
};

document.addEventListener('DOMContentLoaded', () => {
    initializeMemoryGame();
    startMemoryTimer();
});

function initializeMemoryGame() {
    const user = UserSession.getUser();
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    generateMemoryBoard();
    updateMemoryDisplay();
}

function generateMemoryBoard() {
    const gameBoard = document.getElementById('gameBoard');
    gameBoard.innerHTML = '';
    memoryGameState.cards = [];
    memoryGameState.flippedCards = [];
    memoryGameState.matchedPairs = 0;
    memoryGameState.moves = 0;
    memoryGameState.pairsFound = 0;

    const emojis = ['🌕', '🌙', '⭐', '☀️', '🌟', '✨', '💫', '🌠',
                    '🎨', '🎭', '🎪', '🎯', '🎲', '🎳', '🎸', '🎺'];

    // Create pairs
    const pairs = [];
    emojis.forEach(emoji => {
        pairs.push(emoji);
        pairs.push(emoji);
    });

    // Shuffle
    pairs.sort(() => Math.random() - 0.5);

    // Create card elements
    pairs.forEach((emoji, index) => {
        const cardContainer = document.createElement('div');
        cardContainer.className = 'memory-card';
        
        const cardInner = document.createElement('div');
        cardInner.className = 'memory-card-inner';
        
        const cardFront = document.createElement('div');
        cardFront.className = 'memory-card-front';
        cardFront.textContent = '?';
        
        const cardBack = document.createElement('div');
        cardBack.className = 'memory-card-back';
        cardBack.textContent = emoji;
        
        cardInner.appendChild(cardFront);
        cardInner.appendChild(cardBack);
        cardContainer.appendChild(cardInner);
        
        cardContainer.dataset.index = index;
        cardContainer.dataset.emoji = emoji;
        cardContainer.addEventListener('click', () => flipCard(index));
        
        gameBoard.appendChild(cardContainer);
        memoryGameState.cards.push({
            element: cardContainer,
            emoji: emoji,
            flipped: false,
            matched: false
        });
    });
}

function flipCard(index) {
    if (!memoryGameState.gameActive || memoryGameState.flippedCards.length >= 2) return;

    const card = memoryGameState.cards[index];
    if (card.flipped || card.matched) return;

    card.flipped = true;
    memoryGameState.flippedCards.push(index);
    card.element.classList.add('flipped');

    if (memoryGameState.flippedCards.length === 2) {
        checkMatch();
    }

    updateMemoryDisplay();
}

function checkMatch() {
    const [index1, index2] = memoryGameState.flippedCards;
    const card1 = memoryGameState.cards[index1];
    const card2 = memoryGameState.cards[index2];

    memoryGameState.moves++;

    if (card1.emoji === card2.emoji) {
        // Match found
        card1.matched = true;
        card2.matched = true;
        memoryGameState.matchedPairs++;
        memoryGameState.pairsFound++;
        memoryGameState.score += memoryConfig.scorePerMatch;
        
        card1.element.classList.add('matched');
        card2.element.classList.add('matched');
        
        memoryGameState.flippedCards = [];

        if (memoryGameState.matchedPairs === 8) {
            levelUpMemory();
        }
    } else {
        // No match
        setTimeout(() => {
            card1.element.classList.remove('flipped');
            card2.element.classList.remove('flipped');
            card1.flipped = false;
            card2.flipped = false;
            memoryGameState.flippedCards = [];
            updateMemoryDisplay();
        }, 1000);
    }

    updateMemoryDisplay();
}

function levelUpMemory() {
    memoryGameState.level++;
    memoryGameState.timeLeft += memoryConfig.baseTime;
    generateMemoryBoard();
    showToast(`Level Up! 🎉 Level ${memoryGameState.level}`, 'success');
}

function startMemoryTimer() {
    setInterval(() => {
        if (memoryGameState.gameActive && memoryGameState.timeLeft > 0) {
            memoryGameState.timeLeft--;
            updateMemoryDisplay();
        } else if (memoryGameState.timeLeft === 0) {
            endMemoryGame();
        }
    }, 1000);
}

function updateMemoryDisplay() {
    document.getElementById('score').textContent = formatScore(memoryGameState.score);
    document.getElementById('level').textContent = memoryGameState.level;
    document.getElementById('timer').textContent = memoryGameState.timeLeft;
    document.getElementById('pairsFound').textContent = memoryGameState.pairsFound;
    document.getElementById('moves').textContent = memoryGameState.moves;
}

function endMemoryGame() {
    memoryGameState.gameActive = false;
    const gameTime = Math.floor((Date.now() - memoryGameState.gameStartTime) / 1000);
    
    // Save stats
    GameStatsManager.updateStats('memory', memoryGameState.score, memoryGameState.level);
    LeaderboardManager.addToLeaderboard(
        UserSession.getUser().username,
        memoryGameState.score,
        memoryGameState.level,
        'memory'
    );

    showMemoryGameOverModal(gameTime);
}

function showMemoryGameOverModal(gameTime) {
    const modal = document.getElementById('gameOverModal');
    document.getElementById('finalScore').textContent = formatScore(memoryGameState.score);
    document.getElementById('levelReached').textContent = memoryGameState.level;
    document.getElementById('totalMoves').textContent = memoryGameState.moves;
    document.getElementById('timePlayed').textContent = formatTime(gameTime);
    modal.classList.remove('hidden');
}

function restartGame() {
    memoryGameState = {
        score: 0,
        level: 1,
        timeLeft: 60,
        gameActive: true,
        moves: 0,
        pairsFound: 0,
        cards: [],
        flippedCards: [],
        matchedPairs: 0,
        gameStartTime: Date.now()
    };
    
    document.getElementById('gameOverModal').classList.add('hidden');
    initializeMemoryGame();
    startMemoryTimer();
}

function pauseGame() {
    memoryGameState.gameActive = !memoryGameState.gameActive;
    event.target.textContent = memoryGameState.gameActive ? 
        '⏸ Pause' : '▶ Resume';
}

function quitGame() {
    if (confirm('Are you sure you want to quit? Your progress will be lost.')) {
        goToDashboard();
    }
}

function formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
}
