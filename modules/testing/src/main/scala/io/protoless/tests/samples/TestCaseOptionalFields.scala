package io.protoless.tests.samples

case class TestCaseOptionalFields(
  d: Option[Double],
  f: Option[Float],
  i: Option[Int]
)

object TestCaseOptionalFields extends TestCase[TestCaseOptionalFields] {
  override val source = TestCaseOptionalFields(
    d = Some(Double.MaxValue),
    f = None,
    i = Some(Int.MaxValue)
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Optional.newBuilder()
    .setDoubleField(source.d.get)
    .setInt32Field(source.i.get)
    .build())
}
