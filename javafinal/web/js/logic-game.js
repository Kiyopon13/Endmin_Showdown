// ============================================
// LOGIC GAME JS - Logic Game Implementation
// ============================================

let gameState = {
    score: 0,
    level: 1,
    timeLeft: 30,
    gameActive: true,
    moves: [],
    sequence: [],
    tiles: [],
    gameStartTime: Date.now()
};

const gameConfig = {
    baseTime: 30,
    timeDecrement: 2,
    scorePerMatch: 100,
    levelThreshold: 5
};

document.addEventListener('DOMContentLoaded', () => {
    initializeGame();
    startTimer();
});

function initializeGame() {
    const user = UserSession.getUser();
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    generateGameBoard();
    updateDisplay();
}

function generateGameBoard() {
    const gameBoard = document.getElementById('gameBoard');
    gameBoard.innerHTML = '';
    gameState.tiles = [];
    gameState.moves = [];
    gameState.sequence = [];

    const gridSize = 4;
    const patterns = ['🔥', '⚡', '💎', '🎯', '🌟', '🎨', '🔮', '🎭', 
                      '🏆', '🎪', '🎸', '🎲', '🎴', '🃏', '🎯', '✨'];

    for (let i = 0; i < gridSize * gridSize; i++) {
        const tile = document.createElement('div');
        tile.className = 'game-tile';
        tile.innerHTML = patterns[i % patterns.length];
        tile.dataset.index = i;
        tile.dataset.pattern = patterns[i % patterns.length];
        
        tile.addEventListener('click', (e) => handleTileClick(e, i));
        
        gameBoard.appendChild(tile);
        gameState.tiles.push({
            element: tile,
            pattern: patterns[i % patterns.length],
            matched: false
        });
    }

    generateSequence();
}

function generateSequence() {
    gameState.sequence = [];
    const levels = Math.min(gameState.level, 5);
    
    for (let i = 0; i < levels + 2; i++) {
        gameState.sequence.push(Math.floor(Math.random() * 16));
    }
}

function handleTileClick(e, index) {
    if (!gameState.gameActive) return;

    const tile = gameState.tiles[index];
    playClickAnimation(tile.element);
    
    gameState.moves.push(index);

    // Check if pattern matches
    if (gameState.moves[gameState.moves.length - 1] === gameState.sequence[gameState.moves.length - 1]) {
        // Correct move
        gameState.score += gameConfig.scorePerMatch;
        tile.element.classList.add('active');
        
        setTimeout(() => {
            tile.element.classList.remove('active');
        }, 200);

        // Check if completed sequence
        if (gameState.moves.length === gameState.sequence.length) {
            gameState.moves = [];
            levelUp();
        }
    } else {
        // Wrong move
        endGame();
    }

    updateDisplay();
}

function levelUp() {
    if (gameState.moves.length % gameConfig.levelThreshold === 0) {
        gameState.level += 1;
        gameState.timeLeft += gameConfig.baseTime;
        generateSequence();
        showToast(`Level Up! 🎉 Level ${gameState.level}`, 'success');
    }
}

function startTimer() {
    setInterval(() => {
        if (gameState.gameActive && gameState.timeLeft > 0) {
            gameState.timeLeft--;
            updateDisplay();
        } else if (gameState.timeLeft === 0) {
            endGame();
        }
    }, 1000);
}

function updateDisplay() {
    document.getElementById('score').textContent = formatScore(gameState.score);
    document.getElementById('level').textContent = gameState.level;
    document.getElementById('timer').textContent = gameState.timeLeft;
    document.getElementById('objective').textContent = 
        `Follow the pattern correctly. Level ${gameState.level}`;
}

function endGame() {
    gameState.gameActive = false;
    const gameTime = Math.floor((Date.now() - gameState.gameStartTime) / 1000);
    
    // Save stats
    GameStatsManager.updateStats('logic', gameState.score, gameState.level);
    LeaderboardManager.addToLeaderboard(
        UserSession.getUser().username,
        gameState.score,
        gameState.level,
        'logic'
    );

    showGameOverModal(gameTime);
}

function showGameOverModal(gameTime) {
    const modal = document.getElementById('gameOverModal');
    document.getElementById('finalScore').textContent = formatScore(gameState.score);
    document.getElementById('levelReached').textContent = gameState.level;
    document.getElementById('timePlayed').textContent = formatTime(gameTime);
    modal.classList.remove('hidden');
}

function restartGame() {
    gameState = {
        score: 0,
        level: 1,
        timeLeft: 30,
        gameActive: true,
        moves: [],
        sequence: [],
        tiles: [],
        gameStartTime: Date.now()
    };
    
    document.getElementById('gameOverModal').classList.add('hidden');
    initializeGame();
    startTimer();
}

function pauseGame() {
    gameState.gameActive = !gameState.gameActive;
    event.target.textContent = gameState.gameActive ? 
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
