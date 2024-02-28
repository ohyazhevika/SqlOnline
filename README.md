# SOQOL-онлайн

Веб-приложение SOQOL Онлайн позволяет выполнять SQL-запросы к пользовательским базам данных SOQOL. 
Выбор любой версии сервера СУБД Сокол, создание отдельной базы для пользователя и назначение ему в ней роли DBA позволяют выполнять большое множество запросов,
необходимых в реальных приложениях баз данных.
Возможность создания нескольких пользователей в своей базе дает возможность увидеть, как наличие тех или иных системных и объектных привилегий влияет на его доступ к объектам различных схем БД.

## Как предоставить веб-приложению доступ к новому серверу БД
Для предоставления веб-приложению возможности подключения к серверу баз данных, создания на нем пользовательских баз данных и их удаления необходимо:
- Добавить в application.yml описание конфигурации добавляемого сервера
- Предоставить реализацию интерфейса DatabaseServer для данного сервера.


### О конфигурации сервера БД
 В файле application.yml хранятся параметры настройки всех серверов баз данных,
 к которым конечный пользователь может выполнить подключение. 
 Для каждого сервера БД, регистрируемого приложением, необходимо указание следующих параметров:
 - Полное название класса JDBC драйвера, необходимого для создания подключения и запросов к серверу в коде веб-приложения
 - Полный путь к jar-файлу, содержащему имплементацию интерфейса Driver для регистрируемого сервера
 - Имя хоста и порт, на котором будет установлен сервер
 - Наименование версии сервера, отображаемое пользователям веб-сервиса


 ### Чем плоха текущая реализация веб-приложения SOQOL-онлайн:
- Адреса серверов захардкожены в основной, управляющей части кода
- Имя баз данных, выделяемых пользователю, а также имя и пароль пользователя сервиса как пользователя БД захардкожены в управляющей части приложения
- Пока что нет возможности работы с серверами других вендоров СУБД
- Нет никакого отслеживания того, как потребляются базами такие ресурсы приложения, как память и диск
- Криво обрабатываются результаты SQL-запросов.
