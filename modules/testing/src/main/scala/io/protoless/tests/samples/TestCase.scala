package io.protoless.tests.samples

import com.google.protobuf.GeneratedMessageV3

trait ProtoSerializable {
  def toByteArray: Array[Byte]
}

object ProtoSerializable {
  def apply(proto: GeneratedMessageV3): ProtoSerializable = new ProtoSerializable {
    override def toByteArray: Array[Byte] = proto.toByteArray
  }
}

trait TestCase[X] {
  val source: X
  val protobuf: ProtoSerializable
}
