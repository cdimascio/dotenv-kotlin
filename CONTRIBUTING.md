# Contributing

#### 1. Fork it

#### 2. Build the project

```shell
mvn build
```

#### 3. Make a change

fix a bug, add a feature, update the doc, etc

#### 4. Run the Tests

```shell
mvn test
```

#### 5. Create a PR

#### Add yourself as a contributor

after your Pr has been merged, add yourself as a contributor by creating a comment like the following on your PR:

@all-contributors please add @username for code

replace code with doc or test or infra or some combination depending on your contribution.

#### Package

Contributors are not responsible for pushing packages to mavencentral and jcenter. Contributors are responsible for validating that the package step succeeds.

```shell
mvn clean package dokka:javadocJar
```
