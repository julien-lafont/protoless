# Protobuf test case

To regenerate protobuf java classes for `schemas.proto`, run the following command:

```
cd modules/testing
protoc src/main/protobuf/schemas.proto --java_out=src/main/java
```
