package io.protoless.tests.samples

import io.protoless.tests.samples.TestCaseNested.InnerNested

case class TestCaseNested(
  d: Double,
  m1: Option[InnerNested],
  m2: InnerNested,
  rm: Seq[InnerNested])

object TestCaseNested extends TestCase[TestCaseNested] {

  case class InnerNested(
    uuid: String,
    bigDecimal: BigDecimal
  )

  override val source: TestCaseNested = TestCaseNested(
    d = 1.0,
    m1 = Some(InnerNested("bonjour", BigDecimal(Double.MaxValue.toString))),
    m2 = InnerNested("hello", BigDecimal(Long.MinValue)),
    rm = Seq(InnerNested("Добрый день", BigDecimal(1)), InnerNested("buongiorno", BigDecimal(2)))
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Nested.newBuilder()
    .setD(source.d)
    .setM1(Schemas.Custom.newBuilder().setUuid(source.m1.get.uuid).setBigdecimal(source.m1.get.bigDecimal.toString()).build())
    .setM2(Schemas.Custom.newBuilder().setUuid(source.m2.uuid).setBigdecimal(source.m2.bigDecimal.toString()).build())
    .addRm(Schemas.Custom.newBuilder().setUuid(source.rm(0).uuid).setBigdecimal(source.rm(0).bigDecimal.toString()).build())
    .addRm(Schemas.Custom.newBuilder().setUuid(source.rm(1).uuid).setBigdecimal(source.rm(1).bigDecimal.toString()).build())
    .build())

}
