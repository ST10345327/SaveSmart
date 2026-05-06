# SaveSmart - Personal Budget Tracker

**Smart Saving Made Simple**

SaveSmart is a robust, security-focused Android application designed to help users track their spending, manage budgets, and achieve financial goals through a gamified experience.

## 🚀 Key Features

### 🔐 Secure Authentication
- **SHA-256 Hashing**: User credentials are never stored in plain text.
- **Session Management**: Secure auto-login and session persistence.
- **Onboarding**: A guided 3-step setup flow for new users.

### 💰 Expense & Category Management
- **Dynamic Categories**: Create, edit, and customize categories with a professional color picker.
- **Precision Tracking**: Uses the **Milliunit Convention** (integer-based storage) for 100% financial accuracy.
- **Receipt Capture**: Integrated camera support to store photos of physical receipts.

### 📊 Insights & Analytics
- **Live Dashboard**: Real-time spending progress bars and budget status alerts.
- **Visual Reports**: Interactive Pie and Bar charts (using MPAndroidChart) for historical spending analysis.
- **Dynamic Goals**: Monthly budget goals that adapt based on your category settings.

### 🏆 Gamification
- **Points & Levels**: Earn points for consistent logging and level up.
- **Milestone Badges**: Unlock 7 unique badges for achieving savings milestones.
- **Leaderboard**: Compete with other users in global rankings.

## 🛠 Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Database**: Room Persistence Library (SQLite)
- **Networking**: Coroutines for background tasks
- **UI**: Material Design 3, ViewBinding, Navigation Component
- **Charts**: MPAndroidChart
- **CI/CD**: GitHub Actions

## 🏗 Project Structure

```
SaveSmart/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/savesmart/
│   │   │   │   ├── data/       (Entities, DAOs, Database, Repository)
│   │   │   │   ├── ui/         (Fragments & ViewModels per feature)
│   │   │   │   └── util/       (Security, Currency, Session utilities)
│   │   │   └── res/            (Layouts, Themes, Navigation Graph)
│   │   └── test/               (Unit Tests for core logic)
└── docs/                       (Project Workflow & Gantt Charts)
```

## 📋 Requirements Reference

This project implements all requirements for the OPSC6311 POE:
- **R01-R23**: All functional features from Auth to Gamification.
- **T01-T10**: Technical standards including MVVM, Room, and Unit Testing.

## 🚦 Getting Started

1. Clone the repository: `git clone <repo-url>`
2. Open in **Android Studio 2024.1+**.
3. Ensure you have **JDK 17** configured.
4. Build and run on an emulator or device (API 26+).
5. Demo Video on Youtube: 

---
**Author:** Olebogeng Phawe (ST10345327)  
**Course:** OPSC6311 Personal Budget Tracker  
**Institution:** IIE Rosebank College
