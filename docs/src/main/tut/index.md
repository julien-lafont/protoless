---
layout: home
title:  "Home"
section: "home"
---

[![Build Status](https://travis-ci.org/julien-lafont/protoless.svg)](https://travis-ci.org/julien-lafont/protoless)
[![GitHub tag](https://img.shields.io/github/tag/julien-lafont/protoless.svg)]()
[![Bintray](https://img.shields.io/bintray/v/julien-lafont/maven/protoless-core.svg)]()
[![Gitter](https://img.shields.io/gitter/room/julien-lafont/protoless.js.svg)]()

protoless is a [Protobuf 3](https://developers.google.com/protocol-buffers/docs/proto3) serialization
library in **Scala** for JVM, based on automatic type class derivation to perfectly fit with your models.

The type class derivation approach allows to generate `type-safe` [Encoders](https://julien-lafont.github.io/protoless/api/io/protoless/Decoder.html)
and [Decoders](https://julien-lafont.github.io/protoless/api/io/protoless/Encoder.html) at `compile-time` for your own models,
without code-generation. The derivation is done with [Shapeless](https://github.com/milessabin/shapeless),
No macro were harmed in the making of this library.

`Schema-free` doesn't imply any loss of consistency. If you have one, you can still validate it at compile-time with yours models (*not implemented yet*).

protoless is heavily inspired by awesome work made on [Circe](http://circe.io) by Travis Brown, such that their public APIs share a lot in their design.


## QuickStart

protoless is published to [bintray.com/julien-lafont](https://bintray.com/julien-lafont/maven) and cross-built for `scala 2.11.8`, and `scala 2.12.3`, so you can just add the following to your build:

```scala
resolvers += Resolver.bintrayRepo("julien-lafont", "maven")

libraryDependencies += Seq(
  "io.protoless" %% "protoless-core" % "0.0.3",
  "io.protoless" %% "protoless-generic" % "0.0.3"
)
```

Then type `sbt console` to start a REPL and then paste the following:

```tut
import io.protoless._, io.protoless.syntax._, io.protoless.generic.auto._

case class Person(firstname: String, lastname: String, age: Option[Int], locations: Seq[String])

val p = Person("John", "Doe", Some(28), Seq("Paris", "London", "New York"))

Encoder[Person].encodeAsBytes(p) // or p.asProtobufBytes

Decoder[Person].decode(res0) // or res0.as[Person]
```

No boilerplate, no runtime reflection.

# What's next ?

The next sections will focus on:
 - Protobuf message encoding / decoding
 - Create custom field encoder/decoder.
 - Mapping between protobuf types and scala types
