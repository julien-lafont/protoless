package io.protoless.tests.samples

import cats.data.NonEmptyList
import io.protoless.tag._

case class TestCaseCollections(
  d: Seq[Double],
  f: List[Float],
  i: collection.immutable.Seq[Int],
  l: Array[Long],
  ui: scala.collection.Traversable[Int @@ Unsigned],
  ul: scala.collection.Iterable[Long @@ Unsigned],
  si: Stream[Int @@ Signed],
  sl: Vector[Long @@ Signed],
  fi: NonEmptyList[Int @@ Fixed]
)

object TestCaseCollections extends TestCase[TestCaseCollections] {

  override val source: TestCaseCollections = TestCaseCollections(
    d = Seq(1d, 2d),
    f = List(1f, 2f),
    i = scala.collection.immutable.Seq(1, 2),
    l = Array(1L, 2L),
    ui = Seq(unsigned(1), unsigned(2)),
    ul = Seq(unsigned(1L), unsigned(2L)),
    si = Seq(signed(1), signed(2)).toStream,
    sl = Seq(signed(1L), signed(2L)).toVector,
    fi = NonEmptyList.of(fixed(Int.MaxValue))
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Repeated.newBuilder()
    .addDoubleField(source.d(0)).addDoubleField(source.d(1))
    .addFloatField(source.f(0)).addFloatField(source.f(1))
    .addInt32Field(source.i(0)).addInt32Field(source.i(1))
    .addInt64Field(source.l(0)).addInt64Field(source.l(1))
    .addUint32Field(source.ui.toList(0)).addUint32Field(source.ui.toList(1))
    .addUint64Field(source.ul.toList(0)).addUint64Field(source.ul.toList(1))
    .addSint32Field(source.si(0)).addSint32Field(source.si(1))
    .addSint64Field(source.sl(0)).addSint64Field(source.sl(1))
    .addFixed32Field(source.fi.head)
    .build())
}
