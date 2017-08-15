---
layout: docs
title:  "Encode & Decode protobuf fields"
position: 3
---

# Field Encoder & Decoder

Field [encoders](/protoless/api/io/protoless/fields/FieldEncoder$.html) and
[decoders](/protoless/api/io/protoless/fields/FieldDecoder$.html) are automatically provided by protoless for the following types:

* [Protobuf scalar types](#protobuf-scalar-types)
  * [Numeric fields](#numeric-fields)
* [Optional fields](#optional-fields)
* [Repeated fields](#repeated-fields)
* [Nested fields](#nested-fields)
* [Enumerations](#enumerations)
* [Value classes](#value-classes)
* [Other scala types](#other-scala-types)
* [Custom field encoder & decoder](#create-field-encoder-decoder)

## Protobuf scalar types

| .proto type | Scala type                |
|-------------|---------------------------|
| double      | Double                    |
| float       | Float                     |
| int32       | Int                       |
| int64       | Long                      |
| uint32      | Int @@ Unsigned           |
| uint64      | Long @@ Unsigned          |
| sint32      | Int @@ Signed             |
| sint64      | Long @@ Signed            |
| fixed32     | Int @@ Fixed              |
| fixed64     | Long @@ Fixed             |
| sfixed32    | Int @@ Signed with Fixed  |
| sfixed64    | Long @@ Signed with Fixed |
| bool        | Boolean                   |
| string      | String                    |
| bytes       | ByteString                |

### Numeric fields

Even if `int32`, `uint32`, `sint32`, `fixed32` and `sfixed32` are all represented as an `Int`, it's required to know
how the numeric field has been encoded in order to decode it with the right algorithm.

protoless uses [tagged type](http://www.vlachjosef.com/tagged-types-introduction/) to carry this information
on your model, and provides [helpers](/protoless/api/io/protoless/tag/index.html) to tag your data.

```tut:silent
import io.protoless._, io.protoless.generic.auto._, io.protoless.tag._

case class NumericSeries(uint: Int @@ Unsigned, sint: Long @@ Signed, fixed: Int @@ Fixed, sfixed: Int @@ Signed with Fixed)
```
```tut:book
val serie = NumericSeries(
  uint = unsigned(1),
  sint = signed(2),
  fixed = fixed(3),
  sfixed = signedFixed(4)
)

messages.Encoder[NumericSeries].encodeAsBytes(serie)
```

## Optional fields

Unlike protocol buffers version 3 which force optional type by default, your model with lay down the law.
You just have to use an `Option` if you want to encode/decode an optional field.

Implicit instances of `Option[A]` are provided if type `A` has a decoder/encoder instance.

## Repeated fields

protoless can decode a `repeated` field with any uniary subclass of [Traversable](http://www.scala-lang.org/api/2.12.0/scala/collection/Traversable.html):
Seq, List, Set, HashTrieSet, ArrayBuffer... It can also decode cats [NonEmptyList](https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/NonEmptyList.scala)
and Java `Array`.

Encoders, which are not variant, are a bit more restrictive. protoless provides encoders for `Seq`, `immutable.Seq`, `List`,
`Vector`, `Stream`, `Array` and `NonEmptyList`.

You can easily derive new collection encoders:

```tut:reset:silent
import io.protoless.fields._
import scala.collection.mutable.ArrayBuffer

implicit def encodeArrayBuffer[A](implicit enc: RepeatableFieldEncoder[A]): FieldEncoder[ArrayBuffer[A]] = FieldEncoder.deriveFromTraversable
```

## Nested fields

Protocol buffers allows to use a previously defined message as field type.

protoless operates in the same way: it can encode/decode a field of type `A` if there is an implicit instance of
`Encoder[A]`/`Decoder[A]` in the implicit scope.

## Enumerations

Protobuf enumerations can be converted to Scala enumeration, with the constraint that enumerations values must be in the same order.

```proto
enum Color {
  BLACK = 0;
  WHITE = 1;
  GREEN = 2;
}
```
<br>
```tut:silent
case object Colors extends Enumeration {
  type Color = Value
  val Black, White, Green = Value
}
```

An implicit instance for the `encoder` will be provided by protoless.

The `decoder` must be **explicitly** derived with:

```tut:silent
implicit val colorDecoder: RepeatableFieldDecoder[Colors.Value] = FieldDecoder.decodeEnum(Colors)
```

## Value Classes

Implicit instances of [Value Classes](https://docs.scala-lang.org/overviews/core/value-classes.html) are provided if
the underlying type has an encoder/decoder instance.

```tut:book
case class Money(val value: BigDecimal) extends AnyVal

FieldEncoder[Money].encodeAsBytes(1, Money(BigDecimal(Long.MaxValue))) // I'm rich!
```

## Other Scala types

Implicit instances are provided for the following scala types:

| Scala type  | Proto representation  | Notes                                    |
|-------------|-----------------------|------------------------------------------|
| BigDecimal  | string                |                                          |
| BigInt      | string                |                                          |
| UUID        | repeated sint64       |store 128 bits UUID in two 64 bits long   |
| Short       | int32                 |                                          |
| Char        | int32                 |                                          |

## Custom field encoder & decoder

You can create custom encoders by relying on existing ones, and add your own logic inside.

```tut:silent
case class Fahrenheit(value: Float)

implicit val fahrenheitFromCelciusDecoder = FieldDecoder[Float].map(celcius => Fahrenheit((celcius * 1.8f) + 32))

implicit val fahrenheitToCelciusEncoder = FieldEncoder[Float].contramap[Fahrenheit](f => (f.value - 32) / 1.8f)
```
