package io.protoless.tests.samples

import com.google.protobuf.ByteString

import io.protoless.tag._
import io.protoless.tests.samples.Schemas.Color

case class TestCaseRepeatedFields(
  d: Seq[Double],
  f: Seq[Float],
  i: Seq[Int],
  l: Seq[Long],
  ui: Seq[Int @@ Unsigned],
  ul: Seq[Long @@ Unsigned],
  si: Seq[Int @@ Signed],
  sl: Seq[Long @@ Signed],
  fi: Seq[Int @@ Fixed],
  fl: Seq[Long @@ Fixed],
  sfi: Seq[Int @@ Signed with Fixed],
  sfl: Seq[Long @@ Signed with Fixed],
  b: Seq[Boolean],
  s: Seq[String],
  by: Seq[ByteString],
  c: Seq[Colors.Value]
)

object TestCaseRepeatedFields extends TestCase[TestCaseRepeatedFields, Schemas.Repeated] {

  val product = TestCaseRepeatedFields(
    d = Seq(Double.MaxValue, Double.MinValue),
    f = Seq(Float.MaxValue, Float.MinValue),
    i = Seq(Int.MaxValue, Int.MinValue),
    l = Seq(Long.MaxValue, Long.MinValue),
    ui = Seq(unsigned(1), unsigned(Int.MaxValue)),
    ul = Seq(unsigned(0), unsigned(Int.MaxValue)),
    si = Seq(signed(Int.MinValue), signed(Int.MaxValue)),
    sl = Seq(signed(Long.MinValue), signed(Long.MaxValue)),
    fi = Seq(fixed(Int.MaxValue), fixed(Int.MinValue)),
    fl = Seq(fixed(Long.MaxValue), fixed(Long.MinValue)),
    sfi = Seq(signedFixed(Int.MinValue), signedFixed(Int.MaxValue)),
    sfl = Seq(signedFixed(Long.MinValue), signedFixed(Long.MaxValue)),
    b = Seq(true, false),
    s = Seq("Я тебя люблю", "Lorem ipsum"),
    by = Seq(ByteString.copyFrom("Coucou", "utf8"), ByteString.copyFrom("Hello world", "utf8")),
    c = Seq(Colors.Black, Colors.White, Colors.Black, Colors.Green, Colors.Black)
  )

  val protobuf = Schemas.Repeated.newBuilder()
    .addDoubleField(product.d(0)).addDoubleField(product.d(1))
    .addFloatField(product.f(0)).addFloatField(product.f(1))
    .addInt32Field(product.i(0)).addInt32Field(product.i(1))
    .addInt64Field(product.l(0)).addInt64Field(product.l(1))
    .addUint32Field(product.ui(0)).addUint32Field(product.ui(1))
    .addUint64Field(product.ul(0)).addUint64Field(product.ul(1))
    .addSint32Field(product.si(0)).addSint32Field(product.si(1))
    .addSint64Field(product.sl(0)).addSint64Field(product.sl(1))
    .addFixed32Field(product.fi(0)).addFixed32Field(product.fi(1))
    .addFixed64Field(product.fl(0)).addFixed64Field(product.fl(1))
    .addSfixed32Field(product.sfi(0)).addSfixed32Field(product.sfi(1))
    .addSfixed64Field(product.sfl(0)).addSfixed64Field(product.sfl(1))
    .addBoolField(product.b(0)).addBoolField(product.b(1))
    .addStringField(product.s(0)).addStringField(product.s(1))
    .addBytesField(product.by(0)).addBytesField(product.by(1))
    .addColorField(Color.BLACK).addColorField(Color.WHITE).addColorField(Color.BLACK).addColorField(Color.GREEN).addColorField(Color.BLACK)
    .build()
}

object TestCaseRepeatedFieldsOneValue extends TestCase[TestCaseRepeatedFields, Schemas.Repeated] {

  val product = TestCaseRepeatedFields(
    d = Seq(Double.MaxValue),
    f = Seq(Float.MaxValue),
    i = Seq(Int.MaxValue),
    l = Seq(Long.MaxValue),
    ui = Seq(unsigned(1)),
    ul = Seq(unsigned(0)),
    si = Seq(signed(Int.MinValue)),
    sl = Seq(signed(Long.MinValue)),
    fi = Seq(fixed(Int.MaxValue)),
    fl = Seq(fixed(Long.MaxValue)),
    sfi = Seq(signedFixed(Int.MinValue)),
    sfl = Seq(signedFixed(Long.MinValue)),
    b = Seq(true),
    s = Seq("Я тебя люблю"),
    by = Seq(ByteString.copyFrom("Coucou", "utf8")),
    c = Seq(Colors.Black)
  )

  val protobuf = Schemas.Repeated.newBuilder()
    .addDoubleField(product.d(0))
    .addFloatField(product.f(0))
    .addInt32Field(product.i(0))
    .addInt64Field(product.l(0))
    .addUint32Field(product.ui(0))
    .addUint64Field(product.ul(0))
    .addSint32Field(product.si(0))
    .addSint64Field(product.sl(0))
    .addFixed32Field(product.fi(0))
    .addFixed64Field(product.fl(0))
    .addSfixed32Field(product.sfi(0))
    .addSfixed64Field(product.sfl(0))
    .addBoolField(product.b(0))
    .addStringField(product.s(0))
    .addBytesField(product.by(0))
    .addColorField(Color.BLACK)
    .build()
}

case class TestCaseEmptyRepeated(
  d: Seq[Double],
  f: Seq[Float],
  i: Seq[Int])

object TestCaseEmptyRepeated extends TestCase[TestCaseEmptyRepeated, Schemas.Repeated] {
  val product = TestCaseEmptyRepeated(
    Seq(1D, 2D),
    Seq.empty,
    Seq(1, 2))

  val protobuf = Schemas.Repeated.newBuilder()
    .addDoubleField(product.d(0)).addDoubleField(product.d(1))
    .addInt32Field(product.i(0)).addInt32Field(product.i(1))
    .build()
}
