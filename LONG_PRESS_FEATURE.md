# Long-Press Context Menu Feature

## Overview
This feature adds a long-press context menu to app icons in the app drawer, similar to Pixel launcher behavior. Users can now access app management options by long-pressing on any app icon.

## Features Implemented

### 1. Long-Press Gesture Detection
- Uses `combinedClickable` modifier for seamless click and long-click handling
- Preserves existing single-tap functionality to launch apps
- Provides haptic feedback on long press for better user experience

### 2. Context Menu Popup
The context menu displays two options:
- **App Info**: Opens the system settings page for the app
- **Uninstall**: Opens the system uninstall dialog for the app

### 3. Pixel-Style Animations

#### Menu Appearance
- **Scale Animation**: Menu scales from 0.8x to 1.0x using spring animation with medium bouncy damping
- **Fade-In**: Alpha animates from 0 to 1 over 200ms for smooth appearance
- **Elevation**: 8dp shadow elevation for depth perception

#### Icon Feedback
- **Scale Effect**: Icon scales from 1.0x to 1.05x when menu is active
- Provides visual feedback that the menu is associated with that icon

### 4. System Integration

#### Uninstall Intent
```kotlin
Intent(Intent.ACTION_DELETE).apply {
    data = Uri.parse("package:${app.packageName}")
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
```
Opens the system's uninstall confirmation dialog.

#### App Info Intent
```kotlin
Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.parse("package:${app.packageName}")
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
```
Opens the detailed app settings page in system settings.

## User Interaction Flow

1. User **long-presses** on any app icon in the app drawer
2. Device provides **haptic feedback** (vibration)
3. Icon **scales up slightly** (1.05x)
4. Context menu **animates in** near the icon (scale + fade)
5. User can:
   - Tap "App Info" → Opens system app settings
   - Tap "Uninstall" → Opens uninstall dialog
   - Tap outside menu → Menu dismisses
   - Tap back button → Menu dismisses
6. Menu **animates out** and icon returns to normal size

## Design Decisions

### Menu Position
- Positioned relative to the icon with offset: `IntOffset(32, -20)`
- This places the menu slightly above and to the right of the icon
- Ensures menu doesn't overflow screen boundaries in most cases

### Color Scheme
- Background: Uses app's `SurfaceCard` color
- Border: White with 20% opacity (`theme.border`)
- App Info: White icon and text (80-90% opacity)
- Uninstall: Red color (#E57373) to indicate destructive action
- Divider: White with 10% opacity

### Animation Timing
- **Spring Animation**: Medium bouncy damping, medium stiffness
  - Creates a natural, responsive feel
  - Matches Material Design motion guidelines
- **Fade Duration**: 200ms
  - Quick enough to feel responsive
  - Slow enough to be noticeable and smooth

## Accessibility Considerations

1. **Haptic Feedback**: Provides tactile confirmation for users with visual impairments
2. **Clear Icons**: Info and Delete icons are standard and recognizable
3. **Color Coding**: Red color for destructive "Uninstall" action follows common patterns
4. **Focus Management**: Popup is focusable and can be dismissed with back button

## System App Handling

The implementation uses standard Android intents, which handle system app restrictions automatically:
- System apps that cannot be uninstalled will show appropriate error messages
- Pre-installed apps may show "Uninstall updates" option instead of full uninstall
- All behavior matches the standard Android system behavior

## Code Structure

### New Components
- `AppContextMenu`: The popup menu composable with animations
  - Parameters: app info, position offset, callbacks, theme
  - Internal state: visibility for animation triggers
  - Animations: scale and alpha using `animateFloatAsState`

### Modified Components
- `AppGridItemV2`: Enhanced to support long-press
  - Added state: `showContextMenu` boolean and `menuOffset` position
  - Added haptic feedback handler
  - Added scale animation for icon
  - Integrated `AppContextMenu` component

### Dependencies Added
- `androidx.compose.animation.core.*`: For animation APIs
- `androidx.compose.foundation.combinedClickable`: For gesture detection
- `androidx.compose.ui.hapticfeedback.*`: For haptic feedback
- `androidx.compose.ui.window.Popup`: For popup menu positioning
- `android.content.Intent`, `android.net.Uri`, `android.provider.Settings`: For system intents

## Testing Recommendations

### Manual Testing
1. **Long Press Detection**
   - Long press on various app icons
   - Verify haptic feedback occurs
   - Verify menu appears near the icon

2. **Menu Functionality**
   - Test "App Info" on multiple apps
   - Test "Uninstall" on user-installed apps
   - Test on system apps (should show appropriate restrictions)

3. **Animations**
   - Observe menu scale and fade-in
   - Observe icon scale effect
   - Verify smooth transitions

4. **Menu Dismissal**
   - Tap outside menu
   - Press back button
   - Select a menu option

5. **Edge Cases**
   - Long press on top row icons (menu positioning)
   - Long press on bottom row icons (menu positioning)
   - Rapid long presses on different icons

### Automated Testing Considerations
- Test gesture recognition (long press detection)
- Test intent creation with correct package names
- Test animation state transitions
- Test menu dismissal triggers

## Future Enhancements

Possible improvements for future iterations:
1. Add more menu options (e.g., "Add to favorites", "Hide app")
2. Smart menu positioning based on screen boundaries
3. Menu appearance from the touched point (ripple effect)
4. Adaptive menu options based on app type
5. Swipe-to-dismiss gesture
6. Settings to customize long-press duration
7. Background blur effect when menu is open
