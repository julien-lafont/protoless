---
layout: docs
title:  "Encode & decode protobuf messages"
position: 2
---

# Encode & decode protobuf messages

Encoders and Decoders for protobuf Messages can be created in three different ways:
 - [Fully Automatic derivation](#fully-automatic-derivation): encoders & decoders will automatically be derived from your models. All the fields in the
   proto schema **must** be numbered consecutively starting from one.
 - [Semi-automatic derivation](#semi-automatic-derivation) (**recommended**): you have to derive an encoder/decoder for each case class involved in
   protobuf serialization. You can derive with automatic field numbering, or configure a specific mapping.
 - [Hand-crafted encoders/decoders](#hand-crafted-encodersdecoders): you have the freedom to compose your own encoders/decoders using existing bricks.

<div id="diagram"></div>

<script type="text/javascript">
    document.addEventListener("DOMContentLoaded", function(event) {
        var diagram = flowchart.parse(
            'st=>start: Which strategy shoud I use?\n' +

            'endAuto=>end: Fully Automatic derivation:>#fully-automatic-derivation\n' +
            'endSemi=>end: Semi-Automatic derivation:>#semi-automatic-derivation\n' +
            'endCraft=>end: Hand-crafted encoders/decoders:>#hand-crafted-encodersdecoders\n' +


            'cond0=>condition: Do you intend to\n' +
            'validate or transform fields\n' +
            'individually?\n' +

            'cond1=>condition: Are the fields\n' +
            'numbered consecutively\n' +
            'starting from one?\n' +

            'cond2=>condition: Do you like\n' +
            'magic?\n' +

            'st->cond0\n' +
            'cond0(yes)->endCraft\n' +
            'cond0(no)->cond1(yes)->cond2\n' +
            'cond1(no)->endSemi(left)\n' +
            'cond2(no)->endSemi(left)s\n' +
            'cond2(yes, left)->endAuto\n');

        diagram.drawSVG('diagram');
    });
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

Note that the `Student` schema has certainly evolved many times, because the numbering is not sequential, unlike the `Course`, which is numbered consecutively.

In your application, we want to use these types:

```tut:silent
import cats.data.NonEmptyList

case class Course(name: String, price: Double) // we doesn't care about the `external` field
case class StudentId(value: Long) extends AnyVal
case class Student(id: StudentId, name: String, birthDate: String, courses: NonEmptyList[Course])
```

You can summon encoders and decoders for these models with `deriveEncoder` and `deriveDecoder`, respectively :

```tut:silent
import io.protoless.generic.semiauto._
import shapeless.{::, HNil, Nat}

// Summon automatic decoder for course (note that we will read a partial representation of the message Course)
implicit val courseDecoder = deriveDecoder[Course]

// Summon a decoder for student with a custom mapping between fields (studentId: 1, name: 2, birthDate: 4, courses: 8)
implicit val studentDecoder = deriveDecoder[Student, Nat._1 :: Nat._2 :: Nat._4 :: Nat._8 :: HNil]

implicit val courseEncoder = deriveEncoder[Course]
implicit val studentEncoder = deriveEncoder[Student, Nat._1 :: Nat._2 :: Nat._4 :: Nat._8 :: HNil] // tips: move the fields definition in a type alias
```

You're ready to encode and decode protobuf messages:

```tut:book
val student = Student(StudentId(4815162342L), "Kate", "1977-06-21", NonEmptyList.of(
    Course("airline pilot", 8150),
    Course("US marshall", 4912)
))

val bytes = studentEncoder.encodeAsBytes(student)

studentDecoder.decode(bytes)
```

You can also use the syntaxic sugar `.as[A]` and `.asProtobufBytes` to replace the explicit call on decoders/encoders:

```tut:silent
import io.protoless.syntax._

student.asProtobufBytes
bytes.as[Student]
```

### Literal types

If your project use the typelevel [fork](https://github.com/typelevel/scala/) of Scala, you can define the field mapping
with [Literal types](https://github.com/typelevel/scala/blob/typelevel-readme/notes/typelevel-4.md#literal-types-pull5310-milesabin).

```tut:silent
type StudentMapping = 1 :: 2 :: 4 :: 8 :: HNil
```

## Fully Automatic derivation

Automatically derive the required decoder/encoders, using the `automatic field numbering` strategy.

This approach requires less code, but only works if your messages are numbered consecutively starting from one.

```tut:silent
import io.protoless.generic.auto._

case class Cloud(name: String, nickname: Char)
case class Unicorn(color: String, canFlight: Boolean, locations: Seq[Cloud])

val unicorn = Unicorn("pink", true, Seq(Cloud("Celestial", 'c'), Cloud("Misty Cheeky", 'm')))

val bytes = unicorn.asProtobufBytes

bytes.as[Unicorn]
```

## Hand-crafted encoders/decoders

`Hand-crafted` encoders and decoders give you a total control on how your objects are encoded and decoded.
For each field you have to define how and where to read/write it .

It's useful if you want to validate or transform protobuf message to fit your model.

```protobuf
message Meteo {
  string city = 1;
  string country = 2;
  int32 temperature = 3; // in °F
  float wind = 4;
  float humidity = 5; // optional
}
```
<br/>

```tut:reset:silent
case class Location(country: String, city: String)
case class Celcius(temp: Float)

sealed trait Weather
case object Sunny extends Weather
case class Rainy(humidity: Float) extends Weather

case class Meteo(location: Location, temp: Celcius, weather: Weather)
```
<br/>
```tut:silent
import io.protoless.messages._, io.protoless.syntax._

implicit val meteoDecoder = Decoder.instance[Meteo](input =>
  for {
    city <- input.read[String] // if nothing is specified, index are automatically incremented
    country <- input.read[String].map(_.toUpperCase)
    temp <- input.read[Int].map(f => Celcius((f-32f)/1.8f))
    // we don't care about wind
    weather <- input.read[Option[Float]](5).map {
      case Some(humidity) => Rainy(humidity)
      case None => Sunny
    }
  } yield Meteo(Location(city, country), temp, weather)
)

implicit val meteoEncoder = Encoder.instance[Meteo]{ meteo =>
  output =>
    output.write[String](meteo.location.city)
    output.write[String](meteo.location.country)
    output.write[Int]((meteo.temp.temp * 1.8f - 32).toInt)
    output.write[Option[Float]](meteo.weather match {
      case Rainy(humidity) => Some(humidity)
      case _ => None
    }, 6)
}
```
```tut:book
val bytes = meteoEncoder.encodeAsBytes(Meteo(Location("France", "Montpellier"), Celcius(38), Sunny))

bytes.as[Meteo]
```




