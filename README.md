# SaveSmart

## Overview
SaveSmart is an offline-first Android budgeting application built with Kotlin and Room. It provides a secure and robust way for users to track spending, manage category-based budgets, and stay motivated through a built-in gamification system.

## Features
- **Expense Tracking**: Log daily expenses with precision using milliunit conversion.
- **Category Management**: Custom categories with budget goals and color customization.
- **Gamification**: Earn points, unlock achievement badges, and track levels.
- **Receipt Attachments**: Integrated camera support to capture and store receipt images.
- **Offline Storage**: Secure, local SQLite database using Room persistence.
- **Security**: SHA-256 password hashing and secure session management.

## Architecture
- **Language**: Kotlin
- **Pattern**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Database**: Room Persistence Library
- **UI**: Material Design 3, ViewBinding, Navigation Component

## Build Instructions
1. Clone the repository.
2. Open the project in Android Studio (Ladybug or newer).
3. Ensure JDK 17 is configured in Project Structure.
4. Sync Gradle and run on an emulator or device (Min SDK 26).

<<<<<<< HEAD
## Test Instructions
- Run unit tests: `./gradlew test`
- Build APK: `./gradlew assembleDebug`
=======
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
└── docs/                       (Project Workflow, Gantt Charts, PART 1 Research, PART 1 Planning & Design)
```

## 📋 Requirements Reference
 1
This project implements all requirements for the OPSC6311 POE:
- **R01-R23**: All functional features from Auth to Gamification.
- **T01-T10**: Technical standards including MVVM, Room, and Unit Testing.

## 🚦 Getting Started

1. Clone the repository: `git clone <repo-url>`
2. Open in **Android Studio 2024.1+**.
3. Ensure you have **JDK 17** configured.
4. Build and run on an emulator or device (API 26+).
5. Demo Video on YouTube: https://youtube.com/shorts/o-3LbOdyXBU

##  Presentation 

PowerPoint Presentation: https://advtechonline-my.sharepoint.com/:p:/g/personal/st10437200_rcconnect_edu_za/IQBn65wBWD1lQKlcxjtnw88JAQ-_tQDWjkcgiwGYIMDd6vU?e=qvCNB9 

---
**Author:** Olebogeng Phawe (ST10345327), Nkosikhona Dlamini (ST10437200), Mbuso Sbusiso Dube (ST10449154)  
**Course:** OPSC6311 Personal Budget Tracker  
**Institution:** IIE Rosebank College

>>>>>>> ce038ab2d766f20e06e2c9ab1a9ab065e6b9ee31
