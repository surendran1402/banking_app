@echo off
echo Starting fresh git history... > fresh_start.log
git checkout --orphan new_main >> fresh_start.log 2>&1
echo Adding files... >> fresh_start.log
git add . >> fresh_start.log 2>&1
echo Committing... >> fresh_start.log
git commit -m "Initial commit of full project" >> fresh_start.log 2>&1
echo Deleting old main... >> fresh_start.log
git branch -D main >> fresh_start.log 2>&1
echo Renaming branch... >> fresh_start.log
git branch -m main >> fresh_start.log 2>&1
echo Pushing... >> fresh_start.log
git push -u origin main --force >> fresh_start.log 2>&1
echo Done. >> fresh_start.log
type fresh_start.log
