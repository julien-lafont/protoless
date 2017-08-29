# protoless

[![Travis](https://img.shields.io/travis/julien-lafont/protoless/master.svg)](https://travis-ci.org/julien-lafont/protoless)
[![GitHub tag](https://img.shields.io/github/tag/julien-lafont/protoless.svg)](https://github.com/julien-lafont/protoless/releases)
[![Gitter](https://img.shields.io/gitter/room/julien-lafont/protoless.js.svg)](https://gitter.im/protoless/Lobby)

protoless is a [Protobuf 3](https://developers.google.com/protocol-buffers/docs/proto3) serialization
library in **Scala** for JVM, based on automatic type class derivation to perfectly fit your models.

The type class derivation approach allows to generate `type-safe` [Encoders](https://julien-lafont.github.io/protoless/api/io/protoless/Decoder.html)
and [Decoders](https://julien-lafont.github.io/protoless/api/io/protoless/Encoder.html) at `compile-time` for your own models,
without code-generation. The derivation is done with [Shapeless](https://github.com/milessabin/shapeless),
No macro were harmed in the making of this library.

`Schema-free` doesn't imply any loss of consistency. If you have one, you can still validate it at compile-time with yours models (*not implemented yet*).

protoless is heavily inspired by awesome work made on [Circe](http://circe.io) by Travis Brown, so the design of their public APIs has a lot in common.

## QuickStart

protoless is published to [bintray.com/julien-lafont](https://bintray.com/julien-lafont/maven) and cross-built for `scala 2.11.8`, and `scala 2.12.3`, so you can just add the following to your build:

```scala
resolvers += Resolver.bintrayRepo("julien-lafont", "maven")

libraryDependencies += Seq(
  "io.protoless" %% "protoless-core" % "0.0.6",
  "io.protoless" %% "protoless-generic" % "0.0.6"
)
```

Type `sbt console` to start a REPL and then paste the following the following code:

```scala
import io.protoless._, io.protoless.generic.auto._

case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])

val p = Person("John", "Doe", Some(28), Seq("Paris", "London", "New York"))
// p: Person = Right(Person(John, Doe, Some(28), Seq(Paris, London, New York)

val byte = Encoder[Person].encode(p) // or p.asProtobufBytes
// bytes: Array[Byte] = Array(10, 4, 74, 111, 104, 110, 18, ...)

Decoder[Person].decode(bytes) // or bytes.as[Person]
// res1: Either[DecodingFailure, Person] = Right(Person(John, Doe, Some(28), Seq(Paris, London, New York)))
```

No boilerplate, no runtime reflection.

## Documentation

The full documentation is available here: [https://julien-lafont.github.io/protoless](https://julien-lafont.github.io/protoless/).
 - [Library design](https://julien-lafont.github.io/protoless/design.html)
 - [Message Encoder & Decoder](https://julien-lafont.github.io/protoless/message.html)
 - [Field Encoder & Decoder](https://julien-lafont.github.io/protoless/field.html)

## Why?

[ScalaPB](https://github.com/scalapb/ScalaPB), a protocol buffers compiler for scala, was the only serious library to work with protobuf in Scala, but it comes with:
 * Two step code generation (protobuf -> java, java -> scala)
   * And if you want to map your own model, you need a third wrapping level.
 * Heavy builder interface
 * Custom lenses library

protoless proposes a different approach, your lightweight models drive the protobuf serialization, **without weighing it down**.

##  State of progress

- [x] Encoding/decoding protobuf native fields.
- [x] Encoding/decoding scala native types (collections, bigdecimal, enum, etc).
- [x] Work with optional and `repeated` fields.
- [x] Support signed/unsigned/fixed int32/64 with tagging.
- [x] `Automatic` encoder/decoder for basic protobuf messages (fields numbered consecutively starting from one).
- [x] `Semi-automatic` encoder/decoder for message with fields not numbered consecutively.
- [x] Auto-derivation of `value class`.
- [x] Support nested message.
- [x] Fluid syntax to write custom message decoder/encoder
- [ ] Support default value [#3](https://github.com/julien-lafont/protoless/issues/3)
- [ ] Compile time schema validation. [#4](https://github.com/julien-lafont/protoless/issues/4)
- [ ] And last, but not least, [GRPC](https://grpc.io/) integration.

## Contributing

The protoless project welcomes contributions from **anybody wishing to participate**. All code or documentation that is provided must be licensed with the same license that Protoless is licensed with (Apache 2.0, see LICENSE file).

Feel free to open an issue if you notice a bug, have an idea for a feature, or have a question about the code. Pull requests are also gladly accepted. You can also just enter in the gitter channel to talk with us.

## License

Code is provided under the Apache 2.0 license available at http://opensource.org/licenses/Apache-2.0, as well as in the LICENSE file.
