@echo off
echo Cleaning up git state... > git_fix.log
git reset >> git_fix.log 2>&1
echo Adding files (respecting .gitignore)... >> git_fix.log
git add . >> git_fix.log 2>&1
echo Committing... >> git_fix.log
git commit -m "Project structure with gitignore" >> git_fix.log 2>&1
echo Pushing... >> git_fix.log
git push -u origin main --force >> git_fix.log 2>&1
echo Done. >> git_fix.log
type git_fix.log
