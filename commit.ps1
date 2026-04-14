# SaveSmart LoginFragment Setup — Git Commit Script (PowerShell)
# Run this in Android Studio Terminal or PowerShell

Write-Host "📦 Staging all changes..." -ForegroundColor Green
git add .

Write-Host ""
Write-Host "📋 Verifying staged files..." -ForegroundColor Green
git status

Write-Host ""
Write-Host "💾 Committing changes..." -ForegroundColor Green
git commit -m "[auth] Fix LoginFragment — resolve all 12 binding and resource errors

- Create fragment_login.xml layout with Material Design 3 inputs
- Generate nav_graph.xml with login->register and login->dashboard actions
- Create AuthViewModel with secure login logic and LiveData state
- Add all missing string resources (strings.xml) and color definitions
- Enable ViewBinding in build.gradle for type-safe view access
- Add comprehensive logging and error handling throughout
- Create placeholder RegisterFragment and DashboardFragment

Refs: R02, R03, T01, T06"

Write-Host ""
Write-Host "🚀 Pushing to GitHub..." -ForegroundColor Green
git push origin main

Write-Host ""
Write-Host "✅ Commit complete!" -ForegroundColor Green

