package io.protoless.tests.samples

import io.protoless.tests.samples.TestCaseNested.InnerNested

case class TestCaseNested(d: Double, m: InnerNested)

object TestCaseNested extends TestCase[TestCaseNested] {

  case class InnerNested(
    uuid: String
  )

  override val source: TestCaseNested = TestCaseNested(
    d = 1.0,
    m = InnerNested("coucou")
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Nested.newBuilder()
    .setD(1.0D)
    .setM(Schemas.Custom.newBuilder().setUuid("coucou").build())
    .build())

}
