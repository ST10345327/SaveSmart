# SaveSmart Testing Plan

**Project:** SaveSmart - Personal Budget Tracker  
**Student:** Olebogeng Phawe (ST10345327)  
**Course:** OPSC6311 Personal Budget Tracker POE  
**Test Strategy:** Test-Driven Development (TDD)

---

## Testing Overview

### Testing Layers

```
┌─────────────────────────────────────┐
│   UI Testing (Espresso)             │ Manual + Automated
├─────────────────────────────────────┤
│   ViewModel Testing (JUnit)         │ LiveData, state management
├─────────────────────────────────────┤
│   Repository Testing (Mockito)      │ Data layer
├─────────────────────────────────────┤
│   Database Testing (Room)           │ Entity & DAO tests
├─────────────────────────────────────┤
│   Utility Testing (JUnit)           │ SecurityUtils, CurrencyUtils
└─────────────────────────────────────┘
```

---

## Unit Tests

### 1. SecurityUtils Tests

**File:** `app/src/test/java/com/example/savesmart/util/SecurityUtilsTest.kt`

**Test Cases:**
```kotlin
// Password hashing
✓ testHashPassword_ValidInput_ReturnsHash()
✓ testHashPassword_EmptyString_ReturnsHash()
✓ testHashPassword_LongPassword_ReturnsHash()
✓ testHashPassword_SpecialChars_ReturnsHash()
✓ testHashPassword_Consistency_SameInputSameHash()
✓ testHashPassword_Security_DifferentHasheForDifferentInputs()
```

**Coverage Target:** 100%

---

### 2. CurrencyUtils Tests

**File:** `app/src/test/java/com/example/savesmart/util/CurrencyUtilsTest.kt`

**Test Cases:**
```kotlin
// Milliunit conversion (T10)
✓ testToMilliunits_ValidAmount_ReturnsCorrectMilliunits()
✓ testToMilliunits_ZeroAmount_ReturnsZero()
✓ testToMilliunits_LargeAmount_ReturnsCorrectValue()
✓ testToMilliunits_DecimalAmount_RoundsCorrectly()

// Reverse conversion
✓ testFromMilliunits_ValidMilliunits_ReturnsCorrectAmount()
✓ testFromMilliunits_ZeroMilliunits_ReturnsZero()
✓ testFromMilliunits_LargeValue_ReturnsCorrectAmount()
```

**Coverage Target:** 100%

**Example:**
```
Input: R1.00 = 1000L milliunits ✓
Input: R12.50 = 12500L milliunits ✓
Input: R0.01 = 10L milliunits ✓
```

---

### 3. AuthViewModel Tests

**File:** `app/src/test/java/com/example/savesmart/ui/auth/AuthViewModelTest.kt`

**Test Cases:**
```kotlin
// Login functionality (R02)
✓ testLogin_ValidCredentials_ReturnsSuccess()
✓ testLogin_InvalidCredentials_ReturnsError()
✓ testLogin_EmptyUsername_ReturnsError()
✓ testLogin_EmptyPassword_ReturnsError()
✓ testLogin_ObserversReceiveUpdates()

// Registration (R01)
✓ testRegister_NewUser_Success()
✓ testRegister_DuplicateUser_Fails()
✓ testRegister_WeakPassword_Error()
```

**Coverage Target:** 90%

---

### 4. Database DAO Tests

**File:** `app/src/androidTest/java/com/example/savesmart/data/dao/UserDaoTest.kt`

**Test Cases:**
```kotlin
// User DAO (R01, R02)
✓ testInsertUser_ValidUser_Success()
✓ testGetUserByUsername_ExistingUser_ReturnsUser()
✓ testGetUserByUsername_NonExistingUser_ReturnsNull()
✓ testUpdateUser_ValidUpdate_Success()
✓ testDeleteUser_ExistingUser_Deleted()

// Category DAO (R05, R06, R07)
✓ testInsertCategory_ValidCategory_Success()
✓ testGetCategoriesByUser_MultipleCats_ReturnsAll()
✓ testSoftDeleteCategory_MarksDeleted_NotReturned()
✓ testUpdateCategoryGoal_ValidGoal_Updated()

// Expense DAO (R08, R10, R12)
✓ testInsertExpense_ValidExpense_Success()
✓ testGetExpensesByDateRange_ValidRange_ReturnsExpenses()
✓ testGetExpensesByCategory_ValidCategory_ReturnsExpenses()
✓ testDeleteExpense_ExistingExpense_Deleted()

// Badge DAO (R20)
✓ testInsertBadge_ValidBadge_Success()
✓ testGetUserBadges_UserWithBadges_ReturnsAll()
```

**Coverage Target:** 95%

---

### 5. Repository Tests

**File:** `app/src/test/java/com/example/savesmart/data/repository/SaveSmartRepositoryTest.kt`

**Test Cases:**
```kotlin
// Login/Register (R01, R02)
✓ testLoginUser_ValidCredentials_ReturnsUser()
✓ testRegisterUser_NewUser_Success()
✓ testLoginUser_InvalidPassword_ReturnsNull()

// Data consistency
✓ testRepositoryIsSingleSourceOfTruth()
✓ testConcurrentOperations_ConsistentState()
```

**Coverage Target:** 85%

---

## Instrumented Tests (UI Tests)

### 1. LoginFragment UI Tests

**File:** `app/src/androidTest/java/com/example/savesmart/ui/auth/LoginFragmentTest.kt`

**Test Cases (Espresso):**
```kotlin
// UI Rendering (T06)
✓ testLoginScreen_DisplaysAllElements()
✓ testUsernameField_IsDisplayed()
✓ testPasswordField_IsDisplayed()
✓ testLoginButton_IsDisplayed()
✓ testRegisterLink_IsDisplayed()

// User Interactions (R03)
✓ testLoginButton_EmptyFields_ShowsError()
✓ testLoginButton_InvalidCredentials_ShowsError()
✓ testLoginButton_ValidCredentials_NavigatesToDashboard()
✓ testRegisterLink_ClickNavigatestoRegisterFragment()

// Input Validation (R03)
✓ testUsernameField_SpecialChars_Accepted()
✓ testPasswordField_SpecialChars_Accepted()
✓ testPasswordField_Masked_InputHidden()

// Error Messages
✓ testErrorMessage_Empty_DisplaysValidationError()
✓ testErrorMessage_Invalid_DisplaysLoginError()
✓ testErrorMessage_Clears_OnNewInput()
```

**Coverage Target:** 90%

---

### 2. Navigation Tests

**File:** `app/src/androidTest/java/com/example/savesmart/ui/nav/NavigationTest.kt`

**Test Cases:**
```kotlin
// Navigation Flow (Navigation Component)
✓ testLoginToRegisterNavigation_ClickRegisterLink()
✓ testLoginToDashboardNavigation_SuccessfulLogin()
✓ testRegisterToLoginNavigation_BackPress()
✓ testNavigationBackStack_ProperBehavior()

// Deep Linking
✓ testDeepLink_LoginFragment_Launches()
✓ testDeepLink_DashboardFragment_Launches()
```

**Coverage Target:** 85%

---

## Integration Tests

### 1. Authentication Flow

**File:** `app/src/androidTest/java/com/example/savesmart/ui/auth/AuthIntegrationTest.kt`

**Test Cases:**
```kotlin
// End-to-end flow (R01, R02)
✓ testCompleteLoginFlow_UserRegistersAndLogins()
✓ testSessionPersistence_LoginSaved()
✓ testSessionCleared_OnLogout()

// Data persistence (T02)
✓ testUserDataPersisted_InDatabase()
✓ testCategoryDataPersisted_WithUser()
```

**Coverage Target:** 85%

---

### 2. Database Integration

**File:** `app/src/androidTest/java/com/example/savesmart/data/db/DatabaseIntegrationTest.kt`

**Test Cases:**
```kotlin
// Full database workflow
✓ testUserRegistration_CreatesUserEntity()
✓ testCategoryCreation_LinkedToUser()
✓ testExpenseCreation_LinkedToCategory()
✓ testSoftDelete_DataNotReturned()
✓ testTransactions_AllOrNothing()
```

**Coverage Target:** 90%

---

## Manual Testing

### 1. User Acceptance Testing (UAT)

**Test Case: User Login**
```
Scenario: Valid Login
Given: App is running
When: User enters valid username and password
And: Clicks Login button
Then: User logged in
And: Session saved (SharedPreferences)
And: Navigated to Dashboard
```

**Test Case: Input Validation**
```
Scenario: Empty Fields
Given: Login screen displayed
When: User clicks Login without entering data
Then: Error messages shown
And: Not navigated

Scenario: Special Characters
Given: Login screen displayed
When: User enters special characters in fields
Then: Accepted without errors
```

**Test Case: Navigation**
```
Scenario: Register Link
Given: Login screen displayed
When: User clicks Register link
Then: Navigated to RegisterFragment

Scenario: Back Navigation
Given: RegisterFragment displayed
When: User presses Back
Then: Returns to LoginFragment
```

---

## Performance Testing

### 1. Database Performance

**Test:** Database query response time
```
✓ Login query: < 500ms
✓ Get expenses: < 1000ms (100 records)
✓ Get categories: < 100ms
✓ Insert expense: < 200ms
```

---

### 2. UI Performance

**Test:** Layout rendering time
```
✓ Fragment launch: < 1000ms
✓ List scroll: 60 FPS
✓ Navigation transition: < 300ms
```

---

## Security Testing

### 1. Data Validation Tests

**Test Cases:**
```kotlin
// Input sanitization (R03)
✓ testSQLInjection_Blocked()
✓ testXSSAttack_Blocked()
✓ testBufferOverflow_Handled()

// Password security (R01)
✓ testPasswordHashing_SHA256()
✓ testPasswordNeverStored_PlainText()
```

---

### 2. Data Protection Tests

**Test Cases:**
```kotlin
// Session security (R02)
✓ testSessionToken_Secure()
✓ testSessionTimeout_Implemented()
✓ testCredentialsNeverLogged()

// Database security (T02)
✓ testDatabaseEncryption_Enabled()
✓ testSoftDelete_Enforced()
```

---

## Test Execution Schedule

### Phase 1: Unit Tests (Apr 28)
```
SecurityUtils: 1-2 hours
CurrencyUtils: 1-2 hours
AuthViewModel: 2-3 hours
Total: 4-7 hours
```

---

### Phase 2: Database Tests (Apr 28)
```
DAO Tests: 2-3 hours
Repository Tests: 1-2 hours
Total: 3-5 hours
```

---

### Phase 3: UI Tests (Apr 29)
```
LoginFragment: 2-3 hours
Navigation: 1-2 hours
Total: 3-5 hours
```

---

### Phase 4: Integration Tests (Apr 29)
```
Auth Flow: 2-3 hours
Database Integration: 1-2 hours
Total: 3-5 hours
```

---

## Coverage Goals

| Component | Target | Actual |
|-----------|--------|--------|
| Utility Classes | 100% | ⏳ TBD |
| ViewModel | 90% | ⏳ TBD |
| Repository | 85% | ⏳ TBD |
| DAOs | 95% | ⏳ TBD |
| UI (Espresso) | 90% | ⏳ TBD |
| **Overall** | **80%** | ⏳ TBD |

---

## Testing Tools

### Unit Testing
```
✓ JUnit 4 (test framework)
✓ MockK or Mockito (mocking)
✓ Coroutine Test (async testing)
✓ Arch Core (LiveData testing)
```

### Instrumented Testing
```
✓ Espresso (UI testing)
✓ AndroidX Test (framework)
✓ Hamcrest (assertions)
✓ Room Testing (database)
```

### Code Coverage
```
✓ JaCoCo (coverage reporting)
```

---

## Test Execution

### Running Tests

```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

---

## Regression Testing

### Test Runs
```
✓ Before each commit
✓ On pull requests (GitHub Actions)
✓ Weekly full regression
✓ Pre-release testing
```

---

## Known Issues & Workarounds

| Issue | Impact | Workaround |
|-------|--------|-----------|
| Database migration testing | Medium | Use Room schema export |
| Async operation timing | Medium | Use TestDispatchers |
| UI animations | Low | Disable in tests |

---

## Sign-Off

**Testing Plan Status:** ✅ READY FOR IMPLEMENTATION  
**Next Step:** Begin unit tests during Phase 7  
**Last Updated:** April 14, 2026  
**Student:** Olebogeng Phawe (ST10345327)

---

**Target:** 80%+ code coverage  
**Deadline:** April 29, 2026

