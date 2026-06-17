# SaveSmart: Advanced Personal Budgeting and Gamified Finance

## 1. Purpose of the App
SaveSmart is an offline-first Android application designed to bridge the gap between standard financial management and user engagement. 

### The Problem
Traditional budgeting tools are often perceived as tedious or complex, leading to "tracking fatigue" where users stop logging their expenses after a few days. Additionally, many applications suffer from floating-point rounding errors when handling multiple currencies or small transactions, leading to inaccurate financial records over time.

### The Solution
SaveSmart solves these issues by:
- **Gamification**: Integrating a unified engine with points, levels, and badges to turn financial discipline into a rewarding experience.
- **Precision Engineering**: Utilizing milliunit storage (storing currency as integers) to eliminate rounding errors entirely.
- **Privacy and Speed**: An offline-first architecture ensures user data remains on the device, providing instant performance without requiring a constant internet connection.

---

## 2. Design of the App
The application follows a modern, clean aesthetic using **Material Design 3** and is structured to guide the user from setup to mastery.

### Screen-by-Screen Structure
- **Interactive Onboarding**: A 3-step guided walkthrough that introduces core concepts and helps users set their initial budget goals.
- **Secure Authentication**: A SHA-256 hashed login and registration system to protect user profiles.
- **Central Dashboard**: The command center of the app, featuring color-coded budget health indicators and visual spending breakdowns via **MPAndroidChart**.
- **Expense Management**: A robust list view (Requirement R10) with date-range filtering and swipe-to-delete actions (R12).
- **Receipt Digitization**: A specialized interface for capturing and viewing physical receipts using the device camera.
- **Category Customization**: Allows users to define their own spending categories with custom colors and budget limits.
- **Global Leaderboard**: A competitive ranking screen where users can see how their financial discipline compares to others.
- **Achievement Gallery**: A visual trophy room showcasing earned badges like "First Save", "7-Day Streak", and "Goal Crusher".

### UI Components and Decisions
- **Material Design 3**: Used for modern components like the Floating Action Button (FAB), Navigation Bar, and elevated Cards.
- **Dynamic Theming**: Fully optimized for both Light and Dark modes to ensure accessibility in all lighting conditions.
- **MVVM Architecture**: Ensures a separation of concerns, making the UI responsive and the data logic testable.


## 3. GitHub and GitHub Actions
The development of SaveSmart followed professional DevOps practices to ensure code integrity and collaborative efficiency.

### Version Control Strategy
We utilized **GitHub** as our primary repository hosting service. Our strategy included:
- **Atomic Commits**: Ensuring each commit represents a single logical change with a descriptive message.
- **Branch Management**: Using a stable `main` branch for releases and feature-specific development to prevent regressions.
- **Issue Tracking**: Documentation of requirements (R01-R23) were mapped to development milestones.

### CI/CD with GitHub Actions
We implemented a **GitHub Actions** workflow defined in `.github/workflows/android_ci.yml` to automate our quality assurance.

#### Workflow Configuration (`android_ci.yml`):
```yaml
name: Android CI

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4 # Downloads the code to the runner

      - name: Set up JDK 17
        uses: actions/setup-java@v4 # Configures the Java environment
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew # Ensures the build script can run

      - name: Build project
        run: ./gradlew build # Compiles the app and checks for syntax errors

      - name: Run unit tests
        run: ./gradlew test # Executes logic tests to ensure stability
```

**What this workflow does:**
1. **Triggers**: Runs automatically every time code is pushed to the `main` branch or a Pull Request is opened.
2. **Environment Setup**: Provisions a virtual Linux machine and installs Java 17.
3. **Build Validation**: Attempts to compile the entire project. If the code has errors, the "Build" step fails, alerting the team immediately.
4. **Automated Testing**: Runs all unit tests. This ensures that new features don't break existing functionality (Regression Testing).


## Technical Stack Summary
- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **Database**: Room Persistence Library
- **UI**: Material 3, View Binding, Jetpack Navigation
- **Asynchrony**: Coroutines
- **Analytics**: MPAndroidChart

## YouTube Demo
https://youtu.be/I4lUI_FgktQ?si=0H9ZXaBOh7aLXsCc

## Contributors
- **Olebogeng Phawe** (ST10345327)
- **Nkosikhona Dlamini** (ST10437200)
- **Mbuso Sbusiso Dube** (ST10449154)

**Institution**: IIE Rosebank International 
**Final Submission Date**: 15 June 2026
