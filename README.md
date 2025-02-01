![Languages](https://img.shields.io/badge/Languages-Java-white) ![REDIS](https://img.shields.io/badge/REDIS-white) 

# REDIS

This is an implementation of REDIS in java, a type of NoSQL database.

# About The Project:
I had a lot of fun designing this REDIS, learning about concurrency and new thread-safe data structures. Feel free to use this! It's light weight and easy to use.

# Setup:
```bash
#Clone the repo
git clone https://github.com/Chieler/Redis.git
cd Redis/src
javac Server.java
java Server
```

```bash
#In a seperate terminal:
nc localost 6379
```

And your done! The commands are:

```bash
# Sets key value
SET key value
#Deletes key alongside associated value
DELETE key
#Gets value associated with key
GET key
```

Enjoy!

