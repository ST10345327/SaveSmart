# SaveSmart: Personal Budgeting Application

## Overview
SaveSmart is an offline-first Android application designed for personal financial management. Developed using Kotlin and the Room Persistence Library, the application provides a secure environment for tracking expenditures, managing category-specific budget goals, and enhancing user engagement through a structured gamification system.

## Key Features
- **Expenditure Tracking**: Comprehensive logging of daily expenses with high-precision currency handling.
- **Categorization**: Customizable spending categories with individual budget limits and visual identifiers.
- **Gamification Framework**: System-managed point allocation, achievement badges, and progression levels to incentivise financial discipline.
- **Digital Receipt Management**: Integrated camera functionality for the digitisation and storage of physical receipts.
- **Data Persistence**: Robust offline data management using a local SQLite database with Room ORM.
- **Security Protocols**: Implementation of SHA-256 hashing for credential security and managed session states.

## Technical Architecture
- **Programming Language**: Kotlin
- **Architectural Pattern**: MVVM (Model-View-ViewModel) supported by the Repository Pattern.
- **Data Layer**: Room Persistence Library (SQLite).
- **User Interface**: Material Design 3, View Binding, and Jetpack Navigation Component.

## Project Structure
- `app/src/main/java/.../data/`: Data entities, DAOs, and Room database configuration.
- `app/src/main/java/.../ui/`: Feature-specific Fragments and ViewModels.
- `app/src/main/java/.../util/`: Security, session management, and currency utilities.
- `docs/`: Supplemental research, planning documentation, and project workflow charts.

## Requirements Compliance
This implementation adheres to the OPSC6311 Portfolio of Evidence (POE) specifications:
- **Functional Requirements (R01-R23)**: Full implementation of features ranging from Authentication to Gamification.
- **Technical Requirements (T01-T10)**: Adherence to architectural standards, including MVVM, Room Database implementation, and Unit Testing.

## Setup and Installation
1. Clone the repository to your local environment.
2. Open the project in Android Studio (Version 2024.1 or later).
3. Verify the project is configured to use JDK 17.
4. Synchronise the Gradle files.
5. Deploy the application to an Android Emulator or physical device (Minimum SDK: API 26).

## Contributors
- **Olebogeng Phawe** (ST10345327)
- **Nkosikhona Dlamini** (ST10437200)
- **Mbuso Sbusiso Dube** (ST10449154)

**Course**: OPSC6311 Personal Budget Tracker  
**Institution**: IIE Rosebank College
