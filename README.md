# Arise Launcher

A productivity-focused Android launcher that gamifies your daily tasks and helps you stay focused by tracking app usage and rewarding productive behavior.

## 🚀 Features

### 📱 Custom Launcher Experience
- **Modern Home Screen**: Clean, minimalist interface designed for productivity
- **App Drawer**: Organized app drawer with smooth animations and easy access to all your applications
- **Gesture Controls**: Intuitive swipe gestures to access different screens and features
- **Edge-to-Edge Display**: Modern UI that takes full advantage of your device's screen

### ✅ Task Management
- **Task Creation & Organization**: Create tasks with titles, descriptions, and point values
- **Task Categories**: Organize tasks into four categories:
  - 👥 **People**: Relationship and networking tasks
  - 🎯 **Opportunity**: Career and business opportunities
  - 📚 **Skills**: Learning and skill development
  - 💫 **Personal**: Personal growth and wellness
- **Task Priorities**: Set priority levels for your tasks
- **Repeating Tasks**: Schedule tasks to repeat on specific days of the week
- **Task Links**: Attach related links, articles, or videos to tasks for reference
- **Task Completion Tracking**: Mark tasks as complete and track completion history
- **Weekly Schedule View**: Optional weekly view to organize your tasks by day

### 🎮 Gamification & Points System
- **Point Rewards**: Earn points by completing tasks
- **Rank System**: Progress through multiple ranks based on your accumulated points
- **Points History**: Track your earnings and spending over time
- **Task History Screen**: Review all your completed and pending tasks

### ⏱️ App Usage Timer (Tunnel Vision Mode)
- **Focus Tracking**: Automatic timer overlay when you leave the launcher
- **Dynamic Island UI**: iOS-inspired floating timer display showing:
  - Current app name
  - Elapsed time
  - Points being lost
- **App Classification**: Automatically categorizes apps into:
  - 📊 Productivity
  - 📱 Social Media
  - 🎮 Games
  - 📺 Streaming
  - 🎭 Entertainment
  - 🔧 Utility
  - 📦 Miscellaneous
- **Points Deduction**: Lose points for spending time in non-productive apps
- **Vibration Feedback**: Haptic feedback when timer starts/stops

### 🎯 Tunnel Vision Mode
- **Enhanced Focus Mode**: Dedicated mode for intense productivity sessions
- **Focus Tasks vs Personal Tasks**: Separate task categories when in Tunnel Vision mode
- **Stricter Time Tracking**: More aggressive point deduction for distractions

### ⚙️ Settings & Customization
- **Default Launcher**: Set Arise Launcher as your default home screen
- **Toggle Features**:
  - Tunnel Vision Mode on/off
  - Weekly Schedule display
  - App Timer functionality
  - Hide completed tasks option
- **Flexible Configuration**: Customize the launcher to match your productivity style

### 🎨 User Interface
- **Material Design 3**: Modern Material You design language
- **Dark Theme**: Eye-friendly dark color scheme
- **Smooth Animations**: Polished transitions and interactions
- **Responsive Layout**: Optimized for various screen sizes

### 🔐 Permissions & Privacy
- **Usage Stats Permission**: Required for app usage tracking (optional feature)
- **Overlay Permission**: Needed for the floating timer display
- **Package Monitoring**: Tracks app installations/removals for drawer updates
- **Onboarding Flow**: Clear permission explanations during first launch

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture principles
- **Dependency Injection**: Dagger Hilt
- **Database**: Room (SQLite)
- **Navigation**: Jetpack Navigation Compose
- **Coroutines**: For asynchronous operations
- **State Management**: StateFlow and Compose State

## 📋 Requirements

- **Android Version**: Android 9.0 (API 28) or higher
- **Target SDK**: Android 14 (API 35)
- **Permissions**:
  - Usage Stats Access (optional, for app timer)
  - Overlay Permission (optional, for floating timer)
  - Package Manager (for app drawer)

## 🎯 Use Cases

- **Students**: Track study tasks and limit social media usage
- **Professionals**: Manage work tasks and monitor productivity
- **Entrepreneurs**: Balance opportunities, networking, and skill development
- **Anyone**: Seeking to gamify their daily routine and stay focused

## 📱 Screens

1. **Welcome Screen**: First-time user introduction
2. **Permission Onboarding**: Guide users through required permissions
3. **Home Screen**: Main launcher interface with task management
4. **App Drawer**: Access all installed applications
5. **Points Screen**: View your points, rank, and statistics
6. **Task History**: Review completed and pending tasks
7. **Task Details**: Detailed view with links and completion tracking
8. **Settings**: Customize launcher behavior and features
9. **Drive Screen**: Additional productivity features

## 🔄 Version

Current Version: **0.0.1**

---

Built with ❤️ for productivity enthusiasts

