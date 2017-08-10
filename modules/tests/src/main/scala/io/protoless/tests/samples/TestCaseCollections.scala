package io.protoless.tests.samples

import cats.data.NonEmptyList
import io.protoless.core.tag._

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

object TestCaseCollections extends TestCase[TestCaseCollections, Schemas.Repeated] {

  val product = TestCaseCollections(
    d = Seq(1d, 2d),
    f = List(1f, 2f),
    i = scala.collection.immutable.Seq(1, 2),
    l = Array(1l, 2l),
    ui = Seq(unsigned(1), unsigned(2)),
    ul = Seq(unsigned(1l), unsigned(2l)),
    si = Seq(signed(1), signed(2)).toStream,
    sl = Seq(signed(1l), signed(2l)).toVector,
    fi = NonEmptyList.of(fixed(Int.MaxValue))
  )

  val protobuf = Schemas.Repeated.newBuilder()
    .addDoubleField(product.d(0)).addDoubleField(product.d(1))
    .addFloatField(product.f(0)).addFloatField(product.f(1))
    .addInt32Field(product.i(0)).addInt32Field(product.i(1))
    .addInt64Field(product.l(0)).addInt64Field(product.l(1))
    .addUint32Field(product.ui.toList(0)).addUint32Field(product.ui.toList(1))
    .addUint64Field(product.ul.toList(0)).addUint64Field(product.ul.toList(1))
    .addSint32Field(product.si(0)).addSint32Field(product.si(1))
    .addSint64Field(product.sl(0)).addSint64Field(product.sl(1))
    .addFixed32Field(product.fi.head)
    .build()
}
