# java-dotenv 

![](https://travis-ci.org/cdimascio/java-dotenv.svg?branch=master) ![](https://img.shields.io/badge/status-stable-green.svg) ![](https://img.shields.io/badge/coverage-94%25-blue.svg) ![](https://img.shields.io/badge/license-Apache%202.0-blue.svg)

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
Use dotenv.get("...") instead of Java's System.getenv(...). Here's [why](#faq).

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

- see [Kotlin DSL configuration options](#configuration-options)

### Get environment variables
Note, environment variables specified in the host environment take precedence over those in `.env`.

With **Java**

```java
dotenv.get("MY_ENV_VAR1");
dotenv.get("HOME");
```

or with **Kotlin**

```kotlin
dotenv["MY_ENV_VAR1"]
dotenv["HOME"]
```

## Configuration options

### *optional* `directory` 
* `path` specifies the directory containing `.env`. Dotenv first searches for `.env` using the given path on the filesystem. If not found, it searches the given path on the classpath. If `directory` is not specified it defaults to searching the current working directory on the filesystem. If not found, it searches the current directory on the classpath.

	**Java example**
	
	```java
	Dotenv
	  .configure()
	  .directory("/some/path")
	  .load()
	```

	**Kotlin Dsl example**
	
	```java
	dotenv {
	  directory = "/some/path"
	}
    ```


### *optional* `ignoreIfMalformed`

* Do not throw when `.env` entries are malformed. Malformed entries are skipped.

	**Java example**
	
	```java
	Dotenv
	  .configure()
	  .ignoreIfMalformed()
	  .load()
	```
	**Kotlin Dsl example**
	
	```java
	dotenv {
	  ignoreIfMalformed = true
	}
	```

### *optional* `ignoreIfMissing` 

* Do not throw when `.env` does not exist. Dotenv will continue to retrieve environment variables that are set in the environment e.g. `dotenv["HOME"]`

	**Java example**
	
	```java
	Dotenv
	  .configure()
	  .ignoreIfMissing()
	  .load()
	```
	**Kotlin Dsl example**
	
	```java
	dotenv {
	  ignoreIfMissing = true
	}
	```


## Examples

- with [Spring Framework](https://github.com/cdimascio/kotlin-swagger-spring-functional-template) 
- see [Kotlin DSL tests](./src/test/kotlin/tests/DslTests.kt)
- see [Java tests](./src/test/java/tests/JavaTests.java)

## FAQ

**Q:** Why should I use `dotenv.get("MY_ENV_VAR")` instead of `System.getenv("MY_ENV_VAR")`

**A**: Since Java does not provide a way to set environment variables on a currently running process, vars listed in `.env` cannot be set and thus cannot be retrieved using `System.getenv(...)`.

**Q**: Should I commit my `.env` file?

**A**: No. We strongly recommend against committing your `.env` file to version control. It should only include environment-specific values such as database passwords or API keys. Your production database should have a different password than your development database.

**Q**: What happens to environment variables that were already set?

**A**: java-dotenv will never modify any environment variables that have already been set. In particular, if there is a variable in your `.env` file which collides with one that already exists in your environment, then that variable will be skipped. This behavior allows you to override all `.env` configurations with a machine-specific environment, although it is not recommended.

**Q**: What about variable expansion in `.env`? 

**A**: We haven't been presented with a compelling use case for expanding variables and believe it leads to env vars that are not "fully orthogonal" as [The Twelve-Factor App outlines](https://12factor.net/config). Please open an issue if you have a compelling use case.


**Note and reference**: The FAQs present on [motdotla's dotenv](https://github.com/motdotla/dotenv#faq) node project page are so well done that I've included those that are relevant in the FAQs above.

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

