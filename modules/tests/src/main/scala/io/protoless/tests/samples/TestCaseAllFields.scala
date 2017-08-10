package io.protoless.tests.samples

import com.google.protobuf.ByteString

import io.protoless.core.tag._
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

object TestCaseAllFields extends TestCase[TestCaseAllFields, Schemas.Optional] {
  val product = TestCaseAllFields(
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

  val protobuf = Schemas.Optional.newBuilder()
    .setDoubleField(product.d)
    .setFloatField(product.f)
    .setInt32Field(product.i)
    .setInt64Field(product.l)
    .setUint32Field(product.ui)
    .setUint64Field(product.ul)
    .setSint32Field(product.si)
    .setSint64Field(product.sl)
    .setFixed32Field(product.fi)
    .setFixed64Field(product.fl)
    .setSfixed32Field(product.sfi)
    .setSfixed64Field(product.sfl)
    .setBoolField(product.b)
    .setStringField(product.s)
    .setBytesField(product.by)
    .setColorField(Color.GREEN)
    .build()
}
