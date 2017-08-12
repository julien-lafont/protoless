package io.protoless.tests.samples

import com.google.protobuf.ByteString

import io.protoless.tag._
import io.protoless.tests.samples.Schemas.Color

case class TestCaseAllFields(
  d: Double,
  f: Float,
  i: Int,
  l: Long,
  ui: Int @@ Unsigned,
  ul: Long @@ Unsigned,
  si: Int @@ Signed,
  sl: Long @@ Signed,
  fi: Int @@ Fixed,
  fl: Long @@ Fixed,
  sfi: Int @@ Signed with Fixed,
  sfl: Long @@ Signed with Fixed,
  b: Boolean,
  s: String,
  by: ByteString,
  c: Colors.Color
)

object TestCaseAllFields extends TestCase[TestCaseAllFields] {
  override val source: TestCaseAllFields = TestCaseAllFields(
    d = Double.MaxValue,
    f = Float.MaxValue,
    i = Int.MaxValue,
    l = Long.MaxValue,
    ui = unsigned(100),
    ul = unsigned(100L),
    si = signed(Int.MinValue),
    sl = signed(Long.MinValue),
    fi = fixed(Int.MaxValue),
    fl = fixed(Long.MaxValue),
    sfi = signedFixed(Int.MinValue),
    sfl = signedFixed(Long.MinValue),
    b = true,
    s = "Я тебя люблю",
    by = ByteString.copyFrom("Coucou", "utf8"),
    c = Colors.Green
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Optional.newBuilder()
    .setDoubleField(source.d)
    .setFloatField(source.f)
    .setInt32Field(source.i)
    .setInt64Field(source.l)
    .setUint32Field(source.ui)
    .setUint64Field(source.ul)
    .setSint32Field(source.si)
    .setSint64Field(source.sl)
    .setFixed32Field(source.fi)
    .setFixed64Field(source.fl)
    .setSfixed32Field(source.sfi)
    .setSfixed64Field(source.sfl)
    .setBoolField(source.b)
    .setStringField(source.s)
    .setBytesField(source.by)
    .setColorField(Color.GREEN)
    .build())
}
