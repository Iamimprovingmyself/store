<h1 align="center">Привет, это  <a href="https://t.me/tryingescape" target="_blank">мое</a> тестовое задание 
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
 
<h3 align="center">Техническое задание:</h3>
Реализовать необходимые шаги тест-кейса для сервиса https://petstore3.swagger.io/, используя Cucumber.
 
Сценарий:
1.      Создать заказ
2.      Получить заказ по идентификатору
3.      Частично сравнить полученные данные с отправленными параметрами
4.      Удалить созданный заказ
 
Особенности:
1.      Не использовать классы-контейнеры для формирования тела POST запроса и разбора ответа.
2.      Шаблон тела запроса считать из файла


  Сценарии лежат в <a href="https://github.com/Iamimprovingmyself/store/tree/main/src/test/resources/features" target="_blank">features<a/>
  
  
  Использовано
  1. Rest Assured
  2. Junit-4
  3. Allure
  4. Cucumber
  5. Surefire
  

шаги описаны в <a href="https://github.com/Iamimprovingmyself/store/blob/main/src/test/java/ru/sogaz/steps/PetStoreSteps.java">Steps<a/>     
менеджер для управления переменными и константами <a href="https://github.com/Iamimprovingmyself/store/blob/main/src/main/java/manager/TestPropManager.java">manager<a/>      
в папке <a href="https://github.com/Iamimprovingmyself/store/tree/main/src/test/java/ru/sogaz/utils">utils<a/> вспомогательные классы

из-за недостаточно подробного тз реализовал на скорую руку отчеты,без выноса их в отдельные Listener классы    
так же сделал каркас для для настройки общего состояния перед запуском тестов, но затем закомментировал)) (классы Hook и Config)    
по этой же причине сделал более простую реализацию передачи идентификатора из одного метода в другой(сделал не с сохранением в контекст в отдельном шаге и перадачей в последующем шаге как переменную) (utility context class)

Для запуска тестов использовать команду:                        
clean test "-Dcucumber.options=--tags @PetStore" allure:serve

слово "мое" в шапке  кликабельно и ведет в лс в телеграмме для оперативной связи)
