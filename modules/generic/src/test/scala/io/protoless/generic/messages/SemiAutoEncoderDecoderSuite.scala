package io.protoless.generic.messages

import io.protoless.EncoderDecoderAssertions
import io.protoless.fields.{FieldDecoder, RepeatableFieldDecoder}
import io.protoless.messages.{Decoder, Encoder}
import io.protoless.tests.ProtolessSuite
import io.protoless.tests.instances.EqualityInstances
import io.protoless.tests.samples._

class SemiAutoEncoderDecoderSuite extends ProtolessSuite with EqualityInstances with EncoderDecoderAssertions {

  import io.protoless.generic.semiauto._

  implicit val decoderTestCaseAllFields: Decoder[TestCaseAllFields] = deriveDecoder[TestCaseAllFields]
  implicit val decoderTestCaseOptionalFields: Decoder[TestCaseOptionalFields] = deriveDecoder[TestCaseOptionalFields]
  implicit val decoderTestCaseRepeatedFields: Decoder[TestCaseRepeatedFields] = deriveDecoder[TestCaseRepeatedFields]
  implicit val decoderTestCaseEmptyRepeated: Decoder[TestCaseEmptyRepeated] = deriveDecoder[TestCaseEmptyRepeated]
  implicit val decoderTestCaseCollections: Decoder[TestCaseCollections] = deriveDecoder[TestCaseCollections]
  implicit val decoderTestCaseCustomType: Decoder[TestCaseCustomType] = deriveDecoder[TestCaseCustomType]
  implicit val decoderTestCaseNestedInner: Decoder[TestCaseNested.InnerNested] = deriveDecoder[TestCaseNested.InnerNested]
  implicit val decoderTestCaseNested: Decoder[TestCaseNested] = deriveDecoder[TestCaseNested]

  implicit val encoderTestCaseAllFields: Encoder[TestCaseAllFields] = deriveEncoder[TestCaseAllFields]
  implicit val encoderTestCaseOptionalFields: Encoder[TestCaseOptionalFields] = deriveEncoder[TestCaseOptionalFields]
  implicit val encoderTestCaseRepeatedFields: Encoder[TestCaseRepeatedFields] = deriveEncoder[TestCaseRepeatedFields]
  implicit val encoderTestCaseEmptyRepeated: Encoder[TestCaseEmptyRepeated] = deriveEncoder[TestCaseEmptyRepeated]
  implicit val encoderTestCaseCollections: Encoder[TestCaseCollections] = deriveEncoder[TestCaseCollections]
  implicit val encoderTestCaseCustomType: Encoder[TestCaseCustomType] = deriveEncoder[TestCaseCustomType]
  implicit val encoderTestCaseNestedInner: Encoder[TestCaseNested.InnerNested] = deriveEncoder[TestCaseNested.InnerNested]
  implicit val encoderTestCaseNesteInner: Encoder[TestCaseNested] = deriveEncoder[TestCaseNested]

  "SemiAuto Encoders must convert case class to protobuf format for" - {
    "protobuf native fields type" in {
      testEncoding(TestCaseAllFields)
    }
    "optional fields" in {
      testEncoding(TestCaseOptionalFields)
    }

    "repeated fields" in {
      testEncoding(TestCaseRepeatedFields)
    }

    "repeated fields with gap" in {
      testEncoding(TestCaseEmptyRepeated)
    }

    "repeated fields with 0 or 1 value" in {
      testEncoding(TestCaseRepeatedFieldsOneValue)
    }

    "every scala collections" in {
      testEncoding(TestCaseCollections)
    }

    "custom types (uuid, bigdecimal, char)" in {
      testEncoding(TestCaseCustomType)
    }

    "nested fields" in {
      testEncoding(TestCaseNested)
    }
  }

  "SemiAuto Decoders must convert protobuf format to case class for" - {
    "protobuf native fields type" in {
      testDecoding(TestCaseAllFields)
    }
    "optional fields" in {
      testDecoding(TestCaseOptionalFields)
    }

    "repeated fields" in {
      testDecoding(TestCaseRepeatedFields)
    }

    "repeated fields with gap" in {
      testDecoding(TestCaseEmptyRepeated)
    }

    "repeated fields with 0 or 1 value" in {
      testDecoding(TestCaseRepeatedFieldsOneValue)
    }

    "every scala collections" in {
      testDecoding(TestCaseCollections)
    }

    "custom types (uuid, bigdecimal, char)" in {
      testDecoding(TestCaseCustomType)
    }

    "nested fields" in {
      testDecoding(TestCaseNested)
    }
  }

  "SemiAuto Encoders/Decoders must respect law: encode(i) === encode(decode(encode(i))" - {
    "protobuf native fields type" in {
      testFullCycle(TestCaseAllFields)
    }
    "optional fields" in {
      testFullCycle(TestCaseOptionalFields)
    }

    "repeated fields" in {
      testFullCycle(TestCaseRepeatedFields)
    }

    "repeated fields with gap" in {
      testFullCycle(TestCaseEmptyRepeated)
    }

    "repeated fields with 0 or 1 value" in {
      testFullCycle(TestCaseRepeatedFieldsOneValue)
    }

    "every scala collections" in {
      testFullCycle(TestCaseCollections)
    }

    "custom types (uuid, bigdecimal, char)" in {
      testFullCycle(TestCaseCustomType)
    }

    "nested fields" in {
      testFullCycle(TestCaseNested)
    }
  }

}
