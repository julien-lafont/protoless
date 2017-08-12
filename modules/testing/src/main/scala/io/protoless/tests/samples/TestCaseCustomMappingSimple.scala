package io.protoless.tests.samples

import io.protoless.tag._
import io.protoless.tests.samples.Schemas.Color

case class TestCaseCustomMappingSimple(
  f: Float,
  ui: Int @@ Unsigned,
  b: Option[Boolean],
  c: Colors.Color
)

object TestCaseCustomMappingSimple extends TestCase[TestCaseCustomMappingSimple] {
  override val source: TestCaseCustomMappingSimple = TestCaseCustomMappingSimple(
    f = Float.MaxValue,
    ui = unsigned(Int.MaxValue),
    b = None,
    c = Colors.Green
  )

  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Optional.newBuilder()
    .setFloatField(source.f)
    .setUint32Field(source.ui)
    .setColorField(Color.GREEN)
    .build())
}


