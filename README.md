# jsonrpclite

This is a websocket json-rpc & subscribe push library

## maven

```xml
<dependency>
    <groupId>io.github.qyvlik</groupId>
    <artifactId>jsonrpclite-core</artifactId>
    <version>1.1.2</version>
</dependency>
```

## deploy

```bash
export GPG_TTY=$(tty)
mvn clean deploy -Prelease -Dmaven.test.skip=true
```
