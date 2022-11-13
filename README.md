# Great Advice Parser

Kotlin console utility to gather and parse funny advices from fucking-great-adivce site.

Project consists of 4 project:
1. advice-ai - dl4j project to generate new advices that based on existing.
2. advice-core - common things like data classes
3. advice-db - repository, tables, db configs
4. advice-parser - parser that uses vk api to gather url and than goes to site to parse them
5. advice-rest - spring rest service that exposes advise REST API 
6. advice-grpc - ktor grpc/http service that used as playground to touch http/2 and ktor  


## Getting Started

You can run following Gradle command to start gather:
```shell
 ./gradlew runShadow
```



## DB configuration

We use postgres to make CRUD operations on advice objects. Here is docker run command to start postgres-db:

```shell
docker run -itd -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=passw0rd -p 5432:5432 -v /data:/var/lib/postgresql/data --name postgresql_advice postgres
```

The above command uses environment variables POSTGRES_USER and POSTGRES_PASSWORD to set the username and password for the PostgreSQL database. By default, the PostgreSQL database runs on the 5432 port. We exposed the 5432 port on the host using the “-p 5432:5432” in the docker run command.
To back up the data, we also mounted the /var/lib/postgresql/data directory to the /data directory of the host machine of the postgres container.


## Health check of advice-rest
Spring Boot provides several such services (such as health, audits, beans, and more) with its [actuator module](https://docs.spring.io/spring-boot/docs/2.5.0/reference/htmlsingle/#production-ready).

The actuator exposes the following endpoints:
* actuator/health
* actuator

