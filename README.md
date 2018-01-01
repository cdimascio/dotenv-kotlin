# java-dotenv 

![](https://img.shields.io/badge/build-passing-green.svg) ![](https://img.shields.io/badge/tests-passing-green.svg) ![](https://img.shields.io/badge/coverage-94%25-blue.svg) ![](https://img.shields.io/badge/license-Apache%202.0-blue.svg)

<img src="https://raw.githubusercontent.com/cdimascio/java-dotenv/master/assets/java-dotenv.png" alt="dotenv" align="right" /> 

A zero-dependency Java port of the Ruby dotenv project (which loads environment variables from a `.env` file). java-dotenv also offers a [Kotlin DSL](#kotlin-dsl).

From the original Library:

>Storing configuration in the environment is one of the tenets of a [twelve-factor](http://12factor.net/config) app. Anything that is likely to change between deployment environments–such as resource handles for databases or credentials for external services–should be extracted from the code into environment variables.

>But it is not always practical to set environment variables on development machines or continuous integration servers where multiple projects are run. Dotenv load variables from a .env file into ENV when the environment is bootstrapped.

Environment variables listed in the host environment override those in `.env`.  

Use `dotenv.get("...")` instead of Java's `System.getenv(...)`.  

## Install

### Maven 
```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>java-dotenv</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Gradle

```groovy
compile 'io.github.cdimascio:java-dotenv:3.0.0'
```

## Usage

Create a `.env` file in the root of your project

```dosini
# formatted as key=value
MY_ENV_VAR1=some_value
MY_EVV_VAR2=some_value
```

With **Java**

```java
import io.github.cdimascio.dotenv.Dotenv;

Dotenv dotenv = Dotenv.load();
dotenv.get("MY_ENV_VAR1")
```

or with **Kotlin**

```kotlin
import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()
dotenv["MY_ENV_VAR1"]
```

## Advanced Usage

### Configure
Configure `java-dotenv` once in your application. 

With **Java**

```java
Dotenv dotenv = Dotenv.configure()
        .directory("./some/path")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();
```

- see [configuration options](#configuration-options)

or with **Kotlin**

```kotlin
val dotenv = dotenv {
    directory = "./some/path"
    ignoreIfMalformed = true
    ignoreIfMissing = true
}
```

- see [Kotlin DSL configuration options](#kotlin-dsl-configuration-options)

### Get environment variables
Note, environment variables specified in the host environment take precedence over those in `.env`.

With **Java**

```java
dotenv.get("MY_ENV_VAR1");
dotenv.get("HOME");
```

or with **Kotlin**

```kotline
dotenv["MY_ENV_VAR1"]
dotenv["HOME"]
```

## Configuration options

### *optional* `directory(path: String)` 
`path` specifies the directory containing `.env`. Dotenv first searches for `.env` using the given path on the filesystem. If not found, it searches the given path on the classpath. If `directory` is not specified it defaults to searching the current working directory on the filesystem. If not found, it searches the current directory on the classpath.

### *optional* `ignoreIfMalformed()`

Do not throw when `.env` entries are malformed. Malformed entries are skipped.

### *optional* `ignoreIfMissing()` 

Do not throw when `.env` does not exist. Dotenv will continue to retrieve environment variables that are set in the environment e.g. `dotenv["HOME"]`


## Kotlin Dsl Configuration Options


### *optional* `directory: String` 
Specifies the directory containing `.env`. Dotenv first searches for `.env` using the given path on the filesystem. If not found, it searches the given path on the classpath. If `directory` is not specified it defaults to searching the current working directory on the filesystem. If not found, it searches the current directory on the classpath.

### *optional* `ignoreIfMalformed: Boolean`

Do not throw when `.env` entries are malformed. Malformed entries are skipped.

### *optional* `ignoreIfMissing: Boolean` 

Do not throw when `.env` does not exist. Dotenv will continue to retrieve environment variables that are set in the environment e.g. `dotenv["HOME"]`

## Examples

- with [Spring Framework](https://github.com/cdimascio/kotlin-swagger-spring-functional-template) 
- see [Kotlin DSL tests](./src/test/kotlin/DslTests.kt)
- see [Java tests](./src/test/java/JavaTests.java)

## FAQ

**Q:** Why should I use `dotenv.get("MY_ENV_VAR")` instead of `System.getenv("MY_ENV_VAR")`

**A**: Since Java does not provide a way to set environment variables on a currently running process, vars listed in `.env` cannot be set and thus cannot be retrieved using `System.getenv(...)`.

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)