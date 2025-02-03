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

And you're done! The commands are:

For simple key value pairs
```bash
# Sets key value
SET key value
#Deletes key alongside associated value
DELETE key
#Gets value associated with key
GET key
```

For concurrent Lists
```bash
# Pushes value to the front of list
LPUSH [list_name] value
#Pops and returns the first value in list
LPOP [list_name]
#Gets the size of a list
LSIZE [list_name]
```

For concurrent Sets
```bash
# Addes value to the Set
SADD [set_name] value
# removes value from the set
SREMOVE [set_name] value
# Returns a boolean of whether the value is in set
SCONTAINS [set_name] value
# Returns the size of the set
SSIZE [set]
```

For concurrentHashes
```bash
# Puts a key -> value pair into a hash
HPUT [hash_name] key value
# Gets the value associated with key from hash
HGET [hash_name] key
# Removes the value and key from hash
HDEL [hash_name] key
# Returns the size of the set
SSIZE [set]
```
Enjoy!

