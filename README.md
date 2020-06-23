# AJORM
Another Java ORM

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
class User {
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
AJORMConfig config = new AJORMConfig().setDefaultSize(255); //optional
Repo<User> repo = AJORM.register(User.class, sql, config);

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
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
### Dependency
```xml
<dependency>
    <groupId>com.github.JanHolger</groupId>
    <artifactId>AJORM</artifactId>
    <version>COMMIT_HASH</version>
</dependency>
```
### Driver
You also need to add the driver you want to use. AJORM comes with a wrapper for com.mysql.jdbc.Driver, but you can also create an own wrapper implementing the interface SQL. (For some reason I needed a pretty old driver version to connect to some servers, so I thought it would be better to link the old version here)
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.6</version>
    <scope>compile</scope>
</dependency>
```
