package io.protoless.tests.samples

case class TestCaseCustomType(
  uuid: java.util.UUID,
  bigdecimal: BigDecimal,
  bigint: BigInt,
  short: Short,
  char: Char
)

object TestCaseCustomType extends TestCase[TestCaseCustomType, Schemas.Custom] {

  val product = TestCaseCustomType(
    uuid = java.util.UUID.randomUUID(),
    bigdecimal = BigDecimal(Double.MaxValue) * BigDecimal(Double.MaxValue),
    bigint = BigInt(Long.MaxValue) * BigInt(Long.MaxValue),
    short = Short.MaxValue,
    char = 'z'
  )

  val protobuf = Schemas.Custom.newBuilder()
    .setUuid(product.uuid.toString)
    .setBigdecimal(product.bigdecimal.toString())
    .setBigint(product.bigint.toString())
    .setShort(product.short.toInt)
    .setChar(product.char.toInt)
    .build()
}
