package io.protoless.generic.messages

import shapeless.{::, HNil, Nat}

import io.protoless.EncoderDecoderAssertions
import io.protoless.fields.{FieldDecoder, RepeatableFieldDecoder}
import io.protoless.messages.{Decoder, Encoder}
import io.protoless.tests.ProtolessSuite
import io.protoless.tests.instances.EqualityInstances
import io.protoless.tests.samples._
import io.protoless.tests.samples.TestCaseNestedCustomMapping.InnerNestedCustomMapping

class CustomMappingEncoderDecoderSuite extends ProtolessSuite with EqualityInstances with EncoderDecoderAssertions {

  import io.protoless.generic.semiauto._

  // Fields number specified with Nat
  type IndexSimple = Nat._2 :: Nat._5 :: Nat._13 :: Nat._16 :: HNil

  implicit val decoderTestCaseCustomMappingSimple: Decoder[TestCaseCustomMappingSimple] =
    deriveDecoder[TestCaseCustomMappingSimple, IndexSimple]

  implicit val encoderTestCaseCustomMappingSimple: Encoder[TestCaseCustomMappingSimple] =
    deriveEncoder[TestCaseCustomMappingSimple, IndexSimple]

  // Fields number specified by Literal types
  type IndexRepeated = 3 :: 8 :: 13 :: 14 :: 16 :: HNil

  implicit val decoderTestCaseCustomMappingRepeated: Decoder[TestCaseCustomMappingRepeated] =
    deriveDecoder[TestCaseCustomMappingRepeated, IndexRepeated]

  implicit val encoderTestCaseCustomMappingRepeated: Encoder[TestCaseCustomMappingRepeated] =
    deriveEncoder[TestCaseCustomMappingRepeated, IndexRepeated]

  type IndexNestedInner = Nat._2 :: Nat._5 :: HNil
  type IndexNested = Nat._3 :: Nat._4 :: HNil

  implicit val decoderInnerNestedCustomMapping: Decoder[InnerNestedCustomMapping] =
    deriveDecoder[InnerNestedCustomMapping, IndexNestedInner]

  implicit val decoderTestCaseNestedCustomMapping: Decoder[TestCaseNestedCustomMapping] =
    deriveDecoder[TestCaseNestedCustomMapping, IndexNested]


  implicit val encoderInnerNestedCustomMapping: Encoder[InnerNestedCustomMapping] =
    deriveEncoder[InnerNestedCustomMapping, IndexNestedInner]

  implicit val encoderTestCaseNestedCustomMapping: Encoder[TestCaseNestedCustomMapping] =
    deriveEncoder[TestCaseNestedCustomMapping, IndexNested]

  "Encoder must convert case class to protobuf format for" - {
    "protobuf native fields type" in {
      testEncoding(TestCaseCustomMappingSimple)
    }

    "repeated fields" in {
      testEncoding(TestCaseCustomMappingRepeated)
    }

    "nested fields" in {
      testEncoding(TestCaseNestedCustomMapping)
    }
  }

  "Decoder must convert protobuf format to case class for" - {
    "protobuf native fields type" in {
      testDecoding(TestCaseCustomMappingSimple)
    }

    "repeated fields" in {
      testDecoding(TestCaseCustomMappingRepeated)
    }

    "nested fields" in {
      testDecoding(TestCaseNestedCustomMapping)
    }
  }

  "Encoders/Decoders must respect law: encode(i) === encode(decode(encode(i))" - {
    "protobuf native fields type" in {
      testFullCycle(TestCaseCustomMappingSimple)
    }

    "repeated fields" in {
      testFullCycle(TestCaseCustomMappingRepeated)
    }

    "nested fields" in {
      testFullCycle(TestCaseNestedCustomMapping)
    }
  }

}
