# Global Copilot Instructions for Shkuba Card Game

## Project Overview

Shkuba is an Android card game application built with modern Android development technologies. The app implements a traditional card game with an intuitive user interface, multiple language support, and theme customization options.

### Key Features
- Interactive card game with traditional shkuba rules
- Multi-language support (English, Hebrew, Hindi)
- Dark/Light theme switching
- Material 3 Design implementation
- Jetpack Compose UI framework

## Tech Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Build System**: Gradle (Kotlin DSL)
- **Minimum SDK**: API 29 (Android 10)
- **Target SDK**: API 34

### Key Dependencies
- androidx.compose.* (Compose UI)
- androidx.activity.compose (Compose Activity)
- androidx.material3 (Material 3 components)
- androidx.lifecycle.runtime.ktx (Lifecycle management)

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/shkuba/
│   │   │   ├── MainActivity.kt           # Main entry point
│   │   │   ├── GameUI.kt                # Game UI components and logic
│   │   │   └── ui/theme/                # Theme and styling
│   │   └── res/
│   │       ├── values/                  # String resources (English)
│   │       ├── values-iw/               # Hebrew string resources
│   │       ├── values-hi/               # Hindi string resources
│   │       └── drawable/                # Image assets
│   ├── test/                            # Unit tests
│   └── androidTest/                     # Instrumented tests
└── build.gradle.kts                     # Module build configuration
```

## Code Architecture & Patterns

### UI Architecture
- **Composable Functions**: All UI is built using Jetpack Compose
- **State Management**: Uses `remember` and `mutableStateOf` for local state
- **Navigation**: State-based navigation using conditional rendering

### Key Components
- `MainActivity`: Entry point and main state container
- `GameScreen`: Main game interface with card display
- `MainMenu`: Primary navigation menu
- `OptionsScreen`: Settings and preferences
- `InGameMenu`: Pause menu during gameplay

### Data Models
```kotlin
// Core game data structures
data class Card(val value: String, val suit: Suit)
sealed class Suit(val symbol: String)
data class Player(val name: String, val hand: List<Card>)
data class GameState(val players: List<Player>, val tableCards: List<Card>, val currentPlayerIndex: Int)
```

## Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Prefer immutable data structures where possible
- Use sealed classes for type-safe hierarchies

### Compose Best Practices
- Keep composables small and focused
- Use `remember` for expensive computations
- Implement proper state hoisting
- Use `LazyColumn`/`LazyRow` for scrollable lists
- Apply consistent Material 3 theming

### Resource Management
- Store all user-facing strings in `strings.xml`
- Support all three languages (English, Hebrew, Hindi)
- Use semantic color names in themes
- Follow Material 3 design guidelines

## Common Development Tasks

### Adding New UI Screens
1. Create composable function in appropriate file
2. Add navigation logic to `MainScreen`
3. Update state management as needed
4. Add required string resources for all languages

### Internationalization
- Add new strings to `values/strings.xml` (English)
- Translate to `values-iw/strings.xml` (Hebrew)  
- Translate to `values-hi/strings.xml` (Hindi)
- Use `stringResource(R.string.key)` in composables

### Game Logic Implementation
- Extend `GameState` data class for new game features
- Implement game rules in pure functions when possible
- Update UI components to reflect game state changes
- Consider adding proper game state validation

### Theme and Styling
- Define colors in `ui/theme/Color.kt`
- Update theme definitions in `ui/theme/Theme.kt`
- Use MaterialTheme.colorScheme for consistent theming
- Support both light and dark themes

## Testing Strategy

### Unit Tests
- Test game logic functions in isolation
- Validate data model behavior
- Test utility functions and extensions

### UI Tests  
- Use Compose testing framework
- Test user interactions and navigation
- Validate UI state changes
- Test accessibility features

### Running Tests
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

## Build and Development

### Prerequisites
- Android Studio (latest stable)
- JDK 11 or higher
- Android SDK with API 29+ support

### Building the App
```bash
./gradlew assembleDebug          # Debug build
./gradlew assembleRelease        # Release build
./gradlew installDebug           # Install debug on device
```

### Gradle Tasks
```bash
./gradlew clean                  # Clean build artifacts
./gradlew build                  # Full build with tests
./gradlew lint                   # Run static analysis
```

## Common Patterns to Follow

### State Management Pattern
```kotlin
@Composable
fun MyScreen() {
    val state = remember { mutableStateOf(initialValue) }
    
    MyScreenContent(
        state = state.value,
        onAction = { state.value = newValue }
    )
}
```

### Localization Pattern
```kotlin
@Composable
fun LocalizedText() {
    Text(
        text = stringResource(R.string.my_key),
        style = MaterialTheme.typography.titleMedium
    )
}
```

### Theme-Aware Styling
```kotlin
@Composable
fun ThemedCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        // Content
    }
}
```

## Performance Considerations

- Use `LazyColumn`/`LazyRow` for large lists
- Implement proper key management for dynamic lists
- Avoid unnecessary recompositions with stable parameters
- Use `derivedStateOf` for computed values
- Consider `LaunchedEffect` for side effects

## Accessibility Guidelines

- Provide meaningful content descriptions
- Ensure sufficient color contrast
- Support screen readers with proper semantics
- Test with TalkBack enabled
- Use appropriate heading hierarchy

## Contributing Guidelines

1. Create feature branches from main
2. Follow existing code patterns and conventions
3. Add appropriate tests for new functionality
4. Update string resources for all supported languages
5. Test on different screen sizes and orientations
6. Ensure accessibility compliance

## Troubleshooting Common Issues

### Build Issues
- Ensure Android SDK is properly configured
- Verify Gradle version compatibility
- Check for proper plugin versions in build files

### UI Issues
- Verify Material 3 theme configuration
- Check for proper state management
- Ensure composable functions are properly structured

### Localization Issues
- Verify string keys exist in all language files
- Check for proper resource qualification
- Test with different system languages

## Future Enhancements

Consider these areas for future development:
- Multiplayer support with network connectivity
- Game statistics and player profiles  
- Additional card game variants
- Improved animations and visual effects
- Cloud save functionality
- Achievement system

---

This file should be updated as the project evolves to maintain accurate guidance for contributors.