package io.protoless.tests.samples

import io.protoless.tag._
import io.protoless.tests.samples.Schemas.Color

case class TestCaseCustomMappingRepeated(
  i: Seq[Int], // 3
  sl: Seq[Long @@ Signed], // 8
  b: Seq[Boolean], // 13
  s: Seq[String], // 14
  c: Seq[Colors.Value] // 16
)

object TestCaseCustomMappingRepeated extends TestCase[TestCaseCustomMappingRepeated, Schemas.Repeated] {
  override val product: TestCaseCustomMappingRepeated = TestCaseCustomMappingRepeated(
    i = Seq(Int.MaxValue, Int.MinValue),
    sl = Seq(signed(Long.MinValue), signed(Long.MaxValue)),
    b = Seq(true, false),
    s = Seq("Я тебя люблю", "Lorem ipsum"),
    c = Seq(Colors.Black, Colors.White, Colors.Black, Colors.Green, Colors.Black)
  )

  override val protobuf: Schemas.Repeated = Schemas.Repeated.newBuilder()
    .addInt32Field(product.i(0)).addInt32Field(product.i(1))
    .addSint64Field(product.sl(0)).addSint64Field(product.sl(1))
    .addBoolField(product.b(0)).addBoolField(product.b(1))
    .addStringField(product.s(0)).addStringField(product.s(1))
    .addColorField(Color.BLACK).addColorField(Color.WHITE).addColorField(Color.BLACK).addColorField(Color.GREEN).addColorField(Color.BLACK)
    .build()
}
