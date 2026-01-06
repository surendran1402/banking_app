Write-Host "Checking git status..." -ForegroundColor Cyan
git status

Write-Host "`nAdding all files..." -ForegroundColor Cyan
git add .

Write-Host "`nCommitting..." -ForegroundColor Cyan
git commit -m "Add complete banking application"

Write-Host "`nPushing to remote..." -ForegroundColor Cyan
git push origin main

Write-Host "`nDone!" -ForegroundColor Green
