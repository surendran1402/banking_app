@echo off
set MAVEN_VERSION=3.9.6
set MAVEN_HOME=%~dp0apache-maven-%MAVEN_VERSION%
if not exist "%MAVEN_HOME%" (
    echo Downloading Maven %MAVEN_VERSION%...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip', 'maven.zip'); Expand-Archive -Path maven.zip -DestinationPath . -Force; Remove-Item maven.zip"
)
"%MAVEN_HOME%\bin\mvn.cmd" %*
