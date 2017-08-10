package io.protoless.tests.samples

case class TestCaseNestedMessage(
  d: Double,
  m: TestCaseCustomType,
  rm: Seq[TestCaseCustomType],
  b: Boolean
)

object TestCaseNestedMessage extends TestCase[TestCaseNestedMessage, Schemas.Nested] {

  val product = TestCaseNestedMessage(
    d = Double.MaxValue,
    m = TestCaseCustomType.product,
    rm = Seq(TestCaseCustomType.product, TestCaseCustomType.product),
    b = true
  )

  val protobuf = Schemas.Nested.newBuilder()
    .setD(product.d)
    .setM(TestCaseCustomType.protobuf)
    .addRm(TestCaseCustomType.protobuf).addRm(TestCaseCustomType.protobuf)
    .setB(product.b)
    .build()
}
