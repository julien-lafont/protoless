---
layout: docs
title:  "Library Design"
position: 1
---

# Design

The library is designed around 4 [Type Classes](http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html):
 - Message Decoder (*aka Decoder*)
 - Message Encoder (*aka Encoder*)
 - Field Decoder
 - Field Encoder

### Message encoder & decoder:

protoless uses `Encoder` and `Decoder` type classes for encoding and decoding. An `Encoder[A]` instances provides
a function that will convert any `A` to a binary, and a `Decoding[A]` takes a binary type to either an exception (`DecodingFailure`) or an `A`.

The binary type can be an `Array[Byte]`, a `ByteBuffer`, or a `ByteArray[Input|Output]Stream`.

The distinctive feature of Protobuf compared to Json is that fields are referenced by a **field number** (index) rather than
by their name. Consequently, encoders and decoders require to know the `type` and `number` of each field you want to read/write.

Several implementations are available to fit the multiple scenarios:
 - Decode all fields of a protobuf message, or just some of these.
 - Fields are numbered consecutively, or their numbering must be personalized.
 - Fields must be transformed or validated after decoding
 - Protobuf numbering is known at compile-time or not

### Field encoder & decoder:

`Field` decoders/encoders allow to read a specific type at a specific position in a protobuf message.

protoless provides implicit instances of these type classes for many types from the Scala standard library, including `Int`,
`String`, `BigDecimal`, `UUID`, and [others](/protoless/mapping.html). It also provides instances for `Seq[A]`, `Option[A]` and other generic types, but
only if `A` has an encoder/decoder instance.

An implicit instance for types `A` where `A` has a `Decoder[A]` instance is also provided, in order to support protobuf
**nested** fields.

### Important note

For performance reasons, protobuf messages are processed in a **streaming** fashion.

As a consequence, reading the same fields two times, trying to read a field with a type and retrying with another type, or
reading in reverse order is not allowed.
