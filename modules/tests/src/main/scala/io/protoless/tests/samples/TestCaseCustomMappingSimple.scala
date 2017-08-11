package io.protoless.tests.samples

import io.protoless.tag._
import io.protoless.tests.samples.Schemas.Color

case class TestCaseCustomMappingSimple(
  f: Float,
  ui: Int @@ Unsigned,
  b: Option[Boolean],
  c: Colors.Color
)

object TestCaseCustomMappingSimple extends TestCase[TestCaseCustomMappingSimple, Schemas.Optional] {
  val product = TestCaseCustomMappingSimple(
    f = Float.MaxValue,
    ui = unsigned(Int.MaxValue),
    b = None,
    c = Colors.Green
  )

  val protobuf = Schemas.Optional.newBuilder()
    .setFloatField(product.f)
    .setUint32Field(product.ui)
    .setColorField(Color.GREEN)
    .build()
}


