# java-dotenv 

![](https://img.shields.io/badge/build-passing-green.svg)![](https://img.shields.io/badge/tests-passing-green.svg) ![](https://img.shields.io/badge/license-Apache%202.0-blue.svg)

<img src="https://raw.githubusercontent.com/cdimascio/java-dotenv/master/assets/java-dotenv.png" alt="dotenv" align="right" /> 

Dotenv is a zero-dependency module that loads environment variables from a `.env`. Storing configuration in the environment separate from code is based on The [The Twelve-Factor App](http://12factor.net/config) methodology.

**Note:** Java does not provide a way to set environment variables on a currently running process. Thus, once `java-dotenv` is configured, you can use the `dotenv.get("...")` API to get environment variables, instead of `System.getenv(...)`. `dotenv`  should be used to retrieve all environment variables. Those listed in `.env` will override those in the the environment.  
## Install

### Maven 
```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>java-dotenv</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Gradle

```
compile 'io.github.cdimascio:java-dotenv:1.0.2'
```


## Usage

### Create a `.env` file

```dosini
# formatted as key=value
MY_ENV_VAR1=My first env var configure dotenv
MY_EVV_VAR2=My second env var
```

### Kotlin
#### Configure java-dotenv 
Configure `java-dotenv` once in your application.

```kotlin
val dotenv = Dotenv
        .configure()
        .directory("./") // set to the current directory
        .ignoreIfMalformed() // do not throw an error if .env is malformed
        .ignoreIfMissing() // do not throw an error if .env is missing
        .build()
```
	
#### Get an environment variable
Note, environment variables specified in `.env` take precedence over those configured in the actual environment.

```kotlin
dotenv["MY_ENV_VAR1"]
```


### Java
#### Configure java-dotenv
Configure `java-dotenv` once in your application.

```java
Dotenv dotenv = Dotenv.Instance
        .configure()
        .directory("./src/test/resources")
        .ignoreIfMalformed()
        .build();
```

#### Get an environment variable
Note, environment variables specified in `.env` take precedence over those configured in the actual environment.

```java
dotenv.get("MY_ENV_VAR1");
```

## Configuration options

### *required* `directory(path: String)` 
`path` specifies the directory containing `.env`. Dotenv first searches for `.env` using the given path on the filesystem. If not found it searches the given path on the classpath.

### *optional* `ignoreIfMalformed()`

Do not throw when `.env` entries are malformed. Malformed entries are skipped.

### *optional* `ignoreIfMissing()` 

Do not throw when `.env` does not exist. Dotenv will continue to retrieve environment variables that are set in the environment e.g. `dotenv["HOME"]`

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)