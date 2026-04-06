-- SQL schema for the game leaderboard application

CREATE DATABASE IF NOT EXISTS game_leaderboard;
USE game_leaderboard;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    lichess VARCHAR(50)
);

CREATE TABLE stats (
    username VARCHAR(50) NOT NULL,
    game VARCHAR(20) NOT NULL,
    games INT DEFAULT 0,
    wins INT DEFAULT 0,
    time INT DEFAULT 0,
    PRIMARY KEY(username, game),
    FOREIGN KEY(username) REFERENCES users(username)
);

CREATE TABLE achievements (
    username VARCHAR(50) NOT NULL,
    achievement VARCHAR(100) NOT NULL,
    PRIMARY KEY(username, achievement),
    FOREIGN KEY(username) REFERENCES users(username)
);

CREATE TABLE friends (
    user1 VARCHAR(50) NOT NULL,
    user2 VARCHAR(50) NOT NULL,
    PRIMARY KEY(user1, user2),
    FOREIGN KEY(user1) REFERENCES users(username),
    FOREIGN KEY(user2) REFERENCES users(username)
);

CREATE TABLE games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    game_type VARCHAR(20) NOT NULL,
    won BOOLEAN NOT NULL,
    time_spent INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(username) REFERENCES users(username)
);

CREATE TABLE moves (
    game_id BIGINT NOT NULL,
    move_number INT NOT NULL,
    move_text VARCHAR(100) NOT NULL,
    PRIMARY KEY(game_id, move_number),
    FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE
);
