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

Configuration
---
Configuration is in config.yml

- `exceptions` - These items define rule exceptions. Two approaches to defining exceptions can be used: Either define
  entire rules in exceptions, eg `6.1.2`, or define exceptions more specifically using a rule and a subset, eg `6.1.2-1`
  .
- `pathToSentFilesFolder` - Define path, where will be saved validated file.
- `urlToVeraPDFrest` - Define path to endpoint of veraPDF-rest, where will be validated PDF file sent for performing
  validation.
- `databaseUrlJdbc` - Define an access to database and database file.
- `cleanDatabaseTableAtStart` - Define, whether the content of database table will be deleted the next time you run
  stag-pdfa, or not.
- `testSwitch` - Define if application will run in some of the test modes. `{f31, f32, f4, f5, f6}`. Parameter `deff`
  define normal mode without tests.
- `inputStramProcessor` - Define, whether for processing a request will be used class InputStramProcessor1 or
  InputStramProcessor2.
- `javaMail` - Parameters for sendig an emails.
   - `#user` -Defines the username under which it will be possible to log in to the email server.
   - `#pass` - Define password for logint ot email server.
   - `#from` - Define eamil of sender.
   - `#to` - Define email of recipient.
   - `#host` - Define email server.
   - `#port` - Define port.
   - `#authentization` - `true` - For sending an email is forced authentization connection to email server. `false` -
     Emails will be sent without authentization to email server.