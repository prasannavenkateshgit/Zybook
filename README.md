## Connecting the Application to the Database

Edit the `application.properties` file with the following configuration:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=testdb;trustServerCertificate=true
spring.datasource.username=<DB_User>
spring.datasource.password=<DB_Password>
```
Make sure to replace `<DB_User>` and `<DB_Password>` with your actual database username and password.

## Modifying the Database Schema

### Create a Schema Migration File

Create a file in your `db/migrations` directory. This file should contain the SQL commands to set up your database schema. For example, name it `V1_create_schema.sql`.

### Update the Changelog

Append the file path to your `changelog.xml` by including the following line:

```xml
<include file="db/migrations/V1_create_schema.sql"/>
```
