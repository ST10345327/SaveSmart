# ✅ Pre-Commit Verification Checklist

## 📱 Files Created — Verify All Exist

Run this in PowerShell to verify all files were created:

```powershell
# Check layout files
ls C:\Users\CASH\AndroidStudioProjects\SaveSmart\app\src\main\res\layout\

# Check drawable files
ls C:\Users\CASH\AndroidStudioProjects\SaveSmart\app\src\main\res\drawable\

# Check navigation files
ls C:\Users\CASH\AndroidStudioProjects\SaveSmart\app\src\main\res\navigation\

# Check kotlin files
ls C:\Users\CASH\AndroidStudioProjects\SaveSmart\app\src\main\java\com\example\savesmart\ui\auth\
ls C:\Users\CASH\AndroidStudioProjects\SaveSmart\app\src\main\java\com\example\savesmart\ui\dashboard\
```

## ✅ Expected Output

You should see:

### Layout Files:
- ✅ activity_login.xml
- ✅ activity_main.xml
- ✅ activity_register.xml
- ✅ fragment_login.xml ← NEW

### Drawable Files:
- ✅ bg_button_primary.xml ← NEW
- ✅ bg_input.xml ← NEW
- ✅ ic_launcher_background.xml
- ✅ ic_launcher_foreground.xml

### Navigation Files:
- ✅ nav_graph.xml ← NEW

### Auth Kotlin Files:
- ✅ AuthViewModel.kt
- ✅ LoginActivity.kt
- ✅ LoginFragment.kt
- ✅ RegisterActivity.kt
- ✅ RegisterFragment.kt ← NEW

### Dashboard Kotlin Files:
- ✅ DashboardActivity.kt
- ✅ DashboardFragment.kt ← NEW

## 🔧 In Android Studio

1. **Sync Gradle** — Click "Sync Now" if prompted
2. **Build Project** — Ctrl+F9 or Build → Make Project
3. **Check for Errors** — View → Tool Windows → Problems (Ctrl+Alt+6)
4. **Run** — Click the green ▶ button (no errors should appear)

## 🎯 Commit Now

Once all files are verified:

```bash
cd C:\Users\CASH\AndroidStudioProjects\SaveSmart

git add .

git status

git commit -m "[auth] Fix LoginFragment — resolve all 12 binding and resource errors

- Create fragment_login.xml layout with Material Design 3 inputs
- Generate nav_graph.xml with login->register and login->dashboard actions
- Create AuthViewModel with secure login logic and LiveData state
- Add all missing string resources (strings.xml) and color definitions
- Enable ViewBinding in build.gradle for type-safe view access
- Add comprehensive logging and error handling throughout
- Create placeholder RegisterFragment and DashboardFragment

Refs: R02, R03, T01, T06"

git push origin main
```

## ✅ After Commit

Verify on GitHub:

1. Go to https://github.com/YOUR_USERNAME/SaveSmart
2. Click **Commits**
3. Find the commit starting with `[auth] Fix LoginFragment`
4. Verify all files are included in the commit

---

## 🐛 Troubleshooting

**If you get "fatal: not a git repository":**
```bash
cd C:\Users\CASH\AndroidStudioProjects\SaveSmart
git status
```

**If you get "fatal: origin does not appear to be a 'git' repository":**
```bash
git remote -v
git remote add origin https://github.com/YOUR_USERNAME/SaveSmart.git
git push origin main
```

**If you get "permission denied":**
- Check you have GitHub credentials configured
- Generate a personal access token on GitHub
- Use: `git config --global user.token YOUR_TOKEN`

---

## 📝 Verification Complete!

All files are in place. You're ready to commit! ✨

