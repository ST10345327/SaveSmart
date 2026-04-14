# SaveSmart Development Workflow

**Project:** SaveSmart - Personal Budget Tracker  
**Student:** Olebogeng Phawe (ST10345327)  
**Course:** OPSC6311 Personal Budget Tracker POE  
**College:** IIE Varsity College  
**Date:** April 14, 2026

---

## Development Process

### Phase 1: Project Setup ✅

**Duration:** April 11-14, 2026

**Activities:**
1. Project initialization with Gradle
2. Dependency configuration
3. Android SDK setup (min SDK 26)
4. Build system configuration

**Deliverables:**
- ✅ build.gradle.kts (root and app-level)
- ✅ AndroidManifest.xml
- ✅ Gradle wrapper files
- ✅ Project structure

**Code Commits:**
- Initial project setup
- Gradle configuration

---

### Phase 2: Database Layer Implementation ✅

**Duration:** April 11-13, 2026

**Activities:**
1. Room entity design (User, Category, Expense, Badge, UserBadge)
2. DAO (Data Access Object) implementation
3. Database class creation
4. Repository pattern setup
5. Utility classes (SecurityUtils, CurrencyUtils, SessionManager)

**Deliverables:**
- ✅ 5 Room entities with proper annotations
- ✅ 5 DAOs with CRUD operations
- ✅ SaveSmartDatabase class
- ✅ SaveSmartRepository (single source of truth)
- ✅ SecurityUtils.kt (SHA-256 password hashing)
- ✅ CurrencyUtils.kt (milliunit conversion)
- ✅ SessionManager.kt (session persistence)

**Testing:**
- Entity validation (proper column types, constraints)
- DAO method signatures verified
- Repository method accessibility checked

**Code Commit:**
```
[db] Add complete database layer — entities, DAOs, repository and utilities
- Define 5 Room entities with proper relationships
- Implement CRUD operations in all DAOs
- Setup SaveSmartRepository as single source of truth
- Add SecurityUtils with SHA-256 hashing (R01)
- Add CurrencyUtils for milliunit conversion (T10)
- Add SessionManager for session persistence (R02)
Refs: R01, R02, T02, T10
```

---

### Phase 3: Authentication UI Implementation ✅

**Duration:** April 13-14, 2026

**Activities:**
1. LoginFragment creation with MVVM
2. AuthViewModel implementation
3. Navigation graph setup
4. Input validation logic
5. Session management integration
6. Error handling

**Challenges Resolved:**
- ✅ Fixed 12 LoginFragment binding errors
- ✅ Created missing layout file (fragment_login.xml)
- ✅ Generated all required resource files
- ✅ Implemented proper navigation actions

**Deliverables:**
- ✅ LoginFragment.kt (MVVM with ViewBinding)
- ✅ AuthViewModel.kt (authentication logic)
- ✅ fragment_login.xml (Material Design 3)
- ✅ nav_graph.xml (navigation configuration)
- ✅ Input validation with error messages (R03)
- ✅ Session management after login (R02)

**Code Quality:**
- ✅ Harvard references on all files
- ✅ Logging statements (Log.d, Log.w, Log.e)
- ✅ Requirement ID references (R01, R02, R03)
- ✅ GitHub commit suggestions in KDoc

**Code Commit:**
```
[auth] Fix LoginFragment — resolve all 12 binding and resource errors
- Create fragment_login.xml layout with Material Design 3 inputs
- Generate nav_graph.xml with login→register and login→dashboard actions
- Create AuthViewModel with secure login logic and LiveData state
- Add all missing string resources (strings.xml) and color definitions
- Enable ViewBinding in build.gradle for type-safe view access
- Add comprehensive logging and error handling throughout
- Create placeholder RegisterFragment and DashboardFragment
Refs: R02, R03, T01, T06
```

---

### Phase 4: Design System Implementation ✅

**Duration:** April 13-14, 2026

**Activities:**
1. Material Design 3 color implementation
2. Button and input styling
3. Theme application
4. Responsive layout design

**Design Tokens:**
```
Primary Blue:      #1A6FE8 (actions)
Good Green:        #16A34A (under budget)
Close Amber:       #F59E0B (near limit)
Over Red:          #DC2626 (over budget)
Rewards Purple:    #7C3AED (achievements)
Background:        #F5F5F7
Card Surface:      #FFFFFF
Border:            #E5E7EB
Text Primary:      #1C1C23
Text Secondary:    #6B7280

Button Height:     54dp, radius 12dp
Input Height:      56dp, radius 12dp
Screen Padding:    16dp
```

**Deliverables:**
- ✅ colors.xml (Material Design 3 palette)
- ✅ bg_button_primary.xml (button styling)
- ✅ bg_input.xml (input styling)
- ✅ themes.xml (app themes)

---

### Phase 5: Security & CI/CD Setup ✅

**Duration:** April 14, 2026

**Activities:**
1. GitHub Actions CI workflow
2. Git security configuration
3. Sensitive data protection
4. Build automation

**Deliverables:**
- ✅ .github/workflows/android_ci.yml (CI pipeline)
- ✅ Enhanced .gitignore (credential blocking)
- ✅ SSH key generation (local security)
- ✅ Removed sensitive documentation

**CI/CD Pipeline:**
```
Trigger: Push to main branch
Steps:
  1. Checkout repository
  2. Setup JDK 17
  3. Grant execute permission
  4. Build project
  5. Run unit tests
```

**Code Commit:**
```
ci: add GitHub Actions workflow for automated builds
- Setup Android CI/CD pipeline
- Build on every push to main
- Run unit tests automatically
- Configure JDK 17 and Gradle
Refs: T03, T04
```

---

## Milestones Achieved

| Milestone | Status | Date |
|-----------|--------|------|
| Project Setup | ✅ Complete | Apr 11 |
| Database Layer | ✅ Complete | Apr 13 |
| Authentication UI | ✅ Complete | Apr 14 |
| Design System | ✅ Complete | Apr 14 |
| CI/CD Pipeline | ✅ Complete | Apr 14 |
| Security Hardening | ✅ Complete | Apr 14 |

---

## Technical Architecture

### MVVM Pattern
```
Fragments ← ViewModel ← Repository ← DAO ← Database
  (UI)      (Logic)    (Data)      (Access)
```

### Key Technologies
- **Language:** Kotlin
- **Architecture:** MVVM + Repository
- **Database:** Room (SQLite)
- **UI:** Material Design 3 + ViewBinding
- **Navigation:** Navigation Component
- **Async:** Coroutines
- **Testing:** JUnit + Espresso (ready)
- **CI/CD:** GitHub Actions

---

## Code Quality Standards Applied

### Every Kotlin File Includes:
✅ Harvard referencing (APA format)  
✅ Logging statements (Log.d, Log.w, Log.e)  
✅ GitHub commit suggestions in KDoc  
✅ Requirement ID references (R01-R23, T01-T10)  

### MVVM Pattern Enforcement:
✅ Fragments handle UI only (no business logic)  
✅ ViewModels contain all logic (no Context)  
✅ Repository is single source of truth  
✅ LiveData for reactive state  
✅ ViewBinding for type-safe views  

### Input Validation (R03):
✅ Empty field checks  
✅ Length constraints  
✅ Special character handling  
✅ Clear error messages  

### Security (R01, R02):
✅ SHA-256 password hashing  
✅ Session management with SessionManager  
✅ No credentials in code  
✅ Enhanced .gitignore  

---

## Next Phase Planning

### Phase 6: Categories Management
**Activities:**
- Add category CRUD operations
- Implement category colors
- Add budget goal setting
- Create category list UI

**Estimated Duration:** 2-3 days

---

### Phase 7: Expense Tracking
**Activities:**
- Implement expense entry UI
- Add receipt photo capture
- Implement expense filtering
- Create expense list with categories

**Estimated Duration:** 3-4 days

---

### Phase 8: Reports & Analytics
**Activities:**
- Create dashboard with widgets
- Implement pie charts (category spending)
- Implement bar charts (daily spending)
- Add spending reports

**Estimated Duration:** 3-4 days

---

### Phase 9: Rewards System
**Activities:**
- Implement points system
- Create badges logic
- Build leaderboard
- Add achievement tracking

**Estimated Duration:** 2-3 days

---

### Phase 10: Testing & Deployment
**Activities:**
- Write unit tests (minimum 80% coverage)
- Write instrumented tests
- Final UI testing
- Generate APK
- Deploy to GitHub Releases

**Estimated Duration:** 2-3 days

---

## Risk Management

### Identified Risks:
1. **Database migrations** - Mitigation: Early planning and testing
2. **Navigation complexity** - Mitigation: Navigation Component (handled)
3. **Performance with large datasets** - Mitigation: Pagination and indexing
4. **Testing coverage** - Mitigation: Test-driven development

### Mitigation Strategies:
- ✅ Regular commits (easy rollback)
- ✅ GitHub Actions CI (catch issues early)
- ✅ Code review practices
- ✅ Documentation-first approach

---

## Requirements Traceability

### Functional Requirements (R01-R23)

**Phase 1 Coverage:**
- ✅ R01: User registration (framework ready)
- ✅ R02: User login (implemented)
- ✅ R03: Input validation (implemented)
- ⏳ R04-R23: Phases 2-5

### Technical Requirements (T01-T10)

**Phase 1 Coverage:**
- ✅ T01: MVVM pattern (implemented)
- ✅ T02: Room database (implemented)
- ✅ T03: GitHub repo (configured)
- ✅ T06: ViewBinding (enabled)
- ✅ T10: Milliunit convention (ready)
- ⏳ T04, T05, T07-T09: Phases 2-5

---

## Lessons Learned

1. **Early Architecture:** Solid MVVM setup from start saves refactoring
2. **ViewBinding:** Eliminates many binding errors upfront
3. **Resource Planning:** Central colors and strings prevent duplication
4. **Documentation:** Continuous documentation helps POE evaluation
5. **Git Discipline:** Frequent commits with clear messages aid review

---

## Conclusion

Phase 1 successfully established:
- ✅ Solid database foundation
- ✅ Secure authentication framework
- ✅ Professional UI with Material Design 3
- ✅ CI/CD automation
- ✅ Code quality standards

**Status:** Phase 1 Complete ✅  
**Next Step:** Phase 2 - Categories Management  
**Timeline:** On schedule for POE submission

---

**Date:** April 14, 2026  
**Student:** Olebogeng Phawe (ST10345327)  
**Status:** ✅ APPROVED FOR PHASE 2

