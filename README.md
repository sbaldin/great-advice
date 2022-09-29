# Great Advice Parser

Kotlin console utility to gather and parse funny advices from fucking-great-adivce site.

Project consists of 4 project:
1. advice-ai - dl4j project to generate new advices that based on existing.
2. advice-core - common things like dat 
3. advice-parser - parser that uses vk api to gather url and than goes to site to parse them
4. advice-rest - api that setup endpoints to provide advies


## Getting Started

You can run following Gradle command to start gather:
```
 ./gradlew runShadow
```