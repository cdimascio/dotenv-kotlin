# java-dotenv 


Dotenv is a zero-dependency module that loads environment variables from a `.env`. Storing configuration in the environment separate from code is based on The Twelve-Factor App methodology.

**Note:** Java does not provide a way to set environment variables on a currently running process. Thus, once `java-dotenv` is configured, you can use the `dotenv.get("...")` API to get environment variables, instead of `System.getenv(...)`.

## Usage

### Create a `.env` file

```
# formatted as key=value
MY_ENV_VAR1=My first env var with dotenv
MY_EVV_VAR2=My second env var
```

### Kotlin
#### Configure java-dotenv 
Configure `java-dotenv` once in your application.

```kotlin
    val dotenv = Dotenv
            .configure()
            .useDirectory("./src/test/resources")
            .ignoreIfMalformed()
            .build()
```
	
#### Get an environment variable
Note, environment variables specified in `.env` take precedence over those configured in the actual environment.

```
	dotenv["MY_ENV_VAR1"]
```

### Java
#### Configure java-dotenv
Configure `java-dotenv` once in your application.

```java
    Dotenv dotenv = Dotenv
            .configure()
            .useDirectory("./src/test/resources")
            .ignoreIfMalformed()
            .build();
```

#### Get an environment variable
Note, environment variables specified in `.env` take precedence over those configured in the actual environment.

```
	dotenv.get("MY_ENV_VAR1");
```

## License
Apache 2.0