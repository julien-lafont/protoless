package io.protoless.messages

import com.google.protobuf.ByteString

import io.protoless.EncoderDecoderAssertions
import io.protoless.tag.{@@, Fixed, Signed, Unsigned}
import io.protoless.tests.ProtolessSuite
import io.protoless.tests.samples.{Colors, TestCaseAllFields, TestCaseCustomMappingRepeated, TestCaseNested}
import io.protoless.tests.samples.Colors.Color
import io.protoless.tests.samples.TestCaseNested.InnerNested

class HandCraftedEncoderDecoderSuite extends ProtolessSuite with EncoderDecoderAssertions {

  import cats.syntax.either._ // Only required for scala 2.11.x (see http://typelevel.org/cats/faq.html#either)

  implicit val decoderTestCaseAllFields: Decoder[TestCaseAllFields] = Decoder.instance(input =>
    for {
      d <- input.read[Double]
      f <- input.read[Float]
      i <- input.read[Int]
      l <- input.read[Long]
      ui <- input.read[Int @@ Unsigned]
      ul <- input.read[Long @@ Unsigned]
      si <- input.read[Int @@ Signed]
      sl <- input.read[Long @@ Signed]
      fi <- input.read[Int @@ Fixed]
      fl <- input.read[Long @@ Fixed]
      sfi <- input.read[Int @@ Signed with Fixed]
      sfl <- input.read[Long @@ Signed with Fixed]
      b <- input.read[Boolean]
      s <- input.read[String]
      by <- input.read[ByteString]
      c <- input.read[Color]
    } yield TestCaseAllFields(d, f, i, l, ui, ul, si, sl, fi, fl, sfi, sfl, b, s, by, c)
  )

  implicit val encoderTestCaseAllFields: Encoder[TestCaseAllFields] = Encoder.instance { t => output =>
    output.write(t.d)
    output.write(t.f)
    output.write(t.i)
    output.write(t.l)
    output.write(t.ui)
    output.write(t.ul)
    output.write(t.si)
    output.write(t.sl)
    output.write(t.fi)
    output.write(t.fl)
    output.write(t.sfi)
    output.write(t.sfl)
    output.write(t.b)
    output.write(t.s)
    output.write(t.by)
    output.write(t.c)
  }

  implicit val decoderTestCaseNested: Decoder[TestCaseNested] = Decoder.instance { input =>

    implicit val decoderInnerNested: Decoder[InnerNested] = Decoder.instance { i =>
      for {
        b1 <- i.read[BigDecimal]
        b2 <- i.read[BigInt]
      } yield InnerNested(b1, b2)
    }

    for {
      d <- input.read[Double]
      m1 <- input.read[Option[InnerNested]]
      m2 <- input.read[InnerNested]
      rm <- input.read[Seq[InnerNested]]
    } yield TestCaseNested(d, m1, m2, rm)
  }

  implicit val encoderTestCaseNested: Encoder[TestCaseNested] = Encoder.instance { test => output =>

    implicit val encoderInnerNested: Encoder[InnerNested] = Encoder.instance { inner => out =>
      out.write(inner.bigDecimal)
      out.write(inner.bigInt)
    }

    output.write(test.d)
    output.write(test.m1)
    output.write(test.m2)
    output.write(test.rm)
  }

  implicit val decoderTestCaseCustomMappingRepeated: Decoder[TestCaseCustomMappingRepeated] = Decoder.instance(input =>
    for {
      i <- input.read[Seq[Int]](3)
      sl <- input.read[Seq[Long @@ Signed]](8)
      b <- input.read[Seq[Boolean]](13)
      s <- input.read[Seq[String]](14)
      c <- input.read[Seq[Colors.Value]](16)
    } yield TestCaseCustomMappingRepeated(i, sl, b, s, c)
  )

  implicit val encoderTestCaseCustomMappingRepeated: Encoder[TestCaseCustomMappingRepeated] = Encoder.instance(t => output => {
    output.write(t.i, 3)
    output.write(t.sl, 8)
    output.write(t.b, 13)
    output.write(t.s, 14)
    output.write(t.c, 16)
  })

  "Hand crafted Encoders must convert case class to protobuf format for" - {
    "protobuf native fields type" in {
      testEncoding(TestCaseAllFields)
    }
    "nested fields" in {
      testEncoding(TestCaseNested)
    }
    "repeated fields with custom mapping" in {
      testEncoding(TestCaseCustomMappingRepeated)
    }
  }

  "Hand crafted Decoders must convert protobuf format to case class for" - {
    "protobuf native fields type" in {
      testDecoding(TestCaseAllFields)
    }
    "nested fields" in {
      testDecoding(TestCaseNested)
    }
    "repeated fields with custom mapping" in {
      testDecoding(TestCaseCustomMappingRepeated)
    }
  }

  "Hand crafted Encoders/Decoders must respect law: encode(i) === encode(decode(encode(i))" - {
    "protobuf native fields type" in {
      testFullCycle(TestCaseAllFields)
    }
    "nested fields" in {
      testFullCycle(TestCaseNested)
    }
    "repeated fields with custom mapping" in {
      testFullCycle(TestCaseCustomMappingRepeated)
    }
  }

}
