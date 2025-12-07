# COMP2042 Coursework: Modifying and Extending Existing Code

---
- Student ID: 20723677

## 1. GitHub Repository
- Repo: `https://github.com/huixin17/CW2025`

---

## 2. Compilation Instructions
- **Prerequisites**
    - JDK 17+ (Temurin 17.0.11 verified)
    - Maven 3.9.x (or `mvnw`)
    - JavaFX pulled automatically via `org.openjfx:javafx-maven-plugin` (21.0.6 modules)
- **Build / Run**
    - `git clone https://github.com/huixin17/CW2025.git && cd CW2025`
    - `./mvnw clean package` to compile + run tests
    - `./mvnw javafx:run` to launch `com.comp2042.Main`
    - `./mvnw test` for unit tests only
    - IDE tips: import as Maven project, set SDK=JDK 17+, mark `src/main/java` and `src/main/resources`, run `javafx:run` goal
- **Common fixes**
    - Refactoring Artifacts: After moving GuiController, ensure gameLayout.fxml is updated to reference the new package path: fx:controller="controller.gui.GuiController".
    - Missing JavaFX modules → always use Maven run goal
    - Version mismatch → set both IDE and Maven to JDK 17+

---

## 3. Executive Summary of Key Modifications

The coursework focused on addressing architectural debt and expanding gameplay. The initial monolithic control structure was completely overhauled to adhere to the Single Responsibility Principle (SRP). This involved delegating core responsibilities to a specialized suite of helper classes, implementing the Orchestrator Design Pattern.
### Primary Architectural Changes:
- Architectural Refactoring: The singular GuiController was decomposed into seven specialized manager classes, isolating concerns like input, rendering, and game state management.
- Package Reorganization: Classes were systematically sorted into controller.gui (UI presentation and helpers) and controller.game (core game logic and orchestration) packages.
- Feature Integration: A dynamic skill points economy, a power-up shop, and a functional hold piece mechanic were introduced to enhance strategic gameplay depth.
- Critical Bug Fix: The game's pause synchronization was corrected, preventing inadvertent block movement during the resume countdown phase.

---

## 4. Implemented and Verified Functionality

### Core Gameplay Mechanics
- Standard Tetromino handling (spawning, movement, rotation, hard/soft drops).
- Accurate collision detection (walls, floor, settled blocks).
- Scoring system with line-clear bonuses and game over detection.
- Hold Piece Mechanic: Allows storage and swapping of a single Tetromino using the 'C' key.

### UI and Enhanced Features
- Responsive next-piece and hold-piece preview displays.
- Real-time SCORE and LINES tracking, alongside the new SKILL PTS currency.
- Fully functional Pause System with an overlaid menu (Resume, New Game, Quit options).
- The Power-Up Shop is accessible via the 'B' key, allowing purchases using SKILL PTS.
- Custom background video playback provides a modern visual theme.

### System and Design Integrity
- Project compiles and runs reliably via Maven.
- Unit tests confirm the integrity of the score and other core logic.
- The Orchestrator Pattern ensures high cohesion and low coupling across the controller layer.

## 5. Components Requiring Revision
- None

## 6. Unimplemented Features
- Full Audio System: A comprehensive sound effects and background music system was planned but remains unimplemented. This includes looping background music, sound effects for hard drops and line clears, and mixer controls.
- Persistent Data: Online leaderboards or local persistent storage for high-scores are not implemented.
- Alternate Modes: Features like time attack or endless challenge variants were not prioritized.
---

## 7. Project Structure Layout
- Model: Contains the core game state and logic (e.g., SimpleBoard, Score, PowerUpManager).
- Controller: Orchestrates the system, separating UI interaction (controller.gui) from game logic coordination (controller.game).
- View: Defines the user interface using FXML and custom JavaFX components.

---

## 8. New Java Classes Added to the Project

- **Model:** Game logic and state
- **View:** JavaFX UI components and FXML
- **Controller:** Input handling and orchestration

---

## 8. New Java Classes

### GUI Helper Classes (Location: `src/main/java/com/comp2042/controller/gui`)
- **`GuiControllerKeyboardHandler.java`** – Handles all keyboard input and command dispatch (movement, pause, shop toggle, power-up activation). Isolates I/O and user input mapping from game logic.

- **`GuiControllerRenderer.java`** – Manages all visual updates: drawing the board, next piece, hold piece, and ghost piece. Centralizes UI drawing logic, decoupling it from game state updates.

- **`GuiControllerEffectManager.java`** – Manages ephemeral visual feedback, such as the Bomb explosion animation and other particle effects. Dedicated module for managing visual flair and animations.

- **`GuiControllerPauseManager.java`** – Manages the game's paused state, including the pause overlay and the 3-second resume countdown. Encapsulates game flow control and fixed a critical concurrency bug.

- **`GuiControllerSlowMotionManager.java`** – Controls the game's timeline speed and manages the duration of the Slow Motion power-up effect. Isolates time-modification logic.

- **`GuiControllerVideoManager.java`** – Manages the background video asset, including loading, playback, and lifecycle control. Separates media resource handling from core game timing.

- **`GuiControllerPowerUpManager.java`** – Manages the Power-Up Shop UI, inventory display, and SKILL PTS visualization. Decouples the new feature's presentation layer from core controller duties.

### Game Logic 
- **`GameController.java`** (New Package: controller.game/) – Now acts as the central coordinator, reacting to events and updating the board.

## 9. Existing Classes Substantially Modified

### `controller.gui.GuiController`
- **Location**: `src/main/java/com/comp2042/controller/gui/GuiController.java`
- **Changes Made**:
    - Refactored from a large, monolithic class into a lightweight Orchestrator , delegating all core functions to the seven newly created Manager classes.
- **Rationale for change**: Enforced the Single Responsibility Principle (SRP).

### `controller.gui.GuiControllerPauseManager`
- **Location**: `src/main/java/com/comp2042/controller/gui/GuiControllerPauseManager.java`
- **Changes Made**:
    - Explicitly enforces the `isPause` flag and timeline pause state during the 3-second resume countdown to fix a major bug. Added resumeImmediately() for smooth new game startups.
- **Rationale for change**: Corrected the bug where blocks continued to fall during the resume countdown.

### `controller.game.GameController`
- **Location**: `src/main/java/com/comp2042/controller/game/GameController.java`
- **Changes Made**:
    - Updated to integrate the `PowerUpManager`, calculate skill point awards based on hard drops and line clears, and trigger appropriate GUI callbacks.
- **Rationale for change**: Enabled the new skill points economy and power-up system.

### `model.Score`
- **Location**: `src/main/java/com/comp2042/model/Score.java`
- **Changes Made**:
    - Extended to track `skillPoints` separately using a `SimpleIntegerProperty`, in addition to the traditional score, and added logic for shop-related transactions.
- **Rationale for change**: Established the foundation for the Power-Up economy.

### `model.SimpleBoard`
- **Location**: `src/main/java/com/comp2042/model/SimpleBoard.java`
- **Changes Made**:
    - Enhanced collision logic and added methods to handle bomb placements and other power-up initiated board changes (e.g., immediate row clearance).
- **Rationale for change**: Extended the core game model to support power-up functionality.

---

## 10. Modified Files & Resources
- src/main/resources/gameLayout.fxml – Updated to include FXML IDs and bindings for skill point displays, power-up buttons, the shop overlay, and the new controller package reference.
- src/main/resources – New assets added, including the background video (`NeonLights.mp4`) and various HUD graphic files.
- README.md – Significantly expanded to document the new architecture and gameplay features.

---

## 11. Testing and Regression Notes
- Manual Validation: Comprehensive manual playtesting verified the correct functionality of the new Hold Piece feature, the reliable Pause/Resume cycle, and the integration of all power-up effects.
- Unit Tests: Existing and new unit tests (e.g., ScoreTest) confirm that core logic, such as scoring calculation and movement mechanics, has not been broken by the refactoring.

---

## 12. Resolved Development Issues

### Problem 1: Package Migration Conflicts
- **Root Cause**: Errors arose due to simultaneous existence of old and new controller helper classes, leading to ambiguous imports.
- **Solution**: Systematically deleted obsolete, redundant helper classes from the root `controller` package after migration was complete.

### Problem 2: FXML Controller Reference Error
- **Root Cause**: Moving `GuiController` to `controller.gui` broke the FXML path, as `gameLayout.fxml` retained the old package reference.
- **Solution**: Updated `gameLayout.fxml` to correctly reference `fx:controller="controller.gui.GuiController"`.

### Problem 3: Blocks Dropping During Countdown
- **Root Cause**: The resume countdown routine was purely visual and failed to enforce a proper paused state on the game timeline.
- **Solution**: Modified `GuiControllerPauseManager` to explicitly pause the game timeline and set the `isPause` flag before initiating the countdown sequence.

---

## 13. Project Summary
This maintenance and extension task successfully restructured a monolithic codebase using the Orchestrator Pattern to achieve high marks in structural integrity and maintainability. The refactoring effort created a robust foundation, enabling the seamless integration of complex new features, including a Skill Points Economy, a functional Power-Up Shop, and critical quality-of-life fixes to the game flow.