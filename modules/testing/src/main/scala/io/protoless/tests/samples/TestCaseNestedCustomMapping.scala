package io.protoless.tests.samples

import io.protoless.tests.samples.TestCaseNestedCustomMapping.InnerNestedCustomMapping

case class TestCaseNestedCustomMapping(
  m2: InnerNestedCustomMapping,
  rm: Seq[InnerNestedCustomMapping])

object TestCaseNestedCustomMapping extends TestCase[TestCaseNestedCustomMapping] {

  case class InnerNestedCustomMapping(
    bigint: BigInt,
    char: Char
  )

  override val source: TestCaseNestedCustomMapping = TestCaseNestedCustomMapping(
    m2 = InnerNestedCustomMapping(BigInt(1), 'a'),
    rm = Seq(InnerNestedCustomMapping(BigInt(2), 'b'), InnerNestedCustomMapping(BigInt(3), 'c'))
  )
  override val protobuf: ProtoSerializable = ProtoSerializable(Schemas.Nested.newBuilder()
    .setM2(Schemas.Custom.newBuilder().setBigint(source.m2.bigint.toString()).setChar(source.m2.char.toInt).build())
    .addRm(Schemas.Custom.newBuilder().setBigint(source.rm(0).bigint.toString()).setChar(source.rm(0).char.toInt).build())
    .addRm(Schemas.Custom.newBuilder().setBigint(source.rm(1).bigint.toString()).setChar(source.rm(1).char.toInt).build())
    .build()
  )
}
