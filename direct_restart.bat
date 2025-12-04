@echo off

@REM Удалил билд и сделал новый
cd "C:\Users\Eternal Core\OneDrive - MSFT\github_file\ITMO\CPPO\3_sem\Web_programming\labs\Лабораторная 3\lab_3.3> "
rmdir /S /Q "build"
call gradle build

@REM копируем в папку сервера и запускаем
copy /Y "build\libs\lab_3.3.war" "C:\Users\Eternal Core\Downloads\wildfly-35.0.0.Final\wildfly-35.0.0.Final\standalone\deployments\lab_3.3.war"
cd  "C:\Users\Eternal Core\Downloads\wildfly-35.0.0.Final\wildfly-35.0.0.Final\bin"
./standalone.bat
