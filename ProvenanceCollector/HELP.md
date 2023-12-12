# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.0/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.0/gradle-plugin/reference/html/#build-image)
* [Docker Compose Support](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#features.docker-compose)
* [Jersey](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#web.servlet.jersey)
* [Prometheus](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#actuator.metrics.export.prometheus)
* [Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/html5/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Docker Compose support
This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.

