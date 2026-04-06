# Endmin Showdown - Modern Web UI Setup Guide

## Quick Start (Choose One Method)

### Method 1: Using Python (Easiest)
```bash
# Navigate to the web directory
cd web/

# Run Python HTTP Server (Python 3)
python -m http.server 8000

# Or Python 2
python -m SimpleHTTPServer 8000

# Open browser: http://localhost:8000
```

### Method 2: Using Node.js
```bash
# Install http-server globally (one-time only)
npm install -g http-server

# Navigate to web directory
cd web/

# Start server
http-server -p 8000

# Open browser: http://localhost:8000
```

### Method 3: Using VS Code
1. Install "Live Server" extension
2. Right-click on `index.html`
3. Select "Open with Live Server"
4. Browser opens automatically

### Method 4: Using Batch Script (Windows)
```bash
# Simply double-click start-server.bat
# The server will start automatically
```

### Method 5: Using Shell Script (Mac/Linux)
```bash
# Make script executable
chmod +x start-server.sh

# Run script
./start-server.sh
```

### Method 6: Direct Browser (No Server)
- Right-click `index.html`
- Select "Open with" → Your Browser
- Note: Some features work best with HTTP server

## Files & Directories

```
web/
├── index.html                    # 🔐 Login Page
├── register.html                 # 📝 Registration
├── forgot-password.html          # 🔑 Password Reset
├── dashboard.html                # 🏠 Main Dashboard
├── logic-game.html              # 🧠 Logic Game
├── memory-game.html             # 🧩 Memory Game
├── global-leaderboard.html      # 🌍 World Rankings
├── leaderboard-game.html        # 📊 Game Rankings
├── friends.html                 # 👥 Friends
├── profile.html                 # 👤 Profile
├── FEATURES.html                # ⭐ Features Documentation
├── README.md                    # 📖 Full Documentation
├── package.json                 # 📦 Node.js Config
├── start-server.sh              # 🐧 Linux/Mac Script
├── start-server.bat             # 🪟 Windows Script
│
├── css/
│   └── styles.css              # 🎨 Main Stylesheet (2000+ lines)
│
└── js/
    ├── main.js                 # 🔧 Core Utilities
    ├── auth.js                 # 🔐 Auth Logic
    ├── dashboard.js            # 📊 Dashboard Logic
    ├── logic-game.js           # 🧠 Logic Game Logic
    ├── memory-game.js          # 🧩 Memory Game Logic
    ├── leaderboard.js          # 🌍 Global Leaderboard
    ├── game-leaderboard.js     # 📊 Game Leaderboards
    ├── friends.js              # 👥 Friends Manager
    └── profile.js              # 👤 Profile Manager
```

## Testing Credentials

Since this uses LocalStorage, any credentials work:

```
Username: testuser
Password: password123

Or create your own account through registration!
```

## Default Test Account

```
Username: ProGamer
Email: progamer@endmin.com
Password: password123
```

## Features to Test

### 1. Authentication
- [ ] Login with test username
- [ ] Create new account
- [ ] Reset password (use any code like "123456")
- [ ] Logout

### 2. Dashboard
- [ ] View stats
- [ ] Navigate to games
- [ ] Check user info in navbar

### 3. Games
- [ ] Play Logic Game
  - Click tiles in sequence
  - Level up by matching patterns
  - View final score
- [ ] Play Memory Game
  - Flip cards to find pairs
  - Watch timer countdown
  - View game over modal

### 4. Leaderboards
- [ ] View Global Leaderboard
- [ ] Filter by game type
- [ ] Check your ranking
- [ ] View Game Leaderboards

### 5. Social Features
- [ ] Add friends
- [ ] Search friends
- [ ] View online status
- [ ] Remove friends

### 6. Profile
- [ ] View statistics
- [ ] Check achievements
- [ ] Update settings
- [ ] View profile info

## Browser Console Tips

### View LocalStorage Data
```javascript
// In DevTools Console:
localStorage.getItem('endmin_user')
localStorage.getItem('endmin_gameStats')
localStorage.getItem('endmin_leaderboard')
localStorage.getItem('endmin_friends')
```

### Clear All Data
```javascript
localStorage.clear()
```

### Check Specific Stats
```javascript
JSON.parse(localStorage.getItem('endmin_gameStats'))
```

## Troubleshooting

### "Cannot find module" Error
- Make sure you're using a local server (not opening `file://`)
- Try different methods above

### CSS/JS Not Loading
- Clear browser cache (Ctrl+Shift+Del)
- Try hard refresh (Ctrl+Shift+R or Cmd+Shift+R)
- Check file paths

### LocalStorage Not Working
- Check browser security settings
- Make sure cookies/storage is enabled
- Try incognito mode (if private mode disabled storage)

### Mobile Not Responsive
- Use `http://localhost:8000` instead of `127.0.0.1:8000`
- Clear cache on mobile browser
- Try different mobile browser

## Performance Tips

1. **Use Modern Browser**: Chrome 90+, Firefox 88+, Safari 14+
2. **Hardware Acceleration**: Enable in browser settings for smooth animations
3. **DevTools**: Use "Performance" tab to profile
4. **Network**: Check "Network" tab in DevTools for load times

## Customization

### Change Theme Colors
Edit in `css/styles.css`:
```css
--primary-teal: #00d4ff;
--primary-blue: #0047ab;
```

### Adjust Game Difficulty
Edit game JavaScript files (`js/logic-game.js`, etc.):
```javascript
const gameConfig = {
    baseTime: 30,        // Increase for more time
    scorePerMatch: 100,  // Increase for more points
    levelThreshold: 5    // Decrease to level up faster
};
```

### Change Font
In `css/styles.css`:
```css
--font-primary: 'Your Font Name', sans-serif;
```

## Deployment

### GitHub Pages
1. Push files to GitHub repository
2. Enable GitHub Pages in settings
3. Site available at: `https://username.github.io/repo-name`

### Netlify
1. Drag and drop `web/` folder to Netlify
2. Deploy automatically
3. Get live URL

### Traditional Server
1. Upload all files to web server
2. Ensure proper file permissions
3. Access via domain

## Development Notes

- All data persists in browser LocalStorage
- No backend required for demo
- JavaScript runs entirely client-side
- Animations use CSS3 (GPU accelerated)
- Responsive design uses CSS Grid & Flexbox

## Documentation

- **README.md** - Complete technical documentation
- **FEATURES.html** - Interactive feature showcase
- **This file** - Setup and usage guide

## Support

For issues:
1. Check browser console (F12 > Console)
2. Verify all files are present
3. Clear cache and hard refresh
4. Try different browser
5. Check browser version compatibility

## Next Steps

1. Open `http://localhost:8000` in your browser
2. Create account (any details work)
3. Play games and explore
4. Check DevTools to see stored data
5. Modify CSS/JS as needed for customization

---

**Enjoy Endmin Showdown!** 🎮✨

For more details, see README.md and FEATURES.html
