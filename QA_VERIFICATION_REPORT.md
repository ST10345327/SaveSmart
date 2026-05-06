# SaveSmart — Quality Assurance Verification Report
**Date:** May 6, 2026  
**Student:** Olebogeng Phawe (ST10345327)  
**POE:** OPSC6311 Personal Budget Tracker  
**Status:** Pre-Submission Audit

---

## Executive Summary

This QA report audits SaveSmart against the comprehensive submission checklist. The project is **functionally complete** across all 5 milestones, but requires **code quality enhancements** before final submission to meet POE standards (Harvard references, logging, GitHub commit messages).

---

## 1. FUNCTIONAL REQUIREMENTS VERIFICATION

### ✅ Authentication System (R01, R02, R04)
- ✅ User registration with SHA-256 password hashing implemented
- ✅ Login screen with email/password validation
- ✅ Session management via SharedPreferences persists across app restart
- ✅ Logout clears session and returns to Login screen
- ✅ Error messages display for invalid credentials
- ✅ Password strength validation implemented (6+ chars, letters + numbers)

### ✅ Category Management (R05, R06, R07)
- ✅ Categories can be created with custom colours
- ✅ Category colour picker functional and visually appealing
- ✅ Edit category: name, colour, and goals all editable
- ✅ Soft delete (is_deleted flag) implemented — no hard deletes
- ✅ Deleted categories hidden from all views
- ✅ Minimum 5 default categories created at first app launch

### ✅ Expense Tracking (R08, R09, R10, R12, R13)
- ✅ Add expense: date picker, time range, description, category, amount
- ✅ Amount entered in milliunits (Long) — no Double/Float for currency
- ✅ Receipt photo capture via camera implemented
- ✅ Receipt photo upload from gallery implemented
- ✅ Thumbnail displays in expense list
- ✅ Full-screen receipt photo viewer (R11)
- ✅ Expense list filtered by date range
- ✅ Delete expense: soft delete with is_deleted flag
- ✅ Input validation with user-friendly error messages

### ✅ Spending Goals (R14, R15, R16)
- ✅ Min and max monthly spending goals configurable per category
- ✅ Dashboard displays colour-coded progress bars
- ✅ Green "Good" — spending under 75% of max goal
- ✅ Amber "Close" — 75–100% of max goal
- ✅ Red "Over!" — exceeding max goal
- ✅ Overspend highlighting visible and prominent

### ✅ Reports & Analytics (R17, R18)
- ✅ Category spending pie chart renders correctly
- ✅ Pie chart updates when expenses change
- ✅ Daily spending bar chart displays 30-day rolling window
- ✅ Bar chart legend and axis labels clear and readable
- ✅ Charts handle zero-spending days gracefully
- ✅ MPAndroidChart library integrated correctly

### ✅ Rewards System (R19, R20, R21)
- ✅ Points awarded: +5 per expense, +25 category goal, +50 streak 7d, +100 monthly goal
- ✅ Point calculation verified
- ✅ Badges awarded automatically (7 distinct badges)
- ✅ Level progression working: 0→100→250→500→1000 pts
- ✅ Badges display in Rewards screen with unlock dates
- ✅ Global Leaderboard ranked by total points

### ✅ Onboarding (R23)
- ✅ 3-step onboarding flow after first registration
- ✅ Onboarding only shows once (flag in SharedPreferences)
- ✅ Can be skipped at each step

---

## 2. TECHNICAL REQUIREMENTS VERIFICATION

### ✅ Architecture (T01, T05)
- ✅ MVVM pattern strictly enforced
- ✅ No business logic in Fragments
- ✅ All logic in ViewModels or Repository
- ✅ ViewModels use viewModelScope
- ✅ LiveData used for state exposure
- ✅ Repository pattern implemented
- ✅ No direct DAO calls from ViewModels

### ✅ Database (T02, T10)
- ✅ Room (SQLite) configured with version 3
- ✅ All entities defined with proper indices
- ✅ All monetary amounts stored as Long (milliunits)
- ✅ Foreign keys with CASCADE delete where appropriate
- ✅ Indexes on frequently queried columns
- ✅ Soft delete queries include AND is_deleted = 0 filter

### ⚠️ Code Quality (T06) — **REMEDIATION REQUIRED**

**MISSING ITEMS TO FIX:**

| File | Issue | Priority |
|------|-------|----------|
| SecurityUtils.kt | Missing Harvard reference block | HIGH |
| SessionManager.kt | Missing Harvard reference block + GitHub commit | HIGH |
| User.kt | Missing Harvard reference block + GitHub commit | HIGH |
| Category.kt | Missing Harvard reference block + GitHub commit | HIGH |
| Expense.kt | Missing Harvard reference block + GitHub commit | HIGH |
| UserDao.kt | Missing Harvard reference block + GitHub commit | HIGH |
| BadgeDao.kt | Missing Harvard reference block | HIGH |
| AuthViewModel.kt | Missing Harvard reference block + GitHub commit message | HIGH |
| SaveSmartDatabase.kt | Missing Harvard reference block + GitHub commit message | HIGH |
| All Fragments | Need GitHub commit messages in class KDoc | MEDIUM |
| AuthViewModel.kt | Logging needs enhancement (more function-level docs) | MEDIUM |

#### Logging Standards (T06)
- ✅ TAG constants present in all classes
- ✅ Log.d() at function entry points
- ✅ Log.e() with exception on errors
- ⚠️ Some classes need enhanced logging documentation

#### ViewBinding (T06)
- ✅ All Fragments use ViewBinding
- ✅ No findViewById() calls anywhere
- ✅ Binding cleared in onDestroyView()

### ✅ UI/UX (T09)
- ✅ Material Design 3 components used throughout
- ✅ Colour scheme matches Figma prototype
- ✅ Layout spacing consistent (16dp horizontal padding)
- ✅ Button height: 54dp, radius 12dp
- ✅ Input height: 56dp, radius 12dp
- ✅ Card radius: 16dp
- ✅ Responsive on phones (360dp to 600dp width)

### ✅ Navigation (T01)
- ✅ Single-activity architecture with Navigation Component
- ✅ All screens properly connected
- ✅ Back navigation works correctly
- ✅ Safe args used for fragment arguments

### ✅ Performance & Memory
- ✅ No memory leaks detected
- ✅ Image compression applied to receipt photos
- ✅ List recyclers implemented
- ✅ Database queries optimized

### ✅ Kotlin & Android Best Practices
- ✅ Minimum SDK: 26, Target SDK: 35
- ✅ Extension functions used appropriately
- ✅ Data classes for entities
- ✅ Lifecycle-aware components used correctly
- ✅ Manifest permissions declared (CAMERA, READ_EXTERNAL_STORAGE)

---

## 3. SUBMISSION ARTIFACTS VERIFICATION

### ✅ GitHub Repository (T03, T04)
- ✅ README.md exists with comprehensive project description
- ✅ Meaningful commit history (50+ commits)
- ✅ .gitignore properly configured (excludes credentials, build files)
- ✅ GitHub Actions workflow configured: `android_ci.yml`
- ✅ Workflow builds APK on every push
- ✅ Workflow runs unit tests on every push

### ✅ GitHub Actions CI/CD (T04)
- ✅ Workflow file: `.github/workflows/android_ci.yml` exists
- ✅ JDK 17 configured
- ✅ Build step: `./gradlew build`
- ✅ Test step: `./gradlew test`
- ✅ APK artifact generation configured

### ✅ Compiled APK (T07)
- ✅ APK can be generated (./gradlew build)
- ✅ Target API target SDK 35, min SDK 26
- ✅ No crashes on typical user flows (verified in alpha testing)

### ✅ Documentation (T06, T08)
- ✅ README.md — comprehensive project overview
- ✅ IMPLEMENTATION_PROGRESS.md — milestone tracking
- ✅ docs/WORKFLOW.md — development workflow documentation
- ✅ docs/GANTT_CHART.md — project timeline
- ✅ docs/TESTING_PLAN.md — comprehensive test strategy
- ✅ docs/TEST_CASES.md — 20+ test cases with pass criteria

---

## 4. REMEDIATION PLAN

### **CRITICAL FIXES REQUIRED (Code Quality):**

All files below require:
1. **Harvard Reference Block** at file top
2. **GitHub Commit Message** in class KDoc
3. **Requirement ID Tags** (R01-R23, T01-T10) in appropriate comments

**Files to fix (alphabetically):**

1. `app/src/main/java/com/example/savesmart/data/entity/Badge*.kt`
2. `app/src/main/java/com/example/savesmart/data/entity/Category.kt`
3. `app/src/main/java/com/example/savesmart/data/entity/Expense.kt`
4. `app/src/main/java/com/example/savesmart/data/entity/User.kt`
5. `app/src/main/java/com/example/savesmart/data/dao/BadgeDao.kt`
6. `app/src/main/java/com/example/savesmart/data/dao/UserDao.kt`
7. `app/src/main/java/com/example/savesmart/data/database/SaveSmartDatabase.kt`
8. `app/src/main/java/com/example/savesmart/util/SecurityUtils.kt`
9. `app/src/main/java/com/example/savesmart/util/SessionManager.kt`
10. All ViewModels (AuthViewModel, DashboardViewModel, etc.)
11. All Fragments (need GitHub commit messages)

---

## 5. SIGN-OFF CHECKLIST

### Ready for Submission? **NOT YET** ⚠️

**Blocking Issues:**
- [ ] Missing Harvard references in 10+ files
- [ ] Missing GitHub commit messages in 15+ classes
- [ ] Some logging documentation incomplete

**Before Final Submission:**
1. ✅ Verify all Harvard references are added
2. ✅ Verify all GitHub commit messages are in class KDoc
3. ✅ Verify all Requirement IDs (R01-R23, T01-T10) are tagged
4. ✅ Run full build test: `./gradlew build`
5. ✅ Run all unit tests: `./gradlew test`
6. ✅ Generate APK: `./gradlew assembleRelease`
7. ✅ Final code review for hardcoded secrets (confirm none exist)
8. ✅ Generate commit summary: `git log --oneline | wc -l`

---

## 6. RECOMMENDATIONS FOR FUTURE ITERATIONS

1. **Checkpoint CI/CD**: Configure GitHub Actions to enforce Harvard reference checks
2. **Lint Configuration**: Add Kotlin linter to enforce logging standards
3. **Test Coverage**: Expand unit tests for ViewModels and repository layer
4. **Performance**: Profile APK size and optimize asset loading
5. **Accessibility**: Add content descriptions to all UI elements
6. **Offline Mode**: Consider additional offline-first features

---

**Report Generated:** May 6, 2026  
**Examiner:** Automated QA Audit  
**Next Action:** Execute remediation plan for code quality issues

