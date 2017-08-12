package io.protoless.generic

import shapeless.{::, HNil, Nat}

import io.protoless.{Decoder, Encoder}
import io.protoless.fields.{FieldDecoder, RepeatableFieldDecoder}
import io.protoless.tests.ProtolessSuite
import io.protoless.tests.instances.EqualityInstances
import io.protoless.tests.samples._

class CustomMappingEncoderDecoderSuite extends ProtolessSuite with EqualityInstances with EncoderDecoderHelpers {

  import io.protoless.generic.semiauto._

  implicit val colorDecoder: RepeatableFieldDecoder[Colors.Value] = FieldDecoder.decodeEnum(Colors)

  // Fields number specified with Nat
  type IndexSimple = Nat._2 :: Nat._5 :: Nat._13 :: Nat._16 :: HNil

  // Fields number specified by Literal types
  type IndexRepeated = 3 :: 8 :: 13 :: 14 :: 16 :: HNil

  implicit val decoderTestCaseCustomMappingSimple: Decoder[TestCaseCustomMappingSimple] =
    deriveDecoder[TestCaseCustomMappingSimple, IndexSimple]

  implicit val decoderTestCaseCustomMappingRepeated: Decoder[TestCaseCustomMappingRepeated] =
    deriveDecoder[TestCaseCustomMappingRepeated, IndexRepeated]

  implicit val encoderTestCaseCustomMappingSimple: Encoder[TestCaseCustomMappingSimple] =
    deriveEncoder[TestCaseCustomMappingSimple, IndexSimple]

  implicit val encoderTestCaseCustomMappingRepeated: Encoder[TestCaseCustomMappingRepeated] =
    deriveEncoder[TestCaseCustomMappingRepeated, IndexRepeated]

  "Encoder must convert case class to protobuf format for" - {
    "protobuf native fields type" in {
      testEncoding(TestCaseCustomMappingSimple)
    }

    "repeated fields" in {
      testEncoding(TestCaseCustomMappingRepeated)
    }
  }

  "Decoder must convert protobuf format to case class for" - {
    "protobuf native fields type" in {
      testDecoding(TestCaseCustomMappingSimple)
    }

    "repeated fields" in {
      testDecoding(TestCaseCustomMappingRepeated)
    }
  }

  "Encoders/Decoders must respect law: encode(i) === encode(decode(encode(i))" - {
    "protobuf native fields type" in {
      testFullCycle(TestCaseCustomMappingSimple)
    }

    "repeated fields" in {
      testFullCycle(TestCaseCustomMappingRepeated)
    }
  }

}
