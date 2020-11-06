<p align="center"><img src="https://raw.githubusercontent.com/JavaWebStack/docs/master/src/assets/img/icon.svg" width="100">
<br><br>
JWS Object Relational Mapping
</p>

## Introduction
When it came to using an ORM Lib in java I used ORMLite before. It worked quite well, but I didn't like the query builder.

Another thing was that I wanted to have control over the JDBC Wrapper to have a simple way of implementing an auto-reconnect function, if it gets disconnected for some reason. 

I finally decided to make an own ORM that fits my needs and here it is.

## Documentation
Docs for version 2 will follow as soon as version 2 is fully stable.

### Example usage 

#### Model
```java
@Dates @SoftDelete
class User extends Model {
  @Column
  int id;
  @Column
  String name;
  @Column
  Timestamp createdAt;
  @Column
  Timestamp updatedAt;
  @Column
  Timestamp deletedAt;
}
```
#### Usage
```java
//Create connection, initialize repos
SQL sql = new MySQL("localhost",3306,"mydb","myuser","changeme1234");
ORMConfig config = new ORMConfig().setDefaultSize(255); //optional
Repo<User> repo = ORM.register(User.class, sql, config);

//Create the table if it doesn't exist (optional)
repo.migrate();

//Print all usernames
for(User user : Repo.get(User.class).all()){
    System.out.println(user.name);
}
```

## Maven

### Repository
```xml
<repository>
    <id>javawebstack</id>
    <url>https://repo.javawebstack.org</url>
</repository>
```
### Dependency
```xml
<dependency>
    <groupId>org.javawebstack</groupId>
    <artifactId>ORM</artifactId>
    <version>1.0-SNAPSHOT<!-- VERSION --></version>
</dependency>
```
#### or Jitpack
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.JavaWebStack</groupId>
    <artifactId>ORM</artifactId>
    <version>COMMIT_HASH</version>
</dependency>
```

### Driver
You also need to add the driver you want to use. JavaWebStack ORM comes with a wrapper for com.mysql.jdbc.Driver, but you can also create an own wrapper implementing the interface SQL. (For some reason I needed a pretty old driver version to connect to some servers, so I thought it would be better to link the old version here)
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.6</version>
    <scope>compile</scope>
</dependency>
```
