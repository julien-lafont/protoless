---
layout: docs
title:  "Encode & decode protobuf messages"
position: 2
---

# Encode & decode protobuf messages

Encoders and Decoders for protobuf messages can be created if 2 differents ways:
 - `Automatic derivation`: encoders & decoders will automatically be derived from your models. All the fields in the
   proto schema **must** be numbered consecutively starting from one.
 - `Semi-automatic derivation` (**recommended**): you have to derive an encoder/decoder for each case class involved in
   protobuf serialization. You can derive with automatic field numbering, or configure a specific mapping.


<script src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.2.7/raphael.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/flowchart/1.6.6/flowchart.min.js"></script>

<div id="diagram"></div>
<script>
    var diagram = flowchart.parse(
      'st=>start: protoless\n' +

      'endAuto=>end: Automatic derivation\n' +
      'endSemi=>end: Semi-Automatic derivation\n' +

      'cond1=>condition: Are all the fields\n' +
      'numbered consecutively\n' +
      'starting from one?\n' +

      'cond2=>condition: Do you need to\n' +
      'customize field\n' +
      'encoding/decoding?\n' +

      'st->cond1\n' +
      'cond1(yes)->cond2\n' +
      'cond1(no)->endSemi(left)\n' +
      'cond2(yes)->endSemi(left)\n' +
      'cond2(no, left)->endAuto\n');

    diagram.drawSVG('diagram');
</script>

## Semi-automatic derivation

### Usage

Suppose that we need to work with the following proto3 schema:

```protobuf
message Student {
  int64 id = 1;
  string name = 2;
  //uint32 age = 3; -- Deprecated in favor of birthDate
  string birthDate = 4;
  int32 libraryCard = 7;
  repeated Course courses = 8;
}

message Course {
  string name = 1;
  double price = 2;
  bool external = 3;
}
```

Note that `Student` schema has definitively evolved many times, because the numbering is not sequential, unlike `Course`, which is numbered consecutively.

In your application, we want to use these types:

```tut
import cats.data.NonEmptyList

case class Course(name: String, price: Double) // we doesn't care about the `external` field
case class StudentId(value: Long) extends AnyVal
case class Student(id: StudentId, name: String, birthDate: String, courses: NonEmptyList[Course])
```

You can summon encoders and decoders for these models with respectively `deriveEncoder` and `deriveDecoder`:

```tut
import io.protoless.generic.semiauto._
import shapeless.{::, HNil, Nat}
import io.protoless.syntax._

// Summon automatic decoder for course (note that we will read a partial representation of the message Course)
implicit val courseDecoder = deriveDecoder[Course]
// Summon a decoder for student with a custom mapping between fields (studentId: 1, name: 2, birthDate: 4, courses: 8)
implicit val studentDecoder = deriveDecoder[Student, Nat._1 :: Nat._2 :: Nat._4 :: Nat._8 :: HNil]

implicit val courseEncoder = deriveEncoder[Course]
implicit val studentEncoder = deriveEncoder[Student, Nat._1 :: Nat._2 :: Nat._4 :: Nat._8 :: HNil] // tips: move the fields definition in a type alias
```

You're ready to encode and decode protobuf messages:

```tut
val student = Student(StudentId(4815162342L), "Kate", "1977-06-21", NonEmptyList.of(
    Course("airline pilot", 8150),
    Course("US marshall", 4912)
))

val bytes = student.asProtobufBytes

bytes.as[Student]
```

### Literal types

If your project use the typevel [fork](https://github.com/typelevel/scala/) of Scala, you can define the fields mapping
with [Literal types](https://github.com/typelevel/scala/blob/typelevel-readme/notes/typelevel-4.md#literal-types-pull5310-milesabin).

```tut
type StudentMapping = 1 :: 2 :: 4 :: 8 :: HNil
```

## Automatic derivation

Automatically derive the required decoder/encoders, using the `automatic field numbering` strategy.

```tut
import io.protoless.generic.auto._

case class Cloud(name: String, nickname: Char)
case class Unicorn(color: String, canFlight: Boolean, locations: Seq[Cloud])

val unicorn = Unicorn("pink", true, Seq(Cloud("Celestial", 'c'), Cloud("Misty Cheeky", 'm')))

val bytes = unicorn.asProtobufBytes

bytes.as[Unicorn]
```

