package io.protoless.tests.samples

import com.google.protobuf.GeneratedMessageV3

trait TestCase[X, Y <: GeneratedMessageV3] {
  val product: X
  val protobuf: Y
}
