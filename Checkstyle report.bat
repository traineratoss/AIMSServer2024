@echo off
 
echo Building the site. This might take a while...
call mvn site:site
echo.
echo Finished building site
timeout 5
cls
 
echo Start the site on http://localhost:9000. Do not close this window!
echo.
call mvn site:run