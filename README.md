# AJORM
Another Java ORM

## Introduction
When it came to using an ORM Lib in java I used ORMLite before. It worked quite well, but I didn't like the query builder.

Another thing was that I wanted to have control over the JDBC Wrapper to have a simple way of implementing an auto-reconnect function, if it gets disconnected for some reason. 

I finally decided to make an own ORM that fits my needs and here it is.

## Example usage

### Model
```java
@DatabaseTable("users")
class User {
  @DatabaseField(id = true, ai = true)
  int id;
  @DatabaseField
  String name;
  public User(){
    
  }
  public User(String name){
    this.name = name;
  }
}
```
### Repository
```java
class Database {
  public Table users;
  public Database(SQL sql){
    this.users = new Table<User,Integer>(sql,User.class);
  }
  public List<User> getAllUsers(){
    return users.queryForAll();
  }
}
```
### Usage
```java
//Create connection, initialize tables
Database db = new Database(new MySQL("localhost",3306,"mydb","myuser","changeme1234"));
//Print all usernames
for(User user : db.getAllUsers())
  System.out.println(user.name);
```

## Maven

### Repository
```xml
<respository>
  <id>bebendorf</id>
  <url>http://repo.bebendorf.eu/</url>
</repository>
```
### Dependency
```xml
<dependency>
  <groupId>eu.bebendorf</groupId>
  <artifactId>AJORM</artifactId>
  <version>1.0</version>
</dependency>
```
### Driver
You also need to add the driver you want to use. AJORM comes with a wrapper for com.mysql.jdbc.Driver, but you can also create an own wrapper implementing the interface SQL.
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
    <scope>compile</scope>
</dependency>
```

### JitPack
You can alternativly use jitpack if my repo is down or just not reliable enough for your needs.  
https://jitpack.io/#JanHolger/AJORM
