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

    decoded match {
      case Right(res) => res must ===(testCase.source)
      case Left(err) =>
        err.printStackTrace()
        fail(err)
    }
  }

  protected def testFullCycle[X](testCase: TestCase[X])(implicit dec: Decoder[X], enc: Encoder[X]): Assertion = {
    val bytes = enc.encodeAsBytes(testCase.source)
    val decoded = dec.decode(bytes)

    decoded match {
      case Right(res) =>
        val rebytes = enc.encodeAsBytes(res)
        bytes must ===(rebytes)

      case Left(err) =>
        err.printStackTrace()
        fail(err)
    }
  }

}
