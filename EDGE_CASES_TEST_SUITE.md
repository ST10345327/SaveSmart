# SaveSmart — Edge Cases & Error Handling Test Suite
**Date:** May 6, 2026  
**Student:** Olebogeng Phawe (ST10345327)  
**POE:** OPSC6311 Personal Budget Tracker

---

## 📋 EDGE CASE TESTING FRAMEWORK

This document provides comprehensive test cases for edge cases and error conditions across all SaveSmart features.

---

## 🧪 GROUP 1: DATA VALIDATION EDGE CASES

### EC-001: Empty Email Field on Registration
**Requirement:** R03 (Input Validation)  
**Status:** [ ] PASS | [ ] FAIL  

1. Registration screen
2. Leave Email field empty
3. Fill password field: `ValidPass123`
4. Click "Register"
5. **Expected:** Error message: "Email/username is required"

**Validation:**
- [ ] Form does not submit
- [ ] Error highlights email field
- [ ] User can correct and retry

---

### EC-002: Invalid Email Format
**Requirement:** R03  
**Status:** [ ] PASS | [ ] FAIL  

1. Registration screen
2. Email: `notanemail` (no @ symbol)
3. Password: `ValidPass123`
4. Click "Register"
5. **Expected:** Error: "Invalid email format" OR "Username format invalid"

**Validation:**
- [ ] Rejected and explained
- [ ] User understands what went wrong

---

### EC-003: Password Too Short (< 8 Characters)
**Requirement:** R03  
**Status:** [ ] PASS | [ ] FAIL  

1. Registration screen
2. Username: `testuser`
3. Password: `Pass12` (only 6 chars, but should need 8)
4. Click "Register"
5. **Expected:** Error: "Password must be at least 8 characters..."

**Validation:**
- [ ] Validation enforced
- [ ] Clear minimum specified
- [ ] Note: Current code uses 6 chars; consider upgrading to 8 for POE

---

### EC-004: Duplicate Email/Username on Registration
**Requirement:** R03  
**Status:** [ ] PASS | [ ] FAIL  

**Prerequisites:** User `testuser@email.com` already registered

1. Registration screen
2. Username: `testuser@email.com` (duplicate)
3. Password: `ValidPass123`
4. Click "Register"
5. **Expected:** Error: "Email/username already registered"

**Validation:**
- [ ] Duplicate detection works
- [ ] User informed clearly
- [ ] No exception thrown

---

### EC-005: Expense Amount = 0
**Requirement:** R13 (Input Validation)  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Amount: `0`
3. Fill other fields
4. Click "Save Expense"
5. **Expected:** Error: "Amount must be greater than 0" OR amount accepted with warning

**Validation:**
- [ ] Zero rejected OR warned
- [ ] Makes business sense (can't log 0 expense)

---

### EC-006: Expense Date in Future
**Requirement:** R08, R13  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Amount: `50`
3. Date: Tomorrow (future date)
4. Click "Save Expense"
5. **Expected:** Either:
   - ✅ Accepted (reasonable for advance expenses)
   - ✅ Warning shown (proceed with confirmation)
   - ✅ Rejected (strict past-only policy)

**Decision:** Document which approach your app uses

---

### EC-007: Negative Amount
**Requirement:** R08, R13  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Amount: `-50` (negative)
3. Click "Save Expense"
4. **Expected:** Error: "Amount cannot be negative"

**Validation:**
- [ ] Rejected immediately
- [ ] User understands why

---

### EC-008: Very Large Amount (> 999,999.99)
**Requirement:** R08, R13, T10 (Milliunit handling)  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Amount: `999999999.99` (very large)
3. Fill other fields
4. Click "Save Expense"
5. **Expected:** Either:
   - ✅ Accepted (if within app limits)
   - ✅ Rejected with limit shown (e.g., "Max R1,000,000")
   - ✅ Warning about unusual amount

**Validation:**
- [ ] Handles gracefully (no crash)
- [ ] User gets helpful feedback
- [ ] Milliunit conversion accurate

---

### EC-009: Missing Category Selection
**Requirement:** R08 (Expense requires category)  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Amount: `50`
3. Leave Category empty (or set to "None")
4. Fill other fields
5. Click "Save Expense"
6. **Expected:** Error: "Please select a category"

**Validation:**
- [ ] Form does not submit
- [ ] User must select valid category
- [ ] Reasonable error message

---

### EC-010: Missing Receipt Photo (If Required)
**Requirement:** R09 (Receipt handling)  
**Status:** [ ] PASS | [ ] FAIL  

**Decision point:** Is receipt required or optional?

**If REQUIRED:**
1. Add Expense screen
2. All fields filled EXCEPT receipt
3. Click "Save Expense"
4. **Expected:** Error: "Please attach receipt photo"

**If OPTIONAL:**
1. Same action
2. **Expected:** Saves successfully without photo

**Validation:**
- [ ] Behavior matches app design
- [ ] Document in README

---

### EC-011: Special Characters in Description
**Requirement:** R08  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Description: `🍔 Lunch @ Nando's!!! @#$%^&*()`
3. Amount: `50`
4. Click "Save Expense"
5. **Expected:** Accepted and stored correctly

**Validation:**
- [ ] Emoji support (if desired)
- [ ] Special characters don't break database
- [ ] Description displays correctly later

---

### EC-012: Very Long Description (1000+ characters)
**Requirement:** R08  
**Status:** [ ] PASS | [ ] FAIL  

1. Add Expense screen
2. Description: 1000 random characters (copypasta Lorem Ipsum)
3. Amount: `50`
4. Click "Save Expense"
5. **Expected:** Either:
   - ✅ Accepted and truncated to limit (e.g., 500 chars)
   - ✅ Rejected: "Description too long (max 500 chars)"
   - ✅ Accepted without modification

**Validation:**
- [ ] Handles gracefully
- [ ] No database overflow
- [ ] User informed of limits

---

## 🌐 GROUP 2: OFFLINE & NETWORK EDGE CASES

### EC-013: Complete Offline Operation
**Requirement:** T02 (Room database, offline-first)  
**Status:** [ ] PASS | [ ] FAIL  

**Setup:**
1. Enable airplane mode (disconnect all network)
2. Verify Settings → "Offline" indicator shows (if applicable)

**Test workflow:**
1. Register new user (offline registration should work)
2. Add expense
3. Create category
4. View dashboard
5. Add receipt photo
6. All operations complete successfully

**Validation:**
- [ ] App works fully offline
- [ ] No "check network" prompts
- [ ] No crashes
- [ ] No timeout errors

---

### EC-014: Network Reconnection Data Sync
**Requirement:** T02 (May not apply if offline-only)  
**Status:** [ ] PASS | [ ] FAIL  

**If app supports cloud sync:**
1. Add expense while offline
2. Reconnect network
3. Wait 5 seconds for auto-sync (if applicable)
4. Verify dashboard updates
5. **Expected:** Data syncs without user action

**If offline-only:**
- ✅ Mark as N/A (not applicable)

---

### EC-015: Network Interruption During Operation
**Requirement:** T02  
**Status:** [ ] PASS | [ ] FAIL  

**Setup:** Connection available initially

**Test:**
1. Start adding expense (fill form)
2. Disable network mid-entry
3. Click "Save Expense"
4. **Expected:** Either:
   - ✅ Saves locally (offline-first design)
   - ✅ Shows error: "No network connection. Saving locally..."

**Validation:**
- [ ] No data loss
- [ ] User not left in broken state

---

## ⚙️ GROUP 3: CONCURRENCY & THREAD SAFETY

### EC-016: Multiple Expenses Added Rapidly
**Requirement:** T02 (Database thread safety)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Open Add Expense screen
2. Quickly add 10 expenses in succession (as fast as possible)
3. Check Expense List and Dashboard
4. **Expected:**
   - All 10 expenses present
   - Total spending accurate (no duplicates/loss)
   - Dashboard updates correctly

**Validation:**
- [ ] No crashes
- [ ] No data corruption
- [ ] List remains sorted properly

---

### EC-017: Rapid Goal Updates
**Requirement:** R14 (Budget goals)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Edit category goal
2. While updating, add new expense
3. While expense adding, update goal again
4. **Expected:** Final state is consistent

**Validation:**
- [ ] No race conditions
- [ ] Dashboard reflects latest state
- [ ] No exceptions thrown

---

### EC-018: Photo Upload on Background Thread
**Requirement:** R09, T06 (Non-blocking operations)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Add Expense with receipt photo
2. While photo is compressing/uploading:
   - Can scroll list? Yes ✅
   - Can navigate away? Yes ✅
   - UI responsive? Yes ✅
3. Photo saves successfully

**Validation:**
- [ ] UI not frozen during compression
- [ ] No ANR (Application Not Responding) errors
- [ ] Photo persists correctly

---

## 🔄 GROUP 4: STATE MANAGEMENT & ORIENTATION

### EC-019: Orientation Change (Portrait → Landscape)
**Requirement:** T09 (Responsive UI)  
**Status:** [ ] PASS | [ ] FAIL  

**Setup:** Edit Expense screen with partially filled form

**Test:**
1. Add Expense screen with Amount: `50`, Description: partially filled
2. Rotate device to landscape
3. **Expected:** Form data still present, layout adjusted

**Validation:**
- [ ] All form data preserved
- [ ] Layout reflows properly
- [ ] Keyboard not stuck open
- [ ] Can submit successfully

---

### EC-020: Orientation Change Back to Portrait
**Requirement:** T09  
**Status:** [ ] PASS | [ ] FAIL  

**Setup:** Landscape orientation active

**Test:**
1. Device in landscape
2. Rotate back to portrait
3. **Expected:** Data still present, layout correct

**Validation:**
- [ ] Smooth transition
- [ ] No duplicate UI elements
- [ ] Form still functional

---

### EC-021: App Backgrounding & Resuming
**Requirement:** T01 (Lifecycle management)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Dashboard screen showing R1,250.50
2. Home button (background app)
3. Wait 30 seconds
4. Tap app to resume
5. **Expected:** Dashboard still shows R1,250.50, no refresh/reload

**Validation:**
- [ ] Data persisted
- [ ] Session still valid
- [ ] UI restored correctly

---

### EC-022: App Killed While in Background
**Requirement:** T01  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Navigate to Dashboard
2. Home button (background app)
3. Force stop app (Settings → Apps → SaveSmart → Force Stop)
4. Reopen app
5. **Expected:** Dashboard visible (auto-login from session)

**Validation:**
- [ ] Session restored
- [ ] User stays logged in
- [ ] Dashboard data loaded correctly

---

### EC-023: Back Button on Login Screen
**Requirement:** T01 (Navigation)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. On Login screen
2. Press back button
3. **Expected:** App exits (or returns to previous screen if launched from another app)

**Validation:**
- [ ] Graceful exit
- [ ] No crashes
- [ ] Back stack cleared

---

### EC-024: Back Button on Dashboard (Logged In)
**Requirement:** T01, R04  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Dashboard screen (logged in)
2. Press back button
3. **Expected:** Exit app OR show logout confirmation

**Decision:** Document your back behavior

**Validation:**
- [ ] User doesn't accidentally log out
- [ ] Clear behavior documented

---

### EC-025: Fragment Backstack Overflow
**Requirement:** T01  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Navigate deeply: Dashboard → AddExpense → PhotoViewer → Back → AddExpense (repeat 20 times)
2. **Expected:** Back navigation works smoothly all the way

**Validation:**
- [ ] No crashes from deep backstack
- [ ] No duplicate fragments
- [ ] Memory not leaking

---

## 🔐 GROUP 5: SECURITY VERIFICATION

### SEC-001: Password Hashing Verification
**Requirement:** R01 (SHA-256)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Register user with password: `MySecurePass123`
2. Database inspection (debug tools):
   - ✅ Password NOT stored as plaintext
   - ✅ Password IS stored as SHA-256 hash (64 hex characters)
3. Attempt login with `MySecurePass123` → Works ✅
4. Attempt login with `mySecurePass123` (wrong case) → Fails ✅

**Validation:**
- [ ] Plaintext: NOT in database
- [ ] Hash: Exactly 64 hex characters (SHA-256)
- [ ] Case-sensitive matching works

---

### SEC-002: No Salt Issues (If Using Salt)
**Requirement:** R01  
**Status:** [ ] PASS | [ ] FAIL  

**If app uses salt in password hashing:**
1. Register user with password `TestPass123`
2. Register another user with same password `TestPass123`
3. Database inspection:
   - Both passwords hashed
   - Hashes are DIFFERENT (due to different salts)
4. Both users can login with their password

**If no salt (current implementation):**
- ✅ Mark as N/A or "Single hash per password"

---

### SEC-003: Session Token Storage
**Requirement:** R02  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Login user
2. Check SharedPreferences (debug tools):
   - ✅ user_id stored (integer, not sensitive)
   - ✅ username stored (fine, not secret)
   - ✅ NO password stored
   - ✅ is_logged_in flag = true

**Validation:**
- [ ] Session token properly isolated
- [ ] Credentials never stored in SharedPreferences
- [ ] Logout clears all session data

---

### SEC-004: Logout Clears Credentials
**Requirement:** R04  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Login user
2. Check SharedPreferences: session data present
3. Logout
4. Check SharedPreferences again:
   - ✅ is_logged_in = false
   - ✅ user_id cleared
   - ✅ username cleared
5. Verify can't access Dashboard without re-login

**Validation:**
- [ ] All credentials cleared
- [ ] User must re-authenticate
- [ ] No leftover tokens

---

### SEC-005: Receipt Photos in Private Directory
**Requirement:** R09 (Data protection)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Add expense with receipt photo
2. Check file system (debug tools):
   - ✅ Photo stored in `/data/data/com.example.savesmart/files/` or similar private directory
   - NOT stored in `/storage/emulated/0/` (public)
3. Try accessing photo via file manager as other user:
   - Should NOT be accessible

**Validation:**
- [ ] Private directory used
- [ ] Not world-readable
- [ ] Secure storage pattern followed

---

### SEC-006: Database File Permissions
**Requirement:** T02 (Data protection)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. App created and running
2. Check database file permissions (debug tools):
   - Database: `/data/data/com.example.savesmart/databases/savesmart_v3.db`
   - Permissions: Should be readable only by app
3. Attempt access from other app: DENIED ✅

**Validation:**
- [ ] Not world-readable
- [ ] App-only access enforced
- [ ] Room database handles this automatically

---

### SEC-007: No Sensitive Data in Logs
**Requirement:** T06 (Logging standards)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Enable Logcat in Android Studio
2. Run through login workflow
3. Search logs for:
   - ❌ "password" (should NOT appear)
   - ❌ "token" (should NOT appear)
   - ❌ "secret" (should NOT appear)
   - ✅ "Login successful" (OK to log)
   - ✅ "Login failed" (OK to log)

**Validation:**
- [ ] Credentials never logged
- [ ] No PII in logs
- [ ] Operational logging still present

---

### SEC-008: No Hardcoded Secrets in Code
**Requirement:** T06 (Code security)  
**Status:** [ ] PASS | [ ] FAIL  

**Test:**
1. Search codebase for:
   ```bash
   grep -r "password.*=" app/src/main --include="*.kt" | grep -i "=.*\"" 
   grep -r "api_key" app/src/main --include="*.kt"
   grep -r "secret" app/src/main --include="*.kt"
   ```
2. **Expected:** No results (or only legitimate variable names)

**Validation:**
- [ ] No hardcoded passwords
- [ ] No API keys in code
- [ ] No secrets in resources

---

## 📋 EDGE CASE TESTING SUMMARY

### Testing Categories Covered:
- ✅ Data Validation (12 test cases)
- ✅ Offline & Network (3 test cases)
- ✅ Concurrency (3 test cases)
- ✅ State Management (7 test cases)
- ✅ Security (8 test cases)

**Total Edge Case Tests:** 33

---

## ✅ COMPLETION CHECKLIST

Data Validation:
- [ ] EC-001: Empty email rejected
- [ ] EC-002: Invalid email rejected
- [ ] EC-003: Short password rejected
- [ ] EC-004: Duplicate email rejected
- [ ] EC-005: Zero amount rejected
- [ ] EC-006: Future date handled
- [ ] EC-007: Negative amount rejected
- [ ] EC-008: Large amount handled
- [ ] EC-009: Missing category rejected
- [ ] EC-010: Missing receipt handled
- [ ] EC-011: Special characters work
- [ ] EC-012: Long description handled

Offline & Network:
- [ ] EC-013: Complete offline operation works
- [ ] EC-014: Network reconnection syncs (if applicable)
- [ ] EC-015: Network interruption handled

Concurrency:
- [ ] EC-016: Rapid expenses no data loss
- [ ] EC-017: Rapid goal updates no race condition
- [ ] EC-018: Photo upload doesn't freeze UI

State Management:
- [ ] EC-019: Orientation change preserves data
- [ ] EC-020: Back to portrait works
- [ ] EC-021: Background/resume preserves state
- [ ] EC-022: App kill & reopen works
- [ ] EC-023: Back on login screen
- [ ] EC-024: Back on dashboard
- [ ] EC-025: Deep backstack no crashes

Security:
- [ ] SEC-001: Password hashing verified
- [ ] SEC-002: Salt handling (if applicable)
- [ ] SEC-003: Session storage secure
- [ ] SEC-004: Logout clears credentials
- [ ] SEC-005: Photos in private directory
- [ ] SEC-006: Database file secure
- [ ] SEC-007: No sensitive data in logs
- [ ] SEC-008: No hardcoded secrets

---

## 🎯 PASS CRITERIA

**PASS:** 31/33 edge case tests pass (94%+)  
**FAIL:** Any security test fails (SEC-001 to SEC-008 are critical)

---

**Edge Case Testing:** Ready for execution  
**Date Prepared:** May 6, 2026  
**Student:** Olebogeng Phawe (ST10345327)


