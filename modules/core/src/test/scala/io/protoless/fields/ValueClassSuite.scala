package io.protoless.fields

import io.protoless.tests.ProtolessSuite

import shapeless.test.illTyped

case class ValueClass(x: Int) extends AnyVal
case class NotValueClass(x: Int)

class ValueClassClassSuite extends ProtolessSuite {

  private val bytes = Seq(0x08, 0x96, 0x01).map(_.toByte).toArray // number 150 at field 1
  private val valueClass = ValueClass(150)

  "value class must be decoded" in {
    val dec: FieldDecoder[ValueClass] = FieldDecoder[ValueClass]
    dec.decode(bytes, 1) must ===(Right(valueClass))
  }

  "value class must be encoded" in {
    val enc: FieldEncoder[ValueClass] = FieldEncoder[ValueClass]
    enc.encodeAsBytes(1, valueClass) must ===(bytes)
  }

  "Class not inheriting AnyVal must not be decoded like ValueClass" in {
    illTyped("""FieldDecoder[NotValueClass]""")
    illTyped("""FieldDecoder.decodeValueClass.read(bytes, 1)""")
  }

  "Class not inheriting AnyVal must not be encoded like ValueClass" in {
    illTyped("""FieldEncoder[NotValueClass]""")
    illTyped("""FieldEncoder.encodeValueClass.encodeAsByte(1, NotValueClass(1))""")
  }

}
