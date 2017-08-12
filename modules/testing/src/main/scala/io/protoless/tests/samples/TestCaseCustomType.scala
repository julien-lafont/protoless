package io.protoless.tests.samples

case class TestCaseCustomType(
  uuid: java.util.UUID,
  bigdecimal: BigDecimal,
  bigint: BigInt,
  short: Short,
  char: Char
)

object TestCaseCustomType extends TestCase[TestCaseCustomType] {

  override val source: TestCaseCustomType = TestCaseCustomType(
    uuid = java.util.UUID.randomUUID(),
    bigdecimal = BigDecimal(Double.MaxValue) * BigDecimal(Double.MaxValue),
    bigint = BigInt(Long.MaxValue) * BigInt(Long.MaxValue),
    short = Short.MaxValue,
    char = 'z'
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Custom.newBuilder()
    .setUuid(source.uuid.toString)
    .setBigdecimal(source.bigdecimal.toString())
    .setBigint(source.bigint.toString())
    .setShort(source.short.toInt)
    .setChar(source.char.toInt)
    .build())
}
