Shortener
============

    Shortener - Java Web Application with embedded Tomcat Http server.
This module is part of complex microservice application and represents
it's main idea - URL's simplifications.
To simplify url you will need to do three simple steps:

- push long URL like http://www.tschudishipmanagement.com/page/995/Vacancies_Ashore to the App;
- get short response like md.com/abcde (md.com - App domain host name, abcde - unique key);
- share and get access to stored long URL by simple call to md.com/abcde.

Use java -server -jar shortener.jar to run App.
To build jar file execute mvn package.
To pass through integration test mvn verify.





Features
========

- Single Jar Application;
- Powered by Spring Boot;
- Based on Redis to speed up access in high-load case;
- API for most of url's routine;
- Covered by Unit and Integration Tests;
- Swagger integration.



Server configuration
--------------------

| Field | Default value | Meaning |
|-------|:-------------:|---------|
| shorturl.lenght | 5 | Short url length|
| server.context-path | /us | Context path of the application |
| server.port | 8080 | Port the server will be listening at |
| redis.hostname | localhost | Redis server hostname location |
| redis.port | 6379 | Port the Redis server will be listening at |



License
=======

GNU General Public License
