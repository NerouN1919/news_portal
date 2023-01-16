# Новостной портал 
## Развёртывание базы данных в докер контейнере   
**Предварительно сделать docker login**    
  
В PowerShell вписать: 
- docker compose build 
- docker compose up -d
## Получение и работа с JWT  
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

