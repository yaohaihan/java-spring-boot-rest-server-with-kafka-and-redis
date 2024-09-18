# aia-spring-auth-service

## Getting started with Spring Boot
https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/getting-started.html

## Application runner
Locally running application is configured through application.yaml located in `./codenow/config` directory. To override, use JVM options such as

```
-Dserver.port=8080 -Dspring.config.location=file:./codenow/config/application.yaml
```

## Deployment configuration
All files located in `./codenow/config` directory will be deployed alongside the application and available in `/codenow/config` directory

## Curl examples
```
curl -X GET \
 http://localhost:8080/hello-world 
```

## Githooks settings
```
git config core.hooksPath .githooks
```