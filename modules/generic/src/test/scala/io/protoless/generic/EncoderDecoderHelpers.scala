package io.protoless.generic

import org.scalactic.Equality
import org.scalatest.Assertion

import io.protoless.{Decoder, Encoder}
import io.protoless.tests.ProtolessSuite
import io.protoless.tests.samples.TestCase

trait EncoderDecoderHelpers {
  self: ProtolessSuite =>

  protected def testEncoding[X](testCase: TestCase[X])(implicit enc: Encoder[X]): Assertion = {
    val bytes = enc.encodeAsBytes(testCase.source)
    val origin = testCase.protobuf.toByteArray
    bytes must ===(origin)
  }

  protected def testDecoding[X](testCase: TestCase[X])(implicit dec: Decoder[X], eq: Equality[X]): Assertion = {
    val bytes = testCase.protobuf.toByteArray
    val decoded = dec.decode(bytes)
    decoded.right.value must ===(testCase.source)
  }

  protected def testFullCycle[X](testCase: TestCase[X])(implicit dec: Decoder[X], enc: Encoder[X]): Assertion = {
    val bytes = enc.encodeAsBytes(testCase.source)
    val origin = dec.decode(bytes)
    val rebytes = origin.right.map(enc.encodeAsBytes)
    bytes must ===(rebytes.right.get)
  }

}
