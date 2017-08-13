package io.protoless.generic

import io.protoless.fields.{FieldDecoder, RepeatableFieldDecoder}
import io.protoless.tests.ProtolessSuite
import io.protoless.tests.instances.EqualityInstances
import io.protoless.tests.samples._

class AutoEncoderDecoderSuite extends ProtolessSuite with EqualityInstances with EncoderDecoderHelpers {

  import io.protoless.generic.auto._

  implicit val colorDecoder: RepeatableFieldDecoder[Colors.Value] = FieldDecoder.decodeEnum(Colors)

  "Auto Encoder must convert case class to protobuf format for" - {
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

  "Auto Decoder must convert protobuf format to case class for" - {
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

  "Auto Encoders/Decoders must respect law: encode(i) === encode(decode(encode(i))" - {
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
