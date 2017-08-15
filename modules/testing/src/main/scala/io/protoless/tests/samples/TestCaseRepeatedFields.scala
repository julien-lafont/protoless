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

object TestCaseRepeatedFields extends TestCase[TestCaseRepeatedFields] {

  override val source = TestCaseRepeatedFields(
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

  override val protobuf = ProtoSerializable(Schemas.Repeated.newBuilder()
    .addDoubleField(source.d(0)).addDoubleField(source.d(1))
    .addFloatField(source.f(0)).addFloatField(source.f(1))
    .addInt32Field(source.i(0)).addInt32Field(source.i(1))
    .addInt64Field(source.l(0)).addInt64Field(source.l(1))
    .addUint32Field(source.ui(0)).addUint32Field(source.ui(1))
    .addUint64Field(source.ul(0)).addUint64Field(source.ul(1))
    .addSint32Field(source.si(0)).addSint32Field(source.si(1))
    .addSint64Field(source.sl(0)).addSint64Field(source.sl(1))
    .addFixed32Field(source.fi(0)).addFixed32Field(source.fi(1))
    .addFixed64Field(source.fl(0)).addFixed64Field(source.fl(1))
    .addSfixed32Field(source.sfi(0)).addSfixed32Field(source.sfi(1))
    .addSfixed64Field(source.sfl(0)).addSfixed64Field(source.sfl(1))
    .addBoolField(source.b(0)).addBoolField(source.b(1))
    .addStringField(source.s(0)).addStringField(source.s(1))
    .addBytesField(source.by(0)).addBytesField(source.by(1))
    .addColorField(Color.BLACK).addColorField(Color.WHITE).addColorField(Color.BLACK).addColorField(Color.GREEN).addColorField(Color.BLACK)
    .build())
}

object TestCaseRepeatedFieldsOneValue extends TestCase[TestCaseRepeatedFields] {

  override val source: TestCaseRepeatedFields = TestCaseRepeatedFields(
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

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Repeated.newBuilder()
    .addDoubleField(source.d(0))
    .addFloatField(source.f(0))
    .addInt32Field(source.i(0))
    .addInt64Field(source.l(0))
    .addUint32Field(source.ui(0))
    .addUint64Field(source.ul(0))
    .addSint32Field(source.si(0))
    .addSint64Field(source.sl(0))
    .addFixed32Field(source.fi(0))
    .addFixed64Field(source.fl(0))
    .addSfixed32Field(source.sfi(0))
    .addSfixed64Field(source.sfl(0))
    .addBoolField(source.b(0))
    .addStringField(source.s(0))
    .addBytesField(source.by(0))
    .addColorField(Color.BLACK)
    .build())
}

case class TestCaseEmptyRepeated(
  d: Seq[Double],
  f: Seq[Float],
  i: Seq[Int])

object TestCaseEmptyRepeated extends TestCase[TestCaseEmptyRepeated] {
  override val source = TestCaseEmptyRepeated(
    Seq(1D, 2D),
    Seq.empty,
    Seq(1, 2))

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Repeated.newBuilder()
    .addDoubleField(source.d(0)).addDoubleField(source.d(1))
    .addInt32Field(source.i(0)).addInt32Field(source.i(1))
    .build())
}
