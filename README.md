# CgiPocDropwizard

A simple Dropwizard 1.0.5 project exposing REST API to store assets. The 
application is using MySql as a RDBMS.

Database settings are in the config.yml file. To switch to another RDBMS one 
should modify the driver and connection URL. By default it is necessary to create 
CgiPocDropwizard database and it will be populated using Liquibase migrations.  
  
  
How to start the CgiPocDropwizard application
---

1. Create a key store in the project's folder using Java 8 *keytool* 
`keytool -genkeypair -keyalg RSA -dname "CN=localhost" -keystore cgi-poc-dw.keystore -keypass p@ssw0rd -storepass p@ssw0rd`
2. Run `mvn clean package` to build the application
3. To populate the database first ensure that the config.yml file is matching with the database configs, 
then run `java -jar target/cgi-poc-dw-1.0-SNAPSHOT.jar db migrate -i TEST config.yml` 
4. Start application with `java -jar target/cgi-poc-dw-1.0-SNAPSHOT.jar server config.yml`
5. To check that your application is running, enter URL `http://localhost:8081` in the browser 
  
How to try the CgiPocDropwizard application
---
  
The API is secured with Basic Authentication. One can find a user *cgi*
whose password is *p@ssw0rd* along with several assets in the database after 
executing the migrations.

To get the list of all assets stored by *cgi* user enter

~~~~
curl -w "\n" 2>/dev/null -k https://localhost:8443/assets -u cgi:p@ssw0rd
~~~~

To get the data stored for an asset with id == 1 type

~~~~
 curl -w "\n" 2>/dev/null -k https://localhost:8443/assets/1 -u cgi:p@ssw0rd
~~~~

To add an asset it is necessary to key in 

~~~~
curl -X POST -w "\n" 2>/dev/null -k https://localhost:8443/assets \
 -u cgi:p@ssw0rd -H "Content-Type: application/json" \
 -d '{"url":"http://github.com", "description":"A lot of great projects"}'
~~~~

To modify an asset the API offers PUT method

~~~~
curl -X PUT -w "\n" 2>/dev/null -k https://localhost:8443/assets/1 -u cgi:p@ssw0rd \
 -H "Content-Type: application/json" -d '{"url":"https://github.com/cgi/DwAssets"}'
~~~~

To delete an asset use 

~~~~
curl -X DELETE -w "\n" 2>/dev/null -k https://localhost:8443/assets/1 -u cgi:p@ssw0rd
~~~~
