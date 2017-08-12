# protoless

[![Build Status](https://travis-ci.org/julien-lafont/protoless.svg)](https://travis-ci.org/julien-lafont/protoless)
[![GitHub tag](https://img.shields.io/github/tag/julien-lafont/protoless.svg)]()
[![Bintray](https://img.shields.io/bintray/v/julien-lafont/maven/protoless-core.svg)]()
[![Gitter](https://img.shields.io/gitter/room/julien-lafont/protoless.js.svg)]()

protoless is a [Protobuf 3](https://developers.google.com/protocol-buffers/docs/proto3) serialization
library in Scala for JVM, based on automatic type class derivation to perfectly fit with your models.

The type class derivation approach allows to generate `type-safe` [Encoders](https://julien-lafont.github.io/protoless/api/io/protoless/index.html/io/protoless/Encoder.html)
and [Decoders](https://julien-lafont.github.io/protoless/api/io/protoless/index.html/io/protoless/Encoder.html) for your own models,
without code-generation and or requiring proto3 schema file. The derivation is done with [Shapeless](https://github.com/milessabin/shapeless),
No macro were harmed in the making of this library.

Schema-free doesn't imply any loss of consistency. If you have one, you can still validate it at `compile-time` with yours models (*not implemented yet*).

protoless is heavily inspired by awesome work made on [Circe](http://circe.io) by Travis Brown, such that their public APIs share a lot in their design.

## QuickStart

protoless is published to [bintray.com/julien-lafont](https://bintray.com/julien-lafont/maven) and cross-built for `scala 2.11.8`, and `scala 2.12.3`, so you can just add the following to your build:

```scala
resolvers += Resolver.bintrayRepo("julien-lafont", "maven")

libraryDependencies += Seq(
  "io.protoless" %% "protoless-core" % "0.0.2",
  "io.protoless" %% "protoless-generic" % "0.0.2"
)
```

Then type `sbt console` to start a REPL and then paste the following:

```scala
import io.protoless._, io.protoless.syntax._, io.protoless.generic.auto._

case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])
// defined class Person

val p = Person("John", "Doe", Some(28), Seq("Paris", "London", "New York"))
// p: Person = Right(Person(John, Doe, Some(28), Seq(Paris, London, New York)

p.asProtobufBytes
// res0: Array[Byte] = Array(10, 4, 74, 111, 104, 110, 18, ...)

Decoder[Person].decode(p.asProtobufBytes)
// res1: Either[io.protoless.DecodingFailure, Person] = Right(Person(John, Doe, Some(28), Seq(Paris, London, New York)))

```

No boilerplate, no runtime reflection.

## Documentation

The full documentation is available here: [https://julien-lafont.github.io/protoless/](https://julien-lafont.github.io/protoless/).

## Why?

[ScalaPB](https://github.com/scalapb/ScalaPB), a protocol buffers compiler for scala, was the only serious library
if you want to work with protobuf in Scala, but it comes with:
 * Two step code generation (protobuf -> java, java -> scala)
   * And if you want to map your own model, you need a third wrapping level.
 * Heavy builder interface
 * Custom lenses library

protoless proposes a different approach, your lightweight models drive the protobuf serialization, **without weighing it down**.

## Progression

- [x] Encoding/decoding protobuf native fields.
- [x] Encoding/decoding scala native types (collections, bigdecimal, enum, etc).
- [x] Work with optional and `repeated` fields.
- [x] Support signed/unsigned/fixed int32/64 with tagging.
- [x] `Automatic` encoder/decoder for basic protobuf messages (fields numbered consecutively starting from one).
- [x] `Semi-automatic` encoder/decoder for message with fields not numbered consecutively.
- [x] Auto-derivation of `value class`.
- [x] Support nested message.
- [ ] Support default value.
- [ ] Compile time schema validation.
- [ ] And last, but not least, [GRPC](https://grpc.io/) integration.
