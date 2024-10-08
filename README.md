# GraphQL Ktor + GraalVM Native Example

Example app showcasing the usage of [graphql-kotlin](https://github.com/ExpediaGroup/graphql-kotlin/) library to build
a GraphQL Ktor server that can be compiled to GraalVM native.

## Building locally

This project uses Gradle and you can build it locally using

```shell script
./gradlew clean build
```

## Running locally

* Run `com.mywallet.Application.kt` directly from your IDE
* Alternatively you can also use the Gradle application plugin by running `./gradlew run` from the command line.

Once the app has started you can explore the example schema by opening the GraphiQL IDE endpoint at http://localhost:8080/graphiql.

## GraalVM Native

### Building Native Image

In order to generate GraalVM native image we need to run following task

```shell
./gradlew nativeCompile
```

### Running Native Image

Once application is compiled by

### How to run neo4j
1. You could also use the docker run to start using neo4j, example:
```shell
 docker run \                                                                                                                                                                   ─╯
    --name testneo4j \
    -p7474:7474 -p7687:7687 \
    -d \
    -v $HOME/neo4j/data:/data \
    -v $HOME/neo4j/logs:/logs \
    -v $HOME/neo4j/import:/var/lib/neo4j/import \
    -v $HOME/neo4j/plugins:/plugins \
    --env NEO4J_AUTH=neo4j/password \
    neo4j:community-ubi8

```
2. You could also use the command: `$ docker-compose up -d  my-wallet-db`