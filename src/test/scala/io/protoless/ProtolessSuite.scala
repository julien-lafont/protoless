package io.protoless

import java.io.ByteArrayOutputStream

import com.google.protobuf.{CodedOutputStream, GeneratedMessageV3}
import org.scalactic.{Equality, TypeCheckedTripleEquals}
import org.scalatest._
import cats.instances.AllInstances
import cats.syntax.AllSyntax

import io.protoless.core.{Decoder, Encoder}
import io.protoless.tests.samples.TestCase

trait ProtolessSuite extends FreeSpec with MustMatchers with TypeCheckedTripleEquals
  with EitherValues with OptionValues
  with AllInstances with AllSyntax
  with EncoderDecoderHelpers

trait EncoderDecoderHelpers {
  self: ProtolessSuite =>

  protected def testEncoding[X, Y <: GeneratedMessageV3](testCase: TestCase[X, Y])(implicit enc: Encoder[X]): Assertion = {
    val bytes = encodeAsBytes(testCase.product)

    val origin = testCase.protobuf.toByteArray

    bytes must ===(origin)
  }

  protected def testDecoding[X, Y <: GeneratedMessageV3](testCase: TestCase[X, Y])(implicit dec: Decoder[X], eq: Equality[X]): Assertion = {
    val bytes = testCase.protobuf.toByteArray
    val decoded = dec.decode(bytes)

    decoded.right.value must ===(testCase.product)
  }

  protected def testFullCycle[X, Y <: GeneratedMessageV3](testCase: TestCase[X, Y])(implicit dec: Decoder[X], enc: Encoder[X]): Assertion = {
    val bytes = encodeAsBytes(testCase.product)

    val origin = dec.decode(bytes)
    val rebytes = origin.right.map(encodeAsBytes(_)).getOrElse(Array.emptyByteArray)

    bytes must ===(rebytes)
  }

  private def encodeAsBytes[A](a: A)(implicit enc: Encoder[A]): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val cos = CodedOutputStream.newInstance(out)
    enc.encode(a, cos)
    out.toByteArray
  }
}
