package io.protoless.tests.samples

case class TestCaseOptionalFields(
  d: Option[Double],
  f: Option[Float],
  i: Option[Int]
)

object TestCaseOptionalFields extends TestCase[TestCaseOptionalFields, Schemas.Optional] {
  val product = TestCaseOptionalFields(
    d = Some(Double.MaxValue),
    f = None,
    i = Some(Int.MaxValue)
  )

  val protobuf = Schemas.Optional.newBuilder()
    .setDoubleField(product.d.get)
    .setInt32Field(product.i.get)
    .build()
}
