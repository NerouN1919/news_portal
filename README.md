# Новостной портал 
## Для запуска приложения на сервере  
**Склонировать репозиторий**  
**Предварительно сделать docker login**    
**Также следует перейти в директорию с проектом**    
В PowerShell вписать: 
- `docker compose build` 
- `docker compose up`  
## Для запуска приложения в IDE  
**Изменить docker-compose.yml**  
```
version: '3.1'
volumes:
  pg_timeweb:
services:
  pg_db:
    image: 'postgres:latest'
    restart: always
    environment:
      - POSTGRES_PASSWORD=admin
      - POSTGRES_USER=admin
      - POSTGRES_DB=portal
    ports:
      - 5455:5432
```  
И выполнить в директории проекта в PowerShell:
  - `docker compose build` 
  - `docker compose up`  
      
## Получение и работа с JWT  
Время жизни токена - **5 минут**  
Для получения сделать POST запрос: "http://localhost:8080/api/auth/login"  
Тело запроса смотреть в документации к API    
login : **SviridenkoAdmin**  
password : **12345**  
Или:  
login : **MorozovAdmin**  
password : **12345**  

-------------------------  
Полученные JWT вставлять в headers запросов  
## Просмотр документации по использованию REST API
  
В браузере ввести: http://localhost:8080/swagger-ui/index.html  
## Диаграмма базы данных  
![изображение](https://user-images.githubusercontent.com/99546572/218124166-d4042d32-0ba8-451d-be9d-89ec64dea733.png)   
-------------------------
-------------------------   
Используемый стек технологий:  
  - Java 8
  - Spring Data JPA
  - Spring MVC
  - Spring Security
  - Hibernate
  - JWT-api
  - Jackson
  - PostgreSQL    
-------------------------
Используемый стек технологий для тестирования:
  - JUnit
  - Mockito
  - H2 database    
-------------------------
Используемые технологии для запуска:
  - Docker(docker compose)
  
