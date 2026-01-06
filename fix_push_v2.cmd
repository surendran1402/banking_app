@echo off
echo Cleaning up git state... > git_fix_output.txt
git reset >> git_fix_output.txt 2>&1
echo Adding files (respecting .gitignore)... >> git_fix_output.txt
git add . >> git_fix_output.txt 2>&1
echo Committing... >> git_fix_output.txt
git commit -m "Project structure with gitignore" >> git_fix_output.txt 2>&1
echo Pushing... >> git_fix_output.txt
git push -u origin main --force >> git_fix_output.txt 2>&1
echo Done. >> git_fix_output.txt
type git_fix_output.txt
