package io.protoless.tests.samples

case class TestCaseCustomType(
  bigdecimal: BigDecimal,
  bigint: BigInt,
  uuid: java.util.UUID,
  short: Short,
  char: Char
)

object TestCaseCustomType extends TestCase[TestCaseCustomType] {

  override val source: TestCaseCustomType = TestCaseCustomType(
    bigdecimal = BigDecimal(Double.MaxValue) * BigDecimal(Double.MaxValue),
    bigint = BigInt(Long.MaxValue) * BigInt(Long.MaxValue),
    uuid = java.util.UUID.randomUUID(),
    short = Short.MaxValue,
    char = 'z'
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Custom.newBuilder()
    .setBigdecimal(source.bigdecimal.toString())
    .setBigint(source.bigint.toString())
    .addUuid(source.uuid.getMostSignificantBits).addUuid(source.uuid.getLeastSignificantBits)
    .setShort(source.short.toInt)
    .setChar(source.char.toInt)
    .build())
}
