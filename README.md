# SaveSmart: Advanced Personal Budgeting and Gamified Finance

## Overview
SaveSmart is an offline-first Android application designed for personal financial management. Built with Kotlin and modern Android architecture, the application enables users to track spending through precision logging, granular budgeting, and a gamification engine designed to encourage financial discipline.

This project was developed for the OPSC6311 Portfolio of Evidence (POE), focusing on bridging the gap between standard financial tools and user engagement.

---

## Feature Set and Research Alignment

The development process was guided by research into market-leading applications such as YNAB, Wallet, and Money Manager. This resulted in a two-tier feature strategy:

### 1. Features Inspired by Research
These features were identified as industry standards during research and implemented with custom adaptations for SaveSmart:
- **Precision Currency Handling**: Utilizes milliunit storage (inspired by the YNAB API) to eliminate floating-point rounding errors.
- **Interactive Onboarding**: A guided walkthrough (inspired by YNAB) to reduce the initial learning curve.
- **Receipt Digitization**: Integrated camera functionality (inspired by Money Manager) for physical record-keeping.
- **Offline-First Architecture**: Robust Room database implementation (inspired by Money Manager) for data privacy and local performance.
- **Color-Coded Dashboard**: Visual status indicators (inspired by Wallet and YNAB) for instant budget health assessment.

### 2. Original Innovations
These features represent the unique value proposition of SaveSmart and were developed to address the engagement gap found in current market solutions:
- **Unified Gamification Engine**: A comprehensive system combining points, badges, and levels into a core feedback loop.
- **Global Leaderboard (Requirement R22)**: An original competitive feature that ranks registered users by points earned, fostering community and accountability.
- **Level Progression System (Requirement R21)**: A dynamic leveling mechanic (1000 points per level) with visual progress tracking.
- **Achievement Gallery**: A specialized badge system that rewards positive financial behaviors such as consistent logging and budget adherence.
- **Advanced Expense Management (Requirement R12)**: A custom interface for the modification and deletion of historical data.

---

## Technical Architecture and Stack

- **Language**: 100% Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) with the Repository Pattern.
- **Database**: Room Persistence Library with foreign-key integrity and optimized queries for gamification logic.
- **UI and UX**: Material Design 3, View Binding, and Jetpack Navigation.
- **Asynchrony**: Kotlin Coroutines for responsive background processing.
- **Analytics**: MPAndroidChart for visual spending breakdowns.

---

## Dark Mode and Accessibility
A technical focus was placed on accessibility and visual consistency across system themes:
- **Dark Mode Optimization**: Resolved visibility issues via a dedicated night-mode color palette.
- **Onboarding Indicator Fix**: Implemented custom logic for the page indicator using TabLayoutMediator to ensure accurate synchronization.
- **High-Contrast Indicators**: Utilized theme-aware status colors to ensure readability in all environments.

---

## Project Structure
- `data/`: Room entities, DAOs, and the SaveSmartRepository.
- `ui/`: Feature-specific Fragments, Adapters, and ViewModels.
- `util/`: Session management, SHA-256 security implementation, and currency formatting.
- `docs/`: Research, planning, and design documentation.

---

## Setup and Installation
1. Clone the repository.
2. Open the project in Android Studio (Ladybug or later).
3. Perform a Gradle Sync to resolve dependencies.
4. Deploy to an emulator or physical device (Minimum SDK: API 26).

---

## Contributors
- **Olebogeng Phawe** (ST10345327)
- **Nkosikhona Dlamini** (ST10437200)
- **Mbuso Sbusiso Dube** (ST10449154)

**Institution**: IIE Rosebank International 
**Final Submission**: 15 June 2026
