# Contributing

#### Fork it

#### Build the project

```shell
mvn build
```

#### Make a change

fix a bug, add a feature, update the doc, etc

#### Run the Tests

```shell
mvn test
```

#### Create a PR

#### Package

Contributors are not responsible for pushing packages to mavencentral and jcenter. Contributors are responsible for validating that the package step succeeds.

```shell
mvn clean package dokka:javadocJar
```
