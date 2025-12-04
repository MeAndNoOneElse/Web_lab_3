@echo off
docker stop my-wildfly-container
docker rm my-wildfly-container
rmdir /S /Q "C:\Users\Eternal Core\OneDrive - MSFT\github_file\ITMO\CPPO\3_sem\Web_programming\labs\Лабораторная 3\lab_3.3\build"
call gradle clean war
docker-compose up -d
docker build -t my-wildfly-app .
docker run -d -p 8080:8080 -p 9990:9990 --name my-wildfly-container my-wildfly-app

