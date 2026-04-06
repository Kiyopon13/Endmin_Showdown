# Endmin Showdown - Modern Web UI

A modernized, professional web-based frontend for the Endmin Showdown mini games platform featuring dark gradient theme, glassmorphism effects, and smooth animations.

## Features

### 🎨 Design
- **Dark Gradient Theme**: Teal to dark blue gradient background with animated effects
- **Glassmorphism**: Semi-transparent cards with blur effects and soft shadows
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Smooth Animations**: Fade, slide, bounce, and scale animations throughout
- **Professional UI**: Font Awesome icons and Poppins font for modern aesthetics

### 🎮 Game Pages
- **Logic Game**: Pattern matching game with increasing difficulty levels
  - Grid-based tiles with emoji patterns
  - Progressive difficulty with time pressure
  - Score tracking and level advancement
  
- **Memory Game**: Classic memory card matching game
  - 3D flip animations for cards
  - Pair matching mechanics
  - Move counter and performance metrics

### 📊 Leaderboards
- **Global Leaderboard**: Worldwide rankings across all games
  - Top 3 players highlighted with gold, silver, bronze styling
  - Alternating row colors with hover effects
  - Sticky table headers
  - User ranking display
  
- **Game Leaderboards**: Game-specific rankings
  - Separate leaderboards for Logic and Memory games
  - Player statistics and level tracking
  - Performance metrics

### 👥 Social Features
- **Friends Page**: Connect and manage friends
  - Search functionality with real-time filtering
  - Online/offline status indicators
  - Add/remove friend buttons
  - Friend avatar system
  
- **Profile Page**: User profile and settings
  - Personal statistics dashboard
  - Achievement system with tracking
  - Account settings (email, password)
  - Account deletion option

### 🔐 Authentication
- **Login Page**: Glass-style card with modern inputs
  - Username and password fields with icons
  - Password visibility toggle
  - Gradient login button with glow effect
  - Forgot password link
  
- **Registration Page**: Create new accounts
  - Email validation
  - Password confirmation
  - Real-time form validation
  
- **Password Reset**: Multi-step recovery process
  - Email verification
  - OTP code validation
  - Password reset with confirmation

## File Structure

```
web/
├── index.html              # Login page
├── register.html           # Registration page
├── forgot-password.html    # Password reset page
├── dashboard.html          # Main game dashboard
├── logic-game.html         # Logic game page
├── memory-game.html        # Memory game page
├── global-leaderboard.html # Global rankings
├── leaderboard-game.html   # Game-specific rankings
├── friends.html            # Friends page
├── profile.html            # User profile page
├── css/
│   └── styles.css         # Main stylesheet with all styling
└── js/
    ├── main.js            # Shared utilities and navigation
    ├── auth.js            # Authentication logic
    ├── dashboard.js       # Dashboard functionality
    ├── logic-game.js      # Logic game implementation
    ├── memory-game.js     # Memory game implementation
    ├── leaderboard.js     # Global leaderboard
    ├── game-leaderboard.js # Game-specific leaderboards
    ├── friends.js         # Friends management
    └── profile.js         # Profile management
```

## Getting Started

### Installation

1. **Clone or download the project**
   ```bash
   # Navigate to the web directory
   cd web/
   ```

2. **Open in a web server** (recommended for best experience)
   ```bash
   # Using Python 3
   python -m http.server 8000
   
   # Using Python 2
   python -m SimpleHTTPServer 8000
   
   # Using Node.js (http-server)
   npx http-server
   ```

   Then visit: `http://localhost:8000`

3. **Or open directly in browser**
   - Right-click on `index.html`
   - Select "Open with" > Your preferred browser
   - Note: Some features work best when served over HTTP/HTTPS

## Usage Guide

### Creating an Account
1. Click "Create Account" on the login page
2. Enter username (3-20 characters, alphanumeric with .- _)
3. Enter email address
4. Create and confirm password
5. Click "Create Account"

### Playing Games

#### Logic Game
1. From dashboard, click "Play Now" on Logic Game card
2. Watch the pattern sequence at the start of each level
3. Click tiles in the correct order
4. Level up by matching 5+ sequences correctly
5. Time pressure increases as you progress
6. View final score and level when time runs out

#### Memory Game
1. From dashboard, click "Play Now" on Memory Game card
2. Click cards to flip and reveal emojis
3. Find matching pairs to earn points
4. Level up by finding all 8 pairs
5. Minimize moves for better efficiency
6. Time limit increases with each level

### Viewing Leaderboards
1. **Global Leaderboard**: See worldwide rankings
   - Filter by "All Games", "Logic", or "Memory"
   - Your rank is highlighted at the bottom

2. **Game Leaderboards**: Compare game-specific rankings
   - Switch between Logic and Memory tabs
   - See player statistics per game

### Managing Friends
1. **My Friends Tab**: View current friends
   - Search for specific friends
   - See online/offline status
   - Remove friends

2. **Find Friends Tab**: Discover new players
   - Browse suggested players
   - Add new friends with one click

### Profile Management
1. **Statistics**: View all your game stats and achievements
2. **Achievements**: Track unlocked and locked achievements
3. **Settings**: Update email and password
4. **Danger Zone**: Delete your account (irreversible)

## Technical Details

### Technology Stack
- **HTML5**: Semantic markup structure
- **CSS3**: Advanced features
  - CSS Grid and Flexbox for layout
  - CSS Custom Properties (variables)
  - Backdrop filters for glassmorphism
  - Keyframe animations
  - Media queries for responsiveness
- **JavaScript (ES6+)**:
  - LocalStorage for data persistence
  - Classes for organization
  - Event listeners for interactivity
  - Async/await ready structure

### Data Storage
- All data stored in browser's LocalStorage
- User sessions persist between page refreshes
- Game statistics and leaderboards cached locally
- Friends list maintained locally
- No server connection required for demo

### Browser Compatibility
- Chrome/Chromium 90+
- Firefox 88+
- Safari 14+
- Edge 90+
- Mobile browsers (iOS Safari, Chrome Mobile)

## Features Breakdown

### 1. Dark Gradient Theme
- **Color Palette**:
  - Primary Teal: #00d4ff
  - Primary Blue: #0047ab
  - Dark Background: #0a0e27
  - Text: #ffffff / #b0b0b0
  
- **Animated Background**: 
  - Multi-layer gradient shift animation
  - Floating radial gradient overlays
  - Creates depth and visual interest

### 2. Glassmorphism Effects
- **Semi-transparent cards**: 60-80% opacity
- **Backdrop blur**: 10px blur effect
- **Soft borders**: rgba(255, 255, 255, 0.1)
- **Glow effects**: Box shadows with color tints
- **Hardware acceleration**: GPU rendering for smooth performance

### 3. Animations & Transitions
- **Fade In**: Smooth opacity transition
- **Slide Up**: Transform translate animation
- **Click Animation**: Scale on interaction
- **Flip Animation**: 3D transform for memory cards
- **Bounce Animation**: Game icons with vertical bounce
- **Toast Notification**: Slide from bottom animation

### 4. Responsive Design
- **Desktop**: Full multi-column layouts
- **Tablet**: 2-column grids adapting to screen size
- **Mobile**: Single column with optimized spacing
- **Flexible Components**: Cards and buttons scale appropriately
- **Touch-Friendly**: Larger touch targets on mobile

### 5. Accessibility Features
- **Semantic HTML**: Proper heading hierarchy
- **Color Contrast**: WCAG AA compliant
- **Focus States**: Visible keyboard navigation
- **Form Labels**: Proper input associations
- **Icon + Text**: Icons paired with text labels
- **Alt Text**: Images have descriptive alt text

## Customization

### Changing Colors
Edit CSS variables in `css/styles.css`:
```css
:root {
    --primary-teal: #00d4ff;
    --primary-blue: #0047ab;
    --accent-gold: #ffd700;
    /* ... etc */
}
```

### Adjusting Game Difficulty
In game JavaScript files (e.g., `js/logic-game.js`):
```javascript
const gameConfig = {
    baseTime: 30,           // Starting time in seconds
    timeDecrement: 2,       // Time reduction per level
    scorePerMatch: 100,     // Points per correct match
    levelThreshold: 5       // Matches needed to level up
};
```

### Modifying Leaderboard Display
In `js/leaderboard.js`:
```javascript
leaderboard = leaderboard.slice(0, 50);  // Change 50 to desired count
```

## Event Flow

### Login Flow
1. User enters credentials
2. Form validation (email format, password length)
3. User object created and stored in LocalStorage
4. Redirect to dashboard
5. Dashboard loads user stats automatically

### Game Flow
1. User clicks "Play Now"
2. Game initializes with board/cards
3. Game timer starts
4. User interaction tracked
5. Win/loss conditions checked
6. Game over modal displays results
7. Stats saved to LocalStorage
8. User can restart or return to dashboard

### Leaderboard Flow
1. Page loads leaderboard data from LocalStorage
2. Data sorted by score descending
3. Rows formatted with rankings
4. Top 3 highlighted with special styling
5. User's own rank highlighted separately
6. Filter buttons update displayed data

## Performance Optimization

- **CSS Animations**: Use GPU acceleration with `transform` and `opacity`
- **Event Delegation**: Single listeners for multiple elements
- **LocalStorage Limits**: Efficient data structure to avoid quota
- **Image Optimization**: SVG icons via Font Awesome
- **Lazy Loading**: Components render on demand

## Known Limitations

- Data stored locally (no cloud sync)
- Single-device accounts (LocalStorage is device-specific)
- No multiplayer features (can be added with backend)
- No persistent user accounts (demo-only)
- Mobile Safari: Some animation performance may vary

## Future Enhancements

- Backend API integration for persistent accounts
- Real-time multiplayer modes
- Power-ups and special items in games
- Daily challenges and rewards
- Social features (messaging, team battles)
- Analytics and performance tracking
- Dark/light theme toggle
- Sound effects and music
- Offline mode support

## Browser DevTools Tips

1. **LocalStorage Inspection**:
   - Open DevTools (F12)
   - Application > LocalStorage
   - View stored game data in real-time

2. **Animation Testing**:
   - Elements panel > select element
   - Animations tab shows active animations
   - Pause/slow down animations for inspection

3. **Responsive Testing**:
   - Toggle Device Toolbar (Ctrl+Shift+M)
   - Test different device sizes
   - Check touch interactions

## Credits

- **Design Framework**: Custom CSS3 with Glassmorphism
- **Icons**: Font Awesome 6.4.0
- **Fonts**: Poppins, Segoe UI
- **Avatar Generation**: UI Avatars API

## License

This project is designed for academic evaluation. Feel free to modify and use as needed.

## Support

For issues or questions:
1. Check browser console (F12 > Console) for errors
2. Verify LocalStorage is enabled
3. Clear cache/cookies if experiencing issues
4. Ensure using a modern browser

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Status**: Production Ready for Academic Evaluation
