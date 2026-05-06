# SaveSmart — Manual Test Procedures (Pre-Submission)
**Date:** May 6, 2026  
**Student:** Olebogeng Phawe (ST10345327)  
**POE:** OPSC6311 Personal Budget Tracker

---

## 📋 TEST EXECUTION GUIDE

This document provides step-by-step manual test cases to verify all functionality before APK submission.  
**Total Test Cases:** 40+ scenarios covering all functional requirements (R01-R23).

---

## 🔐 GROUP 1: AUTHENTICATION TESTS (R01, R02, R03, R04)

### TC-001: Register with Valid Credentials
**Requirement:** R01, R03  
**Status:** [ ] PASS | [ ] FAIL  

1. Launch app (first time)
2. Click "Create Account" button
3. Enter Username: `testuser001` (alphanumeric, no spaces)
4. Enter Password: `SecurePass123` (6+ chars, letters + numbers)
5. Enter Confirm Password: `SecurePass123` (match)
6. Click "Register" button
7. **Expected:** Toast "Registration successful!" → Navigate to Onboarding Step 1

**Validation:**
- [ ] Username accepted (3-20 chars, alphanumeric + underscore)
- [ ] Password is hashed (not visible in database as plaintext)
- [ ] User can proceed without errors

---

### TC-002: Register with Invalid Username (Too Short)
**Requirement:** R03  
**Status:** [ ] PASS | [ ] FAIL  

1. Click "Create Account"
2. Username: `ab` (only 2 chars)
3. Password: `ValidPass123`
4. Click "Register"
5. **Expected:** Error: "Username must be 3-20 characters..."

**Validation:**
- [ ] Form does not submit
- [ ] Error message displayed on username field

---

### TC-003: Register with Weak Password
**Requirement:** R01, R03  
**Status:** [ ] PASS | [ ] FAIL  

1. Click "Create Account"
2. Username: `testuser002`
3. Password: `12345` (numbers only, no letters)
4. Click "Register"
5. **Expected:** Error: "Password must be at least 6 characters with letters and numbers"

**Validation:**
- [ ] Form rejected
- [ ] Error message displayed
- [ ] Account not created

---

### TC-004: Register – Duplicate Username Prevention
**Requirement:** R01, R03  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User `testuser001` already registered (from TC-001)

1. Click "Create Account"
2. Username: `testuser001` (same as existing user)
3. Password: `NewPass123`
4. Click "Register"
5. **Expected:** Error: "Username already taken"

**Validation:**
- [ ] Form rejected before any database operation
- [ ] User cannot create duplicate account

---

### TC-005: Login with Valid Credentials
**Requirement:** R02  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User `testuser001` registered (from TC-001)

1. On Login screen
2. Username: `testuser001`
3. Password: `SecurePass123`
4. Click "Login" button
5. **Expected:** Navigate to Dashboard (or Onboarding if first login)

**Validation:**
- [ ] Credentials accepted
- [ ] Session saved (SharedPreferences)
- [ ] Can log in on next app restart

---

### TC-006: Login with Invalid Credentials
**Requirement:** R02, R03  
**Status:** [ ] PASS | [ ] FAIL  

1. Login screen
2. Username: `testuser001`
3. Password: `WrongPassword` (incorrect)
4. Click "Login"
5. **Expected:** Toast or alert: "Invalid username or password"

**Validation:**
- [ ] Login rejected
- [ ] No session created
- [ ] User remains on login screen

---

### TC-007: Session Persistence (Auto-Login)
**Requirement:** R02  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User logged in (from TC-005)

1. App is running with user logged in
2. Kill the app (swipe from recents or force close)
3. Restart app
4. **Expected:** Dashboard appears immediately (auto-login)

**Validation:**
- [ ] No login screen shown
- [ ] User ID persisted in SharedPreferences
- [ ] Session valid across restarts

---

### TC-008: Logout – Session Clear
**Requirement:** R04  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User logged in (from TC-007)

1. On Dashboard
2. Click "Logout" button
3. Confirm logout if prompted
4. **Expected:** Return to Login screen

**Validation:**
- [ ] SharedPreferences cleared
- [ ] Login screen appears with empty fields
- [ ] Pressing back does NOT return to Dashboard (back stack cleared)

---

## 📊 GROUP 2: CATEGORY MANAGEMENT TESTS (R05, R06, R07)

### TC-009: Create New Category with Custom Colour
**Requirement:** R05  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User logged in

1. Navigate to Categories screen
2. Click "+" button to add category
3. Category Name: `Groceries`
4. Click colour picker → Select blue (#1A6FE8)
5. Min Budget: 0 | Max Budget: R5,000
6. Click "Save Category"
7. **Expected:** Category appears in list with blue colour

**Validation:**
- [ ] Category created with custom colour
- [ ] Colour persists after closing/reopening
- [ ] All categories listed on screen

---

### TC-010: Edit Category (Name + Colour + Goals)
**Requirement:** R06  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** "Groceries" category exists (from TC-009)

1. Categories screen
2. Tap "Groceries" category card
3. Change Name: `Groceries & Household`
4. Change Colour to red (#DC2626)
5. Change Max Budget: R6,000
6. Click "Save Changes"
7. **Expected:** Category updated in list

**Validation:**
- [ ] Name changed immediately
- [ ] Colour reflects new red (#DC2626)
- [ ] Budget goals updated
- [ ] Changes persistent

---

### TC-011: Delete Category (Soft Delete)
**Requirement:** R07  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** "Groceries & Household" category exists

1. Categories screen
2. Long-press (or swipe) on "Groceries & Household"
3. Tap "Delete" option
4. Confirm deletion
5. **Expected:** Category removed from visible list

**Validation:**
- [ ] Category no longer appears in Categories screen
- [ ] Category hidden from Expense dropdowns
- [ ] BUT: Expenses under this category still visible in reports (historical data preserved)

---

### TC-012: Default Categories on First Launch
**Requirement:** R05  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Fresh app install, new user

1. Register new user
2. Complete Onboarding Step 2
3. Navigate to Categories screen
4. **Expected:** Minimum 5 default categories visible (e.g., Food, Transport, Entertainment, etc.)

**Validation:**
- [ ] Categories appear automatically
- [ ] Each has a distinct colour
- [ ] User can modify or delete them

---

## 💰 GROUP 3: EXPENSE TRACKING TESTS (R08, R09, R10, R12, R13)

### TC-013: Add Single Expense (Complete)
**Requirement:** R08, R13  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User logged in with at least 1 category

1. Dashboard → Click "+" FAB
2. Amount: `125.50`
3. Category: `Groceries`
4. Date: Today (using date picker)
5. Time Start: 10:00 AM
6. Time End: 10:30 AM
7. Description: `Weekly shopping at Pick n Pay`
8. Click "Save Expense"
9. **Expected:** Toast "Expense saved!" → Return to Dashboard

**Validation:**
- [ ] Expense appears in Expense List with R125.50
- [ ] Category correctly assigned
- [ ] Date/time preserved
- [ ] Dashboard total increases by R125.50

---

### TC-014: Add Expense with Receipt Photo (Camera)
**Requirement:** R09  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen (from TC-013)
2. Click camera icon
3. Select "Take Photo"
4. Take a test photo using device camera
5. Confirm/accept photo
6. Fill remaining expense fields
7. Click "Save Expense"
8. **Expected:** Expense saved with receipt photo attached

**Validation:**
- [ ] Receipt photo thumbnail appears in Expense List
- [ ] Photo quality acceptable
- [ ] File stored locally (no internet required)

---

### TC-015: Add Expense with Receipt Photo (Gallery)
**Requirement:** R09  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Click camera icon
3. Select "Choose from Gallery"
4. Select existing photo
5. Fill expense details
6. Save
7. **Expected:** Photo attached to expense

**Validation:**
- [ ] Gallery picker works
- [ ] Photo properly scaled/compressed
- [ ] No crashes on large images

---

### TC-016: Full-Screen Receipt Photo Viewer
**Requirement:** R11  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Expense with receipt photo (from TC-014 or TC-015)

1. Expense List screen
2. Tap receipt thumbnail
3. **Expected:** Full-screen photo viewer opens

**Validation:**
- [ ] Photo displays in full resolution
- [ ] Can zoom/pinch to enlarge
- [ ] Back button closes viewer
- [ ] Not blocking app functionality

---

### TC-017: Edit Expense
**Requirement:** R12, R13  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Expense from TC-013 exists

1. Expense List
2. Tap expense "Weekly shopping at Pick n Pay"
3. Edit amount: 130.75 (was 125.50)
4. Edit description: `Weekly shopping + household items`
5. Click "Save Changes"
6. **Expected:** Expense updated

**Validation:**
- [ ] All fields editable
- [ ] Dashboard total recalculates
- [ ] Changes persist

---

### TC-018: Delete Expense
**Requirement:** R12  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Expense exists from previous tests

1. Expense List
2. Long-press or swipe on an expense
3. Click "Delete"
4. Confirm deletion
5. **Expected:** Expense removed from list

**Validation:**
- [ ] Expense disappeared
- [ ] Dashboard total decreased (soft delete)
- [ ] Expense removed from all reports
- [ ] NOT permanently deleted (is_deleted flag set to 1)

---

### TC-019: Input Validation – Empty Amount
**Requirement:** R13  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Leave Amount empty
3. Fill other fields
4. Click "Save Expense"
5. **Expected:** Error: "Please enter amount"

**Validation:**
- [ ] Form does not submit
- [ ] Error message shown
- [ ] Focus returns to amount field

---

### TC-020: Input Validation – Invalid Date Range
**Requirement:** R13  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Amount: 50
3. Start Time: 14:00 (2:00 PM)
4. End Time: 12:00 (12:00 PM) — BEFORE start time
5. Click Save
6. **Expected:** Error: "End time must be after start time"

**Validation:**
- [ ] Validation triggers
- [ ] Form rejected
- [ ] User corrects and retries

---

### TC-021: Expense List Filtering by Date Range
**Requirement:** R10  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Multiple expenses spanning several days/weeks

1. Expense List screen
2. Click date range filter
3. Start Date: 1st of current month
4. End Date: 15th of current month
5. Click "Apply"
6. **Expected:** List shows only expenses in range

**Validation:**
- [ ] Correct expenses displayed
- [ ] Total for range recalculated
- [ ] Can change date range multiple times

---

## 📈 GROUP 4: DASHBOARD & BUDGET TESTS (R14, R15, R16)

### TC-022: Dashboard – Total Monthly Spending
**Requirement:** R15  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Multiple expenses entered (R08)

1. Navigate to Dashboard
2. Observe "This Month" total
3. **Expected:** Amount displays in R format (e.g., "R1,250.50")

**Validation:**
- [ ] Total is accurate (sum of all month's expenses)
- [ ] Updates immediately when new expense added
- [ ] Format is ZA Rand (R notation)

---

### TC-023: Dashboard – Budget Progress Bar (Good Status)
**Requirement:** R15, R16  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Category with max goal R5,000, spent R3,000 (60%)

1. Dashboard
2. Observe category progress card
3. **Expected:** Green colour, 60% bar filled, "Good" label

**Validation:**
- [ ] Green colour (#16A34A)
- [ ] Progress bar accurate
- [ ] Status label "Good"

---

### TC-024: Dashboard – Budget Progress Bar (Close Status)
**Requirement:** R15, R16  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Category with max goal R5,000, spent R4,200 (84%)

1. Dashboard
2. Observe category progress
3. **Expected:** Amber/yellow colour, 84% bar, "Close" label

**Validation:**
- [ ] Amber colour (#F59E0B)
- [ ] Progress accurate
- [ ] Status "Close"
- [ ] Alert visible to user

---

### TC-025: Dashboard – Budget Progress Bar (Over Status)
**Requirement:** R15, R16  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Category with max goal R5,000, spent R5,500 (110%)

1. Dashboard
2. Observe category progress
3. **Expected:** Red colour, 100%+ bar, prominent "OVER!" label

**Validation:**
- [ ] Red colour (#DC2626)
- [ ] Progress bar maxed at 100%
- [ ] "OVER!" badge visible
- [ ] User cannot miss this alert

---

### TC-026: Category Goal Configuration
**Requirement:** R14  
**Status:** [ ] PASS | [ ] FAIL  

1. Categories → Edit category
2. Set Min Monthly Goal: R1,000
3. Set Max Monthly Goal: R5,000
4. Save
5. **Expected:** Dashboard uses these goals

**Validation:**
- [ ] Goals saved
- [ ] Progress bars reflect new limits
- [ ] Can adjust goals anytime

---

## 📊 GROUP 5: REPORTS & ANALYTICS TESTS (R17, R18)

### TC-027: Category Spending Pie Chart
**Requirement:** R17  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Multiple expenses in different categories

1. Navigate to Reports → Category Report
2. Observe pie chart
3. **Expected:** Slices sized proportional to spending per category

**Validation:**
- [ ] Chart renders without crashing
- [ ] Colours match category colours
- [ ] Legend shows category names + amounts
- [ ] Tapping slice highlights it
- [ ] Chart updates when expenses change

---

### TC-028: Daily Spending Bar Chart (30-Day View)
**Requirement:** R18  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Expenses spread across multiple days

1. Navigate to Reports → Daily Spending Graph
2. Observe bar chart
3. **Expected:** 30-day rolling window displayed

**Validation:**
- [ ] Days with expenses show bars
- [ ] Days without expenses show zero or no bar
- [ ] Y-axis shows currency values
- [ ] X-axis shows dates
- [ ] Bars sized accurately to daily spending
- [ ] No crashes with large datasets

---

### TC-029: Chart Month Navigation
**Requirement:** R18  
**Status:** [ ] PASS | [ ] FAIL  

1. Reports screen
2. Click "Previous Month" button
3. Chart updates to show previous month's data
4. Click "Next Month" button
5. Returns to current month
6. **Expected:** Charts update dynamically

**Validation:**
- [ ] Navigation works smoothly
- [ ] Data updates correctly
- [ ] Can browse historical data

---

### TC-030: Chart with Zero-Spending Days
**Requirement:** R18  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Some days with no expenses

1. Reports → Daily Spending
2. Observe days with zero spending
3. **Expected:** Chart handles gracefully (shows 0 or omits bar)

**Validation:**
- [ ] No crashes or errors
- [ ] Chart still readable
- [ ] Scaling appropriate

---

## 🏆 GROUP 6: GAMIFICATION TESTS (R19, R20, R21, R22)

### TC-031: Points System – Add Expense
**Requirement:** R19  
**Status:** [ ] PASS | [ ] FAIL  

1. Navigate to Rewards screen
2. Note current points
3. Add a new expense
4. Return to Rewards
5. **Expected:** Points increased (typically +5 per expense)

**Validation:**
- [ ] Points increment visible
- [ ] Matches expected value (+5)
- [ ] Updates immediately

---

### TC-032: Badge Award – FIRST_SAVE
**Requirement:** R20  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Fresh user with 0 expenses

1. Add first expense
2. Navigate to Rewards
3. **Expected:** "First Save" badge appears

**Validation:**
- [ ] Badge displays with icon
- [ ] Unlock date shown
- [ ] Points awarded

---

### TC-033: Badge Award – QUICK_LOGGER
**Requirement:** R20  
**Status:** [ ] PASS | [ ] FAIL  

1. Add 10+ expenses in same day
2. Navigate to Rewards
3. **Expected:** "Quick Logger" badge appears

**Validation:**
- [ ] Badge unlocks on 10th expense of day
- [ ] Points awarded
- [ ] Cannot earn twice same day

---

### TC-034: Level Progression
**Requirement:** R21  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User has some points

1. Rewards screen
2. Observe "Level" indicator
3. Points accumulate
4. At 100 pts → Level 2
5. At 250 pts → Level 3
6. **Expected:** Level updates automatically

**Validation:**
- [ ] Levels: 1→100→250→500→1000pts
- [ ] Visual indication of level
- [ ] Progress towards next level shown
- [ ] Level persists

---

### TC-035: Leaderboard – User Ranking
**Requirement:** R22  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Multiple users registered (or if single-user, mock data)

1. Navigate to Leaderboard
2. Observe ranking
3. **Expected:** Users ranked by total_points DESC

**Validation:**
- [ ] Current user highlighted
- [ ] Ranking numbers accurate
- [ ] Points displayed correctly
- [ ] List updates as points change

---

## 🎯 GROUP 7: ONBOARDING TESTS (R23)

### TC-036: 3-Step Onboarding – Step 1
**Requirement:** R23  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Fresh user after registration

1. After registration, Onboarding Step 1 appears
2. **Expected:** Welcome screen with app intro

**Validation:**
- [ ] Step indicator shows "1/3"
- [ ] Information clearly presented
- [ ] Skip button available
- [ ] Next button advances to step 2

---

### TC-037: 3-Step Onboarding – Step 2 (Create Category)
**Requirement:** R23  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Completed Step 1

1. Click "Next"
2. **Expected:** Step 2 – Create first category
3. Enter category name & colour
4. Click "Next"

**Validation:**
- [ ] Category created and saved
- [ ] Advances to Step 3

---

### TC-038: 3-Step Onboarding – Step 3 (Add First Expense)
**Requirement:** R23  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Completed Step 2

1. Step 3 – Add first expense (optional)
2. Can skip or enter expense
3. Click "Finish"
4. **Expected:** Dashboard appears

**Validation:**
- [ ] Onboarding flag set to complete
- [ ] Does NOT re-show onboarding on restart
- [ ] Dashboard accessible

---

### TC-039: Onboarding Not Repeated
**Requirement:** R23  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** Completed onboarding in previous test

1. Logout user
2. LogRestart, log back in
3. **Expected:** Dashboard appears immediately (NO onboarding)

**Validation:**
- [ ] Onboarding skipped entirely
- [ ] Flag persists (SharedPreferences)

---

## ✅ FINAL CHECKLIST

### Device/Emulator Requirements
- [ ] Minimum SDK 26 (Android 8.0) tested
- [ ] Tested on at least 1 physical device or emulator
- [ ] Screen sizes tested: 360dp (phone) and 600dp (tablet if available)

### No Crashes
- [ ] No crashes on any of the above tests
- [ ] All transitions smooth
- [ ] Loading indicators show for async operations
- [ ] No ANR (Application Not Responding) errors

### UI/UX Quality
- [ ] Material Design 3 components used throughout
- [ ] Consistent colour scheme (#1A6FE8 primary, etc.)
- [ ] Readable text hierarchy
- [ ] Buttons easily tappable (54dp height)
- [ ] No overlapping elements

### Data Persistence
- [ ] All data persists across app kills
- [ ] SQLite database file exists at expected location
- [ ] No database corruption observed

### Performance
- [ ] App launches in < 3 seconds
- [ ] Charts render smoothly (no lag)
- [ ] Expense list scrolls smoothly (100+ items tested)
- [ ] No memory leaks (monitored via Android Profiler)

---

## 📝 TEST RESULTS SUMMARY

Tester Name: ___________________________  
Date: May 6, 2026  
Device/Emulator: _________________________  
Android Version: __________________________  

**Total Tests:** 39  
**Passed:** _____  |  **Failed:** _____  |  **Blocked:** _____  

### Failed Tests (if any):
```
1. _________________________________
2. _________________________________
3. _________________________________
```

### Notes:
```
[Space for additional observations]
```

---

**QA Lead Signature:** ______________________  
**Date Signed:** ______________________


