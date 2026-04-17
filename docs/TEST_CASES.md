# SaveSmart - Comprehensive Test Cases
**Date:** April 16, 2026  
**Student:** Olebogeng Phawe (ST10345327)  
**POE:** OPSC6311 Personal Budget Tracker  
**Version:** 1.1

---

## 📋 Test Plan Overview

This document contains **manual test cases** for all SaveSmart features. Each test case includes:
- **Test ID** — Unique identifier
- **Module** — Feature being tested
- **Requirement** — Associated R/T requirement
- **Test Description** — What to test
- **Steps** — How to execute the test
- **Expected Result** — What should happen
- **Test Data** — Input values
- **Pass/Fail Criteria** — How to determine if test passes

---

## 🔐 Module 1: Authentication (R01, R02, R03, R04)

### TC-AUTH-001: Register with Valid Credentials
**Requirement:** R01, R03  
**Description:** User can register with valid username and password  

**Test Data:**
```
Username: testuser123
Password: MyPass@2024
Confirm Password: MyPass@2024
```

**Steps:**
1. Launch app
2. Click "Create Account" on login screen
3. Enter username: `testuser123`
4. Enter password: `MyPass@2024`
5. Enter confirm password: `MyPass@2024`
6. Click "Register" button
7. Verify success message appears
8. Verify navigation to Dashboard

**Expected Result:**
- ✅ Toast: "Registration successful!"
- ✅ User navigates to Dashboard
- ✅ Database contains new user with SHA-256 hashed password
- ✅ Logout and try login with new credentials — should work

**Pass Criteria:** Navigation successful + login works on next attempt

---

### TC-AUTH-010: Logout Clears Session
**Requirement:** R04  
**Description:** Logout clears session and returns to Login

**Prerequisites:** User is logged in on Dashboard

**Steps:**
1. Navigate to Dashboard (logged in)
2. Click "Logout" button
3. Wait for navigation
4. Verify Login screen appears

**Expected Result:**
- ✅ Session cleared from SharedPreferences
- ✅ User returns to Login screen
- ✅ Login fields are empty
- ✅ Back button does not go to Dashboard

**Pass Criteria:** Session completely cleared + Login screen appears

---

## 📊 Module 2: Dashboard (R15, R16)

### TC-DASH-001: Dashboard Displays Total Monthly Spending
**Requirement:** R15  
**Description:** Dashboard shows total spending for current month

**Steps:**
1. Login to app
2. Navigate to Dashboard
3. Verify total amount is correct based on entered expenses.

**Expected Result:**
- ✅ Amount displayed in R format (e.g., "R1,250.00")
- ✅ Live update when new expenses are added.

---

## 💸 Module 3: Expense Management (R08, R10, R12, R13)

### TC-EXP-001: Add New Expense with Valid Data
**Requirement:** R08, R13  
**Description:** User can successfully add a new expense entry.

**Test Data:**
```
Amount: 150.50
Category: Food & Dining
Date: Today
Description: Lunch at Nandos
```

**Steps:**
1. Click the "+" (FAB) button on the Dashboard.
2. Enter "150.50" in the Amount field.
3. Select "Food & Dining" from the Category spinner.
4. Select today's date using the Date Picker.
5. Enter "Lunch at Nandos" in Description.
6. Click "Save Expense".

**Expected Result:**
- ✅ Toast message: "Expense saved successfully!"
- ✅ App navigates back to Dashboard.
- ✅ Dashboard "Total Spending" increases by R150.50.

---

### TC-EXP-002: Add Expense Validation (Empty Amount)
**Requirement:** R13  
**Description:** System prevents saving if amount is empty.

**Steps:**
1. Go to "Add Expense" screen.
2. Leave Amount empty.
3. Click "Save Expense".

**Expected Result:**
- ✅ Error message on Amount field: "Enter amount".
- ✅ App stays on Add Expense screen.

---

### TC-EXP-003: Date Picker Functionality
**Requirement:** R10  
**Description:** User can select a specific date for an expense.

**Steps:**
1. Click the Date field.
2. Select a date from last week.
3. Verify the field updates to the selected date.

**Expected Result:**
- ✅ DatePickerDialog opens.
- ✅ Selected date is displayed in "dd/MM/yyyy" format.

---

### TC-EXP-004: Expense List Filtering by Date Range
**Requirement:** R10  
**Description:** User can view expenses filtered by a selected date range.

**Steps:**
1. Navigate to Expense List.
2. Select Start Date: 01/04/2026 and End Date: 15/04/2026.
3. Verify only expenses within this range are shown.

**Expected Result:**
- ✅ List updates to show filtered items.
- ✅ Total for the range is recalculated.
