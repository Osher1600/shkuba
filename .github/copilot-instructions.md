# Copilot Instructions for Shkuba (Android Frontend - Kotlin)

## 🎯 Goal
Build a clean, maintainable Kotlin Android UI for the Shkuba card game, using modern UI/UX principles and object-oriented design (OOP), SOLID principles, and Android architecture best practices (MVVM).

---

## 📐 Architecture Guidelines

- Use **single responsibility per class**
- Minimize logic in Activities/Fragments
- Ensure clean separation of UI and game logic

---

## 🧠 Kotlin + UI Best Practices

### ✅ Naming
- Classes: `PascalCase`, e.g. `GameViewModel`, `CardAdapter`
- Variables: `camelCase`, e.g. `playerHand`, `onCardClick`
- Constants: `UPPER_SNAKE_CASE`
- XML IDs: `snake_case`, e.g. `player_hand_recycler`

### ✅ UI Components
- Use `RecyclerView` to render lists of cards (hand/table)
- Use `CardView` or custom `ViewHolder` for each card
- Use `ConstraintLayout` for layout control

### ✅ Interactions
- On card click: send intent to ViewModel (`playCard(card)`)
- ViewModel processes intent and updates game state
- UI observes and reflects the state changes via LiveData

---

## 🔧 Copilot Should Generate Code That

✅ Follows SOLID:
- **S**ingle Responsibility: no bloated classes  
- **O**pen/Closed: use interfaces for extension  
- **L**iskov: respect type hierarchies  
- **I**nterface Segregation: split interfaces if needed  
- **D**ependency Inversion: inject dependencies (manually or via constructor)

✅ Is testable:
- No direct context usage in logic classes
- Keep side-effects out of ViewModel

✅ Uses clean UI/UX principles:
- Padding, spacing, consistent sizing
- Clickable areas large enough for touch (≥ 48dp)
- Clear visual feedback on actions (e.g., highlighting selected card)

---

## 💼 UI Component Breakdown (by responsibility)

| Component            | Responsibility                                       |
|----------------------|------------------------------------------------------|
| `GameFragment.kt`    | Display game screen, observe ViewModel, handle UI   |
| `GameViewModel.kt`   | Handle game state, update cards, turns, messages    |
| `CardAdapter.kt`     | RecyclerView adapter to show cards (player/table)   |
| `CardViewHolder.kt`  | Display single card (image + value)                 |
| `GameState.kt`       | Data model for round state                          |
| `Card.kt`            | Model for card (value, suit, isSelected)            |

---

## 📱 UI Screens (to be built)

### 🎮 Game Screen
- RecyclerView for player's hand (bottom)
- RecyclerView for table cards (top)
- Button area: Play / Take / End Turn
- Scoreboard or Toast messages after round ends

---

## 📄 Example Prompts for Copilot

### 🧩 GameFragment.kt
```kotlin
// Display two RecyclerViews: tableCards (top), playerHand (bottom)
// When card is clicked, notify ViewModel via onCardClick(card)