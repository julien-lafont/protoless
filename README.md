# protoless

protoless is a [Protobuf 3](https://developers.google.com/protocol-buffers/docs/proto3) serialization/deserialization
library for the JVM, based on automatic type class derivation instead of code generation.

The Type class derivation approach allows to generate [Encoders](#) and [Decoders](#) for your own models **without protobuf schema**.
But you can still validate yours models with a schema at compile-time if you want to enforce consistency (*not implemented yet*).

protoless is heavily inspirated by the awesome work made on [[Circe]](http://circe.io), such that their public APIs follows the same design.

## QuickStart

protoless is published to *TODO* and cross-built for Scala 2.11, and 2.12, so you can just add the following to your  build:

```scala
libraryDependencies += "io.protoless" %% "protoless-core" % "0.0.1"
```

Then type sbt console to start a REPL and then paste the following:


```scala
import io.protoless._, io.protoless.syntax._, io.protoless.generic.auto._

case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])
// defined class Person

val p = Person("John", "Doe", Some(28), Seq("Paris", "London", "New York"))
// p: Person = Right(Person(John, Doe, Some(28), Seq(Paris, London, New York)

p.asProtobufBytes
// res0: Array[Byte] = Array(10, 4, 74, 111, 104, 110, 18, ...)

Decoder[Person].decode(p.asProtobufBytes)
// res0: Either[io.protoless.DecodingFailure, Person] = Right(Person(John, Doe, Some(28), Seq(Paris, London, New York)

```

No boilerplate, no runtime reflection, no runtime cost.

## Why?

[ScalaPB](https://github.com/scalapb/ScalaPB) is currently the only serious library to work with protobuf protocol in Scala.


## Dependencies and modularity

circe depends on cats instead of Scalaz, and the core project has only one dependency (cats-core).

Other subprojects bring in dependencies on Jawn (for parsing in the jawn subproject), Shapeless
(for automatic codec derivation in generic), but it would be possible to replace the functionality
provided by these subprojects with alternative implementations that use other libraries.
