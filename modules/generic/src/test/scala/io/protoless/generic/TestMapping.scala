package io.protoless.generic

object TestMapping extends App {

  import io.protoless._

  case class Foo(a: String, b: Int, c: Long)

  implicit val decoder: Decoder[Foo] = Decoder.instance(input =>
    for {
      a <- input.read[String](1)
      b <- input.read[Int](2)
      c <- input.read[Long](3)
    } yield Foo(a, b, c)
  )

  implicit val encoder: Encoder[Foo] = Encoder.instance { foo =>
    output =>
      output.write(foo.a, 1)
      output.write(foo.b, 2)
      output.write(foo.c, 3)
  }

  println(decoder)
  println(encoder)

  println(decoder.decode(encoder.encodeAsBytes(Foo("a", 1, 1L))))


}
