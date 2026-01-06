@echo off
echo Starting Git Push Process > git_log.txt
git remote -v >> git_log.txt 2>&1
echo Setting remote URL... >> git_log.txt
git remote set-url origin https://github.com/surendran1402/banking_app >> git_log.txt 2>&1
if %errorlevel% neq 0 (
    echo Remote origin not found or error, trying to add... >> git_log.txt
    git remote add origin https://github.com/surendran1402/banking_app >> git_log.txt 2>&1
)
echo Adding files... >> git_log.txt
git add . >> git_log.txt 2>&1
echo Committing... >> git_log.txt
git commit -m "Pushing full project to new repo" >> git_log.txt 2>&1
echo Branching... >> git_log.txt
git branch -M main >> git_log.txt 2>&1
echo Pushing... >> git_log.txt
git push -u origin main >> git_log.txt 2>&1
echo Done. >> git_log.txt
type git_log.txt
