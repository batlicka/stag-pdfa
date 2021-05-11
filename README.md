# Stag-pdfa

How to start the stag-pdfa application
---

1. Open command line and using command `mvn -version` check if you have installed Maven locally. If is returned versin
   of Maven all is ok. If is returned something like: "command line is not recognized", then is needfull to install
   Maven. Following link can be helpfull.
   https://mkyong.com/maven/how-to-install-maven-in-windows/
2. Run `mvn clean install` to build your application
3. Start application with `java -jar target/dropwizard-1.0-SNAPSHOT.jar server config.yml`
4. To check that your application is running enter url `http://localhost:8080/api/ok`

Endpoints
---

1. GET /api/ok

- Can be test using curl command: `curl localhost:8080/api/ok`

2. POST /api/validate/auto

- Can be test using curl command: `curl -s -F"file=@D:\dokumenty\test.pdf" localhost:8080/api/validate/auto`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
