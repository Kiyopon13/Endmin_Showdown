// ============================================
// AUTH JS - Login, Registration, Password Reset
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', handlePasswordReset);
    }
});

// Login Handler
function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    // Validation
    if (!username || !password) {
        showMessage('Please fill in all fields', 'error');
        return;
    }

    if (!validateUsername(username)) {
        showMessage('Invalid username format', 'error');
        return;
    }

    if (password.length < 3) {
        showMessage('Password too short', 'error');
        return;
    }

    // Simulate API call
    const user = {
        username: username,
        email: `${username}@endmin.com`,
        loginDate: new Date().toISOString(),
        rank: Math.floor(Math.random() * 1000) + 1
    };

    UserSession.setUser(user);
    showMessage('Login successful! Redirecting...', 'success');
    
    setTimeout(() => {
        window.location.href = 'dashboard.html';
    }, 500);
}

// Registration Handler
function handleRegister(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Validation
    if (!username || !email || !password || !confirmPassword) {
        showMessage('Please fill in all fields', 'error');
        return;
    }

    if (!validateUsername(username)) {
        showMessage('Username must be 3-20 characters and contain only letters, numbers, dots, hyphens, and underscores', 'error');
        return;
    }

    if (!validateEmail(email)) {
        showMessage('Please enter a valid email', 'error');
        return;
    }

    if (!validatePassword(password)) {
        showMessage('Password must be at least 6 characters', 'error');
        return;
    }

    if (password !== confirmPassword) {
        showMessage('Passwords do not match', 'error');
        return;
    }

    // Create user
    const user = {
        username: username,
        email: email,
        joinDate: new Date().toISOString(),
        rank: 'N/A'
    };

    UserSession.setUser(user);
    GameStatsManager.resetStats();
    
    showMessage('Account created successfully! Redirecting...', 'success');
    
    setTimeout(() => {
        window.location.href = 'dashboard.html';
    }, 500);
}

// Password Reset Handler
let resetEmail = '';
let resetCode = '';

function sendResetCode() {
    const email = document.getElementById('email').value.trim();

    if (!email) {
        showMessage('Please enter your email', 'error');
        return;
    }

    if (!validateEmail(email)) {
        showMessage('Please enter a valid email', 'error');
        return;
    }

    // Simulate sending code
    resetCode = generateOTP();
    resetEmail = email;

    showMessage('Code sent to ' + email, 'success');
    showStep(2);
    console.log('Reset code (demo):', resetCode);
}

function verifyCode() {
    const otp = document.getElementById('otp').value.trim();

    if (!otp) {
        showMessage('Please enter the verification code', 'error');
        return;
    }

    if (otp !== resetCode) {
        showMessage('Invalid verification code', 'error');
        return;
    }

    showMessage('Code verified! Enter your new password', 'success');
    showStep(3);
}

function handlePasswordReset(e) {
    e.preventDefault();
    
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (!newPassword || !confirmPassword) {
        showMessage('Please fill in all fields', 'error');
        return;
    }

    if (!validatePassword(newPassword)) {
        showMessage('Password must be at least 6 characters', 'error');
        return;
    }

    if (newPassword !== confirmPassword) {
        showMessage('Passwords do not match', 'error');
        return;
    }

    showMessage('Password reset successful! Redirecting to login...', 'success');
    
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1000);
}

// Helper Functions
function showStep(stepNumber) {
    for (let i = 1; i <= 3; i++) {
        const step = document.getElementById('step' + i);
        if (step) {
            if (i === stepNumber) {
                step.classList.remove('hidden');
            } else {
                step.classList.add('hidden');
            }
        }
    }
}

function generateOTP() {
    return Math.random().toString().substring(2, 8);
}
