@echo off

@REM Удалил билд и сделал новый
cd "C:\Users\Eternal Core\OneDrive - MSFT\github_file\lab_3.4"
rmdir /S /Q "build"
call gradle build

@REM копируем в папку сервера и запускаем
copy /Y "build\libs\lab_3.4.war" "C:\Users\Eternal Core\Desktop\wildfly-35.0.0.Final\standalone\deployments\lab_3.4.war"
cd "C:\Users\Eternal Core\Desktop\wildfly-35.0.0.Final\bin"
.\standalone.bat
